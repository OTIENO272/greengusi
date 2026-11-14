package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val registerRoot = findViewById<View>(R.id.registerRoot)
        ViewCompat.setOnApplyWindowInsetsListener(registerRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val extraPaddingPx = (24 * resources.displayMetrics.density).toInt()
            view.setPadding(
                systemBars.left + extraPaddingPx,
                systemBars.top + extraPaddingPx,
                systemBars.right + extraPaddingPx,
                maxOf(systemBars.bottom, ime.bottom) + extraPaddingPx
            )
            insets
        }

        val prefs = getSharedPreferences("GreenGusiiPrefs", Context.MODE_PRIVATE)

        val nameInput = findViewById<EditText>(R.id.regNameInput)
        val emailInput = findViewById<EditText>(R.id.regEmailInput)
        val passwordInput = findViewById<EditText>(R.id.regPasswordInput)
        val phoneInput = findViewById<EditText>(R.id.regPhoneInput)
        val countyInput = findViewById<EditText>(R.id.regCountyInput)
        val subCountyInput = findViewById<EditText>(R.id.regSubCountyInput)
        val wardInput = findViewById<EditText>(R.id.regWardInput)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val alreadyHaveAccountLink = findViewById<TextView>(R.id.alreadyHaveAccountLink)

        createAccountButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val phone = phoneInput.text.toString().trim()
            val county = countyInput.text.toString().trim()
            val subCounty = subCountyInput.text.toString().trim()
            val ward = wardInput.text.toString().trim()

            when {
                name.isEmpty() -> {
                    nameInput.error = "Please enter your full name"
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
                county.isEmpty() -> {
                    countyInput.error = "Please enter your County"
                    countyInput.requestFocus()
                }
                subCounty.isEmpty() -> {
                    subCountyInput.error = "Please enter your Sub-County"
                    subCountyInput.requestFocus()
                }
                ward.isEmpty() -> {
                    wardInput.error = "Please enter your Ward"
                    wardInput.requestFocus()
                }
                else -> {
                    // Save credentials locally with SHA-256 secure hashing
                    val hashedPassword = SecurityUtils.hashPassword(password)
                    prefs.edit {
                        putString("USER_NAME", name)
                        putString("USER_EMAIL", email)
                        putString("USER_PASSWORD_HASH", hashedPassword)
                        putString("USER_PHONE", phone)
                        putString("USER_COUNTY", county)
                        putString("USER_SUBCOUNTY", subCounty)
                        putString("USER_WARD", ward)
                        putString("USER_ADDRESS", "$ward, $subCounty ($county)")
                    }

                    Toast.makeText(this, "Registration successful! Welcome, $name.", Toast.LENGTH_SHORT).show()

                    // Navigate directly to GreenGusiiActivity
                    val intent = Intent(this, GreenGusiiActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }

        alreadyHaveAccountLink.setOnClickListener {
            finish()
        }
    }
}
