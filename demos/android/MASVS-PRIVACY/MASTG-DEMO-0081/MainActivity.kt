package org.owasp.mastestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement

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
        } catch (_: Exception) {
            // not a valid set of DemoResult, so print the result without any parsing
            append(result)
        }
    }

}

@Preview
@Composable
fun MainScreen() {
    val defaultMessage = "Click \"Start\" to send the data.\n\n"
    var displayString by remember { mutableStateOf(buildAnnotatedString { append(defaultMessage) }) }
    var selectedBloodType by remember { mutableStateOf("") }
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
            if (runInMainThread) {
                val result = mastgTestClass.mastgTest(selectedBloodType)
                displayString = UpdateDisplayString(defaultMessage, result)
            } else {
                Thread {
                    val result = mastgTestClass.mastgTest(selectedBloodType)
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        displayString = UpdateDisplayString(defaultMessage, result)
                    }
                }.start()
            }
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Normal visible selection UI: list of radio buttons for blood types
            val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

            var expanded by remember { mutableStateOf(false) }

            Button(onClick = { expanded = !expanded }) {
                Text("Select Blood Type")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                bloodTypes.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            selectedBloodType = it
                            expanded = false
                        }
                    )
                }
            }

            if (selectedBloodType.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White,
                    text = "Selected Blood Type: $selectedBloodType"
                )
            }

            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag(MASTG_TEXT_TAG),
                text = displayString,
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
