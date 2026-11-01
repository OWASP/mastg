package org.owasp.mastestapp

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo

// SUMMARY: A non-tool accessibility service enumerates all accessible
// UI elements without knowing the target application's layout or labels.

class MastgTest(private val context: Context) {

    val shouldRunInMainThread = true

    fun mastgTest(): String {
        val r = DemoResults("0x02")

        val manager = context.getSystemService(
            Context.ACCESSIBILITY_SERVICE
        ) as AccessibilityManager

        val enabled = manager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        ).any {
            it.resolveInfo.serviceInfo.name ==
                MastgAccessibilityService::class.java.name
        }

        if (!enabled) {
            context.startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            r.add(Status.ERROR, "Enable the accessibility service and retry.")
        } else {
            r.add(Status.PASS, "Observer active. Open any app to capture its accessibility hierarchy.")
        }

        return r.toJson()
    }
}

class MastgAccessibilityService : AccessibilityService() {

    private val tag = "MASTG-A11Y"

    private var lastPackage: String? = null
    private var previousNodes = emptyMap<String, String>()

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceInfo = serviceInfo.apply {
            packageNames = null

            eventTypes =
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_SCROLLED

            flags = flags or
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS

            notificationTimeout = 100
        }

        Log.i(tag, "Observer connected. isAccessibilityTool=false")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val root = rootInActiveWindow ?: return
        val pkg = root.packageName?.toString() ?: return

        if (pkg == packageName) {
            lastPackage = null
            previousNodes = emptyMap()
            return
        }

        val currentNodes = linkedMapOf<String, String>()
        collectNodes(root, "0", currentNodes)

        val packageChanged = pkg != lastPackage

        if (packageChanged) {
            Log.i(tag, "===== SCREEN: $pkg =====")
            previousNodes = emptyMap()
            lastPackage = pkg
        }

        currentNodes.forEach { (path, value) ->
            val previous = previousNodes[path]

            if (previous != value) {
                val change = if (previous == null) "FOUND" else "CHANGED"
                Log.i(tag, "$change [$path] $value")
            }
        }

        previousNodes = currentNodes
    }

    private fun collectNodes(
        node: AccessibilityNodeInfo,
        path: String,
        result: MutableMap<String, String>
    ) {
        if (node.isVisibleToUser) {
            describeNode(node)?.let {
                result[path] = it
            }
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                collectNodes(it, "$path.$i", result)
            }
        }
    }

    private fun describeNode(node: AccessibilityNodeInfo): String? {
        val text = clean(node.text)
        val hint = clean(node.hintText)
        val description = clean(node.contentDescription)

        // Ignore containers without meaningful content or actions.
        if (!node.isEditable &&
            !node.isClickable &&
            text.isEmpty() &&
            hint.isEmpty() &&
            description.isEmpty()
        ) {
            return null
        }

        val type = when {
            node.isEditable -> "EDIT"
            node.isClickable -> "ACTION"
            else -> "TEXT"
        }

        return buildString {
            append(type)

            if (node.isEditable) {
                val value = if (node.isShowingHintText) "" else text
                append(" value=\"$value\"")
            } else if (text.isNotEmpty()) {
                append(" text=\"$text\"")
            }

            if (hint.isNotEmpty()) {
                append(" hint=\"$hint\"")
            }

            if (description.isNotEmpty()) {
                append(" description=\"$description\"")
            }

            if (node.isClickable) {
                append(" clickable=true")
            }

            if (node.isPassword) {
                append(" password=true")
            }

            node.viewIdResourceName?.let {
                append(" id=$it")
            }
        }
    }

    private fun clean(value: CharSequence?): String {
        val text = value?.toString()
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            .orEmpty()

        return if (text.length > 200) {
            "${text.take(200)}... (${text.length} characters)"
        } else {
            text
        }
    }

    override fun onInterrupt() {}
}