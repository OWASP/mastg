package sg.vp.owasp_mobile.OMTG_Android
import android.content.Context
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MastgTest {
    private val TAG = "MastgTest"

    // insecure: not updating the GMS security provider
    fun insecureNetworkCall() {
        try {
            val url = URL("https://example.com")
            val connection = url.openConnection() as HttpsURLConnection
            connection.connect()
            Log.d(TAG, "Insecure connection established.")
        } catch (e: Exception) {
            Log.e(TAG, "Connection failed: ${e.message}")
        }
    }

    // secure: properly updating provider before network call
    fun secureNetworkCall(context: Context) {
        try {
            ProviderInstaller.installIfNeeded(context)
            Log.d(TAG, "Security Provider successfully updated.")
            
            val url = URL("https://example.com")
            val connection = url.openConnection() as HttpsURLConnection
            connection.connect()
            Log.d(TAG, "Secure connection established.")

        } catch (e: GooglePlayServicesRepairableException) {
            // prompt user to repair/update play services
            Log.e(TAG, "Play Services needs repair: ${e.message}")
        } catch (e: GooglePlayServicesNotAvailableException) {
            // play services not available
            Log.e(TAG, "Play Services not available: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Network connection failed: ${e.message}")
        }
    }
}
