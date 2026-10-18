package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_feedbacks")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rating: Int, // 1 to 5
    val category: String, // e.g. "Try-On Doğallığı", "Şapka & Gözlük", "AI Üretim Kalitesi", "Arayüz"
    val comment: String,
    val improvementArea: String, // e.g. "Şapka Doğallığı", "Gözlük Oturumu", "Saç Çeşitliliği", "AI Hızı"
    val timestamp: Long = System.currentTimeMillis()
)
