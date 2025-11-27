package org.owasp.mastestapp

import android.content.Context
import android.util.Log
import androidx.room.*
import java.io.File

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val token: String
)

@Dao
interface UserDao {
    @Insert
    fun insert(user: UserEntity)

    @Query("DELETE FROM users")
    fun clear()

    @Query("SELECT * FROM users")
    fun getAll(): List<UserEntity>
}

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        fun getInstance(ctx: Context): AppDatabase {
            val dbPath = ctx.getDatabasePath("PrivateUnencryptedRoomDB")
            Log.i("MASTG-ROOM", "Database absolute path: ${dbPath.absolutePath}")

            // Wymuś utworzenie katalogu databases jeśli nie istnieje
            dbPath.parentFile?.let {
                if (!it.exists()) {
                    Log.i("MASTG-ROOM", "Creating database directory: ${it.absolutePath}")
                    it.mkdirs()
                } else {
                    Log.i("MASTG-ROOM", "Database directory exists: ${it.absolutePath}")
                }
            }

            return Room.databaseBuilder(
                ctx,
                AppDatabase::class.java,
                "PrivateUnencryptedRoomDB"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}

class MastgTest(private val context: Context) {

    private fun writeSensitiveDataToUnencryptedRoom() {
        val username = "demoUser_room"
        val userEmail = "john.doe@maswe.com"
        val accessToken = "ghp_1234567890abcdefghijklmnopqrstuvABCD"

        try {
            val db = AppDatabase.getInstance(context)
            val dao = db.userDao()

            Log.i("MASTG-ROOM", "Clearing existing users...")
            dao.clear()

            Log.i("MASTG-ROOM", "Inserting new user...")
            dao.insert(
                UserEntity(
                    username = username,
                    email = userEmail,
                    token = accessToken
                )
            )

            val allUsers = dao.getAll()
            Log.i("MASTG-ROOM", "Users in DB after insert: ${allUsers.size}")
            allUsers.forEach {
                Log.i("MASTG-ROOM", "User: ${it.username}, ${it.email}, ${it.token}")
            }

            Log.i("MASTG-ROOM", "Room DB written successfully: PrivateUnencryptedRoomDB")

        } catch (e: Exception) {
            Log.e("MASTG-ROOM", "Error writing Room DB: ${e.message}")
        }
    }

    fun mastgTest(): String {
        Log.i("MASTG-ROOM", "Starting MastgTest RoomDB demo...")
        writeSensitiveDataToUnencryptedRoom()
        val dbPath = context.getDatabasePath("PrivateUnencryptedRoomDB")
        Log.i("MASTG-ROOM", "Database final path: ${dbPath.absolutePath}")
        return "RoomDB Demo Complete. Plaintext PII + Token stored in unencrypted Room database."
    }
}
