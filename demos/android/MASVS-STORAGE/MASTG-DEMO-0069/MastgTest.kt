package org.owasp.mastestapp

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.OutputStream


object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        try {
            return UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            Log.e("MASTG-PROTO", "Cannot read proto: ${exception.message}")
            throw exception
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

private val Context.protoStore: DataStore<UserPreferences> by dataStore(
    fileName = "sensitive_datastore_proto.pb",
    serializer = UserPreferencesSerializer
)


private object PrefsKeys {
    val KEY1 = stringPreferencesKey("k1")
    val KEY2 = stringPreferencesKey("k2")
}

private val Context.prefsStore: DataStore<Preferences> by preferencesDataStore(
    name = "sensitive_datastore_prefs"
)

class MastgTest(private val context: Context) {


    private fun writeToProtoDataStore() {
        val userEmail = "john.doe@proto-demo.com"
        val accessToken = "ghp_1234567890abcdefghijklmnopqrstuvABCD"

        runBlocking {
            context.protoStore.updateData {
                UserPreferences.newBuilder()
                    .setName("John Doe")
                    .setEmail(userEmail)
                    .setAccessToken(accessToken)
                    .build()
            }
            Log.i("MASTG-PROTO", "Proto DataStore written: sensitive_datastore_proto.pb")
        }
    }


    private fun writeToPrefsDataStore() {
        val pii = "john.doe@proto-demo.com"
        val password = "s3cr3tp4ssw0rd"

        runBlocking {
            context.prefsStore.edit { prefs ->
                prefs[PrefsKeys.KEY1] = pii
                prefs[PrefsKeys.KEY2] = password
            }
            Log.i("MASTG-PREFS", "Preferences DataStore written: sensitive_datastore_prefs.preferences_datastore")
        }
    }

    fun mastgTest(): String {
        writeToProtoDataStore()
        writeToPrefsDataStore()
        return "DataStore Demo Complete. Proto + Preferences DataStore created with PII/Secrets in plaintext."
    }
}
