package com.example.aicomsapp.viewmodels.resetpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.api.ApiService
import com.example.aicomsapp.viewmodels.response.ForgotPasswordResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ForgotPasswordViewModel(private val apiService: ApiService) : ViewModel() {

    fun sendForgotPasswordEmail(email: String, onSuccess: (ForgotPasswordResponse) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.forgotPassword(email)  // No need to call .isSuccessful or .body()
                onSuccess(response)  // Directly return the response
            } catch (e: HttpException) {
                onError("HttpException: ${e.message}")
            } catch (e: IOException) {
                onError("IOException: ${e.message}")
            }
        }
    }
}
