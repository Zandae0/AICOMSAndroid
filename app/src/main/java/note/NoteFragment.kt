package com.example.aicomsapp.viewmodels.note

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aicomsapp.R
import com.example.aicomsapp.databinding.FragmentNoteBinding
import com.example.aicomsapp.viewmodels.dashboard.DashboardCombinedFragment
import com.example.aicomsapp.viewmodels.inputkas.InputLabFragment
import com.example.aicomsapp.viewmodels.labcash.LabCashFragment
import com.example.aicomsapp.viewmodels.shared.SharedUserViewModel
import com.example.aicomsapp.viewmodels.usercontrol.UserControlFragment
import com.example.aicomsapp.viewmodels.views.BottomNavBar
import com.example.aicomsapp.viewmodels.views.BottomNavListener

class NoteFragment : Fragment(), BottomNavListener {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavBar: BottomNavBar
    private lateinit var sharedViewModel: SharedUserViewModel
    private lateinit var todoAdapter: TodoAdapter
    private var userStatus: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel here to ensure it is available when needed
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedUserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun getUserStatusFromPreferences(): Int {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
        return sharedPref.getInt("USER_STATUS", 0) // 0 sebagai default jika tidak ada
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userStatus = getUserStatusFromPreferences()
        updateUIBasedOnUserStatus()
        // Retrieve UID from arguments passed from Dashboard
        val uid = arguments?.getString("USER_ID")
        todoAdapter = TodoAdapter(sharedViewModel)

        // Menghubungkan RecyclerView dengan todoAdapter
        binding.todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todoAdapter
        }

        if (uid != null) {
            // Initialize ViewModel and set UID in SharedUserViewModel if needed
            sharedViewModel = ViewModelProvider(requireActivity()).get(SharedUserViewModel::class.java)
            sharedViewModel.setUID(uid)  // Assuming setUID is used to store the UID globally

            // Observe the note and to-do list based on the UID
            observeData()
            setupBottomNavigationBar()
            // Set up button click listeners
            setupListeners()

        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupBottomNavigationBar() {
        // Initialize BottomNavBar with listener
        bottomNavBar = BottomNavBar(requireView().findViewById(R.id.bottom_bar), this)
    }
    private fun observeData() {
        sharedViewModel.note.observe(viewLifecycleOwner, { note ->
            binding.noteInput.setText(note)
        })

        sharedViewModel.todoList.observe(viewLifecycleOwner, { list ->
            todoAdapter.submitList(list.toMutableList())
        })
    }
    private fun updateUIBasedOnUserStatus() {
        Log.d("DashboardCombinFragment", "Updating UI, userStatus: $userStatus")
        if (userStatus == 1) {
            // Admin
            binding.bottomBar.userControl.visibility = View.VISIBLE
            binding.bottomBar.kasAicomsButton.visibility = View.VISIBLE
        } else {
            // General User
            binding.bottomBar.userControl.visibility = View.GONE
            binding.bottomBar.kasAicomsButton.visibility = View.GONE
        }
    }
    private fun setupListeners() {
        // Save note button
        binding.saveNoteButton.setOnClickListener {
            val note = binding.noteInput.text.toString()
            sharedViewModel.setNote(note)
            Toast.makeText(requireContext(), "Note saved!", Toast.LENGTH_SHORT).show()
        }

        // Add task button for the to-do list
        binding.addTodoButton.setOnClickListener {
            val task = binding.todoInput.text.toString()
            if (task.isNotEmpty()) {
                sharedViewModel.addTodoItem(task)
                binding.todoInput.text.clear()
            } else {
                Toast.makeText(requireContext(), "Task cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
            .replace(R.id.fragment_container, InputLabFragment()) // Ganti fragment saat ini
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
}
