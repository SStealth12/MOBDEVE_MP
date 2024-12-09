package com.mobdeve.s13.estanol.miguelfrancis.mp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mobdeve.s13.estanol.miguelfrancis.mp.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        createNotificationChannel() // Creates the notification channel

        // Check and request exact alarm permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        setupNotifications() // Sets up the notifications

        Log.d("MainActivity", "NavController: $navController")
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_task,
                R.id.navigation_status, R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Change from Build.VERSION_CODES.S
            val channelName = "Pomodoro Notifications"
            val description = "Reminders for your Pomodoro sessions"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("POMODORO_CHANNEL", channelName, importance).apply {
                this.description = description
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun setupNotifications() {
        val sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE)
        val notificationsSet = sharedPreferences.getBoolean("NOTIFICATIONS_SET", false)

        if (!notificationsSet) {
            // Morning notification at 9 AM
            scheduleNotification(9, 0, "Start your Pomodoro session!", 1001)

            // Afternoon notification at 2 PM
            scheduleNotification(14, 0, "Don't forget your Pomodoro session!", 1002)

            sharedPreferences.edit().putBoolean("NOTIFICATIONS_SET", true).apply()
        }
    }

    private fun scheduleNotification(hour: Int, minute: Int, message: String, notificationId: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("NOTIFICATION_MESSAGE", message)
            putExtra("NOTIFICATION_ID", notificationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // If the time has already passed for today, schedule it for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            Log.d("MainActivity", "Scheduling notification for tomorrow at $hour:$minute")
        } else {
            Log.d("MainActivity", "Scheduling notification for today at $hour:$minute")
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )


        Log.d("MainActivity", "Notification scheduled with ID $notificationId for time ${calendar.time}")
    }

}