package com.example.ceritagabut.theme

import kotlinx.coroutines.flow.Flow

class ThemeSave private constructor(
    private val userAppPreferences: ThemePreferences
) {

    companion object {

        @Volatile
        private var INSTANCE: ThemeSave? = null

        fun getUserInstances(
            themePreferences: ThemePreferences
        ): ThemeSave {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeSave(themePreferences).also { INSTANCE = it }
            }
        }
    }


    fun getThemeSettingApps(): Flow<Boolean> = userAppPreferences.getThemeSettingApps()

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        userAppPreferences.saveThemeSettingApps(isDarkModeActive)
    }

    private suspend fun <T> apiCall(call: suspend () -> T): T {
        try {
            return call()
        } catch (e: Exception) {
            throw Exception("HttpException: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }

}
