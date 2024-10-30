package com.example.aicomsapp.viewmodels.dashboardadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aicomsapp.repository.RetrofitRepository

class DashboardAdminViewModelFactory(private val retrofitRepository: RetrofitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardAdminViewModel::class.java)) {
            return DashboardAdminViewModel(retrofitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}