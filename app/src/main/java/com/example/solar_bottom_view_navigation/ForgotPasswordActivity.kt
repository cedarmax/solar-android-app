package com.example.solar_bottom_view_navigation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app.ui.LoginFragment

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_forgotpassword)

        val emailEditText: EditText = findViewById(R.id.etEmail)
        val sendLinkButton: Button = findViewById(R.id.btnSendLink)
        val backToLoginText: TextView = findViewById(R.id.tvBackToLogin)

        sendLinkButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordResetLink(email)
            }
        }

        backToLoginText.setOnClickListener {
            startActivity(Intent(this, LoginFragment::class.java))
            finish()
        }
    }

    private fun sendPasswordResetLink(email: String) {
        // Placeholder for sending a reset link
        // Ideally, integrate Firebase Authentication or your backend API to handle the email sending

        Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()
    }
}
