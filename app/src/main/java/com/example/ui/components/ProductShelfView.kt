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
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (!hasPortrait) Modifier.alpha(0.35f) else Modifier)
        ) {
            // Category & Gender Filter Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shelf Category Tabs: Gözlük Modelleri & Saç Stilleri
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ItemCategory.values().forEach { category ->
                        val isSelected = category == selectedCategory
                        val bg by animateColorAsState(if (isSelected) AuraWhite else AuraPillInactive, label = "shelfTabBg")
                        val txt by animateColorAsState(if (isSelected) Color.Black else AuraTextMuted, label = "shelfTabTxt")

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(bg)
                                .border(1.dp, if (isSelected) AuraWhite else AuraCardBorder, RoundedCornerShape(18.dp))
                                .clickable(enabled = hasPortrait) { onSelectCategory(category) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("shelf_tab_${category.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val icon = when (category) {
                                    ItemCategory.EYEWEAR -> Icons.Default.Visibility
                                    ItemCategory.HAIRSTYLES -> Icons.Default.FaceRetouchingNatural
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = txt,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.trTitle,
                                    color = txt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Filter Pill: Önerilen (Cinsiyet) vs Tümü
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (showAllItems) AuraPillInactive else Color.White.copy(alpha = 0.12f))
                        .border(1.dp, AuraCardBorder, RoundedCornerShape(14.dp))
                        .clickable(enabled = hasPortrait) { onToggleShowAll() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("gender_filter_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
            }

            Spacer(modifier = Modifier.height(14.dp))

            // The Physical "Raf" (Shelf) Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1E22), // Top illuminated acrylic edge
                                Color(0xFF121214)  // Base shelf
                            )
                        )
                    )
                    .border(1.dp, AuraCardBorder, RoundedCornerShape(26.dp))
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
                        Text(
                            text = "3D GERÇEK ÜRÜN RAFI",
                            color = AuraTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Kaydır ve Rafa Ekle",
                            color = AuraWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Scrolling 3D Shelf with Smooth Tactile Physics
                    val listState = rememberLazyListState()
                    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

                    LazyRow(
                        state = listState,
                        flingBehavior = flingBehavior,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
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

                    // Illuminated Shelf Runner Rail
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        AuraWhite.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }

        // Portrait-First Lock Overlay (Requirement 5)
        if (!hasPortrait) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.88f))
                        .border(1.dp, AuraWhite.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                        .testTag("shelf_locked_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "ÖNCE PORTRENİZİ YÜKLEYİN",
                                color = AuraWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "3D ürünleri denemek için önce bir selfie yükleyin",
                                color = AuraTextMuted,
                                fontSize = 10.sp
                            )
                        }
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

    val borderColor = if (isStaged) AuraWhite else AuraCardBorder
    val borderWidth = if (isStaged) 2.dp else 1.dp
    var itemPositionInWindow by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .width(158.dp)
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(AuraSurfaceElevated)
            .border(borderWidth, borderColor, RoundedCornerShape(22.dp))
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
            .clickable(enabled = enabled) { onSelect() }
            .padding(12.dp)
            .testTag("shelf_product_${item.id}")
    ) {
        Column {
            // 3D Product Visual Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(98.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF2A2A32), Color(0xFF141416))
                        )
                    )
                    .border(1.dp, AuraCardBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Realistic 3D Product Visual
                PhotorealisticProductGlyph(category = item.category, itemId = item.id)

                // Staged Toggle Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isStaged) AuraWhite else AuraPillInactive),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isStaged) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isStaged) "Staged" else "Add",
                        tint = if (isStaged) Color.Black else AuraWhite,
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Brand / Model Tag
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
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                color = AuraWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.brandOrModelRef,
                color = AuraTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.material,
                color = Color(0xFF888890),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PhotorealisticProductGlyph(category: ItemCategory, itemId: String) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val w = size.width
        val h = size.height

        when (category) {
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
