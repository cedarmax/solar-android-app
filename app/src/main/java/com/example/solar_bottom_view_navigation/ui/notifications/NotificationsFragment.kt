package com.example.solar_bottom_view_navigation.ui.notifications

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.solar_bottom_view_navigation.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NotificationsFragment : Fragment() {

    private lateinit var batteryPercentageChart: LineChart
    private lateinit var solarCurrentChart: LineChart
    private lateinit var batteryWattageChart: LineChart
    private lateinit var database: FirebaseDatabase
    private val auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

    private val batteryPercentageEntries = mutableListOf<Entry>()
    private val solarCurrentEntries = mutableListOf<Entry>()
    private val batteryWattageEntries = mutableListOf<Entry>()
    private var time = 0f
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val rootView = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Set up charts
        batteryPercentageChart = rootView.findViewById(R.id.batteryPercentageChart)
        solarCurrentChart = rootView.findViewById(R.id.solarCurrentChart)
        batteryWattageChart = rootView.findViewById(R.id.batteryWattageChart)

        setupChart(batteryPercentageChart)
        setupChart(solarCurrentChart)
        setupChart(batteryWattageChart)

        // Start dynamic updates
        startDynamicUpdates()

        return rootView
    }

    private fun setupChart(chart: LineChart) {
        chart.apply {
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = resources.getColor(android.R.color.white, requireContext().theme)
            axisLeft.textColor = resources.getColor(android.R.color.white, requireContext().theme)
            description.text = ""
            legend.textColor = resources.getColor(android.R.color.white, requireContext().theme)
            setBackgroundColor(resources.getColor(android.R.color.black, requireContext().theme))
        }
    }

    private fun startDynamicUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Generate random values
                val newBatteryPercentage = Random.nextFloat() * 5 + 95 // 95% to 100%
                val newSolarWattage = Random.nextFloat() * 100         // 0W to 100W
                val newBatteryWattage = Random.nextFloat() * 50        // 0W to 50W

                val timestamp = System.currentTimeMillis()

                // Add data points to charts
                batteryPercentageEntries.add(Entry(time, newBatteryPercentage))
                updateChart(batteryPercentageChart, batteryPercentageEntries, "Battery Percentage")

                solarCurrentEntries.add(Entry(time, newSolarWattage))
                updateChart(solarCurrentChart, solarCurrentEntries, "Solar Panel Wattage")

                batteryWattageEntries.add(Entry(time, newBatteryWattage))
                updateChart(batteryWattageChart, batteryWattageEntries, "Battery Wattage")

                // Upload data to Firebase
                uploadDataToFirebase(
                    timestamp,
                    newBatteryPercentage,
                    newSolarWattage,
                    newBatteryWattage
                )

                time += 1f
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun updateChart(chart: LineChart, entries: List<Entry>, label: String) {
        val dataSet = LineDataSet(entries, label).apply {
            color = resources.getColor(android.R.color.holo_blue_light, requireContext().theme)
            valueTextColor = resources.getColor(android.R.color.white, requireContext().theme)
            circleColors = listOf(resources.getColor(android.R.color.white, requireContext().theme))
            lineWidth = 2f
        }
        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun uploadDataToFirebase(
        timestamp: Long,
        batteryPercentage: Float,
        solarWattage: Float,
        batteryWattage: Float
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))

        // Get the current user’s UID
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("Firebase", "User not logged in")
            return
        }

        // Create a reference in Firebase Realtime Database under users/{userId}/powerStatistics
        val dataRef = database.reference
            .child("users")
            .child(userId)
            .child("powerStatistics")
            .child(formattedTime)

        // Data object
        val data = mapOf(
            "batteryPercentage" to batteryPercentage,
            "solarWattage" to solarWattage,
            "batteryWattage" to batteryWattage,
            "time" to formattedTime
        )

        // Push data to Firebase
        dataRef.setValue(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Data uploaded successfully!")
            } else {
                Log.e("Firebase", "Data upload failed: ${task.exception?.message}")
            }
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
