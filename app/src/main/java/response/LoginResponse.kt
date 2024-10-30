package com.example.hical.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: LoginResult? = null
)

data class LoginResult(

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("refreshToken")
    val refreshToken: String? = null,


    @field:SerializedName("expirationTime")
    val expirationTime: Long? = null,

    @field:SerializedName("userStatus")
    val userStatus: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("role")
    val role: String? = null
// Assuming userStatus is an integer representing status
)