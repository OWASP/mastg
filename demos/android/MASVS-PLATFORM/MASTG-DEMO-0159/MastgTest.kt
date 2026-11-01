package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast

// SUMMARY: This sample demonstrates views that handle sensitive data with and without accessibility data sensitivity,
// set explicitly, inherited from a parent, implied by filterTouchesWhenObscured, or explicitly disabled.

class MastgTest(private val context: Context) {

    val shouldRunInMainThread = true

    private val formTag = "mastg-demo-form"

    fun mastgTest(): String {
        val r = DemoResults("0x01")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            r.add(Status.ERROR, "Accessibility data sensitivity requires Android 14 (API level 34) or higher.")
            return r.toJson()
        }

        val form = LinearLayout(context).apply {
            tag = formTag
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(48, 48, 48))
            setOnApplyWindowInsetsListener { view, insets ->
                val bottom = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.ime()).bottom
                view.setPadding(48, 32, 48, 32 + bottom)
                insets
            }
        }

        // FAIL: [MASTG-TEST-0403] The username field handles credentials but isn't marked as accessibility data sensitive.
        val usernameField = createField("Username", InputType.TYPE_CLASS_TEXT)
        form.addView(usernameField)

        // FAIL: [MASTG-TEST-0403] The password field masks its text, but it isn't marked as accessibility data sensitive.
        val passwordField = createField("Password", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        form.addView(passwordField)

        // PASS: [MASTG-TEST-0403] The one-time code field uses filterTouchesWhenObscured, which implies accessibility data sensitivity.
        val otpField = createField("One-time code", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        otpField.filterTouchesWhenObscured = true
        form.addView(otpField)

        // PASS: [MASTG-TEST-0403] The payment container is marked as accessibility data sensitive, so its children inherit the protection.
        val paymentContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        paymentContainer.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)

        val cardField = createField("Card number", InputType.TYPE_CLASS_NUMBER)
        paymentContainer.addView(cardField)

        // FAIL: [MASTG-TEST-0403] The payment confirmation button explicitly disables the protection inherited from its parent.
        val confirmButton = Button(context).apply {
            text = "Confirm Payment"
            setOnClickListener { closeForm(form, "Payment confirmed.") }
        }
        confirmButton.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_NO)
        paymentContainer.addView(confirmButton)

        form.addView(paymentContainer)

        val cancelButton = Button(context).apply {
            text = "Cancel"
            setOnClickListener { closeForm(form, "Payment cancelled.") }
        }
        form.addView(cancelButton)

        (context as? Activity)?.let { activity ->
            val root = activity.findViewById<ViewGroup>(android.R.id.content)
            root.findViewWithTag<View>(formTag)?.let { root.removeView(it) }
            root.addView(
                form,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
                )
            )
            form.requestApplyInsets()
        }

        listOf(
            "Username field" to usernameField,
            "Password field" to passwordField,
            "One-time code field" to otpField,
            "Card number field" to cardField,
            "Confirm Payment button" to confirmButton
        ).forEach { (name, view) ->
            val sensitive = view.isAccessibilityDataSensitive()
            r.add(
                if (sensitive) Status.PASS else Status.FAIL,
                "$name: isAccessibilityDataSensitive=$sensitive"
            )
        }

        return r.toJson()
    }

    private fun closeForm(form: View, message: String) {
        (form.parent as? ViewGroup)?.removeView(form)
        Log.i("MASTG-DEMO", message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun createField(hintText: String, type: Int): EditText {
        return EditText(context).apply {
            hint = hintText
            inputType = type
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
        }
    }
}
