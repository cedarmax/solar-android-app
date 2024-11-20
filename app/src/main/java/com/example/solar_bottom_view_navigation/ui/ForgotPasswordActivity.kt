package com.example.solar_bottom_view_navigation.ui

    import android.os.Bundle
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.example.solar_bottom_view_navigation.ui.databinding.ActivityForgotPasswordBinding
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore


    class ForgotPasswordActivity : AppCompatActivity() {

        private lateinit var binding: ActivityForgotPasswordBinding
        private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
            setContentView(binding.root)

            auth = FirebaseAuth.getInstance()

            binding.btnResetPassword.setOnClickListener {
                val email = binding.etEmail.text.toString()
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error in sending reset email", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }