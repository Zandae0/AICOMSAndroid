package signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.repository.RetrofitRepository
import kotlinx.coroutines.launch
import response.RegisterResponse

class RegisterViewModel(private val repository: RetrofitRepository) : ViewModel() {
    fun registerUser(
        name: String,
        username: String,
        email: String,
        password: String,
        role: String,
        status: Number
    ) {
        viewModelScope.launch {
            try {
                val response: RegisterResponse = repository.register(
                    name = name,
                    username = username,
                    email = email,
                    password = password,
                    role = role,
                    status = status
                )

                // Mengatasi nullability dengan Elvis Operator
                if (response.success ?: false) {
                    // Handle successful registration
                    println("Registration successful: ${response.message}")
                } else {
                    // Handle failed registration
                    println("Registration failed: ${response.message}")
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
}
