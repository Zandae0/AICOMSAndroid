package com.example.aicomsapp.viewmodels.response

data class UserApiResponse(
    val status: String,         // Menggunakan String untuk "status"
    val data: List<UserResponse>  // Menggunakan List<UserResponse> untuk "data"
)

data class UserResponse(
    val status: Int,
    val name: String,
    val uid: String,
    val username: String,
    val email: String,
    val role: String
)
