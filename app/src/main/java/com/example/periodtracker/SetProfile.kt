package com.example.periodtracker

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SetProfile : AppCompatActivity() {

    private val PREFS_NAME = "PeriodTrackerPrefs"
    private val KEY_PERIOD_LENGTH = "periodLength"
    private val KEY_CYCLE_LENGTH = "cycleLength"
    private val KEY_LAST_PERIOD_DATE = "lastPeriodDate"

    private lateinit var periodLengthEditText: EditText
    private lateinit var cycleLengthEditText: EditText
    private lateinit var selectDateTextView: TextView
    private lateinit var finishButton: Button
    private lateinit var selectDateButton: Button

    private var selectedDate: String? = null // Variable to store the selected date
    private val calendar = Calendar.getInstance() // Calendar instance for date picker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        // Initialize the UI components
        periodLengthEditText = findViewById(R.id.periodLength)
        cycleLengthEditText = findViewById(R.id.cycleLength)
        selectDateTextView = findViewById(R.id.selectedDateText)
        finishButton = findViewById(R.id.finishButton)
        selectDateButton = findViewById(R.id.selectDateButton)

        // Set default date if no date is selected
        selectedDate = "Not Set"

        // Handle "Select Date" button click to show the date picker
        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle "Finish" button click to save profile data
        finishButton.setOnClickListener {
            saveProfileData()
        }
    }

    // Method to show the DatePickerDialog
    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Set the selected date in the calendar object
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Format the selected date to display in the TextView and save it
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(calendar.time)
            selectDateTextView.text = "Selected Date: $selectedDate"
        }

        // Show the date picker dialog
        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Save the user profile data in SharedPreferences
    private fun saveProfileData() {
        val periodLength = periodLengthEditText.text.toString().toIntOrNull()
        val cycleLength = cycleLengthEditText.text.toString().toIntOrNull()

        // Check if inputs are valid
        if (periodLength != null && cycleLength != null && selectedDate != "Not Set") {
            // Save the data to SharedPreferences
            val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putInt(KEY_PERIOD_LENGTH, periodLength)
                putInt(KEY_CYCLE_LENGTH, cycleLength)
                putString(KEY_LAST_PERIOD_DATE, selectedDate)
                apply()
            }

            // Notify the user
            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()

            // Navigate to the next activity (e.g., MainActivity2)
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)

            // Finish the current activity
            finish()
        } else {
            // Handle invalid input
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show()
        }
    }
}
