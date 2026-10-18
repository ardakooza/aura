package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.FeedbackEntity
import com.example.data.repository.FeedbackAnalysis
import com.example.ui.theme.AuraBlack
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraSurfaceDark
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeedbackDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSubmitFeedback: (rating: Int, category: String, comment: String, improvementArea: String) -> Unit,
    feedbacks: List<FeedbackEntity>,
    analysis: FeedbackAnalysis?
) {
    if (!isOpen) return

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var rating by remember { mutableIntStateOf(5) }
    var selectedCategory by remember { mutableStateOf("Try-On Doğallığı") }
    var selectedImprovementArea by remember { mutableStateOf("Şapka Çeşitliliği") }
    var commentText by remember { mutableStateOf("") }

    val categories = listOf(
        "Try-On Doğallığı",
        "Şapka & Başlık",
        "Gözlük Oturumu",
        "Saç Stilleri",
        "Canlı Önizleme Hızı",
        "AI Kalitesi"
    )

    val improvementAreas = listOf(
        "Şapka Çeşitliliği",
        "Gözlük Konumlandırma",
        "Kıvırcık Saç Modelleri",
        "Gerçek Zamanlı Hız",
        "AI Çözünürlüğü",
        "Daha Fazla Renk"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, AuraCardBorder, RoundedCornerShape(24.dp))
                .testTag("feedback_dialog_card"),
            colors = CardDefaults.cardColors(containerColor = AuraSurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AURA FEEDBACK & ANALYTICS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = AuraTextSecondary
                        )
                        Text(
                            text = "Kullanıcı Geri Bildirimi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = AuraWhite
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AuraSurfaceElevated)
                            .testTag("close_feedback_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = AuraWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: Feedback Form vs Analytics
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = AuraBlack,
                    contentColor = AuraWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AuraWhite,
                            height = 2.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, AuraCardBorder, RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Geri Bildirim Ver", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("İyileştirme Analizi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                if (selectedTabIndex == 0) {
                    // Feedback Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Rating Stars
                        Text(
                            text = "Deneyiminizi Puanlayın",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraTextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (i in 1..5) {
                                IconButton(
                                    onClick = { rating = i },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "$i Yıldız",
                                        tint = if (i <= rating) Color(0xFFFFD700) else AuraTextSecondary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            val sentimentText = when (rating) {
                                5 -> "★ 5.0 Kusursuz"
                                4 -> "★ 4.0 Çok Başarılı"
                                3 -> "★ 3.0 İyi"
                                2 -> "★ 2.0 Geliştirilmeli"
                                else -> "★ 1.0 Zayıf"
                            }
                            Text(
                                text = sentimentText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category
                        Text(
                            text = "Geri Bildirim Alanı",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraTextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = cat == selectedCategory
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) AuraWhite else AuraCardDark)
                                        .border(1.dp, if (isSelected) AuraWhite else AuraCardBorder, RoundedCornerShape(20.dp))
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AuraBlack else AuraTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Improvement Area
                        Text(
                            text = "En Çok İyileştirilmesini İstediğiniz Nokta",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraTextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            improvementAreas.forEach { area ->
                                val isSelected = area == selectedImprovementArea
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) AuraWhite else AuraCardDark)
                                        .border(1.dp, if (isSelected) AuraWhite else AuraCardBorder, RoundedCornerShape(20.dp))
                                        .clickable { selectedImprovementArea = area }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = area,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AuraBlack else AuraTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Comment Text Field
                        Text(
                            text = "Yorum & Öneriniz",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraTextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = {
                                Text(
                                    "Örn: Şapkaların başa oturması çok gerçekçi, daha fazla fötr ve vintage şapka eklenirse harika olur...",
                                    fontSize = 12.sp,
                                    color = AuraTextSecondary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("feedback_comment_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AuraWhite,
                                unfocusedTextColor = AuraWhite,
                                focusedContainerColor = AuraCardDark,
                                unfocusedContainerColor = AuraCardDark,
                                focusedBorderColor = AuraWhite,
                                unfocusedBorderColor = AuraCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val comment = if (commentText.isBlank()) "Genel değerlendirme yapıldı." else commentText
                                onSubmitFeedback(rating, selectedCategory, comment, selectedImprovementArea)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_feedback_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraWhite,
                                contentColor = AuraBlack
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Geri Bildirimi Gönder",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                } else {
                    // Analytics Dashboard
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (analysis != null) {
                            // Metrics Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = AuraCardDark),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AuraCardBorder)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("ORTALAMA PUAN", fontSize = 10.sp, color = AuraTextSecondary, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "★ ${String.format(Locale.US, "%.1f", analysis.averageScore)}",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD700)
                                        )
                                        Text("${analysis.totalCount} geri bildirim", fontSize = 11.sp, color = AuraTextMuted)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = AuraCardDark),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AuraCardBorder)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("MEMNUNİYET ORANI", fontSize = 10.sp, color = AuraTextSecondary, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "%${analysis.satisfactionRate}",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AuraWhite
                                        )
                                        Text("Pozitif değerlendirme", fontSize = 11.sp, color = AuraTextMuted)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // AI & Stylist Action Summary
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = AuraSurfaceElevated),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AuraCardBorder)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = AuraWhite, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Stilist & Mühendislik Değerlendirmesi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AuraWhite)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = analysis.strategicSummary,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = AuraTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Identified Improvement Areas
                            Text(
                                text = "Tespit Edilen İyileştirme Alanları & Aksiyonlar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraWhite
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            analysis.topAreas.forEach { insight ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = AuraCardDark),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AuraCardBorder)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(insight.area, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AuraWhite)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF1E293B))
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(insight.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(insight.actionTaken, fontSize = 11.sp, color = AuraTextMuted, lineHeight = 15.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Recent Comments
                            Text(
                                text = "Kullanıcı Yorumları (${feedbacks.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraWhite
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr")) }
                            feedbacks.take(5).forEach { fb ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = AuraCardDark),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("★ ${fb.rating}.0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(fb.category, fontSize = 11.sp, color = AuraTextSecondary)
                                            }
                                            Text(
                                                dateFormat.format(Date(fb.timestamp)),
                                                fontSize = 10.sp,
                                                color = AuraTextSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(fb.comment, fontSize = 12.sp, color = AuraWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
