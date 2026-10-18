package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogItem
import com.example.data.model.ItemCategory
import com.example.data.repository.CatalogRepository
import com.example.ui.AuraViewModel
import com.example.ui.components.AuraScoreWidget
import com.example.ui.components.BeforeAfterViewport
import com.example.ui.components.ProductShelfView
import com.example.ui.components.SelfieUploadCard
import com.example.ui.components.StagingShelfWidget
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraWhite
import kotlin.math.roundToInt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun StudioScreen(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current

    // Android Photo Picker
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.loadBitmapFromUri(uri)
        }
    }

    // Camera Capture Launcher (Front camera for selfie)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.setCapturedBitmap(bitmap)
        }
    }

    // Drag-and-drop state
    var portraitBoundsInWindow by remember { mutableStateOf<Rect?>(null) }
    var draggingItem by remember { mutableStateOf<CatalogItem?>(null) }
    var dragPositionInWindow by remember { mutableStateOf(Offset.Zero) }
    var isOverPortrait by remember { mutableStateOf(false) }

    // Filter items based on user profile gender and toggle
    val currentCategoryItems = CatalogRepository.getFilteredItems(
        category = state.selectedCategory,
        genderFilter = state.userProfile.gender,
        showAll = state.showAllItems
    )

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("studio_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Clean Selfie Upload Card (With Quality Warning Banner)
            SelfieUploadCard(
                hasPortrait = state.hasPortrait,
                qualityAssessment = state.qualityAssessment,
                isQualityWarningDismissed = state.isQualityWarningDismissed,
                onPickImage = {
                    pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCaptureImage = {
                    takePictureLauncher.launch(null)
                },
                onDismissWarning = {
                    viewModel.dismissQualityWarning()
                },
                onResetPortrait = {
                    viewModel.resetStudio()
                }
            )

            // 2. Main Portrait Viewport with Top-Right Aura Quality Badge Overlay & Before/After Slider
            if (state.hasPortrait) {
                BeforeAfterViewport(
                    originalBitmap = state.originalBitmap,
                    resultBitmap = state.resultBitmap,
                    qualityAssessment = state.qualityAssessment,
                    isProcessing = state.isProcessing,
                    stageMessage = state.stageMessage,
                    resolution = state.selectedResolution,
                    isDropTargetActive = isOverPortrait,
                    onPickImage = {
                        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onCaptureImage = {
                        takePictureLauncher.launch(null)
                    },
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        portraitBoundsInWindow = coordinates.boundsInWindow()
                    }
                )

                // 3. Staging Shelf (Deneme Rafı) - Visible ONLY when items are staged!
                // Initial state is hidden, smoothly animates into view once objects are added/dragged
                AnimatedVisibility(
                    visible = state.stagedItems.isNotEmpty(),
                    enter = fadeIn() + expandVertically(spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    StagingShelfWidget(
                        stagedItems = state.stagedItems,
                        selectedModelTier = state.aiModelTier,
                        selectedResolution = state.selectedResolution,
                        isProcessing = state.isProcessing,
                        canGenerate = state.canGenerate,
                        onRemoveItem = { viewModel.removeStagedItem(it) },
                        onClearShelf = { viewModel.clearStagingShelf() },
                        onSelectModelTier = { viewModel.selectModelTier(it) },
                        onSelectResolution = { viewModel.selectResolution(it) },
                        onGenerate = { viewModel.executeBatchTryOn() }
                    )
                }
            }

            // 4. Tactile Animated 3D Product Shelf (Gözlük & Saç Modelleri) with Drag & Drop
            ProductShelfView(
                hasPortrait = state.hasPortrait,
                userGender = state.userProfile.gender,
                showAllItems = state.showAllItems,
                onToggleShowAll = { viewModel.toggleShowAllItems() },
                selectedCategory = state.selectedCategory,
                onSelectCategory = { viewModel.selectCategory(it) },
                itemsForCategory = currentCategoryItems,
                stagedItems = state.stagedItems,
                onToggleItem = { viewModel.toggleStageItem(it) },
                onStartDrag = { item, globalOffset ->
                    draggingItem = item
                    dragPositionInWindow = globalOffset
                    val bounds = portraitBoundsInWindow
                    isOverPortrait = bounds != null && bounds.contains(globalOffset)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onDrag = { dragAmount ->
                    dragPositionInWindow += dragAmount
                    val bounds = portraitBoundsInWindow
                    isOverPortrait = bounds != null && bounds.contains(dragPositionInWindow)
                },
                onEndDrag = {
                    val item = draggingItem
                    if (item != null && isOverPortrait) {
                        viewModel.stageItem(item)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    draggingItem = null
                    isOverPortrait = false
                },
                onCancelDrag = {
                    draggingItem = null
                    isOverPortrait = false
                }
            )

            // 5. Aura Score Result Widget
            AnimatedVisibility(
                visible = state.auraScore != null && state.resultBitmap != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                state.auraScore?.let { score ->
                    AuraScoreWidget(
                        score = score,
                        onSaveToLookbook = { viewModel.saveCurrentLookToLookbook() },
                        onShare = { viewModel.shareCurrentResult() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(96.dp)) // Clearance for floating bottom dock
        }

        // Floating Drag Ghost Preview
        val item = draggingItem
        if (item != null) {
            val ghostScale by animateFloatAsState(
                targetValue = if (isOverPortrait) 1.1f else 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
                label = "ghostScale"
            )
            val ghostBorder by animateColorAsState(
                targetValue = if (isOverPortrait) AuraWhite else AuraCardBorder,
                label = "ghostBorder"
            )
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragPositionInWindow.x - 70.dp.toPx()).roundToInt(),
                            (dragPositionInWindow.y - 70.dp.toPx()).roundToInt()
                        )
                    }
                    .graphicsLayer {
                        scaleX = ghostScale
                        scaleY = ghostScale
                        rotationZ = if (isOverPortrait) 0f else -3f
                    }
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1C1C20).copy(alpha = 0.96f))
                    .border(1.5.dp, ghostBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (item.category) {
                        ItemCategory.EYEWEAR -> Icons.Default.Visibility
                        ItemCategory.HAIRSTYLES -> Icons.Default.FaceRetouchingNatural
                        ItemCategory.HEADWEAR -> Icons.Default.Style
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AuraWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.name,
                        color = AuraWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
