package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.status

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mobdeve.s13.estanol.miguelfrancis.mp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobdeve.s13.estanol.miguelfrancis.mp.PomodoroService
import com.mobdeve.s13.estanol.miguelfrancis.mp.databinding.FragmentStatusBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("PomodoroSettings", Context.MODE_PRIVATE)

        updateStatsUI()
        setupDailyGraph()
        setupWeeklyGraph()
    }

    private fun updateStatsUI() {
        val dailyCount = sharedPreferences.getInt("daily_pomodoro_count", 0)
        val weeklyCount = sharedPreferences.getInt("weekly_pomodoro_count", 0)
        val streak = sharedPreferences.getInt("daily_streak", 0)

        binding.dailyPomodoroCountTv.text = dailyCount.toString()
        binding.weeklyPomodoroCountTv.text = weeklyCount.toString()
        binding.streakCountTv.text = streak.toString()
    }

    @SuppressLint("NewApi")
    private fun setupDailyGraph() {
        val today = LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_DATE)
        val dailyPomodoroMapJson = sharedPreferences.getString("weekly_pomodoro_map", "{}")
        val gson = Gson()
        val map: Map<String, Int> = gson.fromJson(
            dailyPomodoroMapJson,
            object : TypeToken<Map<String, Int>>() {}.type
        )

        val todayCount = map[today] ?: 0
        val entries = listOf(BarEntry(1f, todayCount.toFloat()))

        val dataSet = BarDataSet(entries, "Pomodoro Sessions")
        val data = BarData(dataSet)

        binding.dailyChart.data = data
        binding.dailyChart.invalidate()
    }

    private fun setupWeeklyGraph() {
        val weeklyPomodoroMapJson = sharedPreferences.getString("weekly_pomodoro_map", "{}")
        val gson = Gson()
        val map: Map<String, Int> = gson.fromJson(
            weeklyPomodoroMapJson,
            object : TypeToken<Map<String, Int>>() {}.type
        )

        val entries = map.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Pomodoro Sessions")
        val data = LineData(dataSet)

        binding.weeklyChart.data = data
        binding.weeklyChart.invalidate()
    }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateStatsUI() // Refresh stats
            setupDailyGraph() // Refresh daily graph
            setupWeeklyGraph() // Refresh weekly graph
        }
    }

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PomodoroService.BROADCAST_UPDATE)
        requireContext().registerReceiver(updateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(updateReceiver)
    }

    override fun onResume() {
        super.onResume()
        updateStreak()
        updateStatsUI() // Ensure the updated streak is displayed
    }

    @SuppressLint("NewApi")
    private fun updateStreak() {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        val lastOpenedDate = sharedPreferences.getString("last_opened_date", null)
        var streak = sharedPreferences.getInt("daily_streak", 0)

        val editor = sharedPreferences.edit()

        if (lastOpenedDate == null || lastOpenedDate != currentDate) {
            if (lastOpenedDate != null && LocalDate.parse(lastOpenedDate).isBefore(LocalDate.now().minusDays(1))) {
                // Missed a day, reset streak to 1
                streak = 1
            } else {
                // Increment streak
                streak += 1
            }

            // Update SharedPreferences with the new streak and current date
            editor.putString("last_opened_date", currentDate)
            editor.putInt("daily_streak", streak)
            editor.apply()
        }
    }
}
