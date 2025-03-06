package com.example.solar_bottom_view_navigation

import android.content.Intent
import android.util.Patterns
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.solar_bottom_view_navigation.ui.signup.SignUpViewModel
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val passwordToggle = findViewById<Button>(R.id.passwordToggle)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val passwordRequirementsTextView = findViewById<TextView>(R.id.passwordRequirementsTextView)

        // Password visibility toggle logic
        passwordToggle.setOnClickListener {
            if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.text = "🔒" // Change button text to "locked"
            } else {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.text = "👁️" // Change button text to "eye"
            }
            // To ensure the cursor stays at the end of the password when toggling
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Sign-up button logic
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Check password strength
                if (password.length < 8 || !password.matches(".*\\d.*".toRegex()) || !password.matches(".*[!@#\$%^&*(),.?\":{}|<>].*".toRegex())) {
                    // Show password requirements in the TextView if the password doesn't meet the criteria
                    passwordRequirementsTextView.text = "Password must be at least 8 characters, contain a number and a special character."
                } else {
                    // Perform the sign-up logic
                    passwordRequirementsTextView.text = "" // Clear the message
                    Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
