package com.example.aicomsapp.viewmodels.inputkas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.repository.RetrofitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class InputViewModelkas(
    private val repository: RetrofitRepository
) : ViewModel() {

    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> get() = _uploadStatus

    fun uploadData(
        name: String,
        inputDate: String,
        amount: Double,
        source: String,
        transactionType: String,
        file: File
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val uiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val convertedInputDate = apiDateFormat.format(uiDateFormat.parse(inputDate)!!)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                val transactionTypeRequestBody =
                    transactionType.toRequestBody("text/plain".toMediaTypeOrNull())
                val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val inputDateRequestBody = convertedInputDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val amountRequestBody =
                    amount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val sourceRequestBody = source.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = repository.createLabCash(
                    nameRequestBody,
                    inputDateRequestBody,
                    amountRequestBody,
                    sourceRequestBody,
                    transactionTypeRequestBody,
                    body
                )

                if (response.success) {
                    val id = response.data?.id ?: "N/A" // Ambil ID jika tersedia
                    _uploadStatus.postValue("Data uploaded successfully. ID: $id")
                } else {
                    _uploadStatus.postValue("Failed to upload data: ${response.message}")
                }
            } catch (e: Exception) {
                _uploadStatus.postValue("Error: ${e.message}")
                Log.e("UploadData", "Exception: ${e.message}", e)
            }
        }
    }

    fun updateKas(
        id: String,
        name: String,
        inputDate: String,
        amount: Double,
        source: String,
        transactionType: String,
        file: File? = null // Image is optional for updating
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Convert the inputDate from UI format (dd/MM/yyyy) to API format (yyyy/MM/dd)
                val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val uiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val convertedInputDate = apiDateFormat.format(uiDateFormat.parse(inputDate)!!)

                // Prepare request bodies for other fields
                val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val inputDateRequestBody =
                    convertedInputDate.toRequestBody("text/plain".toMediaTypeOrNull())  // Already converted date
                val sourceRequestBody = source.toRequestBody("text/plain".toMediaTypeOrNull())
                val amountRequestBody =
                    amount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val transactionTypeRequestBody =
                    transactionType.toRequestBody("text/plain".toMediaTypeOrNull())

                // Prepare the file if it's provided (for image upload)
                val body = file?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                }

                // Make the update request, with or without the image file
                val response = if (body != null) {
                    repository.updateLabCash(
                        id,
                        nameRequestBody,
                        inputDateRequestBody,
                        amountRequestBody,
                        sourceRequestBody,
                        transactionTypeRequestBody,
                        body,
                        null// Image part only if not null
                    )
                } else {
                    repository.updateLabCash(
                        id,
                        nameRequestBody,
                        inputDateRequestBody,
                        amountRequestBody,
                        sourceRequestBody,
                        transactionTypeRequestBody,
                        null, // No image part, the photo is optional
                        null
                    )
                }

                // Check the response and post status
                if (response.success) {
                    _uploadStatus.postValue("Data updated successfully.")
                } else {
                    _uploadStatus.postValue("Failed to update data: ${response.message}")
                }

            } catch (e: Exception) {
                // Handle any errors and post the error status
                _uploadStatus.postValue("Error: ${e.message}")
                Log.e("UpdateImprest", "Exception: ${e.message}", e)
            }
        }
    }
}