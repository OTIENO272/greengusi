package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class Nursery(
    val name: String,
    val town: String,
    val subCounty: String,
    val county: String,
    val phone: String,
    val whatsapp: String,
    val seedlings: List<String>
)

class NurseryDirectoryActivity : AppCompatActivity() {

    private val allNurseries = listOf(
        Nursery(
            name = "Kisii Central Agroforestry Hub",
            town = "Kisii Town (Daraja Mbili bypass)",
            subCounty = "Kisii Central",
            county = "Kisii",
            phone = "+254712345678",
            whatsapp = "254712345678",
            seedlings = listOf(
                "Grevillea Robusta (KSh 15)",
                "Hass Avocado grafted (KSh 120)",
                "Giant Bamboo for rivers (KSh 80)",
                "Croton Megalocarpus (KSh 20)"
            )
        ),
        Nursery(
            name = "Manga Ridge Community Nursery",
            town = "Manga / Kemera",
            subCounty = "Manga",
            county = "Nyamira",
            phone = "+254723456789",
            whatsapp = "254723456789",
            seedlings = listOf(
                "Macadamia integrifolia (KSh 150)",
                "Grevillea Robusta (KSh 15)",
                "Indigenous Croton (KSh 25)",
                "Prunus Africana (KSh 35)"
            )
        ),
        Nursery(
            name = "Keroka Highland Riparian Nursery",
            town = "Keroka Town (Near Nyangusu Rd)",
            subCounty = "Nyaribari Masaba",
            county = "Kisii / Nyamira Border",
            phone = "+254734567890",
            whatsapp = "254734567890",
            seedlings = listOf(
                "Riverbank Bamboo culms (KSh 70)",
                "Hass Avocado (KSh 110)",
                "Grevillea shade trees (KSh 15)",
                "Markhamia Lutea (KSh 25)"
            )
        ),
        Nursery(
            name = "Ogembo Tea Zone Seedling Center",
            town = "Ogembo Town",
            subCounty = "Bomachoge Chache",
            county = "Kisii",
            phone = "+254745678901",
            whatsapp = "254745678901",
            seedlings = listOf(
                "Grevillea boundary timber (KSh 15)",
                "Grafted Hass Avocado (KSh 100)",
                "Cordia Africana (KSh 30)",
                "Water-friendly Bamboo (KSh 75)"
            )
        ),
        Nursery(
            name = "Suneka Eco-Guardians Nursery",
            town = "Suneka / Riana Junction",
            subCounty = "Bonchari",
            county = "Kisii",
            phone = "+254756789012",
            whatsapp = "254756789012",
            seedlings = listOf(
                "Indigenous Croton (KSh 20)",
                "Grafted Hass Avocado (KSh 110)",
                "Grevillea windbreak (KSh 15)",
                "Bamboo rhizomes (KSh 80)"
            )
        ),
        Nursery(
            name = "Nyamira Sironga Green Hub",
            town = "Nyamira Town (Sironga Junction)",
            subCounty = "West Mugirango",
            county = "Nyamira",
            phone = "+254767890123",
            whatsapp = "254767890123",
            seedlings = listOf(
                "Riparian Bamboo (KSh 75)",
                "Hass Avocado (KSh 120)",
                "Grevillea Robusta (KSh 15)",
                "Macadamia nut trees (KSh 140)"
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nursery_directory)

        val nurseryRoot = findViewById<View>(R.id.nurseryRoot)
        ViewCompat.setOnApplyWindowInsetsListener(nurseryRoot) { view, insets ->
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

        val nurseryListLayout = findViewById<LinearLayout>(R.id.nurseryListLayout)
        val searchInput = findViewById<EditText>(R.id.searchNurseryInput)

        fun renderNurseries(filter: String = "") {
            nurseryListLayout.removeAllViews()

            val filtered = allNurseries.filter { nursery ->
                val query = filter.lowercase().trim()
                query.isEmpty() ||
                        nursery.name.lowercase().contains(query) ||
                        nursery.town.lowercase().contains(query) ||
                        nursery.subCounty.lowercase().contains(query) ||
                        nursery.county.lowercase().contains(query) ||
                        nursery.seedlings.any { it.lowercase().contains(query) }
            }

            if (filtered.isEmpty()) {
                val emptyTv = TextView(this).apply {
                    text = "No verified nurseries found matching '$filter'. Try 'Kisii', 'Bamboo', or 'Avocado'."
                    setTextColor(Color.parseColor("#C8E6C9"))
                    textSize = 14f
                    setPadding(16, 32, 16, 16)
                    gravity = Gravity.CENTER
                }
                nurseryListLayout.addView(emptyTv)
                return
            }

            for (nursery in filtered) {
                val card = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(Color.parseColor("#E8F5E9"))
                    setPadding(36, 32, 36, 32)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 24)
                    }
                }

                val titleTv = TextView(this).apply {
                    text = "🌿 ${nursery.name}"
                    setTextColor(Color.parseColor("#1B5E20"))
                    textSize = 17f
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                }

                val locationTv = TextView(this).apply {
                    text = "📍 ${nursery.town} (${nursery.subCounty}, ${nursery.county})"
                    setTextColor(Color.parseColor("#2E7D32"))
                    textSize = 13f
                    setPadding(0, 6, 0, 10)
                }

                val seedlingsHeader = TextView(this).apply {
                    text = "Available Seedlings & Prices:"
                    setTextColor(Color.parseColor("#1A1C1E"))
                    textSize = 13f
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                }

                val seedlingsList = TextView(this).apply {
                    text = nursery.seedlings.joinToString("\n") { "• $it" }
                    setTextColor(Color.parseColor("#37474F"))
                    textSize = 13f
                    setLineSpacing(4f, 1f)
                    setPadding(0, 4, 0, 14)
                }

                // Action buttons row (Call & WhatsApp)
                val buttonsRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                val callBtn = Button(this).apply {
                    text = "📞 Call Nursery"
                    setBackgroundColor(Color.parseColor("#1B5E20"))
                    setTextColor(Color.WHITE)
                    textSize = 12f
                    isAllCaps = false
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        (44 * resources.displayMetrics.density).toInt(),
                        1f
                    ).apply {
                        marginEnd = 12
                    }
                    setOnClickListener {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${nursery.phone}"))
                        startActivity(dialIntent)
                    }
                }

                val waBtn = Button(this).apply {
                    text = "💬 WhatsApp"
                    setBackgroundColor(Color.parseColor("#2E7D32"))
                    setTextColor(Color.WHITE)
                    textSize = 12f
                    isAllCaps = false
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        (44 * resources.displayMetrics.density).toInt(),
                        1f
                    )
                    setOnClickListener {
                        val message = "Hello ${nursery.name}, I am a Green Gusii farmer interested in ordering seedlings."
                        val waUri = Uri.parse("https://wa.me/${nursery.whatsapp}?text=${Uri.encode(message)}")
                        startActivity(Intent(Intent.ACTION_VIEW, waUri))
                    }
                }

                buttonsRow.addView(callBtn)
                buttonsRow.addView(waBtn)

                card.addView(titleTv)
                card.addView(locationTv)
                card.addView(seedlingsHeader)
                card.addView(seedlingsList)
                card.addView(buttonsRow)

                nurseryListLayout.addView(card)
            }
        }

        renderNurseries()

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                renderNurseries(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<Button>(R.id.nurseryBackToHubButton).setOnClickListener {
            finish()
        }
    }
}
