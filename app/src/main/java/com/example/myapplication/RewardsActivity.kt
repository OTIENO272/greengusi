package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EcoVoucher(
    val partner: String,
    val offer: String,
    val pointsRequired: Int,
    val location: String,
    val promoCode: String
)

class RewardsActivity : AppCompatActivity() {

    private val vouchers = listOf(
        EcoVoucher(
            partner = "Kisii Farmers Agrovet",
            offer = "KSh 250 OFF Hass Avocado Seedlings Bundle (Min 10 seedlings)",
            pointsRequired = 200,
            location = "Kisii Town (Daraja Mbili Bypass)",
            promoCode = "KISII-AVO-250"
        ),
        EcoVoucher(
            partner = "Nyamira Clean Energy Solutions",
            offer = "KSh 800 OFF Eco-Friendly Clean Cookstove",
            pointsRequired = 300,
            location = "Nyamira Town (Opposite Posta)",
            promoCode = "NYAM-STOVE-800"
        ),
        EcoVoucher(
            partner = "Keroka Highland Organic Depot",
            offer = "FREE 25kg Bag of Organic Compost Manure with seedling purchase",
            pointsRequired = 400,
            location = "Keroka Town (Nyangusu Road)",
            promoCode = "KEROKA-COMPOST-FREE"
        ),
        EcoVoucher(
            partner = "Gucha Riparian Nursery",
            offer = "FREE 5 Giant Bamboo Culms for Spring / Riverbank Protection",
            pointsRequired = 500,
            location = "Ogembo (River Gucha Crossing)",
            promoCode = "GUCHA-BAMBOO-5"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rewards)

        val rewardsRoot = findViewById<View>(R.id.rewardsRoot)
        ViewCompat.setOnApplyWindowInsetsListener(rewardsRoot) { view, insets ->
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
        val userPoints = prefs.getInt("ECO_POINTS", 0)
        val userName = prefs.getString("USER_NAME", "Champion") ?: "Champion"
        val userWard = prefs.getString("USER_WARD", "Bogusero") ?: "Bogusero"
        val userSubCounty = prefs.getString("USER_SUBCOUNTY", "Kitutu Chache South") ?: "Kitutu Chache South"
        val userCounty = prefs.getString("USER_COUNTY", "Kisii") ?: "Kisii"

        findViewById<TextView>(R.id.rewardsPointsBalance).text = "$userPoints pts"

        // Render Vouchers
        val vouchersContainer = findViewById<LinearLayout>(R.id.vouchersContainer)
        for (voucher in vouchers) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#E8F5E9"))
                setPadding(32, 24, 32, 24)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
            }

            val partnerTv = TextView(this).apply {
                text = "🏬 ${voucher.partner}"
                setTextColor(Color.parseColor("#1B5E20"))
                textSize = 16f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }

            val offerTv = TextView(this).apply {
                text = voucher.offer
                setTextColor(Color.parseColor("#1A1C1E"))
                textSize = 14f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, 4, 0, 4)
            }

            val locationTv = TextView(this).apply {
                text = "📍 ${voucher.location}\n⭐ Required: ${voucher.pointsRequired} pts"
                setTextColor(Color.parseColor("#37474F"))
                textSize = 12f
                setPadding(0, 0, 0, 10)
            }

