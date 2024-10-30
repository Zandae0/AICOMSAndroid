package com.example.aicomsapp.viewmodels.dashboardgeneral

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aicomsapp.adapters.GeneralImprestFundAdapter
import com.example.aicomsapp.databinding.ActivityDashboardGeneralBinding
import com.example.aicomsapp.di.Injection

class DashboardGeneralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardGeneralBinding
    private lateinit var viewModel: DashboardGeneralViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardGeneralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupBottomNavigation() // Set up bottom navbar including home button
    }

    private fun setupViewModel() {
        val retrofitRepository = Injection.provideRetrofitRepository(this)
        val factory = DashboardGeneralViewModelFactory(retrofitRepository)  // Pass the repository
        viewModel = ViewModelProvider(this, factory).get(DashboardGeneralViewModel::class.java)  // Correct ViewModel class
    }

    private fun setupBottomNavigation() {
        binding.bottomBar.apply {
            // Combine the home button click here
            homeButton.setOnClickListener {
                fetchImprestFunds() // Handle home click
            }
            fabPlus.setOnClickListener {
                // Handle fab click
            }
            noteButton.setOnClickListener {
                // Handle note click
            }
            // Hide user control and kasAicoms for general users
            userControl.visibility = View.GONE
            kasAicomsButton.visibility = View.GONE
        }
    }

    private fun fetchImprestFunds() {
        viewModel.getImprestFunds(
            onSuccess = { imprestFunds ->
                Log.d("DashboardGeneraActivity", "Fetched imprest funds: ${imprestFunds.size}")
                updateUI(imprestFunds)
            },
            onError = { error ->
                Log.e("DashboardGeneraActivity", "Failed to fetch imprest funds: $error")
                Toast.makeText(this, "Failed to fetch imprest funds", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateUI(imprestFunds: List<com.example.aicomsapp.viewmodels.response.ImprestFund>) {
        // Set up RecyclerView and pass data to the adapter
        binding.imprestFundsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = GeneralImprestFundAdapter(imprestFunds)
        binding.imprestFundsRecyclerView.adapter = adapter
    }
}
