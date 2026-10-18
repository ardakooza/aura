package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

/**
 * Aura design kit — small shared primitives so every screen shares the same
 * touch feedback, radii and motion instead of re-implementing them per file.
 *
 * Radius scale (hierarchy, not one radius everywhere):
 *   hero 32dp  ·  card 24dp  ·  control 18dp  ·  chip 12dp
 */
object AuraShape {
    val Hero = RoundedCornerShape(32.dp)
    val Card = RoundedCornerShape(24.dp)
    val Control = RoundedCornerShape(18.dp)
    val Chip = RoundedCornerShape(12.dp)
}

/** Tabular figures so animated numbers don't jitter horizontally while counting. */
val AuraTabularNums = TextStyle(fontFeatureSettings = "tnum")

/**
 * Click with a springy press-in scale and a light haptic tick.
 * Replaces the default ripple, which reads as generic Material on a monochrome UI.
 */
fun Modifier.pressable(
    enabled: Boolean = true,
    haptic: Boolean = true,
    pressedScale: Float = 0.96f,
    onClick: () -> Unit
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled
        ) {
            if (haptic) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
}

/** Dashed rounded border. [phase] is read at draw time, so animating it never recomposes. */
fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp = 1.dp,
    dash: Dp = 6.dp,
    gap: Dp = 6.dp,
    phase: () -> Float = { 0f }
): Modifier = this.drawBehind {
    val sw = strokeWidth.toPx()
    val inset = sw / 2f
    drawRoundRect(
        color = color,
        topLeft = Offset(inset, inset),
        size = Size(size.width - sw, size.height - sw),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(
            width = sw,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash.toPx(), gap.toPx()), phase())
        )
    )
}

/** Viewfinder-style corner marks used by the scan overlay and the drop target. */
fun DrawScope.drawCornerBrackets(
    color: Color,
    inset: Float,
    length: Float,
    strokeWidth: Float
) {
    val l = inset
    val t = inset
    val r = size.width - inset
    val b = size.height - inset
    val cap = StrokeCap.Round
    drawLine(color, Offset(l, t + length), Offset(l, t), strokeWidth, cap)
    drawLine(color, Offset(l, t), Offset(l + length, t), strokeWidth, cap)
    drawLine(color, Offset(r - length, t), Offset(r, t), strokeWidth, cap)
    drawLine(color, Offset(r, t), Offset(r, t + length), strokeWidth, cap)
    drawLine(color, Offset(l, b - length), Offset(l, b), strokeWidth, cap)
    drawLine(color, Offset(l, b), Offset(l + length, b), strokeWidth, cap)
    drawLine(color, Offset(r - length, b), Offset(r, b), strokeWidth, cap)
    drawLine(color, Offset(r, b), Offset(r, b - length), strokeWidth, cap)
}

/** Small translucent label that stays legible on top of any photo. */
@Composable
fun GlassChip(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(AuraShape.Chip)
            .background(Color.Black.copy(alpha = 0.58f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), AuraShape.Chip)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        content = content
    )
}

data class SegmentOption(
    val label: String,
    val icon: ImageVector? = null,
    val testTag: String? = null
)

/**
 * Segmented control with a thumb that glides between options (spring), instead of
 * each option flashing its own background. Options share the width equally.
 */
@Composable
fun AuraSegmentedControl(
    options: List<SegmentOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 38.dp
) {
    val shape = RoundedCornerShape(height / 2)
    val haptics = LocalHapticFeedback.current

    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(AuraPillInactive)
            .border(BorderStroke(1.dp, AuraCardBorder), shape)
            .padding(3.dp)
    ) {
        val segmentWidth = maxWidth / options.size
        val thumbOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
            label = "segmentThumb"
        )

        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .clip(shape)
                .background(AuraWhite)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, option ->
                val selected = index == selectedIndex
                val tint by animateColorAsState(
                    targetValue = if (selected) Color.Black else AuraTextMuted,
                    label = "segmentTint"
                )
                val interaction = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(shape)
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            enabled = enabled
                        ) {
                            if (!selected) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onSelect(index)
                        }
                        .then(if (option.testTag != null) Modifier.testTag(option.testTag) else Modifier),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (option.icon != null) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(14.dp)
                        )
                        Box(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = option.label,
                        color = tint,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
