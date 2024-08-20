package com.example.ceritagabut.responses

import com.google.gson.annotations.SerializedName

data class AppSignInResult(

    @field:SerializedName("name")
    val username: String,

    @field:SerializedName("userId")
    val userItemId: String,

    @field:SerializedName("token")
    val usertoken: String
)
data class AppSignInResponses(

    @field:SerializedName("loginResult")
    val userLoginResult: AppSignInResult?,

    @field:SerializedName("error")
    val appError: Boolean,

    @field:SerializedName("message")
    val appMessage: String,
)
