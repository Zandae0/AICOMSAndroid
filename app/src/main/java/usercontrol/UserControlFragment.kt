package com.example.aicomsapp.viewmodels.usercontrol

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import com.example.aicomsapp.R
import com.example.aicomsapp.viewmodels.response.UserResponse
import com.example.aicomsapp.viewmodels.views.BottomNavBar
import com.example.aicomsapp.viewmodels.views.BottomNavListener
import androidx.appcompat.widget.SwitchCompat
import com.example.aicomsapp.viewmodels.dashboard.DashboardCombinedFragment
import com.example.aicomsapp.viewmodels.inputimprest.InputImprestFragment
import com.example.aicomsapp.viewmodels.labcash.LabCashFragment
import com.example.aicomsapp.viewmodels.note.NoteFragment
import com.example.aicomsapp.viewmodels.shared.SharedUserViewModel

class UserControlFragment : Fragment(), BottomNavListener {

    private lateinit var userTable: TableLayout
    private lateinit var applyButton: Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var bottomNavBar: BottomNavBar
    private var userStatus: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_control, container, false)
    }

    private fun getUserStatusFromPreferences(): Int {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
        return sharedPref.getInt("USER_STATUS", 0) // 0 sebagai default jika tidak ada
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userStatus = getUserStatusFromPreferences()
        Log.d("UserControlFragment", "User status from preferences: $userStatus")

        // Set up ViewModel dan UI berdasarkan user status
        setupBottomNavigationBar()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.fetchUsers()
        userViewModel.users.distinctUntilChanged().observe(viewLifecycleOwner) { users ->
            updateTable(users)
        } // Set status
        // Initialize views
        userTable = view.findViewById(R.id.userTable)
        applyButton = view.findViewById(R.id.applyButton)

        // Setup Bottom Navigation Bar
        setupBottomNavigationBar()

        // Fetch user status from arguments if any
        userStatus = arguments?.getInt("USER_STATUS", 0) ?: 0

        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Set up refresh button listener

        applyButton.setOnClickListener {
            applyChanges()
        }

        // Observe the users LiveData with distinctUntilChanged
        userViewModel.users.distinctUntilChanged().observe(viewLifecycleOwner) { users ->
            updateTable(users)
        }
    }

    private fun setupBottomNavigationBar() {
        // Initialize BottomNavBar with listener
        bottomNavBar = BottomNavBar(requireView().findViewById(R.id.bottom_bar), this)
    }

    private fun updateTable(users: List<UserResponse>) {
        // Clear existing rows, keeping the header
        if (userTable.childCount > 1) {
            userTable.removeViews(1, userTable.childCount - 1)
        }

        for (user in users) {
            val row = TableRow(requireContext()).apply {
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(16, 8, 16, 8)
            }

            // Creating a TextView for the name
            val nameTextView = TextView(requireContext()).apply {
                text = user.name
                tag = user.uid
                setPadding(16, 8, 16, 8)
                setTextColor(Color.BLACK)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            // Convert status from 1/0 to "Admin"/"User"
            val statusSwitch = SwitchCompat(requireContext()).apply {
                isChecked = user.status == 1
                tag = user.status
                updateSwitchDrawable(this)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    0.5f
                )
                setPadding(16, 8, 16, 8)
            }

            statusSwitch.setOnCheckedChangeListener { _, isChecked ->
                updateSwitchDrawable(statusSwitch)
            }

            // Add TextViews to the TableRow
            row.addView(nameTextView)
            row.addView(statusSwitch)

            // Add the TableRow to the TableLayout
            userTable.addView(row)
        }
    }

    private fun applyChanges() {
        var changesMade = false
        for (i in 1 until userTable.childCount) {
            val row = userTable.getChildAt(i) as TableRow
            val nameTextView = row.getChildAt(0) as TextView
            val switch = row.getChildAt(1) as SwitchCompat

            val initialStatus = switch.tag as Int
            val currentStatus = if (switch.isChecked) 1 else 0

            Log.d("UserControlFragment", "Processing user: ${nameTextView.text}, UID: ${nameTextView.tag}, Initial status: $initialStatus, Current status: $currentStatus")

            // Only update if the status has actually changed
            if (initialStatus != currentStatus) {
                val uid = nameTextView.tag.toString()
                userViewModel.updateUserStatus(uid)
                switch.tag = currentStatus // Update the tag to the new status
                changesMade = true
            }
        }
        if (changesMade) {
            Toast.makeText(requireContext(), "Changes applied successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No changes were made", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSwitchDrawable(switch: SwitchCompat) {
        switch.trackDrawable = if (switch.isChecked) {
            ContextCompat.getDrawable(requireContext(), R.drawable.track_admin)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.track_user)
        }?.mutate()
        switch.thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.thumb)?.mutate()
    }

    // Implement BottomNavListener methods for navigation
    override fun onHomeClicked() {
        // Navigasi ke DashboardCombinedFragment lagi
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DashboardCombinedFragment())
            .commit()
    }

    override fun onUserControlClicked() {
        // Navigasi ke UserControlActivity
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UserControlFragment())
            .commit()
    }

    override fun onFabPlusClicked() {
        // Arahkan ke InputImprestFragment saat FAB diklik
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, InputImprestFragment()) // Ganti fragment saat ini
            .addToBackStack(null) // Tambahkan ke backstack untuk navigasi kembali
            .commit()
    }

    override fun onLabCashClicked() {
        // Navigasi ke UserControlActivity
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LabCashFragment())
            .commit()

    }
    override fun onNoteClicked() {
        // Retrieve the UID from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)

        if (userId != null) {
            // Create a new instance of NoteFragment
            val noteFragment = NoteFragment()

            // Pass the UID to NoteFragment using Bundle
            val bundle = Bundle().apply {
                putString("USER_ID", userId)
            }
            noteFragment.arguments = bundle

            // Navigate to NoteFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, noteFragment)
                .addToBackStack(null)
                .commit()
        } else {
            // Handle the case where UID is not found
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun handleBackPressed() {
        // Ketika tombol back ditekan, navigasi ke DashboardCombinedFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DashboardCombinedFragment())  // ganti ke Dashboard
            .commit()
    }
}
