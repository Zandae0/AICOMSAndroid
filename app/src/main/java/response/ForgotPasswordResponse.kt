package com.example.aicomsapp.viewmodels.response

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    @SerializedName("status") val status: String,        // The status of the request (e.g., "Success" or "Failed")
    @SerializedName("code") val code: String?,           // Error code, if applicable (e.g., "auth/invalid-email")
    @SerializedName("message") val message: String       // A message describing the result (e.g., "Password reset email sent successfully")
)
