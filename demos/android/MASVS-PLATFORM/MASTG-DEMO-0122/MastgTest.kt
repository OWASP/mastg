package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.FileProvider
import java.io.File

// SUMMARY: Demonstrates oversharing via FileProvider misconfiguration where filepaths.xml uses path="." exposing the entire internal files directory.
// FAIL: [MASTG-TEST-0357] FileProvider is configured with path="." in filepaths.xml, which exposes all files in filesDir — not just the intended reports subdirectory.

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0122")
        return try {
            File(context.filesDir, "session_token.txt")
                .writeText("sess_7f3a9b1e4d2c8f0a5e6b3c1d9f4a2e7b")
            File(context.filesDir, "reports").mkdirs()
            File(context.filesDir, "reports/lab_result_3829.pdf")
                .writeText("%PDF-1.4 stub: cholesterol 195, HbA1c 6.8")

            r.add(
                Status.FAIL,
                "FileProvider path=\".\" exposes all of filesDir. " +
                        "Intended: content://org.owasp.mastestapp.fileprovider/app_files/reports/lab_result_3829.pdf — " +
                        "but session_token.txt is also reachable via the same authority."
            )
            r.toJson()
        } catch (e: Exception) {
            r.add(Status.ERROR, "${e.javaClass.simpleName}: ${e.message}")
            r.toJson()
        }
    }

    // INTENTIONALLY VULNERABLE: exported activity that wraps attacker-controlled
    // filenames into FileProvider URIs and grants read access back to the caller.
    class ShareReportActivity : Activity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val requested = intent.getStringExtra("file_name") ?: run {
                setResult(RESULT_CANCELED); finish(); return
            }

            val file = File(filesDir, requested)
            val uri = FileProvider.getUriForFile(
                this,
                "org.owasp.mastestapp.fileprovider",
                file
            )

            val result = Intent().apply {
                data = uri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            setResult(RESULT_OK, result)
            finish()
        }
    }
}
