package com.example.aicomsapp.viewmodels.inputkas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aicomsapp.R
import com.example.aicomsapp.api.ApiConfig
import com.example.aicomsapp.databinding.FragmentInputKasBinding
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.preference.dataStore
import com.example.aicomsapp.repository.RetrofitRepository
import com.example.aicomsapp.viewmodels.detailpagekas.DetailLabFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

class InputLabFragment : Fragment() {

    private var _binding: FragmentInputKasBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InputViewModelkas
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputKasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            // Tetapkan "Donasi" sebagai radio button yang dipilih secara default

        val roleAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.SumberTujuan, // your array defined in strings.xml
            R.layout.spinner_selected_item // custom layout for selected item (main view)
        )
        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerTujuan.adapter = roleAdapter

        // Setup TextWatcher for editTextJumlah
        binding.editTextJumlah.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.editTextJumlah.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false // Flag to prevent recursive updates

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("EditTextWatcher", "onTextChanged called with: $s")
                if (isUpdating) return // Skip if already updating

                isUpdating = true // Set flag to prevent recursion

                // Remove any non-numeric characters (except for decimals)
                val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")

                if (cleanString.isNotEmpty()) {
                    try {
                        // Parse the clean string to a long for formatting
                        val parsed = cleanString.toLong()

                        // Format the number to Indonesian locale with thousand separators
                        val formatted = NumberFormat.getInstance(Locale("id", "ID")).format(parsed)

                        // Set the formatted text with "Rp" prefix
                        val newText = "Rp $formatted"

                        // Update the EditText with the new formatted value
                        binding.editTextJumlah.setText(newText)

                        // Move the cursor to the end of the text
                        binding.editTextJumlah.setSelection(newText.length)
                    } catch (e: NumberFormatException) {
                        e.printStackTrace() // Handle invalid number format
                    }
                }

                isUpdating = false // Reset the flag after update
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })

        // Handle arguments passed to the fragment
        arguments?.let { bundle ->
            val name = bundle.getString("EXTRA_NAME")
            val amount = bundle.getDouble("EXTRA_AMOUNT", 0.0)
            val inputDate = bundle.getString("EXTRA_INPUT_DATE", "")
            val source = bundle.getString("EXTRA_SOURCE")
            val photoUri = bundle.getString("EXTRA_IMAGE_URI")
            val transactionType = bundle.getString("EXTRA_TRANSACTION_TYPE")
            val fromDetail = bundle.getBoolean("EXTRA_FROM_DETAIL", false) ?: false

            // Set RadioGroup visibility based on fromDetail
            if (fromDetail) {
                binding.textTipeTransaksi.visibility = View.GONE
                binding.radioGroupTransaksi.visibility = View.GONE
            } else {
                binding.radioGroupTransaksi.visibility = View.VISIBLE
                binding.textTipeTransaksi.visibility = View.VISIBLE
            }
            binding.editTextNama.setText(name)
            photoUri?.let {
                selectedImageUri = Uri.parse(it)
                binding.imageView11.setImageURI(selectedImageUri)
            }
            if (amount > 0) {
                val formattedAmount = NumberFormat.getInstance(Locale("id", "ID")).format(amount)
                binding.editTextJumlah.setText("Rp $formattedAmount")
            }

            // Set the appropriate radio button based on source
            when (transactionType) {
                "donation" -> binding.Masuk.isChecked = true
                "out" -> binding.Keluar.isChecked = true
            }
        }

        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference(requireActivity().dataStore)
        val repository = RetrofitRepository(apiService, userPreference)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(InputViewModelkas::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return InputViewModelkas(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(InputViewModelkas::class.java)

        viewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
            Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
        }

        // Handle image selection
        binding.imageView11.setOnClickListener {
            selectImage()
        }

        // Handle upload button click
        binding.button4.setOnClickListener {
            uploadData()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.imageView11.setImageURI(selectedImageUri)
        }
    }

    private fun uploadData() {
        val name = binding.editTextNama.text.toString().trim()
        val inputDate = binding.editTextTanggal.text.toString().trim()
        val amountText = binding.editTextJumlah.text.toString().trim().replace("[Rp,.\\s]".toRegex(), "")
        val amount: Double? = amountText.toDoubleOrNull()
        val source = binding.spinnerTujuan.selectedItem.toString().trim()
        val fromDetail = arguments?.getBoolean("EXTRA_FROM_DETAIL", false) ?: false
        val transactionType = if (fromDetail) {
            arguments?.getString("EXTRA_TRANSACTION_TYPE") ?: ""
        } else {
            when (binding.radioGroupTransaksi.checkedRadioButtonId) {
                R.id.Masuk -> "donation"
                R.id.Keluar -> "out"
                else -> ""
            }
        }
        val isUpdate = arguments?.getString("EXTRA_ID") != null
        val selectedImageUriString = arguments?.getString("EXTRA_IMAGE_URI")  // Uri gambar dari detail
        if (name.isEmpty() || inputDate.isEmpty() || amount == null || source.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill all fields and provide a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isUpdate && selectedImageUri == null) {
            Toast.makeText(requireActivity(), "Please select an image to upload.", Toast.LENGTH_SHORT).show()
            return
        }
        val file: File? = selectedImageUri?.let { uri ->
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                val file = File(requireActivity().cacheDir, "uploaded_image.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                file
            } catch (e: FileNotFoundException) {
                Log.e("UploadData", "FileNotFoundException: Unable to open image file from URI: $selectedImageUri", e)
                null
            } catch (e: IOException) {
                Log.e("UploadData", "IOException: Failed to copy image file", e)
                null
            }
        }
            if (isUpdate) {
                // If this is an update, proceed without requiring a new image (file can be null)
                val id = arguments?.getString("EXTRA_ID") ?: ""
                viewModel.updateKas(
                    id,  // Pass the ID for updating
                    name,
                    inputDate,
                    amount,
                    source,
                    transactionType,
                    file,

                )
            } else {
                // For new upload, image is mandatory
                if (file == null) {
                    Toast.makeText(requireActivity(), "Please select an image to upload.", Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.uploadData(
                    name,
                    inputDate,
                    amount,
                    source,
                    transactionType,
                    file,
                )
            }

            // Navigate to DetailFragment after successful upload
            val detailFragment = DetailLabFragment().apply {
                arguments = Bundle().apply {
                    putString("EXTRA_ID", id.toString())
                    putString("EXTRA_NAME", name)
                    putDouble("EXTRA_AMOUNT", amount)
                    putString("EXTRA_INPUT_DATE", inputDate)
                    putString("EXTRA_SOURCE", source)
                    putString("EXTRA_IMAGE_URI", selectedImageUri.toString())
                    putString("EXTRA_TRANSACTION_TYPE", transactionType)
                    putBoolean("EXTRA_CAN_EDIT", false)
                    putBoolean("EXTRA_FROM_INPUT", true)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()

        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
