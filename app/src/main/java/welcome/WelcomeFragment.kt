package welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.aicomsapp.R
import com.example.aicomsapp.signup.RegisterFragment
import signin.LoginFragment

class WelcomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize buttons
        val registerButton: Button = view.findViewById(R.id.button)
        val loginButton: Button = view.findViewById(R.id.button2)

        // Set click listeners
        registerButton.setOnClickListener {
            navigateToRegister()
        }

        loginButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToRegister() {
        val registerFragment = RegisterFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, registerFragment) // Ensure you have a container for your fragments
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToLogin() {
        val loginFragment = LoginFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, loginFragment) // Ensure you have a container for your fragments
            .addToBackStack(null)
            .commit()
    }
}
