// SUMMARY: This sample demonstrates the use of HMAC to verify SharedPreferences data integrity.

package org.owasp.mastestapp

import android.content.Context
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
    }

    fun mastgTest(): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        if (!prefs.contains("setup_complete")) {
            val initialRole = "user" 
            saveData("user_role_insecure", initialRole, useHmac = false)
            saveData("user_role_secure", initialRole, useHmac = true)

            prefs.edit(commit = true) {
                putBoolean("setup_complete", true)
            }

            return "INITIAL SETUP COMPLETE.\n\n" +
                    "The role for both secure and insecure tests has been set to 'user'.\n\n" +
                    "ACTION REQUIRED:\n" +
                    "1. Use a file explorer or ADB shell on a rooted device.\n" +
                    "2. Go to: /data/data/org.owasp.mastestapp/shared_prefs/\n" +
                    "3. Open the file: app_settings.xml\n" +
                    "4. Change BOTH <string>user</string> values to <string>admin</string>.\n" +
                    "5. Save the file and run this test again to see the results." +
                    "   OR use this ADB one-liner:\n" +
                    "   adb shell \"su -c 'sed -i \\\"s/>user<\\/>admin<\\/g\\\" /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml'\"\n"

        } else {

            val results = StringBuilder()

            results.append("--- VERIFYING SCENARIO 1: 'kind: fail' (No HMAC Protection) ---\n")
            // FAIL: [MASTG-TEST-XXXX] Data is loaded without integrity verification.
            val insecureRole = loadData("user_role_insecure", "error", useHmac = false)
            results.append("Loaded role from 'user_role_insecure': '$insecureRole'\n")
            if (insecureRole == "admin") {
                results.append(">> OUTCOME: VULNERABLE. The application accepted the tampered 'admin' role because there was no integrity check.\n")
            } else {
                results.append(">> OUTCOME: NOT EXPLOITED. The role is still '$insecureRole'. Please ensure you changed it to 'admin' in the XML file.\n")
            }

            results.append("\n--- VERIFYING SCENARIO 2: 'kind: pass' (HMAC Protection Enabled) ---\n")
            // PASS: [MASTG-TEST-XXXX] Data is loaded with a valid HMAC integrity check.
            val secureRole = loadData("user_role_secure", "tampering_detected", useHmac = true)
            results.append("Loaded role from 'user_role_secure': '$secureRole'\n")

            when (secureRole) {
                "tampering_detected" -> results.append(">> OUTCOME: SECURE. The application detected tampering and rejected the role.\n")
                "admin" -> results.append(">> OUTCOME: UNEXPECTED. The role is 'admin', HMAC check failed.\n")
                else -> results.append(">> OUTCOME: NOT TAMPERED. The role is still '$secureRole', and its HMAC is valid.\n")
            }

            results.append("\n\nTest complete.")
            return results.toString()
        }
    }
    
    @Suppress("SameParameterValue")
    private fun saveData(key: String, value: String, useHmac: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit(commit = true) {
            putString(key, value)
            if (useHmac) {
                calculateHmac(value)?.let { hmac ->
                    putString("${key}_hmac", hmac)
                    Log.d("MASTG-TEST", "Saved data with HMAC.")
                }
            } else {
                Log.d("MASTG-TEST", "Saved data WITHOUT HMAC.")
            }
        }
    }

    private fun loadData(key: String, defaultValue: String, useHmac: Boolean): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val value = prefs.getString(key, null) ?: return defaultValue

        if (!useHmac) {
            Log.d("MASTG-TEST", "Loaded data without HMAC check. Value is: $value")
            return value
        }

        val storedHmac = prefs.getString("${key}_hmac", null)
        if (storedHmac == null) {
            Log.w("MASTG-TEST", "HMAC verification failed: No HMAC found for key '$key'.")
            return defaultValue
        }

        val calculatedHmac = calculateHmac(value)

        return if (storedHmac == calculatedHmac) {
            Log.d("MASTG-TEST", "HMAC verification SUCCESS. Value is: $value")
            value
        } else {
            Log.e("MASTG-TEST", "HMAC verification FAILED! Data has been tampered with.")
            defaultValue
        }
    }

    private fun calculateHmac(data: String): String? {
        return try {
            val mac = Mac.getInstance(HMAC_ALGORITHM)
            val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(), HMAC_ALGORITHM)
            mac.init(secretKeySpec)
            val hmacBytes = mac.doFinal(data.toByteArray())
            bytesToHex(hmacBytes)
        } catch (e: NoSuchAlgorithmException) {
            Log.e("MASTG-TEST", "HMAC algorithm not found", e)
            null
        } catch (e: InvalidKeyException) {
            Log.e("MASTG-TEST", "Invalid HMAC key", e)
            null
        }
    }


    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789abcdef"
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach {
            val i = it.toInt()
            result.append(hexChars[i shr 4 and 0x0f])
            result.append(hexChars[i and 0x0f])
        }
        return result.toString()
    }
}
