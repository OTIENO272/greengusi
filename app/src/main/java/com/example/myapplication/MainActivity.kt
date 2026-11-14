package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainLayout = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
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

        val emailInput = findViewById<EditText>(R.id.loginEmailInput)
        val passwordInput = findViewById<EditText>(R.id.loginPasswordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPasswordLink = findViewById<TextView>(R.id.forgotPasswordLink)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        // Pre-fill registered email if available
        val savedEmail = prefs.getString("USER_EMAIL", null)
        if (!savedEmail.isNullOrEmpty()) {
            emailInput.setText(savedEmail)
        }

        // Login Handler
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            when {
                email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Please enter a valid email address"
                    emailInput.requestFocus()
                }
                password.isEmpty() -> {
                    passwordInput.error = "Please enter your password"
                    passwordInput.requestFocus()
                }
                else -> {
                    hideKeyboard()

                    val registeredEmail = prefs.getString("USER_EMAIL", null)
                    val registeredPassword = prefs.getString("USER_PASSWORD", null)
                    val registeredName = prefs.getString("USER_NAME", "Champion")

                    if (registeredEmail != null && registeredPassword != null) {
                        if (email.equals(registeredEmail, ignoreCase = true) && password == registeredPassword) {
                            Toast.makeText(this, "Welcome back, $registeredName!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, GreenGusiiActivity::class.java))
                        } else if (!email.equals(registeredEmail, ignoreCase = true)) {
                            emailInput.error = "No account found with this email"
                            emailInput.requestFocus()
                        } else {
                            passwordInput.error = "Incorrect password"
                            passwordInput.requestFocus()
                        }
                    } else {
                        // Demo / First-time fallback: log in directly
                        Toast.makeText(this, "Logged in to Green Gusii Hub!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, GreenGusiiActivity::class.java))
                    }
                }
            }
        }

        // Forgot Password Handler
        forgotPasswordLink.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val dialogInput = EditText(this).apply {
                hint = "Enter your registered email"
                setText(email)
                setPadding(50, 40, 50, 40)
            }

            AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("We will send a password reset verification link to your email:")
                .setView(dialogInput)
                .setPositiveButton("Send Link") { _, _ ->
                    val targetEmail = dialogInput.text.toString().trim()
                    if (targetEmail.isNotEmpty()) {
                        Toast.makeText(
                            this,
                            "Password recovery link sent to $targetEmail ✉️",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Don't have an account -> Register Link
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
