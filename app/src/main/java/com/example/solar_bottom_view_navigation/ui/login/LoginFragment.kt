package com.example.solar_bottom_view_navigation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.solar_bottom_view_navigation.R
import com.example.solar_bottom_view_navigation.ui.login.LoginViewModel

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)

        val loginButton = view.findViewById<View>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginViewModel.login(email, password)
        }

        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()
                // Navigate to Home Fragment or Dashboard
            } else {
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
            }
        })

        loginViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
