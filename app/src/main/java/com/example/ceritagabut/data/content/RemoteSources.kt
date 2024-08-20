package com.example.ceritagabut.data

import com.example.ceritagabut.apis.ApiConfigs
import com.example.ceritagabut.responses.AddItemResponses
import com.example.ceritagabut.responses.AppSignInResponses
import com.example.ceritagabut.responses.AppSignUpResponses
import com.example.ceritagabut.responses.ItemResponses
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteSources {
    var responseitemcode = ""

    fun signinPerson(callback: SigninAppCallback, email: String, password: String) {
        callback.onSigninApp(AppSignInResponses(null, true, ""))
        val personSignIn = ApiConfigs.getApiServices().userSignin(email, password)
        personSignIn.enqueue(object : Callback<AppSignInResponses> {
            override fun onResponse(
                call: Call<AppSignInResponses>,
                response: Response<AppSignInResponses>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSigninApp(it) }
                } else {
                    handleErrorResponse(response.code())
                    callback.onSigninApp(AppSignInResponses(null, true, responseitemcode))
                    callback.onSigninFailed(AppSignInResponses(null, true, responseitemcode))
                }
            }

            override fun onFailure(call: Call<AppSignInResponses>, t: Throwable) {
                callback.onSigninApp(AppSignInResponses(null, true, t.message.toString()))
                callback.onSigninFailed(AppSignInResponses(null, true, responseitemcode))
            }
        })
    }

    fun signupPerson(callback: SignupAppCallback, name: String, email: String, password: String) {
        val signupinfo = AppSignUpResponses(true, "")
        callback.onSignupApp(signupinfo)
        val personSignUp = ApiConfigs.getApiServices().userSignup(name, email, password)
        personSignUp.enqueue(object : Callback<AppSignUpResponses> {
            override fun onResponse(
                call: Call<AppSignUpResponses>,
                response: Response<AppSignUpResponses>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSignupApp(it) }
                    responseitemcode = if (response.code() == 201) "201" else "400"
                    callback.onSignupApp(AppSignUpResponses(true, responseitemcode))
                } else {
                    handleErrorResponse(response.code())
                    callback.onSignupApp(AppSignUpResponses(true, responseitemcode))
                }
            }

            override fun onFailure(call: Call<AppSignUpResponses>, t: Throwable) {
                callback.onSignupApp(AppSignUpResponses(true, t.message.toString()))
            }
        })
    }

    fun postNewItems(
        callback: AddNewItemAppCallback,
        tokenUser: String,
        imageFileUser: File,
        descUser: String,
        lonUser: String? = null,
        latUser: String? = null
    ) {
        callback.addItems(AddItemResponses(true, ""))
        val descriptionUser = descUser.toRequestBody("text/plain".toMediaType())
        val latitudeUser = latUser?.toRequestBody("text/plain".toMediaType())
        val longitudeUser = lonUser?.toRequestBody("text/plain".toMediaType())
        val requestImageFileUser = imageFileUser.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipartUser =
            MultipartBody.Part.createFormData("photo", imageFileUser.name, requestImageFileUser)
        val PersonItems = ApiConfigs.getApiServices()
            .postNewItems("Bearer $tokenUser", imageMultipartUser, descriptionUser, latitudeUser!!, longitudeUser!!)
        PersonItems.enqueue(object : Callback<AddItemResponses> {
            override fun onResponse(
                call: Call<AddItemResponses>,
                response: Response<AddItemResponses>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback.addItems(responseBody)
                    } else {
                        callback.addItems(AddItemResponses(true, "Gagal upload file"))
                    }
                } else {
                    callback.addItems(AddItemResponses(true, "Gagal upload file"))
                }
            }

            override fun onFailure(call: Call<AddItemResponses>, t: Throwable) {
                callback.addItems(AddItemResponses(true, "Gagal upload file"))
            }
        })
    }

fun getListMapsItems(callback: GetListMapsItemAppCallback, token: String) {
    try {
        val apiService = ApiConfigs.getApiServices()
        val request = apiService.getListMapsItems("Bearer $token")
        request.enqueue(object : Callback<ItemResponses> {
            override fun onResponse(call: Call<ItemResponses>, response: Response<ItemResponses>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onMapsItem(it) }
                } else {
                    val errorResponse = ItemResponses(emptyList(), true, "Load Failed!")
                    callback.onMapsItem(errorResponse)
                }
            }

            override fun onFailure(call: Call<ItemResponses>, t: Throwable) {
                val errorResponse = ItemResponses(emptyList(), true, t.message.toString())
                callback.onMapsItem(errorResponse)
            }
        })
    } catch (e: Exception) {
        val errorResponse = ItemResponses(emptyList(), true, "Exception occurred: ${e.message}")
        callback.onMapsItem(errorResponse)
    }
}


    private fun handleErrorResponse(code: Int) {
        responseitemcode = when (code) {
            200 -> "200"
            400 -> "400"
            401 -> "401"
            else -> "ERROR $code"
        }
    }

    interface SigninAppCallback {
        fun onSigninApp(signinResponse: AppSignInResponses)
        fun onSigninFailed(errorMsg: AppSignInResponses)
    }

    interface SignupAppCallback {
        fun onSignupApp(signupResponse: AppSignUpResponses)
        fun onSignupFailed(errorMessage: AppSignUpResponses)
    }

    interface GetListMapsItemAppCallback {
        fun onMapsItem(itemResultResponses: ItemResponses)
        fun onMapsItemLoadFailed(message: ItemResponses)
    }

    interface AddNewItemAppCallback {
        fun addItems(addItemResponses: AddItemResponses)
        fun onAddItemError(error: AddItemResponses)
    }


    companion object {
        @Volatile
        private var appInstance: RemoteSources? = null

        fun getAppInstances(): RemoteSources =
            appInstance ?: synchronized(this) {
                appInstance ?: RemoteSources()
            }
    }
}