package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

// SUMMARY: This sample demonstrates the risk of processing unsanitized data from implicit intent results.
class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x04")

        try {
            val intent = Intent(context, VulnerableActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            r.add(Status.FAIL, "Launched VulnerableActivity to demonstrate intent response handling")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        return r.toJson()
    }
}

class VulnerableActivity : Activity() {

    companion object {
        private const val REQUEST_CODE_GET_CONTENT = 1337
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val privateDir = File(filesDir, "private").apply { mkdirs() }
        File(filesDir, "public").mkdirs()
        File(privateDir, "secret.txt").writeText("Original Secret Content")

        val intent = Intent("org.owasp.mastestapp.REQUEST_FILE")
        try {
            startActivityForResult(intent, REQUEST_CODE_GET_CONTENT)
        } catch (e: android.content.ActivityNotFoundException) {
            val tv = TextView(this)
            tv.textSize = 18f
            tv.setPadding(32, 32, 32, 32)
            tv.text = "No handler found for REQUEST_FILE.\nInstall the attacker app from MASTG-DEMO-0141 first."
            setContentView(tv)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GET_CONTENT && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                var fileName = "temp_file"

                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            // FAIL: [MASTG-TEST-0375] Blindly trusting the filename provided by an external app
                            fileName = it.getString(nameIndex)
                        }
                    }
                }

                val publicDir = File(filesDir, "public")
                // FAIL: [MASTG-TEST-0375] Writing to internal storage using the unsanitized filename
                val outFile = File(publicDir, fileName)
                val content = contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() } ?: "(empty)"
                FileOutputStream(outFile).use { it.write(content.toByteArray()) }

                val tv = TextView(this)
                tv.textSize = 18f
                tv.setPadding(32, 32, 32, 32)
                tv.text = "Filename: $fileName\nResolved path: ${outFile.canonicalPath}"
                setContentView(tv)
            }
        }
    }
}
