package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.roundToInt

class TreePlannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tree_planner)

        val plannerRoot = findViewById<View>(R.id.plannerRoot)
        ViewCompat.setOnApplyWindowInsetsListener(plannerRoot) { view, insets ->
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

        val acresInput = findViewById<EditText>(R.id.acresInput)
        val calculatePlanButton = findViewById<Button>(R.id.calculatePlanButton)
        val resultsCard = findViewById<LinearLayout>(R.id.resultsCard)
        val resultsTitleText = findViewById<TextView>(R.id.resultsTitleText)
        val totalSeedlingsText = findViewById<TextView>(R.id.totalSeedlingsText)
        val grevilleaText = findViewById<TextView>(R.id.grevilleaText)
        val avocadoText = findViewById<TextView>(R.id.avocadoText)
        val crotonBambooText = findViewById<TextView>(R.id.crotonBambooText)
        val sharePlanButton = findViewById<Button>(R.id.sharePlanButton)

        fun calculateAndDisplay(acres: Double) {
            if (acres <= 0.0) {
                acresInput.error = "Please enter an acreage greater than 0"
                acresInput.requestFocus()
                return
            }

            hideKeyboard()

            val grevilleaCount = (acres * 35).roundToInt()
            val avocadoCount = (acres * 50).roundToInt()
            val crotonCount = (acres * 20).roundToInt()
            val totalCount = grevilleaCount + avocadoCount + crotonCount

            resultsTitleText.text = "Agroforestry Plan for $acres Acre(s)"
            totalSeedlingsText.text = "Total Seedlings Needed: $totalCount"
            grevilleaText.text = "🌳 Grevillea Robusta: $grevilleaCount seedlings\n(Boundary windbreak & light canopy; deep roots avoid crop competition)"
            avocadoText.text = "🥑 Hass Avocado / Macadamia: $avocadoCount seedlings\n(High-value cash crops integrated with tea/coffee strips)"
            crotonBambooText.text = "🎋 Croton Megalocarpus / Bamboo: $crotonCount seedlings\n(Riverbank stabilization & erosion control; replaces eucalyptus)"

            resultsCard.visibility = View.VISIBLE

            resultsCard.post {
                (plannerRoot.parent as? ScrollView)?.smoothScrollTo(0, resultsCard.bottom)
            }

            sharePlanButton.setOnClickListener {
                val planSummary = """
                    🌱 *GREEN GUSII HIGHLAND AGROFORESTRY PLAN* 🌱
                    ---------------------------------------------
                    📍 *Land Size:* $acres Acre(s)
                    📦 *Total Seedlings:* $totalCount seedlings
                    
                    1. 🌳 *Grevillea Robusta:* $grevilleaCount seedlings
                       • Boundary shade, timber, and wind protection.
                    2. 🥑 *Hass Avocado / Macadamia:* $avocadoCount seedlings
                       • High-value cash crop integrated with tea/coffee.
                    3. 🎋 *Croton Megalocarpus / Giant Bamboo:* $crotonCount seedlings
                       • Riverbank, slope stabilization, and stream preservation.
                       
                    ☀️ *Spacing & Crop Yield Protection:*
                    Maintain at least 8m x 8m grid spacing between trees so 65%+ sunlight penetrates down to nourish tea bushes, coffee berries, and bananas.
                    
                    ⚡ *Calculated via Green Gusii Community App*
                """.trimIndent()

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Green Gusii Agroforestry Plan ($acres Acres)")
                    putExtra(Intent.EXTRA_TEXT, planSummary)
                }
                startActivity(Intent.createChooser(intent, "Share Agroforestry Plan via..."))
            }
        }

        calculatePlanButton.setOnClickListener {
            val inputStr = acresInput.text.toString().trim()
            val acres = inputStr.toDoubleOrNull()
            if (acres == null || acres <= 0.0) {
                acresInput.error = "Please enter a valid acreage (e.g. 1.5)"
                acresInput.requestFocus()
            } else {
                calculateAndDisplay(acres)
            }
        }

        // Quick presets
        findViewById<Button>(R.id.presetHalfAcre).setOnClickListener {
            acresInput.setText("0.5")
            calculateAndDisplay(0.5)
        }
        findViewById<Button>(R.id.presetOneAcre).setOnClickListener {
            acresInput.setText("1.0")
            calculateAndDisplay(1.0)
        }
        findViewById<Button>(R.id.presetTwoAcres).setOnClickListener {
            acresInput.setText("2.0")
            calculateAndDisplay(2.0)
        }
        findViewById<Button>(R.id.presetFiveAcres).setOnClickListener {
            acresInput.setText("5.0")
            calculateAndDisplay(5.0)
        }

        findViewById<Button>(R.id.plannerBackToHubButton).setOnClickListener {
            finish()
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
