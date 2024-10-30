package com.example.aicomsapp.viewmodels.detailpageimprest

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.aicomsapp.R
import com.example.aicomsapp.databinding.FragmentDetailPageImprestBinding
import com.example.aicomsapp.viewmodels.dashboard.DashboardCombinedFragment
import com.example.aicomsapp.viewmodels.inputimprest.InputImprestFragment
import java.text.NumberFormat
import java.util.Locale

class DetailImprestFragment : Fragment() {

    private lateinit var binding: FragmentDetailPageImprestBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailPageImprestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)


        // Retrieve the data passed through arguments
        val fromInput = arguments?.getBoolean("EXTRA_FROM_INPUT", false) ?: false
        val id = arguments?.getString("EXTRA_ID")?: ""
        val name = arguments?.getString("EXTRA_NAME") ?: ""
        val amount = arguments?.getDouble("EXTRA_AMOUNT")?: 0.0
        val inputDate = arguments?.getString("EXTRA_INPUT_DATE") ?: ""
        val purpose = arguments?.getString("EXTRA_PURPOSE")?: ""
        val source = arguments?.getString("EXTRA_SOURCE") ?: ""
        val transactionDate = arguments?.getString("EXTRA_TRANSACTION_DATE") ?: ""
        val pic = arguments?.getString("EXTRA_PIC") ?: ""
        val photoUri = arguments?.getString("EXTRA_IMAGE_URI") ?: ""
        val canEdit = arguments?.getBoolean("EXTRA_CAN_EDIT", true) ?: true
        Log.d("DetailImprestFragment", "ID: $id")
        if (photoUri != null && photoUri.isNotEmpty()) {
            Glide.with(this)
                .load(photoUri)
                .into(binding.ivUploadedPhotoimprest)
        } else {
            // Handle case where photoUrl is null or empty
            Log.d("DetailImprestFragment", "Photo URL is null or empty")
        }

        viewModel.setUserData(name, amount, inputDate, purpose, source, transactionDate, pic, photoUri)
        val formattedAmount = NumberFormat.getInstance(Locale("id", "ID")).format(amount)

        // Set the retrieved data to the views
        binding.tvNameimprest.text = name
        binding.tvAmountimprest.text = "+ Rp$formattedAmount"
        binding.tvInputDateimprest.text = "Tanggal Input: $inputDate"
        binding.tvPurposeimprest.text = "Sumber/Tujuan: $source"
        binding.tvTransactionDateimprest.text = "Tanggal Transaksi: $transactionDate"
        binding.tvPICimprest.text = "PIC: $pic"
        Log.d("DetailImprestFragment", "Purpose received: $purpose")
        binding.tvDescriptionimprest.text = "Keterangan: $purpose"
        binding.ivUploadedPhotoimprest.setImageURI(Uri.parse(photoUri))
        Log.d("DetailImprestFragment", "Photo URI: $photoUri")

        if (!canEdit) {
            binding.editButtonimprest.visibility = View.GONE // or disable it with isEnabled = false
        }
        if (fromInput) {
            // Ini berasal dari input baru, maka lakukan countdown
            Handler(Looper.getMainLooper()).postDelayed({
                // Navigasi kembali ke Dashboard setelah beberapa detik
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DashboardCombinedFragment())
                    .commit()
            }, 3000)  // 3000 milliseconds = 3 detik
        }

        // Handle the pencil button click to open InputImprestFragment for editing
        binding.editButtonimprest.setOnClickListener {
            val inputImprestFragment = InputImprestFragment()

            // Bundle to pass the data to InputImprestFragment
            val bundle = Bundle().apply {
                putString("EXTRA_ID", id)
                putString("EXTRA_NAME", name)
                putDouble("EXTRA_AMOUNT", amount)
                putString("EXTRA_INPUT_DATE", inputDate)
                putString("EXTRA_SOURCE", source)
                putString("EXTRA_TRANSACTION_DATE", transactionDate)
                putString("EXTRA_PIC", pic)
                putString("EXTRA_PURPOSE", purpose)
                putString("EXTRA_IMAGE_URI", photoUri)
                putBoolean("EXTRA_FROM_DETAIL", true)
            }

            inputImprestFragment.arguments = bundle

            // Use FragmentTransaction to replace current fragment with InputImprestFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, inputImprestFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.ivUploadedPhotoimprest.setOnClickListener {
        Log.d("DetailLabFragment", "Photo URI for enlargement: $photoUri")
        showEnlargedImageDialog(photoUri)
    }
}

private fun showEnlargedImageDialog(photoUri: String) {
        val enlargedImageDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_enlarge_detail, null)
        val enlargedImageView = enlargedImageDialogView.findViewById<ImageView>(R.id.enlargedImageView)
        val btnCloseDialog = enlargedImageDialogView.findViewById<ImageView>(R.id.btnCloseDialog)

        if (photoUri.isNotEmpty()) {
            Glide.with(this)
                .load(photoUri)
                .error(R.drawable.error_404)
                .into(enlargedImageView)
        } else {
            Log.e("DetailLabFragment", "Error: photoUri is empty")
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(enlargedImageDialogView)
            .create()

        // Close dialog on button click
        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
