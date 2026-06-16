package org.owasp.mastestapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val MASTG_TEXT_TAG = "mastgTestText"

class MainActivity : ComponentActivity() {

    private val mastgTest by lazy { MastgTest(applicationContext) }
    private lateinit var appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val updateState = mutableStateOf(MastgTest.UpdateState.CHECKING)
    private val isUpdateCheckComplete = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appUpdateResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            handleUpdateFlowResult(result.resultCode)
        }

        mastgTest.registerInstallStateListener(appUpdateResultLauncher)

        mastgTest.onUpdateStateChanged = { state ->
            runOnUiThread {
                updateState.value = state
                handleUpdateStateChange(state)
            }
        }

        setContent {
            when {
                !isUpdateCheckComplete.value -> {
                    UpdateCheckingScreen()
                }
                updateState.value == MastgTest.UpdateState.UPDATE_REQUIRED ||
                updateState.value == MastgTest.UpdateState.UPDATE_IN_PROGRESS ||
                updateState.value == MastgTest.UpdateState.UPDATE_CANCELED ||
                updateState.value == MastgTest.UpdateState.UPDATE_FAILED -> {
                    UpdateRequiredScreen(updateState.value)
                }
                else -> {
                    MainScreen(
                        displayString = "App is running. No mandatory updates are required.",
                        onStartClick = {
                            mastgTest.checkForUpdate(appUpdateResultLauncher)
                        }
                    )
                }
            }
        }
        mastgTest.checkForUpdate(appUpdateResultLauncher)
    }

    private fun handleUpdateFlowResult(resultCode: Int) {
        when (resultCode) {
            RESULT_OK -> {
                Log.d("MainActivity", "Update accepted. The update is now in progress.")
                updateState.value = MastgTest.UpdateState.UPDATE_IN_PROGRESS
            }
            RESULT_CANCELED -> {
                // User pressed back or X button on update dialog
                Log.w("MainActivity", "Update was CANCELED by user (back/X). Re-enforcing mandatory update.")
                updateState.value = MastgTest.UpdateState.UPDATE_CANCELED
                // Immediately re-check and enforce
                mastgTest.checkForUpdate(appUpdateResultLauncher)
            }
            else -> {
                Log.e("MainActivity", "Update flow failed with result code: $resultCode. Re-enforcing.")
                updateState.value = MastgTest.UpdateState.UPDATE_FAILED
                mastgTest.checkForUpdate(appUpdateResultLauncher)
            }
        }
    }

    private fun handleUpdateStateChange(state: MastgTest.UpdateState) {
        Log.d("MainActivity", "Update state changed to: $state")

        when (state) {
            MastgTest.UpdateState.NO_UPDATE_AVAILABLE,
            MastgTest.UpdateState.UPDATE_INSTALLED -> {
                isUpdateCheckComplete.value = true
            }
            MastgTest.UpdateState.CHECKING -> {
                isUpdateCheckComplete.value = false
            }
            MastgTest.UpdateState.UPDATE_REQUIRED,
            MastgTest.UpdateState.UPDATE_IN_PROGRESS,
            MastgTest.UpdateState.UPDATE_CANCELED,
            MastgTest.UpdateState.UPDATE_FAILED -> {
                isUpdateCheckComplete.value = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume: Enforcing mandatory update check...")
        mastgTest.enforceUpdateOnResume(appUpdateResultLauncher)
    }

    override fun onDestroy() {
        super.onDestroy()
        mastgTest.unregisterInstallStateListener()
        Log.d("MainActivity", "onDestroy: InstallStateUpdatedListener unregistered.")
    }
}

@Composable
fun MainScreen(
    displayString: String,
    onStartClick: () -> Unit = {}
) {
    BaseScreen(onStartClick = onStartClick) {
        Text(
            modifier = Modifier
                .padding(16.dp)
                .testTag(MASTG_TEXT_TAG),
            text = displayString,
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(displayString = "App is running.")
}

@Preview
@Composable
fun UpdateCheckingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = Color.Black)
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .testTag(MASTG_TEXT_TAG),
                text = "Checking for mandatory updates...",
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UpdateRequiredScreen(state: MastgTest.UpdateState) {
    val message = when (state) {
        MastgTest.UpdateState.UPDATE_REQUIRED -> "A mandatory update is required.\nPlease install to continue."
        MastgTest.UpdateState.UPDATE_IN_PROGRESS -> "Update in progress...\nPlease wait."
        MastgTest.UpdateState.UPDATE_CANCELED -> "Update is required.\nRestarting update flow..."
        MastgTest.UpdateState.UPDATE_FAILED -> "Update failed.\nRetrying..."
        else -> "Update required."
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state == MastgTest.UpdateState.UPDATE_IN_PROGRESS ||
                state == MastgTest.UpdateState.UPDATE_CANCELED ||
                state == MastgTest.UpdateState.UPDATE_FAILED) {
                CircularProgressIndicator(color = Color.Black)
            }
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .testTag(MASTG_TEXT_TAG),
                text = message,
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun UpdateRequiredScreenPreview() {
    UpdateRequiredScreen(MastgTest.UpdateState.UPDATE_REQUIRED)
}