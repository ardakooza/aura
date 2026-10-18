package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStream

object BitmapUtils {

    fun toBase64Jpeg(bitmap: Bitmap, quality: Int = 85): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val cleanBase64 = if (base64String.contains(",")) {
                base64String.substringAfter(",")
            } else {
                base64String
            }
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun decodeUriToBitmap(context: Context, uri: Uri, maxSize: Int = 1024): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeStream(stream, null, options)

                    val scale = calculateInSampleSize(options, maxSize, maxSize)
                    context.contentResolver.openInputStream(uri)?.use { secondStream ->
                        val actualOptions = BitmapFactory.Options().apply {
                            inSampleSize = scale
                        }
                        BitmapFactory.decodeStream(secondStream, null, actualOptions)
                    }
                }
            } catch (e: Exception) {
                null
            }
        }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    suspend fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String): Uri? =
        withContext(Dispatchers.IO) {
            val filename = "AURA_${System.currentTimeMillis()}.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AuraStudio")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                try {
                    val stream: OutputStream? = context.contentResolver.openOutputStream(uri)
                    stream?.use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        context.contentResolver.update(uri, contentValues, null, null)
                    }
                } catch (e: Exception) {
                    return@withContext null
                }
            }
            uri
        }

    /**
     * High-fidelity studio lighting portrait simulation for immediate testing
     * with realistic skin subsurface scattering, lighting gradients, and depth.
     */
    fun createCuratedSamplePortrait(type: Int): Bitmap {
        val size = 720
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Studio Dark Backdrop with soft radial spotlight
        val bgShader = RadialGradient(
            size * 0.5f, size * 0.4f, size * 0.65f,
            intArrayOf(Color.rgb(35, 35, 42), Color.rgb(10, 10, 12)),
            null,
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = bgShader }
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), bgPaint)

        // Realistic skin tones with volumetric shading
        val baseSkin = when (type) {
            0 -> Color.rgb(232, 204, 186)
            1 -> Color.rgb(205, 172, 148)
            2 -> Color.rgb(244, 222, 204)
            else -> Color.rgb(175, 130, 105)
        }
        val shadowSkin = when (type) {
            0 -> Color.rgb(188, 150, 130)
            1 -> Color.rgb(155, 120, 95)
            2 -> Color.rgb(195, 165, 145)
            else -> Color.rgb(130, 90, 70)
        }

        // Torso / Shoulders
        val torsoShader = LinearGradient(
            0f, size * 0.65f, 0f, size.toFloat(),
            Color.rgb(26, 26, 30), Color.rgb(14, 14, 16),
            Shader.TileMode.CLAMP
        )
        val torsoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = torsoShader }
        val torsoPath = Path().apply {
            moveTo(size * 0.15f, size.toFloat())
            quadTo(size * 0.5f, size * 0.68f, size * 0.85f, size.toFloat())
            close()
        }
        canvas.drawPath(torsoPath, torsoPaint)

        // Neck
        val neckShader = LinearGradient(
            size * 0.42f, 0f, size * 0.58f, 0f,
            shadowSkin, baseSkin,
            Shader.TileMode.CLAMP
        )
        val neckPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = neckShader }
        canvas.drawRoundRect(RectF(size * 0.41f, size * 0.52f, size * 0.59f, size * 0.72f), 24f, 24f, neckPaint)

        // Face Oval with soft 3D lighting gradient
        val faceShader = RadialGradient(
            size * 0.48f, size * 0.38f, size * 0.28f,
            intArrayOf(baseSkin, shadowSkin),
            floatArrayOf(0.5f, 1.0f),
            Shader.TileMode.CLAMP
        )
        val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = faceShader }
        val faceRect = RectF(size * 0.29f, size * 0.20f, size * 0.71f, size * 0.60f)
        canvas.drawOval(faceRect, facePaint)

        // Natural Hair Silhouette with gradient
        val hairColor = when (type) {
            0 -> Color.rgb(28, 22, 18)
            1 -> Color.rgb(16, 14, 14)
            2 -> Color.rgb(55, 38, 26)
            else -> Color.rgb(22, 18, 16)
        }
        val hairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = hairColor }
        val hairPath = Path().apply {
            moveTo(size * 0.27f, size * 0.42f)
            cubicTo(size * 0.25f, size * 0.14f, size * 0.75f, size * 0.14f, size * 0.73f, size * 0.42f)
            cubicTo(size * 0.65f, size * 0.22f, size * 0.35f, size * 0.22f, size * 0.27f, size * 0.42f)
            close()
        }
        canvas.drawPath(hairPath, hairPaint)

        // Eyes & realistic highlights
        val featurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(42, 34, 30)
            strokeWidth = 3f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        // Eyebrows
        canvas.drawLine(size * 0.37f, size * 0.35f, size * 0.45f, size * 0.345f, featurePaint)
        canvas.drawLine(size * 0.55f, size * 0.345f, size * 0.63f, size * 0.35f, featurePaint)

        // Irises
        val irisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(38, 30, 26) }
        canvas.drawCircle(size * 0.41f, size * 0.38f, 10f, irisPaint)
        canvas.drawCircle(size * 0.59f, size * 0.38f, 10f, irisPaint)

        // Catchlights (Eye reflections)
        val catchlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        canvas.drawCircle(size * 0.41f - 3f, size * 0.38f - 3f, 3f, catchlightPaint)
        canvas.drawCircle(size * 0.59f - 3f, size * 0.38f - 3f, 3f, catchlightPaint)

        // Nose contour
        val nosePath = Path().apply {
            moveTo(size * 0.50f, size * 0.37f)
            lineTo(size * 0.49f, size * 0.44f)
            lineTo(size * 0.52f, size * 0.445f)
        }
        canvas.drawPath(nosePath, featurePaint)

        // Lips with natural tone
        val lipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(185, 115, 110)
            style = Paint.Style.FILL
        }
        val lipRect = RectF(size * 0.44f, size * 0.49f, size * 0.56f, size * 0.52f)
        canvas.drawRoundRect(lipRect, 8f, 8f, lipPaint)

        return bitmap
    }
}
