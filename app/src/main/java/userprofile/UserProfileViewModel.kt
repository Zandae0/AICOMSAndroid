package com.example.aicomsapp.viewmodels.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.repository.RetrofitRepository
import com.example.aicomsapp.viewmodels.response.UserProfileResponse
import kotlinx.coroutines.launch

// UserProfileViewModel.kt
class UserProfileViewModel(private val repository: RetrofitRepository) : ViewModel() {

    private val _changePasswordResult = MutableLiveData<String>()
    val changePasswordResult: LiveData<String> = _changePasswordResult

    fun changePassword(uid: String, currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                // Menggunakan hasil dari repository tanpa memeriksa isSuccessful
                val response: UserProfileResponse = repository.changePassword(uid, currentPassword, newPassword)

                // Cek apakah status dari response bernilai sukses
                if (response.status == "success") {
                    _changePasswordResult.value = response.message ?: "Password updated successfully"
                } else {
                    _changePasswordResult.value = response.message ?: "Error: Unknown error occurred"
                }
            } catch (e: Exception) {
                // Tangani jika ada exception
                _changePasswordResult.value = "Failed to update password: ${e.message}"
            }
        }
    }
}