package org.owasp.mastestapp

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

// SUMMARY: Demonstrates unauthorized access to sensitive medical records through an exported ContentProvider without permission enforcement.
// FAIL: [MASTG-TEST-0356] AppointmentProvider is exported without android:readPermission or android:writePermission, allowing any app on the device to query patient appointment records.

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0121")
        return try {
            context.openOrCreateDatabase(AppointmentDbHelper.DB_NAME, 0, null).close()
            r.add(
                Status.FAIL,
                "Appointment database initialized. Any app can read patient records via: content://org.owasp.mastestapp.appointments/appointments"
            )
            r.toJson()
        } catch (e: Exception) {
            r.add(Status.ERROR, "Initialization error: ${e.javaClass.simpleName}: ${e.message}")
            r.toJson()
        }
    }

    class AppointmentDbHelper(context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_APPOINTMENTS (
                  _id INTEGER PRIMARY KEY AUTOINCREMENT,
                  patient_name TEXT NOT NULL,
                  dob TEXT NOT NULL,
                  appointment_date TEXT NOT NULL,
                  doctor TEXT NOT NULL,
                  diagnosis TEXT,
                  notes TEXT
                )
                """.trimIndent()
            )
            insert(db, "Maria Garcia", "1988-03-14", "2026-04-10", "Dr. Chen",  "Type 2 Diabetes",        "Adjust metformin dosage to 1000mg twice daily")
            insert(db, "James Wilson", "1975-09-27", "2026-04-11", "Dr. Patel", "Essential Hypertension", "Refer to cardiology; increase lisinopril to 20mg")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
            onCreate(db)
        }

        private fun insert(
            db: SQLiteDatabase,
            name: String, dob: String, date: String,
            doctor: String, diagnosis: String, notes: String
        ) {
            val cv = ContentValues().apply {
                put("patient_name", name)
                put("dob", dob)
                put("appointment_date", date)
                put("doctor", doctor)
                put("diagnosis", diagnosis)
                put("notes", notes)
            }
            db.insert(TABLE_APPOINTMENTS, null, cv)
        }

        companion object {
            const val DB_NAME = "appointments.db"
            const val DB_VERSION = 1
            const val TABLE_APPOINTMENTS = "appointments"
        }
    }

    class AppointmentProvider : ContentProvider() {

        private lateinit var db: AppointmentDbHelper

        override fun onCreate(): Boolean {
            db = AppointmentDbHelper(requireNotNull(context))
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
                MATCH_APPOINTMENTS -> readableDb.query(
                    AppointmentDbHelper.TABLE_APPOINTMENTS,
                    null, null, null, null, null, "_id ASC"
                )
                MATCH_APPOINTMENT_BY_ID -> {
                    val id = ContentUris.parseId(uri)
                    readableDb.query(
                        AppointmentDbHelper.TABLE_APPOINTMENTS,
                        null, "_id=?", arrayOf(id.toString()), null, null, "_id ASC"
                    )
                }
                else -> null
            }
        }

        override fun getType(uri: Uri): String? = when (MATCHER.match(uri)) {
            MATCH_APPOINTMENTS -> "vnd.android.cursor.dir/vnd.mastestapp.appointment"
            MATCH_APPOINTMENT_BY_ID -> "vnd.android.cursor.item/vnd.mastestapp.appointment"
            else -> null
        }

        override fun insert(uri: Uri, values: ContentValues?): Uri? = null

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

        override fun update(
            uri: Uri, values: ContentValues?,
            selection: String?, selectionArgs: Array<out String>?
        ): Int = 0

        companion object {
            private const val AUTH = "org.owasp.mastestapp.appointments"
            private const val MATCH_APPOINTMENTS = 1
            private const val MATCH_APPOINTMENT_BY_ID = 2

            private val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
                addURI(AUTH, "appointments", MATCH_APPOINTMENTS)
                addURI(AUTH, "appointments/#", MATCH_APPOINTMENT_BY_ID)
            }
        }
    }
}
