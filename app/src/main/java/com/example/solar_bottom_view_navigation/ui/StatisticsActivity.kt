package com.example.solar_bottom_view_navigation.ui

    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import com.example.solar_bottom_view_navigation.ui.databinding.ActivityStatisticsBinding
    import com.github.mikephil.charting.charts.LineChart
    import com.github.mikephil.charting.data.Entry
    import com.github.mikephil.charting.data.LineData
    import com.github.mikephil.charting.data.LineDataSet
    import com.google.firebase.firestore.FirebaseFirestore

    class StatisticsActivity : AppCompatActivity() {

        private lateinit var binding: ActivityStatisticsBinding
        private lateinit var lineChart: LineChart
        private var firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityStatisticsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            lineChart = binding.lineChart
            loadGraphData()
        }

        private fun loadGraphData() {
            // Fetch on/off status of the light over time from Firestore
            firestoreDb.collection("light_logs")
                .get()
                .addOnSuccessListener { result ->
                    val entries = mutableListOf<Entry>()
                    var i = 0f
                    for (document in result) {
                        val status = document.getBoolean("status") ?: false
                        entries.add(Entry(i, if (status) 1f else 0f))
                        i += 1
                    }
                    val dataSet = LineDataSet(entries, "Light On Status")
                    dataSet.color = resources.getColor(R.color.purple_500)
                    val lineData = LineData(dataSet)
                    lineChart.data = lineData
                    lineChart.invalidate()
                }
        }
    }

