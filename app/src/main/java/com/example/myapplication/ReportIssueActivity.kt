package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ReportIssueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_issue)

        val reportRoot = findViewById<android.view.View>(R.id.reportRoot)
        ViewCompat.setOnApplyWindowInsetsListener(reportRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val extraPaddingPx = (20 * resources.displayMetrics.density).toInt()
            view.setPadding(
                systemBars.left + extraPaddingPx,
                systemBars.top + extraPaddingPx,
                systemBars.right + extraPaddingPx,
                maxOf(systemBars.bottom, ime.bottom) + extraPaddingPx
            )
            insets
        }

        val prefs = getSharedPreferences("GreenGusiiPrefs", Context.MODE_PRIVATE)
        val reporterName = prefs.getString("USER_NAME", "Green Gusii Resident") ?: "Resident"
        val reporterPhone = prefs.getString("USER_PHONE", "Not provided") ?: "Not provided"
        val reporterEmail = prefs.getString("USER_EMAIL", "Not provided") ?: "Not provided"
        val reporterCounty = prefs.getString("USER_COUNTY", "Kisii/Nyamira") ?: "Kisii/Nyamira"
        val reporterSubCounty = prefs.getString("USER_SUBCOUNTY", "") ?: ""
        val reporterWard = prefs.getString("USER_WARD", "") ?: ""

        val subCounties = arrayOf(
            "Kitutu Chache South",
            "Kitutu Chache North",
            "Kisii Central",
            "Nyaribari Chache",
            "Nyaribari Masaba",
            "Bobasi",
            "Bomachoge Borabu",
            "Bomachoge Chache",
            "South Mugirango",
            "Nyamira Town",
            "Kitutu Masaba",
            "West Mugirango",
            "North Mugirango",
            "Borabu"
        )

        val threats = arrayOf(
            "Eucalyptus near Water Springs / Streams (Severe Drying)",
            "River Dumping & Industrial / Agricultural Chemical Runoff",
            "Wetland & Riparian Encroachment / Drainage",
            "Hilltop Deforestation & Charcoal Burning",
            "Unregulated Stone Quarrying & Landslide Hazard"
        )

        val subCountySpinner = findViewById<Spinner>(R.id.subCountySpinner)
        val hazardTypeSpinner = findViewById<Spinner>(R.id.hazardTypeSpinner)
        val landmarkInput = findViewById<EditText>(R.id.landmarkInput)
        val hazardDescriptionInput = findViewById<EditText>(R.id.hazardDescriptionInput)

        val subCountyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subCounties)
        subCountySpinner.adapter = subCountyAdapter

        // Pre-select if matches user's sub-county
        val defaultIndex = subCounties.indexOfFirst { it.equals(reporterSubCounty, ignoreCase = true) }
        if (defaultIndex >= 0) {
            subCountySpinner.setSelection(defaultIndex)
        }

        val threatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, threats)
        hazardTypeSpinner.adapter = threatAdapter

        findViewById<Button>(R.id.broadcastAlertButton).setOnClickListener {
            val selectedSubCounty = subCountySpinner.selectedItem.toString()
            val selectedThreat = hazardTypeSpinner.selectedItem.toString()
            val landmark = landmarkInput.text.toString().trim()
            val description = hazardDescriptionInput.text.toString().trim()

            if (landmark.isEmpty()) {
                landmarkInput.error = "Please specify a landmark, river, or village"
                landmarkInput.requestFocus()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                hazardDescriptionInput.error = "Please provide brief details of the observed hazard"
                hazardDescriptionInput.requestFocus()
                return@setOnClickListener
            }

            val reportMessage = """
                🚨 *GREEN GUSII ENVIRONMENTAL HAZARD ALERT* 🚨
                --------------------------------------------------
                📍 *Location:* $selectedSubCounty ($reporterCounty)
                📌 *Landmark/Village:* $landmark
                ⚠️ *Threat:* $selectedThreat
                📝 *Observations:* $description

                👤 *Reported By:* $reporterName
                📞 *Phone:* $reporterPhone
                ✉️ *Email:* $reporterEmail
                🏡 *Resident Base:* $reporterWard, $selectedSubCounty

                ⚡ *Sent via Green Gusii Community App* for urgent attention by NEMA & County Environment Officers.
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "URGENT: Green Gusii Environmental Alert - $selectedSubCounty")
                putExtra(Intent.EXTRA_TEXT, reportMessage)
            }

            startActivity(Intent.createChooser(shareIntent, "Broadcast Alert via WhatsApp, SMS, or Email"))
            Toast.makeText(this, "Opening broadcast channels...", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.backToHubButton).setOnClickListener {
            finish()
        }
    }
}
