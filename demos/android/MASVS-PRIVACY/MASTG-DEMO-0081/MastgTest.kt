package org.owasp.mastestapp

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlin.random.Random

class MastgTest(context: Context) {

    val analytics = FirebaseAnalytics.getInstance(context)

    // Random arbitrary number for the sake of the demo
    val userId: String = (1..8).map { Random.nextInt(0, 10) }.joinToString("")

    fun mastgTest(bloodType: String): String {
        analytics.logEvent("user_blood_type") {
            param("user_id", userId)
            param("blood_type", bloodType)
        }

        return """
            'user_blood_type' event was sent to Firebase Analytics.

            User id: $userId
            Blood type: $bloodType
        """.trimIndent()
    }
}