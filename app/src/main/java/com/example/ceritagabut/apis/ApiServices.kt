package com.example.ceritagabut.apis

import com.example.ceritagabut.responses.AddItemResponses
import com.example.ceritagabut.responses.AppSignInResponses
import com.example.ceritagabut.responses.AppSignUpResponses
import com.example.ceritagabut.responses.ItemResponses
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiServices {
    @GET("v1/stories")
    suspend fun getListItems(
        @Header("Authorization") bearer: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        ): ItemResponses

    @FormUrlEncoded
    @POST("/v1/login")
    fun userSignin(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<AppSignInResponses>

    @FormUrlEncoded
    @POST("/v1/register")
    fun userSignup(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<AppSignUpResponses>

    @Multipart
    @POST("/v1/stories")
    fun postNewItems(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<AddItemResponses>

    @GET("/v1/stories?location=1")
    fun getListMapsItems(
        @Header("Authorization") bearer: String?
    ): Call<ItemResponses>
}