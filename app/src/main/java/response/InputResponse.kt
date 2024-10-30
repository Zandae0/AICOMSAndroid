package com.example.aicomsapp.viewmodels.response
import com.google.gson.annotations.SerializedName

data class InputResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: InputData?
)

data class InputData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("inputDate") val inputDate: String,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("amount") val amount: Number,
    @SerializedName("source") val source: String,
    @SerializedName("pic") val pic: String,
    @SerializedName("photoUrl") val photoUrl: String,
    @SerializedName("photoSudah") val photoSudah: String,
    @SerializedName("transactionType") val transactionType: String,  // Ditambahkan
    @SerializedName("status") val status: String  // Ditambahkan
)
