package com.example.aicomsapp.viewmodels.resetpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aicomsapp.api.ApiService

class ForgotPasswordViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgotPasswordViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
