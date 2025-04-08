package com.example.solar_bottom_view_navigation.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.solar_bottom_view_navigation.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

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

        auth = FirebaseAuth.getInstance()
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
        val user: FirebaseUser? = auth.currentUser
        userNameTextView.text = "Welcome, ${user?.displayName ?: "Guest"}"
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
        }
    }

    private fun loadPowerStatistics(timePeriod: String) {
        val userId = auth.currentUser?.uid ?: return
        val dataRef = database.reference.child("users").child(userId).child("powerStatistics")

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<Entry>()
                var batteryPercentage = 100f

                for (data in snapshot.children) {
                    val timeString = data.child("time").value.toString()
                    val batteryPct = data.child("batteryPercentage").getValue(Float::class.java) ?: 0f

                    val timeValue = parseTimeToXValue(timeString, timePeriod)
                    entries.add(Entry(timeValue, batteryPct))

                    batteryPercentage = batteryPct
                }

                updateChart(entries)
                updateBatteryUI(batteryPercentage)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read data: ${error.message}")
            }
        })
    }

    private fun updateChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Battery Percentage").apply {
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
        batteryPercentageTextView.text = "${batteryPercentage.toInt()}%"

        val batteryIconRes = when {
            //batteryPercentage >= 80 -> R.drawable.ic_battery_full
            batteryPercentage >= 50 -> android.R.drawable.ic_lock_idle_charging // Battery charging icon
            //batteryPercentage >= 20 ->        // Alert icon (for low battery)
            else -> android.R.drawable.ic_dialog_alert
        }

        batteryIcon.setImageResource(batteryIconRes)
    }

    private fun parseTimeToXValue(timeString: String, timePeriod: String): Float {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            val date = dateFormat.parse(timeString)
            when (timePeriod) {
                "daily" -> date.hours.toFloat()
                "weekly" -> date.day.toFloat()
                "monthly" -> date.date.toFloat()
                else -> 0f
            }
        } catch (e: Exception) {
            Log.e("TimeParsing", "Error parsing time: $e")
            0f
        }
    }
}
