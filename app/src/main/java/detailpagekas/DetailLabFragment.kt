package com.example.aicomsapp.viewmodels.detailpagekas

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
import com.example.aicomsapp.databinding.FragmentDetailPageKasBinding
import com.example.aicomsapp.viewmodels.dashboard.DashboardCombinedFragment
import com.example.aicomsapp.viewmodels.inputkas.InputLabFragment
import com.example.aicomsapp.viewmodels.labcash.LabCashFragment
import java.text.NumberFormat
import java.util.Locale

class DetailLabFragment : Fragment() {

    private lateinit var binding: FragmentDetailPageKasBinding
    private lateinit var viewModel: DetailLabViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailPageKasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DetailLabViewModel::class.java)

        // Retrieve the data passed through arguments
        val fromInput = arguments?.getBoolean("EXTRA_FROM_INPUT", false) ?: false
        val id = arguments?.getString("EXTRA_ID") ?: ""
        val name = arguments?.getString("EXTRA_NAME") ?: ""
        val amount = arguments?.getDouble("EXTRA_AMOUNT") ?: 0.0
        val inputDate = arguments?.getString("EXTRA_INPUT_DATE") ?: ""
        val Source = arguments?.getString("EXTRA_SOURCE") ?: ""
        val photoUri = arguments?.getString("EXTRA_IMAGE_URI") ?: ""
        val transactionType = arguments?.getString("EXTRA_TRANSACTION_TYPE") ?: ""
        Log.d("DetailImprestFragment", "ID: $id")
        val canEdit = arguments?.getBoolean("EXTRA_CAN_EDIT", true) ?: true
        if (photoUri.isNotEmpty()) {
            Glide.with(this)
                .load(photoUri)
                .into(binding.ivUploadedPhotolab)
        } else {
            // Handle case where photoUrl is null or empty
            Log.d("DetailImprestFragment", "Photo URL is null or empty")
        }

        viewModel.setUserData(name, amount, inputDate, Source, photoUri)
        val formattedAmount = NumberFormat.getInstance(Locale("id", "ID")).format(amount)

        // Set the retrieved data to the views
        binding.tvNamelab.text = name
        binding.tvAmountlab.text = "+ Rp$formattedAmount"
        binding.tvInputDatelab.text = "Tanggal Input: $inputDate"
        binding.tvSourcelab.text = "Sumber/Tujuan: $Source"
        binding.ivUploadedPhotolab.setImageURI(Uri.parse(photoUri))
        Log.d("DetailImprestFragment", "Photo URI: $photoUri")

        if (!canEdit) {
            binding.editButtonimprest.visibility = View.GONE // or disable it with isEnabled = false
        }
        if (fromInput) {
            // Ini berasal dari input baru, maka lakukan countdown
            Handler(Looper.getMainLooper()).postDelayed({
                // Navigasi kembali ke Dashboard setelah beberapa detik
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LabCashFragment())
                    .commit()
            }, 3000)  // 3000 milliseconds = 3 detik
        }

        // Handle the pencil button click to open InputImprestFragment for editing
        binding.editButtonimprest.setOnClickListener {
            val inputLabFragment = InputLabFragment()

            // Bundle to pass the data to InputImprestFragment
            val bundle = Bundle().apply {
                putString("EXTRA_ID", id)
                putString("EXTRA_NAME", name)
                putDouble("EXTRA_AMOUNT", amount)
                putString("EXTRA_INPUT_DATE", inputDate)
                putString("EXTRA_SOURCE", Source)
                putString("EXTRA_IMAGE_URI", photoUri)
                putString("EXTRA_TRANSACTION_TYPE", transactionType)
                putBoolean("EXTRA_FROM_DETAIL", true)
            }

            inputLabFragment.arguments = bundle

            // Use FragmentTransaction to replace current fragment with InputImprestFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, inputLabFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.ivUploadedPhotolab.setOnClickListener {
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
