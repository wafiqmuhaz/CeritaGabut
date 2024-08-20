package com.example.ceritagabut.theme

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ThemeFactory(private val themeSave: ThemeSave) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ThemeFactory? = null

        fun getInstance(context: Context, themeSave: ThemeSave): ThemeFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeFactory(themeSave).also { INSTANCE = it }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ThemeModel::class.java) ->
                ThemeModel(themeSave) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
}
