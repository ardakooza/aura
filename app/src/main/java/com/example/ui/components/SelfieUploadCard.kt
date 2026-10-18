package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraOffWhite
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite
import com.example.util.QualityAssessment
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily

@Composable
fun SelfieUploadCard(
    hasPortrait: Boolean,
    qualityAssessment: QualityAssessment?,
    isQualityWarningDismissed: Boolean,
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit,
    onDismissWarning: () -> Unit,
    onResetPortrait: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!hasPortrait) {
            // Intake container: a dashed edge reads as "drop something here"; the dashes drift slowly.
            val dashTransition = rememberInfiniteTransition(label = "intakeDash")
            val dashPhase by dashTransition.animateFloat(
                initialValue = 0f,
                targetValue = 48f,
                animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing)),
                label = "dashPhase"
            )
            val breathe by dashTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.06f,
                animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "breathe"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AuraShape.Hero)
                    .background(AuraCardDark)
                    .dashedBorder(
                        color = AuraCardBorder.copy(alpha = 0.9f),
                        cornerRadius = 32.dp,
                        strokeWidth = 1.5.dp,
                        phase = { dashPhase }
                    )
                    .padding(24.dp)
                    .testTag("selfie_upload_card")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .graphicsLayer {
                                scaleX = breathe
                                scaleY = breathe
                            }
                            .clip(CircleShape)
                            .background(AuraSurfaceElevated)
                            .border(1.dp, AuraCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Upload Selfie",
                            tint = AuraWhite,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bir selfie ekleyin",
                        color = AuraWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Gözlük, şapka ve saç modellerini yüzünüzde deneyin. Yüz hatlarınız ve ışık korunur.",
                        color = AuraTextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onCaptureImage,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraWhite,
                                contentColor = Color.Black
                            ),
                            shape = AuraShape.Control,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("camera_selfie_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Selfie çek",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onPickImage,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraWhite),
                            shape = AuraShape.Control,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuraCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("gallery_picker_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Galeri",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Quality Warning Banner
            qualityAssessment?.let { assessment ->
                AnimatedVisibility(
                    visible = assessment.isWarning && !isQualityWarningDismissed,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(AuraSurfaceElevated)
                            .border(1.dp, AuraCardBorder, RoundedCornerShape(22.dp))
                            .padding(16.dp)
                            .testTag("quality_warning_banner")
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = AuraWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Görsel Kalite Analizi",
                                    color = AuraWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "%${assessment.score}",
                                    color = AuraTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = assessment.message,
                                color = AuraOffWhite,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onResetPortrait,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraWhite),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("retake_photo_button")
                                ) {
                                    Text(
                                        text = "Farklı Görsel Seç",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = onDismissWarning,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AuraWhite,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("proceed_anyway_button")
                                ) {
                                    Text(
                                        text = "Yine de Devam Et",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
