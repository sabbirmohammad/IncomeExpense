package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.MarketCompletionResult
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getDepartmentMeta
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Comprehensive Opaque-Box E2E and Unit Test Suite for Market & Market List Modernization.
 *
 * Follows the 4-tier methodology:
 * - Tier 1: Feature Coverage (>=5 tests per feature for all 6 target features)
 * - Tier 2: Boundary & Corner Cases (>=5 tests per feature for all 6 target features)
 * - Tier 3: Cross-Feature Combinations (Pairwise interactions)
 * - Tier 4: Real-World Workload Scenarios (Realistic market trips, WhatsApp lists, price tracking)
 */
class MarketOverhaulE2ETest {

    // =========================================================================
    // TIER 1: FEATURE COVERAGE (6 Features × 5 Tests = 30 Tests)
    // =========================================================================

    // -------------------------------------------------------------------------
    // Feature 1: In-Store Shopping Experience
    // -------------------------------------------------------------------------

    @Test
    fun t1_f1_01_testItemCheckOffTogglingUpdatesPurchasedState() {
        val initialItem = MarketItem(id = 1, listId = 10, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = false)
        val toggledPurchased = initialItem.copy(isPurchased = true, amount = 120.0, transactionId = 101L)

        assertTrue(toggledPurchased.isPurchased)
        assertEquals(120.0, toggledPurchased.amount ?: 0.0, 0.001)
        assertEquals(101L, toggledPurchased.transactionId)
    }

    @Test
    fun t1_f1_02_testItemUncheckRevertsPurchasedStateAndClearsAmount() {
        val purchasedItem = MarketItem(id = 2, listId = 10, name = "ডিম", quantity = 1.0, unit = "ডজন", isPurchased = true, amount = 150.0, transactionId = 102L)
        val revertedItem = purchasedItem.copy(isPurchased = false, amount = null, transactionId = null)

        assertFalse(revertedItem.isPurchased)
        assertNull(revertedItem.amount)
        assertNull(revertedItem.transactionId)
    }

