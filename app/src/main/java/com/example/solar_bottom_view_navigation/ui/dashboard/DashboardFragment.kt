package com.example.solar_bottom_view_navigation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.solar_bottom_view_navigation.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private lateinit var switch1: ImageView
    private lateinit var switch2: ImageView
    private lateinit var lightBulb1: ImageView
    private lateinit var lightBulb2: ImageView
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize UI elements
        switch1 = view.findViewById(R.id.switch1)
        switch2 = view.findViewById(R.id.switch2)
        lightBulb1 = view.findViewById(R.id.lightBulb1)
        lightBulb2 = view.findViewById(R.id.lightBulb2)
        editText1 = view.findViewById(R.id.editText1)
        editText2 = view.findViewById(R.id.editText2)

        // Listen for real-time changes from Firebase
        observeSwitchStates()
        observeLabels()

        // Set up switch click listeners
        switch1.setOnClickListener { toggleSwitch(1) }
        switch2.setOnClickListener { toggleSwitch(2) }

        // Save labels to Firebase when focus is lost
        editText1.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) saveLabel(1) }
        editText2.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) saveLabel(2) }

        return view
    }

    private fun toggleSwitch(switchNumber: Int) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)
        val switchField = "switch$switchNumber"

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentState = document.getBoolean(switchField) ?: false
                userRef.update(switchField, !currentState)
            }
        }
    }

    private fun saveLabel(labelNumber: Int) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)
        val labelField = "label$labelNumber"
        val newText = if (labelNumber == 1) editText1.text.toString() else editText2.text.toString()

        userRef.update(labelField, newText).addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to update label", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeSwitchStates() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)

        userRef.addSnapshotListener { document, _ ->
            if (document != null && document.exists()) {
                val switch1State = document.getBoolean("switch1") ?: false
                val switch2State = document.getBoolean("switch2") ?: false

                switch1.setImageResource(if (switch1State) R.drawable.switch_on else R.drawable.switch_off)
                lightBulb1.setImageResource(if (switch1State) R.drawable.bulb_on else R.drawable.bulb_off)

                switch2.setImageResource(if (switch2State) R.drawable.switch_on else R.drawable.switch_off)
                lightBulb2.setImageResource(if (switch2State) R.drawable.bulb_on else R.drawable.bulb_off)
            }
        }
    }

    private fun observeLabels() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)

        userRef.addSnapshotListener { document, _ ->
            if (document != null && document.exists()) {
                editText1.setText(document.getString("label1") ?: "Switch 1")
                editText2.setText(document.getString("label2") ?: "Switch 2")
            }
        }
    }
}
