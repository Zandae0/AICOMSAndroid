package com.example.aicomsapp.viewmodels.labcash

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.aicomsapp.R
import com.example.aicomsapp.databinding.FragmentLabcashBinding
import com.example.aicomsapp.di.Injection
import com.example.aicomsapp.viewmodels.dashboard.DashboardCombinedFragment
import com.example.aicomsapp.viewmodels.detailpagekas.DetailLabFragment
import com.example.aicomsapp.viewmodels.inputkas.InputLabFragment
import com.example.aicomsapp.viewmodels.note.NoteFragment
import com.example.aicomsapp.viewmodels.response.LabCash
import com.example.aicomsapp.viewmodels.usercontrol.UserControlFragment
import com.example.aicomsapp.viewmodels.views.BottomNavBar
import com.example.aicomsapp.viewmodels.views.BottomNavListener
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class LabCashFragment : Fragment(), BottomNavListener {

    private var _binding: FragmentLabcashBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LabCashViewModel
    private var selectedImageFile: File? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var balance: Double = 0.0
    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1001
    }


    private var backPressedTime: Long = 0
    private lateinit var toast: Toast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLabcashBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var dialogView: View? = null
    private var dialog: AlertDialog? = null
    private var selectedLabCash: LabCash? = null
    private var fullLabCashList: List<LabCash> = emptyList()
    private lateinit var adapter: LabCashAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    selectedImageFile = File(getRealPathFromURI(selectedImageUri))
                    Toast.makeText(requireContext(), "Image selected", Toast.LENGTH_SHORT).show()
                    dialogView?.let { view ->
                        val selectedImageView = view.findViewById<ImageView>(R.id.selectedImageView)
                        val imagePickerButton = view.findViewById<Button>(R.id.buttonChooseFile)

                        selectedImageView.setImageURI(selectedImageUri)
                        selectedImageView.visibility = View.VISIBLE
                        imagePickerButton.visibility = View.GONE
                    }// Reopen dialog to display selected image
                } else {
                    Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        adapter = LabCashAdapter(
            listOf(),
            onItemClick = { labCash -> navigateToDetail(labCash) },
            onPictureClick = { labCash -> showUploadDialog(labCash) },
            onPhotoSudahClick = { labCash -> showEnlargedSudahImageDialog(labCash) },
            onTransactionTypeChange = { labCash, newTransactionType ->
                updateTransactionType(labCash, newTransactionType)
            }
        )
        binding.LabRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.LabRecyclerView.adapter = adapter
        // Set user name from shared preferences
        val roleAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Filter, // your array defined in strings.xml
            R.layout.filter_selected_item // custom layout for selected item (main view)
        )
        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.filterSpinner.adapter = roleAdapter
        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                sortLabCashList(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        sortLabCashList("Nama") // Sort by "Nama" initially

        // Setup ViewModel
        setupViewModel()

        // Setup Bottom Navigation Bar
        setupBottomNavigationBar()


        // Fetch Imprest Funds to display
        fetchLabCash()
        setupSearchView()


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
        if (backPressedTime + 5000 > System.currentTimeMillis()) {
            toast.cancel()
            requireActivity().finish() // Exit the app
        } else {
            toast = Toast.makeText(requireContext(), "Press back again to exit the application", Toast.LENGTH_SHORT)
            toast.show()
        }
        backPressedTime = System.currentTimeMillis() // Set time of first back press
    }


    private fun setupViewModel() {
        val retrofitRepository = Injection.provideRetrofitRepository(requireContext())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(LabCashViewModel::class.java) -> {
                        LabCashViewModel(retrofitRepository) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
        viewModel = ViewModelProvider(this, factory).get(LabCashViewModel::class.java)
    }

    private fun setupBottomNavigationBar() {
        val bottomNavBar = BottomNavBar(binding.bottomBar.root, this) // Use 'this' as listener
    }
    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0 // Menghilangkan desimal
        return format.format(amount).replace("Rp", "Rp.")
    }
    //perhitungan saldo berdasarkan transactionType
    private fun updateTransactionType(labCash: LabCash, newTransactionType: String) {
        balance -= when (labCash.transactionType) {
            "out" -> labCash.amount // If previously "out", subtract the amount
            "in" -> 0.0 // If previously "in", no effect as it neutralizes the previous out
            "donation" -> labCash.amount // If previously "donation", subtract it from the balance
            else -> 0.0
        }

        // Apply the effect of the new transaction type
        balance += when (newTransactionType) {
            "out" -> -labCash.amount // If now "out", subtract the amount
            "in" -> 0.0 // If now "in", the effect is neutral (cancel out any out)
            "donation" -> labCash.amount // If now "donation", add to the balance
            else -> 0.0
        }

        // Update the transaction type on the object
        labCash.transactionType = newTransactionType
        binding.tvSaldoAmountLab.text = formatCurrency(balance)

        // Update the adapter if needed
        binding.LabRecyclerView.adapter?.notifyDataSetChanged()

    }
    //buat searching
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                filterLabCash(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes for real-time filtering
                filterLabCash(newText)
                return true
            }
        })// Change the color of the text in the SearchView to black
        binding.searchView.apply {
            val searchEditText = findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchEditText?.setTextColor(Color.BLACK)
            searchEditText?.setHintTextColor(Color.GRAY)
        }

    }

    // Function to filter LabCash items based on the query
    private fun filterLabCash(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            fullLabCashList
        } else {
            fullLabCashList.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(filteredList)
    }
    //untuk ngesorting
    private fun sortLabCashList(criteria: String) {
        val sortedList = when (criteria) {
            "Nama" -> fullLabCashList.sortedBy { it.name }
            "Tanggal" -> fullLabCashList.sortedByDescending { it.inputDate }
            "Uang" -> fullLabCashList.sortedBy { it.amount }
            "Status" -> fullLabCashList.sortedWith(compareBy {
                when (it.transactionType) {
                    "out" -> 0
                    "donation" -> 1
                    "in" -> 2
                    else -> 3
                }
            })
            else -> fullLabCashList
        }
        adapter.updateData(sortedList)
    }

    //fetching get API
    private fun fetchLabCash() {
        viewModel.getLabCash(
            onSuccess = { LabCashList ->
                fullLabCashList = LabCashList
                balance = 0.0

                // Hitung saldo berdasarkan transactionType
                fullLabCashList.forEach { labCash ->
                    balance += when (labCash.transactionType) {
                        "out" -> -labCash.amount // Subtract for "out"
                        "in" -> 0.0 // "in" transactions neutralize, so no effect
                        "donation" -> labCash.amount // Add for "donation"
                        else -> 0.0
                    }
                }

                // Set saldo ke TextView
                binding.tvSaldoAmountLab.text = formatCurrency(balance)
                adapter.updateData(fullLabCashList)
                sortLabCashList("Nama")
            },
            onError = {
                Toast.makeText(requireContext(), "Failed to fetch imprest funds", Toast.LENGTH_SHORT).show()
            }
        )
    }
    //mengirimkan data ke detailpage
    private fun navigateToDetail(labCash: LabCash) {
        val detailFragment = DetailLabFragment()
        val bundle = Bundle().apply {
            putString("EXTRA_ID", labCash.id)
            putString("EXTRA_NAME", labCash.name)
            putDouble("EXTRA_AMOUNT", labCash.amount)
            putString("EXTRA_INPUT_DATE", labCash.inputDate)
            putString("EXTRA_SOURCE", labCash.source)
            putString("EXTRA_IMAGE_URI", labCash.photoURL)
            putString("EXTRA_TRANSACTION_TYPE", labCash.transactionType)
        }
        Log.d("LabCashFragment", "Photo URI: ${labCash.photoURL}")
        detailFragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
    //upload foto sudah
    private fun showUploadDialog(labCash: LabCash) {
        selectedLabCash = labCash
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_upload_image, null)
        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Upload Image")
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                // Clear selected image when canceled and dismiss dialog
                selectedImageFile = null
                dialogInterface.dismiss()
            }
            .setPositiveButton("Upload") { _, _ ->
                selectedImageFile?.let {
                    updateLabCash(
                        id = labCash.id,
                        name = labCash.name,
                        inputDate = labCash.inputDate,
                        amount = labCash.amount,
                        source = labCash.source,
                        transactionType = "in",
                        photo = null,
                        photoSudah = it // Only photoSudah is required
                    )
                }
            }
            .create()

        val imagePickerButton = dialogView?.findViewById<Button>(R.id.buttonChooseFile)
        val selectedImageView = dialogView?.findViewById<ImageView>(R.id.selectedImageView)


        // Set up image picker button
        selectedImageView?.visibility = if (selectedImageFile != null) View.VISIBLE else View.GONE
        imagePickerButton?.visibility = if (selectedImageFile != null) View.GONE else View.VISIBLE

        imagePickerButton?.setOnClickListener {
            selectImage()
        }
        selectedImageView?.setOnClickListener {
            showEnlargedImageDialog()
        }

        dialog?.setOnDismissListener {
            selectedImageFile = null
            dialogView = null
            dialog = null
        }

        dialog?.show()
        viewModel.uploadStatus.observe(viewLifecycleOwner) { statusMessage ->
            Toast.makeText(requireContext(), statusMessage, Toast.LENGTH_SHORT).show()
            fetchLabCash()
            dialog?.dismiss()
        }

    }
    //membesarkan foto yang dipilih
    private fun showEnlargedImageDialog() {
        val enlargedImageDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_enlarged_image, null)
        val enlargedImageView = enlargedImageDialogView.findViewById<ImageView>(R.id.enlargedImageView)

        // Display selected image in enlarged view
        selectedImageFile?.let { file ->
            enlargedImageView.setImageURI(Uri.fromFile(file))
        }

        AlertDialog.Builder(requireContext())
            .setView(enlargedImageDialogView)
            .setPositiveButton("Close", null)
            .create()
            .show()
    }
    //membesarkan foto yang sudah diupload
    private fun showEnlargedSudahImageDialog(labCash: LabCash) {
        val photoSudahUri = labCash.photoSUDAH
        if (!photoSudahUri.isNullOrEmpty()) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_enlarge_detail, null)
            val enlargedImageView = dialogView.findViewById<ImageView>(R.id.enlargedImageView)
            Log.d("LabCashFragment", "photoSUDAH URI: $photoSudahUri")

            // Load the image using Glide
            Glide.with(this)
                .load(photoSudahUri)
                .error(R.drawable.error_404)
                .into(enlargedImageView)

            // Create and show the dialog
            AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
                .show()
        } else {
            Log.e("LabCashFragment", "Error: photoSUDAH URI is empty or null")
            Toast.makeText(requireContext(), "No image available", Toast.LENGTH_SHORT).show()
        }
    }


    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    // Fungsi tambahan untuk mendapatkan path sebenarnya dari URI
    private fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            if (idx != -1) {
                filePath = cursor.getString(idx)
            }
            cursor.close()
        }
        return filePath
    }
    //mengirim data update ke viewmodel
    private fun updateLabCash(
        id: String,
        name: String = "",
        inputDate: String = "",
        amount: Double = 0.0,
        source: String = "",
        transactionType: String = "",
        photo: File? = null,
        photoSudah: File
    ) {
        viewModel.updateLabCash(id, name, inputDate, amount, source, transactionType, photo, photoSudah)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
//bottomnavbar
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
        // Buat instance InputLabFragment
        val inputLabFragment = InputLabFragment()

        // Buat bundle dan tambahkan flag sebagai argumen
        val bundle = Bundle().apply {
            putBoolean("EXTRA_FROM_LABCASH_DASHBOARD", true) // Kirim flag
        }

        // Set argument ke fragment
        inputLabFragment.arguments = bundle

        // Ganti fragment saat ini dengan InputLabFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, inputLabFragment) // Ganti fragment saat ini
            .addToBackStack(null) // Tambahkan ke backstack untuk navigasi kembali
            .commit()
    }


    override fun onLabCashClicked() {
        if (parentFragmentManager.findFragmentByTag("LabCashFragment") == null) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LabCashFragment(), "LabCashFragment")
                .commit()
        }
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
