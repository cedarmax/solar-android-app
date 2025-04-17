package com.example.solar_bottom_view_navigation.ui.home

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TimestampAxisFormatter(private val cutoff: Long) : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        val actualTimestamp = (cutoff + value.toLong()) * 1000 // Back to millis
        return dateFormat.format(Date(actualTimestamp))
    }
}

