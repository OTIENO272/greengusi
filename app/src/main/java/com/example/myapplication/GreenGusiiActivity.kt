package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GreenGusiiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_green_gusii)

        val hubRoot = findViewById<android.view.View>(R.id.hubRoot)
        ViewCompat.setOnApplyWindowInsetsListener(hubRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingPx = (20 * resources.displayMetrics.density).toInt()
            view.setPadding(
                systemBars.left + extraPaddingPx,
                systemBars.top + extraPaddingPx,
                systemBars.right + extraPaddingPx,
                systemBars.bottom + extraPaddingPx
            )
            insets
        }

        val prefs = getSharedPreferences("GreenGusiiPrefs", Context.MODE_PRIVATE)

        // Personalize dashboard
        val userName = prefs.getString("USER_NAME", "Champion") ?: "Champion"
        val userWard = prefs.getString("USER_WARD", "Bogusero") ?: "Bogusero"
        val userSubCounty = prefs.getString("USER_SUBCOUNTY", "Kitutu Chache South") ?: "Kitutu Chache South"
        val userCounty = prefs.getString("USER_COUNTY", "Kisii") ?: "Kisii"

        findViewById<TextView>(R.id.hubWelcomeText).text = "Welcome, $userName!"
        findViewById<TextView>(R.id.hubLocationText).text = "Ward: $userWard | $userSubCounty, $userCounty"

        // Eco-Impact Tracker
        val pointsLabel = findViewById<TextView>(R.id.pointsLabel)
        val rankBadge = findViewById<TextView>(R.id.rankBadge)
        val progressBar = findViewById<ProgressBar>(R.id.ecoProgressBar)

        val pledges = listOf(
            findViewById<CheckBox>(R.id.pledge1),
            findViewById<CheckBox>(R.id.pledge2),
            findViewById<CheckBox>(R.id.pledge3),
            findViewById<CheckBox>(R.id.pledge4),
            findViewById<CheckBox>(R.id.pledge5)
        )

        fun updateEcoRank() {
            val checkedCount = pledges.count { it.isChecked }
            val points = checkedCount * 100
            progressBar.progress = points
            pointsLabel.text = "Impact: $points / 500 pts"

            val rankText = when (points) {
                0 -> "New Member"
                100 -> "Eco Scout 🌱"
                200 -> "Wetland Protector 💧"
                300, 400 -> "Highland Conservationist ⛰️"
                500 -> "Gusii Forest Guardian 🌳✨"
                else -> "Eco Scout 🌱"
            }
            rankBadge.text = rankText

            // Save state
            prefs.edit {
                putInt("ECO_POINTS", points)
                pledges.forEachIndexed { index, checkBox ->
                    putBoolean("PLEDGE_$index", checkBox.isChecked)
                }
            }
        }

        // Restore saved pledges
        pledges.forEachIndexed { index, checkBox ->
            checkBox.isChecked = prefs.getBoolean("PLEDGE_$index", false)
            checkBox.setOnCheckedChangeListener { _, _ -> updateEcoRank() }
        }
        updateEcoRank()

        // Routes to Field Tools
        findViewById<Button>(R.id.openReportToolButton).setOnClickListener {
            startActivity(Intent(this, ReportIssueActivity::class.java))
        }

        findViewById<Button>(R.id.openTreePlannerButton).setOnClickListener {
            startActivity(Intent(this, TreePlannerActivity::class.java))
        }

        findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            finish()
        }
    }
}
