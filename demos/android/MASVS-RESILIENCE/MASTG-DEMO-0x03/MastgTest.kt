package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.File
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

    private external fun storeNativeSecret(cipher: Cipher, path: String)
    private external fun recoverNativeSecret(cipher: Cipher, path: String): ByteArray

    init {
        if (detectHooking()) {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    private fun detectHooking(): Boolean {
        try {
            BufferedReader(FileReader("/proc/self/maps")).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val l = line!!.lowercase()
                    if (l.contains("frida") || l.contains("gadget")) {
                        return true
                    }
                }
            }
        } catch (_: Exception) {
            // Unable to read maps
        }
        return false
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
        if (detectHooking()) {
            android.os.Process.killProcess(android.os.Process.myPid())
            return ""
        }

        return try {
            val key = getOrCreateSecretKey()

            // Existing Java cryptographic operations remain observable by the bypass script.
            val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            encryptCipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = encryptCipher.iv
            val encryptedBytes = encryptCipher.doFinal(sensitiveApiKey.toByteArray(Charsets.UTF_8))
            val encryptedData = Base64.encodeToString(iv + encryptedBytes, Base64.DEFAULT)

            val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
            val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            decryptCipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, decodedData.copyOfRange(0, 12)))
            val decryptedString =
                String(decryptCipher.doFinal(decodedData.copyOfRange(12, decodedData.size)), Charsets.UTF_8)

            val file = File(context.filesDir, "native-secret.bin")
            val nativeEncryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            nativeEncryptCipher.init(Cipher.ENCRYPT_MODE, key)
            storeNativeSecret(nativeEncryptCipher, file.absolutePath)

            val storedIv = ByteArray(12)
            DataInputStream(file.inputStream()).use { it.readFully(storedIv) }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, storedIv))
            val recovered = recoverNativeSecret(cipher, file.absolutePath)

            "Encryption and decryption successful.\n" +
                    "Encrypted: $encryptedData\n" +
                    "Decrypted: $decryptedString\n" +
                    "Native encrypted: ${Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)}\n" +
                    "Native decrypted: ${String(recovered, Charsets.UTF_8)}\n"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
