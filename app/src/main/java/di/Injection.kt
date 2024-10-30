package com.example.aicomsapp.di

import android.content.Context
import com.example.aicomsapp.api.ApiConfig
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.preference.dataStore
import com.example.aicomsapp.repository.RetrofitRepository

object Injection {

    fun provideRetrofitRepository(context: Context): RetrofitRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return RetrofitRepository(apiService, userPreference)
    }
}
