package com.example.aicomsapp.viewmodels.usercontrol

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aicomsapp.api.ApiConfig
import com.example.aicomsapp.viewmodels.response.UserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    private val _users = MutableLiveData<List<UserResponse>>()
    val users: LiveData<List<UserResponse>> get() = _users

    fun fetchUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.users()

                if (response.status == "Success") {
                    withContext(Dispatchers.Main) {
                        _users.value = response.data  // Ambil data dari respons API
                    }
                } else {
                    Log.e("UserViewModel", "API Error: ${response.status}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("UserViewModel", "Error fetching users: ${e.message}")
            }
        }
    }
    fun updateUserStatus(uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.updateUserStatus(uid)

                if (response.status == "Success") {
                    // Optionally, refresh the users after updating
                    fetchUsers()
                } else {
                    Log.e("UserViewModel", "API Error: ${response.status}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("UserViewModel", "Error updating user status: ${e.message}")
            }
        }
    }

}