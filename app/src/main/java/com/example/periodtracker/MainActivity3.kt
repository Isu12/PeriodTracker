package com.example.periodtracker

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class MainActivity3 : AppCompatActivity() {
    private lateinit var selectDateButton: Button
    private lateinit var selectDateTextView: TextView
    private lateinit var selectedDate: String

    // Constants for SharedPreferences
    private val PREFS_NAME = "PeriodTrackerPrefs"
    private val KEY_CYCLE_LENGTH = "cycleLength"
    private val KEY_PERIOD_LENGTH = "periodLength"
    private val KEY_LAST_PERIOD_DATE = "lastPeriodDate"
    private val NOTIFICATION_ID = 1

    // UI Components
    private lateinit var periodStartText: TextView
    private lateinit var periodEndText: TextView

    // Calendar instance
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        createNotificationChannel()

        selectDateButton = findViewById(R.id.selectDateButton)
        selectDateTextView = findViewById(R.id.selectDateButton)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        selectedDate = "Not Set"

        // Handle "Select Date" button click to show the date picker
        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Initialize UI components
        periodStartText = findViewById(R.id.textView5)
        periodEndText = findViewById(R.id.periodEndText)

        // Display initial period dates
        displayNextPeriodDates()

        // Setup TabLayout behavior
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            // Handle Calendar tab
                        }
                        1 -> {
                            // Navigate to Settings activity
                            val intent = Intent(this@MainActivity3, settings::class.java)
                            startActivity(intent)
                        }
                        2 -> {
                            // Navigate to MyAccount activity
                            val intent = Intent(this@MainActivity3, myaccount::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(calendar.time)
            selectDateTextView.text = "Period Started: $selectedDate"

            saveLastPeriodDate(selectedDate)
            displayNextPeriodDates()
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "periodReminderChannel"
            val name = "Period Reminder"
            val description = "Channel for period reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(nextPeriodStart: Date) {
        Log.d("MainActivity3", "Scheduling notification for $nextPeriodStart")
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("notificationId", NOTIFICATION_ID)
            putExtra("notificationTitle", "Upcoming Period Reminder")
            putExtra("notificationMessage", "Your period is starting tomorrow!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationTime = Calendar.getInstance().apply {
            time = nextPeriodStart
            add(Calendar.SECOND, 10 ) // Schedule for 1 day before the next period
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)
        Log.d("MainActivity3", "Notification scheduled for ${notificationTime.timeInMillis}")
    }

    private fun displayNextPeriodDates() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val cycleLength = sharedPreferences.getInt(KEY_CYCLE_LENGTH, 28)
        val periodLength = sharedPreferences.getInt(KEY_PERIOD_LENGTH, 5)
        val lastPeriodStart = sharedPreferences.getString(KEY_LAST_PERIOD_DATE, null)

        if (lastPeriodStart != null) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val lastPeriodDate = sdf.parse(lastPeriodStart) ?: throw Exception("Failed to parse lastPeriodStart into Date.")

                val nextPeriodStart = Calendar.getInstance().apply {
                    time = lastPeriodDate
                    add(Calendar.DAY_OF_MONTH, periodLength + cycleLength)
                }.time

                val nextPeriodEnd = Calendar.getInstance().apply {
                    time = nextPeriodStart
                    add(Calendar.DAY_OF_MONTH, periodLength)
                }.time

                // Display next period dates
                periodStartText.text = "Your next period starts on ${sdf.format(nextPeriodStart)}"
                periodEndText.text = "Your next period ends on ${sdf.format(nextPeriodEnd)}"

                // Save the next period dates to SharedPreferences for the widget
                saveNextPeriodDates(nextPeriodStart, nextPeriodEnd)

                // Schedule notification for the next period start date
                scheduleNotification(nextPeriodStart)
                // Update widget
                runOnUiThread { updateWidget() }
            } catch (e: Exception) {
                e.printStackTrace()
                periodStartText.text = "Error: ${e.message}"
                periodEndText.text = ""
            }
        } else {
            periodStartText.text = "Please set your last period date."
            periodEndText.text = ""
        }
    }

    private fun saveLastPeriodDate(date: String) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_LAST_PERIOD_DATE, date).apply()
    }

    private fun saveNextPeriodDates(start: Date, end: Date) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sharedPreferences.edit()
            .putString("nextPeriodStart", sdf.format(start))
            .putString("nextPeriodEnd", sdf.format(end))
            .apply()
    }

    private fun updateWidget() {
        val intent = Intent(this, PeriodWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(this)
            .getAppWidgetIds(ComponentName(this, PeriodWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
