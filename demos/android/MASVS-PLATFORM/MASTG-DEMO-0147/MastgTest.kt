package org.owasp.mastestapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class MastgTest(private val context: Context) {

    // SUMMARY: This sample demonstrates insecure uses of PendingIntent in Android, including mutable PendingIntents and implicit base intents.

    fun mastgTest(): String {
        val results = StringBuilder()

        // FAIL: [MASTG-TEST-0313] Mutable PendingIntent with implicit intent - vulnerable to hijacking
        val implicitIntent = Intent(Intent.ACTION_VIEW)
        val mutablePendingIntent = PendingIntent.getActivity(
            context,
            0,
            implicitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT  // Missing FLAG_IMMUTABLE
        )
        results.append("Created mutable PendingIntent with implicit intent\n")

        // FAIL: [MASTG-TEST-0313] Explicit FLAG_MUTABLE used without justification
        val explicitMutableIntent = Intent(context, MastgTest::class.java)
        val explicitMutablePendingIntent = PendingIntent.getService(
            context,
            1,
            explicitMutableIntent,
            PendingIntent.FLAG_MUTABLE
        )
        results.append("Created explicitly mutable PendingIntent\n")

        // FAIL: [MASTG-TEST-0313] Broadcast with implicit intent
        val broadcastIntent = Intent("com.example.CUSTOM_ACTION")
        val broadcastPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            broadcastIntent,
            0  // No flags - mutable by default on API < 31
        )
        results.append("Created broadcast PendingIntent with implicit intent\n")

        // PASS: [MASTG-TEST-0313] Secure PendingIntent with FLAG_IMMUTABLE and explicit intent
        val secureIntent = Intent(context, MastgTest::class.java).apply {
            setPackage(context.packageName)
        }
        val securePendingIntent = PendingIntent.getActivity(
            context,
            3,
            secureIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        results.append("Created secure PendingIntent with FLAG_IMMUTABLE\n")

        return results.toString()
    }
}
