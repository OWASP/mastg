package org.owasp.mastestapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MastgTest(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val HMAC_ALGORITHM = "HmacSHA256"
        private const val SECRET_KEY = "this-is-a-very-secret-key-for-the-demo"

        private const val KEY_SETUP_COMPLETE = "setup_complete"
        private const val KEY_ROLE_INSECURE = "user_role_insecure"
        private const val KEY_ROLE_SECURE = "user_role_secure"
        private const val KEY_ROLE_SECURE_HMAC = "user_role_secure_hmac"

        private const val DEFAULT_ROLE = "user"
        private const val TAMPERING_DETECTED = "tampering_detected"
        private const val TAG = "MASTG-TEST"
    }

    fun mastgTest(): String {
        val prefs = prefs()

        if (!prefs.contains(KEY_SETUP_COMPLETE)) {
            initializeDemoData()
            return """
SETUP DONE.

Stored values:
insecure = user
secure = user
secure_hmac = HMAC(user)

Run the app again after editing app_settings.xml.
""".trimIndent()
        }

        val insecureRole = loadPlain(KEY_ROLE_INSECURE, "error")
        val secureRole = loadProtected(KEY_ROLE_SECURE, TAMPERING_DETECTED)

        return """
Stored now:
insecure = ${prefs.getString(KEY_ROLE_INSECURE, null)}
secure = ${prefs.getString(KEY_ROLE_SECURE, null)}
secure_hmac = ${prefs.getString(KEY_ROLE_SECURE_HMAC, null)}

Loaded:
insecure = $insecureRole
secure = $secureRole

Result:
${describeInsecure(insecureRole)}
${describeSecure(secureRole)}
""".trimIndent()
    }

    private fun describeInsecure(value: String): String {
        return when (value) {
            "admin" -> "❌ Insecure check bypassed."
            "user" -> "✅ Insecure value unchanged."
            else -> "⚠️ Insecure value unexpected."
        }
    }

    private fun describeSecure(value: String): String {
        return when (value) {
            TAMPERING_DETECTED -> "✅ Secure check detected tampering."
            "admin" -> "⚠️ Secure check bypassed with forged HMAC."
            "user" -> "✅ Secure value unchanged."
            else -> "⚠️ Secure value unexpected."
        }
    }

    private fun initializeDemoData() {
        savePlain(KEY_ROLE_INSECURE, DEFAULT_ROLE)
        saveProtected(KEY_ROLE_SECURE, DEFAULT_ROLE)
        prefs().edit(commit = true) {
            putBoolean(KEY_SETUP_COMPLETE, true)
        }
    }

    private fun savePlain(key: String, value: String) {
        prefs().edit(commit = true) {
            putString(key, value)
        }
        Log.d(TAG, "Saved plain value, key=$key, value=$value")
    }

    private fun saveProtected(key: String, value: String) {
        val hmac = calculateHmac(value)
        prefs().edit(commit = true) {
            putString(key, value)
            putString("${key}_hmac", hmac)
        }
        Log.d(TAG, "Saved protected value, key=$key, value=$value, hmac=$hmac")
    }

    private fun loadPlain(key: String, defaultValue: String): String {
        return prefs().getString(key, defaultValue) ?: defaultValue
    }

    private fun loadProtected(key: String, defaultValue: String): String {
        val value = prefs().getString(key, null) ?: return defaultValue
        val storedHmac = prefs().getString("${key}_hmac", null) ?: return defaultValue
        val calculatedHmac = calculateHmac(value)
        return if (storedHmac == calculatedHmac) value else defaultValue
    }

    private fun calculateHmac(data: String): String {
        return try {
            val mac = Mac.getInstance(HMAC_ALGORITHM)
            val key = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), HMAC_ALGORITHM)
            mac.init(key)
            bytesToHex(mac.doFinal(data.toByteArray(Charsets.UTF_8)))
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "HMAC algorithm not found", e)
            ""
        } catch (e: InvalidKeyException) {
            Log.e(TAG, "Invalid HMAC key", e)
            ""
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789abcdef"
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach { b ->
            val i = b.toInt() and 0xff
            result.append(hexChars[i ushr 4])
            result.append(hexChars[i and 0x0f])
        }
        return result.toString()
    }

    private fun prefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}