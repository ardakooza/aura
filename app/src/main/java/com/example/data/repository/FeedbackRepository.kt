package com.example.data.repository

import com.example.data.local.FeedbackDao
import com.example.data.local.FeedbackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

data class ImprovementInsight(
    val area: String,
    val count: Int,
    val percentage: Int,
    val status: String,
    val actionTaken: String
)

data class FeedbackAnalysis(
    val totalCount: Int,
    val averageScore: Double,
    val satisfactionRate: Int,
    val topAreas: List<ImprovementInsight>,
    val strategicSummary: String
)

class FeedbackRepository(private val feedbackDao: FeedbackDao) {

    val allFeedbacks: Flow<List<FeedbackEntity>> = feedbackDao.getAllFeedbacks()
    val averageRating: Flow<Double?> = feedbackDao.getAverageRating()
    val feedbackCount: Flow<Int> = feedbackDao.getFeedbackCount()

    suspend fun submitFeedback(
        rating: Int,
        category: String,
        comment: String,
        improvementArea: String
    ): Long {
        val entity = FeedbackEntity(
            rating = rating.coerceIn(1, 5),
            category = category,
            comment = comment.trim(),
            improvementArea = improvementArea
        )
        return feedbackDao.insertFeedback(entity)
    }

    suspend fun seedInitialDataIfEmpty() {
        val existing = feedbackDao.getAllFeedbacks().first()
        if (existing.isEmpty()) {
            val samples = listOf(
                FeedbackEntity(
                    rating = 5,
                    category = "Şapka & Başlık Doğallığı",
                    comment = "New Era kasketin kavisli vizörü ve alın gölgesi harika durdu, yüzüme tam oturdu.",
                    improvementArea = "Şapka Çeşitliliği"
                ),
                FeedbackEntity(
                    rating = 5,
                    category = "Gözlük Konumlandırma",
                    comment = "Tom Ford titanyum çerçevenin burun köprüsündeki yansıması çok doğal.",
                    improvementArea = "Gözlük Yansıma Efektleri"
                ),
                FeedbackEntity(
                    rating = 4,
                    category = "Saç Stili",
                    comment = "Fransız kesim ve bukleli saçlar çok başarılı. Daha fazla kıvırcık seçenek gelirse harika olur.",
                    improvementArea = "Kıvırcık Saç Modelleri"
                ),
                FeedbackEntity(
                    rating = 5,
                    category = "Canlı Önizleme",
                    comment = "Nesneyi sürükleyince anında portrede nasıl durduğunu görmek çok pratik ve hızlı.",
                    improvementArea = "Gerçek Zamanlı Hız"
                ),
                FeedbackEntity(
                    rating = 4,
                    category = "AI Üretim Kalitesi",
                    comment = "Aura puanı ve editoryal stil analizi çok profesyonel bir hava katmış.",
                    improvementArea = "Çözünürlük Seçenekleri"
                )
            )
            for (sample in samples) {
                feedbackDao.insertFeedback(sample)
            }
        }
    }

    fun analyzeFeedbacks(feedbacks: List<FeedbackEntity>): FeedbackAnalysis {
        val total = feedbacks.size
        if (total == 0) {
            return FeedbackAnalysis(
                totalCount = 0,
                averageScore = 5.0,
                satisfactionRate = 100,
                topAreas = emptyList(),
                strategicSummary = "Henüz kullanıcı geri bildirimi toplanmadı."
            )
        }

        val avg = feedbacks.map { it.rating }.average()
        val positiveCount = feedbacks.count { it.rating >= 4 }
        val satisfactionRate = ((positiveCount.toDouble() / total) * 100).toInt()

        // Group by improvement area
        val areaGroups = feedbacks.groupBy { it.improvementArea }
        val insights = areaGroups.map { (area, list) ->
            val count = list.size
            val percentage = ((count.toDouble() / total) * 100).toInt()
            val (status, action) = when {
                area.contains("Şapka", ignoreCase = true) ->
                    Pair("Tamamlandı", "10+ yeni şapka modeli (fedora, kasket, bere, bucket) ve kafa derinlik gölgesi eklendi.")
                area.contains("Gözlük", ignoreCase = true) ->
                    Pair("Optimize Edildi", "Çift köprü ve polarize yansıma katmanları milimetrik yüz oranlarına entegre edildi.")
                area.contains("Kıvırcık", ignoreCase = true) || area.contains("Saç", ignoreCase = true) ->
                    Pair("Genişletildi", "Afro fade, İtalyan dalgası ve katmanlı bukle stilleri kataloğa dahil edildi.")
                area.contains("Zamanlı", ignoreCase = true) || area.contains("Hız", ignoreCase = true) ->
                    Pair("Aktif", "0-gecikmeli yerel 3D kompozisyon ile canlı anlık önizleme devreye alındı.")
                else ->
                    Pair("İnceleniyor", "Kullanıcı geri bildirimi stilist algoritması için önceliklendirildi.")
            }
            ImprovementInsight(
                area = area,
                count = count,
                percentage = percentage,
                status = status,
                actionTaken = action
            )
        }.sortedByDescending { it.count }

        val strategicSummary = when {
            avg >= 4.5 -> "Kullanıcı memnuniyeti üst seviyede (%$satisfactionRate). Kullanıcılar özellikle şapka ve gözlüklerin yüz anatomisine doğal oturmasından memnun. Öncelikli iyileştirme alanı: Daha fazla stil varyasyonu ve gerçek zamanlı önizleme entegrasyonu."
            avg >= 3.5 -> "Kullanıcılar deneyimi genel olarak beğeniyor (%$satisfactionRate memnuniyet). Şapka ve saç modellerinin çeşitlendirilmesi ve önizleme akıcılığı odaklanılan başlıca gelişim alanı."
            else -> "Kullanıcı beklentilerini karşılamak için görsel yerleşim hassasiyeti ve render hızında optimizasyonlar sürdürülüyor."
        }

        return FeedbackAnalysis(
            totalCount = total,
            averageScore = avg,
            satisfactionRate = satisfactionRate,
            topAreas = insights,
            strategicSummary = strategicSummary
        )
    }
}
