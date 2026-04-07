package org.owasp.mastestapp

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class LibraryLoaderActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MastgTest.REQUEST_LIBRARY && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                loadLibraryFromUri(uri)
            }
        }
    }

    private fun loadLibraryFromUri(uri: Uri) {
        try {

            val fileName = getFileNameFromUri(uri) ?: "downloaded_lib.so"


            val libDir = File(applicationInfo.nativeLibraryDir)
            val targetFile = File(libDir, fileName) 

            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("LIB-LOADER", "Library copied to: ${targetFile.absolutePath}")


            System.load(targetFile.absolutePath)
            Log.d("LIB-LOADER", "Library loaded successfully")

        } catch (e: Exception) {
            Log.e("LIB-LOADER", "Failed to load library: ${e.message}")
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null

        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex("_display_name")
                if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}
