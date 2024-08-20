package com.example.ceritagabut.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferences private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val THEME_APP_KEY = booleanPreferencesKey("theme_setting")

        @Volatile
        private var APP_INSTANCE: ThemePreferences? = null

        fun getAppInstances(dataStore: DataStore<Preferences>): ThemePreferences {
            return APP_INSTANCE ?: synchronized(this) {
                APP_INSTANCE ?: ThemePreferences(dataStore).also { APP_INSTANCE = it }
            }
        }
    }

    suspend fun saveThemeSettingApps(darkModeState: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_APP_KEY] = darkModeState
        }
    }

    fun getThemeSettingApps(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_APP_KEY] ?: false
        }
    }
}