package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AIModelTier
import com.example.data.model.ItemCategory
import com.example.data.model.UserGender
import com.example.data.repository.CatalogRepository
import com.example.util.PortraitQualityAnalyzer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aura", appName)
  }

  @Test
  fun `catalog repository has real eyewear and hairstyles filtered by gender`() {
    val maleGlasses = CatalogRepository.getFilteredItems(ItemCategory.EYEWEAR, UserGender.MALE, showAll = false)
    val femaleGlasses = CatalogRepository.getFilteredItems(ItemCategory.EYEWEAR, UserGender.FEMALE, showAll = false)
    val allGlasses = CatalogRepository.getFilteredItems(ItemCategory.EYEWEAR, UserGender.MALE, showAll = true)

    assertTrue("Male glasses should not be empty", maleGlasses.isNotEmpty())
    assertTrue("Female glasses should not be empty", femaleGlasses.isNotEmpty())
    assertTrue("All glasses count should be at least male glasses count", allGlasses.size >= maleGlasses.size)

    val maleHair = CatalogRepository.getFilteredItems(ItemCategory.HAIRSTYLES, UserGender.MALE, showAll = false)
    val femaleHair = CatalogRepository.getFilteredItems(ItemCategory.HAIRSTYLES, UserGender.FEMALE, showAll = false)
    assertTrue("Male hair should not be empty", maleHair.isNotEmpty())
    assertTrue("Female hair should not be empty", femaleHair.isNotEmpty())
  }

  @Test
  fun `portrait quality analyzer evaluates bitmap`() = runBlocking {
    val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val assessment = PortraitQualityAnalyzer.analyze(testBitmap)
    assertNotNull(assessment)
    assertTrue(assessment.score in 0..100)
    assertTrue(assessment.message.isNotEmpty())
  }

  @Test
  fun `ai model tier offers economical flash and pro studio tiers`() {
    val flash = AIModelTier.FLASH_IMAGE
    val pro = AIModelTier.PRO_IMAGE
    assertEquals("gemini-2.5-flash-image", flash.modelId)
    assertEquals("gemini-3-pro-image-preview", pro.modelId)
  }
}
