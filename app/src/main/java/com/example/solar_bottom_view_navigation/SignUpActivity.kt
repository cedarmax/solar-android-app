package com.example.solar_bottom_view_navigation

import android.content.Intent
import android.util.Patterns
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val passwordToggle = findViewById<Button>(R.id.passwordToggle)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val passwordRequirementsTextView = findViewById<TextView>(R.id.passwordRequirementsTextView)
        val loginRedirectText = findViewById<TextView>(R.id.loginRedirectText) // Make sure this ID exists in your XML

        // Password visibility toggle logic
        passwordToggle.setOnClickListener {
            if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.text = "🔒"
            } else {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.text = "👁️"
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Login redirect text logic
        loginRedirectText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Sign-up button logic
        signUpButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length < 8 || !password.matches(".*\\d.*".toRegex()) || !password.matches(".*[!@#\$%^&*(),.?\":{}|<>].*".toRegex())) {
                    passwordRequirementsTextView.text = "Password must be at least 8 characters, contain a number and a special character."
                    Toast.makeText(this, "Invalid password. Please meet the requirements.", Toast.LENGTH_SHORT).show()
                } else {
                    passwordRequirementsTextView.text = ""

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                val userMap = hashMapOf(
                                    "firstName" to firstName,
                                    "lastName" to lastName,
                                    "email" to email
                                )

                                if (userId != null) {
                                    db.collection("users").document(userId).set(userMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Sign-up successful! Please log in.", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Failed to save user info", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