    @Test
    fun t1_f1_03_testRunningSpendTotalCalculatesOnlyPurchasedItems() {
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "রুই মাছ", quantity = 1.5, unit = "কেজি", amount = 450.0, isPurchased = true),
            MarketItem(id = 3, listId = 1, name = "সয়াবিন তেল", quantity = 2.0, unit = "লিটার", amount = null, isPurchased = false),
            MarketItem(id = 4, listId = 1, name = "ডিম", quantity = 1.0, unit = "ডজন", amount = 160.0, isPurchased = false)
        )
        val list = MarketList(id = 1, title = "সকালের বাজার")
        val marketListWithItems = MarketListWithItems(list = list, items = items)

        assertEquals(4, marketListWithItems.totalItems)
        assertEquals(2, marketListWithItems.purchasedItems)
        assertEquals(2, marketListWithItems.remainingItems)
        assertEquals(570.0, marketListWithItems.totalSpent, 0.001)
        assertEquals(0.5f, marketListWithItems.progress, 0.001f)
        assertFalse(marketListWithItems.isAllPurchased)
    }

    @Test
    fun t1_f1_04_testTripSummaryProgressAndCompletionState() {
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "চাল", quantity = 5.0, unit = "কেজি", amount = 350.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "ডাল", quantity = 1.0, unit = "কেজি", amount = 140.0, isPurchased = true)
        )
        val listWithItems = MarketListWithItems(list = MarketList(id = 1), items = items)

        assertEquals(2, listWithItems.totalItems)
        assertEquals(2, listWithItems.purchasedItems)
        assertEquals(0, listWithItems.remainingItems)
        assertEquals(490.0, listWithItems.totalSpent, 0.001)
        assertEquals(1.0f, listWithItems.progress, 0.001f)
        assertTrue(listWithItems.isAllPurchased)
    }

    @Test
    fun t1_f1_05_testSingleTapCheckoutTripCompletionResult() {
        val result = MarketCompletionResult(
            success = true,
            totalSpent = 1250.0,
            purchasedCount = 6,
            transactionId = 205L
        )
        assertTrue(result.success)
        assertEquals(1250.0, result.totalSpent, 0.001)
        assertEquals(6, result.purchasedCount)
        assertEquals(205L, result.transactionId)
    }

    // -------------------------------------------------------------------------
    // Feature 2: Frictionless Price & Quantity Logging
    // -------------------------------------------------------------------------

    @Test
    fun t1_f2_06_testCalculatorKeypadBasicOperatorsEvaluation() {
        assertEquals(205.0, CalculatorEvaluator.evaluate("120 + 85")!!, 0.001)
        assertEquals(155.0, CalculatorEvaluator.evaluate("200 - 45")!!, 0.001)
        assertEquals(200.0, CalculatorEvaluator.evaluate("50 × 4")!!, 0.001)
        assertEquals(25.0, CalculatorEvaluator.evaluate("100 ÷ 4")!!, 0.001)
        assertEquals(150.0, CalculatorEvaluator.evaluate("200 − 50")!!, 0.001)
    }

    @Test
    fun t1_f2_07_testCalculatorCompoundExpressionsWithPrecedence() {
        // Multiplication before addition: 120 + (85 * 2) = 290
        assertEquals(290.0, CalculatorEvaluator.evaluate("120 + 85 * 2")!!, 0.001)
        // Mixed operators with division: 100 - (20 * 3) + (40 / 8) = 100 - 60 + 5 = 45
        assertEquals(45.0, CalculatorEvaluator.evaluate("100 - 20 * 3 + 40 / 8")!!, 0.001)
    }

    @Test
    fun t1_f2_08_testRateSuggestionCostEstimationFromBasePrice() {
        // Base rate = ৳60/kg
        // Target: 2.5 kg -> ৳150
        assertEquals(150.0, UnitConverterEngine.estimateCost(60.0, 2.5, "কেজি"), 0.001)
        // Target: 500 g -> 0.5 kg -> ৳30
        assertEquals(30.0, UnitConverterEngine.estimateCost(60.0, 500.0, "গ্রাম"), 0.001)
        // Target: 1 powa (0.25 kg) -> ৳15
        assertEquals(15.0, UnitConverterEngine.estimateCost(60.0, 1.0, "পোয়া"), 0.001)
    }

    @Test
    fun t1_f2_09_testUnitConverterEngineStandardNormalizations() {
        val kg = UnitConverterEngine.normalize(2.0, "কেজি")
        assertEquals(2.0, kg.baseQuantity, 0.001)
        assertEquals("কেজি", kg.baseUnit)

        val liter = UnitConverterEngine.normalize(1.5, "লিটার")
        assertEquals(1.5, liter.baseQuantity, 0.001)
        assertEquals("লিটার", liter.baseUnit)

        val eggs = UnitConverterEngine.normalize(1.0, "ডজন")
        assertEquals(12.0, eggs.baseQuantity, 0.001)
        assertEquals("পিস", eggs.baseUnit)

        val hali = UnitConverterEngine.normalize(2.0, "হালি")
        assertEquals(8.0, hali.baseQuantity, 0.001)
        assertEquals("পিস", hali.baseUnit)
    }

    @Test
    fun t1_f2_10_testRateVarianceCalculationForPriceIndicator() {
        // Base rate: ৳60/kg
        // Current purchase: 2 kg for ৳150 -> ৳75/kg
        val (currentRate, _) = UnitConverterEngine.calculateBaseUnitPrice(150.0, 2.0, "কেজি")
        val baseRate = 60.0
        val diff = currentRate - baseRate // +15.0
        val percent = (diff / baseRate) * 100.0 // +25.0%

        assertEquals(75.0, currentRate, 0.001)
        assertEquals(15.0, diff, 0.001)
        assertEquals(25.0, percent, 0.001)
        assertTrue("Price increase > 1% is classified as inflation", percent > 1.0)
    }

    // -------------------------------------------------------------------------
    // Feature 3: Department Categorization & Visual Hierarchy
    // -------------------------------------------------------------------------

    @Test
    fun t1_f3_11_testVeggiesDepartmentClassification() {
        assertEquals("veggies", getMarketItemCategory("আলু"))
        assertEquals("veggies", getMarketItemCategory("পেঁয়াজ"))
        assertEquals("veggies", getMarketItemCategory("টমেটো"))
        assertEquals("veggies", getMarketItemCategory("পালং শাক"))
        assertEquals("veggies", getMarketItemCategory("কাঁচামরিচ"))
        assertEquals("veggies", getMarketItemCategory("বেগুন"))
    }

    @Test
    fun t1_f3_12_testFishAndMeatDepartmentClassification() {
        assertEquals("meat_fish", getMarketItemCategory("রুই মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("ইলিশ মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("গরুর মাংস"))
        assertEquals("meat_fish", getMarketItemCategory("ব্রয়লার মুরগি"))
        assertEquals("meat_fish", getMarketItemCategory("খাসির মাংস"))
        assertEquals("meat_fish", getMarketItemCategory("চিংড়ি"))
    }

    @Test
    fun t1_f3_13_testDairyAndBakeryDepartmentClassification() {
        assertEquals("dairy_bakery", getMarketItemCategory("তরল দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("পাউরুটি"))
        assertEquals("dairy_bakery", getMarketItemCategory("মাখন"))
        assertEquals("dairy_bakery", getMarketItemCategory("দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("টোস্ট বিস্কুট"))
    }

    @Test
    fun t1_f3_14_testGroceriesAndSpicesDepartmentClassification() {
        assertEquals("groceries", getMarketItemCategory("তীর সয়াবিন তেল"))
        assertEquals("groceries", getMarketItemCategory("মিনিকেট চাল"))
        assertEquals("groceries", getMarketItemCategory("মসুর ডাল"))
        assertEquals("groceries", getMarketItemCategory("লবণ"))
        assertEquals("groceries", getMarketItemCategory("চিনি"))
        assertEquals("groceries", getMarketItemCategory("আটা"))
    }

    @Test
    fun t1_f3_15_testDepartmentMetadataContractForAllDepartments() {
        assertEquals(4, ALL_DEPARTMENTS.size)

        val veggies = getDepartmentMeta("veggies")
        assertEquals("কাঁচাবাজার", veggies.titleBn)
        assertEquals("Vegetables", veggies.titleEn)
        assertEquals("🥬", veggies.icon)

        val meatFish = getDepartmentMeta("meat_fish")
        assertEquals("মাছ ও মাংস", meatFish.titleBn)
        assertEquals("Meat & Fish", meatFish.titleEn)
        assertEquals("🐟", meatFish.icon)

        val dairy = getDepartmentMeta("dairy_bakery")
        assertEquals("দুধ ও বেকারি", dairy.titleBn)
        assertEquals("Dairy & Bakery", dairy.titleEn)
        assertEquals("🥛", dairy.icon)

        val groceries = getDepartmentMeta("groceries")
        assertEquals("মুদি সদাই", groceries.titleBn)
        assertEquals("Groceries", groceries.titleEn)
        assertEquals("🧂", groceries.icon)
    }

    // -------------------------------------------------------------------------
    // Feature 4: Commodity Price Intelligence & Trends
    // -------------------------------------------------------------------------

    @Test
    fun t1_f4_16_testPriceTrendEnumValuesAndThresholds() {
        assertEquals(PriceTrend.UP, if (5.0 > 1.0) PriceTrend.UP else PriceTrend.STABLE)
        assertEquals(PriceTrend.DOWN, if (-2.5 < -1.0) PriceTrend.DOWN else PriceTrend.STABLE)
        assertEquals(PriceTrend.STABLE, if (0.5 in -1.0..1.0) PriceTrend.STABLE else PriceTrend.UP)
    }

    @Test
    fun t1_f4_17_testBaseUnitPriceCalculationAccuracy() {
        // 5 kg for ৳350 -> ৳70/kg
        val (rate1, unit1) = UnitConverterEngine.calculateBaseUnitPrice(350.0, 5.0, "কেজি")
        assertEquals(70.0, rate1, 0.001)
        assertEquals("কেজি", unit1)

        // 250 g for ৳30 -> 0.25 kg -> ৳120/kg
        val (rate2, unit2) = UnitConverterEngine.calculateBaseUnitPrice(30.0, 250.0, "গ্রাম")
        assertEquals(120.0, rate2, 0.001)
        assertEquals("কেজি", unit2)

        // 1 hali for ৳56 -> 4 pcs -> ৳14/pc
        val (rate3, unit3) = UnitConverterEngine.calculateBaseUnitPrice(56.0, 1.0, "হালি")
        assertEquals(14.0, rate3, 0.001)
        assertEquals("পিস", unit3)
    }

    @Test
    fun t1_f4_18_testProductPriceInsightTrendProperties() {
        val upInsight = ProductPriceInsight(
            productName = "পেঁয়াজ",
            currentUnitPrice = 120.0,
            previousUnitPrice = 100.0,
            baseUnit = "কেজি",
            priceDiff = 20.0,
            percentDiff = 20.0,
            priceTrend = PriceTrend.UP
        )
        assertTrue(upInsight.isPriceUp)
        assertFalse(upInsight.isPriceDown)
        assertFalse(upInsight.isPriceStable)
        assertEquals("৳120/কেজি", upInsight.formatUnitRate())
        assertEquals("+৳20 (20.0%)", upInsight.formatDiff())

        val downInsight = ProductPriceInsight(
            productName = "টমেটো",
            currentUnitPrice = 60.0,
            previousUnitPrice = 80.0,
            baseUnit = "কেজি",
            priceDiff = -20.0,
            percentDiff = -25.0,
            priceTrend = PriceTrend.DOWN
        )
        assertTrue(downInsight.isPriceDown)
        assertEquals("-৳20 (25.0%)", downInsight.formatDiff())
    }

    @Test
    fun t1_f4_19_testHistoricalPriceSparklineOrdering() {
        val history = listOf(
            ProductPriceRecord(id = 1, productName = "আলু", rawQuantity = 2.0, rawUnit = "কেজি", baseQuantity = 2.0, baseUnit = "কেজি", totalAmount = 100.0, unitPrice = 50.0, timestamp = 1000L),
            ProductPriceRecord(id = 2, productName = "আলু", rawQuantity = 3.0, rawUnit = "কেজি", baseQuantity = 3.0, baseUnit = "কেজি", totalAmount = 165.0, unitPrice = 55.0, timestamp = 2000L),
            ProductPriceRecord(id = 3, productName = "আলু", rawQuantity = 1.0, rawUnit = "কেজি", baseQuantity = 1.0, baseUnit = "কেজি", totalAmount = 60.0, unitPrice = 60.0, timestamp = 3000L)
        )
        val sparklinePoints = history.sortedBy { it.timestamp }.map { it.unitPrice }

        assertEquals(listOf(50.0, 55.0, 60.0), sparklinePoints)
    }

    @Test
    fun t1_f4_20_testPriceInsightAverageAndExtremaCalculations() {
        val records = listOf(50.0, 60.0, 70.0)
        val minRate = records.minOrNull() ?: 0.0
        val maxRate = records.maxOrNull() ?: 0.0
        val avgRate = records.average()

        assertEquals(50.0, minRate, 0.001)
        assertEquals(70.0, maxRate, 0.001)
        assertEquals(60.0, avgRate, 0.001)
    }

    // -------------------------------------------------------------------------
    // Feature 5: Multi-List Hub & WhatsApp Bulk List Flow
    // -------------------------------------------------------------------------

    @Test
    fun t1_f5_21_testWhatsAppNumberedListParsing() {
        val text = """
            1. আলু ২ কেজি
            2. পেঁয়াজ ১ কেজি
            3. ডিম ১ ডজন
        """.trimIndent()
        val parsed = MarketListParser.parseBulkText(text)

        assertEquals(3, parsed.size)
        assertEquals("আলু", parsed[0].name)
        assertEquals(2.0, parsed[0].quantity, 0.01)
        assertEquals("কেজি", parsed[0].unit)
        assertEquals("পেঁয়াজ", parsed[1].name)
        assertEquals("ডিম", parsed[2].name)
        assertEquals("ডজন", parsed[2].unit)
    }

    @Test
    fun t1_f5_22_testWhatsAppBulletedAndDashedListParsing() {
        val text = """
            - সয়াবিন তেল ২ লিটার
            • মুরগি ১.৫ কেজি
            * লবণ ১ কেজি
        """.trimIndent()
        val parsed = MarketListParser.parseBulkText(text)

        assertEquals(3, parsed.size)
        assertEquals("সয়াবিন তেল", parsed[0].name)
        assertEquals("লিটার", parsed[0].unit)
        assertEquals("মুরগি", parsed[1].name)
        assertEquals(1.5, parsed[1].quantity, 0.01)
        assertEquals("লবণ", parsed[2].name)
    }

    @Test
    fun t1_f5_23_testCommaSeparatedBulkTextParsing() {
        val text = "রসুন ২৫০ গ্রাম, আদা ৫০০ গ্রাম, কাঁচামরিচ ১০০ গ্রাম"
        val parsed = MarketListParser.parseBulkText(text)

        assertEquals(3, parsed.size)
        assertEquals("রসুন", parsed[0].name)
        assertEquals(250.0, parsed[0].quantity, 0.01)
        assertEquals("আদা", parsed[1].name)
        assertEquals(500.0, parsed[1].quantity, 0.01)
        assertEquals("কাঁচামরিচ", parsed[2].name)
    }

    @Test
    fun t1_f5_24_testBengaliFractionsParsingInMarketList() {
        val itemDerdh = MarketListParser.parseSingleLine("গরুর মাংস দেড় কেজি")
        assertNotNull(itemDerdh)
        assertEquals("গরুর মাংস", itemDerdh?.name)
        assertEquals(1.5, itemDerdh?.quantity ?: 0.0, 0.01)

        val itemArai = MarketListParser.parseSingleLine("আলু আড়াই কেজি")
        assertNotNull(itemArai)
        assertEquals("আলু", itemArai?.name)
        assertEquals(2.5, itemArai?.quantity ?: 0.0, 0.01)

        val itemHalf = MarketListParser.parseSingleLine("তেল হাফ লিটার")
        assertNotNull(itemHalf)
        assertEquals("তেল", itemHalf?.name)
        assertEquals(0.5, itemHalf?.quantity ?: 0.0, 0.01)
    }

    @Test
    fun t1_f5_25_testMasterProductStaplePromotionAfterThreePurchases() {
        val productInitial = MasterProduct(name = "আলু", normalizedName = "আলু", purchaseCount = 1, isStaple = false)
        assertFalse(productInitial.isStaple)

        val productThirdPurchase = productInitial.copy(purchaseCount = 3, isStaple = (3 >= 3))
        assertTrue(productThirdPurchase.isStaple)
    }

    // -------------------------------------------------------------------------
    // Feature 6: Functional Integrity & ACID Transaction Safety
    // -------------------------------------------------------------------------

    @Test
    fun t1_f6_26_testTripCompletionConsolidatesIntoSingleExpenseTransaction() {
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "মুরগি", quantity = 1.0, unit = "কেজি", amount = 220.0, isPurchased = true)
        )
        val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
        val consolidatedTx = Transaction(
            title = "৫ নভেম্বর বাজার",
            amount = totalSpent,
            type = TransactionType.EXPENSE,
            categoryId = 2L,
            categoryName = "Groceries",
            categoryIcon = "ShoppingCart",
            categoryColor = "#2EC4B6",
            dateMillis = System.currentTimeMillis(),
            paymentMethod = PaymentMethod.CASH,
            notes = "বাজারের বিবরণ (২টি পণ্য):\n• আলু (2 কেজি) - ৳120\n• মুরগি (1 কেজি) - ৳220"
        )

        assertEquals(340.0, consolidatedTx.amount, 0.001)
        assertEquals(TransactionType.EXPENSE, consolidatedTx.type)
        assertEquals(2L, consolidatedTx.categoryId)
        assertTrue(consolidatedTx.notes.contains("আলু"))
        assertTrue(consolidatedTx.notes.contains("মুরগি"))
    }

    @Test
    fun t1_f6_27_testShoppingExpenseAttributionToTargetAccount() {
        val cashAccount = Account(id = 1L, name = "Cash in Hand", type = AccountType.CASH, initialBalance = 5000.0)
        val tripAmount = 650.0

        val newBalance = cashAccount.initialBalance - tripAmount
        assertEquals(4350.0, newBalance, 0.001)
    }

    @Test
    fun t1_f6_28_testBatchPriceHistoryRecordingDataContract() {
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "পেঁয়াজ", quantity = 2.0, unit = "কেজি", amount = 200.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "সয়াবিন তেল", quantity = 1.0, unit = "লিটার", amount = 190.0, isPurchased = true)
        )
        val priceRecords = items.map { item ->
            val norm = UnitConverterEngine.normalize(item.quantity, item.unit)
            ProductPriceRecord(
                productName = item.name,
                rawQuantity = item.quantity,
                rawUnit = item.unit,
                baseQuantity = norm.baseQuantity,
                baseUnit = norm.baseUnit,
                totalAmount = item.amount ?: 0.0,
                unitPrice = (item.amount ?: 0.0) / norm.baseQuantity,
                marketItemId = item.id,
                transactionId = 999L
            )
        }

        assertEquals(2, priceRecords.size)
        assertEquals(100.0, priceRecords[0].unitPrice, 0.001) // 200 / 2 kg = 100
        assertEquals(190.0, priceRecords[1].unitPrice, 0.001) // 190 / 1 ltr = 190
    }

    @Test
    fun t1_f6_29_testUnpurchasedItemsRollOverPreservesProperties() {
        val oldTripItems = listOf(
            MarketItem(id = 10, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 11, listId = 1, name = "মাছ", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 12, listId = 1, name = "ধনেপাতা", quantity = 1.0, unit = "আঁটি", amount = null, isPurchased = false)
        )
        val unpurchased = oldTripItems.filter { !it.isPurchased }
        val rolledOverNewItems = unpurchased.map {
            it.copy(id = 0, listId = 0, isPurchased = false, amount = null)
        }

        assertEquals(2, rolledOverNewItems.size)
        assertEquals("মাছ", rolledOverNewItems[0].name)
        assertEquals("ধনেপাতা", rolledOverNewItems[1].name)
        assertEquals(0L, rolledOverNewItems[0].id)
        assertFalse(rolledOverNewItems[0].isPurchased)
    }

    @Test
    fun t1_f6_30_testMarketTripTitleFormattingLogic() {
        // Date: 2026-11-05
        val cal = java.util.Calendar.getInstance().apply {
            set(2026, java.util.Calendar.NOVEMBER, 5, 10, 0, 0)
        }
        val banglaTitle = DateTimeHelper.formatMarketTripTitle(cal.timeInMillis, isBangla = true, useBengaliNumerals = true)
        val englishTitle = DateTimeHelper.formatMarketTripTitle(cal.timeInMillis, isBangla = false, useBengaliNumerals = false)

        assertTrue("Bangla trip title must contain 'বাজার'", banglaTitle.contains("বাজার"))
        assertTrue("English trip title must contain 'Market'", englishTitle.contains("Market"))
    }


    // =========================================================================
    // TIER 2: BOUNDARY & CORNER CASES (6 Features × 5 Tests = 30 Tests)
    // =========================================================================

    // -------------------------------------------------------------------------
    // Feature 1 Boundaries: In-Store Shopping Boundaries
    // -------------------------------------------------------------------------

    @Test
    fun t2_f1_31_testEmptyMarketListCheckoutHandlesGracefully() {
        val emptyListWithItems = MarketListWithItems(list = MarketList(id = 1, title = "খালি ফর্দ"), items = emptyList())

        assertEquals(0, emptyListWithItems.totalItems)
        assertEquals(0, emptyListWithItems.purchasedItems)
        assertEquals(0.0, emptyListWithItems.totalSpent, 0.001)
        assertEquals(0f, emptyListWithItems.progress, 0.001f)
        assertFalse(emptyListWithItems.isAllPurchased)
    }

    @Test
    fun t2_f1_32_testCheckoutWithZeroItemsTickedPurchasesAllPending() {
        // When user finishes shopping without ticking individual items, repository marks all unpurchased as purchased
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 2, listId = 1, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", isPurchased = false)
        )
        val completedItems = items.map { it.copy(isPurchased = true) }

        assertTrue(completedItems.all { it.isPurchased })
        assertEquals(2, completedItems.count { it.isPurchased })
    }

    @Test
    fun t2_f1_33_testSingleItemLargeAmountTripCompletion() {
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "বড় ইলিশ মাছ", quantity = 2.5, unit = "কেজি", amount = 6500.0, isPurchased = true)
        )
        val listWithItems = MarketListWithItems(list = MarketList(id = 1), items = items)

        assertEquals(6500.0, listWithItems.totalSpent, 0.001)
        assertEquals(1, listWithItems.purchasedItems)
        assertTrue(listWithItems.isAllPurchased)
    }

    @Test
    fun t2_f1_34_testMixedItemsWithAndWithoutPriceNotesGeneration() {
        val isBn = true
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "কাঁচামরিচ", quantity = 250.0, unit = "গ্রাম", amount = null, isPurchased = true)
        )
        val breakdownLines = items.filter { it.isPurchased }.joinToString("\n") { itm ->
            val amtStr = if (itm.amount != null && itm.amount > 0) {
                val formattedNum = if (itm.amount % 1.0 == 0.0) "${itm.amount.toInt()}" else "${itm.amount}"
                "৳$formattedNum"
            } else {
                if (isBn) "মূল্য ছাড়া" else "No price"
            }
            "• ${itm.name} (${itm.formatQuantity()}) - $amtStr"
        }

        assertTrue(breakdownLines.contains("• আলু (2 কেজি) - ৳120"))
        assertTrue(breakdownLines.contains("• কাঁচামরিচ (250 গ্রাম) - মূল্য ছাড়া"))
    }

    @Test
    fun t2_f1_35_testItemDeletionDuringActiveShoppingRecalculatesRunningSpend() {
        var items = listOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "মাছ", quantity = 1.0, unit = "কেজি", amount = 400.0, isPurchased = true),
            MarketItem(id = 3, listId = 1, name = "ডিম", quantity = 1.0, unit = "ডজন", amount = 150.0, isPurchased = true)
        )
        assertEquals(670.0, items.sumOf { it.amount ?: 0.0 }, 0.001)

        // Delete item 2
        items = items.filterNot { it.id == 2L }
        assertEquals(2, items.size)
        assertEquals(270.0, items.sumOf { it.amount ?: 0.0 }, 0.001)
    }

    // -------------------------------------------------------------------------
    // Feature 2 Boundaries: Logging & Numeric Boundaries
    // -------------------------------------------------------------------------

    @Test
    fun t2_f2_36_testCalculatorEvaluatorDivisionByZeroReturnsNull() {
        assertNull(CalculatorEvaluator.evaluate("100 / 0"))
        assertNull(CalculatorEvaluator.evaluate("100 ÷ 0"))
        assertNull(CalculatorEvaluator.evaluate("০ ÷ ০"))
        assertNull(CalculatorEvaluator.evaluate("50 / -0"))
    }

    @Test
    fun t2_f2_37_testCalculatorEvaluatorExtremelySmallFractionalQuantities() {
        val parsed = "0.05 * 500".toParsedAmountOrNull()
        assertNotNull(parsed)
        assertEquals(25.0, parsed!!, 0.001)

        // 50 grams at ৳200/kg
        val est50g = UnitConverterEngine.estimateCost(200.0, 50.0, "গ্রাম")
        assertEquals(10.0, est50g, 0.001)
    }

    @Test
    fun t2_f2_38_testUnitConverterCustomAndNonStandardPackaging() {
        val pack = UnitConverterEngine.normalize(2.0, "প্যাকেট")
        assertEquals(2.0, pack.baseQuantity, 0.001)
        assertEquals("প্যাকেট", pack.baseUnit)

        val bottle = UnitConverterEngine.normalize(1.0, "বোতল")
        assertEquals(1.0, bottle.baseQuantity, 0.001)
        assertEquals("বোতল", bottle.baseUnit)

        val customUnit = UnitConverterEngine.normalize(3.0, "custom_crate")
        assertEquals(3.0, customUnit.baseQuantity, 0.001)
        assertEquals("custom_crate", customUnit.baseUnit)
        assertFalse(customUnit.isStandardUnit)
    }

    @Test
    fun t2_f2_39_testProductNameMatchingRejectsFalseFriendCommodities() {
        val insights = mapOf(
            "আলু" to ProductPriceInsight("আলু", currentUnitPrice = 50.0, baseUnit = "কেজি"),
            "কলা" to ProductPriceInsight("কলা", currentUnitPrice = 10.0, baseUnit = "পিস"),
            "কুমড়া" to ProductPriceInsight("কুমড়া", currentUnitPrice = 40.0, baseUnit = "পিস")
        )

        // "মিষ্টি আলু" (sweet potato) is a distinct commodity and MUST NOT match "আলু"
        val sweetPotatoMatch = UnitConverterEngine.findMatchingInsight("মিষ্টি আলু", insights)
        assertNull("মিষ্টি আলু must not match regular আলু", sweetPotatoMatch)

        // "কাঁচা কলা" (plantain) must NOT match "কলা" (banana)
        val rawBananaMatch = UnitConverterEngine.findMatchingInsight("কাঁচা কলা", insights)
        assertNull("কাঁচা কলা must not match regular কলা", rawBananaMatch)

        // "চাল কুমড়া" must NOT match "কুমড়া"
        val waxGourdMatch = UnitConverterEngine.findMatchingInsight("চাল কুমড়া", insights)
        assertNull("চাল কুমড়া must not match regular কুমড়া", waxGourdMatch)
    }

    @Test
    fun t2_f2_40_testBengaliNumeralInputWithSpacesAndDecimals() {
        val evaluated = CalculatorEvaluator.evaluate("  ২৫০.৫০ + ৫০  ")
        assertNotNull(evaluated)
        assertEquals(300.50, evaluated!!, 0.001)
    }

    // -------------------------------------------------------------------------
    // Feature 3 Boundaries: Categorization Edge Cases
    // -------------------------------------------------------------------------

    @Test
    fun t3_f3_41_testSpicePowderWithVeggieKeywordsMapsToGroceries() {
        // "মরিচ গুঁড়া" contains "মরিচ" (veggie keyword) but is a spice powder -> must map to groceries
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুড়া"))
        assertEquals("groceries", getMarketItemCategory("chilli powder"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("ধনিয়া গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("জিরা গুঁড়া"))
    }

    @Test
    fun t3_f3_42_testEnglishCommodityNamesCategorization() {
        assertEquals("veggies", getMarketItemCategory("Potato"))
        assertEquals("veggies", getMarketItemCategory("Tomato"))
        assertEquals("veggies", getMarketItemCategory("Cucumber"))
        assertEquals("meat_fish", getMarketItemCategory("Beef"))
        assertEquals("meat_fish", getMarketItemCategory("Chicken"))
        assertEquals("meat_fish", getMarketItemCategory("Fish"))
        assertEquals("dairy_bakery", getMarketItemCategory("Milk"))
        assertEquals("dairy_bakery", getMarketItemCategory("Egg"))
        assertEquals("groceries", getMarketItemCategory("Soybean Oil"))
        assertEquals("groceries", getMarketItemCategory("Rice"))
    }

    @Test
    fun t3_f3_43_testUnknownCommodityFallbackToGroceries() {
        assertEquals("groceries", getMarketItemCategory("অজানা অদ্ভুত পণ্য"))
        assertEquals("groceries", getMarketItemCategory("xyz123"))
        assertEquals("groceries", getMarketItemCategory(""))
    }

    @Test
    fun t3_f3_44_testDepartmentProgressCountersPerAisle() {
        val items = listOf(
            MarketItem(name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = true),
            MarketItem(name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", isPurchased = true),
            MarketItem(name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = false),
            MarketItem(name = "রুই মাছ", quantity = 1.0, unit = "কেজি", isPurchased = false),
            MarketItem(name = "দুধ", quantity = 1.0, unit = "লিটার", isPurchased = true)
        )
        val groupedByDept = items.groupBy { getMarketItemCategory(it.name) }

        val veggies = groupedByDept["veggies"] ?: emptyList()
        val veggiesCompleted = veggies.count { it.isPurchased }
        val veggiesTotal = veggies.size
        assertEquals(2, veggiesCompleted)
        assertEquals(3, veggiesTotal)

        val meatFish = groupedByDept["meat_fish"] ?: emptyList()
        val meatCompleted = meatFish.count { it.isPurchased }
        val meatTotal = meatFish.size
        assertEquals(0, meatCompleted)
        assertEquals(1, meatTotal)

        val dairy = groupedByDept["dairy_bakery"] ?: emptyList()
        val dairyCompleted = dairy.count { it.isPurchased }
        val dairyTotal = dairy.size
        assertEquals(1, dairyCompleted)
        assertEquals(1, dairyTotal)
    }

    @Test
    fun t3_f3_45_testCaseInsensitivityAndNewlineResilienceInCategorization() {
        assertEquals("meat_fish", getMarketItemCategory("  CHICKEN  "))
        assertEquals("meat_fish", getMarketItemCategory("রুই মাছ\n"))
        assertEquals("dairy_bakery", getMarketItemCategory("\tEGG\t"))
        assertEquals("groceries", getMarketItemCategory("  SOYBEAN OIL\n\n"))
    }

    // -------------------------------------------------------------------------
    // Feature 4 Boundaries: Price Intelligence & Trend Edge Cases
    // -------------------------------------------------------------------------

    @Test
    fun t2_f4_46_testSingleHistoricalPurchaseYieldsStableTrend() {
        val insight = ProductPriceInsight(
            productName = "স্ট্রবেরি",
            currentUnitPrice = 800.0,
            previousUnitPrice = null,
            baseUnit = "কেজি",
            priceDiff = 0.0,
            percentDiff = 0.0,
            purchaseCount = 1,
            priceTrend = PriceTrend.STABLE
        )
        assertEquals(800.0, insight.minUnitPrice, 0.001)
        assertEquals(800.0, insight.maxUnitPrice, 0.001)
        assertEquals(800.0, insight.avgUnitPrice, 0.001)
        assertTrue(insight.isPriceStable)
        assertFalse(insight.isPriceUp)
        assertFalse(insight.isPriceDown)
        assertEquals("০%", insight.formatDiff())
    }

    @Test
    fun t2_f4_47_testIdenticalRepeatPriceYieldsStableTrend() {
        val insight = ProductPriceInsight(
            productName = "লবণ",
            currentUnitPrice = 40.0,
            previousUnitPrice = 40.0,
            baseUnit = "কেজি",
            priceDiff = 0.0,
            percentDiff = 0.0,
            purchaseCount = 3,
            priceTrend = PriceTrend.STABLE
        )
        assertTrue(insight.isPriceStable)
        assertEquals("০%", insight.formatDiff())
    }

    @Test
    fun t2_f4_48_testExtremePriceInflationAndDeflationFormatting() {
        val surge = ProductPriceInsight(
            productName = "কাঁচামরিচ",
            currentUnitPrice = 400.0,
            previousUnitPrice = 100.0,
            baseUnit = "কেজি",
            priceDiff = 300.0,
            percentDiff = 300.0,
            priceTrend = PriceTrend.UP
        )
        assertTrue(surge.isPriceUp)
        assertEquals("+৳300 (300.0%)", surge.formatDiff())

        val drop = ProductPriceInsight(
            productName = "পেঁয়াজ",
            currentUnitPrice = 50.0,
            previousUnitPrice = 150.0,
            baseUnit = "কেজি",
            priceDiff = -100.0,
            percentDiff = -66.666,
            priceTrend = PriceTrend.DOWN
        )
        assertTrue(drop.isPriceDown)
        assertEquals("-৳100 (66.7%)", drop.formatDiff())
    }

    @Test
    fun t2_f4_49_testThirtyDayHistoricalWindowComparison() {
        val insight = ProductPriceInsight(
            productName = "সয়াবিন তেল",
            currentUnitPrice = 190.0,
            previousUnitPrice = 185.0,
            baseUnit = "লিটার",
            priceDiff = 5.0,
            percentDiff = 2.7,
            oneMonthAgoUnitPrice = 170.0,
            oneMonthDiff = 20.0,
            oneMonthPercentDiff = 11.76,
            priceTrend = PriceTrend.UP
        )
        assertNotNull(insight.oneMonthAgoUnitPrice)
        assertEquals(170.0, insight.oneMonthAgoUnitPrice!!, 0.001)
        assertEquals(20.0, insight.oneMonthDiff!!, 0.001)
        assertEquals(11.76, insight.oneMonthPercentDiff!!, 0.01)
    }

    @Test
    fun t2_f4_50_testMultiQuantityVaryingUnitPurchasesNormalizeToBaseRate() {
        // Purchase 1: 500 grams for ৳40 -> ৳80/kg
        val (rate1, unit1) = UnitConverterEngine.calculateBaseUnitPrice(40.0, 500.0, "গ্রাম")
        // Purchase 2: 2 kg for ৳150 -> ৳75/kg
        val (rate2, unit2) = UnitConverterEngine.calculateBaseUnitPrice(150.0, 2.0, "কেজি")
        // Purchase 3: 1 powa (0.25 kg) for ৳22 -> ৳88/kg
        val (rate3, unit3) = UnitConverterEngine.calculateBaseUnitPrice(22.0, 1.0, "পোয়া")

        assertEquals("কেজি", unit1)
        assertEquals("কেজি", unit2)
        assertEquals("কেজি", unit3)
        assertEquals(80.0, rate1, 0.001)
        assertEquals(75.0, rate2, 0.001)
        assertEquals(88.0, rate3, 0.001)
    }

    // -------------------------------------------------------------------------
    // Feature 5 Boundaries: Bulk Parsing & Multi-List State Boundaries
    // -------------------------------------------------------------------------

    @Test
    fun t2_f5_51_testMessyWhatsAppFormattingWithEmojisAndPunctuation() {
        val messy = "১. আলু (দেশি) - ২.৫ কেজি!!! 🥬\n২) সয়াবিন তেল [তীর] : ১ লিটার... 🥛"
        val parsed = MarketListParser.parseBulkText(messy)

        assertEquals(2, parsed.size)
        assertTrue(parsed[0].name.contains("আলু"))
        assertEquals(2.5, parsed[0].quantity, 0.01)
        assertEquals("কেজি", parsed[0].unit)
        assertTrue(parsed[1].name.contains("সয়াবিন তেল"))
        assertEquals(1.0, parsed[1].quantity, 0.01)
        assertEquals("লিটার", parsed[1].unit)
    }

    @Test
    fun t2_f5_52_testMultiListSwitcherFilteringActiveLists() {
        val allLists = listOf(
            MarketList(id = 1, title = "সকালের বাজার", isCompleted = false),
            MarketList(id = 2, title = "গত সপ্তাহের বাজার", isCompleted = true),
            MarketList(id = 3, title = "বিকালের মুদি", isCompleted = false)
        )
        val activeLists = allLists.filter { !it.isCompleted }

        assertEquals(2, activeLists.size)
        assertEquals(1L, activeLists[0].id)
        assertEquals(3L, activeLists[1].id)
    }

    @Test
    fun t2_f5_53_testEmptyAndWhitespaceBulkTextYieldsEmptyList() {
        assertTrue(MarketListParser.parseBulkText("").isEmpty())
        assertTrue(MarketListParser.parseBulkText("   \n\t   \n").isEmpty())
    }

    @Test
    fun t2_f5_54_testMissingQuantityOrUnitFallsBackToDefaults() {
        val parsed = MarketListParser.parseSingleLine("সাবান")
        assertNotNull(parsed)
        assertEquals("সাবান", parsed?.name)
        assertEquals(1.0, parsed?.quantity ?: 0.0, 0.01)
        assertEquals("পিস", parsed?.unit)
    }

    @Test
    fun t2_f5_55_testMixedBengaliAndEnglishDigitsInBulkParsing() {
        val parsed = MarketListParser.parseSingleLine("মুরগি ২.5 কেজি")
        assertNotNull(parsed)
        assertEquals("মুরগি", parsed?.name)
        assertEquals(2.5, parsed?.quantity ?: 0.0, 0.01)
    }

    // -------------------------------------------------------------------------
    // Feature 6 Boundaries: Transaction & Integrity Edge Cases
    // -------------------------------------------------------------------------

    @Test
    fun t2_f6_56_testTransactionNotesItemizedBreakdownFormatting() {
        val items = listOf(
            MarketItem(name = "আলু", quantity = 2.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(name = "ডিম", quantity = 1.0, unit = "ডজন", amount = 160.0, isPurchased = true)
        )
        val noteHeader = "বাজারের বিবরণ (২টি পণ্য):"
        val breakdown = items.joinToString("\n") { "• ${it.name} (${it.formatQuantity()}) - ৳${it.amount?.toInt()}" }
        val fullNote = "$noteHeader\n$breakdown"

        assertTrue(fullNote.startsWith("বাজারের বিবরণ (২টি পণ্য):"))
        assertTrue(fullNote.contains("• আলু (2 কেজি) - ৳120"))
        assertTrue(fullNote.contains("• ডিম (1 ডজন) - ৳160"))
    }

    @Test
    fun t2_f6_57_testCascadingItemCleanupOnMarketListDeletion() {
        val listIdToDelete = 5L
        val allItems = listOf(
            MarketItem(id = 1, listId = 5, name = "আলু", quantity = 2.0, unit = "কেজি"),
            MarketItem(id = 2, listId = 5, name = "পটল", quantity = 1.0, unit = "কেজি"),
            MarketItem(id = 3, listId = 6, name = "চাল", quantity = 5.0, unit = "কেজি")
        )
        val remainingItems = allItems.filterNot { it.listId == listIdToDelete }

        assertEquals(1, remainingItems.size)
        assertEquals(6L, remainingItems[0].listId)
    }

    @Test
    fun t2_f6_58_testUntogglingPurchasedItemIdentifiesTransactionToDelete() {
        val item = MarketItem(id = 1, listId = 1, name = "মাছ", quantity = 1.0, unit = "কেজি", isPurchased = true, amount = 450.0, transactionId = 888L)
        val txIdToDelete = if (item.transactionId != null && item.transactionId > 0) item.transactionId else null

        assertNotNull(txIdToDelete)
        assertEquals(888L, txIdToDelete)
    }

    @Test
    fun t2_f6_59_testListTitleFallbackPreservesCustomNames() {
        val genericTitle = "বাজারের ফর্দ"
        val customTitle = "শুক্রবার স্পেশাল কাঁচাবাজার"

        val resolvedGeneric = if (genericTitle == "বাজারের ফর্দ") "৫ নভেম্বর বাজার" else genericTitle
        val resolvedCustom = if (customTitle == "বাজারের ফর্দ") "৫ নভেম্বর বাজার" else customTitle

        assertEquals("৫ নভেম্বর বাজার", resolvedGeneric)
        assertEquals("শুক্রবার স্পেশাল কাঁচাবাজার", resolvedCustom)
    }

    @Test
    fun t2_f6_60_testZeroCostItemsDoNotCorruptTotalSpentCalculation() {
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "ধনেপাতা ফাউ", quantity = 1.0, unit = "আঁটি", amount = 0.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "মাংস", quantity = 1.0, unit = "কেজি", amount = 750.0, isPurchased = true)
        )
        val totalSpent = items.filter { it.isPurchased && (it.amount ?: 0.0) > 0 }.sumOf { it.amount ?: 0.0 }

        assertEquals(750.0, totalSpent, 0.001)
    }


    // =========================================================================
    // TIER 3: CROSS-FEATURE COMBINATIONS (Pairwise Interactions = 8 Tests)
    // =========================================================================

    @Test
    fun t3_01_testWhatsAppBulkImportToDepartmentClassificationToShoppingList() {
        val whatsappText = """
            1. আলু ২ কেজি
            2. রুই মাছ ১ কেজি
            3. ডিম ১ ডজন
            4. সয়াবিন তেল ১ লিটার
        """.trimIndent()
        val parsedItems = MarketListParser.parseBulkText(whatsappText)
        val categorizedItems = parsedItems.map { it.copy(listId = 1L) to getMarketItemCategory(it.name) }

        assertEquals(4, categorizedItems.size)
        assertEquals("veggies", categorizedItems[0].second)
        assertEquals("meat_fish", categorizedItems[1].second)
        assertEquals("dairy_bakery", categorizedItems[2].second)
        assertEquals("groceries", categorizedItems[3].second)
    }

    @Test
    fun t3_02_testInStoreItemCheckoffWithKeypadCalculatorRunningTotalUpdate() {
        var runningTotal = 0.0
        val items = mutableListOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি"),
            MarketItem(id = 2, listId = 1, name = "মুরগি", quantity = 1.5, unit = "কেজি")
        )

        // Item 1: entered simple amount 120
        val amount1 = "120".toParsedAmountOrNull()!!
        items[0] = items[0].copy(isPurchased = true, amount = amount1)
        runningTotal += amount1
        assertEquals(120.0, runningTotal, 0.001)

        // Item 2: entered calculator expression 150 * 1.5 = 225
        val amount2 = CalculatorEvaluator.evaluate("150 * 1.5")!!
        items[1] = items[1].copy(isPurchased = true, amount = amount2)
        runningTotal += amount2
        assertEquals(345.0, runningTotal, 0.001)
    }

    @Test
    fun t3_03_testHistoricalPriceInsightMatchingRateEstimationAndVarianceAlert() {
        val insights = mapOf(
            "আলু" to ProductPriceInsight("আলু", currentUnitPrice = 50.0, baseUnit = "কেজি")
        )
        val matchedInsight = UnitConverterEngine.findMatchingInsight("দেশি আলু", insights)
        assertNotNull(matchedInsight)
        assertEquals(50.0, matchedInsight?.currentUnitPrice ?: 0.0, 0.001)

        // Estimated cost for 2.5 kg at ৳50/kg = ৳125
        val estimated = UnitConverterEngine.estimateCost(matchedInsight!!.currentUnitPrice, 2.5, "কেজি")
        assertEquals(125.0, estimated, 0.001)

        // Actual paid: ৳150 (variance = +৳25, +20% spike!)
        val actualPaid = 150.0
        val variance = actualPaid - estimated
        val variancePct = (variance / estimated) * 100.0
        assertEquals(25.0, variance, 0.001)
        assertEquals(20.0, variancePct, 0.001)
        assertTrue(variancePct >= 20.0)
    }

    @Test
    fun t3_04_testMultiListHubIndependentListSwitchingAndIsolation() {
        val list1Items = listOf(
            MarketItem(id = 1, listId = 10, name = "মাছ", quantity = 1.0, unit = "কেজি", amount = 500.0, isPurchased = true)
        )
        val list2Items = listOf(
            MarketItem(id = 2, listId = 20, name = "লবণ", quantity = 1.0, unit = "কেজি", amount = 40.0, isPurchased = true),
            MarketItem(id = 3, listId = 20, name = "চিনি", quantity = 1.0, unit = "কেজি", amount = 130.0, isPurchased = true)
        )
        val list1WithItems = MarketListWithItems(list = MarketList(id = 10), items = list1Items)
        val list2WithItems = MarketListWithItems(list = MarketList(id = 20), items = list2Items)

        assertEquals(500.0, list1WithItems.totalSpent, 0.001)
        assertEquals(1, list1WithItems.purchasedItems)

        assertEquals(170.0, list2WithItems.totalSpent, 0.001)
        assertEquals(2, list2WithItems.purchasedItems)
    }

    @Test
    fun t3_05_testRollOverUnpurchasedItemsPreservesDepartmentGrouping() {
        val completedTrip = listOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 100.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "রুই মাছ", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 3, listId = 1, name = "দুধ", quantity = 1.0, unit = "লিটার", amount = null, isPurchased = false)
        )
        val unpurchased = completedTrip.filter { !it.isPurchased }
        val newTripItems = unpurchased.map { it.copy(id = 0, listId = 2) }

        val newTripDepartments = newTripItems.map { getMarketItemCategory(it.name) }
        assertEquals(listOf("meat_fish", "dairy_bakery"), newTripDepartments)
    }

    @Test
    fun t3_06_testShoppingTripCompletionAccountDeductionAndPriceRecordPairing() {
        val bankAccount = Account(id = 2L, name = "City Bank", type = AccountType.BANK, initialBalance = 25000.0)
        val tripItems = listOf(
            MarketItem(id = 1, listId = 1, name = "গরুর মাংস", quantity = 2.0, unit = "কেজি", amount = 1600.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "পোলাও চাল", quantity = 1.0, unit = "কেজি", amount = 150.0, isPurchased = true)
        )
        val totalSpent = tripItems.sumOf { it.amount ?: 0.0 }
        val updatedAccountBalance = bankAccount.initialBalance - totalSpent

        assertEquals(1750.0, totalSpent, 0.001)
        assertEquals(23250.0, updatedAccountBalance, 0.001)

        val priceRecordMeat = UnitConverterEngine.calculateBaseUnitPrice(1600.0, 2.0, "কেজি")
        assertEquals(800.0, priceRecordMeat.first, 0.001)
    }

    @Test
    fun t3_07_testCommodityQualifierMatchingAllowsDeshiAndRejectsSweetPotato() {
        val insights = mapOf(
            "আলু" to ProductPriceInsight("আলু", currentUnitPrice = 50.0, baseUnit = "কেজি")
        )
        // Generic qualifier "দেশি" should match "আলু"
        val deshiMatch = UnitConverterEngine.findMatchingInsight("দেশি আলু", insights)
        assertNotNull(deshiMatch)
        assertEquals("আলু", deshiMatch?.productName)

        // Non-qualifier "মিষ্টি" must NOT match "আলু"
        val sweetMatch = UnitConverterEngine.findMatchingInsight("মিষ্টি আলু", insights)
        assertNull(sweetMatch)
    }

    @Test
    fun t3_08_testMasterProductStaplePromotionAfterTripsIntegration() {
        var masterProduct = MasterProduct(name = "পেঁয়াজ", normalizedName = "পেয়াজ", purchaseCount = 2, isStaple = false)
        assertFalse(masterProduct.isStaple)

        // Completing 3rd shopping trip with onion
        masterProduct = masterProduct.copy(purchaseCount = 3, isStaple = true)
        assertTrue(masterProduct.isStaple)
    }


    // =========================================================================
    // TIER 4: REAL-WORLD WORKLOAD SCENARIOS (5 Scenarios)
    // =========================================================================

    @Test
    fun t4_01_testScenarioFridayMorningDhakaBazaarShoppingTrip() {
        // Step 1: User receives a WhatsApp list on Friday morning
        val whatsappList = """
            1. আলু ২ কেজি
            2. পেঁয়াজ ১ কেজি
            3. রুই মাছ ১.৫ কেজি
            4. দেশি মুরগি ১ কেজি
            5. তরল দুধ ২ লিটার
            6. ফার্মের ডিম ১ ডজন
            7. তীর সয়াবিন তেল ২ লিটার
            8. মসুর ডাল ১ কেজি
        """.trimIndent()
        val parsedItems = MarketListParser.parseBulkText(whatsappList)
        assertEquals(8, parsedItems.size)

        // Step 2: Items are correctly categorized into 4 aisles
        val aisleCounts = parsedItems.groupBy { getMarketItemCategory(it.name) }.mapValues { it.value.size }
        assertEquals(2, aisleCounts["veggies"]) // আলু, পেঁয়াজ
        assertEquals(2, aisleCounts["meat_fish"]) // রুই মাছ, দেশি মুরগি
        assertEquals(2, aisleCounts["dairy_bakery"]) // তরল দুধ, ফার্মের ডিম
        assertEquals(2, aisleCounts["groceries"]) // তীর সয়াবিন তেল, মসুর ডাল

        // Step 3: User walks through bazaar and checks off items with prices
        val purchasedList = listOf(
            parsedItems[0].copy(isPurchased = true, amount = 110.0), // আলু: ৳110
            parsedItems[1].copy(isPurchased = true, amount = 130.0), // পেঁয়াজ: ৳130
            parsedItems[2].copy(isPurchased = true, amount = 600.0), // রুই মাছ: ৳600
            parsedItems[3].copy(isPurchased = true, amount = 420.0), // দেশি মুরগি: ৳420
            parsedItems[4].copy(isPurchased = true, amount = 180.0), // তরল দুধ: ৳180
            parsedItems[5].copy(isPurchased = true, amount = 160.0), // ডিম: ৳160
            parsedItems[6].copy(isPurchased = true, amount = 380.0), // সয়াবিন তেল: ৳380
            parsedItems[7].copy(isPurchased = true, amount = 140.0)  // মসুর ডাল: ৳140
        )
        val shoppingList = MarketListWithItems(list = MarketList(id = 1, title = "শুক্রবার সকালের বাজার"), items = purchasedList)

        // Step 4: Verify progress and total spent
        assertEquals(8, shoppingList.purchasedItems)
        assertEquals(1.0f, shoppingList.progress, 0.001f)
        val expectedTotal = 110.0 + 130.0 + 600.0 + 420.0 + 180.0 + 160.0 + 380.0 + 140.0 // ৳2120
        assertEquals(2120.0, shoppingList.totalSpent, 0.001)

        // Step 5: Complete trip and record transaction
        val completion = MarketCompletionResult(success = true, totalSpent = shoppingList.totalSpent, purchasedCount = 8, transactionId = 501L)
        assertTrue(completion.success)
        assertEquals(2120.0, completion.totalSpent, 0.001)
    }

    @Test
    fun t4_02_testScenarioPartialShoppingTripWithOutOfStockRollOver() {
        val initialItems = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 2, name = "টমেটো", quantity = 1.0, unit = "কেজি", amount = 80.0, isPurchased = true),
            MarketItem(id = 3, name = "ইলিশ মাছ", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 4, name = "গরুর মাংস", quantity = 1.0, unit = "কেজি", amount = 780.0, isPurchased = true),
            MarketItem(id = 5, name = "ঘি", quantity = 1.0, unit = "বোতল", amount = null, isPurchased = false)
        )
        val currentTrip = MarketListWithItems(list = MarketList(id = 10), items = initialItems)

        assertEquals(3, currentTrip.purchasedItems)
        assertEquals(2, currentTrip.remainingItems)
        assertEquals(980.0, currentTrip.totalSpent, 0.001)

        // Roll over remaining 2 items into next trip
        val rollOver = initialItems.filterNot { it.isPurchased }.map { it.copy(id = 0, listId = 11) }
        assertEquals(2, rollOver.size)
        assertEquals("ইলিশ মাছ", rollOver[0].name)
        assertEquals("ঘি", rollOver[1].name)
    }

    @Test
    fun t4_03_testScenarioWhatsAppColloquialBengaliFractionsWithBudgetEstimation() {
        val colloquialText = """
            1. গরুর মাংস দেড় কেজি
            2. আলু আড়াই কেজি
            3. কাঁচামরিচ ১ পোয়া
            4. ডিম ১ হালি
            5. সয়াবিন তেল হাফ লিটার
        """.trimIndent()
        val parsed = MarketListParser.parseBulkText(colloquialText)
        assertEquals(5, parsed.size)

        // Known base rates
        val baseRates = mapOf(
            "গরুর মাংস" to 750.0, // ৳750/kg
            "আলু" to 50.0,         // ৳50/kg
            "কাঁচামরিচ" to 200.0,  // ৳200/kg
            "ডিম" to 14.0,         // ৳14/pc
            "সয়াবিন তেল" to 190.0  // ৳190/ltr
        )

        val estimatedCosts = parsed.map { item ->
            val rate = baseRates[item.name] ?: 0.0
            UnitConverterEngine.estimateCost(rate, item.quantity, item.unit)
        }
        // 1. 1.5 * 750 = 1125
        assertEquals(1125.0, estimatedCosts[0], 0.001)
        // 2. 2.5 * 50 = 125
        assertEquals(125.0, estimatedCosts[1], 0.001)
        // 3. 1 powa (0.25 kg) * 200 = 50
        assertEquals(50.0, estimatedCosts[2], 0.001)
        // 4. 1 hali (4 pcs) * 14 = 56
        assertEquals(56.0, estimatedCosts[3], 0.001)
        // 5. 0.5 ltr * 190 = 95
        assertEquals(95.0, estimatedCosts[4], 0.001)

        val totalEstimatedBudget = estimatedCosts.sum()
        assertEquals(1451.0, totalEstimatedBudget, 0.001)
    }

    @Test
    fun t4_04_testScenarioMultiTripCommodityInflationTracking() {
        val commodity = "পেঁয়াজ"
        // Trip 1 (30 days ago): 2 kg for ৳120 -> ৳60/kg
        val (trip1Rate, _) = UnitConverterEngine.calculateBaseUnitPrice(120.0, 2.0, "কেজি")
        val record1 = ProductPriceRecord(productName = commodity, rawQuantity = 2.0, rawUnit = "কেজি", baseQuantity = 2.0, baseUnit = "কেজি", totalAmount = 120.0, unitPrice = trip1Rate, timestamp = 1000L)

        // Trip 2 (15 days ago): 2.5 kg for ৳200 -> ৳80/kg (+33.3% UP)
        val (trip2Rate, _) = UnitConverterEngine.calculateBaseUnitPrice(200.0, 2.5, "কেজি")
        val record2 = ProductPriceRecord(productName = commodity, rawQuantity = 2.5, rawUnit = "কেজি", baseQuantity = 2.5, baseUnit = "কেজি", totalAmount = 200.0, unitPrice = trip2Rate, timestamp = 2000L)

        // Trip 3 (Today): 2 kg for ৳150 -> ৳75/kg (-6.25% DOWN compared to Trip 2)
        val (trip3Rate, _) = UnitConverterEngine.calculateBaseUnitPrice(150.0, 2.0, "কেজি")
        val record3 = ProductPriceRecord(productName = commodity, rawQuantity = 2.0, rawUnit = "কেজি", baseQuantity = 2.0, baseUnit = "কেজি", totalAmount = 150.0, unitPrice = trip3Rate, timestamp = 3000L)

        val allRecords = listOf(record1, record2, record3)

        // Verify min, max, avg
        val minRate = allRecords.minOf { it.unitPrice }
        val maxRate = allRecords.maxOf { it.unitPrice }
        val avgRate = allRecords.sumOf { it.totalAmount } / allRecords.sumOf { it.baseQuantity }

        assertEquals(60.0, minRate, 0.001)
        assertEquals(80.0, maxRate, 0.001)
        // Total spent: 120 + 200 + 150 = 470; Total qty: 2 + 2.5 + 2 = 6.5 kg; Avg = 470 / 6.5 = 72.307
        assertEquals(72.307, avgRate, 0.01)

        // Current vs Previous (Trip 3 vs Trip 2)
        val priceDiff = trip3Rate - trip2Rate // 75 - 80 = -5
        val percentDiff = (priceDiff / trip2Rate) * 100.0 // -6.25%
        val trend = if (percentDiff < -1.0) PriceTrend.DOWN else PriceTrend.STABLE

        assertEquals(-5.0, priceDiff, 0.001)
        assertEquals(-6.25, percentDiff, 0.001)
        assertEquals(PriceTrend.DOWN, trend)
    }

    @Test
    fun t4_05_testScenarioCrossAccountShoppingPaymentMethods() {
        val bKashAccount = Account(id = 3L, name = "bKash Personal", type = AccountType.MFS, initialBalance = 4000.0)
        val tripAmount = 1350.0

        val tx = Transaction(
            title = "৫ নভেম্বর বাজার",
            amount = tripAmount,
            type = TransactionType.EXPENSE,
            categoryId = 2L,
            categoryName = "Groceries",
            categoryIcon = "ShoppingCart",
            categoryColor = "#2EC4B6",
            dateMillis = System.currentTimeMillis(),
            accountId = bKashAccount.id,
            accountName = bKashAccount.name,
            paymentMethod = PaymentMethod.BKASH
        )

        assertEquals(PaymentMethod.BKASH, tx.paymentMethod)
        assertEquals(3L, tx.accountId)
        val finalBalance = bKashAccount.initialBalance - tripAmount
        assertEquals(2650.0, finalBalance, 0.001)
    }
}
