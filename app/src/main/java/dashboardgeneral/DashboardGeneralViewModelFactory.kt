package com.example.aicomsapp.viewmodels.dashboardgeneral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aicomsapp.repository.RetrofitRepository

class DashboardGeneralViewModelFactory(private val retrofitRepository: RetrofitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardGeneralViewModel::class.java)) {
            return DashboardGeneralViewModel(retrofitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}