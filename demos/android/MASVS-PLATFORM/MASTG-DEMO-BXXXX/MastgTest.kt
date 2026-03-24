package org.owasp.mastestapp

// SUMMARY: Demonstrates insecure exposure of sensitive stored data through an exported ContentProvider.
// FAIL: Exported providers allow external apps to query credential records from the app database.
// PASS: Providers should restrict access using manifest permissions or explicit caller validation.

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class MastgTest(private val context: android.content.Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x07")

        return try {
            context.openOrCreateDatabase(CredentialDbHelper.DB_NAME, 0, null).close()
            r.add(
                Status.FAIL,
                "Initialized sample data. The exported content provider allows external callers to read credential records via content://org.owasp.mastestapp.credentials/credentials"
            )
            r.toJson()
        } catch (e: Exception) {
            r.add(Status.ERROR, "Initialization error: ${e.javaClass.simpleName}: ${e.message}")
            r.toJson()
        }
    }

    class CredentialDbHelper(context: android.content.Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

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

        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor? {
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
}
