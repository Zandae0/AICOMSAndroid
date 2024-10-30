package com.example.aicomsapp.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.aicomsapp.R
import com.example.aicomsapp.di.Injection
import com.example.aicomsapp.repository.RetrofitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import signin.LoginFragment

class RegisterFragment : Fragment() {

    private lateinit var repository: RetrofitRepository
    private val defaultStatus = 0  // Define default status

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText: EditText = view.findViewById(R.id.editTextNama)
        val usernameEditText: EditText = view.findViewById(R.id.editTextUsename)
        val emailEditText: EditText = view.findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = view.findViewById(R.id.editTextPassword)
        val roleSpinner: Spinner = view.findViewById(R.id.list_role)
        val registerButton: Button = view.findViewById(R.id.btn_register)
        val roleAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.role, // your array defined in strings.xml
            R.layout.spinner_selected_item // custom layout for selected item (main view)
        )

        // Set custom layout for the dropdown view as well
        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        roleSpinner.adapter = roleAdapter


        // Initialize repository
        repository = Injection.provideRetrofitRepository(requireContext())

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = getRoleFromSpinner(roleSpinner.selectedItem.toString())
            val status = defaultStatus  // Use default status

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (role.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Invalid role selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = repository.register(name, username, email, password, role, status)
                    withContext(Dispatchers.Main) {
                        Log.d("RegisterFragment", "Response received: ${response.status}")
                        if (response.status == "Success") {
                            Log.d("RegisterFragment", "Showing success dialog")
                            showSuccessDialog("Registration successful. Please login.")
                        } else {
                            Toast.makeText(requireContext(), "Registration failed: ${response.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getRoleFromSpinner(selectedItem: String): String? {
        return when (selectedItem.lowercase()) {
            "student", "mahasiswa" -> "mahasiswa"
            "lecturer", "dosen" -> "dosen"
            else -> null
        }
    }

    private fun showSuccessDialog(message: String?) {
        Log.d("RegisterFragment", "Attempting to show success dialog")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Registrasi Selesai")
        builder.setMessage(message)
        builder.setPositiveButton("Lanjutkan") { dialog, _ ->
            Log.d("RegisterFragment", "Continue button clicked")
            dialog.dismiss()
            navigateToLogin()
        }
        builder.setCancelable(false)

        val dialog = builder.create()
        Log.d("RegisterFragment", "Dialog created")
        try {
            dialog.show()
            Log.d("RegisterFragment", "Dialog shown")
        } catch (e: Exception) {
            Log.e("RegisterFragment", "Error showing dialog: ${e.message}")
        }
    }

    private fun navigateToLogin() {
        val loginFragment = LoginFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, loginFragment) // Ensure you have a container for your fragments
            .addToBackStack(null)
            .commit()
    }
}
