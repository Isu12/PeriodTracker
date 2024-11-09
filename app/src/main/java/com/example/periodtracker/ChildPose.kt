package com.example.periodtracker

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.periodtracker.R
import com.example.periodtracker.myaccount
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class ChildPose : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startTimerButton: Button
    private lateinit var stopTimerButton: Button
    private lateinit var setTimerButton: Button
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 10000 // Default is 10 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_pose)

        // Initialize UI elements
        timerTextView = findViewById(R.id.timerTextView)
        startTimerButton = findViewById(R.id.startTimerButton)
        stopTimerButton = findViewById(R.id.stopTimerButton)
        setTimerButton = findViewById(R.id.setTimerButton)

        // Set the initial timer text
        updateTimerText()

        // Start Timer
        startTimerButton.setOnClickListener {
            startTimer()
        }

        // Stop Timer
        stopTimerButton.setOnClickListener {
            stopTimer()
        }

        // Set Timer Button Click Listener
        setTimerButton.setOnClickListener {
            showTimePickerDialog()
        }

        val backButton: FloatingActionButton = findViewById(R.id.backArrowChildPose)
        backButton.setOnClickListener {
            val intent = Intent(this, myaccount::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Convert selected hour and minute to milliseconds
            timeLeftInMillis = (selectedHour * 3600 + selectedMinute * 60) * 1000L
            updateTimerText()
        }, hour, minute, true).show()
    }

    // Start the countdown timer
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                notifyUserTimeIsUp()
                timeLeftInMillis = 10000 // Reset to default
                updateTimerText()
            }
        }.start()

        startTimerButton.isEnabled = false
        stopTimerButton.isEnabled = true
    }

    // Stop the countdown timer
    private fun stopTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = 10000 // Reset to default
        updateTimerText()

        startTimerButton.isEnabled = true
        stopTimerButton.isEnabled = false
    }

    // Update the timer text in the TextView
    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }

    // Notify the user that the time is up (with sound and vibration)
    private fun notifyUserTimeIsUp() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(2000)

        // Create an AlertDialog to notify the user
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Time's Up!")
        builder.setMessage("Your timer has finished.")

        // Reset Button
        builder.setPositiveButton("Reset") { dialog, _ ->
            resetTimer()
            dialog.dismiss()
        }

        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }

    // Reset the timer
    private fun resetTimer() {
        stopTimer()
    }
}
