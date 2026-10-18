package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgeBracket
import com.example.data.model.StyleVibe
import com.example.data.model.UserGender
import com.example.data.model.UserProfile
import com.example.ui.theme.AuraBlack
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraOffWhite
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    currentProfile: UserProfile,
    isEditing: Boolean = false,
    onComplete: (UserProfile) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedGender by remember { mutableStateOf(currentProfile.gender) }
    var selectedAge by remember { mutableStateOf(currentProfile.ageBracket) }
    var selectedStyles by remember { mutableStateOf(currentProfile.preferredStyles) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuraBlack)
            .padding(24.dp)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Step Dots & Dismiss if editing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Monochrome Step Indicator Dots (inspired by the screenshot dots)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .size(if (step == i) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (step == i) AuraWhite else AuraCardBorder)
                        )
                    }
                }

                if (isEditing) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AuraPillInactive)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AuraWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Content
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState > initialState) width else -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> if (targetState > initialState) -width else width } + fadeOut()
                },
                label = "onboardingSteps",
                modifier = Modifier.weight(1f)
            ) { currentStep ->
                when (currentStep) {
                    0 -> GenderStep(
                        selectedGender = selectedGender,
                        onSelect = { selectedGender = it }
                    )
                    1 -> AgeStep(
                        selectedAge = selectedAge,
                        onSelect = { selectedAge = it }
                    )
                    2 -> StyleStep(
                        selectedStyles = selectedStyles,
                        onToggleStyle = { style ->
                            selectedStyles = if (selectedStyles.contains(style)) {
                                if (selectedStyles.size > 1) selectedStyles - style else selectedStyles
                            } else {
                                selectedStyles + style
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    Button(
                        onClick = { step-- },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraPillInactive,
                            contentColor = AuraWhite
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .border(1.dp, AuraCardBorder, RoundedCornerShape(20.dp))
                    ) {
                        Text("Geri", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }

                Button(
                    onClick = {
                        if (step < 2) {
                            step++
                        } else {
                            onComplete(
                                UserProfile(
                                    gender = selectedGender,
                                    ageBracket = selectedAge,
                                    preferredStyles = selectedStyles,
                                    isOnboarded = true
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuraWhite,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("onboarding_next_button")
                ) {
                    Text(
                        text = if (step == 2) "Stüdyoya Başla" else "Devam Et",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GenderStep(
    selectedGender: UserGender,
    onSelect: (UserGender) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AuraPillInactive),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                tint = AuraWhite,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Kimin için deniyorsunuz?",
            color = AuraWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Gözlük ve saç modelleri rafını kişisel tarzınıza göre özelleştirelim.",
            color = AuraTextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            UserGender.values().forEach { gender ->
                val isSelected = gender == selectedGender
                val bg by animateColorAsState(if (isSelected) AuraWhite else AuraCardDark, label = "genderBg")
                val txt by animateColorAsState(if (isSelected) Color.Black else AuraOffWhite, label = "genderTxt")
                val border = if (isSelected) AuraWhite else AuraCardBorder

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(22.dp))
                        .clickable { onSelect(gender) }
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                        .testTag("gender_option_${gender.name.lowercase()}"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = gender.label,
                            color = txt,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgeStep(
    selectedAge: AgeBracket,
    onSelect: (AgeBracket) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AuraPillInactive),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = AuraWhite,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Yaş aralığınız nedir?",
            color = AuraWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Yüz anatomisi ve oranlarına en uygun çerçeve ve saç kesimlerini önermek için kullanılır.",
            color = AuraTextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AgeBracket.values().forEach { age ->
                val isSelected = age == selectedAge
                val bg by animateColorAsState(if (isSelected) AuraWhite else AuraCardDark, label = "ageBg")
                val txt by animateColorAsState(if (isSelected) Color.Black else AuraOffWhite, label = "ageTxt")
                val border = if (isSelected) AuraWhite else AuraCardBorder

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(22.dp))
                        .clickable { onSelect(age) }
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                        .testTag("age_option_${age.name.lowercase()}"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = age.label,
                            color = txt,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StyleStep(
    selectedStyles: Set<StyleVibe>,
    onToggleStyle: (StyleVibe) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AuraPillInactive),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Style,
                contentDescription = null,
                tint = AuraWhite,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hangi tarzları seversiniz?",
            color = AuraWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Birden fazla seçebilirsiniz. Raf ürünleri ve Aura yapay zeka puanlaması buna göre kalibre edilir.",
            color = AuraTextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StyleVibe.values().forEach { style ->
                val isSelected = selectedStyles.contains(style)
                val bg by animateColorAsState(if (isSelected) AuraWhite else AuraCardDark, label = "styleBg")
                val txt by animateColorAsState(if (isSelected) Color.Black else AuraOffWhite, label = "styleTxt")
                val border = if (isSelected) AuraWhite else AuraCardBorder

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(20.dp))
                        .clickable { onToggleStyle(style) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("style_chip_${style.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = style.label,
                            color = txt,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
