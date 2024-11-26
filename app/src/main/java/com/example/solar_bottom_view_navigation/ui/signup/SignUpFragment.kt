package com.example.solar_bottom_view_navigation.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.solar_bottom_view_navigation.R
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)

        val signUpButton = view.findViewById<View>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            signUpViewModel.signUp(email, password)
        }
        // Navigate back to LoginFragment
        val backToLoginButton: TextView = view.findViewById(R.id.tvAlreadyHaveAccount)
        backToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        signUpViewModel.signUpStatus.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                // Navigate to Login Fragment
            } else {
                Toast.makeText(requireContext(), "Sign-Up Failed", Toast.LENGTH_SHORT).show()
            }
        })

        signUpViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
