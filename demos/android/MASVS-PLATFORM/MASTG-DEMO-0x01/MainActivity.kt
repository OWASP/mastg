package org.owasp.mastestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.isSensitiveData
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.sensitiveContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement

// SUMMARY: This sample demonstrates a Jetpack Compose payment form whose UI elements handle sensitive data with and without
// the isSensitiveData semantics property, which restricts access to accessibility services declared as accessibility tools.

const val MASTG_TEXT_TAG = "mastgTestText"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

fun UpdateDisplayString(
    defaultMessage: String,
    displayString: AnnotatedString,
    result: String
): AnnotatedString {
    return buildAnnotatedString {
        append(defaultMessage)
        try {
            val jsonArrayFromString = Json.parseToJsonElement(result) as JsonArray
            val demoResults = jsonArrayFromString.map { Json.decodeFromJsonElement<DemoResult>(it) }

            for (demoResult in demoResults) {
                when (demoResult.status) {
                    Status.PASS -> {
                        withStyle(style = SpanStyle(color = Color.Green)) {
                            append("MASTG-DEMO-${demoResult.demoId} demonstrated a successful test:\n${demoResult.message}\n\n")
                        }
                    }
                    Status.FAIL -> {
                        withStyle(style = SpanStyle(color = Color(0xFFFF9800))) {
                            append("MASTG-DEMO-${demoResult.demoId} demonstrated a failed test:\n${demoResult.message}\n\n")
                        }
                    }
                    Status.ERROR -> {
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append("MASTG-DEMO-${demoResult.demoId} failed:\n${demoResult.message}\n\n")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // not a valid set of DemoResult, so print the result without any parsing
            append(result)
        }
    }

}

@Preview
@Composable
fun MainScreen() {
    val defaultMessage = "Click \"Start\" to run the test.\n\n"
    var displayString by remember { mutableStateOf(buildAnnotatedString { append(defaultMessage) }) }
    var showPaymentForm by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val mastgTestClass = MastgTest(context)
    // By default run the test in a separate thread, this ensures that network tests such as those using SSLSocket work properly.
    // However, some tests which interact with UI elements need to run on the main thread.
    // You can set shouldRunInMainThread = true in MastgTest.kt for those tests.
    val runInMainThread = MastgTest::class.members
        .find { it.name == "shouldRunInMainThread" }
        ?.call(mastgTestClass) as? Boolean ?: false

    BaseScreen(
        onStartClick = {
            showPaymentForm = true
        }
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp)
                .testTag(MASTG_TEXT_TAG),
            text = displayString,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )
    }

    if (showPaymentForm) {
        PaymentDialog(
            onResult = { message ->
                showPaymentForm = false
                displayString = UpdateDisplayString(defaultMessage, displayString, message)
            }
        )
    }
}

@Composable
fun PaymentDialog(onResult: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult("Payment cancelled.") },
        title = { Text("Payment") },
        text = {
            Column {
                // FAIL: [MASTG-TEST-0403] The username field handles credentials but isn't marked with isSensitiveData.
                TextField(
                    state = rememberTextFieldState(),
                    label = { Text("Username") },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .semantics { isSensitiveData = false }
                        // Unrelated to accessibility services:
                        // The window containing the sensitive field will be hidden when doing screen sharing except recordings made with the default system recorder.
                        // Only enforced by the OS on Android 15 (API 35)+
                        .sensitiveContent(isContentSensitive = true)
                )
                // FAIL: [MASTG-TEST-0403] The password field masks its text, but it isn't marked with isSensitiveData.
                SecureTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Password") },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                // PASS: [MASTG-TEST-0403] The card number field is marked with isSensitiveData.
                TextField(
                    state = rememberTextFieldState(),
                    label = { Text("Card number") },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .semantics { isSensitiveData = true }
                )
            }
        },
        confirmButton = {
            // FAIL: [MASTG-TEST-0403] The payment confirmation button triggers a sensitive action but isn't marked with isSensitiveData.
            Button(onClick = { onResult("Payment confirmed.") }) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            // The Cancel button doesn't handle sensitive data, so it isn't marked with isSensitiveData.
            TextButton(onClick = { onResult("Payment cancelled.") }) {
                Text("Cancel")
            }
        }
    )
}
