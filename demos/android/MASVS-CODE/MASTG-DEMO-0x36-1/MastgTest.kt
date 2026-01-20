package org.owasp.mastestapp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class MastgTest(context: Context) {

    private val appUpdateManager: FakeAppUpdateManager = FakeAppUpdateManager(context).apply {
        setUpdateAvailable(2)
    }
    private var installStateListener: InstallStateUpdatedListener? = null
    private var pendingUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>? = null
    private val handler = Handler(Looper.getMainLooper())
    private var updateDelayRunnable: Runnable? = null

    // Callback to notify MainActivity of update state changes
    var onUpdateStateChanged: ((UpdateState) -> Unit)? = null

    enum class UpdateState {
        CHECKING,
        UPDATE_REQUIRED,
        UPDATE_IN_PROGRESS,
        UPDATE_CANCELED,
        UPDATE_FAILED,
        NO_UPDATE_AVAILABLE,
        UPDATE_INSTALLED
    }

    fun registerInstallStateListener(
        appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        pendingUpdateLauncher = appUpdateResultLauncher

        installStateListener = InstallStateUpdatedListener { state ->
            handleInstallState(state, appUpdateResultLauncher)
        }
        appUpdateManager.registerListener(installStateListener!!)
        Log.d("MastgTest", "InstallStateUpdatedListener registered.")
    }

    fun unregisterInstallStateListener() {
        installStateListener?.let {
            appUpdateManager.unregisterListener(it)
            Log.d("MastgTest", "InstallStateUpdatedListener unregistered.")
        }
        installStateListener = null
        pendingUpdateLauncher = null
    }

    /**
     * Handles install state changes from the Play Core library.
     */
    @Suppress("DEPRECATION")
    private fun handleInstallState(
        state: InstallState,
        appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                Log.d("MastgTest", "Update downloading: ${state.bytesDownloaded()}/${state.totalBytesToDownload()}")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_IN_PROGRESS)
            }
            InstallStatus.DOWNLOADED -> {
                // For IMMEDIATE updates, this shouldn't happen as they auto-install
                // But handle it just in case
                Log.d("MastgTest", "Update downloaded, completing installation...")
                appUpdateManager.completeUpdate()
            }
            InstallStatus.INSTALLING -> {
                Log.d("MastgTest", "Update installing...")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_IN_PROGRESS)
            }
            InstallStatus.INSTALLED -> {
                Log.d("MastgTest", "Update installed successfully.")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_INSTALLED)
            }
            InstallStatus.CANCELED -> {
                Log.w("MastgTest", "Update was CANCELED by user. Re-triggering mandatory update.")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_CANCELED)
                // Immediately re-check and enforce update
                checkForUpdate(appUpdateResultLauncher)
            }
            InstallStatus.FAILED -> {
                Log.e("MastgTest", "Update FAILED. Re-triggering mandatory update.")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_FAILED)
                // Immediately re-check and enforce update
                checkForUpdate(appUpdateResultLauncher)
            }
            InstallStatus.PENDING -> {
                Log.d("MastgTest", "Update pending...")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_IN_PROGRESS)
            }
            InstallStatus.UNKNOWN -> {
                Log.d("MastgTest", "Update status unknown.")
            }
            InstallStatus.REQUIRES_UI_INTENT -> {
                Log.d("MastgTest", "Update requires UI intent. Re-triggering update flow.")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_REQUIRED)
                checkForUpdate(appUpdateResultLauncher)
            }
        }
    }

    /**
     * Checks if an IMMEDIATE update is available on the Play Store.
     */
    fun checkForUpdate(
        appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        Log.d("MastgTest", "Checking for an update...")
        onUpdateStateChanged?.invoke(UpdateState.CHECKING)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            handleUpdateAvailability(appUpdateInfo, appUpdateResultLauncher)
        }.addOnFailureListener { e ->
            Log.e("MastgTest", "Failed to check for updates.", e)
            onUpdateStateChanged?.invoke(UpdateState.NO_UPDATE_AVAILABLE)
        }
    }

    private fun handleUpdateAvailability(
        appUpdateInfo: AppUpdateInfo,
        appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val updateAvailability = appUpdateInfo.updateAvailability()
        val isImmediateUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

        Log.d("MastgTest", "Update availability: $updateAvailability, Immediate allowed: $isImmediateUpdateAllowed")

        when (updateAvailability) {
            UpdateAvailability.UPDATE_AVAILABLE -> {
                if (isImmediateUpdateAllowed) {
                    Log.d("MastgTest", "Immediate update available. Starting flow.")
                    onUpdateStateChanged?.invoke(UpdateState.UPDATE_REQUIRED)
                    startUpdateFlow(appUpdateInfo, appUpdateResultLauncher)
                } else {
                    Log.d("MastgTest", "Update available but IMMEDIATE not allowed.")
                    onUpdateStateChanged?.invoke(UpdateState.NO_UPDATE_AVAILABLE)
                }
            }
            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                Log.d("MastgTest", "Update already in progress. Resuming flow.")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_IN_PROGRESS)
                startUpdateFlow(appUpdateInfo, appUpdateResultLauncher)
            }
            UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                Log.d("MastgTest", "No update available.")
                onUpdateStateChanged?.invoke(UpdateState.NO_UPDATE_AVAILABLE)
            }
            UpdateAvailability.UNKNOWN -> {
                Log.d("MastgTest", "Update availability unknown.")
                onUpdateStateChanged?.invoke(UpdateState.NO_UPDATE_AVAILABLE)
            }
        }
    }

    private fun startUpdateFlow(
        appUpdateInfo: AppUpdateInfo,
        appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val started = appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            appUpdateResultLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
        if (started) {
            // Show update required initially
            Log.d("MastgTest", "Mandatory updates are required to install. Waiting 10 seconds...")
            onUpdateStateChanged?.invoke(UpdateState.UPDATE_REQUIRED)

            // After 10 seconds, simulate the update flow using FakeAppUpdateManager
            updateDelayRunnable = Runnable {
                Log.d("MastgTest", "Starting update installation...")
                onUpdateStateChanged?.invoke(UpdateState.UPDATE_IN_PROGRESS)

                appUpdateManager.apply {
                    userAcceptsUpdate()
                    downloadStarts()
                    downloadCompletes()
                    completeUpdate()
                    installCompletes()
                }

                // After FakeAppUpdateManager completes, show no update required
                handler.postDelayed({
                    Log.d("MastgTest", "App is running. No mandatory updates required.")
                    onUpdateStateChanged?.invoke(UpdateState.NO_UPDATE_AVAILABLE)
                }, 1000)
            }
            handler.postDelayed(updateDelayRunnable!!, 30000) // 30 seconds delay
        } else {
            Log.e("MastgTest", "Failed to start update flow.")
            onUpdateStateChanged?.invoke(UpdateState.UPDATE_FAILED)
        }
    }

    fun enforceUpdateOnResume(
        appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        Log.d("MastgTest", "onResume: Checking for pending mandatory updates...")

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val updateAvailability = appUpdateInfo.updateAvailability()
            val isImmediateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            Log.d("MastgTest", "onResume check - Availability: $updateAvailability, Immediate allowed: $isImmediateAllowed")

            when (updateAvailability) {
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    // Update was started but user dismissed/backgrounded during download
                    Log.d("MastgTest", "onResume: Resuming in-progress update.")
                    onUpdateStateChanged?.invoke(UpdateState.UPDATE_IN_PROGRESS)
                    startUpdateFlow(appUpdateInfo, appUpdateResultLauncher)
                }
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    // CRITICAL FIX: User dismissed BEFORE download started
                    // This is the bypass scenario we're preventing
                    if (isImmediateAllowed) {
                        Log.w("MastgTest", "onResume: Update still available but not started. Re-enforcing mandatory update.")
                        onUpdateStateChanged?.invoke(UpdateState.UPDATE_REQUIRED)
                        startUpdateFlow(appUpdateInfo, appUpdateResultLauncher)
                    }
                }
                UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                    Log.d("MastgTest", "onResume: No update required.")
                    onUpdateStateChanged?.invoke(UpdateState.NO_UPDATE_AVAILABLE)
                }
                UpdateAvailability.UNKNOWN -> {
                    Log.d("MastgTest", "onResume: Update availability unknown, checking again...")
                    // Re-check to be safe
                    checkForUpdate(appUpdateResultLauncher)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("MastgTest", "onResume: Failed to check update status.", e)
        }
    }

    @Deprecated("Use enforceUpdateOnResume() for comprehensive bypass prevention",
        ReplaceWith("enforceUpdateOnResume(appUpdateResultLauncher)"))
    fun resumeUpdateIfInProgress(
        appUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        enforceUpdateOnResume(appUpdateResultLauncher)
    }
}