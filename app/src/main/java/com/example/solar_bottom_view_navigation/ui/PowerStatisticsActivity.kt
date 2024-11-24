package com.example.solar_bottom_view_navigation.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.solar_bottom_view_navigation.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.random.Random

import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PowerStatisticsActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    private val batteryPercentageEntries = mutableListOf<Entry>()
    private val solarCurrentEntries = mutableListOf<Entry>()
    private val batteryWattageEntries = mutableListOf<Entry>()

    private var time = 0f
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_statistics)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance()

        // Chart setup
        setupChart(findViewById(R.id.batteryPercentageChart))
        setupChart(findViewById(R.id.solarCurrentChart))
        setupChart(findViewById(R.id.batteryWattageChart))

        // Start dynamic updates
        startDynamicUpdates()
    }

    private fun setupChart(chart: LineChart) {
        chart.apply {
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = resources.getColor(android.R.color.white, theme)
            axisLeft.textColor = resources.getColor(android.R.color.white, theme)
            description.text = ""
            legend.textColor = resources.getColor(android.R.color.white, theme)
            setBackgroundColor(resources.getColor(android.R.color.black, theme))
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
                updateChart(findViewById(R.id.batteryPercentageChart), batteryPercentageEntries, "Battery Percentage")

                solarCurrentEntries.add(Entry(time, newSolarWattage))
                updateChart(findViewById(R.id.solarCurrentChart), solarCurrentEntries, "Solar Panel Wattage")

                batteryWattageEntries.add(Entry(time, newBatteryWattage))
                updateChart(findViewById(R.id.batteryWattageChart), batteryWattageEntries, "Battery Wattage")

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
            color = resources.getColor(android.R.color.holo_blue_light, theme)
            valueTextColor = resources.getColor(android.R.color.white, theme)
            circleColors = listOf(resources.getColor(android.R.color.white, theme))
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

        // Create a reference in Firebase Realtime Database
        val dataRef = database.reference.child("powerStatistics").child(formattedTime)

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


