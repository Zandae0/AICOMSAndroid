package com.example.aicomsapp.viewmodels.response

import io.grpc.Status


// DashboardResponse.kt
data class DashboardResponse(
    val imprestFunds: List<ImprestFund>
)

data class ImprestFund(
    val id: String,
    val name: String,
    val inputDate: String,
    val purpose: String,
    val transactionDate: String,
    val amount: Double,
    val source: String,
    val pic: String,
    var transactionType: String, // Added transaction type
    val photoURL: String,
    val photoSUDAH: String,
    val status: String
)