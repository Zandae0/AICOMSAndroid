package com.example.aicomsapp.viewmodels.detailpageimprest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class ImprestFund(
    val name: String,
    val inputDate: String,
    val purpose: String,
    val amount: Double,
    val transactionDate: String,
    val pic: String,
    val photoUri: String
)

class DetailViewModel : ViewModel() {

    // Private mutable live data to store user data
    private val _userData = MutableLiveData<ImprestFund>()

    // Public immutable live data to expose user data to the UI

    // Initial data (you can remove this if you don't want initial data)
    init {
        // Set example data (remove if not needed)
        _userData.value = ImprestFund(
            name = "Guest",
            amount = 000.0,
            inputDate = "15 Juni 2024",
            purpose = "Belanja",
            transactionDate = "15 Juni 2024",
            pic = "John Doe",
            photoUri = "https://example.com/photo.jpg"
        )
    }

    // Function to set user data dynamically
    fun setUserData(
        name: String,
        amount: Double,
        inputDate: String,
        purpose: String,
        source: String,
        transactionDate: String,
        pic: String,
        photoUri: String
    ) {
        // Update the MutableLiveData with the new data
        _userData.value = ImprestFund(
            name = name,
            amount = amount,
            inputDate = inputDate,
            purpose = purpose,
            transactionDate = transactionDate,
            pic = pic,
            photoUri = photoUri
        )
    }
}