package resetpass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aicomsapp.R
import com.example.aicomsapp.api.ApiConfig
import com.example.aicomsapp.viewmodels.resetpass.ForgotPasswordViewModel
import com.example.aicomsapp.viewmodels.resetpass.ForgotPasswordViewModelFactory
import com.example.aicomsapp.viewmodels.response.ForgotPasswordResponse

class ForgotPasswordFragment : Fragment() {

    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var emailEditText: EditText
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var forgotPasswordButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        emailEditText = view.findViewById(R.id.editTextEmail)
        forgotPasswordTextView = view.findViewById(R.id.textView2)
        forgotPasswordButton = view.findViewById(R.id.btn_forgotpassword)

        val mainView = view.findViewById<View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Create ApiService instance
        val apiService = ApiConfig.getApiService()

        // Initialize ViewModel with the factory
        viewModel = ViewModelProvider(this, ForgotPasswordViewModelFactory(apiService))
            .get(ForgotPasswordViewModel::class.java)

        // Set up the forgot password button click listener
        forgotPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                viewModel.sendForgotPasswordEmail(email, { response: ForgotPasswordResponse ->
                    // On success, show the response message and make the TextView visible
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    forgotPasswordTextView.visibility = View.VISIBLE
                }, { error ->
                    // On error, show a toast with the error message
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
