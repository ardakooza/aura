package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.AuraShape
import com.example.ui.components.AuraTabularNums
import com.example.ui.components.pressable
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraOffWhite
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** One saved look, reduced to what the calendar needs. */
data class LookDay(val timestamp: Long, val score: Int)

private val TrLocale = Locale("tr", "TR")
private const val DAY_MS = 86_400_000L

private fun dayIndex(timestamp: Long): Long =
    (timestamp + TimeZone.getDefault().getOffset(timestamp)) / DAY_MS

/**
 * Style calendar. Every dot is a real day of the month; a day with saved looks lights up
 * and grows with the number of looks. Stats below are computed from the lookbook, so the
 * screen is empty (and says so) until the first look is saved.
 */
@Composable
fun DotMatrixCalendarScreen(
    userProfile: UserProfile,
    lookbookCount: Int,
    modifier: Modifier = Modifier,
    looks: List<LookDay> = emptyList()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("dot_matrix_calendar_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MonthMatrixCard(looks = looks)

        StatsCard(looks = looks, totalCount = maxOf(lookbookCount, looks.size))

        Spacer(modifier = Modifier.height(88.dp)) // clearance for the floating dock
    }
}

@Composable
private fun MonthMatrixCard(looks: List<LookDay>) {
    val now = remember { Calendar.getInstance() }
    var monthOffset by remember { mutableIntStateOf(0) }

    val month = remember(monthOffset) {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MONTH, monthOffset)
        }
    }
    val year = month.get(Calendar.YEAR)
    val monthIndex = month.get(Calendar.MONTH)
    val daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingBlanks = (month.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday-first
    val isCurrentMonth = monthOffset == 0
    val todayDay = now.get(Calendar.DAY_OF_MONTH)

    val countsByDay = remember(looks, monthOffset) {
        val result = HashMap<Int, Int>()
        val c = Calendar.getInstance()
        for (look in looks) {
            c.timeInMillis = look.timestamp
            if (c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == monthIndex) {
                val d = c.get(Calendar.DAY_OF_MONTH)
                result[d] = (result[d] ?: 0) + 1
            }
        }
        result
    }

    var selectedDay by remember(monthOffset) {
        mutableStateOf<Int?>(if (isCurrentMonth) todayDay else null)
    }

    val monthTitle = remember(monthOffset) {
        val name = month.getDisplayName(Calendar.MONTH, Calendar.LONG, TrLocale) ?: ""
        name.replaceFirstChar { it.titlecase(TrLocale) } + " " + year
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AuraShape.Hero)
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, AuraShape.Hero)
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = monthTitle,
                transitionSpec = { fadeIn(tween(220, delayMillis = 60)) togetherWith fadeOut(tween(120)) },
                label = "monthTitle"
            ) { title ->
                Text(
                    text = title,
                    color = AuraWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MonthStepButton(label = "‹", description = "Önceki ay", enabled = true) { monthOffset-- }
                MonthStepButton(label = "›", description = "Sonraki ay", enabled = monthOffset < 0) { monthOffset++ }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Pt", "Sa", "Ça", "Pe", "Cu", "Ct", "Pz").forEach { day ->
                Text(
                    text = day,
                    color = AuraTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        val rows = (leadingBlanks + daysInMonth + 6) / 7
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val day = row * 7 + col - leadingBlanks + 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day in 1..daysInMonth) {
                                val isFuture = isCurrentMonth && day > todayDay
                                DayDot(
                                    day = day,
                                    count = countsByDay[day] ?: 0,
                                    isToday = isCurrentMonth && day == todayDay,
                                    isFuture = isFuture,
                                    isSelected = selectedDay == day,
                                    monthKey = monthOffset,
                                    onClick = { selectedDay = if (selectedDay == day) null else day }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val picked = selectedDay
        AnimatedContent(
            targetState = picked,
            transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(100)) },
            label = "selectedDayLabel"
        ) { day ->
            val text = when {
                day == null -> "Bir güne dokunarak o günün kaydını görün"
                else -> {
                    val n = countsByDay[day] ?: 0
                    val monthName = (month.getDisplayName(Calendar.MONTH, Calendar.LONG, TrLocale) ?: "")
                    "$day $monthName: " + if (n == 0) "kayıt yok" else "$n look kaydedildi"
                }
            }
            Text(
                text = text,
                color = if (day != null && (countsByDay[day] ?: 0) > 0) AuraWhite else AuraTextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MonthStepButton(
    label: String,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .semantics { contentDescription = description }
            .pressable(enabled = enabled, onClick = onClick)
            .clip(CircleShape)
            .background(AuraPillInactive)
            .border(1.dp, AuraCardBorder, CircleShape)
            .graphicsLayer { alpha = if (enabled) 1f else 0.35f },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = AuraWhite, fontSize = 20.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DayDot(
    day: Int,
    count: Int,
    isToday: Boolean,
    isFuture: Boolean,
    isSelected: Boolean,
    monthKey: Int,
    onClick: () -> Unit
) {
    // Dots pop in once per month change, rippling along the grid.
    val appear = remember(monthKey) { Animatable(0f) }
    LaunchedEffect(monthKey) {
        delay(day * 14L)
        appear.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
    }

    val dotSize = when {
        count <= 0 -> 5.dp
        else -> (10 + (minOf(count, 3) - 1) * 3).dp
    }
    val dotColor = when {
        count > 0 -> AuraWhite
        isFuture -> Color(0xFF1F1F23)
        else -> Color(0xFF34343A)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .graphicsLayer {
                scaleX = appear.value
                scaleY = appear.value
            }
            .pressable(haptic = true, pressedScale = 0.88f, onClick = onClick)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF26262B) else Color.Transparent)
            .then(
                if (isToday) Modifier.border(1.5.dp, AuraWhite, CircleShape) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .drawBehind {
                    if (count > 0) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.16f),
                            radius = size.minDimension * 1.15f
                        )
                    }
                }
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}

@Composable
private fun StatsCard(looks: List<LookDay>, totalCount: Int) {
    val stats = remember(looks) {
        val now = System.currentTimeMillis()
        val c = Calendar.getInstance()
        val thisYear = c.get(Calendar.YEAR)
        val thisMonth = c.get(Calendar.MONTH)

        var monthCount = 0
        val monthDays = HashSet<Long>()
        val allDays = HashSet<Long>()
        var best = 0
        var sum = 0
        for (look in looks) {
            c.timeInMillis = look.timestamp
            allDays.add(dayIndex(look.timestamp))
            sum += look.score
            if (look.score > best) best = look.score
            if (c.get(Calendar.YEAR) == thisYear && c.get(Calendar.MONTH) == thisMonth) {
                monthCount++
                monthDays.add(dayIndex(look.timestamp))
            }
        }

        // Streak: consecutive days with a saved look, counting back from today
        // (or from yesterday if nothing was saved yet today).
        val today = dayIndex(now)
        var cursor = if (allDays.contains(today)) today else today - 1
        var streak = 0
        while (allDays.contains(cursor)) {
            streak++
            cursor--
        }

        Stats(
            monthCount = monthCount,
            activeDays = monthDays.size,
            streak = streak,
            average = if (looks.isEmpty()) 0 else sum / looks.size,
            best = best
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AuraShape.Card)
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, AuraShape.Card)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bu ay",
                color = AuraWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Toplam $totalCount look",
                color = AuraTextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                style = AuraTabularNums
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            StatCell(value = stats.monthCount, label = "look", modifier = Modifier.weight(1f))
            StatCell(value = stats.activeDays, label = "aktif gün", modifier = Modifier.weight(1f))
            StatCell(value = stats.streak, label = "gün seri", modifier = Modifier.weight(1f))
        }

        if (looks.isEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Stüdyoda bir look kaydettiğinizde takvimde bir nokta yanar.",
                color = AuraTextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AuraShape.Control)
                    .background(AuraPillInactive)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ortalama skor ${stats.average}",
                    color = AuraOffWhite,
                    fontSize = 13.sp,
                    style = AuraTabularNums
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = AuraWhite,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "En iyi ${stats.best}",
                        color = AuraWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = AuraTabularNums
                    )
                }
            }
        }
    }
}

private data class Stats(
    val monthCount: Int,
    val activeDays: Int,
    val streak: Int,
    val average: Int,
    val best: Int
)

@Composable
private fun StatCell(value: Int, label: String, modifier: Modifier = Modifier) {
    val animated by animateIntAsState(
        targetValue = value,
        animationSpec = tween(700),
        label = "statCount"
    )
    Column(modifier = modifier) {
        Text(
            text = "$animated",
            color = AuraWhite,
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Serif,
            style = AuraTabularNums
        )
        Text(
            text = label,
            color = AuraTextMuted,
            fontSize = 12.sp
        )
    }
}
