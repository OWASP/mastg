package org.owasp.mastestapp

import android.content.Context
import android.os.Build
import androidx.security.state.SecurityPatchState
import androidx.security.state.SecurityStateManager

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val manager = SecurityStateManager(context)
        manager.getGlobalSecurityState()
        val state = SecurityPatchState(context)
        val updated = state.isDeviceFullyUpdated()
        return "SPL: ${Build.VERSION.SECURITY_PATCH}, fully updated: $updated"
    }
}
