package com.example.aicomsapp.viewmodels.dashboardadmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aicomsapp.adapters.ImprestFundAdapter
import com.example.aicomsapp.databinding.ActivityDashboardAdminBinding
import com.example.aicomsapp.di.Injection
import com.example.aicomsapp.viewmodels.usercontrol.UserControlFragment

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var viewModel: DashboardAdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupBottomNavigation() // Set up bottom navbar including home button
        fetchImprestFunds() // Fetch data for RecyclerView
    }

    private fun setupViewModel() {
        val retrofitRepository = Injection.provideRetrofitRepository(this)
        val factory = DashboardAdminViewModelFactory(retrofitRepository)
        viewModel = ViewModelProvider(this, factory).get(DashboardAdminViewModel::class.java)
    }

    private fun setupBottomNavigation() {
        // Set custom click listeners for each button in bottomBar
        binding.bottomBar.homeButton.setOnClickListener {
            // Handle home button click
            fetchImprestFunds()
            val intent = Intent(this, DashboardAdminActivity::class.java)  // Ganti dengan DashboardMainActivity jika ada
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()  // Optional, close this activity
        }


        binding.bottomBar.userControl.setOnClickListener {
            // Handle user control button click
            val intent = Intent(this, UserControlFragment::class.java)
            startActivity(intent)
        }

        binding.bottomBar.fabPlus.setOnClickListener {
            // Handle fab button click
            Toast.makeText(this, "FAB Plus Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.bottomBar.kasAicomsButton.setOnClickListener {
            // Handle kas aicoms button click
            Toast.makeText(this, "Kas Aicoms Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.bottomBar.noteButton.setOnClickListener {
            // Handle note button click
            Toast.makeText(this, "Note Button Clicked", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        // Handle custom back action here if needed, or call super for default
        super.onBackPressed()
    }

    private fun fetchImprestFunds() {
        viewModel.getImprestFunds(
            onSuccess = { imprestFunds ->
                Log.d("DashboardAdminActivity", "Fetched imprest funds: ${imprestFunds.size}")
                updateUI(imprestFunds)
            },
            onError = { error ->
                Log.e("DashboardAdminActivity", "Failed to fetch imprest funds: $error")
                Toast.makeText(this, "Failed to fetch imprest funds", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateUI(imprestFunds: List<com.example.aicomsapp.viewmodels.response.ImprestFund>) {
        binding.imprestFundsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ImprestFundAdapter(imprestFunds)
        binding.imprestFundsRecyclerView.adapter = adapter
    }
}

