package com.example.ceritagabut.data

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.ceritagabut.apis.ApiServices
import com.example.ceritagabut.responses.AddItemResponses
import com.example.ceritagabut.responses.AppSignInResponses
import com.example.ceritagabut.responses.AppSignInResult
import com.example.ceritagabut.responses.AppSignUpResponses
import com.example.ceritagabut.responses.ItemResponses
import com.example.ceritagabut.responses.ListResultItems
import com.example.ceritagabut.responses.PersonDatastore
import java.io.File

class ItemRepository(
    private val apiServices: ApiServices,
    private val personDatastore: PersonDatastore,
    private val remoteSources: RemoteSources
) : DatailtemFunction {

    override fun getPerson(): LiveData<AppSignInResult> {
        val resultLiveData = MutableLiveData<AppSignInResult>()
        val user = personDatastore.getPerson().asLiveData()
        val processedResult = processUser()
        resultLiveData.postValue(processedResult)
        return user
    }

    private fun processUser(): AppSignInResult {
        return AppSignInResult("", "", "")
    }

    suspend fun appSignout() = personDatastore.signoutApp()

    override fun signinPerson(email: String, password: String): LiveData<AppSignInResponses> {
        val signinPersonLiveData = MutableLiveData<AppSignInResponses>()
        val callback = object : RemoteSources.SigninAppCallback {
            override fun onSigninApp(signinResponse: AppSignInResponses) {
                signinPersonLiveData.postValue(signinResponse)
            }

            override fun onSigninFailed(errorMsg: AppSignInResponses) {
                signinPersonLiveData.postValue(errorMsg)
            }
        }

        Log.e("callback signinPerson", "${callback}")
        remoteSources.signinPerson(
            object : RemoteSources.SigninAppCallback {
                override fun onSigninApp(signinResponse: AppSignInResponses) {
                    signinPersonLiveData.postValue(signinResponse)
                }

                override fun onSigninFailed(errorMsg: AppSignInResponses) {
                    signinPersonLiveData.postValue(errorMsg)
                }
            },
            email, password,
        )
        return signinPersonLiveData
    }

    override fun signupPerson(
        name: String,
        email: String,
        password: String
    ): LiveData<AppSignUpResponses> {
        val registerPersonLiveData = MutableLiveData<AppSignUpResponses>()
        try {
            val callback = object : RemoteSources.SignupAppCallback {
                override fun onSignupApp(signupResponse: AppSignUpResponses) {
                    registerPersonLiveData.postValue(signupResponse)
                }

                override fun onSignupFailed(errorMessage: AppSignUpResponses) {
                    registerPersonLiveData.postValue(errorMessage)
                }

            }
            Log.e("callback signupPerson", "${callback.toString()}")
            remoteSources.signupPerson(object : RemoteSources.SignupAppCallback {
                override fun onSignupApp(signupResponse: AppSignUpResponses) {
                    registerPersonLiveData.postValue(signupResponse)
                }

                override fun onSignupFailed(errorMessage: AppSignUpResponses) {
                    registerPersonLiveData.postValue(errorMessage)
                }
            }, name, email, password)
        } catch (e: Exception) {
            val err = false
            Log.e("SignupError", "Gagal mendaftarkan pengguna: ${e.message}")
            registerPersonLiveData.postValue(
                AppSignUpResponses(
                    err,
                    "Gagal mendaftarkan pengguna"
                )
            )
        }
        return registerPersonLiveData
    }

    override fun getAllItems(token: String): LiveData<PagingData<ListResultItems>> {
        val pageSize = 10
        val prefetchDistance = 5

        val config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = false,
            initialLoadSize = pageSize * 3
        )
        val pagingSourceFactory = {
            ItemPagingSource(
                apiServicePagingApp = apiServices,
                dataStoreRepositoryApp = personDatastore
            )
        }
        val pager = Pager(
            config = config,
            pagingSourceFactory = pagingSourceFactory
        )
        return pager.liveData
    }

    @SuppressLint("SuspiciousIndentation")
    override fun postNewItems(
        token: String,
        imageFile: File,
        desc: String,
        lon: String?,
        lat: String?
    ): LiveData<AddItemResponses> {
        val uploadItems = MutableLiveData<AddItemResponses>()
        try {
            val callback = object : RemoteSources.AddNewItemAppCallback {
                override fun addItems(addItemResponses: AddItemResponses) {
                    uploadItems.postValue(addItemResponses)
                }

                override fun onAddItemError(error: AddItemResponses) {
                    uploadItems.postValue(error)
                }
            }
            Log.e("callback", "callback: ${callback}")
            remoteSources.postNewItems(object : RemoteSources.AddNewItemAppCallback {
                override fun addItems(addItemResponses: AddItemResponses) {
                    uploadItems.postValue(addItemResponses)
                }

                override fun onAddItemError(error: AddItemResponses) {
                    uploadItems.postValue(error)
                }
            }, token, imageFile, desc, lon, lat)
        } catch (e: Exception) {
            val Err = false
            Log.e("PostNewItemError", "Gagal mengunggah item baru: ${e.message}")
            uploadItems.postValue(AddItemResponses(Err, "Gagal mengunggah item baru"))
        }
        return uploadItems
    }

    suspend fun savePerson(userName: String?, userId: String?, userToken: String?): Boolean {
        if (userName.isNullOrEmpty() || userId.isNullOrEmpty() || userToken.isNullOrEmpty()) {
            Log.e("SavePersonError", "Gagal menyimpan data pengguna: Parameter tidak valid")
            return false
        }
        try {
            personDatastore.savePerson(userName, userId, userToken)
            return true
        } catch (e: Exception) {
            Log.e("SavePersonError", "Gagal menyimpan data pengguna: ${e.message}")
            return false
        }
    }

    override fun getListMapsItems(token: String): LiveData<ItemResponses> {
        val itemResponses = MutableLiveData<ItemResponses>()
        val callback = object : RemoteSources.GetListMapsItemAppCallback {
            override fun onMapsItem(itemResultResponses: ItemResponses) {
                if (itemResultResponses != null) {
                    itemResponses.postValue(itemResultResponses)
                } else {
                    Log.e(
                        "GetListMapsItemsError",
                        "Gagal mendapatkan respon item dari remote source: Respon kosong"
                    )
                }
            }

            override fun onMapsItemLoadFailed(message: ItemResponses) {
                itemResponses.postValue(message)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            if (!token.isNullOrEmpty()) {
                remoteSources.getListMapsItems(callback, token)
            } else {
                Log.e(
                    "GetListMapsItemsError",
                    "Gagal mendapatkan daftar item peta dari remote source: Token tidak valid"
                )
            }
        }, 1000)
        return itemResponses
    }

}

interface DatailtemFunction {
    fun getPerson(): LiveData<AppSignInResult>
    fun signinPerson(email: String, password: String): LiveData<AppSignInResponses>
    fun signupPerson(name: String, email: String, password: String): LiveData<AppSignUpResponses>
    fun postNewItems(
        token: String,
        imageFile: File,
        desc: String,
        lon: String?,
        lat: String?
    ): LiveData<AddItemResponses>

    fun getAllItems(token: String): LiveData<PagingData<ListResultItems>>
    fun getListMapsItems(token: String): LiveData<ItemResponses>

    companion object {
        @Volatile
        private var appInstance: ItemRepository? = null

        fun getAppInstances(
            apiService: ApiServices,
            personDatastore: PersonDatastore,
            remoteSources: RemoteSources
        ): ItemRepository {
            return appInstance ?: synchronized(this) {
                appInstance ?: buildItemRepository(apiService, personDatastore, remoteSources)
            }
        }

        private fun buildItemRepository(
            apiService: ApiServices,
            personDatastore: PersonDatastore,
            remoteSources: RemoteSources
        ): ItemRepository {
            val newItemRepository = ItemRepository(apiService, personDatastore, remoteSources)
            appInstance = newItemRepository
            return newItemRepository
        }
    }
}