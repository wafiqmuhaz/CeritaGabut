package com.example.ceritagabut.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ceritagabut.responses.ListResultItems
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(
    entities = [ListResultItems::class, RemoteAppKeys::class],
    version = 3,
    exportSchema = false
)
abstract class ItemRoomDatabase : RoomDatabase() {
    abstract fun itemsDao(): ItemDao
    abstract fun remoteKeysDao(): RemoteAppKeysDao

    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getAppDatabase(context: Context): ItemRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}