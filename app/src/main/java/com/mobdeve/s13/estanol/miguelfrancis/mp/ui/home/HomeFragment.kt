package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobdeve.s13.estanol.miguelfrancis.mp.PomodoroService
import com.mobdeve.s13.estanol.miguelfrancis.mp.R

class HomeFragment : Fragment() {

    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val timeLeft = intent?.getLongExtra(PomodoroService.EXTRA_TIME_LEFT, 0L) ?: 0L
            val isRunning = intent?.getBooleanExtra(PomodoroService.EXTRA_IS_RUNNING, false) ?: false
            val isWorkPhase = intent?.getBooleanExtra(PomodoroService.EXTRA_IS_WORK_PHASE, true) ?: true
            updateUI(timeLeft, isRunning, isWorkPhase)

            if (timeLeft == 0L && !isRunning) {
                // Log the phase for debugging
                val phase = if (isWorkPhase) "Work Phase" else "Break Phase"
                Log.d("PomodoroService", "Phase switched to: $phase")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvTimer = view.findViewById(R.id.tvTimer)
        btnStart = view.findViewById(R.id.btnStart)
        btnPause = view.findViewById(R.id.btnPause)
        btnStop = view.findViewById(R.id.btnStop)

        btnStart.setOnClickListener { startTimer() }
        btnPause.setOnClickListener { pauseTimer() }
        btnStop.setOnClickListener { stopTimer() }

        return view
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(PomodoroService.BROADCAST_UPDATE)
        requireContext().registerReceiver(timerReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(timerReceiver)
    }

    private fun startTimer() {
        val intent = Intent(requireContext(), PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_START
        }
        requireContext().startService(intent)
    }

    private fun pauseTimer() {
        val intent = Intent(requireContext(), PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_PAUSE
        }
        requireContext().startService(intent)
    }

    private fun stopTimer() {
        val intent = Intent(requireContext(), PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_STOP
        }
        requireContext().startService(intent)
    }

    private fun updateUI(timeLeft: Long, isRunning: Boolean, isWorkPhase: Boolean) {
        val minutes = (timeLeft / 1000) / 60
        val seconds = (timeLeft / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)

        if (!isRunning && timeLeft == 0L) {
            btnStart.isEnabled = true
            btnPause.isEnabled = false
            btnStop.isEnabled = false

            // Show the phase that is about to start
            val nextPhase = if (isWorkPhase) "Break Phase" else "Work Phase"
            tvTimer.text = "Next: $nextPhase"
        } else {
            btnStart.isEnabled = !isRunning
            btnPause.isEnabled = isRunning
            btnStop.isEnabled = isRunning
        }
    }
}

