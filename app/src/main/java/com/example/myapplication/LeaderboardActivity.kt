package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class WardScore(
    val wardName: String,
    val subCounty: String,
    val county: String,
    var basePoints: Int,
    var treesPlanted: Int,
    var plasticClearedKg: Int
)

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)

        val leaderboardRoot = findViewById<View>(R.id.leaderboardRoot)
        ViewCompat.setOnApplyWindowInsetsListener(leaderboardRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val extraPaddingPx = (16 * resources.displayMetrics.density).toInt()
            view.setPadding(
                systemBars.left + extraPaddingPx,
                systemBars.top + extraPaddingPx,
                systemBars.right + extraPaddingPx,
                maxOf(systemBars.bottom, ime.bottom) + extraPaddingPx
            )
            insets
        }

        val prefs = getSharedPreferences("GreenGusiiPrefs", Context.MODE_PRIVATE)
        val userWard = prefs.getString("USER_WARD", "Bogusero") ?: "Bogusero"
        val userPoints = prefs.getInt("ECO_POINTS", 0)

        val wards = mutableListOf(
            WardScore("Bogusero", "Kitutu Chache South", "Kisii", 4850, 2420, 310),
            WardScore("Kiogoro", "Nyaribari Chache", "Kisii", 4620, 2310, 280),
            WardScore("Bonyamatuta", "West Mugirango", "Nyamira", 4290, 2140, 240),
            WardScore("Rigoma", "Kitutu Masaba", "Nyamira", 3980, 1990, 215),
            WardScore("Nyatieko", "Kitutu Chache South", "Kisii", 3740, 1870, 195),
            WardScore("Gesusu", "Nyaribari Masaba", "Kisii", 3410, 1700, 180),
            WardScore("Magombo", "Kitutu Masaba", "Nyamira", 3120, 1560, 150)
        )

        // Credit user's points to their ward
        val matchingWard = wards.find { it.wardName.equals(userWard, ignoreCase = true) }
        if (matchingWard != null) {
            matchingWard.basePoints += userPoints
            matchingWard.treesPlanted += (userPoints / 50)
        } else {
            // Add custom ward
            wards.add(WardScore(userWard, "Local Sub-County", "Gusii", 2500 + userPoints, 1200 + (userPoints / 50), 120))
        }

        // Sort descending
        wards.sortByDescending { it.basePoints }

        val userRank = wards.indexOfFirst { it.wardName.equals(userWard, ignoreCase = true) } + 1
        val userWardScore = wards.find { it.wardName.equals(userWard, ignoreCase = true) }

        findViewById<TextView>(R.id.userWardBadgeText).text =
            "Your Ward: $userWard | Community Rank: #$userRank (Your contribution: +$userPoints pts)"

        val listLayout = findViewById<LinearLayout>(R.id.leaderboardListLayout)

        wards.forEachIndexed { index, ward ->
            val isUserWard = ward.wardName.equals(userWard, ignoreCase = true)
            val rankIcon = when (index) {
                0 -> "🥇"
                1 -> "🥈"
                2 -> "🥉"
                else -> "#${index + 1}"
            }

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(if (isUserWard) Color.parseColor("#C8E6C9") else Color.parseColor("#E8F5E9"))
                setPadding(32, 24, 32, 24)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
            }

            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val titleTv = TextView(this).apply {
                text = "$rankIcon ${ward.wardName} Ward"
                setTextColor(Color.parseColor("#1B5E20"))
                textSize = 16f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val pointsTv = TextView(this).apply {
                text = "${ward.basePoints} pts"
                setTextColor(Color.parseColor("#1B5E20"))
                textSize = 15f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }

            headerRow.addView(titleTv)
            headerRow.addView(pointsTv)

            val detailsTv = TextView(this).apply {
                val userTag = if (isUserWard) " ★ YOUR NEIGHBORHOOD" else ""
                text = "📍 ${ward.subCounty}, ${ward.county}$userTag\n🌲 Trees Planted: ${ward.treesPlanted} | ♻️ Plastic Cleared: ${ward.plasticClearedKg} kg"
                setTextColor(Color.parseColor("#37474F"))
                textSize = 12f
                setLineSpacing(3f, 1f)
                setPadding(0, 4, 0, 0)
            }

            card.addView(headerRow)
            card.addView(detailsTv)
            listLayout.addView(card)
        }

        findViewById<Button>(R.id.shareLeaderboardButton).setOnClickListener {
            val shareText = """
                🌿 *GREEN GUSII WARD LEADERBOARD CHALLENGE* 🌿
                ----------------------------------------------
                🏡 *Our Ward:* $userWard ($userRank of ${wards.size} Wards)
                ⭐ *Total Ward Impact:* ${userWardScore?.basePoints ?: 0} pts
                🌲 *Trees Planted:* ${userWardScore?.treesPlanted ?: 0} seedlings
                ♻️ *Plastic Waste Cleared:* ${userWardScore?.plasticClearedKg ?: 0} kg

                Let's unite as neighbors to protect our springs and beat other wards to the top spot!
                ⚡ *Join the challenge on the Green Gusii Community App.*
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Green Gusii Leaderboard - Rally for $userWard!")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Rally Ward on WhatsApp / Social Media"))
        }

        findViewById<Button>(R.id.leaderboardBackToHubButton).setOnClickListener {
            finish()
        }
    }
}
