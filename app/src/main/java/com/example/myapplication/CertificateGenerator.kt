package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CertificateGenerator {

    fun generateAndShareCertificate(
        context: Context,
        userName: String,
        ward: String,
        subCounty: String,
        county: String
    ): Uri? {
        val width = 1200
        val height = 850
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background: Soft parchment / mint ivory
        canvas.drawColor(Color.parseColor("#F4FBF7"))

        val paint = Paint().apply { isAntiAlias = true }

        // Outer Border: Deep Forest Green
        paint.color = Color.parseColor("#1D493A")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 24f
        canvas.drawRect(RectF(24f, 24f, width - 24f, height - 24f), paint)

        // Inner Border: Rich Gold
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 6f
        canvas.drawRect(RectF(48f, 48f, width - 48f, height - 48f), paint)

        // Top Emblem / Subtitle
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#2E7D32")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("🌱 GREEN GUSII HIGHLAND CONSERVATION INITIATIVE 🌱", width / 2f, 110f, paint)

        // Certificate Title
        paint.color = Color.parseColor("#1D493A")
        paint.textSize = 48f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("CERTIFICATE OF EXCELLENCE", width / 2f, 180f, paint)

        paint.color = Color.parseColor("#37474F")
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        canvas.drawText("This certifies that distinguished conservation champion", width / 2f, 240f, paint)

        // Member Name
        paint.color = Color.parseColor("#1B5E20")
        paint.textSize = 54f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText(userName, width / 2f, 320f, paint)

        // Gold divider line under name
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(width / 4f, 345f, 3f * width / 4f, 345f, paint)

        // Location & Achievement Text
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#263238")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("Resident of $ward Ward, $subCounty ($county)", width / 2f, 400f, paint)

        paint.color = Color.parseColor("#D4AF37")
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("★ RANK: GUSII FOREST GUARDIAN (500/500 PTS) ★", width / 2f, 455f, paint)

        paint.color = Color.parseColor("#37474F")
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        val description1 = "For outstanding community leadership in restoring Gusii hilltops,"
        val description2 = "protecting clean riverbanks, and eliminating water-guzzling eucalyptus."
        canvas.drawText(description1, width / 2f, 520f, paint)
        canvas.drawText(description2, width / 2f, 555f, paint)

        // Date and Official Verification
        val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        paint.color = Color.parseColor("#1D493A")
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("Issued: $currentDate", 220f, 680f, paint)
        canvas.drawText("Seal: Green Gusii Trust", width - 220f, 680f, paint)

        // Digital Seal Badge
        paint.color = Color.parseColor("#1B5E20")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        canvas.drawCircle(width / 2f, 680f, 60f, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#D4AF37")
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("OFFICIAL", width / 2f, 675f, paint)
        canvas.drawText("GUARDIAN", width / 2f, 695f, paint)

        // Save to cache file and return URI
        return try {
            val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(cacheDir, "green_champion_certificate.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
