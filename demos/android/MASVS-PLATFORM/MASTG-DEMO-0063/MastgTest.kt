package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.compose.runtime.Composable

class MastgTest (private val context: Context){

    var shouldRunInMainThread: Boolean = true

    fun mastgTest(): String {
        if (context is Activity) {

            // Compose dialog with SecureFlagPolicy.SecureOn
            val composeHost = ComposeView(context)
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            composeHost.setContent {
                SecureComposeDialog(
                    onDismiss = {
                        val parent = composeHost.parent as? ViewGroup
                        parent?.removeView(composeHost)
                    }
                )
            }
            context.addContentView(composeHost, lp)

            return "SUCCESS!!\n\nThe Compose dialog should be visible.\n\nIt has SecureFlagPolicy.SecureOff set, so it should appear in screenshots or the recent apps view."
        } else {
            return "ERROR: Context is not an Activity"
        }

    }
}

@Composable
private fun SecureComposeDialog(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(securePolicy = SecureFlagPolicy.SecureOff)
    ) {
        MaterialTheme {
            Surface {
                Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
                    Text("This is a Compose dialog.")
                    Text("Secure policy is set to SecureOff.")
                    Button(onClick = onDismiss, modifier = androidx.compose.ui.Modifier.padding(top = 12.dp)) {
                        Text("OK")
                    }
                }
            }
        }
    }
}