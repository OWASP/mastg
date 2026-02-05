package org.owasp.mastestapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

// SUMMARY: This sample demonstrates programmatic configuration of accessibility data sensitivity in Android.

class MastgTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // PASS: [MASTG-TEST-0321] Programmatically set accessibility data sensitivity on a button
        val confirmButton = findViewById<Button>(R.id.loginButton)
        confirmButton.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)

        // Check current sensitivity settings
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val isDataSensitive = passwordField.accessibilityDataSensitive
        val isFilteringTouches = passwordField.isFilterTouchesWhenObscured

        // PASS: [MASTG-TEST-0321] Programmatically set filterTouchesWhenObscured for additional protection
        val usernameField = findViewById<EditText>(R.id.usernameField)
        usernameField.setFilterTouchesWhenObscured(true)

        // Example: Conditionally disable protection (should be avoided for sensitive views)
        // FAIL: [MASTG-TEST-0321] Never explicitly disable protection on sensitive views
        // val creditCardField = findViewById<EditText>(R.id.creditCardField)
        // creditCardField.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_NO)
    }
}
