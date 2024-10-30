package main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicomsapp.repository.RetrofitRepository
import com.example.aicomsapp.viewmodels.UserModel
import com.example.aicomsapp.preference.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(
    private val retrofitRepository: RetrofitRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    init {
        checkUserLoginStatus()
    }

    // Mengecek status login pengguna
    private fun checkUserLoginStatus() {
        viewModelScope.launch {
            userPreference.getLoginStatus().collect { isLoggedIn ->
                _isLoggedIn.value = isLoggedIn // Mengupdate LiveData dengan status login
            }
        }
    }
}
