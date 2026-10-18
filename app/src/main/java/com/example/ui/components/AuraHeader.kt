package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

/**
 * Vogue-inspired editorial high-fashion top bar with a geometric typographic emblem.
 */
@Composable
fun AuraTopBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    userProfile: UserProfile,
    onOpenProfile: () -> Unit,
    lookbookCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // High Fashion Typographic Monogram & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("vogue_fashion_brand_header")
        ) {
            // Vogue Geometric Emblem
            VogueFashionEmblem(modifier = Modifier.size(34.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "A U R A",
                    color = AuraWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Atelier",
                    color = AuraTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp
                )
            }
        }

        // Action Buttons: Active Style Capsule + Preferences
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Style Tag Capsule
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(AuraCardDark)
                    .border(1.dp, AuraCardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = userProfile.preferredStyles.firstOrNull()?.tag ?: "Minimalist",
                    color = AuraWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Profile / Style Preferences Button
            IconButton(
                onClick = onOpenProfile,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(AuraCardDark)
                    .border(1.dp, AuraCardBorder, CircleShape)
                    .testTag("open_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Tarz Tercihleri",
                    tint = AuraWhite,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

/**
 * Geometric typographic emblem matching Vogue high-fashion editorial aesthetic.
 * Sharp diamond/square frame containing high-contrast stylized serif monogram.
 */
@Composable
fun VogueFashionEmblem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF141416))
            .border(1.dp, AuraWhite.copy(alpha = 0.8f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val w = size.width
            val h = size.height

            // Geometric outer diamond lines
            val diamondPath = Path().apply {
                moveTo(w * 0.5f, 2f)
                lineTo(w - 2f, h * 0.5f)
                lineTo(w * 0.5f, h - 2f)
                lineTo(2f, h * 0.5f)
                close()
            }
            drawPath(
                path = diamondPath,
                color = AuraWhite.copy(alpha = 0.35f),
                style = Stroke(width = 1.dp.toPx())
            )

            // Haute Couture Monogram "A" with sharp apex and horizontal crossbar
            val letterPath = Path().apply {
                moveTo(w * 0.5f, h * 0.22f)
                lineTo(w * 0.24f, h * 0.78f)
                moveTo(w * 0.5f, h * 0.22f)
                lineTo(w * 0.76f, h * 0.78f)
            }
            drawPath(
                path = letterPath,
                color = AuraWhite,
                style = Stroke(width = 2.dp.toPx())
            )

            // Crossbar
            drawLine(
                color = AuraWhite,
                start = Offset(w * 0.34f, h * 0.58f),
                end = Offset(w * 0.66f, h * 0.58f),
                strokeWidth = 1.8.dp.toPx()
            )

            // Serif footings
            drawLine(
                color = AuraWhite,
                start = Offset(w * 0.18f, h * 0.78f),
                end = Offset(w * 0.30f, h * 0.78f),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = AuraWhite,
                start = Offset(w * 0.70f, h * 0.78f),
                end = Offset(w * 0.82f, h * 0.78f),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}
