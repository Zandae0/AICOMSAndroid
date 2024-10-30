package com.example.aicomsapp.repository

import com.example.aicomsapp.api.ApiService
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.viewmodels.UserModel
import com.example.aicomsapp.viewmodels.response.ImprestFund
import com.example.hical.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import response.RegisterResponse
import com.example.aicomsapp.viewmodels.response.InputResponse
import com.example.aicomsapp.viewmodels.response.LabCash
import com.example.aicomsapp.viewmodels.response.LabCashResponse
import com.example.aicomsapp.viewmodels.response.UserProfileResponse
import com.google.android.gms.common.api.Response

class RetrofitRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun register(name: String, username: String, email: String, password: String, role: String, status: Number): RegisterResponse {
        return apiService.signup(name, username, email, password, role, status)
    }

    suspend fun login(identifier: String, password: String): LoginResponse {
        return apiService.signin(identifier, password)
    }

    suspend fun getImprestFunds(): List<ImprestFund> {
        return apiService.getImprestFunds()
    }

    suspend fun getLabCash(): List<LabCash> {
        return apiService.getLabCash()
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    // Tambahkan fungsi untuk upload data
    suspend fun createImprestFund(
        name: RequestBody,
        inputDate: RequestBody,
        purpose: RequestBody,
        transactionDate: RequestBody,
        amount: RequestBody,
        source: RequestBody,
        pic: RequestBody,
        transactionType: RequestBody,
        status: RequestBody,
        photo: MultipartBody.Part
    ): InputResponse {
        return apiService.createImprestFund(
            name, inputDate, purpose, transactionDate, amount, source, pic, transactionType, status, photo
        )
    }
    suspend fun createLabCash(
        name: RequestBody,
        inputDate: RequestBody,
        amount: RequestBody,
        source: RequestBody,
        transactionType: RequestBody,
        photo: MultipartBody.Part
    ): LabCashResponse {
        return apiService.createLabCash(name, inputDate, amount, source, transactionType, photo)
    }

    companion object {
        @Volatile
        private var instance: RetrofitRepository? = null

        // Ensure a singleton instance of RetrofitRepository
        fun getInstance(apiService: ApiService, userPreference: UserPreference): RetrofitRepository {
            return instance ?: synchronized(this) {
                instance ?: RetrofitRepository(apiService, userPreference).also { instance = it }
            }
        }
    }
    suspend fun changePassword(uid: String, currentPassword: String, newPassword: String): UserProfileResponse{
        return apiService.changePassword(uid, currentPassword, newPassword)
    }
    suspend fun updateImprestFund(
        id: String,
        name: RequestBody,
        inputDate: RequestBody,
        purpose: RequestBody,
        transactionDate: RequestBody,
        amount: RequestBody,
        source: RequestBody,
        pic: RequestBody,
        transactionType: RequestBody,
        status: RequestBody,
        photo: MultipartBody.Part?,
        photoSudah: MultipartBody.Part?
    ): InputResponse {
        return apiService.updateImprestFund(
            id, name, inputDate, purpose, transactionDate, amount, source, pic, transactionType, status, photo, photoSudah
        )
    }
    suspend fun updateLabCash(
        id: String,
        name: RequestBody,
        inputDate: RequestBody,
        amount: RequestBody,
        source: RequestBody,
        transactionType: RequestBody,
        photo: MultipartBody.Part?,
        photoSudah: MultipartBody.Part?
    ): LabCashResponse {
        return apiService.updateLabCash(
            id, name, inputDate, amount, source, transactionType, photo, photoSudah
        )
    }
}
