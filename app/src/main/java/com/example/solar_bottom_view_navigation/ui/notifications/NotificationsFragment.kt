package com.example.solar_bottom_view_navigation.ui.notifications

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.solar_bottom_view_navigation.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NotificationsFragment : Fragment() {

    private lateinit var switchSpinner: Spinner
    private lateinit var switchState: SwitchCompat
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var dateTimePreview: TextView
    private lateinit var saveButton: Button

    private var selectedDateTime: Calendar? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        switchSpinner = view.findViewById(R.id.switchSpinner)
        switchState = view.findViewById(R.id.switchState)
        dateButton = view.findViewById(R.id.dateButton)
        timeButton = view.findViewById(R.id.timeButton)
        dateTimePreview = view.findViewById(R.id.dateTimePreview)
        saveButton = view.findViewById(R.id.saveButton)

        setupSpinner()
        setupButtons()

        return view
    }

    private fun setupSpinner() {
        val switches = listOf("Switch 1", "Switch 2")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, switches)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        switchSpinner.adapter = adapter
    }

    private fun setupButtons() {
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                if (selectedDateTime == null) selectedDateTime = Calendar.getInstance()
                selectedDateTime!!.set(year, month, dayOfMonth)
                updatePreview()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        timeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                if (selectedDateTime == null) selectedDateTime = Calendar.getInstance()
                selectedDateTime!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateTime!!.set(Calendar.MINUTE, minute)
                updatePreview()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        saveButton.setOnClickListener {
            saveSchedule()
        }
    }

    private fun updatePreview() {
        selectedDateTime?.let {
            val dateStr = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", it)
            dateTimePreview.text = "Scheduled: $dateStr"
        }
    }

    private fun saveSchedule() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val switchName = switchSpinner.selectedItem.toString()
        val isOn = switchState.isChecked
        val timestamp = selectedDateTime?.timeInMillis?.let { Timestamp(Date(it)) } ?: return

        val scheduleData = hashMapOf(
            "switch" to switchName,
            "turnOn" to isOn,
            "time" to timestamp
        )

        db.collection("users")
            .document(userId)
            .collection("schedules")
            .add(scheduleData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Schedule saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save schedule", Toast.LENGTH_SHORT).show()
            }
    }
}
