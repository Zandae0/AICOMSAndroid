package com.example.aicomsapp.viewmodels.dashboardadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.repository.RetrofitRepository
import com.example.aicomsapp.viewmodels.response.ImprestFund
import kotlinx.coroutines.launch

class DashboardAdminViewModel(private val repository: RetrofitRepository) : ViewModel() {

    fun getImprestFunds(onSuccess: (List<ImprestFund>) -> Unit, onError: (String) -> Unit) {
        // Launch a coroutine to call the suspend function
        viewModelScope.launch {
            try {
                // Assuming getImprestFunds is a suspend function
                val imprestFunds = repository.getImprestFunds()
                onSuccess(imprestFunds)  // Pass the result to the success callback
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")  // Pass the error message
            }
        }
    }
}
