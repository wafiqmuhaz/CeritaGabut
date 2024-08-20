package com.example.ceritagabut.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ceritagabut.responses.ListResultItems

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(quote: List<ListResultItems>)
    @Query("SELECT * FROM items")
    fun getAllItem(): PagingSource<Int, ListResultItems>

    @Query("DELETE FROM items")
    suspend fun deleteAllItem()
}