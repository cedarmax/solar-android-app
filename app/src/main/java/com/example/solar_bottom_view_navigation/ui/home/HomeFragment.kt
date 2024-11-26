package com.example.solar_bottom_view_navigation.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.solar_bottom_view_navigation.LoginActivity
import com.example.solar_bottom_view_navigation.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userNameTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        userNameTextView = view.findViewById(R.id.tvUserName)
        logoutButton = view.findViewById(R.id.btnLogout)

        // Display the user's name
        displayUserName()

        // Set up the logout button
        logoutButton.setOnClickListener {
            logOutUser()
        }

        return view
    }

    private fun displayUserName() {
        // Get the currently logged-in user
        val user: FirebaseUser? = auth.currentUser

        if (user != null) {
            // If the user is logged in, get their display name or email
            val userName = user.displayName ?: user.email
            userNameTextView.text = "Welcome, $userName"
        } else {
            // If no user is logged in, show a default message
            userNameTextView.text = "Welcome, Guest!"
        }
    }

    private fun logOutUser() {
        // Sign out from Firebase
        auth.signOut()

        // Show a confirmation message
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Redirect to LoginActivity (or any other activity you want to show after logout)
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)

        // Finish the parent activity to clear the back stack
        activity?.finish()
    }
}
