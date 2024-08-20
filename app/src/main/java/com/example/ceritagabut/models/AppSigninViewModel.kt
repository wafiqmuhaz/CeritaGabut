package com.example.ceritagabut.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ceritagabut.data.ItemRepository
import kotlinx.coroutines.launch


class AppSigninViewModel(private val repo: ItemRepository) : ViewModel() {

    fun signinPerson(email: String, password: String) = repo.signinPerson(email, password)

    fun getPerson() = repo.getPerson()

    fun savePerson(userName: String, userId: String, userToken: String) {
        viewModelScope.launch { repo.savePerson(userName, userId, userToken) }
    }

    fun signoutPerson() {
        viewModelScope.launch { repo.appSignout() }
    }
}