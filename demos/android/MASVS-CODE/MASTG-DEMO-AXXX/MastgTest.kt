package org.owasp.mastestapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.toColorInt
import org.json.JSONArray
import org.json.JSONObject

class MastgTest(private val runnerContext: Context? = null) : AppCompatActivity() {

    companion object {
        val dynamicResults = mutableListOf<Pair<String, String>>()
    }


    @Suppress("unused")
    fun shouldRunInMainThread(): Boolean = false

    fun mastgTest(): String {
        dynamicResults.clear()

        val context = runnerContext ?: return "Error: No Context provided"
        val intent = Intent(context, MastgTest::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        try {
            Thread.sleep(1500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val jsonArray = JSONArray()
        if (dynamicResults.isEmpty()) {
            val obj = JSONObject()
            obj.put("status", "ERROR")
            obj.put("message", "Activity failed to report results.")
            obj.put("demoId", "1")
            jsonArray.put(obj)
        } else {
            for ((status, message) in dynamicResults) {
                val obj = JSONObject()
                obj.put("status", status)
                obj.put("message", message)
                obj.put("demoId", "1")
                jsonArray.put(obj)
            }
        }
        return jsonArray.toString()
    }


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = FrameLayout(this)
        rootLayout.setBackgroundColor(Color.WHITE)
        setContentView(rootLayout)

        val loginContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(60, 60, 60, 60)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val pinField = EditText(this).apply {
            hint = "Enter PIN124"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 50) }
        }

        val loginButton = object : AppCompatButton(this) {
            override fun onFilterTouchEventForSecurity(event: MotionEvent): Boolean {
                if ((event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0 ||
                    (event.flags and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0
                ) {
                    Toast.makeText(context, "Protected! Touch blocked by onFilterTouchEventForSecurity.", Toast.LENGTH_LONG).show()

                    (context as? Activity)?.finish()

                    return false
                }
                return super.onFilterTouchEventForSecurity(event)
            }
        }

        loginButton.text = "Login (Secure)"
        loginButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        loginContainer.addView(pinField)
        loginContainer.addView(loginButton)
        rootLayout.addView(loginContainer)

        loginButton.setOnClickListener {
            Toast.makeText(this, "Login Clicked", Toast.LENGTH_SHORT).show()
        }

        dynamicResults.add(
            Pair(
                "PASS",
                "Secure: Custom onFilterTouchEventForSecurity is implemented via anonymous class to block obscured touches."
            )
        )

        val overlayView = View(this).apply {
            setBackgroundColor("#99FF0000".toColorInt())
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            isClickable = false
            isFocusable = false
        }
        addContentView(overlayView, overlayView.layoutParams)

        val warningText = TextView(this).apply {
            text = "SECURE MODE\nTouches blocked when obscured"
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
                setMargins(0, 100, 0, 0)
            }
        }
        addContentView(warningText, warningText.layoutParams)
    }
}