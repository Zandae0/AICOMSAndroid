package com.example.aicomsapp.viewmodels

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
