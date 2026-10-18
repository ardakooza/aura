package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

/**
 * Floating bottom navigation dock.
 * The selected tab grows into a labelled pill (spring-sized) while the others stay as
 * plain icons, so the active destination is readable without a separate indicator.
 */
@Composable
fun FloatingBottomDock(
    currentTab: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dockShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(24.dp, dockShape, ambientColor = Color.Black, spotColor = Color.Black)
                .clip(dockShape)
                .background(Color(0xFF141416).copy(alpha = 0.96f))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.04f))
                    ),
                    shape = dockShape
                )
                .padding(6.dp)
                .testTag("floating_bottom_dock"),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockItem(
                icon = Icons.Default.GridView,
                label = "Stüdyo",
                isSelected = currentTab == 0,
                onClick = { onSelectTab(0) },
                contentDescription = "Studio",
                testTag = "dock_tab_studio"
            )
            DockItem(
                icon = Icons.Default.CalendarMonth,
                label = "Takvim",
                isSelected = currentTab == 1,
                onClick = { onSelectTab(1) },
                contentDescription = "Style Matrix Calendar",
                testTag = "dock_tab_calendar"
            )
            DockItem(
                icon = Icons.Default.Style,
                label = "Lookbook",
                isSelected = currentTab == 2,
                onClick = { onSelectTab(2) },
                contentDescription = "Lookbook",
                testTag = "dock_tab_lookbook"
            )
        }
    }
}

@Composable
private fun DockItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    testTag: String
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) AuraWhite else Color.Transparent,
        animationSpec = tween(220),
        label = "dockBg"
    )
    val tint by animateColorAsState(
        targetValue = if (isSelected) Color.Black else AuraTextMuted,
        animationSpec = tween(220),
        label = "dockTint"
    )

    Row(
        modifier = Modifier
            .pressable(onClick = onClick)
            .clip(CircleShape)
            .background(bg)
            .animateContentSize(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)
            )
            .height(46.dp)
            .padding(horizontal = 13.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(180, delayMillis = 70)) + expandHorizontally(),
            exit = fadeOut(tween(90)) + shrinkHorizontally()
        ) {
            Text(
                text = label,
                color = tint,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
