//class InputImprestFragment : Fragment() {
//
//    private lateinit var editTextNama: EditTextNama
//    private lateinit var editTextTanggal: EditTextTanggal
//    private lateinit var editTextKeperluan: EditTextKeperluan
//    private lateinit var editTextTanggalTrans: EditTextTanggalTransaksi
//    private lateinit var editTextJumlah: EditText
//    private lateinit var editTextTujuan: EditTextKeperluan
//    private lateinit var editTextPIC: EditTextKeperluan
//    private lateinit var imageViewUpload: ImageView
//    private lateinit var buttonUpload: Button
//    private lateinit var tipetransaksi: RadioButton
//    private lateinit var spinnerTujuan: Spinner
//    private lateinit var radioGroupTransaksi: RadioGroup
//    private lateinit var radioMasuk: RadioButton
//    private lateinit var radioKeluar: RadioButton
//    private var selectedImageUri: Uri? = null
//    private var status: String = "undone" // default status is "undone"
//    private lateinit var viewModel: InputViewModelimprest
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_input, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize views
//        editTextNama = view.findViewById(R.id.editTextNama)
//        editTextTanggal = view.findViewById(R.id.editTextTanggal)
//        editTextKeperluan = view.findViewById(R.id.editTextKeperluan)
//        editTextTanggalTrans = view.findViewById(R.id.editTextTanggalTrans)
//        editTextJumlah = view.findViewById(R.id.editTextJumlah)
//        spinnerTujuan = view.findViewById(R.id.spinnerTujuan)
//        editTextPIC = view.findViewById(R.id.editTextPIC)
//        imageViewUpload = view.findViewById(R.id.imageView11)
//        buttonUpload = view.findViewById(R.id.buttonSubmit)
//        radioGroupTransaksi = view.findViewById(R.id.radioGroupTransaksi)
//        radioMasuk = view.findViewById(R.id.Masuk)
//        radioKeluar = view.findViewById(R.id.Keluar)
//        val roleAdapter = ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.role, // your array defined in strings.xml
//            R.layout.spinner_selected_item // custom layout for selected item (main view)
//        )
//
//        // Set custom layout for the dropdown view as well
//        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
//        spinnerTujuan.adapter = roleAdapter
//
//        arguments?.let { bundle ->
//            val name = bundle.getString("EXTRA_NAME")
//            val amount = bundle.getDouble("EXTRA_AMOUNT", 0.0)
//            val inputDate = bundle.getString("EXTRA_INPUT_DATE", "")
//            val source = bundle.getString("EXTRA_SOURCE")
//            val transactionDate = bundle.getString("EXTRA_TRANSACTION_DATE")
//            val pic = bundle.getString("EXTRA_PIC")
//            val description = bundle.getString("EXTRA_DESCRIPTION")
//            val photoUri = bundle.getString("EXTRA_IMAGE_URI")
//
//            // Set the retrieved data to the input fields
//            editTextNama.setText(name)
//
//            // Hapus sementara TextWatcher untuk mencegah masalah saat setText
//            editTextJumlah.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
//
//            editTextJumlah.addTextChangedListener(object : TextWatcher {
//                private var isUpdating = false // Flag to prevent recursive updates
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    // No action needed
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    Log.d("EditTextWatcher", "onTextChanged called with: $s")
//                    if (isUpdating) return // Skip if already updating
//
//                    isUpdating = true // Set flag to prevent recursion
//
//                    // Remove any non-numeric characters (except for decimals)
//                    val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")
//
//                    if (cleanString.isNotEmpty()) {
//                        try {
//                            // Parse the clean string to a long for formatting
//                            val parsed = cleanString.toLong()
//
//                            // Format the number to Indonesian locale with thousand separators
//                            val formatted = NumberFormat.getInstance(Locale("id", "ID")).format(parsed)
//
//                            // Set the formatted text with "Rp" prefix
//                            val newText = "Rp $formatted"
//
//                            // Update the EditText with the new formatted value
//                            editTextJumlah.setText(newText)
//
//                            // Move the cursor to the end of the text
//                            editTextJumlah.setSelection(newText.length)
//                        } catch (e: NumberFormatException) {
//                            e.printStackTrace() // Handle invalid number format
//                        }
//                    }
//
//                    isUpdating = false // Reset the flag after update
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//                    // No action needed
//                }
//            })
//
//
//
//            // Tambahkan kembali TextWatcher
//
//            // Set nilai lainnya
//            editTextTanggal.setText(inputDate)
//            editTextTanggalTrans.setText(transactionDate)
//            editTextKeperluan.setText(description)
//            editTextPIC.setText(pic)
//            Log.d("DetailImprestFragment", "Amount: $amount, Input Date: $inputDate")
//
//            // Set selected image if available
//            photoUri?.let {
//                selectedImageUri = Uri.parse(it)
//                imageViewUpload.setImageURI(selectedImageUri)
//            }
//            photoUri?.let {
//                // Menggunakan Glide untuk menampilkan gambar di imageView11
//                Glide.with(this)
//                    .load(photoUri)
//                    .into(imageViewUpload)
//            }
//            // Lakukan update UI jika dibutuhkan
//            view.post {
//                editTextTanggal.setText(inputDate) // Tetap gunakan formattedAmount
//            }
//
//
//            // If the transaction type was passed, you can set the appropriate radio button here
//            if (source == "Masuk") {
//                radioMasuk.isChecked = true
//            } else {
//                radioKeluar.isChecked = true
//            }
//
//        }
//
//        val apiService = ApiConfig.getApiService()
//        val userPreference = UserPreference(requireActivity().dataStore)
//
//        // Initialize RetrofitRepository
//        val repository = RetrofitRepository(apiService, userPreference)
//
//        // Initialize ViewModel
//        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                if (modelClass.isAssignableFrom(InputViewModelimprest::class.java)) {
//                    @Suppress("UNCHECKED_CAST")
//                    return InputViewModelimprest(repository) as T
//                }
//                throw IllegalArgumentException("Unknown ViewModel class")
//            }
//        }).get(InputViewModelimprest::class.java)
//
//        // Observe ViewModel status
//        viewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
//            Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
//        }
//
//        // Handle image selection
//        imageViewUpload.setOnClickListener {
//            selectImage()
//        }
//
//        // Handle upload button click
//        buttonUpload.setOnClickListener {
//            uploadData()
//        }
//
//        // Handle date picker
//        editTextTanggalTrans.setOnClickListener {
//            showDatePickerDialog()
//        }
//    }
//
//    private fun selectImage() {
//        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        resultLauncher.launch(intent)
//    }
//
//    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
//            selectedImageUri = result.data?.data
//            imageViewUpload.setImageURI(selectedImageUri)
//        }
//    }
//
//    private fun uploadData() {
//        val name = editTextNama.text.toString().trim()
//        val inputDate = editTextTanggal.text.toString().trim()
//        val purpose = editTextKeperluan.text.toString().trim()
//        val transactionDate = editTextTanggalTrans.text.toString().trim()
//
//        val amountText = editTextJumlah.text.toString().trim().replace("[Rp,\\s]".toRegex(), "")
//        val amount: Double? = amountText.toDoubleOrNull()
//
//        val source = spinnerTujuan.selectedItem.toString().trim()
//        val pic = editTextPIC.text.toString().trim()
//        val transactionType = when (radioGroupTransaksi.checkedRadioButtonId) {
//            R.id.Masuk -> "in"
//            R.id.Keluar -> "out"
//            else -> ""
//        }
//
//        // Check if it's an update (EXTRA_ID exists) or a new upload
//        val isUpdate = arguments?.getString("EXTRA_ID") != null
//
//        // Image is required for new upload, but not for updates
//        if (name.isEmpty() || inputDate.isEmpty() || purpose.isEmpty() || transactionDate.isEmpty() ||
//            amount == null || source.isEmpty() || pic.isEmpty() || transactionType.isEmpty() ||
//            (!isUpdate && selectedImageUri == null)) {
//
//            val message = if (isUpdate) {
//                "Please fill all fields and provide a valid amount"
//            } else {
//                "Please fill all fields, select an image, and provide a valid amount"
//            }
//            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        try {
//            // Create file from selected image URI, only if an image was selected
//            val file: File? = selectedImageUri?.let { uri ->
//                val inputStream = requireActivity().contentResolver.openInputStream(uri)
//                val file = File(requireActivity().cacheDir, "uploaded_image.jpg")
//                inputStream?.use { input ->
//                    file.outputStream().use { output ->
//                        input.copyTo(output)
//                    }
//                }
//                file
//            }
//
//            if (isUpdate) {
//                // If this is an update, proceed without requiring a new image (file can be null)
//                val id = arguments?.getString("EXTRA_ID") ?: ""
//                viewModel.updateImprest(
//                    id,  // Pass the ID for updating
//                    name,
//                    inputDate,
//                    purpose,
//                    transactionDate,
//                    amount,
//                    source,
//                    pic,
//                    file,
//                    transactionType,
//                    status
//                )
//            } else {
//                // For new upload, image is mandatory
//                if (file == null) {
//                    Toast.makeText(requireActivity(), "Please select an image to upload.", Toast.LENGTH_SHORT).show()
//                    return
//                }
//                viewModel.uploadData(
//                    name,
//                    inputDate,
//                    purpose,
//                    transactionDate,
//                    amount,
//                    source,
//                    pic,
//                    file,
//                    transactionType,
//                    status
//                )
//            }
//
//            val detailImprestFragment = DetailImprestFragment()
//            val bundle = Bundle().apply {
//                putString("EXTRA_ID", id.toString())
//                putString("EXTRA_NAME", name)
//                putDouble("EXTRA_AMOUNT", amount)
//                putString("EXTRA_INPUT_DATE", inputDate)
//                putString("EXTRA_SOURCE", source)
//                putString("EXTRA_TRANSACTION_DATE", transactionDate)
//                putString("EXTRA_PIC", pic)
//                putString("EXTRA_DESCRIPTION", purpose)
//                putString("EXTRA_IMAGE_URI", selectedImageUri.toString())
//                putString("EXTRA_STATUS", status)
//                putBoolean("EXTRA_CAN_EDIT", false)
//                putBoolean("EXTRA_FROM_INPUT", true)
//            }
//            detailImprestFragment.arguments = bundle
//
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, detailImprestFragment)
//                .addToBackStack(null)
//                .commit()
//
//        } catch (e: FileNotFoundException) {
//            Toast.makeText(requireActivity(), "Error: Unable to open image file", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        } catch (e: IOException) {
//            Toast.makeText(requireActivity(), "Error: Failed to copy image file", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }
//
//    private fun showDatePickerDialog() {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(
//            requireActivity(),
//            { _, selectedYear, selectedMonth, selectedDay ->
//                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
//                editTextTanggalTrans.setText(formattedDate)
//            },
//            year, month, day
//        )
//        datePickerDialog.show()
//    }
//}
//
//inputimprestfragment.kt
//
//ubah kodingan inputimprestfragment menggunakan binding, atau kodingan ini dipersingkat tapi fungsinya sama
