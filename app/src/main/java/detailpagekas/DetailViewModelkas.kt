package com.example.aicomsapp.viewmodels.detailpagekas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class kas(
    val name: String,
    val amount: Double,
    val inputDate: String,
    val source: String,
    val photoUri: String
)

class DetailLabViewModel : ViewModel() {

    // Private mutable live data to store user data
    private val _userData = MutableLiveData<kas>()

    // Initial data (you can remove this if you don't want initial data)
    init {
        // Set example data (remove if not needed)
        _userData.value = kas(
            name = "Guest",
            amount = 000.0,
            inputDate = "15 Juni 2024",
            source = "Tel-U Finance",
            photoUri = "https://example.com/photo.jpg"
        )
    }

    // Function to set user data dynamically
    fun setUserData(
        name: String,
        amount: Double,
        inputDate: String,
        source: String,
        photoUri: String
    ) {
        // Update the MutableLiveData with the new data
        _userData.value = kas(
            name = name,
            amount = amount,
            inputDate = inputDate,
            source = source,
            photoUri = photoUri
        )
    }
}