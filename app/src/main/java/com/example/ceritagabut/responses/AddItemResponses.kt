package com.example.ceritagabut.responses

import com.google.gson.annotations.SerializedName

data class AddItemResponses(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    )