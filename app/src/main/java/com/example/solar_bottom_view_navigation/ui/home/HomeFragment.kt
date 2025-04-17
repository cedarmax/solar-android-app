package com.example.solar_bottom_view_navigation.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.solar_bottom_view_navigation.LoginActivity
import com.example.solar_bottom_view_navigation.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userNameTextView: TextView
    private lateinit var batteryPercentageTextView: TextView
    private lateinit var batteryIcon: ImageView
    private lateinit var lineChart: LineChart
    private lateinit var timePeriodRadioGroup: RadioGroup

    private var selectedTimePeriod: String = "daily" // Default to daily

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val logoutButton = view?.findViewById<Button>(R.id.btnLogout)
        logoutButton?.setOnClickListener {
            // Log out from Firebase Auth
            FirebaseAuth.getInstance().signOut()

            // Confirm log out by checking currentUser
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Log.d("HomeFragment", "User logged out successfully")
            } else {
                Log.d("HomeFragment", "User NOT logged out")
            }

            // Navigate to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        updateCurrentUserInFirestore()
        database = FirebaseDatabase.getInstance()

        userNameTextView = view.findViewById(R.id.tvUserName)
        batteryPercentageTextView = view.findViewById(R.id.tvBatteryPercentage)
        batteryIcon = view.findViewById(R.id.batteryIcon)
        lineChart = view.findViewById(R.id.lineChart)
        timePeriodRadioGroup = view.findViewById(R.id.timePeriodRadioGroup)

        setupChart()
        displayUserName()
        loadPowerStatistics(selectedTimePeriod) // Load default daily data

        // Listen for time period selection changes
        timePeriodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedTimePeriod = when (checkedId) {
                R.id.rbDaily -> "daily"
                R.id.rbWeekly -> "weekly"
                R.id.rbMonthly -> "monthly"
                else -> "daily"
            }
            loadPowerStatistics(selectedTimePeriod)
        }

        return view
    }

    private fun displayUserName() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val userId = user.uid

        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val userRef = firestore.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    userNameTextView.text = "Welcome, $firstName $lastName"
                } else {
                    userNameTextView.text = "Welcome, User"
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting user data", e)
                userNameTextView.text = "Welcome, User"
            }
    }

    private fun setupChart() {
        lineChart.apply {
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = resources.getColor(android.R.color.black, requireContext().theme)
            axisLeft.textColor = resources.getColor(android.R.color.black, requireContext().theme)
            description.isEnabled = false
            legend.textColor = resources.getColor(android.R.color.black, requireContext().theme)
            setBackgroundColor(resources.getColor(android.R.color.white, requireContext().theme))
            //lineChart.xAxis.valueFormatter = TimestampAxisFormatter()
            setNoDataText("No data for selected period")
            setNoDataTextColor(Color.GRAY)
        }
    }

    private fun loadPowerStatistics(timePeriod: String) {
        val userId = auth.currentUser?.uid ?: return
        val dataRef = database.reference.child("users").child(userId).child("ina260_log")
        val now = System.currentTimeMillis() / 1000
        val cutoff: Long = when (timePeriod) {
            "daily" -> now - 24 * 60 * 60
            "weekly" -> now - 7 * 24 * 60 * 60
            "monthly" -> now - 30L * 24 * 60 * 60
            else -> now - 24 * 60 * 60
        }

        // Important: set value formatter here with updated cutoff
        lineChart.xAxis.valueFormatter = TimestampAxisFormatter(cutoff)

        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<Entry>()
                var lastVoltage = 0f

                for (data in snapshot.children) {
                    val timestampKey = data.key?.toLongOrNull() ?: continue
                    Log.d("FirebaseDebug", "timestampKey: $timestampKey, cutoff: $cutoff")

                    if (timestampKey < cutoff) continue

                    val voltage = data.child("voltage").getValue(Float::class.java) ?: continue
                    val power = data.child("power").getValue(Float::class.java) ?: continue

                    val xValue = parseTimestampToXValue(timestampKey, timePeriod)
                    entries.add(Entry(xValue, power))
                    lastVoltage = voltage
                }
                Log.d("FirebaseDebug", "Loaded ${entries.size} entries")

                updateChart(entries)
                updateBatteryUI(voltageToBatteryPercentage(lastVoltage))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read data: ${error.message}")
            }
        })
    }

    private fun parseTimestampToXValue(timestamp: Long, timePeriod: String): Float {
        val now = System.currentTimeMillis() / 1000
        val cutoff: Long = when (timePeriod) {
            "daily" -> now - 24 * 60 * 60
            "weekly" -> now - 7 * 24 * 60 * 60
            "monthly" -> now - 30L * 24 * 60 * 60
            else -> now - 24 * 60 * 60
        }

        return (timestamp - cutoff).toFloat()
    }


    private fun updateChart(entries: List<Entry>) {
        if (entries.isEmpty()) {
            lineChart.clear() // ⬅️ This ensures the "No data" text is shown
            return
        }

        val dataSet = LineDataSet(entries, "Power").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }



    private fun updateBatteryUI(batteryPercentage: Float) {
        val percentageFormatted = String.format("%.2f", batteryPercentage)
        batteryPercentageTextView.text = "$percentageFormatted%"

        val batteryIconRes = when {
            batteryPercentage >= 50 -> android.R.drawable.ic_lock_idle_charging
            else -> android.R.drawable.ic_dialog_alert
        }

        batteryIcon.setImageResource(batteryIconRes)
    }
    private fun voltageToBatteryPercentage(voltage: Float): Float {
        return when {
            voltage >= 12.9f -> 100f
            voltage <= 11.6f -> 0f
            else -> ((voltage - 11.6f) / (12.9f - 11.6f) * 100f)
        }
    }

    private fun updateCurrentUserInFirestore() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val firestore = FirebaseFirestore.getInstance()
        val docRef = firestore.collection("current_user").document("active")

        val data = hashMapOf("userId" to user.uid)

        docRef.set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Current user updated in Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating current user", e)
            }
    }


}
