package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogItem
import com.example.data.model.ItemCategory
import com.example.data.model.UserGender
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraPillInactive
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraWhite

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer

private fun ItemCategory.shortLabel(): String = when (this) {
    ItemCategory.HEADWEAR -> "Şapka"
    ItemCategory.EYEWEAR -> "Gözlük"
    ItemCategory.HAIRSTYLES -> "Saç"
}

@Composable
fun ProductShelfView(
    hasPortrait: Boolean,
    userGender: UserGender,
    showAllItems: Boolean,
    onToggleShowAll: () -> Unit,
    selectedCategory: ItemCategory,
    onSelectCategory: (ItemCategory) -> Unit,
    itemsForCategory: List<CatalogItem>,
    stagedItems: Set<CatalogItem>,
    onToggleItem: (CatalogItem) -> Unit,
    modifier: Modifier = Modifier,
    onStartDrag: (CatalogItem, Offset) -> Unit = { _, _ -> },
    onDrag: (Offset) -> Unit = {},
    onEndDrag: () -> Unit = {},
    onCancelDrag: () -> Unit = {}
) {
    val categories = ItemCategory.values()
    val shelfAlpha by animateFloatAsState(
        targetValue = if (hasPortrait) 1f else 0.35f,
        animationSpec = tween(300),
        label = "shelfAlpha"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(shelfAlpha)
        ) {
            AuraSegmentedControl(
                options = categories.map {
                    SegmentOption(
                        label = it.shortLabel(),
                        testTag = "shelf_tab_${it.name.lowercase()}"
                    )
                },
                selectedIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                onSelect = { onSelectCategory(categories[it]) },
                enabled = hasPortrait,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AuraShape.Card)
                    .background(AuraCardDark)
                    .border(1.dp, AuraCardBorder, AuraShape.Card)
                    .padding(vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedCategory.trTitle,
                                color = AuraWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dokun: ekle  ·  Basılı tut: portreye sürükle",
                                color = AuraTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Filter pill: gender-recommended vs all
                        Row(
                            modifier = Modifier
                                .pressable(enabled = hasPortrait) { onToggleShowAll() }
                                .clip(AuraShape.Chip)
                                .background(if (showAllItems) AuraPillInactive else Color.White.copy(alpha = 0.12f))
                                .border(1.dp, AuraCardBorder, AuraShape.Chip)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("gender_filter_toggle"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = AuraWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showAllItems) "Tümü (${itemsForCategory.size})" else "${userGender.label} (${itemsForCategory.size})",
                                color = AuraWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val listState = rememberLazyListState()
                    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

                    LazyRow(
                        state = listState,
                        flingBehavior = flingBehavior,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(itemsForCategory, key = { it.id }) { item ->
                            val isStaged = stagedItems.contains(item)
                            ShelfProduct3DCard(
                                item = item,
                                isStaged = isStaged,
                                enabled = hasPortrait,
                                onSelect = { onToggleItem(item) },
                                onStartDrag = onStartDrag,
                                onDrag = onDrag,
                                onEndDrag = onEndDrag,
                                onCancelDrag = onCancelDrag
                            )
                        }
                    }
                }
            }
        }

        // Portrait-first lock: the shelf is only usable once there is a face to try things on.
        AnimatedVisibility(
            visible = !hasPortrait,
            enter = fadeIn(),
            exit = fadeOut(tween(250)),
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .clip(AuraShape.Control)
                        .background(Color.Black.copy(alpha = 0.88f))
                        .border(1.dp, AuraWhite.copy(alpha = 0.25f), AuraShape.Control)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("shelf_locked_overlay"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(AuraPillInactive),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = AuraWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Önce bir portre ekleyin",
                            color = AuraWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Ürünleri denemek için selfie yükleyin",
                            color = AuraTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShelfProduct3DCard(
    item: CatalogItem,
    isStaged: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
    onStartDrag: (CatalogItem, Offset) -> Unit = { _, _ -> },
    onDrag: (Offset) -> Unit = {},
    onEndDrag: () -> Unit = {},
    onCancelDrag: () -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue = if (isStaged) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isStaged) AuraWhite else AuraCardBorder,
        animationSpec = tween(200),
        label = "cardBorder"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isStaged) 1.5.dp else 1.dp,
        animationSpec = tween(200),
        label = "cardBorderWidth"
    )
    val badgeBg by animateColorAsState(
        targetValue = if (isStaged) AuraWhite else AuraPillInactive,
        animationSpec = tween(200),
        label = "badgeBg"
    )
    val badgeScale by animateFloatAsState(
        targetValue = if (isStaged) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "badgeScale"
    )
    var itemPositionInWindow by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .width(158.dp)
            .scale(scale)
            .clip(AuraShape.Card)
            .background(AuraSurfaceElevated)
            .border(borderWidth, borderColor, AuraShape.Card)
            .onGloballyPositioned { coordinates ->
                itemPositionInWindow = coordinates.positionInWindow()
            }
            .pointerInput(enabled, item) {
                if (!enabled) return@pointerInput
                detectDragGesturesAfterLongPress(
                    onDragStart = { localOffset ->
                        onStartDrag(item, itemPositionInWindow + localOffset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onEndDrag() },
                    onDragCancel = { onCancelDrag() }
                )
            }
            .pressable(enabled = enabled, pressedScale = 0.97f) { onSelect() }
            .padding(12.dp)
            .testTag("shelf_product_${item.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(98.dp)
                    .clip(AuraShape.Control)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF2A2A32), Color(0xFF141416))
                        )
                    )
                    .border(1.dp, AuraCardBorder, AuraShape.Control),
                contentAlignment = Alignment.Center
            ) {
                ProductGlyph(category = item.category, itemId = item.id)

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .graphicsLayer {
                            scaleX = badgeScale
                            scaleY = badgeScale
                        }
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isStaged) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isStaged) "Staged" else "Add",
                        tint = if (isStaged) Color.Black else AuraWhite,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.72f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.styleTag,
                        color = AuraWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                color = AuraWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.brandOrModelRef,
                color = AuraTextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.material,
                color = Color(0xFF888890),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProductGlyph(category: ItemCategory, itemId: String) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val w = size.width
        val h = size.height

        when (category) {
            ItemCategory.HEADWEAR -> {
                val crown = Path().apply {
                    moveTo(w * 0.26f, h * 0.60f)
                    cubicTo(w * 0.26f, h * 0.20f, w * 0.74f, h * 0.20f, w * 0.74f, h * 0.60f)
                }
                drawPath(
                    path = crown,
                    color = Color.White,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(w * 0.27f, h * 0.50f),
                    end = Offset(w * 0.73f, h * 0.50f),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(w * 0.12f, h * 0.64f),
                    end = Offset(w * 0.88f, h * 0.64f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            ItemCategory.EYEWEAR -> {
                val frameColor = when {
                    itemId.contains("tf") || itemId.contains("aviator") -> Color(0xFFE0E0E6)
                    itemId.contains("cartier") -> Color(0xFFE8C872) // 18K Gold
                    itemId.contains("op") -> Color(0xFF6B4E3D) // Tortoiseshell
                    itemId.contains("matsuda") -> Color(0xFFD0D0D8) // Silver
                    else -> Color(0xFF161618)
                }

                // Soft raytraced 3D shadow
                drawRoundRect(
                    color = Color(0x66000000),
                    topLeft = Offset(w * 0.12f + 4f, h * 0.35f + 4f),
                    size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.28f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = Color(0x66000000),
                    topLeft = Offset(w * 0.56f + 4f, h * 0.35f + 4f),
                    size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.28f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )

                // Lenses & Frames
                drawRoundRect(
                    color = frameColor,
                    topLeft = Offset(w * 0.12f, h * 0.35f),
                    size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.28f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
                    style = Stroke(width = 3.dp.toPx())
                )
                drawRoundRect(
                    color = frameColor,
                    topLeft = Offset(w * 0.56f, h * 0.35f),
                    size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.28f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Bridge
                drawLine(
                    color = frameColor,
                    start = Offset(w * 0.44f, h * 0.42f),
                    end = Offset(w * 0.56f, h * 0.42f),
                    strokeWidth = 3.dp.toPx()
                )

                // Specular reflection glint
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(w * 0.18f, h * 0.38f),
                    end = Offset(w * 0.24f, h * 0.54f),
                    strokeWidth = 2.dp.toPx()
                )
            }
            ItemCategory.HAIRSTYLES -> {
                val hairPath = Path().apply {
                    moveTo(w * 0.22f, h * 0.65f)
                    cubicTo(w * 0.20f, h * 0.22f, w * 0.80f, h * 0.22f, w * 0.78f, h * 0.65f)
                    lineTo(w * 0.70f, h * 0.48f)
                    cubicTo(w * 0.60f, h * 0.35f, w * 0.40f, h * 0.35f, w * 0.30f, h * 0.48f)
                    close()
                }
                drawPath(hairPath, color = Color.White, style = Stroke(width = 2.5.dp.toPx()))

                // Texture strands
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(w * 0.38f, h * 0.30f),
                    end = Offset(w * 0.44f, h * 0.44f),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(w * 0.52f, h * 0.28f),
                    end = Offset(w * 0.58f, h * 0.44f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }
    }
}
