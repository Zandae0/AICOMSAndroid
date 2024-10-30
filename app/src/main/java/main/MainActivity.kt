package com.example.aicomsapp.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aicomsapp.databinding.ActivityMainBinding
import com.example.aicomsapp.viewmodels.ViewModelFactory
import main.MainViewModel
import welcome.WelcomeFragment
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import com.example.aicomsapp.R
import com.example.aicomsapp.api.ApiConfig
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.repository.RetrofitRepository
import com.google.firebase.FirebaseApp
import splashscreen.SplashScreenFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class MainActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi tampilan
        setupUI()
    }

    private fun setupUI() {
        // Mengatur tampilan atau fragment yang diinginkan
        val fragment = SplashScreenFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
