package com.example.aicomsapp.viewmodels.response

import com.google.gson.annotations.SerializedName

data class LabCashResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: LabCashData?
)

data class LabCashData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("inputDate") val inputDate: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("source") val source: String,
    @SerializedName("photoUrl") val photoUrl: String
)
