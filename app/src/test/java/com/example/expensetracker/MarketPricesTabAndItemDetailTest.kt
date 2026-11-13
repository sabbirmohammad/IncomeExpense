package com.example.expensetracker

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.data.model.MovementFilter
import com.example.expensetracker.ui.dashboard.PriceSortOrder
import com.example.expensetracker.ui.market.findDuplicateCommodities
import com.example.expensetracker.ui.market.PriceHistoryRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Stage 3 Unit Tests: Prices tab, Item Detail drill-down, Duplicate merge detection,
 * sorting, and range-filtering invariants.
 */
class MarketPricesTabAndItemDetailTest {

    // =========================================================================
    // 1. DUPLICATE COMMODITY DETECTION & BENGALI CANONICAL NORMALIZATION
    // =========================================================================

    @Test
    fun testBengaliCanonicalCommodityKeyNormalization() {
        // "কাঁচা মরিচ" and "কাঁচামরিচ" must normalize to the exact same canonical key
        val keyWithSpace = UnitConverterEngine.canonicalCommodityKey("কাঁচা মরিচ")
        val keyWithoutSpace = UnitConverterEngine.canonicalCommodityKey("কাঁচামরিচ")
        val keyWithMultiSpace = UnitConverterEngine.canonicalCommodityKey("  কাঁচা   মরিচ  ")
        val keyWithZwj = UnitConverterEngine.canonicalCommodityKey("কাঁচা\u200Dমরিচ")
        val keyWithZwnj = UnitConverterEngine.canonicalCommodityKey("কাঁচা\u200Cমরিচ")

        assertEquals("কাঁচামরিচ", keyWithSpace)
        assertEquals(keyWithSpace, keyWithoutSpace)
        assertEquals(keyWithSpace, keyWithMultiSpace)
        assertEquals(keyWithSpace, keyWithZwj)
        assertEquals(keyWithSpace, keyWithZwnj)
    }

    @Test
    fun testFindDuplicateCommoditiesGrouping() {
        val commodity1 = MarketCommodityUiModel(
            name = "কাঁচা মরিচ",
            category = "কাঁচাবাজার",
            defaultUnit = "কেজি",
            currentPrice = 120.0
        )
        val commodity2 = MarketCommodityUiModel(
            name = "কাঁচামরিচ",
            category = "কাঁচাবাজার",
            defaultUnit = "কেজি",
            currentPrice = 130.0
        )
        val commodity3 = MarketCommodityUiModel(
            name = "আলু",
            category = "কাঁচাবাজার",
            defaultUnit = "কেজি",
            currentPrice = 30.0
        )
        val commodity4 = MarketCommodityUiModel(
            name = "পেঁয়াজ",
            category = "মুদি",
            defaultUnit = "কেজি",
            currentPrice = 80.0
        )

        val duplicates = findDuplicateCommodities(listOf(commodity1, commodity2, commodity3, commodity4))

        // Only "কাঁচা মরিচ" and "কাঁচামরিচ" should be grouped together
        assertEquals(1, duplicates.size)
        val group = duplicates.first()
        assertEquals(2, group.variants.size)
        assertTrue(group.variants.any { it.name == "কাঁচা মরিচ" })
        assertTrue(group.variants.any { it.name == "কাঁচামরিচ" })
        assertFalse(group.variants.any { it.name == "আলু" })
    }

    @Test
    fun testNoDuplicatesWhenAllCommoditiesDistinct() {
        val commodities = listOf(
            MarketCommodityUiModel(name = "আলু", category = "কাঁচাবাজার", defaultUnit = "কেজি", currentPrice = 30.0),
            MarketCommodityUiModel(name = "টমেটো", category = "কাঁচাবাজার", defaultUnit = "কেজি", currentPrice = 60.0),
            MarketCommodityUiModel(name = "সয়াবিন তেল", category = "মুদি", defaultUnit = "লিটার", currentPrice = 180.0)
        )
        val duplicates = findDuplicateCommodities(commodities)
        assertTrue("No duplicate groups should be detected when items are distinct", duplicates.isEmpty())
    }

    // =========================================================================
    // 2. PRICE SORT ORDERS (Recent, Hike, Drop, Most Purchased, Alphabetical)
    // =========================================================================

    @Test
    fun testPriceSortOrderRecent() {
        val itemA = MarketCommodityUiModel(name = "A", category = "cat", defaultUnit = "kg", currentPrice = 10.0, lastPurchasedTimestamp = 1000L)
        val itemB = MarketCommodityUiModel(name = "B", category = "cat", defaultUnit = "kg", currentPrice = 10.0, lastPurchasedTimestamp = 5000L)
        val itemC = MarketCommodityUiModel(name = "C", category = "cat", defaultUnit = "kg", currentPrice = 10.0, lastPurchasedTimestamp = 3000L)

        val sorted = listOf(itemA, itemB, itemC).sortedWith(
            compareByDescending<MarketCommodityUiModel> { it.lastPurchasedTimestamp }
                .thenBy { it.name }
        )

        assertEquals("B", sorted[0].name)
        assertEquals("C", sorted[1].name)
        assertEquals("A", sorted[2].name)
    }

