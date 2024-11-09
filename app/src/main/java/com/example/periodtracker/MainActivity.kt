package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button // Make sure to import the Button class

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Make sure to use the correct ID for the button
        val getStartButton: Button = findViewById(R.id.getStart) // Use R.id.get_started_button, not getStart

        // Find the button by ID and set up an onClickListener
        getStartButton.setOnClickListener {
            // Create an Intent to start MainActivity2
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }
}
