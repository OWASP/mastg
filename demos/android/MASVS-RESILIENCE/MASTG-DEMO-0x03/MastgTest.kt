package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.BufferedReader
import java.io.FileReader
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class MastgTest(private val context: Context) {

    private val sensitiveApiKey = "sk-OWASP-MAS-SuperSecretKey-1234567890"
    private val keyAlias = "mastgCipherKey"

    companion object {
        init {
            System.loadLibrary("nativekey")
        }
    }

    private external fun encryptNativeSecret(cipher: Cipher): ByteArray
    private external fun decryptNativeSecret(cipher: Cipher, data: ByteArray): ByteArray

    // Compares the decrypted bytes against the secret hardcoded in native code,
    // so the plaintext constant never needs to exist in Java.
    private external fun verifyNativeSecret(plaintext: ByteArray): Boolean

    // Returns the first Frida-related line from /proc/self/maps, or null if none.
    private fun detectFridaEntry(): String? {
        try {
            BufferedReader(FileReader("/proc/self/maps")).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val l = line!!.lowercase()
                    if (l.contains("frida") || l.contains("gadget")) {
                        return line
                    }
                }
            }
        } catch (_: Exception) {
            // Unable to read maps
        }
        return null
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return if (keyStore.containsAlias(keyAlias)) {
            (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            ).apply {
                init(
                    KeyGenParameterSpec.Builder(
                        keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
        }
    }

    fun mastgTest(): String {
        val report = StringBuilder()
        val fridaEntry = detectFridaEntry()
        if (fridaEntry != null) {
            report.appendLine("[DETECTED] Frida agent found in /proc/self/maps: $fridaEntry")
            report.appendLine("[BLOCKED]  Skipping all encryption steps")
            return report.toString()
        }
        report.appendLine("[PASS] No Frida agent found in /proc/self/maps")

        try {
            val key = getOrCreateSecretKey()
            report.appendLine("[PASS] AndroidKeyStore key ready (alias \"$keyAlias\")")

            // --- Java round-trip ---
            report.appendLine()
            report.appendLine("=== Java AES/GCM round-trip (AndroidKeyStore) ===")
            val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            encryptCipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedBytes = encryptCipher.doFinal(sensitiveApiKey.toByteArray(Charsets.UTF_8))
            val iv = encryptCipher.iv
            val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            decryptCipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
            val decryptedString = String(decryptCipher.doFinal(encryptedBytes), Charsets.UTF_8)

            report.appendLine("Secret:       $sensitiveApiKey")
            report.appendLine("IV:           ${Base64.encodeToString(iv, Base64.NO_WRAP)}")
            report.appendLine("Ciphertext:   ${Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)}")
            report.appendLine("Decrypted:    $decryptedString")
            report.appendLine(
                if (decryptedString == sensitiveApiKey)
                    "[PASS] Decrypted secret matches the original"
                else "[ERROR] Decrypted secret does not match the original"
            )

            // --- Native round-trip (in memory, no file on disk) ---
            report.appendLine()
            report.appendLine("=== Native AES/GCM round-trip (JNI) ===")
            try {
                val nativeEncryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
                nativeEncryptCipher.init(Cipher.ENCRYPT_MODE, key)
                val nativeIv = nativeEncryptCipher.iv
                val nativeCiphertext = encryptNativeSecret(nativeEncryptCipher)

                val nativeDecryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
                nativeDecryptCipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, nativeIv))
                val nativeDecryptedBytes = decryptNativeSecret(nativeDecryptCipher, nativeCiphertext)
                val nativeDecrypted = String(nativeDecryptedBytes, Charsets.UTF_8)
                val nativeVerified = verifyNativeSecret(nativeDecryptedBytes)

                report.appendLine("IV:           ${Base64.encodeToString(nativeIv, Base64.NO_WRAP)}")
                report.appendLine("Ciphertext:   ${Base64.encodeToString(nativeCiphertext, Base64.NO_WRAP)}")
                report.appendLine("Decrypted:    $nativeDecrypted")
                report.appendLine(
                    if (nativeVerified)
                        "[PASS] Decrypted secret matches the secret hardcoded in native"
                    else "[ERROR] Decrypted secret does not match the secret hardcoded in native"
                )
            } catch (e: Exception) {
                // Raised by the native detections: Frida trampoline at the JNI entry or libc .text mismatch.
                report.appendLine("[BLOCKED] Native step aborted by detection: ${e.message ?: e.javaClass.simpleName}")
            }
        } catch (e: Exception) {
            report.appendLine()
            report.appendLine("[ERROR] ${e.javaClass.simpleName}: ${e.message}")
        }
        return report.toString()
    }
}
