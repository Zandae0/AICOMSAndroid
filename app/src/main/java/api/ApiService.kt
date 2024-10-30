package com.example.aicomsapp.api

import android.telecom.Call
import com.example.aicomsapp.viewmodels.response.ForgotPasswordResponse
import com.example.aicomsapp.viewmodels.response.ImprestFund
import com.example.aicomsapp.viewmodels.response.InputResponse
import com.example.aicomsapp.viewmodels.response.LabCash
import com.example.aicomsapp.viewmodels.response.LabCashResponse
import com.example.aicomsapp.viewmodels.response.UserApiResponse
import com.example.aicomsapp.viewmodels.response.UserProfileResponse
import com.example.hical.response.LoginResponse
import com.google.firebase.firestore.auth.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("/auth/signup")
    suspend fun signup(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: String,
        @Field("status") status: Number,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("/auth/signin")
    suspend fun signin(
        @Field("identifier") identifier: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("/users")
    suspend fun users(): UserApiResponse

    @FormUrlEncoded
    @PATCH("/users/update-status")
    suspend fun updateUserStatus(
        @Field("uidganti") uid: String
    ): UserApiResponse

        @Multipart
        @POST("/imprest")
        suspend fun createImprestFund(
        @Part("name") name: RequestBody,
        @Part("inputDate") inputDate: RequestBody,
        @Part("purpose") purpose: RequestBody,
        @Part("transactionDate") transactionDate: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("source") source: RequestBody,
        @Part("pic") pic: RequestBody,
        @Part("transactionType") transactionType: RequestBody,
        @Part("status") status: RequestBody,
        @Part photo: MultipartBody.Part
    ): InputResponse

        @Multipart
        @PUT("/imprest/{id}")
        suspend fun updateImprestFund(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("inputDate") inputDate: RequestBody,
        @Part("purpose") purpose: RequestBody,
        @Part("transactionDate") transactionDate: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("source") source: RequestBody,
        @Part("pic") pic: RequestBody,
        @Part("transactionType") transactionType: RequestBody,
        @Part("status") status: RequestBody,
        @Part photo: MultipartBody.Part?,
        @Part photoSudah: MultipartBody.Part?
    ):InputResponse
        @Multipart
        @POST("/lab-cash")
        suspend fun createLabCash(
        @Part("name") name: RequestBody,
        @Part("inputDate") inputDate: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("source") source: RequestBody,
        @Part("transactionType") transactionType: RequestBody,
        @Part photo: MultipartBody.Part
    ): LabCashResponse
    @Multipart
    @PUT("/lab-cash/{id}")
    suspend fun updateLabCash(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("inputDate") inputDate: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("source") source: RequestBody,
        @Part("transactionType") transactionType: RequestBody,
        @Part photo: MultipartBody.Part?,
        @Part photoSudah: MultipartBody.Part?
    ): LabCashResponse
        @FormUrlEncoded
        @POST("/auth/forgot-password") // Assuming this is the correct endpoint
        suspend fun forgotPassword(
            @Field("email") email: String
        ): ForgotPasswordResponse

        @GET("/imprest")
        suspend fun getImprestFunds(): List<ImprestFund>

        @GET("/lab-cash")
        suspend fun getLabCash(): List<LabCash>

    @FormUrlEncoded
    @PATCH("users/change-password")
    suspend fun changePassword(
        @Field("uid") uid: String,
        @Field("currentPassword") currentPassword: String,
        @Field("newPassword") newPassword: String
    ): UserProfileResponse
}


