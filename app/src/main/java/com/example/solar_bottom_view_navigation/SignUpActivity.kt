package com.example.solar_bottom_view_navigation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.solar_bottom_view_navigation.R
import com.example.solar_bottom_view_navigation.LoginActivity
import com.example.solar_bottom_view_navigation.ui.signup.SignUpViewModel

class SignUpActivity : AppCompatActivity() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            signUpViewModel.signUp(email, password)
        }

        // Navigate back to LoginActivity
        val backToLoginButton: TextView = findViewById(R.id.tvAlreadyHaveAccount)
        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the SignUpActivity
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        signUpViewModel.signUpStatus.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                // Optionally navigate to LoginActivity or MainActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Close SignUpActivity
            } else {
                Toast.makeText(this, "Sign-Up Failed", Toast.LENGTH_SHORT).show()
            }
        })

        signUpViewModel.errorMessage.observe(this, Observer { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
