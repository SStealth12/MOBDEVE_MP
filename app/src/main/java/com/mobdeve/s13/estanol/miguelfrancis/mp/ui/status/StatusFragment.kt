package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.status

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.mobdeve.s13.estanol.miguelfrancis.mp.databinding.FragmentStatusBinding

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val statusViewModel = ViewModelProvider(this).get(StatusViewModel::class.java)

        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupDailyBarChart(binding.dailyTimeGraph)
        setupWeeklyLineChart(binding.weeklyTimeGraph)
        setupDailyTaskPieChart(binding.dailyTaskGraph)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Set up the daily bar chart (Dummy data for now)
    private fun setupDailyBarChart(barChart: BarChart) {
        val entries = ArrayList<BarEntry>()
        val hoursOfDay = arrayOf(8, 9, 10, 11, 12, 13, 14, 15)  // Example time in hours
        val pomodoroMinutes = arrayOf(25f, 50f, 15f, 40f, 35f, 60f, 30f, 45f)  // Pomodoro minutes

        for (i in hoursOfDay.indices) {
            entries.add(BarEntry(hoursOfDay[i].toFloat(), pomodoroMinutes[i]))
        }

        val dataSet = BarDataSet(entries, "Pomodoro Minutes")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()  // Use material color scheme
        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.description.isEnabled = false
        barChart.setFitBars(true)  // Make bars fit nicely into chart
        barChart.invalidate()  // Refresh the chart
    }

    // Set up the weekly line chart (Dummy data for now)
    private fun setupWeeklyLineChart(lineChart: LineChart) {
        val entries = ArrayList<Entry>()
        val dates = arrayOf(19f, 20f, 21f, 22f, 23f, 24f, 25f)  // Dates (e.g., Oct 19, Oct 20)
        val pomodoroMinutes = arrayOf(120f, 100f, 80f, 140f, 60f, 100f, 110f)  // Pomodoro minutes

        for (i in dates.indices) {
            entries.add(Entry(dates[i], pomodoroMinutes[i]))
        }

        val dataSet = LineDataSet(entries, "Pomodoro Minutes")
        dataSet.color = Color.BLUE
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(Color.BLUE)

        val data = LineData(dataSet)
        lineChart.data = data
        lineChart.description.isEnabled = false
        lineChart.invalidate()  // Refresh the chart
    }

    // Set up the daily task breakdown pie chart (Dummy data for now)
    private fun setupDailyTaskPieChart(pieChart: PieChart) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40f, "Work"))
        entries.add(PieEntry(30f, "Study"))
        entries.add(PieEntry(20f, "Exercise"))
        entries.add(PieEntry(10f, "Leisure"))

        val dataSet = PieDataSet(entries, "Task Breakdown")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()  // Use material color scheme

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.invalidate()  // Refresh the chart
    }
}
