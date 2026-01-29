package org.owasp.mastestapp

import android.content.Context
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout

// SUMMARY: This sample demonstrates different approaches to handling overlay attacks in Android apps, 
// showing both vulnerable patterns and proper protections using filterTouchesWhenObscured.

class MastgTest (private val context: Context){

    fun mastgTest(): String {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        // FAIL: [MASTG-TEST-0x35] Sensitive button without overlay protection
        val vulnerableButton = Button(context).apply {
            text = "Vulnerable: Confirm Payment"
            setOnClickListener {
                // Sensitive action: confirming a payment
            }
        }
        layout.addView(vulnerableButton)
        
        // PASS: [MASTG-TEST-0x35] Button with overlay protection using filterTouchesWhenObscured
        val protectedButton = Button(context).apply {
            text = "Protected: Confirm Payment"
            filterTouchesWhenObscured = true
            setOnClickListener {
                // Sensitive action protected from overlay attacks
            }
        }
        layout.addView(protectedButton)
        
        // PASS: [MASTG-TEST-0x35] Custom view with manual obscured check
        val customProtectedButton = object : Button(context) {
            override fun onFilterTouchEventForSecurity(event: MotionEvent): Boolean {
                if ((event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {
                    // Window is obscured, filter the touch event
                    return false
                }
                return super.onFilterTouchEventForSecurity(event)
            }
        }.apply {
            text = "Custom Protection: Grant Permission"
            setOnClickListener {
                // Sensitive permission grant protected by custom implementation
            }
        }
        layout.addView(customProtectedButton)

        return "Created buttons with various overlay protections:\n" +
               "1. Vulnerable button (no protection)\n" +
               "2. Protected button (filterTouchesWhenObscured)\n" +
               "3. Custom protected button (onFilterTouchEventForSecurity)"
    }
}
