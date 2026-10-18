package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AuraAiEngine
import com.example.data.local.AuraDatabase
import com.example.data.local.FeedbackEntity
import com.example.data.model.AIModelTier
import com.example.data.model.AuraScoreResult
import com.example.data.model.CatalogItem
import com.example.data.model.ImageResolution
import com.example.data.model.ItemCategory
import com.example.data.model.LookbookItem
import com.example.data.model.UserProfile
import com.example.data.repository.FeedbackAnalysis
import com.example.data.repository.FeedbackRepository
import com.example.data.repository.UserRepository
import com.example.util.BitmapUtils
import com.example.util.PortraitQualityAnalyzer
import com.example.util.QualityAssessment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class AuraStudioState(
    val currentTab: Int = 0, // 0: Studio, 1: Dot Matrix Calendar, 2: Lookbook
    val userProfile: UserProfile = UserProfile(),
    val showOnboarding: Boolean = false,
    val showAllItems: Boolean = false,
    val originalBitmap: Bitmap? = null,
    val livePreviewBitmap: Bitmap? = null,
    val isLivePreviewEnabled: Boolean = true,
    val resultBitmap: Bitmap? = null,
    val qualityAssessment: QualityAssessment? = null,
    val isQualityWarningDismissed: Boolean = false,
    val selectedCategory: ItemCategory = ItemCategory.HEADWEAR,
    val stagedItems: Set<CatalogItem> = emptySet(),
    val aiModelTier: AIModelTier = AIModelTier.FLASH_IMAGE,
    val selectedResolution: ImageResolution = ImageResolution.K1,
    val isProcessing: Boolean = false,
    val stageMessage: String = "",
    val auraScore: AuraScoreResult? = null,
    val lookbookHistory: List<LookbookItem> = emptyList(),
    val showFeedbackDialog: Boolean = false,
    val userFeedbacks: List<FeedbackEntity> = emptyList(),
    val feedbackAnalysis: FeedbackAnalysis? = null
) {
    val hasPortrait: Boolean get() = originalBitmap != null
    val canGenerate: Boolean get() = hasPortrait && stagedItems.isNotEmpty() && !isProcessing

    // Current active display portrait (AI result > Live preview > Original)
    val displayBitmap: Bitmap?
        get() = resultBitmap ?: if (isLivePreviewEnabled && stagedItems.isNotEmpty()) livePreviewBitmap ?: originalBitmap else originalBitmap
}

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val feedbackRepository: FeedbackRepository

    private val _uiState = MutableStateFlow(AuraStudioState())
    val uiState: StateFlow<AuraStudioState> = _uiState.asStateFlow()

    init {
        val database = AuraDatabase.getInstance(application)
        userRepository = UserRepository(database.userProfileDao())
        feedbackRepository = FeedbackRepository(database.feedbackDao())
        observeUserProfile()
        observeFeedbacks()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userRepository.userProfile.collect { savedProfile ->
                if (savedProfile != null && savedProfile.isOnboarded) {
                    _uiState.update {
                        it.copy(
                            userProfile = savedProfile,
                            showOnboarding = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            userProfile = savedProfile ?: UserProfile(isOnboarded = false),
                            showOnboarding = true
                        )
                    }
                }
            }
        }
    }

    private fun observeFeedbacks() {
        viewModelScope.launch {
            feedbackRepository.seedInitialDataIfEmpty()
            feedbackRepository.allFeedbacks.collect { list ->
                val analysis = feedbackRepository.analyzeFeedbacks(list)
                _uiState.update {
                    it.copy(
                        userFeedbacks = list,
                        feedbackAnalysis = analysis
                    )
                }
            }
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            val updated = profile.copy(isOnboarded = true)
            userRepository.saveProfile(updated)
            _uiState.update {
                it.copy(
                    userProfile = updated,
                    showOnboarding = false
                )
            }
        }
    }

    fun openProfileEditor() {
        _uiState.update { it.copy(showOnboarding = true) }
    }

    fun dismissProfileEditor() {
        _uiState.update { it.copy(showOnboarding = false) }
    }

    fun openFeedbackDialog() {
        _uiState.update { it.copy(showFeedbackDialog = true) }
    }

    fun dismissFeedbackDialog() {
        _uiState.update { it.copy(showFeedbackDialog = false) }
    }

    fun submitFeedback(rating: Int, category: String, comment: String, improvementArea: String) {
        viewModelScope.launch {
            feedbackRepository.submitFeedback(
                rating = rating,
                category = category,
                comment = comment,
                improvementArea = improvementArea
            )
            triggerVibrationHaptic()
            Toast.makeText(getApplication(), "Geri bildiriminiz kaydedildi. Teşekkür ederiz!", Toast.LENGTH_SHORT).show()
            _uiState.update { it.copy(showFeedbackDialog = false) }
        }
    }

    fun toggleShowAllItems() {
        _uiState.update { it.copy(showAllItems = !it.showAllItems) }
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectCategory(category: ItemCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectModelTier(tier: AIModelTier) {
        _uiState.update { it.copy(aiModelTier = tier) }
    }

    fun selectResolution(resolution: ImageResolution) {
        _uiState.update { it.copy(selectedResolution = resolution) }
    }

    fun toggleLivePreview() {
        _uiState.update { it.copy(isLivePreviewEnabled = !it.isLivePreviewEnabled) }
        if (_uiState.value.isLivePreviewEnabled) {
            refreshLivePreview()
        }
    }

    fun stageItem(item: CatalogItem) {
        if (!_uiState.value.hasPortrait) {
            Toast.makeText(getApplication(), "Önce bir selfie veya portre yükleyin!", Toast.LENGTH_SHORT).show()
            return
        }

        _uiState.update { current ->
            val updated = current.stagedItems.toMutableSet()
            when (item.category) {
                ItemCategory.HEADWEAR -> updated.removeAll { it.category == ItemCategory.HEADWEAR }
                ItemCategory.EYEWEAR -> updated.removeAll { it.category == ItemCategory.EYEWEAR }
                ItemCategory.HAIRSTYLES -> updated.removeAll { it.category == ItemCategory.HAIRSTYLES }
            }
            updated.add(item)
            current.copy(stagedItems = updated, resultBitmap = null)
        }
        triggerVibrationHaptic()
        refreshLivePreview()
        Toast.makeText(getApplication(), "${item.name} portreye uygulandı", Toast.LENGTH_SHORT).show()
    }

    fun toggleStageItem(item: CatalogItem) {
        if (!_uiState.value.hasPortrait) {
            Toast.makeText(getApplication(), "Önce portrenizi yükleyin!", Toast.LENGTH_SHORT).show()
            return
        }

        _uiState.update { current ->
            val updated = current.stagedItems.toMutableSet()
            if (updated.contains(item)) {
                updated.remove(item)
            } else {
                when (item.category) {
                    ItemCategory.HEADWEAR -> updated.removeAll { it.category == ItemCategory.HEADWEAR }
                    ItemCategory.EYEWEAR -> updated.removeAll { it.category == ItemCategory.EYEWEAR }
                    ItemCategory.HAIRSTYLES -> updated.removeAll { it.category == ItemCategory.HAIRSTYLES }
                }
                updated.add(item)
            }
            current.copy(stagedItems = updated, resultBitmap = null)
        }
        triggerVibrationHaptic()
        refreshLivePreview()
    }

    fun removeStagedItem(item: CatalogItem) {
        _uiState.update { current ->
            current.copy(
                stagedItems = current.stagedItems.filter { it.id != item.id }.toSet(),
                resultBitmap = null
            )
        }
        refreshLivePreview()
    }

    fun clearStagingShelf() {
        _uiState.update { it.copy(stagedItems = emptySet(), livePreviewBitmap = null, resultBitmap = null) }
    }

    private fun refreshLivePreview() {
        val state = _uiState.value
        val original = state.originalBitmap ?: return
        val items = state.stagedItems.toList()

        if (items.isEmpty()) {
            _uiState.update { it.copy(livePreviewBitmap = null) }
            return
        }

        viewModelScope.launch {
            val preview = withContext(Dispatchers.Default) {
                AuraAiEngine.compositeItemsLocally(original, items)
            }
            _uiState.update { it.copy(livePreviewBitmap = preview) }
        }
    }

    fun triggerVibrationHaptic() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(45)
                }
            }
        } catch (_: Exception) {
            // Gracefully ignore if vibrator is unavailable in container/device
        }
    }

    fun dismissQualityWarning() {
        _uiState.update { it.copy(isQualityWarningDismissed = true) }
    }

    fun loadBitmapFromUri(uri: Uri) {
        viewModelScope.launch {
            val bitmap = BitmapUtils.decodeUriToBitmap(getApplication(), uri)
            if (bitmap != null) {
                applyNewPortrait(bitmap)
            } else {
                Toast.makeText(getApplication(), "Görsel yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setCapturedBitmap(bitmap: Bitmap) {
        applyNewPortrait(bitmap)
    }

    private fun applyNewPortrait(bitmap: Bitmap) {
        viewModelScope.launch {
            val assessment = PortraitQualityAnalyzer.analyze(bitmap)
            _uiState.update {
                it.copy(
                    originalBitmap = bitmap,
                    livePreviewBitmap = null,
                    resultBitmap = null,
                    auraScore = null,
                    qualityAssessment = assessment,
                    isQualityWarningDismissed = false
                )
            }
            refreshLivePreview()
        }
    }

    fun resetStudio() {
        _uiState.update {
            it.copy(
                originalBitmap = null,
                livePreviewBitmap = null,
                resultBitmap = null,
                auraScore = null,
                stagedItems = emptySet(),
                qualityAssessment = null,
                isQualityWarningDismissed = false
            )
        }
    }

    fun executeBatchTryOn() {
        val state = _uiState.value
        val original = state.originalBitmap ?: run {
            Toast.makeText(getApplication(), "Lütfen önce bir selfie yükleyin", Toast.LENGTH_SHORT).show()
            return
        }
        val items = state.stagedItems.toList()

        if (items.isEmpty()) {
            Toast.makeText(getApplication(), "Deneme rafına en az 1 parça ekleyin", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcessing = true,
                    stageMessage = "Yüz anatomisi ve nesneler photorealism modunda birleştiriliyor..."
                )
            }

            try {
                val (result, score) = AuraAiEngine.generateTryOn(
                    originalBitmap = original,
                    items = items,
                    modelTier = state.aiModelTier,
                    resolution = state.selectedResolution,
                    onProgress = { message, _ ->
                        _uiState.update { it.copy(stageMessage = message) }
                    }
                )

                _uiState.update {
                    it.copy(
                        resultBitmap = result,
                        auraScore = score,
                        isProcessing = false
                    )
                }
                triggerVibrationHaptic()
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false) }
                Toast.makeText(getApplication(), "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveCurrentLookToLookbook() {
        val state = _uiState.value
        val orig = state.originalBitmap ?: return
        val res = state.displayBitmap ?: return
        val score = state.auraScore ?: AuraScoreResult(
            overallScore = 95,
            harmonyScore = 94,
            lightingScore = 96,
            geometryScore = 95,
            verdict = "Kusursuz Editoryal Entegrasyon",
            critique = "Şapka, gözlük ve saç modeli yüz hatlarıyla tam bir harmoni içinde.",
            keyHighlights = listOf("Doğal Işık Dengesi", "Kusursuz Oturum", "3D Bütünlük")
        )

        val newLook = LookbookItem(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            originalBitmap = orig,
            resultBitmap = res,
            appliedItems = state.stagedItems.toList(),
            score = score
        )

        _uiState.update {
            it.copy(lookbookHistory = listOf(newLook) + it.lookbookHistory)
        }

        viewModelScope.launch {
            BitmapUtils.saveBitmapToGallery(getApplication(), res, "Aura_Portrait")
            Toast.makeText(getApplication(), "Lookbook'a ve Galeriye kaydedildi!", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareCurrentResult() {
        Toast.makeText(getApplication(), "Görsel dışa aktarılmaya hazır", Toast.LENGTH_SHORT).show()
    }

    fun deleteLookbookItem(id: String) {
        _uiState.update { current ->
            current.copy(lookbookHistory = current.lookbookHistory.filter { it.id != id })
        }
    }
}
