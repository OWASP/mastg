package org.owasp.mastestapp

// SUMMARY: This sample demonstrates storing sensitive data unencrypted and encrypted using the Java File APIs (openFileOutput and FileOutputStream).

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class MastgTest(private val context: Context) {

    private val password = "MyS3cr3tP4ssw0rd"
    private val apiKey = "AKIAABCDEFGHIJKLMNOP"
    private val keyAlias = "mastgFileKey"

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

    private fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun mastgTest(): String {
        return try {
            var result = ""

            // FAIL: [MASTG-TEST-0x01] The app stores the password unencrypted using openFileOutput, exposing it to attackers with device access.
            context.openFileOutput("secret_token.txt", Context.MODE_PRIVATE).use { output ->
                output.write(password.toByteArray())
                Log.d("FileAPIs", "Written unencrypted password to secret_token.txt")
            }
            result += "[FAIL]: Stored unencrypted password in secret_token.txt using openFileOutput.\n\n"

            // FAIL: [MASTG-TEST-0x01] The app stores the API key unencrypted using FileOutputStream, making it readable by attackers with sandbox access.
            val apiKeyFile = File(context.filesDir, "api_key.txt")
            FileOutputStream(apiKeyFile).use { output ->
                output.write(apiKey.toByteArray())
                Log.d("FileAPIs", "Written unencrypted API key to api_key.txt")
            }
            result += "[FAIL]: Stored unencrypted API key in api_key.txt using FileOutputStream.\n\n"

            // PASS: [MASTG-TEST-0x01] The app encrypts the API key with AES-GCM using a KeyStore-backed key before writing, preventing plaintext exposure.
            val encryptedApiKeyFile = File(context.filesDir, "encrypted_api_key.bin")
            FileOutputStream(encryptedApiKeyFile).use { output ->
                val encryptedApiKey = encrypt(apiKey)
                output.write(encryptedApiKey.toByteArray())
                Log.d("FileAPIs", "Written encrypted API key to encrypted_api_key.bin")
            }
            result += "[OK]: Stored encrypted API key in encrypted_api_key.bin using FileOutputStream with AES-GCM.\n\n"

            result
        } catch (e: Exception) {
            "Error during MastgTest: ${e.message ?: "Unknown error"}"
        }
    }
}
