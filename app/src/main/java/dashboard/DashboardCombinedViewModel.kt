package com.example.aicomsapp.viewmodels.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.repository.RetrofitRepository
import com.example.aicomsapp.viewmodels.response.ImprestFund
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardCombinedViewModel(private val repository: RetrofitRepository) : ViewModel() {
    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> get() = _uploadStatus
    // Fungsi umum untuk fetch ImprestFunds (baik admin maupun general)
    fun getImprestFunds(onSuccess: (List<ImprestFund>) -> Unit, onError: (String) -> Unit) {
        // Launch a coroutine to call the suspend function
        viewModelScope.launch {
            try {
                // Panggil fungsi dari repository
                val imprestFundsList = repository.getImprestFunds()
                onSuccess(imprestFundsList)  // Pass the result to the success callback
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")  // Pass the error message
            }
        }
    }

    fun updateImprestFund(
    id: String,
    name: String,
    inputDate: String,
    purpose: String,
    transactionDate: String,
    amount: Double,
    source: String,
    pic: String,
    transactionType: String,
    status: String,
    photo: File? = null,
    photoSudah: File? = null
    )   {
    viewModelScope.launch {
        try {
            // Convert date from UI format to API format
            val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val uiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Prepare request bodies for text fields
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val inputDateBody =
                inputDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val transactionDateBody =
                transactionDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val amountBody = amount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val purposeBody = purpose.toRequestBody("text/plain".toMediaTypeOrNull())
            val picBody = pic.toRequestBody("text/plain".toMediaTypeOrNull())
            val sourceBody = source.toRequestBody("text/plain".toMediaTypeOrNull())
            val transactionTypeBody =
                transactionType.toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())


            // Prepare multipart parts for the photos if they are provided
            val photoPart: MultipartBody.Part? = photo?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", it.name, requestFile)
            }
            val photoSudahPart: MultipartBody.Part? = photoSudah?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photoSudah", it.name, requestFile)
            }

            // Call repository update method
            val response = repository.updateImprestFund(
                id,
                nameBody,
                inputDateBody,
                purposeBody,
                transactionDateBody,
                amountBody,
                sourceBody,
                picBody,
                transactionTypeBody,
                statusBody,
                photoPart,
                photoSudahPart
            )

            // Handle response and update upload status
            if (response.success) {
                _uploadStatus.postValue("Data updated successfully: ${response.message}")

            } else {
                _uploadStatus.postValue(response.message)

            }

        } catch (e: Exception) {
            _uploadStatus.postValue("Error: ${e.message}")
            Log.e("LabCashUpdate", "Exception: ${e.message}", e)

        }
    }
}
}