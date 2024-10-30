package com.example.aicomsapp.viewmodels.response

data class LabCashas(
    val labCash: List<LabCash>
)

data class LabCash(
    val id: String,
    val name: String,
    val inputDate: String,
    val amount: Double,
    val source: String,
    var transactionType: String, // Added transaction type
    val photoURL: String,
    val photoSUDAH : String
)