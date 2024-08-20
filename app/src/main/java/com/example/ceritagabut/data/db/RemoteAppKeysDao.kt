package com.example.ceritagabut.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteAppKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<RemoteAppKeys>)
    @Query("DELETE FROM remote_app_keys")
    fun deleteRemoteKeys()
    @Query("SELECT * FROM remote_app_keys WHERE id = :id")
    fun getRemoteKeysId(id: String): RemoteAppKeys?
}