package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

/**
 * Floating bottom navigation dock directly modeled after user reference screenshot
 * (Capsule container with Grid, Calendar, and Lookbook icons).
 */
@Composable
fun FloatingBottomDock(
    currentTab: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF141416).copy(alpha = 0.94f))
                .border(1.dp, AuraCardBorder, RoundedCornerShape(32.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("floating_bottom_dock")
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockIconItem(
                    icon = Icons.Default.GridView,
                    isSelected = currentTab == 0,
                    onClick = { onSelectTab(0) },
                    contentDescription = "Studio",
                    testTag = "dock_tab_studio"
                )

                DockIconItem(
                    icon = Icons.Default.CalendarMonth,
                    isSelected = currentTab == 1,
                    onClick = { onSelectTab(1) },
                    contentDescription = "Style Matrix Calendar",
                    testTag = "dock_tab_calendar"
                )

                DockIconItem(
                    icon = Icons.Default.Style,
                    isSelected = currentTab == 2,
                    onClick = { onSelectTab(2) },
                    contentDescription = "Lookbook",
                    testTag = "dock_tab_lookbook"
                )
            }
        }
    }
}

@Composable
private fun DockIconItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    testTag: String
) {
    val bg by animateColorAsState(if (isSelected) AuraWhite else Color.Transparent, label = "dockBg")
    val tint by animateColorAsState(if (isSelected) Color.Black else AuraTextMuted, label = "dockTint")

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}
