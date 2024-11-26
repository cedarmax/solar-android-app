package com.example.solar_bottom_view_navigation.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _signUpStatus = MutableLiveData<Boolean>()
    val signUpStatus: LiveData<Boolean> get() = _signUpStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty."
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signUpStatus.value = true
                } else {
                    _signUpStatus.value = false
                    _errorMessage.value = task.exception?.message
                }
            }
    }
}
