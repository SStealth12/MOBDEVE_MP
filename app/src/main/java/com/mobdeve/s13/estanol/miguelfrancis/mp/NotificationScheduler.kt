package com.mobdeve.s13.estanol.miguelfrancis.mp

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleMorningNotification(context: Context) {
        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Calculate delay in milliseconds
        val delay = if (currentTime.before(scheduledTime)) {
            scheduledTime.timeInMillis - currentTime.timeInMillis
        } else {
            // Schedule for the next day if the time has passed
            scheduledTime.timeInMillis + TimeUnit.DAYS.toMillis(1) - currentTime.timeInMillis
        }

        val morningRequest = OneTimeWorkRequestBuilder<MorningNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "MorningNotificationWork",
            ExistingWorkPolicy.KEEP,
            morningRequest
        )
    }

    fun scheduleAfternoonNotification(context: Context) {
        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Calculate delay in milliseconds
        val delay = if (currentTime.before(scheduledTime)) {
            scheduledTime.timeInMillis - currentTime.timeInMillis
        } else {
            // Schedule for the next day if the time has passed
            scheduledTime.timeInMillis + TimeUnit.DAYS.toMillis(1) - currentTime.timeInMillis
        }

        val afternoonRequest = OneTimeWorkRequestBuilder<AfternoonNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "AfternoonNotificationWork",
            ExistingWorkPolicy.KEEP,
            afternoonRequest
        )
    }
}