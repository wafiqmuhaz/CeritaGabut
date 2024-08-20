package com.example.ceritagabut.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "items")
data class ListResultItems(
    @field:SerializedName("photoUrl")
    val userPhotoUrl: String,

    @field:SerializedName("createdAt")
    val userCreatedAt: String,

    @field:SerializedName("name")
    val username: String,

    @field:SerializedName("description")
    val itemDesc: String,

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lon")
    val longitude: Double,

    @field:SerializedName("lat")
    val latitude: Double
)

data class ItemResponses(

    @field:SerializedName("listStory")
    val listItems: List<ListResultItems>,

    @field:SerializedName("error")
    val appError: Boolean,

    @field:SerializedName("message")
    val appMessage: String
)
