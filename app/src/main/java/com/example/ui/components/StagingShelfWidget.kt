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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith

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
            .clip(AuraShape.Card)
            .background(AuraCardDark)
            .border(1.dp, AuraCardBorder, AuraShape.Card)
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
                        text = "Deneme rafı",
                        color = AuraWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AuraPillInactive)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${stagedItems.size} parça",
                            color = AuraTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (stagedItems.isNotEmpty()) {
                    Text(
                        text = "Rafı boşalt",
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
                        text = "Aşağıdaki raftan parça seçin",
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
                            onRemove = { onRemoveItem(item) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Model tier + output resolution
            val tiers = AIModelTier.values()
            val resolutions = ImageResolution.values()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuraSegmentedControl(
                    options = tiers.map { tier ->
                        SegmentOption(
                            label = when (tier) {
                                AIModelTier.FLASH_IMAGE -> "Hızlı"
                                AIModelTier.PRO_IMAGE -> "Pro"
                            },
                            testTag = "model_tier_${tier.name.lowercase()}"
                        )
                    },
                    selectedIndex = tiers.indexOf(selectedModelTier).coerceAtLeast(0),
                    onSelect = { onSelectModelTier(tiers[it]) },
                    modifier = Modifier.weight(1f),
                    height = 36.dp
                )

                AuraSegmentedControl(
                    options = resolutions.map { res ->
                        SegmentOption(
                            label = res.label,
                            testTag = "res_pill_${res.label.lowercase()}"
                        )
                    },
                    selectedIndex = resolutions.indexOf(selectedResolution).coerceAtLeast(0),
                    onSelect = { onSelectResolution(resolutions[it]) },
                    modifier = Modifier.weight(1.25f),
                    height = 36.dp
                )
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
                shape = AuraShape.Control,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("batch_generate_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AnimatedContent(
                    targetState = isProcessing,
                    transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                    label = "generateLabel"
                ) { processing ->
                    Text(
                        text = if (processing) "Oluşturuluyor…" else "Oluştur ve puanla (${stagedItems.size})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StagedItemChip(
    item: CatalogItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
