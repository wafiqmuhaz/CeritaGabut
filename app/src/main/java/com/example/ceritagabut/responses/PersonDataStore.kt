package com.example.ceritagabut.responses

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonDatastore(val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Person")

    private val APP_NAME_KEY = stringPreferencesKey("name")
    private val APP_USERID_KEY = stringPreferencesKey("userId")
    private val APP_TOKEN_KEY = stringPreferencesKey("token")

    suspend fun savePerson(name: String, id: String, token: String) {
        context.dataStore.edit { pref ->
            pref[APP_NAME_KEY] = name
            pref[APP_USERID_KEY] = id
            pref[APP_TOKEN_KEY] = token
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var APP_INSTANCE: PersonDatastore? = null

        fun getInstances(context: Context): PersonDatastore {
            return APP_INSTANCE ?: synchronized(this) {
                val instance = PersonDatastore(context.applicationContext)
                APP_INSTANCE = instance
                instance
            }
        }
    }

    fun getPerson(): Flow<AppSignInResult> {
        return context.dataStore.data.map { pref ->
            AppSignInResult(
                pref[APP_NAME_KEY] ?: "",
                pref[APP_USERID_KEY] ?: "",
                pref[APP_TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun signoutApp() {
        context.dataStore.edit { pref ->
            pref[APP_NAME_KEY] = ""
            pref[APP_USERID_KEY] = ""
            pref[APP_TOKEN_KEY] = ""
        }
    }
}
