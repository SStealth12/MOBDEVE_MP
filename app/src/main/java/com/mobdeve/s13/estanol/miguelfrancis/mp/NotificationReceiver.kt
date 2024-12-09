package com.mobdeve.s13.estanol.miguelfrancis.mp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission", "NotificationPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val notificationId = intent?.getIntExtra("NOTIFICATION_ID", 0) ?: 0
            val message = intent?.getStringExtra("NOTIFICATION_MESSAGE") ?: "Time for a Pomodoro!"

            // Create the Intent that will open MainActivity when the notification is tapped
            val notificationIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Build the notification
            val notification = NotificationCompat.Builder(context, "POMODORO_CHANNEL")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Pomodoro Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            // Show the notification
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notification)
        } catch (e: Exception) {
            // Log the exception for debugging purposes
            Log.e("NotificationReceiver", "Error while showing notification", e)
        }
    }
}
