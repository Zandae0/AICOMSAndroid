package splashscreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import com.example.aicomsapp.R
import com.example.aicomsapp.viewmodels.dashboard.DashboardCombinedFragment
import welcome.WelcomeFragment

class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment ini
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)

        // Delay selama 2 detik sebelum mengecek status login
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()  // Panggil fungsi untuk mengecek status login
        }, 2000)

        return view
    }

    private fun checkLoginStatus() {
        // Cek status login dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)
        Log.d("SplashScreenFragment", "Token: $token")

        // Jika token ada, user sudah login, arahkan ke DashboardFragment
        if (token != null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardCombinedFragment())
                .commit()
        } else {
            // Jika token tidak ada, arahkan ke WelcomeFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WelcomeFragment())
                .commit()
        }
    }
}
