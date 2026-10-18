package com.example.data.model

import android.graphics.Bitmap
import com.example.util.QualityAssessment

enum class ItemCategory(val trTitle: String, val enTitle: String) {
    EYEWEAR("Gözlük Modelleri", "Eyewear"),
    HAIRSTYLES("Saç Stilleri", "Hairstyles")
}

enum class AIModelTier(val title: String, val subtitle: String, val modelId: String) {
    FLASH_IMAGE("Hızlı & Ekonomik", "gemini-2.5-flash-image", "gemini-2.5-flash-image"),
    PRO_IMAGE("Ultra Stüdyo Pro", "gemini-3-pro-image-preview", "gemini-3-pro-image-preview")
}

enum class ImageResolution(val label: String, val apiValue: String, val detail: String) {
    K1("1K", "1K", "1024 × 1024"),
    K2("2K", "2K", "2048 × 2048"),
    K4("4K", "4K", "4096 × 4096")
}

data class CatalogItem(
    val id: String,
    val name: String,
    val brandOrModelRef: String,
    val category: ItemCategory,
    val targetGender: UserGender,
    val styleVibe: StyleVibe,
    val description: String,
    val styleTag: String,
    val material: String,
    val promptPromptModifier: String
)

data class AuraScoreResult(
    val overallScore: Int,
    val harmonyScore: Int,
    val lightingScore: Int,
    val geometryScore: Int,
    val verdict: String,
    val critique: String,
    val keyHighlights: List<String>
)

data class LookbookItem(
    val id: String,
    val timestamp: Long,
    val originalBitmap: Bitmap,
    val resultBitmap: Bitmap,
    val appliedItems: List<CatalogItem>,
    val score: AuraScoreResult
)
