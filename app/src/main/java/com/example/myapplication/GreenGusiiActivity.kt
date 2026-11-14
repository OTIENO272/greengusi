package com.example.myapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class GreenGusiiActivity : AppCompatActivity() {

    private val CHANNEL_ID = "seasonal_planting_channel"

    private lateinit var langEnButton: Button
    private lateinit var langSwButton: Button
    private lateinit var langGusiiButton: Button

    private lateinit var hubHeaderTitle: TextView
    private lateinit var hubWelcomeText: TextView
    private lateinit var hubLocationText: TextView
    private lateinit var seasonalAlertTitle: TextView
    private lateinit var seasonalAlertBody: TextView
    private lateinit var ecoTrackerTitle: TextView
    private lateinit var pointsLabel: TextView
    private lateinit var rankBadge: TextView
    private lateinit var pledgesPrompt: TextView
    private lateinit var fieldToolsLabel: TextView
    private lateinit var shareCertificateButton: Button

    private lateinit var pledge1: CheckBox
    private lateinit var pledge2: CheckBox
    private lateinit var pledge3: CheckBox
    private lateinit var pledge4: CheckBox
    private lateinit var pledge5: CheckBox
    private lateinit var progressBar: ProgressBar

    private var currentLang = "EN" // "EN", "SW", "GU"

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            postPlantingNotification()
        } else {
            Toast.makeText(this, "Notification permission is required for seasonal alerts", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_green_gusii)

        createNotificationChannel()

        val hubRoot = findViewById<View>(R.id.hubRoot)
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
        currentLang = prefs.getString("APP_LANG", "EN") ?: "EN"

        // Initialize view references
        langEnButton = findViewById(R.id.langEnButton)
        langSwButton = findViewById(R.id.langSwButton)
        langGusiiButton = findViewById(R.id.langGusiiButton)

        hubHeaderTitle = findViewById(R.id.hubHeaderTitle)
        hubWelcomeText = findViewById(R.id.hubWelcomeText)
        hubLocationText = findViewById(R.id.hubLocationText)
        seasonalAlertTitle = findViewById(R.id.seasonalAlertTitle)
        seasonalAlertBody = findViewById(R.id.seasonalAlertBody)
        ecoTrackerTitle = findViewById(R.id.ecoTrackerTitle)
        pointsLabel = findViewById(R.id.pointsLabel)
        rankBadge = findViewById(R.id.rankBadge)
        pledgesPrompt = findViewById(R.id.pledgesPrompt)
        fieldToolsLabel = findViewById(R.id.fieldToolsLabel)
        shareCertificateButton = findViewById(R.id.shareCertificateButton)

        pledge1 = findViewById(R.id.pledge1)
        pledge2 = findViewById(R.id.pledge2)
        pledge3 = findViewById(R.id.pledge3)
        pledge4 = findViewById(R.id.pledge4)
        pledge5 = findViewById(R.id.pledge5)
        progressBar = findViewById(R.id.ecoProgressBar)

        val userName = prefs.getString("USER_NAME", "Champion") ?: "Champion"
        val userWard = prefs.getString("USER_WARD", "Bogusero") ?: "Bogusero"
        val userSubCounty = prefs.getString("USER_SUBCOUNTY", "Kitutu Chache South") ?: "Kitutu Chache South"
        val userCounty = prefs.getString("USER_COUNTY", "Kisii") ?: "Kisii"

        hubWelcomeText.text = "Welcome, $userName!"
        hubLocationText.text = "Ward: $userWard | $userSubCounty, $userCounty"

        val pledgesList = listOf(pledge1, pledge2, pledge3, pledge4, pledge5)

        fun updateEcoRank() {
            val checkedCount = pledgesList.count { it.isChecked }
            val points = checkedCount * 100
            progressBar.progress = points
            pointsLabel.text = "Impact: $points / 500 pts"

            val rankText = when (currentLang) {
                "SW" -> when (points) {
                    0 -> "Mwanachama Mpya"
                    100 -> "Mpelelezi Mazingira 🌱"
                    200 -> "Mlinzi wa Maeneo Oevu 💧"
                    300, 400 -> "Mhifadhi Nyanda za Juu ⛰️"
                    500 -> "Mlinzi Mkuu wa Misitu 🌳✨"
                    else -> "Mpelelezi Mazingira 🌱"
                }
                "GU" -> when (points) {
                    0 -> "Omomembara Omohyaka"
                    100 -> "Omorobereri Bw'Enyomberere 🌱"
                    200 -> "Omorindi bw'Amache 💧"
                    300, 400 -> "Omorindi bw'Ebigori ⛰️"
                    500 -> "Omorindi Munene bw'Emete 🌳✨"
                    else -> "Omorobereri Bw'Enyomberere 🌱"
                }
                else -> when (points) {
                    0 -> "New Member"
                    100 -> "Eco Scout 🌱"
                    200 -> "Wetland Protector 💧"
                    300, 400 -> "Highland Conservationist ⛰️"
                    500 -> "Gusii Forest Guardian 🌳✨"
                    else -> "Eco Scout 🌱"
                }
            }
            rankBadge.text = rankText

            if (points == 500) {
                shareCertificateButton.setBackgroundColor(Color.parseColor("#D4AF37"))
                shareCertificateButton.text = "🏆 Share Green Champion Certificate (UNLOCKED!)"
            } else {
                shareCertificateButton.setBackgroundColor(Color.parseColor("#9E9E9E"))
                shareCertificateButton.text = "🏆 Green Champion Certificate ($points/500 pts)"
            }

            prefs.edit {
                putInt("ECO_POINTS", points)
                pledgesList.forEachIndexed { index, checkBox ->
                    putBoolean("PLEDGE_$index", checkBox.isChecked)
                }
            }
        }

        // Restore pledges
        pledgesList.forEachIndexed { index, checkBox ->
            checkBox.isChecked = prefs.getBoolean("PLEDGE_$index", false)
            checkBox.setOnCheckedChangeListener { _, _ -> updateEcoRank() }
        }

        // Language Switcher Handler
        fun applyLanguage(lang: String) {
            currentLang = lang
            prefs.edit { putString("APP_LANG", lang) }

            // Highlight selected button
            val selectedColor = Color.parseColor("#1B5E20")
            val defaultColor = Color.parseColor("#264D3F")
            langEnButton.setBackgroundColor(if (lang == "EN") selectedColor else defaultColor)
            langSwButton.setBackgroundColor(if (lang == "SW") selectedColor else defaultColor)
            langGusiiButton.setBackgroundColor(if (lang == "GU") selectedColor else defaultColor)

            when (lang) {
                "SW" -> {
                    hubHeaderTitle.text = "KITUO CHA MAZINGIRA YA GUSII"
                    hubWelcomeText.text = "Karibu, $userName!"
                    hubLocationText.text = "Wadi: $userWard | $userSubCounty, $userCounty"
                    seasonalAlertTitle.text = "🌧️ Msimu wa Mvua na Upandaji Miti"
                    seasonalAlertBody.text = "Unyevu wa udongo kwenye nyanda za juu za Gusii sasa ni thabiti kwa kupandikiza Grevillea na Parachichi ya Hass."
                    ecoTrackerTitle.text = "🌿 Kifuatilia Athari za Mazingira"
                    pledgesPrompt.text = "Weka alama kwenye ahadi zako za mazingira:"
                    pledge1.text = "Panda miti ya asili kando ya mito na chemchemi (+100 pts)"
                    pledge2.text = "Ng'oa/epuka mikaratusi ndani ya mita 30 kutoka chemchemi (+100 pts)"
                    pledge3.text = "Weka matuta na mbolea ya samadi kwenye chai/kahawa (+100 pts)"
                    pledge4.text = "Shiriki katika usafi wa mito na vijito vya jamii (+100 pts)"
                    pledge5.text = "Ripoti utupaji wa taka au uharibifu wa chemchemi (+100 pts)"
                    fieldToolsLabel.text = "ZANA ZA NCHA ZA JAMII"
                }
                "GU" -> {
                    hubHeaderTitle.text = "EKIRO BWI'CHONGA Y'OMOGUSII"
                    hubWelcomeText.text = "Mbuya Orare, $userName!"
                    hubLocationText.text = "Ewadi: $userWard | $userSubCounty, $userCounty"
                    seasonalAlertTitle.text = "🌧️ Engaki y'Embura n'Okohanda Emete"
                    seasonalAlertBody.text = "Oborere bw'amache aase emegondo nigo borigwa hande emete y'echai, emikatusi ekerigwa, n'ebirobia bia Avocado."
                    ecoTrackerTitle.text = "🌿 Okorora Oboronge bw'Enyomberere"
                    pledgesPrompt.text = "Simba emechando yao y'okorenda enyomberere:"
                    pledge1.text = "Handa emete y'omogusii aase emigera n'amache (+100 pts)"
                    pledge2.text = "Ruta emikatusi aase obwenge bw'amache (+100 pts)"
                    pledge3.text = "Beka amataratara n'omanyua aase amani y'echai (+100 pts)"
                    pledge4.text = "Tanga amaboko gosea enyomberere y'omochie (+100 pts)"
                    pledge5.text = "Ripoti abasasemi b'amache n'enyomberere (+100 pts)"
                    fieldToolsLabel.text = "EBINTU BIE'CHONGA Y'OMOCHIE"
                }
                else -> {
                    hubHeaderTitle.text = "GREEN GUSII ACTION HUB"
                    hubWelcomeText.text = "Welcome, $userName!"
                    hubLocationText.text = "Ward: $userWard | $userSubCounty, $userCounty"
                    seasonalAlertTitle.text = "🌧️ Planting & Rainfall Season"
                    seasonalAlertBody.text = "Soil moisture in Gusii highlands is primed for transplanting Grevillea and Hass Avocado seedlings."
                    ecoTrackerTitle.text = "🌿 Live Eco-Impact Tracker"
                    pledgesPrompt.text = "Check off your local environmental pledges:"
                    pledge1.text = "Plant indigenous trees along riverbanks (+100 pts)"
                    pledge2.text = "Avoid / replace Eucalyptus within 30m of springs (+100 pts)"
                    pledge3.text = "Practice terracing & compost manuring in tea/coffee (+100 pts)"
                    pledge4.text = "Participate in community wetland & stream cleanups (+100 pts)"
                    pledge5.text = "Report waste dumping or hazardous encroachments (+100 pts)"
                    fieldToolsLabel.text = "COMMUNITY FIELD TOOLS"
                }
            }
            updateEcoRank()
        }

        langEnButton.setOnClickListener { applyLanguage("EN") }
        langSwButton.setOnClickListener { applyLanguage("SW") }
        langGusiiButton.setOnClickListener { applyLanguage("GU") }

        applyLanguage(currentLang)

        // Seasonal Notification Trigger
        findViewById<Button>(R.id.triggerNotificationButton).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    postPlantingNotification()
                } else {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                postPlantingNotification()
            }
        }

        // Digital Certificate Generation & Sharing
        shareCertificateButton.setOnClickListener {
            val points = prefs.getInt("ECO_POINTS", 0)
            if (points < 500) {
                Toast.makeText(this, "Complete all 5 pledges (500 pts) to unlock your certificate! Currently: $points pts", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val certUri = CertificateGenerator.generateAndShareCertificate(
                this, userName, userWard, userSubCounty, userCounty
            )

            if (certUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, certUri)
                    putExtra(Intent.EXTRA_TEXT, "I just earned the GUSII FOREST GUARDIAN rank on the Green Gusii app! 🌳✨ Join us to reforest our highlands.")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Green Champion Certificate"))
            } else {
                Toast.makeText(this, "Failed to generate certificate", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigation Routes
        findViewById<Button>(R.id.openReportToolButton).setOnClickListener {
            startActivity(Intent(this, ReportIssueActivity::class.java))
        }

        findViewById<Button>(R.id.openTreePlannerButton).setOnClickListener {
            startActivity(Intent(this, TreePlannerActivity::class.java))
        }

        findViewById<Button>(R.id.openNurseryDirectoryButton).setOnClickListener {
            startActivity(Intent(this, NurseryDirectoryActivity::class.java))
        }

        findViewById<Button>(R.id.openLeaderboardButton).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            finish()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Planting & Rainfall Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Highland soil moisture and seasonal agroforestry alerts for Kisii and Nyamira"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun postPlantingNotification() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0-based

        val (title, body) = when (month) {
            in Calendar.MARCH..Calendar.MAY -> {
                "🌧️ Chiri Rains Alert (March–May)" to "Long rains have arrived in Gusii! Peak soil moisture for planting Grevillea and Hass Avocado seedlings on highland slopes."
            }
            in Calendar.OCTOBER..Calendar.DECEMBER -> {
                "🌧️ Egesarate Short Rains Alert (Oct–Dec)" to "Short rains season active! Optimal window for planting Bamboo along riverbanks and riparian buffer strips."
            }
            else -> {
                "🌱 Nursery & Soil Preparation Season" to "Dry spell ahead: Prepare farm pits, apply compost, and order seedlings from your local nursery directory."
            }
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(101, builder.build())
        Toast.makeText(this, "Seasonal Planting Alert notification sent! 🔔", Toast.LENGTH_SHORT).show()
    }
}
