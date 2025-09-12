package org.owasp.mastestapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        return """
            This app is vulnerable to SQLI in content provider.

            Test on adb shell with:
            # content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob' OR '1'='1'"
        """.trimIndent()
    }

    //  Vulnerable ContentProvider with path-based SQL injection
    class StudentProvider : ContentProvider() {

        companion object {
            const val AUTHORITY = "org.owasp.mastestapp.provider"
            const val STUDENTS = 1
            const val STUDENT_ID = 2
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
                addURI(AUTHORITY, "students", STUDENTS)
                addURI(AUTHORITY, "students/#", STUDENT_ID)
            }
        }

        private lateinit var dbHelper: DatabaseHelper

        override fun onCreate(): Boolean {
            dbHelper = DatabaseHelper(context!!)
            return true
        }

        override fun query(
            uri: Uri,
            projection: Array<String>?,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String?
        ): Cursor? {
            val db = dbHelper.readableDatabase
            val qb = SQLiteQueryBuilder()
            qb.tables = "students"

            when (uriMatcher.match(uri)) {
                STUDENTS -> {
                    // No filtering — all rows
                }
                STUDENT_ID -> {
                    // Vulnerable: unvalidated input from path used in query
                    val id = uri.getPathSegments().get(1)
                    qb.appendWhere("id=" + id)
                    Log.e("SQLI", "Injected ID segment: $id")
                }
                else -> throw IllegalArgumentException("Unknown URI: $uri")
            }

            val cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }

        override fun getType(uri: Uri): String? = null
        override fun insert(uri: Uri, values: ContentValues?): Uri? = null
        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
        override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
    }

    //  DB helper for student data
    class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, "students.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY, name TEXT)")
            db.execSQL("INSERT INTO students (name) VALUES ('Alice')")
            db.execSQL("INSERT INTO students (name) VALUES ('Bob')")
            db.execSQL("INSERT INTO students (name) VALUES ('Charlie')")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS students")
            onCreate(db)
        }
    }
}
