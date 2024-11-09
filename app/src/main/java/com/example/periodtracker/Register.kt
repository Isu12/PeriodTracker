package com.example.periodtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText: EditText = findViewById(R.id.editTextText)
        val emailEditText: EditText = findViewById(R.id.editTextTextEmailAddress2)
        val passwordEditText: EditText = findViewById(R.id.editTextTextPassword)
        val confirmPasswordEditText: EditText = findViewById(R.id.editTextTextPassword2)
        val registerButton: Button = findViewById(R.id.loginPGLoginBTN)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            } else if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 6 characters long with a number", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                saveUserData(name, email, password)
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, SetProfile::class.java)
                startActivity(intent)
                finish()
            }
        }

        val backButton: FloatingActionButton = findViewById(R.id.backArrowInRegister)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveUserData(name: String, email: String, password: String) {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("fullName", name)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9]).{6,}$" // At least 6 characters and at least one number
        return password.matches(passwordPattern.toRegex())
    }
}
