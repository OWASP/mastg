import android.content.Context
import com.google.gson.Gson
import java.io.File

data class User(val username: String, val password: String)

class DataManager(private val context: Context) {
    private val gson = Gson()

    fun saveUserData(user: User) {
        val json = gson.toJson(user)
        val file = File(context.filesDir, "user_data.json")
        file.writeText(json)
    }

    fun loadUserData(): User? {
        val file = File(context.filesDir, "user_data.json")
        return if (file.exists()) {
            val json = file.readText()
            gson.fromJson(json, User::class.java)
        } else {
            null
        }
    }
}