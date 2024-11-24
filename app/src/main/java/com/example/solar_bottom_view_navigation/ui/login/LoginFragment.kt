package com.example.solar_bottom_view_navigation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController // For navigation
import com.example.solar_bottom_view_navigation.R
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        signupTextView = view.findViewById(R.id.signupTextView)
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView)

        // Set up Login Button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up Sign-Up Text
        signupTextView.setOnClickListener {
            navigateToSignUp()
        }

        // Set up Forgot Password Text
        forgotPasswordTextView.setOnClickListener {
            navigateToForgotPassword()
        }

        return view
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to the main screen
                } else {
                    Toast.makeText(requireContext(), "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToSignUp() {
        // Navigate to the Sign-Up Fragment
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

    private fun navigateToForgotPassword() {
        // Navigate to the Forgot Password Fragment
        findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }
}
