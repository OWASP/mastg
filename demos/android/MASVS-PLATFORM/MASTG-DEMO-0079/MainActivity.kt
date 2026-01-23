package org.owasp.mastestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

@Preview
@Composable
fun MainScreen() {
    BaseScreen {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            TextField(
                state = rememberTextFieldState(),
                label = { Text("Username") },
                modifier = Modifier.padding(4.dp)
            )
            SecureTextField(
                state = rememberTextFieldState(),
                textObfuscationMode = TextObfuscationMode.Visible,
                modifier = Modifier.padding(4.dp),
                label = { Text("Visible Password Field") },
            )
        }
    }
}
