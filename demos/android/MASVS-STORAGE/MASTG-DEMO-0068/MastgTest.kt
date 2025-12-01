package org.owasp.mastestapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class MastgTest (private val context: Context){


    fun writeSensitiveDataToUnencryptedSQLite() {
        val username = "demoUser_sqlite"
        val userEmail = "john.doe@maswe-0006.com"
        val accessToken = "ghp_1234567890abcdefghijklmnopqrstuvABCD"
        val dbName = "PrivateUnencryptedData"

        try {
            val db: SQLiteDatabase = context.openOrCreateDatabase(
                dbName,
                Context.MODE_PRIVATE,
                null
            )

            db.execSQL("CREATE TABLE IF NOT EXISTS Users(Username VARCHAR, Email VARCHAR, Token VARCHAR);")


            db.execSQL("DELETE FROM Users;")

            db.execSQL("INSERT INTO Users VALUES('$username', '$userEmail', '$accessToken');")

            db.close()
            Log.i("MASTG-SQLITE", "Data written to unencrypted database: $dbName. Ready for ADB extraction.")

        } catch (e: Exception) {
            Log.e("MASTG-SQLITE", "Error writing data to SQLite: ${e.message}")
        }
    }

    fun mastgTest(): String {
        writeSensitiveDataToUnencryptedSQLite()
        return "SQLite Demo Complete. Database 'PrivateUnencryptedData' created with PII and Token."
    }
}
