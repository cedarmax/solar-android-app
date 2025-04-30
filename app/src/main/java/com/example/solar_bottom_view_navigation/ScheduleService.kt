package com.example.solar_bottom_view_navigation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScheduleService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val interval = 60 * 1000L // 60 seconds

    private val runnable = object : Runnable {
        override fun run() {
            checkSchedules()
            handler.postDelayed(this, interval)
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "schedule_channel",
                "Schedule Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun checkSchedules() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val schedulesRef = db.collection("users").document(userId).collection("schedules")
        val switchesRef = db.collection("users").document(userId)
        val now = System.currentTimeMillis()

        schedulesRef.get().addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                val time = doc.getTimestamp("time")?.toDate()?.time ?: continue
                val alreadyTriggered = doc.getBoolean("triggered") == true
                if (time <= now && !alreadyTriggered) {
                    val switch = doc.getLong("switch")?.toInt() ?: continue
                    val turnOn = doc.getBoolean("turnOn") ?: continue

                    val field = when (switch) {
                        1 -> "switch1"
                        2 -> "switch2"
                        else -> continue
                    }

                    // Update the switch state
                    switchesRef.update(field, turnOn)

                    // Optionally mark this schedule as triggered
                    doc.reference.update("triggered", true)
                }
            }
        }
    }


    private fun startForegroundNotification() {
        val channelId = "ScheduleServiceChannel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Schedule Checker",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Schedule Checker Running")
            .setContentText("Monitoring switch schedules every 60 seconds")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundNotification()
        handler.post(runnable)
        return START_STICKY
    }


    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
