package org.owasp.mastestapp

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

// SUMMARY: This sample demonstrates different approaches to handling overlay attacks in Android apps, 
// showing both vulnerable patterns and proper protections using filterTouchesWhenObscured.

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

@Composable
fun MainScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        // FAIL: [MASTG-TEST-0035] Sensitive button without overlay protection
        Button(
            onClick = { 
                // Sensitive action: confirming a payment
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Vulnerable: Confirm Payment")
        }
        
        // PASS: [MASTG-TEST-0035] Button with overlay protection using filterTouchesWhenObscured
        AndroidView(
            factory = { context ->
                Button(context).apply {
                    text = "Protected: Confirm Payment"
                    filterTouchesWhenObscured = true
                    setOnClickListener {
                        // Sensitive action protected from overlay attacks
                        Toast.makeText(context, "Payment confirmed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        
        // PASS: [MASTG-TEST-0035] Custom view with manual obscured check
        AndroidView(
            factory = { context ->
                object : Button(context) {
                    override fun onFilterTouchEventForSecurity(event: MotionEvent): Boolean {
                        if ((event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {
                            // Window is obscured, filter the touch event
                            Toast.makeText(context, "Touch blocked - window obscured", Toast.LENGTH_SHORT).show()
                            return false
                        }
                        return super.onFilterTouchEventForSecurity(event)
                    }
                }.apply {
                    text = "Custom Protection: Grant Permission"
                    setOnClickListener {
                        // Sensitive permission grant protected by custom implementation
                        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}