    @Test
    fun testPriceSortOrderHighestHikeAndBiggestDrop() {
        val itemDrop = MarketCommodityUiModel(name = "Drop", category = "cat", defaultUnit = "kg", currentPrice = 80.0, percentDiff = -20.0, priceDiff = -20.0)
        val itemHikeSmall = MarketCommodityUiModel(name = "HikeSmall", category = "cat", defaultUnit = "kg", currentPrice = 110.0, percentDiff = 10.0, priceDiff = 10.0)
        val itemHikeBig = MarketCommodityUiModel(name = "HikeBig", category = "cat", defaultUnit = "kg", currentPrice = 150.0, percentDiff = 50.0, priceDiff = 50.0)
        val itemFlat = MarketCommodityUiModel(name = "Flat", category = "cat", defaultUnit = "kg", currentPrice = 100.0, percentDiff = 0.0, priceDiff = 0.0)

        val list = listOf(itemFlat, itemDrop, itemHikeSmall, itemHikeBig)

        // Highest Hike: 50% -> 10% -> 0% -> -20%
        val sortedHike = list.sortedWith(
            compareByDescending<MarketCommodityUiModel> { it.percentDiff }
                .thenByDescending { it.priceDiff }
                .thenBy { it.name }
        )
        assertEquals("HikeBig", sortedHike[0].name)
        assertEquals("HikeSmall", sortedHike[1].name)
        assertEquals("Flat", sortedHike[2].name)
        assertEquals("Drop", sortedHike[3].name)

        // Biggest Drop: -20% -> 0% -> 10% -> 50%
        val sortedDrop = list.sortedWith(
            compareBy<MarketCommodityUiModel> { it.percentDiff }
                .thenBy { it.priceDiff }
                .thenBy { it.name }
        )
        assertEquals("Drop", sortedDrop[0].name)
        assertEquals("Flat", sortedDrop[1].name)
        assertEquals("HikeSmall", sortedDrop[2].name)
        assertEquals("HikeBig", sortedDrop[3].name)
    }

    @Test
    fun testPriceSortOrderAlphabetical() {
        val itemZ = MarketCommodityUiModel(name = "টমেটো", category = "cat", defaultUnit = "kg", currentPrice = 50.0)
        val itemA = MarketCommodityUiModel(name = "আলু", category = "cat", defaultUnit = "kg", currentPrice = 30.0)
        val itemP = MarketCommodityUiModel(name = "পেঁয়াজ", category = "cat", defaultUnit = "kg", currentPrice = 70.0)

        val sorted = listOf(itemZ, itemA, itemP).sortedWith(
            compareBy { it.name.lowercase() }
        )
        assertEquals("আলু", sorted[0].name)
        assertEquals("টমেটো", sorted[1].name)
        assertEquals("পেঁয়াজ", sorted[2].name)
    }

    // =========================================================================
    // 3. MOVEMENT FILTERING (All, Up, Down, Stable)
    // =========================================================================

    @Test
    fun testMovementFilterLogic() {
        val upItem = MarketCommodityUiModel(
            name = "Up",
            category = "cat",
            defaultUnit = "kg",
            currentPrice = 120.0,
            priceDiff = 20.0,
            previousPrice = 100.0,
            priceTrend = PriceTrend.UP
        )
        val downItem = MarketCommodityUiModel(
            name = "Down",
            category = "cat",
            defaultUnit = "kg",
            currentPrice = 80.0,
            priceDiff = -20.0,
            previousPrice = 100.0,
            priceTrend = PriceTrend.DOWN
        )
        val stableItem = MarketCommodityUiModel(
            name = "Stable",
            category = "cat",
            defaultUnit = "kg",
            currentPrice = 100.0,
            priceDiff = 0.0,
            previousPrice = 100.0,
            priceTrend = PriceTrend.STABLE
        )

        assertTrue(upItem.isPriceUp)
        assertFalse(upItem.isPriceDown)
        assertFalse(upItem.isPriceStable)

        assertTrue(downItem.isPriceDown)
        assertFalse(downItem.isPriceUp)
        assertFalse(downItem.isPriceStable)

        assertTrue(stableItem.isPriceStable)
        assertFalse(stableItem.isPriceUp)
        assertFalse(stableItem.isPriceDown)
    }

    // =========================================================================
    // 4. PRICE HISTORY RANGE FILTERING (7d, 30d, 90d, All)
    // =========================================================================

