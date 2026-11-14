package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val scrollView = findViewById<ScrollView>(R.id.mainScrollView)
        val mainLayout = findViewById<View>(R.id.main)

        // Proper Edge-to-Edge inset handling (accounts for system bars & IME/keyboard)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // Convert 24dp to pixels
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

        // Connect Form Input Views
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val countyInput = findViewById<EditText>(R.id.countyInput)
        val subCountyInput = findViewById<EditText>(R.id.subCountyInput)
        val wardInput = findViewById<EditText>(R.id.wardInput)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Connect Summary Card Views
        val summaryCard = findViewById<LinearLayout>(R.id.summaryCard)
        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val detailsText = findViewById<TextView>(R.id.detailsText)
        val enterHubButton = findViewById<Button>(R.id.enterHubButton)

        // Restore previously saved details if available
        val savedName = prefs.getString("USER_NAME", null)
        if (!savedName.isNullOrEmpty()) {
            nameInput.setText(savedName)
            emailInput.setText(prefs.getString("USER_EMAIL", ""))
            phoneInput.setText(prefs.getString("USER_PHONE", ""))
            countyInput.setText(prefs.getString("USER_COUNTY", ""))
            subCountyInput.setText(prefs.getString("USER_SUBCOUNTY", ""))
            wardInput.setText(prefs.getString("USER_WARD", ""))
        }

        submitButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val phone = phoneInput.text.toString().trim()
            val county = countyInput.text.toString().trim()
            val subCounty = subCountyInput.text.toString().trim()
            val ward = wardInput.text.toString().trim()

            // Form validation
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
                    // Hide soft keyboard
                    hideKeyboard()

                    // Save details via AndroidX KTX edit extension
                    prefs.edit {
                        putString("USER_NAME", name)
                        putString("USER_EMAIL", email)
                        putString("USER_PHONE", phone)
                        putString("USER_COUNTY", county)
                        putString("USER_SUBCOUNTY", subCounty)
                        putString("USER_WARD", ward)
                        putString("USER_ADDRESS", "$ward, $subCounty ($county)")
                    }

                    val maskedPassword = "•".repeat(password.length)

                    welcomeText.text = "Welcome to Green Gusii, $name!"
                    detailsText.text = """
                        Name: $name
                        Email: $email
                        Password: $maskedPassword
                        Phone: $phone
                        County: $county
                        Sub-County: $subCounty
                        Ward: $ward
                    """.trimIndent()

                    summaryCard.visibility = View.VISIBLE

                    // Smooth scroll down to reveal the summary card
                    summaryCard.post {
                        (mainLayout.parent as? ScrollView)?.smoothScrollTo(0, summaryCard.bottom)
                    }
                }
            }
        }

        enterHubButton.setOnClickListener {
            try {
                startActivity(Intent(this, GreenGusiiActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "GreenGusiiActivity not found! Please check AndroidManifest.xml",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        currentFocus?.let { view ->
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}