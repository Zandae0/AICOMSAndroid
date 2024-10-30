package com.example.aicomsapp.viewmodels.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.repository.RetrofitRepository
import com.example.aicomsapp.viewmodels.UserModel
import com.example.aicomsapp.viewmodels.detailpageimprest.ImprestFund
import com.example.aicomsapp.viewmodels.response.ErrorResponse
import com.example.hical.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val retrofitRepository : RetrofitRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            retrofitRepository.saveSession(user)
        }
    }

    fun login(
        identifier: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = retrofitRepository.login(identifier, password)
                Log.d("LoginViewModel", "Raw Login Response: ${Gson().toJson(response)}")

                if (response.status == "Success") {
                    response.data?.let { loginResult ->
                        val token = loginResult.token
                        if (token != null) {
                            val user = UserModel(identifier, token, true)
                            saveSession(user)
                            onSuccess(response)
                        } else {
                            onError("Token is null")
                        }
                    } ?: run {
                        onError("Login result is null")
                    }
                } else {
                    onError(response.message ?: "Login failed with unknown error")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception: ${e.message}")
                handleException(e, onError)
            }
        }
    }

    private fun handleException(e: Exception, onError: (String) -> Unit) {
        if (e is HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: "Unknown error occurred"
            onError(errorMessage)
        } else {
            onError(e.message ?: "An unexpected error occurred")
        }
    }
}