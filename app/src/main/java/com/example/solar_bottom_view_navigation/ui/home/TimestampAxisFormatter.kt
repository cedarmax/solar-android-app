package com.example.solar_bottom_view_navigation.ui.home

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TimestampAxisFormatter(
    private val cutoff: Long,
    private val timePeriod: String
) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        val actualTimestamp = (cutoff + value.toLong()) * 1000 // Convert to millis
        val date = Date(actualTimestamp)

        val dateFormat = when (timePeriod) {
            "daily" -> SimpleDateFormat("HH:mm", Locale.getDefault())
            "weekly", "monthly" -> SimpleDateFormat("MM/dd", Locale.getDefault())
            else -> SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        }

        return dateFormat.format(date)
    }
}


