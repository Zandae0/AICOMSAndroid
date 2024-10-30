package com.example.aicomsapp.viewmodels.inputimprest

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

class InputViewModelimprest(
    private val repository: RetrofitRepository
) : ViewModel() {

    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> get() = _uploadStatus

    fun uploadData(
        name: String,
        inputDate: String,  // Format dd/MM/yyyy dari UI
        purpose: String,
        transactionDate: String,  // Format dd/MM/yyyy dari UI
        amount: Number,
        source: String,
        pic: String,
        file: File,
        transactionType: String,
        status: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Konversi tanggal dari UI format (dd/MM/yyyy) ke API format (yyyy/MM/dd)
                val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val uiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val convertedInputDate = apiDateFormat.format(uiDateFormat.parse(inputDate)!!)
                val convertedTransactionDate =
                    apiDateFormat.format(uiDateFormat.parse(transactionDate)!!)

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                // Konversi semua field menjadi RequestBody
                val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val inputDateRequestBody =
                    convertedInputDate.toRequestBody("text/plain".toMediaTypeOrNull())  // Tanggal sudah dikonversi
                val purposeRequestBody = purpose.toRequestBody("text/plain".toMediaTypeOrNull())
                val transactionDateRequestBody =
                    convertedTransactionDate.toRequestBody("text/plain".toMediaTypeOrNull())  // Tanggal sudah dikonversi
                val amountRequestBody =
                    amount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val sourceRequestBody = source.toRequestBody("text/plain".toMediaTypeOrNull())
                val picRequestBody = pic.toRequestBody("text/plain".toMediaTypeOrNull())
                val transactionTypeRequestBody =
                    transactionType.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusRequestBody = status.toRequestBody("text/plain".toMediaTypeOrNull())

                // Panggil repository untuk upload data
                val response = repository.createImprestFund(
                    nameRequestBody,
                    inputDateRequestBody,
                    purposeRequestBody,
                    transactionDateRequestBody,
                    amountRequestBody,
                    sourceRequestBody,
                    picRequestBody,
                    transactionTypeRequestBody,
                    statusRequestBody,
                    body
                )

                if (response.success) {
                    val id = response.data?.id ?: "N/A"
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

    fun updateImprest(
        id: String,
        name: String,
        inputDate: String,  // Format dd/MM/yyyy dari UI
        purpose: String,
        transactionDate: String,  // Format dd/MM/yyyy dari UI
        amount: Number,
        source: String,
        pic: String,
        file: File? = null,
        transactionType: String,
        status: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Konversi tanggal dari UI format (dd/MM/yyyy) ke API format (yyyy/MM/dd)
                val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val uiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val convertedInputDate = apiDateFormat.format(uiDateFormat.parse(inputDate)!!)
                val convertedTransactionDate =
                    apiDateFormat.format(uiDateFormat.parse(transactionDate)!!)

                val body = file?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                }

                val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val inputDateRequestBody =
                    convertedInputDate.toRequestBody("text/plain".toMediaTypeOrNull())  // Tanggal sudah dikonversi
                val purposeRequestBody = purpose.toRequestBody("text/plain".toMediaTypeOrNull())
                val transactionDateRequestBody =
                    convertedTransactionDate.toRequestBody("text/plain".toMediaTypeOrNull())  // Tanggal sudah dikonversi
                val amountRequestBody =
                    amount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val sourceRequestBody = source.toRequestBody("text/plain".toMediaTypeOrNull())
                val picRequestBody = pic.toRequestBody("text/plain".toMediaTypeOrNull())
                val transactionTypeRequestBody =
                    transactionType.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusRequestBody = status.toRequestBody("text/plain".toMediaTypeOrNull())

                // PUT request for updating ImprestFund
                val response = if (body != null) {
                    repository.updateImprestFund(
                        id,
                        nameRequestBody,
                        inputDateRequestBody,
                        purposeRequestBody,
                        transactionDateRequestBody,
                        amountRequestBody,
                        sourceRequestBody,
                        picRequestBody,
                        transactionTypeRequestBody,
                        statusRequestBody,
                        body,
                        null
                    )
                }else   repository.updateImprestFund(
                    id,
                    nameRequestBody,
                    inputDateRequestBody,
                    purposeRequestBody,
                    transactionDateRequestBody,
                    amountRequestBody,
                    sourceRequestBody,
                    picRequestBody,
                    transactionTypeRequestBody,
                    statusRequestBody,
                    null,
                    null
                )
                if (response.success) {
                    _uploadStatus.postValue("Data updated successfully.")
                } else {
                    _uploadStatus.postValue("Failed to update data: ${response.message}")
                }
            } catch (e: Exception) {
                _uploadStatus.postValue("Error: ${e.message}")
                Log.e("UpdateImprest", "Exception: ${e.message}", e)
            }
        }
    }
}