package com.example.ceritagabut.models

import androidx.lifecycle.ViewModel
import com.example.ceritagabut.data.ItemRepository


class AppSignUpViewModel(private val repo: ItemRepository) : ViewModel() {

    fun signup(name: String, email: String, password: String) =
        repo.signupPerson(name, email, password)
}