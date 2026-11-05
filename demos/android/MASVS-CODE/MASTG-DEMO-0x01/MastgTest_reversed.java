package org.owasp.mastestapp;

import android.content.Context;
import android.os.Build;
import androidx.security.state.SecurityPatchState;
import androidx.security.state.SecurityStateManager;

public final class MastgTest {
    private final Context context;

    public MastgTest(Context context) {
        this.context = context;
    }

    public final String mastgTest() {
        SecurityStateManager manager = new SecurityStateManager(this.context);
        manager.getGlobalSecurityState();
        SecurityPatchState state = new SecurityPatchState(this.context);
        boolean updated = state.isDeviceFullyUpdated();
        return "SPL: " + Build.VERSION.SECURITY_PATCH + ", fully updated: " + updated;
    }
}
