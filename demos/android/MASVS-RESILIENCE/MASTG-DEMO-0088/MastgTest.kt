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
        // Check for hooking before performing cryptographic operations
        if (detectHooking()) {
            android.os.Process.killProcess(android.os.Process.myPid())
            return ""
        }

        return try {
            val key = getOrCreateSecretKey()

            // Encrypt the sensitive API key
            val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            encryptCipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = encryptCipher.iv
            val encryptedBytes = encryptCipher.doFinal(sensitiveApiKey.toByteArray(Charsets.UTF_8))
            val encryptedData = Base64.encodeToString(iv + encryptedBytes, Base64.DEFAULT)

            // Decrypt to verify
            val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
            val ivFromData = decodedData.copyOfRange(0, 12)
            val ciphertext = decodedData.copyOfRange(12, decodedData.size)

            val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
            decryptCipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, ivFromData))
            val decryptedBytes = decryptCipher.doFinal(ciphertext)
            val decryptedString = String(decryptedBytes, Charsets.UTF_8)

            "Encryption and decryption successful.\n" +
                    "Encrypted: $encryptedData\n" +
                    "Decrypted: $decryptedString"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
