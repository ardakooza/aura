package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ImageResolution
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraOffWhite
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite
import com.example.util.QualityAssessment
import com.example.util.QualityStatus
import kotlin.math.roundToInt

@Composable
fun BeforeAfterViewport(
    originalBitmap: Bitmap?,
    resultBitmap: Bitmap?,
    qualityAssessment: QualityAssessment?,
    isProcessing: Boolean,
    stageMessage: String,
    resolution: ImageResolution,
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit,
    modifier: Modifier = Modifier,
    isDropTargetActive: Boolean = false
) {
    val borderColor = if (isDropTargetActive) AuraWhite else AuraCardBorder
    val borderWidth = if (isDropTargetActive) 2.5.dp else 1.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(32.dp))
            .background(AuraCardDark)
            .border(borderWidth, borderColor, RoundedCornerShape(32.dp))
            .testTag("before_after_viewport"),
        contentAlignment = Alignment.Center
    ) {
        if (originalBitmap == null) {
            // Empty State (Strictly Real Selfie Upload without demo buttons)
            EmptySelfiePrompt(
                onPickImage = onPickImage,
                onCaptureImage = onCaptureImage
            )
        } else {
            // Interactive Display
            if (resultBitmap == null || isProcessing) {
                Image(
                    bitmap = originalBitmap.asImageBitmap(),
                    contentDescription = "Original Portrait",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (isProcessing) {
                    ShimmerPortraitOverlay(
                        stageMessage = stageMessage,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Interactive Before / After Split Slider
                SplitComparisonView(
                    originalBitmap = originalBitmap,
                    resultBitmap = resultBitmap
                )
            }

            // Drop Target Overlay when user is dragging an item over portrait
            if (isDropTargetActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BURAYA BIRAKIN",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Top-Left Resolution Tag & Compare Indicator
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, AuraCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = resolution.label,
                        color = AuraWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (resultBitmap != null && !isProcessing) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, AuraCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = null,
                                tint = AuraWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Kaydır",
                                color = AuraWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Top-Right: Visual Aura Quality Badge Overlay (Requirement 4: 1 + 2 format)
            qualityAssessment?.let { assessment ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.72f))
                        .border(1.dp, AuraWhite.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("aura_quality_badge_overlay")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (assessment.isWarning) Color(0xFFFFB300) else AuraWhite)
                        )
                        Text(
                            text = "AURA KALİTE: %${assessment.score}",
                            color = AuraWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.4.sp
                        )
                        Text(
                            text = "•",
                            color = AuraTextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = when (assessment.status) {
                                QualityStatus.OPTIMAL -> "Mükemmel"
                                QualityStatus.LOW_LIGHT -> "Düşük Işık"
                                QualityStatus.OVEREXPOSED -> "Yüksek Işık"
                                QualityStatus.BLURRY -> "Bulanık"
                            },
                            color = if (assessment.isWarning) Color(0xFFFFD54F) else AuraOffWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SplitComparisonView(
    originalBitmap: Bitmap,
    resultBitmap: Bitmap
) {
    var splitFraction by remember { mutableFloatStateOf(0.5f) }
    var isHoldingOriginal by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val newFraction = (change.position.x / size.width).coerceIn(0.05f, 0.95f)
                    splitFraction = newFraction
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHoldingOriginal = true
                        tryAwaitRelease()
                        isHoldingOriginal = false
                    }
                )
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val dividerX = widthPx * splitFraction

        if (isHoldingOriginal) {
            Image(
                bitmap = originalBitmap.asImageBitmap(),
                contentDescription = "Original Portrait",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Orijinal Ham Selfie",
                    color = AuraWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Image(
                bitmap = resultBitmap.asImageBitmap(),
                contentDescription = "Try-On Result",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Image(
                bitmap = originalBitmap.asImageBitmap(),
                contentDescription = "Original Portion",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        clipPath(Path().apply {
                            addRect(Rect(0f, 0f, dividerX, heightPx))
                        }) {
                            this@drawWithContent.drawContent()
                        }
                    }
            )

            // Divider Line
            Box(
                modifier = Modifier
                    .offset { IntOffset(dividerX.roundToInt(), 0) }
                    .width(2.dp)
                    .height(this@BoxWithConstraints.maxHeight)
                    .background(AuraWhite)
            )

            // Draggable Knob / Handle
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dividerX - 18.dp.toPx()).roundToInt(),
                            (heightPx * 0.5f - 18.dp.toPx()).roundToInt()
                        )
                    }
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AuraWhite)
                    .border(2.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Compare,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Labels: ÖNCE & SONRA (AURA)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ÖNCE",
                    color = AuraWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Text(
                    text = "SONRA (AURA)",
                    color = AuraWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptySelfiePrompt(
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AuraSurfaceElevated)
                .border(1.dp, AuraCardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                tint = AuraWhite,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selfie'nizi Yükleyin",
            color = AuraWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Yüz hatlarınızı analiz edip gerçek 3D gözlük ve saç stillerini kusursuz entegre eder.",
            color = AuraTextMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AuraWhite)
                    .pointerInput(Unit) { detectTapGestures { onCaptureImage() } }
                    .padding(horizontal = 18.dp, vertical = 11.dp)
                    .testTag("empty_camera_selfie_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Selfie Çek",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AuraPillInactive)
                    .border(1.dp, AuraCardBorder, RoundedCornerShape(20.dp))
                    .pointerInput(Unit) { detectTapGestures { onPickImage() } }
                    .padding(horizontal = 18.dp, vertical = 11.dp)
                    .testTag("empty_gallery_picker_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = AuraWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Galeriden Seç",
                        color = AuraWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
