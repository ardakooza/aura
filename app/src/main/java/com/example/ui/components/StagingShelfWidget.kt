package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AIModelTier
import com.example.data.model.CatalogItem
import com.example.data.model.ImageResolution
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraCardDark
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

@Composable
fun StagingShelfWidget(
    stagedItems: Set<CatalogItem>,
    selectedModelTier: AIModelTier,
    selectedResolution: ImageResolution,
    isProcessing: Boolean,
    canGenerate: Boolean,
    onRemoveItem: (CatalogItem) -> Unit,
    onClearShelf: () -> Unit,
    onSelectModelTier: (AIModelTier) -> Unit,
    onSelectResolution: (ImageResolution) -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, RoundedCornerShape(26.dp))
            .padding(16.dp)
            .testTag("staging_shelf_widget")
    ) {
        Column {
            // Header Row: Staging Shelf title + count badge + clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (stagedItems.isNotEmpty()) AuraWhite else AuraTextMuted)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DENEME RAFI",
                        color = AuraWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AuraPillInactive)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${stagedItems.size} Parça",
                            color = AuraTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (stagedItems.isNotEmpty()) {
                    Text(
                        text = "Rafı Boşalt",
                        color = AuraTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onClearShelf() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Staged Items Row or Empty prompt
            if (stagedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraPillInactive)
                        .border(1.dp, AuraCardBorder, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aşağıdaki raflardan denemek istediğiniz 3D parçaları seçin",
                        color = AuraTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(stagedItems.toList(), key = { it.id }) { item ->
                        StagedItemChip(
                            item = item,
                            onRemove = { onRemoveItem(item) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Model Tier & Resolution Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dual Model Selector (Flash vs Pro)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraPillInactive)
                        .border(1.dp, AuraCardBorder, RoundedCornerShape(18.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AIModelTier.values().forEach { tier ->
                        val isSelected = tier == selectedModelTier
                        val bg by animateColorAsState(if (isSelected) AuraWhite else Color.Transparent, label = "modelBg")
                        val txt by animateColorAsState(if (isSelected) Color.Black else AuraTextMuted, label = "modelTxt")

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .clickable { onSelectModelTier(tier) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("model_tier_${tier.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tier.title,
                                color = txt,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Resolution Selector (1K / 2K / 4K)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraPillInactive)
                        .border(1.dp, AuraCardBorder, RoundedCornerShape(18.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ImageResolution.values().forEach { res ->
                        val isSelected = res == selectedResolution
                        val bg by animateColorAsState(if (isSelected) AuraWhite else Color.Transparent, label = "resBg")
                        val txt by animateColorAsState(if (isSelected) Color.Black else AuraTextMuted, label = "resTxt")

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .clickable { onSelectResolution(res) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("res_pill_${res.label.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res.label,
                                color = txt,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Batch Generation Button ("Üret & Puanla")
            Button(
                onClick = onGenerate,
                enabled = canGenerate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuraWhite,
                    contentColor = Color.Black,
                    disabledContainerColor = AuraPillInactive,
                    disabledContentColor = AuraTextMuted
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("batch_generate_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isProcessing) "İşleniyor..." else "Üret ve Puanla (${stagedItems.size} Parça)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun StagedItemChip(
    item: CatalogItem,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AuraSurfaceElevated)
            .border(1.dp, AuraCardBorder, RoundedCornerShape(16.dp))
            .padding(start = 10.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Column {
                Text(
                    text = item.name,
                    color = AuraWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.material,
                    color = AuraTextMuted,
                    fontSize = 9.sp
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Kaldır",
                    tint = AuraWhite,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
