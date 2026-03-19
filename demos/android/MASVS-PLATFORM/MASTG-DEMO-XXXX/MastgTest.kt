// SUMMARY: This sample demonstrates a vulnerable login activity whose layout does not set android:filterTouchesWhenObscured="true", leaving sensitive UI elements exposed to overlay (tapjacking) attacks.
package org.owasp.mastestapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import org.json.JSONArray
import org.json.JSONObject

class MastgTest(private val context: Context) {

    @Suppress("unused")
    fun shouldRunInMainThread(): Boolean = false

    fun mastgTest(): String {
        VulnerableLoginActivity.clearResults()

        val intent = Intent(context, VulnerableLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val results = VulnerableLoginActivity.dynamicResults
        val jsonArray = JSONArray()

        if (results.isEmpty()) {
            val obj = JSONObject()
            obj.put("status", "ERROR")
            obj.put("message", "Activity failed to report results.")
            obj.put("demoId", "1")
            jsonArray.put(obj)
        } else {
            for ((status, message) in results) {
                val obj = JSONObject()
                obj.put("status", status)
                obj.put("message", message)
                obj.put("demoId", "1")
                jsonArray.put(obj)
            }
        }

        return jsonArray.toString()
    }
}

class VulnerableLoginActivity : AppCompatActivity() {

    companion object {
        val dynamicResults = mutableListOf<Pair<String, String>>()
        fun clearResults() = dynamicResults.clear()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_vulnerable_login)

        val loginButton = findViewById<Button>(R.id.login_button)

        val isButtonProtected = loginButton.filterTouchesWhenObscured

        // FAIL: The login button does not have filterTouchesWhenObscured set to true, allowing overlay attacks to intercept touch events on this sensitive UI element.
        if (!isButtonProtected) {
            dynamicResults.add(
                Pair(
                    "FAIL",
                    "Dynamic Check: Sensitive views (login button) are NOT protected by touch filtering (filterTouchesWhenObscured is false)."
                )
            )
        } else {
            dynamicResults.add(Pair("PASS", "Button is protected."))
        }

        loginButton.setOnClickListener {
            Toast.makeText(this, "Login Clicked (Vulnerable)", Toast.LENGTH_SHORT).show()
        }

        val overlayView = View(this).apply {
            setBackgroundColor("#88FF0000".toColorInt())
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            isClickable = false
            isFocusable = false
        }

        addContentView(overlayView, overlayView.layoutParams)

        val warningText = TextView(this).apply {
            text = "Vulnerable to Overlay Attack "
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            setPadding(0, 100, 0, 0)
        }
        addContentView(
            warningText, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }
}