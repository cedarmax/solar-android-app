package com.example.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.solar_bottom_view_navigation.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val forgotPasswordTextView: TextView = view.findViewById(R.id.forgotPasswordTextView)
        val signUpTextView: TextView = view.findViewById(R.id.signUpTextView)

        // Set click listeners for login, forgot password, and sign-up actions
        loginButton.setOnClickListener {
            // Implement login logic here
        }

        forgotPasswordTextView.setOnClickListener {
            // Navigate to Forgot Password screen
        }

        signUpTextView.setOnClickListener {
            // Navigate to Sign Up screen
        }

        return view
    }
}
