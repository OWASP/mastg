package org.owasp.mastestapp

import android.os.Bundle
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivityWebView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebViewScreen()
        }
    }

    override fun onStop() {
        WebStorage.getInstance().deleteAllData()
        super.onStop()
    }
}

@Composable
@Preview
fun WebViewScreen() {
    val context = LocalContext.current
    val mastgTestWebViewClass = MastgTestWebView(context)
    var showWebView by remember { mutableStateOf(false) } // Controls whether the WebView is displayed
    var webViewKey by remember { mutableIntStateOf(0) }   // Unique key to force WebView recreation
    var webViewRef by remember { mutableStateOf<WebView?>(null) } // Holds a reference to the WebView

    // Use BackHandler to intercept the back press when the WebView is visible.
    if (showWebView) {
        BackHandler {
            // Check if the WebView can go back in history.
            if (webViewRef?.canGoBack() == true) {
                webViewRef?.goBack()
            } else {
                // If no back history exists, hide the WebView.
                showWebView = false
            }
        }
    }

    BaseScreen(
        onStartClick = {
            showWebView = true // Show the WebView
            webViewKey++       // Increment key to create/recreate WebView
        }
    ) {
        if (showWebView) {
            // Recompose AndroidView with a new key when showWebView is true
            key(webViewKey) {
                AndroidView(
                    factory = {
                        WebView(context).apply {
                            webViewRef = this // Store a reference to the WebView.
                            mastgTestWebViewClass.mastgTest(this)
                            setBackgroundColor(0)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        } else {
            // Placeholder with text
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Click \"Start\" to open the WebView.",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}