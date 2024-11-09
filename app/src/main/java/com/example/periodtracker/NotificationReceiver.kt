package com.example.periodtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", 0)
        val title = intent.getStringExtra("notificationTitle")
        val message = intent.getStringExtra("notificationMessage")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "periodReminderChannel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.pastel_green_white_feminine_accessories_logo__1_) // Replace with your icon resource
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
