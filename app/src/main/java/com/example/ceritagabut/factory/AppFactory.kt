package com.example.ceritagabut.factory

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ceritagabut.data.ItemRepository
import com.example.ceritagabut.models.AppMainViewModel
import com.example.ceritagabut.models.AppSignUpViewModel
import com.example.ceritagabut.models.AppSigninViewModel
import com.example.ceritagabut.utils.DependencyInjection

class AppFactory(private val pref: ItemRepository) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AppSigninViewModel::class.java -> {
                val viewModel = AppSigninViewModel(pref) as T
                logViewModelCreation(modelClass)
                viewModel
            }

            AppSignUpViewModel::class.java -> {
                val viewModel = AppSignUpViewModel(pref) as T
                logViewModelCreation(modelClass)
                viewModel
            }

            AppMainViewModel::class.java -> {
                val viewModel = AppMainViewModel(pref) as T
                logViewModelCreation(modelClass)
                viewModel
            }

            else -> {
                throw IllegalArgumentException("Unknown : ${modelClass.name}")
            }
        }
    }

    companion object {
        @Volatile
        private var appInstances: AppFactory? = null
        fun getInstance(context: Context): AppFactory =
            appInstances ?: synchronized(this) {
                appInstances ?: AppFactory(DependencyInjection.provideRepository(context))
            }.also { appInstances = it }

        private fun logViewModelCreation(modelClass: Class<*>) {
            Log.d("ViewModelCreation", "Created ViewModel: ${modelClass.simpleName}")
        }
    }

}

