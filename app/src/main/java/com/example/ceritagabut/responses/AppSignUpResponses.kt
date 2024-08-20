package com.example.ceritagabut.responses

import com.google.gson.annotations.SerializedName

data class AppSignUpResponses(

    @field:SerializedName("error")
    val appError: Boolean,

    @field:SerializedName("message")
    val appMessage: String,
)