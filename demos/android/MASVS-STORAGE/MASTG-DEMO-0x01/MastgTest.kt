package org.owasp.mastestapp

// SUMMARY: This sample demonstrates storing sensitive data unencrypted to the app's internal storage using the Java File APIs (openFileOutput and FileOutputStream).

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MastgTest(private val context: Context) {

    private val password = "MyS3cr3tP4ssw0rd"
    private val apiKey = "AKIAABCDEFGHIJKLMNOP"

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

            result
        } catch (e: IOException) {
            "Error during MastgTest: ${e.message ?: "Unknown error"}"
        }
    }
}
