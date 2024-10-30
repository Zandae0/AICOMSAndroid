package com.example.aicomsapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.repository.RetrofitRepository // Ensure this matches the actual package of RetrofitRepository
import com.example.aicomsapp.viewmodels.labcash.LabCashViewModel
import com.example.aicomsapp.viewmodels.signin.LoginViewModel
import com.example.aicomsapp.viewmodels.userprofile.UserProfileViewModel
import main.MainViewModel
import signup.RegisterViewModel

class ViewModelFactory(
    private val context: Context,
    private val retrofitRepository: RetrofitRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(retrofitRepository, userPreference) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(retrofitRepository, userPreference) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(retrofitRepository) as T
            }
            modelClass.isAssignableFrom(UserProfileViewModel::class.java) -> {
                UserProfileViewModel(retrofitRepository) as T // Add this line for UserProfileViewModel
            }
            modelClass.isAssignableFrom(LabCashViewModel::class.java) -> {
                LabCashViewModel(retrofitRepository) as T // Add this line for UserProfileViewModel
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(context: Context, retrofitRepository: RetrofitRepository, userPreference: UserPreference): ViewModelFactory {
            return ViewModelFactory(context, retrofitRepository, userPreference)
        }
    }
}
