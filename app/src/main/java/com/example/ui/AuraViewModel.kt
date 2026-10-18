package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AuraAiEngine
import com.example.data.local.AuraDatabase
import com.example.data.model.AIModelTier
import com.example.data.model.AuraScoreResult
import com.example.data.model.CatalogItem
import com.example.data.model.ImageResolution
import com.example.data.model.ItemCategory
import com.example.data.model.LookbookItem
import com.example.data.model.UserProfile
import com.example.data.repository.UserRepository
import com.example.util.BitmapUtils
import com.example.util.PortraitQualityAnalyzer
import com.example.util.QualityAssessment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AuraStudioState(
    val currentTab: Int = 0, // 0: Studio, 1: Dot Matrix Calendar, 2: Lookbook
    val userProfile: UserProfile = UserProfile(),
    val showOnboarding: Boolean = false,
    val showAllItems: Boolean = false,
    val originalBitmap: Bitmap? = null,
    val resultBitmap: Bitmap? = null,
    val qualityAssessment: QualityAssessment? = null,
    val isQualityWarningDismissed: Boolean = false,
    val selectedCategory: ItemCategory = ItemCategory.EYEWEAR,
    val stagedItems: Set<CatalogItem> = emptySet(),
    val aiModelTier: AIModelTier = AIModelTier.FLASH_IMAGE,
    val selectedResolution: ImageResolution = ImageResolution.K1,
    val isProcessing: Boolean = false,
    val stageMessage: String = "",
    val auraScore: AuraScoreResult? = null,
    val lookbookHistory: List<LookbookItem> = emptyList()
) {
    val hasPortrait: Boolean get() = originalBitmap != null
    val canGenerate: Boolean get() = hasPortrait && stagedItems.isNotEmpty() && !isProcessing
}

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository

    private val _uiState = MutableStateFlow(AuraStudioState())
    val uiState: StateFlow<AuraStudioState> = _uiState.asStateFlow()

    init {
        val database = AuraDatabase.getInstance(application)
        userRepository = UserRepository(database.userProfileDao())
        observeUserProfile()
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

    fun stageItem(item: CatalogItem) {
        if (!_uiState.value.hasPortrait) {
            Toast.makeText(getApplication(), "Önce bir selfie veya portre yükleyin!", Toast.LENGTH_SHORT).show()
            return
        }

        _uiState.update { current ->
            val updated = current.stagedItems.toMutableSet()
            if (item.category == ItemCategory.HAIRSTYLES) {
                updated.removeAll { it.category == ItemCategory.HAIRSTYLES }
            } else if (item.category == ItemCategory.EYEWEAR) {
                updated.removeAll { it.category == ItemCategory.EYEWEAR }
            }
            updated.add(item)
            current.copy(stagedItems = updated)
        }
        Toast.makeText(getApplication(), "${item.name} deneme rafına eklendi", Toast.LENGTH_SHORT).show()
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
                if (item.category == ItemCategory.HAIRSTYLES) {
                    updated.removeAll { it.category == ItemCategory.HAIRSTYLES }
                } else if (item.category == ItemCategory.EYEWEAR) {
                    updated.removeAll { it.category == ItemCategory.EYEWEAR }
                }
                updated.add(item)
            }
            current.copy(stagedItems = updated)
        }
    }

    fun removeStagedItem(item: CatalogItem) {
        _uiState.update { current ->
            current.copy(stagedItems = current.stagedItems.filter { it.id != item.id }.toSet())
        }
    }

    fun clearStagingShelf() {
        _uiState.update { it.copy(stagedItems = emptySet()) }
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
                    resultBitmap = null,
                    auraScore = null,
                    qualityAssessment = assessment,
                    isQualityWarningDismissed = false
                )
            }
        }
    }

    fun resetStudio() {
        _uiState.update {
            it.copy(
                originalBitmap = null,
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
                    stageMessage = "Yüz segmentasyonu ve parçalar hazırlanıyor..."
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
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false) }
                Toast.makeText(getApplication(), "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveCurrentLookToLookbook() {
        val state = _uiState.value
        val orig = state.originalBitmap ?: return
        val res = state.resultBitmap ?: return
        val score = state.auraScore ?: return

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
