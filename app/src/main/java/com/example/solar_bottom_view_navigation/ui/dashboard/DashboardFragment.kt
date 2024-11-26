package com.example.solar_bottom_view_navigation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.Toast
import com.example.solar_bottom_view_navigation.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private lateinit var switch1: ImageView
    private lateinit var switch2: ImageView
    private lateinit var lightBulb1: ImageView
    private lateinit var lightBulb2: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize the switches and light bulb images
        switch1 = view.findViewById(R.id.switch1)
        switch2 = view.findViewById(R.id.switch2)
        lightBulb1 = view.findViewById(R.id.lightBulb1)
        lightBulb2 = view.findViewById(R.id.lightBulb2)

        // Load initial states of switches from Firestore
        loadSwitchStates()

        // Set up switch 1 listener
        switch1.setOnClickListener {
            val isOn = switch1.drawable.constantState == resources.getDrawable(R.drawable.switch_off).constantState
            toggleSwitch(1, isOn)
        }

        // Set up switch 2 listener
        switch2.setOnClickListener {
            val isOn = switch2.drawable.constantState == resources.getDrawable(R.drawable.switch_off).constantState
            toggleSwitch(2, isOn)
        }

        return view
    }

    private fun toggleSwitch(switchNumber: Int, turnOn: Boolean) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = db.collection("users").document(userId)
        val switchStateField = "switch$switchNumber"

        // Update UI based on switch state
        if (switchNumber == 1) {
            switch1.setImageResource(if (turnOn) R.drawable.switch_on else R.drawable.switch_off)
            lightBulb1.setImageResource(if (turnOn) R.drawable.bulb_on else R.drawable.bulb_off)
        } else if (switchNumber == 2) {
            switch2.setImageResource(if (turnOn) R.drawable.switch_on else R.drawable.switch_off)
            lightBulb2.setImageResource(if (turnOn) R.drawable.bulb_on else R.drawable.bulb_off)
        }

        // Update Firestore with the new switch state
        userRef.update(switchStateField, turnOn)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Switch $switchNumber updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error updating switch $switchNumber: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadSwitchStates() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val switch1State = document.getBoolean("switch1") ?: false
                    val switch2State = document.getBoolean("switch2") ?: false

                    // Set the initial state of switch 1 and light bulb 1
                    switch1.setImageResource(if (switch1State) R.drawable.switch_on else R.drawable.switch_off)
                    lightBulb1.setImageResource(if (switch1State) R.drawable.bulb_on else R.drawable.bulb_off)

                    // Set the initial state of switch 2 and light bulb 2
                    switch2.setImageResource(if (switch2State) R.drawable.switch_on else R.drawable.switch_off)
                    lightBulb2.setImageResource(if (switch2State) R.drawable.bulb_on else R.drawable.bulb_off)
                } else {
                    // Document does not exist, initialize with default values
                    val initialData = mapOf("switch1" to false, "switch2" to false)
                    userRef.set(initialData)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading switch states: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
