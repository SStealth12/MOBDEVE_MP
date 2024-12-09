package com.mobdeve.s13.estanol.miguelfrancis.mp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PomodoroService : Service() {

    companion object {
        private const val DEFAULT_WORK_TIME: Long = 2 * 60 * 1000 // Default: 2 minutes in milliseconds
        private const val DEFAULT_REST_TIME: Long = 1 * 60 * 1000 // Default: 1 minutes in milliseconds

        const val ACTION_START = "START"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_STOP = "STOP"
        const val BROADCAST_UPDATE = "com.mobdeve.s13.estanol.miguelfrancis.mp.UPDATE"
        const val EXTRA_TIME_LEFT = "TIME_LEFT"
        const val EXTRA_IS_RUNNING = "IS_RUNNING"
        const val EXTRA_IS_WORK_PHASE = "IS_WORK_PHASE"
        const val NOTIFICATION_CHANNEL_ID = "pomodoro_timer_channel"
        const val NOTIFICATION_ID = 1
    }

    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var isWorkPhase = true // Indicates whether the current phase is work or rest
    private var timeLeftInMillis: Long = DEFAULT_WORK_TIME

    private var workEndAlarm: MediaPlayer? = null
    private var breakEndAlarm: MediaPlayer? = null

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        sharedPreferences = getSharedPreferences("PomodoroSettings", Context.MODE_PRIVATE)

        // Load user-selected alarm sounds
        loadAlarmSounds()

        // Load default timer durations from preferences
        timeLeftInMillis = getWorkDuration()
    }

    private fun loadAlarmSounds() {
        val workAlarmIndex = sharedPreferences.getInt("work_alarm", 0)
        val breakAlarmIndex = sharedPreferences.getInt("break_alarm", 0)

        workEndAlarm = createMediaPlayer(AlarmSoundMappings.workAlarmSounds[workAlarmIndex] ?: R.raw.eightbit_1)
        breakEndAlarm = createMediaPlayer(AlarmSoundMappings.breakAlarmSounds[breakAlarmIndex] ?: R.raw.eightbit_2)
    }

    private fun createMediaPlayer(resourceId: Int): MediaPlayer {
        return MediaPlayer.create(this, resourceId)
    }

    private fun getWorkDuration(): Long {
        val minutes = sharedPreferences.getInt("work_duration", 1) // Default: 1 minutes
        return minutes * 60 * 1000L
    }

    private fun getRestDuration(): Long {
        val minutes = sharedPreferences.getInt("break_duration", 1) // Default: 1 minutes
        return minutes * 60 * 1000L
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    private fun startTimer() {
        // Reset the timer if it has finished
        timeLeftInMillis = if (timeLeftInMillis == 0L) {
            if (isWorkPhase) getWorkDuration() else getRestDuration()
        } else {
            timeLeftInMillis
        }

        timer?.cancel()
        val notificationBuilder = createNotification()
        val notification = notificationBuilder.build()

        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                broadcastUpdate()
                updateNotification()
            }

            override fun onFinish() {
                isRunning = false

                // Play alarm based on the phase
                if (isWorkPhase) {
                    workEndAlarm?.start()
                    updateStatsOnPomodoroCompletion() // Update stats on work phase completion
                } else {
                    breakEndAlarm?.start()
                }

                isWorkPhase = !isWorkPhase
                timeLeftInMillis = if (isWorkPhase) getWorkDuration() else getRestDuration()

                // Broadcast and notification updates
                broadcastUpdate()
                updateNotification()
            }
        }.start()

        isRunning = true
        broadcastUpdate()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        broadcastUpdate()
        updateNotification()
    }

    private fun stopTimer() {
        timer?.cancel()
        isRunning = false
        isWorkPhase = true
        timeLeftInMillis = getWorkDuration()
        broadcastUpdate()
        stopForeground(true)
    }

    private fun switchPhase() {
        isWorkPhase = !isWorkPhase
        timeLeftInMillis = if (isWorkPhase) DEFAULT_WORK_TIME else DEFAULT_REST_TIME
        broadcastUpdate() // Notify UI of phase change
    }

    private fun broadcastUpdate() {
        val intent = Intent(BROADCAST_UPDATE).apply {
            putExtra(EXTRA_TIME_LEFT, timeLeftInMillis)
            putExtra(EXTRA_IS_RUNNING, isRunning)
            putExtra(EXTRA_IS_WORK_PHASE, isWorkPhase)
        }
        sendBroadcast(intent)
    }

    private fun createNotification(): NotificationCompat.Builder {
        val startIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_START }
        val pauseIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_PAUSE }
        val stopIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_STOP }

        val startPendingIntent = PendingIntent.getService(
            this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pausePendingIntent = PendingIntent.getService(
            this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Pomodoro Timer")
            .setContentText("Time remaining: ${formatTime(timeLeftInMillis)} (${if (isWorkPhase) "Work" else "Rest"})")
            .setSmallIcon(R.drawable.ic_home_black_24dp) // Update with your icon resource
            .addAction(R.drawable.ic_start, "Start", startPendingIntent)
            .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setOngoing(isRunning)
    }

    @SuppressLint("NotificationPermission")
    private fun updateNotification() {
        val notification = createNotification()
            .setContentText("Time remaining: ${formatTime(timeLeftInMillis)} (${if (isWorkPhase) "Work" else "Rest"})")
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    @SuppressLint("NewApi")
    private fun updateStatsOnPomodoroCompletion() {
        val editor = sharedPreferences.edit()
        val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        // Daily Pomodoro Count
        val lastUpdatedDate = sharedPreferences.getString("last_updated_date", "")
        var dailyCount = sharedPreferences.getInt("daily_pomodoro_count", 0)
        if (lastUpdatedDate != currentDate) {
            editor.putString("last_updated_date", currentDate)
            editor.putInt("daily_pomodoro_count", 1) // Reset daily count for a new day
        } else {
            dailyCount += 1
            editor.putInt("daily_pomodoro_count", dailyCount)
        }

        // Weekly Pomodoro Count
        val weeklyPomodoroMap = sharedPreferences.getString("weekly_pomodoro_map", "{}")
        val updatedMap = updateWeeklyPomodoroMap(weeklyPomodoroMap, currentDate)
        editor.putString("weekly_pomodoro_map", updatedMap)
        editor.putInt("weekly_pomodoro_count", calculateWeeklyCount(updatedMap))

        // Apply changes
        editor.apply()
    }

    @SuppressLint("NewApi")
    private fun updateWeeklyPomodoroMap(mapJson: String?, currentDate: String): String {
        val gson = Gson()
        val map: MutableMap<String, Int> = if (mapJson.isNullOrEmpty()) {
            mutableMapOf()
        } else {
            gson.fromJson(mapJson, object : TypeToken<MutableMap<String, Int>>() {}.type)
        }

        // Add or increment today's entry
        map[currentDate] = (map[currentDate] ?: 0) + 1

        // Remove entries older than 7 days
        val sevenDaysAgo = LocalDate.now().minusDays(7)
        map.entries.removeIf { (date, _) -> LocalDate.parse(date).isBefore(sevenDaysAgo) }

        return gson.toJson(map)
    }

    private fun calculateWeeklyCount(mapJson: String?): Int {
        val gson = Gson()
        val map: Map<String, Int> = if (mapJson.isNullOrEmpty()) {
            emptyMap()
        } else {
            gson.fromJson(mapJson, object : TypeToken<Map<String, Int>>() {}.type)
        }
        return map.values.sum()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        // Cancel the timer to release resources
        timer?.cancel()

        // Release MediaPlayer resources
        workEndAlarm?.release()
        workEndAlarm = null

        breakEndAlarm?.release()
        breakEndAlarm = null
    }

}
