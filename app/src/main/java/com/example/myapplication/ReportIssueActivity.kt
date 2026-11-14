package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream

class ReportIssueActivity : AppCompatActivity() {

    private var capturedBitmap: Bitmap? = null
    private var capturedImageUri: Uri? = null
    private var currentGpsLocation: String? = null
    private var mapsLink: String? = null

    private lateinit var photoPreview: ImageView
    private lateinit var gpsCoordinatesText: TextView

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            photoPreview.setImageBitmap(bitmap)
            photoPreview.visibility = View.VISIBLE
            saveBitmapToCache(bitmap)
            Toast.makeText(this, "Evidence photo captured! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(this, "Camera permission needed to snap evidence", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            fetchLiveGps()
        } else {
            Toast.makeText(this, "Location permission is required for GPS tagging", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_issue)

        val reportRoot = findViewById<View>(R.id.reportRoot)
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
        val capturePhotoButton = findViewById<Button>(R.id.capturePhotoButton)
        val tagGpsButton = findViewById<Button>(R.id.tagGpsButton)
        photoPreview = findViewById(R.id.photoPreview)
        gpsCoordinatesText = findViewById(R.id.gpsCoordinatesText)

        val subCountyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subCounties)
        subCountySpinner.adapter = subCountyAdapter

        val defaultIndex = subCounties.indexOfFirst { it.equals(reporterSubCounty, ignoreCase = true) }
        if (defaultIndex >= 0) {
            subCountySpinner.setSelection(defaultIndex)
        }

        val threatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, threats)
        hazardTypeSpinner.adapter = threatAdapter

        // Camera evidence trigger
        capturePhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(null)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        // GPS Tagging trigger
        tagGpsButton.setOnClickListener {
            val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (hasFine || hasCoarse) {
                fetchLiveGps()
            } else {
                requestLocationPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }

        // Broadcast Alert
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

            val gpsSection = if (currentGpsLocation != null) {
                """
                📍 *GPS Coordinates:* $currentGpsLocation
                🗺️ *Google Maps:* $mapsLink
                """.trimIndent()
            } else {
                "📍 *GPS Coordinates:* Not tagged (Landmark: $landmark)"
            }

            val photoSection = if (capturedImageUri != null) "📸 *Photo Evidence:* Attached" else "📸 *Photo Evidence:* None attached"

            val reportMessage = """
                🚨 *GREEN GUSII ENVIRONMENTAL HAZARD ALERT* 🚨
                --------------------------------------------------
                📍 *Location:* $selectedSubCounty ($reporterCounty)
                📌 *Landmark/Village:* $landmark
                ⚠️ *Threat:* $selectedThreat
                📝 *Observations:* $description
                
                $gpsSection
                $photoSection

                👤 *Reported By:* $reporterName
                📞 *Phone:* $reporterPhone
                ✉️ *Email:* $reporterEmail
                🏡 *Resident Base:* $reporterWard, $selectedSubCounty

                ⚡ *Sent via Green Gusii Community App* for immediate NEMA & County Environment intervention.
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                if (capturedImageUri != null) {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, capturedImageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                }
                putExtra(Intent.EXTRA_SUBJECT, "URGENT: Environmental Hazard Alert - $selectedSubCounty")
                putExtra(Intent.EXTRA_TEXT, reportMessage)
            }

            startActivity(Intent.createChooser(shareIntent, "Broadcast Alert via WhatsApp, SMS, or Email"))
            Toast.makeText(this, "Opening broadcast channels...", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.backToHubButton).setOnClickListener {
            finish()
        }
    }

    private fun fetchLiveGps() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            Toast.makeText(this, "Location service unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            var bestLocation: Location? = null

            for (provider in providers) {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                        bestLocation = loc
                    }
                }
            }

            if (bestLocation != null) {
                applyLocation(bestLocation)
            } else {
                // Request a single location update
                gpsCoordinatesText.text = "GPS: Acquiring satellite fix..."
                val singleListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        applyLocation(location)
                        locationManager.removeUpdates(this)
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 10f, singleListener)
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyLocation(location: Location) {
        val lat = String.format("%.5f", location.latitude)
        val lng = String.format("%.5f", location.longitude)
        currentGpsLocation = "Lat $lat, Lng $lng"
        mapsLink = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        gpsCoordinatesText.text = "📍 Live GPS: $currentGpsLocation\n🔗 $mapsLink"
        Toast.makeText(this, "GPS Tagged: $currentGpsLocation", Toast.LENGTH_SHORT).show()
    }

    private fun saveBitmapToCache(bitmap: Bitmap) {
        try {
            val imagesFolder = File(cacheDir, "images").apply { mkdirs() }
            val file = File(imagesFolder, "hazard_evidence_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            capturedImageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
