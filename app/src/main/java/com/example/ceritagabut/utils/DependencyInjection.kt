package com.example.ceritagabut.utils

import android.content.Context
import com.example.ceritagabut.apis.ApiConfigs
import com.example.ceritagabut.data.DatailtemFunction
import com.example.ceritagabut.data.ItemRepository
import com.example.ceritagabut.data.RemoteSources
import com.example.ceritagabut.data.db.ItemRoomDatabase
import com.example.ceritagabut.responses.PersonDatastore

object DependencyInjection {

    fun provideRepository(context: Context): ItemRepository {
        val remoteDataSource = RemoteSources.getAppInstances()
        val apiService = ApiConfigs.getApiServices()
        ItemRoomDatabase.getAppDatabase(context)
        val PersonDataStore = PersonDatastore.getInstances(context)
        return DatailtemFunction.getAppInstances(apiService, PersonDataStore, remoteDataSource)
    }

}