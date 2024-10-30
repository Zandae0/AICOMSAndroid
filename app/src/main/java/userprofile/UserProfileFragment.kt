package com.example.aicomsapp.viewmodels.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aicomsapp.databinding.FragmentUserProfileBinding
import com.example.aicomsapp.di.Injection
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.preference.dataStore
import com.example.aicomsapp.repository.RetrofitRepository
import com.example.aicomsapp.viewmodels.ViewModelFactory
import com.example.aicomsapp.viewmodels.signin.LoginViewModel
import com.example.aicomsapp.viewmodels.userprofile.UserProfileViewModel

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        // Get the UID of the current user from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Set the username from SharedPreferences
        val userName = sharedPref.getString("USER_NAME", "Guest")
        binding.editTextName.text = userName

        val userRole = sharedPref.getString("USER_ROLE", "Guest")
        binding.editTextRole.text = userRole

        // Apply button listener to change the password
        binding.buttonApply.setOnClickListener {
            val currentPassword = binding.editTextCurrentPassword.text.toString().trim()
            val newPassword = binding.editTextNewPassword.text.toString().trim()

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                // Show error if fields are empty
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Retrieve the UID from SharedPreferences
                val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val uid = sharedPref.getString("USER_ID", "") ?: ""

                if (uid.isNotEmpty()) {
                    // Call the ViewModel to change password, passing the UID, current password, and new password
                    viewModel.changePassword(uid, currentPassword, newPassword)
                } else {
                    Toast.makeText(requireContext(), "Error: User ID not found", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Observe change password result
        viewModel.changePasswordResult.observe(viewLifecycleOwner, { result ->
            // Show the result message from ViewModel using a Toast
            Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
        })
    }

    private fun setupViewModel() {
        val retrofitRepository = Injection.provideRetrofitRepository(requireContext())
        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory(requireContext(), retrofitRepository, userPreference)
        viewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}