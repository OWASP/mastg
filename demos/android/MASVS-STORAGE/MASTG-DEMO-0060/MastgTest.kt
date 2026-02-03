package org.owasp.mastestapp

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class MastgTest(private val context: Context) {
    // WARNING: In a real application, these keys should NOT be hardcoded. They should be stored securely, for instance, in the Android Keystore.
    private val awsKey = "AKIAABCDEFGHIJKLMNOP"
    private val githubToken = "ghp_1234567890abcdefghijklmnopqrstuvABCD"
    private val preSharedKeys = hashSetOf(
        "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALfX7kbfFv3pc3JjOHQ=\n-----END PRIVATE KEY-----",
        "-----BEGIN PRIVATE KEY-----\ngJXS9EwpuzK8U1TOgfplwfKEVngCE2D5FNBQWvNmuHHbigmTCabsA=\n-----END PRIVATE KEY-----"
    )
    private val sharedPrefsName = "MasSharedPref_Sensitive_Data"

    fun mastgTest(): String {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                sharedPrefsName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            encryptedPrefs.edit {
                putString("EncryptedAWSKey", awsKey)
                putString("GitHubToken", githubToken)
                putStringSet("preSharedKeys", preSharedKeys)
            }

            "Sensitive data has been written and deleted in the sandbox."
        } catch (e: Exception) {
            "Error during MastgTest: ${e.message ?: "Unknown error"}"
        }
    }
}