    @Test
    fun testPriceHistoryRangeCutoffs() {
        val now = 1700000000000L // arbitrary base time
        val dayMillis = 24 * 60 * 60 * 1000L

        val record2DaysAgo = ProductPriceRecord(
            id = 1,
            productName = "আলু",
            rawQuantity = 2.0,
            rawUnit = "কেজি",
            baseQuantity = 2.0,
            baseUnit = "কেজি",
            unitPrice = 30.0,
            totalAmount = 60.0,
            timestamp = now - (2 * dayMillis)
        )
        val record15DaysAgo = record2DaysAgo.copy(id = 2, timestamp = now - (15 * dayMillis), unitPrice = 28.0)
        val record45DaysAgo = record2DaysAgo.copy(id = 3, timestamp = now - (45 * dayMillis), unitPrice = 25.0)
        val record120DaysAgo = record2DaysAgo.copy(id = 4, timestamp = now - (120 * dayMillis), unitPrice = 22.0)

        val allRecords = listOf(record2DaysAgo, record15DaysAgo, record45DaysAgo, record120DaysAgo)

        // 7 Days
        val cutoff7d = now - (PriceHistoryRange.SEVEN_DAYS.days.toLong() * dayMillis)
        val filtered7d = allRecords.filter { it.timestamp >= cutoff7d }
        assertEquals(1, filtered7d.size)
        assertEquals(1L, filtered7d.first().id)

        // 30 Days
        val cutoff30d = now - (PriceHistoryRange.THIRTY_DAYS.days.toLong() * dayMillis)
        val filtered30d = allRecords.filter { it.timestamp >= cutoff30d }
        assertEquals(2, filtered30d.size)

        // 90 Days
        val cutoff90d = now - (PriceHistoryRange.NINETY_DAYS.days.toLong() * dayMillis)
        val filtered90d = allRecords.filter { it.timestamp >= cutoff90d }
        assertEquals(3, filtered90d.size)

        // All
        val filteredAll = allRecords
        assertEquals(4, filteredAll.size)
    }

    // =========================================================================
    // 5. ITEM DETAIL STATS CALCULATION (Min, Avg, Max)
    // =========================================================================

    @Test
    fun testMinAvgMaxComputation() {
        val records = listOf(
            ProductPriceRecord(id = 1, productName = "তেল", rawQuantity = 1.0, rawUnit = "লিটার", baseQuantity = 1.0, baseUnit = "লিটার", unitPrice = 180.0, totalAmount = 180.0, timestamp = 1L),
            ProductPriceRecord(id = 2, productName = "তেল", rawQuantity = 2.0, rawUnit = "লিটার", baseQuantity = 2.0, baseUnit = "লিটার", unitPrice = 160.0, totalAmount = 320.0, timestamp = 2L),
            ProductPriceRecord(id = 3, productName = "তেল", rawQuantity = 1.0, rawUnit = "লিটার", baseQuantity = 1.0, baseUnit = "লিটার", unitPrice = 190.0, totalAmount = 190.0, timestamp = 3L),
            ProductPriceRecord(id = 4, productName = "তেল", rawQuantity = 1.0, rawUnit = "লিটার", baseQuantity = 1.0, baseUnit = "লিটার", unitPrice = 170.0, totalAmount = 170.0, timestamp = 4L)
        )

        val minPrice = records.minOfOrNull { it.unitPrice } ?: 0.0
        val maxPrice = records.maxOfOrNull { it.unitPrice } ?: 0.0
        val avgPrice = if (records.isNotEmpty()) records.map { it.unitPrice }.average() else 0.0

        assertEquals(160.0, minPrice, 0.001)
        assertEquals(190.0, maxPrice, 0.001)
        assertEquals(175.0, avgPrice, 0.001)
    }

    // =========================================================================
    // 6. DELTA BADGE DISPLAY INVARIANTS (Target E: Delta badge ONLY when change != 0)
    // =========================================================================

    @Test
    fun testDeltaBadgeOnlyWhenChangeNotZero() {
        val unchangedItem = MarketCommodityUiModel(
            name = "আলু",
            category = "কাঁচাবাজার",
            defaultUnit = "কেজি",
            currentPrice = 30.0,
            previousPrice = 30.0,
            priceDiff = 0.0,
            percentDiff = 0.0
        )
        // Per Target E: delta badge MUST NOT appear if priceDiff == 0.0
        val shouldShowDelta = !unchangedItem.isBenchmarkOnly &&
            unchangedItem.previousPrice != null &&
            unchangedItem.priceDiff != 0.0

        assertFalse("Delta badge should not be shown when priceDiff == 0.0", shouldShowDelta)

        val changedItem = unchangedItem.copy(currentPrice = 35.0, priceDiff = 5.0, percentDiff = 16.7, priceTrend = PriceTrend.UP)
        val shouldShowDeltaForChanged = !changedItem.isBenchmarkOnly &&
            changedItem.previousPrice != null &&
            changedItem.priceDiff != 0.0

        assertTrue("Delta badge must be shown when priceDiff != 0.0", shouldShowDeltaForChanged)
    }
}
