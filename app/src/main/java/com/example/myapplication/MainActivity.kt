package com.example.myapplication

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + 48,
                systemBars.top + 48,
                systemBars.right + 48,
                systemBars.bottom + 48
            )
            insets
        }

        // Connect your XML form elements
        val nameInput = findViewById<EditText>(R.id.editTextText)
        val emailInput = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val passwordInput = findViewById<EditText>(R.id.editTextTextPassword4)
        val phoneInput = findViewById<EditText>(R.id.editTextPhone)
        val addressInput = findViewById<EditText>(R.id.editTextTextPostalAddress)
        val submitButton = findViewById<Button>(R.id.button)

        // Connect the new summary card views
        val summaryCard = findViewById<LinearLayout>(R.id.summaryCard)
        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val detailsText = findViewById<TextView>(R.id.detailsText)

        // Handle Submit button click
        submitButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val phone = phoneInput.text.toString().trim()
            val address = addressInput.text.toString().trim()

            when {
                name.isEmpty() -> {
                    nameInput.error = "Please enter your name"
                    nameInput.requestFocus()
                }
                email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Please enter a valid email address"
                    emailInput.requestFocus()
                }
                password.length < 6 -> {
                    passwordInput.error = "Password must be at least 6 characters"
                    passwordInput.requestFocus()
                }
                phone.isEmpty() -> {
                    phoneInput.error = "Please enter your phone number"
                    phoneInput.requestFocus()
                }
                address.isEmpty() -> {
                    addressInput.error = "Please enter your postal address"
                    addressInput.requestFocus()
                }
                else -> {
                    val maskedPassword = "•".repeat(password.length)

                    welcomeText.text = "Welcome to Green Gusii, $name!"
                    detailsText.text = """
                        Name: $name
                        Email: $email
                        Password: $maskedPassword
                        Phone: $phone
                        Address: $address
                    """.trimIndent()

                    summaryCard.visibility = View.VISIBLE
                }
            }
        }
    }
}
