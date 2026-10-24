package org.owasp.mastestapp

import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.Base64


class MastgTest(@Suppress("unused") private val context: Context) {

    open class BaseUser(val username: String) : Serializable {
        companion object {
            // Different UID to distinguish from the Admin subclass
            @JvmStatic
            val serialVersionUID = 100L
        }
    }


    class AdminUser(username: String) : BaseUser(username) {

        var isAdmin: Boolean = false

        companion object {
            @JvmStatic
            val serialVersionUID = 200L
        }
    }


    object UserManager {

        var currentUser: BaseUser = BaseUser("Standard User")
    }


    fun mastgTest(): String {
        // SUMMARY: This sample demonstrates insecure object deserialization from an untrusted source (Intent extra).
        val user = UserManager.currentUser
        val status = if (user is AdminUser && user.isAdmin) {
            "PRIVILEGED ADMIN!"
        } else {
            "(Not an Admin)"
        }

        val resultString = "Current User: ${user.username}\n" +
                "Status: $status\n\n" +
                "Vulnerability: Unwanted Object Deserialization is active.\n" +
                "The app will deserialize any 'BaseUser' subclass from the 'payload_b64' extra, " +
                "overwriting the current user state.\n\n" +
                "ADB command to trigger:\nadb shell am start -n org.owasp.mastestapp/.MainActivity --es payload_b64 'rO0ABXNyAChvcmcub3dhc3AubWFzdGVzdGFwcC5NYXN0Z1Rlc3QkQWRtaW5Vc2VyAAAAAAAAAMgCAAFaAAdpc0FkbWlueHIAJ29yZy5vd2FzcC5tYXN0ZXN0YXBwLk1hc3RnVGVzdCRCYXNlVXNlcgAAAAAAAABkAgABTAAIdXNlcm5hbWV0ABJMamF2YS9sYW5nL1N0cmluZzt4cHQAD0V4cGxvaXRlZCBBZG1pbgE='"

        Log.d("MASTG-TEST", resultString)
        return resultString
    }


    fun processIntent(intent: Intent) {
        if (intent.hasExtra("payload_b64")) {
            val b64Payload = intent.getStringExtra("payload_b64")
            Log.d("VULN_APP", "Received a base64 payload. Deserializing user object...")

            try {
                val serializedPayload = Base64.getDecoder().decode(b64Payload)
                // FAIL: [MASTG-TEST-0337] The app deserializes objects from an untrusted source without any type filtering or validation.
                val ois = ObjectInputStream(ByteArrayInputStream(serializedPayload))

                val untrustedObject = ois.readObject()
                ois.close()

                if (untrustedObject is BaseUser) {
                    UserManager.currentUser = untrustedObject
                    Log.i("VULN_APP", "User state overwritten with deserialized object!")
                } else {
                    Log.w("VULN_APP", "Deserialized object was not a user. State unchanged.")
                }

            } catch (e: Exception) {
                Log.e("VULN_APP", "Failed to deserialize payload", e)
            }
        }
    }
}