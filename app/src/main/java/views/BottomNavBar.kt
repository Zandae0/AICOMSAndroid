package com.example.aicomsapp.viewmodels.views

import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.example.aicomsapp.R

class BottomNavBar(
    private val bottomBar: View,
    private val listener: BottomNavListener
) {

    init {
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val homeButton: ImageButton = bottomBar.findViewById(R.id.home_button)
        val userControlButton: ImageButton = bottomBar.findViewById(R.id.user_control)
        val fabPlusButton: View = bottomBar.findViewById(R.id.fab_plus)
        val kasAicomsButton: ImageButton = bottomBar.findViewById(R.id.kas_aicoms_button)
        val noteButton: ImageButton = bottomBar.findViewById(R.id.note_button)

        homeButton.setOnClickListener {
            Toast.makeText(bottomBar.context, "Anda Sekarang di Dashboard", Toast.LENGTH_SHORT).show()
            listener.onHomeClicked()
        }

        userControlButton.setOnClickListener {
            Toast.makeText(bottomBar.context, "Anda Sekarang di User Control", Toast.LENGTH_SHORT).show()
            listener.onUserControlClicked()
        }

        fabPlusButton.setOnClickListener {
            Toast.makeText(bottomBar.context, "Silahkan Input Data", Toast.LENGTH_SHORT).show()
            listener.onFabPlusClicked()
        }

        kasAicomsButton.setOnClickListener {
            Toast.makeText(bottomBar.context, "Kas AICOMS Clicked", Toast.LENGTH_SHORT).show()
            listener.onLabCashClicked()
            // Implement functionality for Kas AICOMS
        }

        noteButton.setOnClickListener {
            Toast.makeText(bottomBar.context, "Note Button Clicked", Toast.LENGTH_SHORT).show()
            listener.onNoteClicked()
            // Implement functionality for Note
        }
    }
}
