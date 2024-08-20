package com.example.ceritagabut.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "remote_app_keys")
data class RemoteAppKeys(
    @PrimaryKey val id: String,
    val nextAppKey: Int?,
    val prevAppKey: Int?,
)