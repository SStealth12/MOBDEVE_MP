package com.mobdeve.s13.estanol.miguelfrancis.mp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MorningNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("MorningNotificationWorker", "Starting morning notification worker")
        sendMorningNotification()
        Log.d("MorningNotificationWorker", "Finished morning notification worker")
        return Result.success()
    }

    @SuppressLint("NotificationPermission", "RemoteViewLayout")
    private fun sendMorningNotification() {
        val channelId = "morning_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Morning Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create a RemoteView for the custom layout (if you have a custom layout)
        val customView = RemoteViews(applicationContext.packageName, R.layout.notification_morning)

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_home_black_24dp)
            .setContentTitle("Time to start your day!")
            .setContentText("It's 9:00 AM! Begin your Pomodoro sessions and stay productive.")
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) // Use the standard system template
            .setCustomContentView(customView) // Assign your custom view to the notification
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1001, notification)
    }
}
