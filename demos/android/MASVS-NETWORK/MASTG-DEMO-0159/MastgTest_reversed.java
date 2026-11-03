package org.owasp.mastestapp;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;

public final class MastgTest {
    private final Context context;
    private final String TAG = "MastgTest";

    public MastgTest(Context context) {
        this.context = context;
    }

    public final void mastgTest() {
        insecureNetworkCall();
        secureNetworkCall();
    }

    public final void insecureNetworkCall() {
        try {
            URLConnection openConnection = new URL("https://example.com").openConnection();
            if (openConnection != null) {
                ((HttpsURLConnection) openConnection).connect();
                Log.d("MastgTest", "Insecure connection established.");
                return;
            }
            throw new NullPointerException("null cannot be cast to non-null type javax.net.ssl.HttpsURLConnection");
        } catch (Exception e) {
            Log.e("MastgTest", "Connection failed: " + e.getMessage());
        }
    }

    public final boolean secureNetworkCall() {
        try {
            ProviderInstaller.installIfNeeded(this.context);
            Log.d("MastgTest", "Security Provider successfully updated.");
            URLConnection openConnection = new URL("https://example.com").openConnection();
            if (openConnection != null) {
                ((HttpsURLConnection) openConnection).connect();
                Log.d("MastgTest", "Secure connection established.");
                return true;
            }
            throw new NullPointerException("null cannot be cast to non-null type javax.net.ssl.HttpsURLConnection");
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().showErrorNotification(this.context, e.getConnectionStatusCode());
            Log.e("MastgTest", "Play Services needs repair, prompting user.");
            return false;
        } catch (GooglePlayServicesNotAvailableException e2) {
            Log.e("MastgTest", "Play Services not available: " + e2.getMessage());
            return false;
        } catch (Exception e3) {
            Log.e("MastgTest", "Network connection failed: " + e3.getMessage());
            return false;
        }
    }
}
