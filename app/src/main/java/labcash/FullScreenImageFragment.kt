package com.example.aicomsapp.viewmodels.labcash

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aicomsapp.databinding.FragmentFullScreenImageBinding

class FullScreenImageFragment : Fragment() {

    private var _binding: FragmentFullScreenImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the image URI from the arguments
        val imageUri = arguments?.getString("IMAGE_URI") ?: return

        // Set the image URI to ImageView
        binding.fullScreenImageView.setImageURI(Uri.parse(imageUri))

        // Optional: Close fragment on image click
        binding.fullScreenImageView.setOnClickListener {
            parentFragmentManager.popBackStack() // Closes the fragment when tapped
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(imageUri: String): FullScreenImageFragment {
            val fragment = FullScreenImageFragment()
            val args = Bundle()
            args.putString("IMAGE_URI", imageUri)
            fragment.arguments = args
            return fragment
        }
    }
}

