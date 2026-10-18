package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class QualityStatus {
    OPTIMAL,
    LOW_LIGHT,
    OVEREXPOSED,
    BLURRY
}

data class QualityAssessment(
    val status: QualityStatus,
    val score: Int,
    val message: String,
    val isWarning: Boolean
)

object PortraitQualityAnalyzer {

    /**
     * Rapid on-device evaluation of the portrait's lighting and sharpness.
     */
    suspend fun analyze(bitmap: Bitmap): QualityAssessment = withContext(Dispatchers.Default) {
        val sampleSize = 120
        val scaled = Bitmap.createScaledBitmap(bitmap, sampleSize, sampleSize, false)

        var totalLuma = 0.0
        var pixelCount = 0
        val lumaGrid = Array(sampleSize) { DoubleArray(sampleSize) }

        for (y in 0 until sampleSize) {
            for (x in 0 until sampleSize) {
                val pixel = scaled.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                // ITU-R BT.601 luma formula
                val luma = 0.299 * r + 0.587 * g + 0.114 * b
                lumaGrid[y][x] = luma
                totalLuma += luma
                pixelCount++
            }
        }

        val avgLuma = totalLuma / pixelCount

        // Laplacian edge variance (sharpness approximation)
        var edgeSum = 0.0
        var edgeCount = 0
        for (y in 1 until sampleSize - 1) {
            for (x in 1 until sampleSize - 1) {
                val center = lumaGrid[y][x]
                val laplacian = Math.abs(
                    4 * center - lumaGrid[y - 1][x] - lumaGrid[y + 1][x] - lumaGrid[y][x - 1] - lumaGrid[y][x + 1]
                )
                edgeSum += laplacian
                edgeCount++
            }
        }

        val avgEdge = if (edgeCount > 0) edgeSum / edgeCount else 0.0

        if (scaled != bitmap) {
            scaled.recycle()
        }

        when {
            avgLuma < 45.0 -> {
                QualityAssessment(
                    status = QualityStatus.LOW_LIGHT,
                    score = (avgLuma * 1.4).toInt().coerceIn(30, 65),
                    message = "Düşük Işık: Yüz hatlarının ve parçaların gerçekçi kaynaşması için daha aydınlık bir ortam önerilir.",
                    isWarning = true
                )
            }
            avgLuma > 220.0 -> {
                QualityAssessment(
                    status = QualityStatus.OVEREXPOSED,
                    score = 55,
                    message = "Aşırı Parlaklık: Yüzdeki detaylar patlamış olabilir, dengeli ışık alan bir portre deneyebilirsiniz.",
                    isWarning = true
                )
            }
            avgEdge < 5.0 -> {
                QualityAssessment(
                    status = QualityStatus.BLURRY,
                    score = 60,
                    message = "Hafif Bulanıklık: Net bir portre, 3D aksesuar yerleşiminin doğruluğunu büyük ölçüde artırır.",
                    isWarning = true
                )
            }
            else -> {
                val score = (85 + (avgEdge.coerceAtMost(30.0) / 30.0 * 14.0)).toInt().coerceIn(85, 99)
                QualityAssessment(
                    status = QualityStatus.OPTIMAL,
                    score = score,
                    message = "Mükemmel Portre Kalitesi: Işıklandırma ve yüz netliği stüdyo segmentasyonu için ideal.",
                    isWarning = false
                )
            }
        }
    }
}
