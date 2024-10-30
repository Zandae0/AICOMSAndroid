package com.example.aicomsapp.viewmodels.inputimprest

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.aicomsapp.viewmodels.detailpageimprest.DetailImprestFragment
import com.example.aicomsapp.R
import com.example.aicomsapp.api.ApiConfig
import com.example.aicomsapp.databinding.FragmentInputBinding
import com.example.aicomsapp.preference.UserPreference
import com.example.aicomsapp.repository.RetrofitRepository
import customview.*
import java.io.File
import java.util.Calendar
import com.example.aicomsapp.preference.dataStore
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.FileNotFoundException
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InputImprestFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InputViewModelimprest
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up spinner and default values
        val roleAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.SumberTujuan,
            R.layout.spinner_selected_item
        )
        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerTujuan.adapter = roleAdapter

        // Format the currency input field
        binding.editTextJumlah.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.editTextJumlah.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true
                val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")
                if (cleanString.isNotEmpty()) {
                    try {
                        val parsed = cleanString.toLong()
                        val formatted = NumberFormat.getInstance(Locale("id", "ID")).format(parsed)
                        val newText = "Rp $formatted"
                        binding.editTextJumlah.setText(newText)
                        binding.editTextJumlah.setSelection(newText.length)
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                }
                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.editTextTanggalTrans.setOnClickListener {
            showDatePickerDialog(binding.editTextTanggalTrans) }
        // Retrieve arguments for update scenario
        arguments?.let { bundle ->
            val id = bundle.getString("EXTRA_ID")
            val name = bundle.getString("EXTRA_NAME")
            val purpose = bundle.getString("EXTRA_PURPOSE")
            val inputDate = bundle.getString("EXTRA_INPUT_DATE")
            val transactionDate = bundle.getString("EXTRA_TRANSACTION_DATE")
            val amount = bundle.getDouble("EXTRA_AMOUNT", 0.0)
            val source = bundle.getString("EXTRA_SOURCE")
            val pic = bundle.getString("EXTRA_PIC")
            val photoUri = bundle.getString("EXTRA_IMAGE_URI")
            val transactionType = bundle.getString("EXTRA_TRANSACTION_TYPE")
            val status = bundle.getString("EXTRA_STATUS")
            val fromDetail = bundle.getBoolean("EXTRA_FROM_DETAIL", false) ?: false

            // Set RadioGroup visibility based on fromDetail
            if (fromDetail) {
                binding.textTipeTransaksi.visibility = View.GONE
                binding.radioGroupTransaksi.visibility = View.GONE
            } else {
                binding.radioGroupTransaksi.visibility = View.VISIBLE
                binding.textTipeTransaksi.visibility = View.VISIBLE
            }
            // Set the view data based on arguments
            binding.editTextPIC.setText(pic)
            binding.editTextNama.setText(name)
            binding.editTextKeperluan.setText(purpose)
            binding.editTextTanggal.setText(inputDate)
            binding.editTextTanggalTrans.setText(transactionDate)
            photoUri?.let {
                selectedImageUri = Uri.parse(it)
                binding.imageView11.setImageURI(selectedImageUri)
            }
            if (amount > 0) {
                val formattedAmount = NumberFormat.getInstance(Locale("id", "ID")).format(amount)
                binding.editTextJumlah.setText("Rp $formattedAmount")
            }
            when (transactionType) {
                "in" -> binding.Masuk.isChecked = true
                "out" -> binding.Keluar.isChecked = true
            }
        }

        // Setup ViewModel and repository
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference(requireActivity().dataStore)
        val repository = RetrofitRepository(apiService, userPreference)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(InputViewModelimprest::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return InputViewModelimprest(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(InputViewModelimprest::class.java)

        viewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
            Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
        }

        binding.imageView11.setOnClickListener { selectImage() }
        binding.buttonSubmit.setOnClickListener { uploadData() }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setTheme(R.style.CustomDatePickerTheme)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selectedDate
            }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            editText.setText("$day/$month/$year")
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun selectImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                binding.imageView11.setImageURI(selectedImageUri)
            }
        }

    private fun uploadData() {
        val name = binding.editTextNama.text.toString().trim()
        val purpose = binding.editTextKeperluan.text.toString().trim()
        val inputDate = binding.editTextTanggal.text.toString().trim()
        val transactionDate = binding.editTextTanggalTrans.text.toString().trim()
        val amountText =
            binding.editTextJumlah.text.toString().trim().replace("[Rp,.\\s]".toRegex(), "")
        val amount = amountText.toDoubleOrNull()
        val source = binding.spinnerTujuan.selectedItem.toString().trim()
        val pic = binding.editTextPIC.text.toString().trim()
        val status = "undone" // Or any other default value
        val transactionType = when (binding.radioGroupTransaksi.checkedRadioButtonId) {
            R.id.Masuk -> "in"
            R.id.Keluar -> "out"
            else -> ""
        }

        if (name.isEmpty() || purpose.isEmpty() || inputDate.isEmpty() || transactionDate.isEmpty() || amount == null || source.isEmpty() || pic.isEmpty()) {
            Toast.makeText(
                requireActivity(),
                "Please fill all fields and provide a valid amount",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val isUpdate = arguments?.getString("EXTRA_ID") != null
        val selectedImageUriString = arguments?.getString("EXTRA_IMAGE_URI")
        if (!isUpdate && selectedImageUri == null) {
            Toast.makeText(requireActivity(), "Please select an image to upload.", Toast.LENGTH_SHORT).show()
            return
        }
        val file: File? = selectedImageUri?.let { uri ->
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                val file = File(requireActivity().cacheDir, "uploaded_image.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                file
            } catch (e: FileNotFoundException) {
                null
            } catch (e: IOException) {
                null
            }
        }


        if (isUpdate) {
            val id = arguments?.getString("EXTRA_ID") ?: ""
            viewModel.updateImprest(
                id,
                name,
                inputDate,
                purpose,
                transactionDate,
                amount,
                source,
                pic,
                file,
                status,
                transactionType
            )
        } else {
            if (file == null) {
                Toast.makeText(
                    requireActivity(),
                    "Please select an image to upload.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            viewModel.uploadData(
                name,
                inputDate,
                purpose,
                transactionDate,
                amount,
                source,
                pic,
                file,
                transactionType,
                status

            )
        }



        val detailImprestFragment = DetailImprestFragment()
        val bundle = Bundle().apply {
            putString("EXTRA_ID", id.toString())
            putString("EXTRA_NAME", name)
            putDouble("EXTRA_AMOUNT", amount)
            putString("EXTRA_INPUT_DATE", inputDate)
            putString("EXTRA_SOURCE", source)
            putString("EXTRA_TRANSACTION_DATE", transactionDate)
            putString("EXTRA_PIC", pic)
            putString("EXTRA_PURPOSE", purpose)
            putString("EXTRA_IMAGE_URI", selectedImageUri.toString())
            putString("EXTRA_STATUS", status)
            putBoolean("EXTRA_CAN_EDIT", false)
            putBoolean("EXTRA_FROM_INPUT", true)
        }
        Log.d("BundleCheck", "Purpose to be sent: $purpose")

            detailImprestFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailImprestFragment)
                .addToBackStack(null)
                .commit()
    }


}