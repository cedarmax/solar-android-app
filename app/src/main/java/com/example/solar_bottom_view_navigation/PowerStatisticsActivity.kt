package com.example.powerstats

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
/*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.random.Random

class PowerStatisticsActivity : AppCompatActivity() {

    private lateinit var batteryVoltageTextView: TextView
    private lateinit var solarCurrentTextView: TextView
    private lateinit var batteryVoltageChart: LineChart
    private lateinit var solarCurrentChart: LineChart
    private val batteryVoltageEntries = ArrayList<Entry>()
    private val solarCurrentEntries = ArrayList<Entry>()
    private val handler = Handler(Looper.getMainLooper())
    private var time = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_statistics)

        batteryVoltageTextView = findViewById(R.id.batteryVoltageTextView)
        solarCurrentTextView = findViewById(R.id.solarCurrentTextView)
        batteryVoltageChart = findViewById(R.id.batteryVoltageChart)
        solarCurrentChart = findViewById(R.id.solarCurrentChart)

        setupChart(batteryVoltageChart, "Battery Voltage (V)")
        setupChart(solarCurrentChart, "Solar Panel Current (A)")

        // Start updating values and graphs
        startUpdating()
    }

    private fun setupChart(chart: LineChart, label: String) {
        chart.apply {
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            description.isEnabled = false
        }
    }

    private fun startUpdating() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val batteryVoltage = getRandomVoltage()
                val solarCurrent = getRandomCurrent()

                updateBatteryVoltage(batteryVoltage)
                updateSolarCurrent(solarCurrent)

                // Schedule the next update
                time += 1f
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun updateBatteryVoltage(voltage: Float) {
        batteryVoltageTextView.text = "Battery Voltage: $voltage V"
        batteryVoltageEntries.add(Entry(time, voltage))

        val dataSet = LineDataSet(batteryVoltageEntries, "Voltage (V)")
        val lineData = LineData(dataSet)
        batteryVoltageChart.data = lineData
        batteryVoltageChart.notifyDataSetChanged()
        batteryVoltageChart.invalidate()
    }

    private fun updateSolarCurrent(current: Float) {
        solarCurrentTextView.text = "Solar Panel Current: $current A"
        solarCurrentEntries.add(Entry(time, current))

        val dataSet = LineDataSet(solarCurrentEntries, "Current (A)")
        val lineData = LineData(dataSet)
        solarCurrentChart.data = lineData
        solarCurrentChart.notifyDataSetChanged()
        solarCurrentChart.invalidate()
    }

    // Simulate random voltage values (replace with actual sensor data)
    private fun getRandomVoltage(): Float {
        return Random.nextFloat() * 4 + 10 // Example range: 10V to 14V
    }

    // Simulate random current values (replace with actual sensor data)
    private fun getRandomCurrent(): Float {
        return Random.nextFloat() * 5 // Example range: 0A to 5A
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
*/