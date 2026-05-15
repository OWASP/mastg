package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast

// SUMMARY: This sample demonstrates activity-level overlay protection using setHideOverlayWindows(true),
// which prevents any overlay window from appearing over the activity. It requires the HIDE_OVERLAY_WINDOWS
// permission and targets Android 12 (API level 31) and above.

class MastgTest(private val context: Context) {

    val shouldRunInMainThread = true

    fun mastgTest(): String {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.TRANSPARENT)
        }

        val halfWidth = (context.resources.displayMetrics.widthPixels * 0.5).toInt()

        val buttonParams = LinearLayout.LayoutParams(
            halfWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = 24
        }

        val vulnerableButton = Button(context).apply {
            text = "Confirm Payment"
            setOnClickListener {
                Toast.makeText(context, "Button clicked", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(vulnerableButton, buttonParams)

        (context as? Activity)?.let { activity ->
            // PASS: [MASTG-TEST-0x01] The activity uses setHideOverlayWindows(true) to prevent overlay
            // windows from appearing over this activity, protecting all sensitive UI elements at once.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                activity.window.setHideOverlayWindows(true)
            }

            val root = activity.findViewById<ViewGroup>(android.R.id.content)
            if (layout.parent == null) {
                root.addView(
                    layout,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }

        return "Created button with activity level overlay protection"
    }
}