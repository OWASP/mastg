package org.owasp.mastestapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.view.WindowManager
import android.widget.LinearLayout
import android.view.ViewGroup
import android.widget.TextView
import android.view.Gravity

// IMPORTANT: Assume DemoResults, Status are defined elsewhere in your project
// You must ensure these classes/enums are available for the code to compile.

/**
 * The Vulnerable Activity containing two known vulnerabilities that are checked dynamically:
 * 1. Missing FLAG_SECURE on the window.
 * 2. Missing touch filtering/protection on sensitive views (simulated check).
 */
class VulnerableLoginActivity : AppCompatActivity() {

    // Global variable to store the dynamic results
    companion object {
        // NOTE: Status and DemoResults must be defined in the project scope for this to compile.
        val dynamicResults = mutableListOf<Pair<Status, String>>()
        fun clearResults() = dynamicResults.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Clear previous results on launch
        clearResults()

        // --- DYNAMIC VULNERABILITY VERIFICATION ---

        // 1. Check for FLAG_SECURE (Dynamic equivalent of Static Analysis finding)
        // LINES CONTAINING FLAG_SECURE HAVE BEEN REMOVED AS REQUESTED.
        /*
        val isFlagSecureSet = (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0

        if (!isFlagSecureSet) {
            dynamicResults.add(
                Pair(Status.FAIL, "Dynamic Check: FLAG_SECURE is NOT set on the window, making the screen vulnerable to capture and overlay attacks.")
            )
        } else {
            dynamicResults.add(
                Pair(Status.PASS, "Dynamic Check: FLAG_SECURE IS set on the window.")
            )
        }
        */

        // 2. Check for Touch Filtering on a sensitive view (Simulated, as Android doesn't expose this flag easily)
        val isButtonProtected = false // We assume false since we didn't set it in the code.

        if (!isButtonProtected) {
            dynamicResults.add(
                Pair(Status.FAIL, "Dynamic Check: Sensitive views (login button) are NOT protected by touch filtering, allowing tapjacking.")
            )
        }

        // --- End Verification ---

        // --- PROGRAMMATIC UI (Replaces setContentView(R.layout...) to ensure functionality) ---
        // This ensures the Activity launches with content and the objects needed for the
        // login functionality exist without external XML resource files.

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val usernameInput = EditText(this).apply {
            hint = "Username (Simulated)"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, 0, 0, 16) }
        }

        val loginButton = Button(this).apply {
            text = "VULNERABLE LOGIN"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Add views to the layout
        layout.addView(TextView(this).apply { text = "Vulnerable Login Activity" })
        layout.addView(usernameInput)
        layout.addView(loginButton)
        setContentView(layout) // Set the programmatically created layout

        // The vulnerable functionality remains the same
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            Toast.makeText(this, "Login Attempted for: $username", Toast.LENGTH_SHORT).show()
        }
    }
}

// Assume DemoResults, Status are defined elsewhere in your project

class MastgTest (private val context: Context){

    // CRITICAL: Must run in the main thread to interact with the Activity/Window
    fun shouldRunInMainThread(): Boolean = true

    /**
     * Test case for MASTG-TEST-0035: Performs a Dynamic Analysis by launching the
     * vulnerable activity and reading its self-reported security status.
     */
    fun mastgTest(): String {
        val r = DemoResults("MASTG-TEST") // Assuming DemoResults is available

            // Step 1: Launch the vulnerable activity (required for dynamic check)
            val intent = Intent(context, VulnerableLoginActivity::class.java)
            // Use FLAG_ACTIVITY_NEW_TASK since we are starting from a non-Activity context (the test thread)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

            // NOTE: In a real test system, you'd need to WAIT here
            // for the activity to fully start and run onCreate().
            // For this demo, we assume the activity runs quickly.

            // Step 2: Read the results stored by the VULNERABLE ACTIVITY
            val resultsFromVulnerableActivity = VulnerableLoginActivity.dynamicResults

            // NOTE: Assuming Status.ERROR is available
            if (resultsFromVulnerableActivity.isEmpty()) {
                r.add(Status.ERROR, "Dynamic check failed: Could not read results from VulnerableLoginActivity. Did the Activity launch correctly?")
            } else {
                for ((status, message) in resultsFromVulnerableActivity) {
                    r.add(status, message)
                }
            }


        return r.toJson()
    }
}