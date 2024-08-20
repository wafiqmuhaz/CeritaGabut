package com.example.ceritagabut.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ceritagabut.data.ItemRepository
import java.io.File

class AppMainViewModel(private val repo: ItemRepository) : ViewModel() {

    val Lat = MutableLiveData(0.0)
    val Long = MutableLiveData(0.0)

    fun getListItems(token: String) =  repo.getAllItems(token)
    fun getListMapsItems(token: String) = repo.getListMapsItems(token)
    fun postNewItems(token: String, imageFile: File, desc: String, lon: String?, lat: String?) = repo.postNewItems(token, imageFile, desc, lon, lat)
}
