package com.example.periodtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executor

class MainActivity2 : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize views
        val emailEditText: EditText = findViewById(R.id.editTextTextEmailAddress)
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.loginPGLoginBTN)
        val fingerprintButton: Button = findViewById(R.id.loginPGLoginFingerPrintBTN)
        val registerButton: Button = findViewById(R.id.loginPGRegisterBTN)
        val backButton: FloatingActionButton = findViewById(R.id.backArrowLogin)

        // Set up fingerprint authentication
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // On successful fingerprint authentication, navigate to the next activity
                    val intent = Intent(this@MainActivity2, MainActivity3::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for Period Tracker")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        // Fingerprint login button
        fingerprintButton.setOnClickListener {
            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    biometricPrompt.authenticate(promptInfo) // Show fingerprint prompt
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_SHORT).show()
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Toast.makeText(this, "Biometric hardware is unavailable", Toast.LENGTH_SHORT).show()
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Toast.makeText(this, "No biometrics enrolled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Login with email and password
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show()
            } else {
                if (authenticateUser(email, password)) {
                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Register button
        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Back button
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Authentication for email and password
    private fun authenticateUser(email: String, password: String): Boolean {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val storedEmail = sharedPreferences.getString("email", null)
        val storedPassword = sharedPreferences.getString("password", null)

        return email == storedEmail && password == storedPassword
    }
}
