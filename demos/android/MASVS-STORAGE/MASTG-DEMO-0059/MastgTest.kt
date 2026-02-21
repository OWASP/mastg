package org.owasp.mastestapp

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import androidx.core.content.edit

class MastgTest(private val context: Context) {
    private val awsKey = "AKIAABCDEFGHIJKLMNOP"
    private val githubToken = "ghp_1234567890abcdefghijklmnopqrstuvABCD"
    private val preSharedKeys = hashSetOf(
        "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALfX7kbfFv3pc3JjOHQ=\n-----END PRIVATE KEY-----",
        "-----BEGIN PRIVATE KEY-----\ngJXS9EwpuzK8U1TOgfplwfKEVngCE2D5FNBQWvNmuHHbigmTCabsA=\n-----END PRIVATE KEY-----"
    )
    private val keyAlias = "mastgKey"

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
            var returnStatus = ""
            val sharedPref = context.getSharedPreferences(
                "MasSharedPref_Sensitive_Data",
                Context.MODE_PRIVATE
            )
            sharedPref.edit {
                putString("UnencryptedGitHubToken", githubToken)
                returnStatus += "[FAIL]: Stored unencrypted sensitive data (Github Token) using putString.\n\n"

                putString("EncryptedAwsKey", encrypt(awsKey))
                returnStatus += "[OK]: Stored encrypted sensitive data (AWS key) using putString.\n\n"

                putStringSet("UnencryptedPreSharedKeys", preSharedKeys)
                returnStatus += "[FAIL]: Stored unencrypted binary keys using putStringSet.\n\n"
            }
            returnStatus
        } catch (e: Exception) {
            "Error during MastgTest: ${e.message ?: "Unknown error"}"
        }
    }
}