package com.example.expensetracker

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.BazaarSentimentPulse
import com.example.expensetracker.data.model.COMMODITY_ENGLISH_DICTIONARY
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.PriceTier
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.buildMarketCommodities
import com.example.expensetracker.data.model.computeBazaarSentimentPulse
import com.example.expensetracker.theme.LightAppColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketCommodityModelsTest {

    private val testColors = LightAppColors

    @Test
    fun testPriceTier_matchingAndFromPrice() {
        assertEquals(PriceTier.BUDGET_UNDER_100, PriceTier.fromPrice(50.0))
        assertEquals(PriceTier.BUDGET_UNDER_100, PriceTier.fromPrice(99.99))
        assertEquals(PriceTier.ESSENTIAL_100_500, PriceTier.fromPrice(100.0))
        assertEquals(PriceTier.ESSENTIAL_100_500, PriceTier.fromPrice(350.0))
        assertEquals(PriceTier.ESSENTIAL_100_500, PriceTier.fromPrice(500.0))
        assertEquals(PriceTier.PREMIUM_OVER_500, PriceTier.fromPrice(500.01))
        assertEquals(PriceTier.PREMIUM_OVER_500, PriceTier.fromPrice(850.0))

        assertTrue(PriceTier.ALL.matches(50.0))
        assertTrue(PriceTier.ALL.matches(300.0))
        assertTrue(PriceTier.ALL.matches(900.0))

        assertTrue(PriceTier.BUDGET_UNDER_100.matches(60.0))
        assertFalse(PriceTier.BUDGET_UNDER_100.matches(100.0))

        assertTrue(PriceTier.ESSENTIAL_100_500.matches(100.0))
        assertTrue(PriceTier.ESSENTIAL_100_500.matches(500.0))
        assertFalse(PriceTier.ESSENTIAL_100_500.matches(650.0))

        assertTrue(PriceTier.PREMIUM_OVER_500.matches(750.0))
        assertFalse(PriceTier.PREMIUM_OVER_500.matches(500.0))
    }

    @Test
    fun testCommodityEnglishDictionary() {
        assertEquals("Potato", COMMODITY_ENGLISH_DICTIONARY["আলু"])
        assertEquals("Beef", COMMODITY_ENGLISH_DICTIONARY["গরুর মাংস"])
        assertEquals("Soybean Oil", COMMODITY_ENGLISH_DICTIONARY["সয়াবিন তেল"])
        assertEquals("Farm Eggs", COMMODITY_ENGLISH_DICTIONARY["ফার্মের ডিম"])
    }

    @Test
    fun testMarketCommodityUiModel_formattingAndEnglishTranslation() {
        val commodity = MarketCommodityUiModel(
            name = "আলু",
            category = "কাঁচাবাজার",
            defaultUnit = "কেজি",
            currentPrice = 60.0,
            isBenchmarkOnly = true
        )

        assertEquals("Potato", commodity.englishName)
        assertEquals("৳৬০/কেজি", commodity.formatCurrentRate(isBangla = true))
        assertEquals("বাজার দর", commodity.formatDiff(isBangla = true))
        assertEquals("Benchmark", commodity.formatDiff(isBangla = false))

        val hikedCommodity = MarketCommodityUiModel(
            name = "গরুর মাংস",
            category = "মাংস",
            defaultUnit = "কেজি",
            currentPrice = 750.0,
            previousPrice = 700.0,
            priceDiff = 50.0,
            percentDiff = 7.14,
            priceTrend = PriceTrend.UP,
            isBenchmarkOnly = false
        )

        assertEquals("Beef", hikedCommodity.englishName)
        assertEquals(PriceTier.PREMIUM_OVER_500, hikedCommodity.priceTier)
        assertTrue(hikedCommodity.isPriceUp)
        assertFalse(hikedCommodity.isPriceDown)
        assertTrue(hikedCommodity.formatDiff(isBangla = true).contains("+৳৫০"))
    }

    @Test
    fun testBuildMarketCommodities_cleanInstallMasterProductsOnly() {
        val masterProducts = listOf(
            MasterProduct(
                id = 1,
                name = "আলু",
                normalizedName = "আলু",
                category = "কাঁচাবাজার",
                defaultUnit = "কেজি",
                latestUnitPrice = 45.0,
                isStaple = true
            ),
            MasterProduct(
                id = 2,
                name = "ব্রয়লার মুরগি",
                normalizedName = "মুরগি",
                category = "মাংস",
                defaultUnit = "কেজি",
                latestUnitPrice = 190.0,
                isStaple = true
            )
        )

        val result = buildMarketCommodities(masterProducts, emptyMap())

        assertEquals(2, result.size)
        val potato = result.find { it.name == "আলু" }
        assertNotNull(potato)
        assertTrue(potato!!.isBenchmarkOnly)
        assertEquals(45.0, potato.currentPrice, 0.01)
        assertEquals("বাজার দর", potato.formatDiff(isBangla = true))
        assertEquals(PriceTier.BUDGET_UNDER_100, potato.priceTier)

        val chicken = result.find { it.name == "ব্রয়লার মুরগি" }
        assertNotNull(chicken)
        assertEquals(PriceTier.ESSENTIAL_100_500, chicken!!.priceTier)
    }

    @Test
    fun testBuildMarketCommodities_mergesWithPersonalPriceInsights() {
        val masterProducts = listOf(
            MasterProduct(
                id = 1,
                name = "আলু",
                normalizedName = "আলু",
                category = "কাঁচাবাজার",
                defaultUnit = "কেজি",
                latestUnitPrice = 45.0,
                isStaple = true
            )
        )

        val insights = mapOf(
            "আলু" to ProductPriceInsight(
                productName = "আলু",
                baseUnit = "কেজি",
                currentUnitPrice = 50.0,
                avgUnitPrice = 46.0,
                minUnitPrice = 40.0,
                maxUnitPrice = 55.0,
                previousUnitPrice = 42.0,
                priceDiff = 8.0,
                percentDiff = 19.05,
                priceTrend = PriceTrend.UP,
                purchaseCount = 5,
                lastPurchasedTimestamp = 1000L,
                recentPricePoints = listOf(40.0, 42.0, 50.0)
            ),
            "আম" to ProductPriceInsight(
                productName = "আম",
                baseUnit = "কেজি",
                currentUnitPrice = 120.0,
                avgUnitPrice = 120.0,
                minUnitPrice = 120.0,
                maxUnitPrice = 120.0,
                previousUnitPrice = 100.0,
                priceDiff = 20.0,
                percentDiff = 20.0,
                priceTrend = PriceTrend.UP,
                purchaseCount = 1,
                lastPurchasedTimestamp = 2000L,
                recentPricePoints = listOf(100.0, 120.0)
            )
        )

        val result = buildMarketCommodities(masterProducts, insights)

        assertEquals(2, result.size)

        val potato = result.find { it.name == "আলু" }
        assertNotNull(potato)
        assertFalse(potato!!.isBenchmarkOnly)
        assertEquals(50.0, potato.currentPrice, 0.01)
        assertEquals(8.0, potato.priceDiff, 0.01)
        assertEquals(PriceTrend.UP, potato.priceTrend)
        assertEquals(3, potato.recentPricePoints.size)

        // Custom product "আম" added from personal insights
        val mango = result.find { it.name == "আম" }
        assertNotNull(mango)
        assertFalse(mango!!.isBenchmarkOnly)
        assertEquals(120.0, mango.currentPrice, 0.01)
    }

    @Test
    fun testComputeBazaarSentimentPulse() {
        // 1. Empty commodities
        val emptyPulse = computeBazaarSentimentPulse(emptyList(), testColors)
        assertEquals(0, emptyPulse.totalTrackedItems)
        assertNull(emptyPulse.inflationIndexPercent)

        // 2. Rising market
        val items = listOf(
            MarketCommodityUiModel(
                name = "পেঁয়াজ",
                currentPrice = 90.0,
                previousPrice = 75.0,
                priceDiff = 15.0,
                percentDiff = 20.0,
                priceTrend = PriceTrend.UP,
                isBenchmarkOnly = false
            ),
            MarketCommodityUiModel(
                name = "ডিম",
                currentPrice = 140.0,
                previousPrice = 135.0,
                priceDiff = 5.0,
                percentDiff = 3.7,
                priceTrend = PriceTrend.UP,
                isBenchmarkOnly = false
            ),
            MarketCommodityUiModel(
                name = "রসুন",
                currentPrice = 220.0,
                previousPrice = 240.0,
                priceDiff = -20.0,
                percentDiff = -8.33,
                priceTrend = PriceTrend.DOWN,
                isBenchmarkOnly = false
            )
        )

        val pulse = computeBazaarSentimentPulse(items, testColors)
        assertEquals(3, pulse.totalTrackedItems)
        assertEquals("ঊর্ধ্বমুখী বাজার", pulse.sentimentTitleBn)
        assertEquals(testColors.expense, pulse.sentimentColor)
        assertEquals("পেঁয়াজ", pulse.topHikeCommodity?.name)
        assertEquals("রসুন", pulse.topBargainCommodity?.name)
    }
}
