package com.example.expensetracker

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductPriceIntelligenceTest {

    @Test
    fun testUnitConverterWeightNormalization() {
        // 5 kg potato -> baseQty 5.0, baseUnit "কেজি"
        val n5kg = UnitConverterEngine.normalize(5.0, "কেজি")
        assertEquals(5.0, n5kg.baseQuantity, 0.001)
        assertEquals("কেজি", n5kg.baseUnit)

        // 500 grams potato -> baseQty 0.5, baseUnit "কেজি"
        val n500g = UnitConverterEngine.normalize(500.0, "গ্রাম")
        assertEquals(0.5, n500g.baseQuantity, 0.001)
        assertEquals("কেজি", n500g.baseUnit)

        // 1 powa (পোয়া) -> baseQty 0.25, baseUnit "কেজি"
        val n1powa = UnitConverterEngine.normalize(1.0, "পোয়া")
        assertEquals(0.25, n1powa.baseQuantity, 0.001)
        assertEquals("কেজি", n1powa.baseUnit)

        // 1 palla (পাল্লা) -> baseQty 5.0, baseUnit "কেজি"
        val n1palla = UnitConverterEngine.normalize(1.0, "পাল্লা")
        assertEquals(5.0, n1palla.baseQuantity, 0.001)
        assertEquals("কেজি", n1palla.baseUnit)
    }

    @Test
    fun testUnitConverterCountAndVolumeNormalization() {
        // 1 hali eggs -> baseQty 4.0, baseUnit "পিস"
        val n1hali = UnitConverterEngine.normalize(1.0, "হালি")
        assertEquals(4.0, n1hali.baseQuantity, 0.001)
        assertEquals("পিস", n1hali.baseUnit)

        // 1 dozen eggs -> baseQty 12.0, baseUnit "পিস"
        val n1doz = UnitConverterEngine.normalize(1.0, "ডজন")
        assertEquals(12.0, n1doz.baseQuantity, 0.001)
        assertEquals("পিস", n1doz.baseUnit)

        // 500 ml oil -> baseQty 0.5, baseUnit "লিটার"
        val n500ml = UnitConverterEngine.normalize(500.0, "মিলি")
        assertEquals(0.5, n500ml.baseQuantity, 0.001)
        assertEquals("লিটার", n500ml.baseUnit)
    }

    @Test
    fun testCalculateBaseUnitPriceAndComparison() {
        // 5 kg potato for 300 tk -> 60 tk/kg
        val (rate5kg, unit5kg) = UnitConverterEngine.calculateBaseUnitPrice(300.0, 5.0, "কেজি")
        assertEquals(60.0, rate5kg, 0.001)
        assertEquals("কেজি", unit5kg)

        // 500 grams potato for 35 tk -> 70 tk/kg
        val (rate500g, unit500g) = UnitConverterEngine.calculateBaseUnitPrice(35.0, 500.0, "গ্রাম")
        assertEquals(70.0, rate500g, 0.001)
        assertEquals("কেজি", unit500g)

        // Difference: 70 - 60 = +10 tk/kg (+16.67%)
        val diff = rate500g - rate5kg
        assertEquals(10.0, diff, 0.001)
        val percent = (diff / rate5kg) * 100.0
        assertEquals(16.666, percent, 0.01)
    }

    @Test
    fun testCostEstimationForMarketList() {
        // Base rate = 60 tk/kg
        // Target: 2.5 kg potato -> 150 tk
        val est2_5kg = UnitConverterEngine.estimateCost(60.0, 2.5, "কেজি")
        assertEquals(150.0, est2_5kg, 0.001)

        // Target: 500 g potato -> 30 tk
        val est500g = UnitConverterEngine.estimateCost(60.0, 500.0, "গ্রাম")
        assertEquals(30.0, est500g, 0.001)

        // Base rate = 12 tk/piece (eggs)
        // Target: 1 hali eggs -> 48 tk
        val est1hali = UnitConverterEngine.estimateCost(12.0, 1.0, "হালি")
        assertEquals(48.0, est1hali, 0.001)

        // Target: 1 dozen eggs -> 144 tk
        val est1doz = UnitConverterEngine.estimateCost(12.0, 1.0, "ডজন")
        assertEquals(144.0, est1doz, 0.001)
    }

    @Test
    fun testProductNameNormalization() {
        assertEquals("আলু", UnitConverterEngine.normalizeProductName("আলু"))
        assertEquals("আলু", UnitConverterEngine.normalizeProductName("  আলু   "))
        assertEquals("গোল আলু", UnitConverterEngine.normalizeProductName("গোল আলু (দেশি)"))
        assertEquals("আলু", UnitConverterEngine.normalizeProductName("আলু-১"))
    }

    @Test
    fun testProductPriceInsightModelLogic() {
        val insight = ProductPriceInsight(
            productName = "আলু",
            currentUnitPrice = 60.0,
            previousUnitPrice = 50.0,
            baseUnit = "কেজি",
            priceDiff = 10.0,
            percentDiff = 20.0,
            priceTrend = PriceTrend.UP
        )

        assertTrue(insight.isPriceUp)
        assertEquals("৳60/কেজি", insight.formatUnitRate())
        assertTrue(insight.formatDiff().contains("+৳10"))
        assertTrue(insight.formatDiff().contains("20.0%"))
    }

    private fun readFile(relativePath: String): String {
        val file = java.io.File("src/main/java/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testDatabaseSchemaAndVersion14Migration() {
        val constants = readFile("com/example/expensetracker/data/local/DatabaseConstants.kt")
        assertTrue(constants.contains("DATABASE_VERSION = 14"))
        assertTrue(constants.contains("TABLE_PRODUCT_PRICE_HISTORY = \"product_price_history\""))
        assertTrue(constants.contains("COL_PPH_BASE_QUANTITY = \"base_quantity\""))
        assertTrue(constants.contains("COL_PPH_BASE_UNIT = \"base_unit\""))
        assertTrue(constants.contains("COL_PPH_UNIT_PRICE = \"unit_price\""))

        val schema = readFile("com/example/expensetracker/data/local/DatabaseSchema.kt")
        assertTrue(schema.contains("createProductPriceHistory(db)"))
        assertTrue(schema.contains("CREATE TABLE \$TABLE_PRODUCT_PRICE_HISTORY"))

        val migrations = readFile("com/example/expensetracker/data/local/DatabaseMigrations.kt")
        assertTrue(migrations.contains("upgradeToVersion14(db)"))
    }

    @Test
    fun testExpenseDatabaseHelperAndRepositoryWiring() {
        val helper = readFile("com/example/expensetracker/data/local/ExpenseDatabaseHelper.kt")
        assertTrue(helper.contains("fun recordProductPrice("))
        assertTrue(helper.contains("fun getProductPriceInsight("))
        assertTrue(helper.contains("fun getAllProductPriceInsights()"))
        assertTrue(helper.contains("fun estimateProductCost("))

        val repo = readFile("com/example/expensetracker/data/repository/ExpenseRepository.kt")
        assertTrue(repo.contains("val productPriceInsights: StateFlow<Map<String, com.example.expensetracker.data.model.ProductPriceInsight>>"))
        assertTrue(repo.contains("refreshProductPrices()"))
    }

    @Test
    fun testMarketUiIntegration() {
        val promptDialog = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")
        assertTrue(promptDialog.contains("calculateBaseUnitPrice"))
        assertTrue(promptDialog.contains("priceInsight: ProductPriceInsight?"))

        val itemRow = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")
        assertTrue(itemRow.contains("priceInsight: ProductPriceInsight?"))

        val addNewItemRow = readFile("com/example/expensetracker/ui/dashboard/MarketAddNewItemRow.kt")
        assertTrue(addNewItemRow.contains("productPriceInsights: Map<String, ProductPriceInsight>"))
        assertTrue(addNewItemRow.contains("estimateCost"))

        val insightsSheet = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertTrue(insightsSheet.contains("ProductPriceInsightsSheet"))
        assertTrue(insightsSheet.contains("PriceTrend.UP"))
        assertTrue(insightsSheet.contains("PriceTrend.DOWN"))
    }
}
