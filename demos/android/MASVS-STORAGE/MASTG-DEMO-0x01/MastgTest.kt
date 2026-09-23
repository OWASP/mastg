package org.owasp.mastestapp

// SUMMARY: This sample demonstrates storing sensitive data unencrypted to the app's internal storage using the Java File APIs (openFileOutput and FileOutputStream), and also shows the correct approach using AES/GCM encryption with an AndroidKeyStore-backed key.

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class MastgTest(private val context: Context) {

    private val password = "MyS3cr3tP4ssw0rd"
    private val apiKey = "AKIAABCDEFGHIJKLMNOP"
    private val encryptedSecret = "SensitiveDataToEncrypt"
    private val keyAlias = "MastgTestKeyAlias"

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        keyStore.getKey(keyAlias, null)?.let { return it as SecretKey }

        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            .apply { init(spec) }
            .generateKey()
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

            // PASS: [MASTG-TEST-0x01] The app encrypts the data using AES/GCM with an AndroidKeyStore-backed key before writing it to internal storage.
            val secretKey = getOrCreateKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.ENCRYPT_MODE, secretKey)
            }
            val iv = cipher.iv
            val ciphertext = cipher.doFinal(encryptedSecret.toByteArray())
            val encryptedFile = File(context.filesDir, "encrypted_data.bin")
            FileOutputStream(encryptedFile).use { output ->
                output.write(iv.size)
                output.write(iv)
                output.write(ciphertext)
                Log.d("FileAPIs", "Written AES/GCM-encrypted data to encrypted_data.bin")
            }
            result += "[PASS]: Stored AES/GCM-encrypted data in encrypted_data.bin using an AndroidKeyStore-backed key.\n\n"

            result
        } catch (e: IOException) {
            "Error during MastgTest: ${e.message ?: "Unknown error"}"
        }
    }
}
