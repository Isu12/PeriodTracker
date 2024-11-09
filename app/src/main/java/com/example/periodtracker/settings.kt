package com.example.periodtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class settings : AppCompatActivity() {

    private val PREFS_NAME = "UserPrefs"
    private val KEY_NAME = "fullName"
    private val KEY_EMAIL = "email"
    private val KEY_PASSWORD = "password"
    private val KEY_PERIOD_LENGTH = "periodLength"
    private val KEY_CYCLE_LENGTH = "cycleLength"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // UI elements
        val nameEditText: EditText = findViewById(R.id.myName)
        val emailEditText: EditText = findViewById(R.id.myEmail)
        val periodLengthEditText: EditText = findViewById(R.id.editTextText2)
        val cycleLengthEditText: EditText = findViewById(R.id.editTextText3)
        val passwordEditText: EditText = findViewById(R.id.editTextText5)
        val deleteButton: Button = findViewById(R.id.deleteBTN)
        val saveProfileButton: Button = findViewById(R.id.saveBTN1)
        val savePeriodButton: Button = findViewById(R.id.saveBTN2)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout2)

        // Set up TabLayout navigation
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> startActivity(Intent(this@settings, MainActivity3::class.java))
                        1 -> { /* No action for the current tab */ }
                        2 -> startActivity(Intent(this@settings, myaccount::class.java))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Load data when the activity starts
        loadUserProfile()
        loadPeriodData()

        // Handle "Save Profile" button click
        saveProfileButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate email format and password policy
            if (validateEmail(email) && validatePassword(password)) {
                saveUserProfile(name, email, password)
                Toast.makeText(this, "Profile changes saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid email and a password with at least 6 characters, including letters and numbers.", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle "Save Period Data" button click
        savePeriodButton.setOnClickListener {
            val periodLengthText = periodLengthEditText.text.toString().trim()
            val cycleLengthText = cycleLengthEditText.text.toString().trim()

            if (periodLengthText.isNotEmpty() && cycleLengthText.isNotEmpty()) {
                val periodLength = periodLengthText.toIntOrNull()
                val cycleLength = cycleLengthText.toIntOrNull()

                if (periodLength != null && cycleLength != null) {
                    savePeriodData(periodLength, cycleLength)
                    Toast.makeText(this, "Period data saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter valid numbers for period and cycle length.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all period data fields.", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle "Delete Account" button click
        deleteButton.setOnClickListener {
            deleteUserAccount()
            Toast.makeText(this, "Account deleted successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
        }
    }

    // Load user profile from SharedPreferences
    private fun loadUserProfile() {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString(KEY_NAME, "")
        val email = sharedPreferences.getString(KEY_EMAIL, "")
        val password = sharedPreferences.getString(KEY_PASSWORD, "")

        findViewById<EditText>(R.id.myName).setText(name)
        findViewById<EditText>(R.id.myEmail).setText(email)
        findViewById<EditText>(R.id.editTextText5).setText(password)
    }

    // Load period data from SharedPreferences
    private fun loadPeriodData() {
        val sharedPreferences = getSharedPreferences("PeriodTrackerPrefs", Context.MODE_PRIVATE)
        val periodLength = sharedPreferences.getInt(KEY_PERIOD_LENGTH, 0)
        val cycleLength = sharedPreferences.getInt(KEY_CYCLE_LENGTH, 0)

        findViewById<EditText>(R.id.editTextText2).setText(periodLength.toString())
        findViewById<EditText>(R.id.editTextText3).setText(cycleLength.toString())
    }

    // Save user profile to SharedPreferences
    private fun saveUserProfile(name: String, email: String, password: String) {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    // Save period data to SharedPreferences
    private fun savePeriodData(periodLength: Int, cycleLength: Int) {
        val sharedPreferences = getSharedPreferences("PeriodTrackerPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt(KEY_PERIOD_LENGTH, periodLength)
            putInt(KEY_CYCLE_LENGTH, cycleLength)
            apply()
        }
    }

    // Delete user account and period data from SharedPreferences
    private fun deleteUserAccount() {
        val userPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val periodPrefs = getSharedPreferences("periodData", Context.MODE_PRIVATE)

        with(userPrefs.edit()) {
            clear()
            apply()
        }

        with(periodPrefs.edit()) {
            clear()
            apply()
        }
    }

    // Email validation
    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Password validation (minimum 6 characters with letters and numbers)
    private fun validatePassword(password: String): Boolean {
        return password.length >= 6 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }
}
