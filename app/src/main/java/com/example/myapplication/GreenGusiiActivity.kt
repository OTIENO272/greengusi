package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GreenGusiiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_green_gusii)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.hubRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("GreenGusiiPrefs", MODE_PRIVATE)
        val userName = prefs.getString("USER_NAME", "Champion")
        val userAddress = prefs.getString("USER_ADDRESS", "Gusii Region")

        findViewById<TextView>(R.id.hubWelcomeText).text = "Welcome, $userName!"
        findViewById<TextView>(R.id.hubLocationText).text = "Active Base: $userAddress"
    }
}
