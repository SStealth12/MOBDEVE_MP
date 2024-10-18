package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobdeve.s13.estanol.miguelfrancis.mp.R

class HomeFragment : Fragment() {

    private lateinit var circularProgressBar: ProgressBar
    private lateinit var timerTv: TextView
    private lateinit var startBtn: Button
    private lateinit var pauseBtn: Button
    private lateinit var continueBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var timer: CountDownTimer
    private var timeRemaining = 25 * 60 * 1000L // 25 minutes in milliseconds
    private var isPaused = false
    private val totalDuration = 25 * 60 * 1000L // Total time for 25 minutes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        circularProgressBar = root.findViewById(R.id.circularProgressBar)
        timerTv = root.findViewById(R.id.timerTv)
        startBtn = root.findViewById(R.id.startBtn)
        pauseBtn = root.findViewById(R.id.pauseBtn)
        continueBtn = root.findViewById(R.id.continueBtn)
        stopBtn = root.findViewById(R.id.stopBtn)

        // Start button logic
        startBtn.setOnClickListener {
            startTimer()
            startBtn.visibility = View.GONE
            pauseBtn.visibility = View.VISIBLE
        }

        // Pause button logic
        pauseBtn.setOnClickListener {
            pauseTimer()
            pauseBtn.visibility = View.GONE
            continueBtn.visibility = View.VISIBLE
            stopBtn.visibility = View.VISIBLE
        }

        // Continue button logic
        continueBtn.setOnClickListener {
            continueTimer()
            continueBtn.visibility = View.GONE
            stopBtn.visibility = View.GONE
            pauseBtn.visibility = View.VISIBLE
        }

        // Stop button logic
        stopBtn.setOnClickListener {
            stopTimer()
            continueBtn.visibility = View.GONE
            stopBtn.visibility = View.GONE
            startBtn.visibility = View.VISIBLE
        }

        return root
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateTimerText()
                updateProgressBar()
            }

            override fun onFinish() {
                timerTv.text = "00:00"
                circularProgressBar.progress = 0
            }
        }.start()
    }

    private fun pauseTimer() {
        timer.cancel()
        isPaused = true
    }

    private fun continueTimer() {
        startTimer() // Continue from where it left off
        isPaused = false
    }

    private fun stopTimer() {
        timer.cancel()
        timeRemaining = totalDuration // Reset to 25 minutes
        updateTimerText()
        circularProgressBar.progress = 100
    }

    private fun updateTimerText() {
        val minutes = (timeRemaining / 1000) / 60
        val seconds = (timeRemaining / 1000) % 60
        timerTv.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateProgressBar() {
        val progress = (timeRemaining.toFloat() / totalDuration.toFloat()) * 100
        circularProgressBar.progress = progress.toInt()
    }
}