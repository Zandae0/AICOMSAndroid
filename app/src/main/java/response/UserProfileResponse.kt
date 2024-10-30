package com.example.aicomsapp.viewmodels.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)
