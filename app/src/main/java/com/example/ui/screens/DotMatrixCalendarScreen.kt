package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraOffWhite
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraScoreRingProgress
import com.example.ui.theme.AuraScoreRingTrack
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

/**
 * Screen implementing the Dot-Matrix Calendar and Task widgets directly inspired
 * by the user's reference screenshots (October calendar, Day 67% dot matrix, Tasks 3/6 widget).
 */
@Composable
fun DotMatrixCalendarScreen(
    userProfile: UserProfile,
    lookbookCount: Int,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("dot_matrix_calendar_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Widget 1: October Calendar Header Widget (IMG_20261007_232842_316)
        CalendarHeaderWidget()

        // Widget 2: Dot-Matrix Progress Widget (2 rows of 12 dots, Day 67%)
        DotMatrixProgressWidget()

        // Widget 3: Tasks & Style Routine Widget (Tasks "Harika bir gün", 3/6 circle)
        StyleTasksWidget()

        // Widget 4: Lookbook & Style Volume Metrics
        StyleVolumeCard(lookbookCount = lookbookCount, userProfile = userProfile)

        Spacer(modifier = Modifier.height(72.dp)) // Padding for floating bottom dock
    }
}

@Composable
private fun CalendarHeaderWidget() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, RoundedCornerShape(26.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ekim",
                    color = AuraWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "4 stil kaydı",
                    color = AuraTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day labels (Mo, Tu, We, Th, Fr, Sa, Su)
            val days = listOf("Pt", "Sa", "Ça", "Pe", "Cu", "Ct", "Pz")
            val dates = listOf(6, 7, 8, 9, 10, 11, 12)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        color = AuraTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(36.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dates.forEach { date ->
                    val isToday = date == 7
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isToday) AuraWhite else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$date",
                            color = if (isToday) Color.Black else AuraOffWhite,
                            fontSize = 13.sp,
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DotMatrixProgressWidget() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, RoundedCornerShape(26.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Günlük Stil Yoğunluğu",
                    color = AuraWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "67%",
                    color = AuraWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2 rows of 12 luminous dots (First 16 active white dots, last 8 inactive dark dots)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 12) {
                        val isActive = i < 10
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(if (isActive) AuraWhite else Color(0xFF2C2C30))
                        )
                    }
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 12) {
                        val isActive = i < 6
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(if (isActive) AuraWhite else Color(0xFF2C2C30))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleTasksWidget() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, RoundedCornerShape(26.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stil Rutini",
                        color = AuraWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Güne harika bir başlangıç",
                        color = AuraTextMuted,
                        fontSize = 12.sp
                    )
                }

                // 3/6 Circular Progress Gauge
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(48.dp)) {
                        drawArc(
                            color = AuraScoreRingTrack,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = AuraScoreRingProgress,
                            startAngle = -90f,
                            sweepAngle = 180f, // 3/6 is 50%
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "3/6",
                        color = AuraWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two-column Task Pills directly matching IMG_20261007_232842_290
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPill(
                        title = "Gözlük Denemesi",
                        isChecked = true,
                        modifier = Modifier.weight(1f)
                    )
                    TaskPill(
                        title = "Saç Önizlemesi",
                        isChecked = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPill(
                        title = "Aura Analizi",
                        isChecked = true,
                        modifier = Modifier.weight(1f)
                    )
                    TaskPill(
                        title = "Lookbook Kaydı",
                        isChecked = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskPill(
    title: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isChecked) AuraWhite else AuraPillInactive)
            .border(1.dp, if (isChecked) AuraWhite else AuraCardBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = if (isChecked) Color.Black else AuraOffWhite,
                fontSize = 12.sp,
                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium
            )
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .border(1.dp, AuraCardBorder, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun StyleVolumeCard(lookbookCount: Int, userProfile: UserProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, RoundedCornerShape(26.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Toplam Stil Denemesi",
                    color = AuraTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${lookbookCount + 4} Look",
                    color = AuraWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AuraPillInactive)
                    .border(1.dp, AuraCardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = userProfile.gender.label,
                    color = AuraWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
