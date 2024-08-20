package com.example.ceritagabut.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ThemeModel (private val userRepos: ThemeSave) : ViewModel(){
    fun saveThemeSetting(darkModeState: Boolean) {
        viewModelScope.launch {
            userRepos.saveThemeSetting(darkModeState)
        }
    }
    val getAppTheme: Flow<Boolean> = userRepos.getThemeSettingApps()
}