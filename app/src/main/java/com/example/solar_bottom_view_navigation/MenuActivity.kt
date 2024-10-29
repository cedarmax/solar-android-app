package com.example.solar_bottom_view_navigation

package com.example.powerstats

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val powerStatsButton = findViewById<Button>(R.id.powerStatsButton)

        powerStatsButton.setOnClickListener {
            val intent = Intent(this, PowerStatisticsActivity::class.java)
            startActivity(intent)
        }
    }
}
