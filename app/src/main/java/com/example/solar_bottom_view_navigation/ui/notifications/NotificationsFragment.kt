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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_bottom_view_navigation.R
import com.example.solar_bottom_view_navigation.databinding.FragmentNotificationsBinding
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

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var selectedDateTime: Calendar? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val view = binding.root


        switchSpinner = view.findViewById(R.id.switchSpinner)
        switchState = view.findViewById(R.id.switchState)
        dateButton = view.findViewById(R.id.dateButton)
        timeButton = view.findViewById(R.id.timeButton)
        dateTimePreview = view.findViewById(R.id.dateTimePreview)
        saveButton = view.findViewById(R.id.saveButton)

        setupSpinner()
        setupButtons()

        val recyclerView = view.findViewById<RecyclerView>(R.id.scheduleRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadSchedules(recyclerView)

        return view
    }

    private fun setupSpinner() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val label1 = document.getString("label1") ?: "Switch 1"
                val label2 = document.getString("label2") ?: "Switch 2"

                val switchMap = mapOf(
                    label1 to "switch1",
                    label2 to "switch2"
                )

                val labels = switchMap.keys.toList()
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labels)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.switchSpinner.adapter = adapter

                // Optional: save the mapping if needed elsewhere
                binding.switchSpinner.tag = switchMap
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load switch labels", Toast.LENGTH_SHORT).show()
        }
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
        val selectedLabel = binding.switchSpinner.selectedItem as String
        val switchMap = binding.switchSpinner.tag as? Map<String, String>
        val selectedSwitchKey = switchMap?.get(selectedLabel) ?: "switch1"

        val switchIndex = when (selectedSwitchKey) {
            "switch1" -> 1
            "switch2" -> 2
            else -> 1
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val isOn = switchState.isChecked
        val timestamp = selectedDateTime?.timeInMillis?.let { Timestamp(Date(it)) }

        if (timestamp == null) {
            Toast.makeText(requireContext(), "Please select a date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val scheduleData = hashMapOf(
            "switch" to switchIndex,
            "turnOn" to isOn,
            "time" to timestamp
        )

        db.collection("users")
            .document(userId)
            .collection("schedules")
            .add(scheduleData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Schedule saved", Toast.LENGTH_SHORT).show()
                loadSchedules(requireView().findViewById(R.id.scheduleRecyclerView))

                // 🔁 Only reset on success
                binding.switchSpinner.setSelection(0)
                binding.switchState.isChecked = false
                selectedDateTime = null
                binding.dateTimePreview.text = "Scheduled: not set" // safe to clear now
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save schedule: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    sealed class ScheduleListItem {
        data class Header(val title: String) : ScheduleListItem()
        data class Entry(
            val switchKey: String,
            val switchLabel: String,
            val state: Boolean,
            val timeMillis: Long,
            val docId: String,
            val isPast: Boolean // <-- new
        ) : ScheduleListItem()


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun loadSchedules(recyclerView: RecyclerView) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(userId)
        val schedulesRef = userDocRef.collection("schedules")

        // First: get labels
        userDocRef.get().addOnSuccessListener { userDoc ->
            val label1 = userDoc.getString("label1") ?: "Switch 1"
            val label2 = userDoc.getString("label2") ?: "Switch 2"

            // Then: load schedules
            schedulesRef.get().addOnSuccessListener { snapshot ->
                val now = System.currentTimeMillis()
                val past = mutableListOf<ScheduleListItem.Entry>()
                val future = mutableListOf<ScheduleListItem.Entry>()

                for (doc in snapshot.documents) {
                    val switchIndex = doc.getLong("switch")?.toInt() ?: continue
                    val turnOn = doc.getBoolean("turnOn") ?: false
                    val time = doc.getTimestamp("time")?.toDate()?.time ?: continue

                    val switchKey = "switch$switchIndex"
                    val label = when (switchIndex) {
                        1 -> label1
                        2 -> label2
                        else -> "Switch $switchIndex"
                    }

                    val isPast = time < now
                    val entry = ScheduleListItem.Entry(
                        switchKey = switchKey,
                        switchLabel = label,
                        state = turnOn,
                        timeMillis = time,
                        docId = doc.id,
                        isPast = isPast // <-- new
                    )



                    if (time < now) past.add(entry) else future.add(entry)
                }

                val finalList = mutableListOf<ScheduleListItem>()
                if (future.isNotEmpty()) {
                    finalList.add(ScheduleListItem.Header("Upcoming"))
                    finalList.addAll(future.sortedBy { it.timeMillis })
                }
                if (past.isNotEmpty()) {
                    finalList.add(ScheduleListItem.Header("Past"))
                    finalList.addAll(past.sortedByDescending { it.timeMillis })
                }

                recyclerView.adapter = ScheduleAdapter(finalList) {
                    loadSchedules(recyclerView)
                }

            }
        }
    }





}
