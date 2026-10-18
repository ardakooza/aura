package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraWhite

/**
 * Processing overlay: a scan line sweeps the portrait between viewfinder corners while
 * the current stage message crossfades at the bottom. All animated values are read
 * inside draw/graphicsLayer lambdas, so the sweep never triggers recomposition.
 */
@Composable
fun ShimmerPortraitOverlay(
    stageMessage: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "scan")
    val scan by transition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )
    val sweep by transition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing)
        ),
        label = "sweep"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.42f))

            // Soft diagonal sheen travelling across the frame
            val sx = sweep * size.width
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    start = Offset(sx - size.width * 0.25f, 0f),
                    end = Offset(sx + size.width * 0.25f, size.height * 0.4f)
                )
            )

            // Scan line with a trailing glow
            val y = scan * size.height
            val band = size.height * 0.2f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    startY = y - band,
                    endY = y + band
                ),
                topLeft = Offset(0f, y - band),
                size = Size(size.width, band * 2f)
            )
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5.dp.toPx()
            )

            drawCornerBrackets(
                color = Color.White.copy(alpha = 0.5f + 0.4f * pulse),
                inset = 18.dp.toPx(),
                length = 22.dp.toPx(),
                strokeWidth = 2.dp.toPx()
            )
        }

        GlassChip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer { alpha = pulse }
                    .clip(CircleShape)
                    .background(AuraWhite)
            )
            AnimatedContent(
                targetState = stageMessage,
                transitionSpec = { fadeIn(tween(250, delayMillis = 80)) togetherWith fadeOut(tween(150)) },
                label = "stageMessage"
            ) { message ->
                Text(
                    text = message,
                    color = AuraWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    modifier = Modifier.widthIn(max = 250.dp)
                )
            }
        }
    }
}
