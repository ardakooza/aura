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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BeforeAfterViewport(
    originalBitmap: Bitmap?,
    livePreviewBitmap: Bitmap? = null,
    resultBitmap: Bitmap? = null,
    isLivePreviewEnabled: Boolean = true,
    onToggleLivePreview: () -> Unit = {},
    qualityAssessment: QualityAssessment?,
    isProcessing: Boolean,
    stageMessage: String,
    resolution: ImageResolution,
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit,
    modifier: Modifier = Modifier,
    isDropTargetActive: Boolean = false
) {
    val borderColor by animateColorAsState(
        targetValue = if (isDropTargetActive) AuraWhite else AuraCardBorder,
        animationSpec = tween(180),
        label = "viewportBorder"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isDropTargetActive) 2.dp else 1.dp,
        animationSpec = tween(180),
        label = "viewportBorderWidth"
    )
    val dropScale by animateFloatAsState(
        targetValue = if (isDropTargetActive) 1.015f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "viewportDropScale"
    )

    // The "slide to compare" hint only needs to be seen once per result.
    var showCompareHint by remember(resultBitmap) { mutableStateOf(true) }
    LaunchedEffect(resultBitmap) {
        if (resultBitmap != null) {
            delay(3600)
            showCompareHint = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = dropScale
                scaleY = dropScale
            }
            .clip(AuraShape.Hero)
            .background(AuraCardDark)
            .border(borderWidth, borderColor, AuraShape.Hero)
            .testTag("before_after_viewport"),
        contentAlignment = Alignment.Center
    ) {
        if (originalBitmap == null) {
            EmptySelfiePrompt(
                onPickImage = onPickImage,
                onCaptureImage = onCaptureImage
            )
        } else {
            val activeBitmap = when {
                resultBitmap != null -> resultBitmap
                isLivePreviewEnabled && livePreviewBitmap != null -> livePreviewBitmap
                else -> originalBitmap
            }

            if (resultBitmap != null && !isProcessing) {
                SplitComparisonView(
                    originalBitmap = originalBitmap,
                    resultBitmap = resultBitmap
                )
            } else {
                Image(
                    bitmap = activeBitmap.asImageBitmap(),
                    contentDescription = "Portrait Viewport",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Processing overlay fades in/out instead of popping.
            AnimatedVisibility(
                visible = isProcessing,
                enter = fadeIn(tween(220)),
                exit = fadeOut(tween(260))
            ) {
                ShimmerPortraitOverlay(
                    stageMessage = stageMessage,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Drop target: dim the photo, mark the corners, show a single clear label.
            AnimatedVisibility(
                visible = isDropTargetActive,
                enter = fadeIn(tween(160)) + scaleIn(initialScale = 0.96f, animationSpec = tween(200)),
                exit = fadeOut(tween(120))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawRect(Color.Black.copy(alpha = 0.5f))
                            drawCornerBrackets(
                                color = Color.White,
                                inset = 18.dp.toPx(),
                                length = 28.dp.toPx(),
                                strokeWidth = 3.dp.toPx()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "Rafa ekle",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Top-left: resolution + one-time compare hint
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassChip {
                    Text(
                        text = resolution.label,
                        color = AuraWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = AuraTabularNums
                    )
                }

                AnimatedVisibility(
                    visible = resultBitmap != null && !isProcessing && showCompareHint,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    GlassChip {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = AuraWhite,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Kaydırarak karşılaştır",
                            color = AuraWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // Top-right: photo quality
            qualityAssessment?.let { assessment ->
                GlassChip(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .testTag("aura_quality_badge_overlay")
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (assessment.isWarning) Color(0xFFFFB300) else AuraWhite)
                    )
                    Text(
                        text = "Fotoğraf %${assessment.score}",
                        color = AuraWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = AuraTabularNums
                    )
                    Text(
                        text = when (assessment.status) {
                            QualityStatus.OPTIMAL -> "Mükemmel"
                            QualityStatus.LOW_LIGHT -> "Düşük ışık"
                            QualityStatus.OVEREXPOSED -> "Yüksek ışık"
                            QualityStatus.BLURRY -> "Bulanık"
                        },
                        color = if (assessment.isWarning) Color(0xFFFFD54F) else AuraTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
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
    val split = remember { Animatable(0.97f) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    var isDragging by remember { mutableStateOf(false) }
    var isHoldingOriginal by remember { mutableStateOf(false) }

    // One orchestrated moment: when the result lands, the divider sweeps in from the
    // edge to the middle so the person sees what changed without touching anything.
    LaunchedEffect(resultBitmap) {
        split.snapTo(0.97f)
        delay(250)
        split.animateTo(0.5f, tween(1000, easing = FastOutSlowInEasing))
    }

    val knobScale by animateFloatAsState(
        targetValue = if (isDragging) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "knobScale"
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.3f else 1f,
        label = "labelAlpha"
    )
    val originalOverlayAlpha by animateFloatAsState(
        targetValue = if (isHoldingOriginal) 1f else 0f,
        animationSpec = tween(140),
        label = "holdOriginal"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { start ->
                        isDragging = true
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        scope.launch { split.snapTo((start.x / size.width).coerceIn(0.05f, 0.95f)) }
                    },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false }
                ) { change, _ ->
                    change.consume()
                    scope.launch { split.snapTo((change.position.x / size.width).coerceIn(0.05f, 0.95f)) }
                }
            }
            .pointerInput(Unit) {
                // Press-and-hold shows the untouched original. The short delay keeps it from
                // flashing every time a drag starts.
                detectTapGestures(
                    onPress = {
                        val job = scope.launch {
                            delay(220)
                            isHoldingOriginal = true
                        }
                        tryAwaitRelease()
                        job.cancel()
                        isHoldingOriginal = false
                    }
                )
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()

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
                    val x = split.value * size.width
                    clipRect(left = 0f, top = 0f, right = x, bottom = size.height) {
                        this@drawWithContent.drawContent()
                    }
                }
        )

        // Press-and-hold: full original on top
        Image(
            bitmap = originalBitmap.asImageBitmap(),
            contentDescription = "Original Portrait",
            contentScale = ContentScale.Crop,
            alpha = originalOverlayAlpha,
            modifier = Modifier.fillMaxSize()
        )

        // Divider
        Box(
            modifier = Modifier
                .offset { IntOffset((split.value * widthPx - 1.dp.toPx()).roundToInt(), 0) }
                .width(2.dp)
                .fillMaxHeight()
                .graphicsLayer { alpha = 1f - originalOverlayAlpha }
                .background(AuraWhite)
        )

        // Handle: two chevrons drawn directly so they stay crisp at any size
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (split.value * widthPx - 20.dp.toPx()).roundToInt(),
                        (constraints.maxHeight * 0.5f - 20.dp.toPx()).roundToInt()
                    )
                }
                .graphicsLayer {
                    scaleX = knobScale
                    scaleY = knobScale
                    alpha = 1f - originalOverlayAlpha
                }
                .size(40.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(AuraWhite),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(20.dp)) {
                val sw = 2.dp.toPx()
                val cy = size.height / 2f
                val a = size.width * 0.18f
                val d = size.height * 0.2f
                // left chevron
                drawLine(Color.Black, Offset(size.width * 0.34f, cy - d), Offset(a + size.width * 0.16f, cy), sw, StrokeCap.Round)
                drawLine(Color.Black, Offset(a + size.width * 0.16f, cy), Offset(size.width * 0.34f, cy + d), sw, StrokeCap.Round)
                // right chevron
                drawLine(Color.Black, Offset(size.width * 0.66f, cy - d), Offset(size.width - a - size.width * 0.16f, cy), sw, StrokeCap.Round)
                drawLine(Color.Black, Offset(size.width - a - size.width * 0.16f, cy), Offset(size.width * 0.66f, cy + d), sw, StrokeCap.Round)
            }
        }

        // Side labels
        GlassChip(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .graphicsLayer { alpha = labelAlpha * (1f - originalOverlayAlpha) }
        ) {
            Text(text = "Önce", color = AuraWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        GlassChip(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .graphicsLayer { alpha = labelAlpha * (1f - originalOverlayAlpha) }
        ) {
            Text(text = "Sonra", color = AuraWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        GlassChip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .graphicsLayer { alpha = originalOverlayAlpha }
        ) {
            Text(text = "Orijinal fotoğraf", color = AuraWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