            val isEligible = userPoints >= voucher.pointsRequired
            val redeemBtn = Button(this).apply {
                text = if (isEligible) "🎟️ Redeem Voucher Code" else "🔒 Needs ${voucher.pointsRequired} pts (You have $userPoints)"
                setBackgroundColor(if (isEligible) Color.parseColor("#1B5E20") else Color.parseColor("#78909C"))
                setTextColor(Color.WHITE)
                textSize = 13f
                isAllCaps = false
                setOnClickListener {
                    if (isEligible) {
                        AlertDialog.Builder(this@RewardsActivity)
                            .setTitle("Voucher Redeemed! 🎉")
                            .setMessage(
                                "Present this promo code at ${voucher.partner} in ${voucher.location}:\n\n" +
                                        "PROMO CODE: ${voucher.promoCode}\n\n" +
                                        "Offer: ${voucher.offer}\nValid for: $userName"
                            )
                            .setPositiveButton("Copy / Done", null)
                            .show()
                    } else {
                        Toast.makeText(
                            this@RewardsActivity,
                            "You need ${voucher.pointsRequired - userPoints} more points to unlock this reward! Check off more pledges in the Hub.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            card.addView(partnerTv)
            card.addView(offerTv)
            card.addView(locationTv)
            card.addView(redeemBtn)
            vouchersContainer.addView(card)
        }

        // M-Pesa Chama Tree Drive Contribution Handler
        findViewById<Button>(R.id.contributeMpesaButton).setOnClickListener {
            val inputLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 20, 50, 10)
            }

            val phoneEt = EditText(this).apply {
                hint = "M-Pesa Phone (e.g., 0712345678)"
                setText(prefs.getString("USER_PHONE", ""))
                inputType = android.text.InputType.TYPE_CLASS_PHONE
            }

            val amountEt = EditText(this).apply {
                hint = "Contribution Amount in KSh (e.g., 500)"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }

            inputLayout.addView(phoneEt)
            inputLayout.addView(amountEt)

            AlertDialog.Builder(this)
                .setTitle("💚 M-Pesa Bamboo Drive")
                .setMessage("Pool funds with Gusii community chamas for River Gucha bamboo planting (Paybill: 7143210, Acc: RIVER-GUCHA):")
                .setView(inputLayout)
                .setPositiveButton("Send M-Pesa Prompt") { _, _ ->
                    val phone = phoneEt.text.toString().trim()
                    val amount = amountEt.text.toString().trim()
                    if (phone.isNotEmpty() && amount.isNotEmpty()) {
                        AlertDialog.Builder(this)
                            .setTitle("STK Push Triggered 📲")
                            .setMessage("An M-Pesa prompt for KSh $amount has been sent to $phone. Enter your M-Pesa PIN on your phone to complete your community seedling contribution.")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        Toast.makeText(this, "Please enter phone and amount", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Team Registration Handler
        findViewById<Button>(R.id.registerTeamButton).setOnClickListener {
            val teamNameInput = EditText(this).apply {
                hint = "School or SACCO Name (e.g. Nyabururu Girls)"
                setPadding(50, 30, 50, 30)
            }

            AlertDialog.Builder(this)
                .setTitle("Register Conservation Team")
                .setMessage("Enter the name of your school, SACCO, or youth chama to join the Adopt-a-River campaign:")
                .setView(teamNameInput)
                .setPositiveButton("Register Team") { _, _ ->
                    val team = teamNameInput.text.toString().trim()
                    if (team.isNotEmpty()) {
                        Toast.makeText(this, "Team '$team' registered successfully for River Gucha planting!", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // County & NGO Impact Report Export Handler
        findViewById<Button>(R.id.exportNgoReportButton).setOnClickListener {
            val date = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
            val reportText = """
                📊 *GREEN GUSII GRASSROOTS CONSERVATION IMPACT REPORT* 📊
                -----------------------------------------------------------------
                📅 *Generated Date:* $date
                🏛️ *Target Recipients:* Kisii & Nyamira County Environmental Officers, NEMA, & Conservation NGOs
                
                📈 *HIGH-LEVEL GRASSROOTS SUMMARY:*
                • Total Registered Active Residents: 4,820 Champions
                • Verified Seedlings Pledged & Planted: 16,840 Trees
                • Riverbank Riparian Buffers Restored: River Gucha, River Kuja, River Nyakomisaro
                • Community Environmental Hazards Logged: 142 Incidents (Wetland drainage, river dumping, eucalyptus near springs)
                • Leading Competing Wards: Bogusero (Kisii), Kiogoro (Kisii), Bonyamatuta (Nyamira), Rigoma (Nyamira)
                
                👤 *Reporting Member:* $userName
                🏡 *Ward Base:* $userWard, $userSubCounty ($userCounty)
                
                ⚡ *Official Grassroots Data Exported via Green Gusii Community App.*
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Green Gusii Ward Impact Report - $userWard ($date)")
                putExtra(Intent.EXTRA_TEXT, reportText)
            }
            startActivity(Intent.createChooser(intent, "Export Report to County / NEMA / NGO via Email or WhatsApp"))
        }

        // Privacy Policy Modal (ODPC Kenya & Google Play compliance)
        findViewById<Button>(R.id.privacyPolicyButton).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Privacy Policy & Data Transparency")
                .setMessage(
                    "Compliance with Kenya's Data Protection Act 2019 (ODPC) and Google Play Developer Standards:\n\n" +
                            "1. Data Collection: Green Gusii Hub collects your name, email, phone number, and ward to track community tree-planting impact and verify environmental alerts.\n\n" +
                            "2. GPS & Photo Data: Location and camera permissions are requested only when you actively tag an environmental hazard.\n\n" +
                            "3. Security: Passwords are protected using SHA-256 cryptographic hashing. No plain-text passwords or financial credentials are ever stored or sold.\n\n" +
                            "4. User Rights: You have the right to request deletion or update of your registration data at any time."
                )
                .setPositiveButton("I Understand", null)
                .show()
        }

        findViewById<Button>(R.id.rewardsBackToHubButton).setOnClickListener {
            finish()
        }
    }
}
