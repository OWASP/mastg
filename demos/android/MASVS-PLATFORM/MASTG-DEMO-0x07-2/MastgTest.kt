package org.owasp.mastestapp

// SUMMARY: Demonstrates secure protection of sensitive stored data through exported ContentProviders.
// PASS: [MASTG-TEST-0x07-2] Exported providers enforce manifest permissions, explicit caller signature validation, and canonical-path checks before returning sensitive data.

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Binder
import android.os.ParcelFileDescriptor
import java.io.File
import kotlin.io.path.writeText

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x07IP_SECURE")

        try {
            val secretFile = File(context.filesDir, "secret.txt")
            secretFile.toPath().writeText("TOP_SECRET_TOKEN=tok_live_12345\nPIN=9876\n")

            context.openOrCreateDatabase(CredentialDbHelper.DB_NAME, 0, null).close()

            r.add(
                Status.PASS,
                "Initialized sample data. Exported ContentProviders are protected with signature-level permissions, provider-level permission enforcement, runtime signature verification, and canonical-path validation."
            )
        } catch (e: Exception) {
            r.add(Status.ERROR, "Initialization error: ${e.javaClass.simpleName}: ${e.message}")
        }

        return r.toJson()
    }

    class CredentialDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_CREDENTIALS (
                  _id INTEGER PRIMARY KEY AUTOINCREMENT,
                  username TEXT NOT NULL,
                  password TEXT NOT NULL,
                  note TEXT
                )
                """.trimIndent()
            )

            insert(db, "admin", "StrongPwd!2026", "prod account")
            insert(db, "test", "test1234", "dev account")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CREDENTIALS")
            onCreate(db)
        }

        private fun insert(db: SQLiteDatabase, user: String, pass: String, note: String) {
            val cv = ContentValues().apply {
                put("username", user)
                put("password", pass)
                put("note", note)
            }
            db.insert(TABLE_CREDENTIALS, null, cv)
        }

        companion object {
            const val DB_NAME = "creds.db"
            const val DB_VERSION = 1
            const val TABLE_CREDENTIALS = "credentials"
        }
    }

    class CredentialProvider : ContentProvider() {

        private lateinit var db: CredentialDbHelper

        override fun onCreate(): Boolean {
            db = CredentialDbHelper(requireNotNull(context))
            return true
        }

        private fun enforceSameSignatureCaller() {
            val ctx = requireNotNull(context)
            val pm = ctx.packageManager
            val myPkg = ctx.packageName
            val callerUid = Binder.getCallingUid()

            if (callerUid == android.os.Process.myUid()) return

            val callerPkgs = pm.getPackagesForUid(callerUid) ?: emptyArray()
            val match = callerPkgs.any { callerPkg ->
                pm.checkSignatures(myPkg, callerPkg) == PackageManager.SIGNATURE_MATCH
            }

            if (!match) {
                throw SecurityException("Caller not signed with same certificate")
            }
        }

        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor? {
            enforceSameSignatureCaller()

            val readableDb = db.readableDatabase
            return when (MATCHER.match(uri)) {
                MATCH_CREDENTIALS -> readableDb.query(
                    CredentialDbHelper.TABLE_CREDENTIALS,
                    arrayOf("_id", "username", "password", "note"),
                    null,
                    null,
                    null,
                    null,
                    "_id ASC"
                )

                MATCH_CREDENTIAL_BY_ID -> {
                    val id = ContentUris.parseId(uri)
                    readableDb.query(
                        CredentialDbHelper.TABLE_CREDENTIALS,
                        arrayOf("_id", "username", "password", "note"),
                        "_id=?",
                        arrayOf(id.toString()),
                        null,
                        null,
                        "_id ASC"
                    )
                }

                else -> null
            }
        }

        override fun getType(uri: Uri): String? = when (MATCHER.match(uri)) {
            MATCH_CREDENTIALS -> "vnd.android.cursor.dir/vnd.mastestapp.credential"
            MATCH_CREDENTIAL_BY_ID -> "vnd.android.cursor.item/vnd.mastestapp.credential"
            else -> null
        }

        override fun insert(uri: Uri, values: ContentValues?): Uri? = null

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

        override fun update(
            uri: Uri,
            values: ContentValues?,
            selection: String?,
            selectionArgs: Array<out String>?
        ): Int = 0

        companion object {
            private const val AUTH = "org.owasp.mastestapp.credentials"
            private const val MATCH_CREDENTIALS = 1
            private const val MATCH_CREDENTIAL_BY_ID = 2

            private val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
                addURI(AUTH, "credentials", MATCH_CREDENTIALS)
                addURI(AUTH, "credentials/#", MATCH_CREDENTIAL_BY_ID)
            }
        }
    }

    class FileLeakProvider : ContentProvider() {

        override fun onCreate(): Boolean = true

        private fun enforceSameSignatureCaller() {
            val ctx = requireNotNull(context)
            val pm = ctx.packageManager
            val myPkg = ctx.packageName
            val callerUid = Binder.getCallingUid()

            if (callerUid == android.os.Process.myUid()) return

            val callerPkgs = pm.getPackagesForUid(callerUid) ?: emptyArray()
            val match = callerPkgs.any { callerPkg ->
                pm.checkSignatures(myPkg, callerPkg) == PackageManager.SIGNATURE_MATCH
            }

            if (!match) {
                throw SecurityException("Caller not signed with same certificate")
            }
        }

        override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
            enforceSameSignatureCaller()

            val ctx = requireNotNull(context)
            val base = ctx.filesDir.canonicalFile
            val filename = uri.lastPathSegment ?: return null

            if (filename != "secret.txt") {
                throw SecurityException("Access denied")
            }

            if (filename.contains('/') || filename.contains('\\')) {
                throw SecurityException("Invalid filename")
            }

            val target = File(base, filename).canonicalFile
            if (!target.path.startsWith(base.path + File.separator)) {
                throw SecurityException("Path traversal blocked")
            }

            return ParcelFileDescriptor.open(target, ParcelFileDescriptor.MODE_READ_ONLY)
        }

        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor? = null

        override fun getType(uri: Uri): String? = "application/octet-stream"

        override fun insert(uri: Uri, values: ContentValues?) = null

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

        override fun update(
            uri: Uri,
            values: ContentValues?,
            selection: String?,
            selectionArgs: Array<out String>?
        ) = 0
    }
}