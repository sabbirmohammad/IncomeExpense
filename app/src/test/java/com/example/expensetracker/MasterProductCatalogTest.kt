package com.example.expensetracker

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.local.*
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class MasterProductCatalogTest {

    private fun readFile(relativePath: String): String {
        val file = File("src/main/java/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testMasterProductCalculations() {
        // Test newly registered product
        val newProduct = MasterProduct(
            id = 1L,
            name = "স্ট্রবেরি",
            normalizedName = "স্ট্রবেরি",
            category = "veggies",
            defaultUnit = "কেজি",
            latestUnitPrice = 800.0,
            previousUnitPrice = null,
            purchaseCount = 1,
            isStaple = false
        )
        assertTrue(newProduct.isNewProduct)
        assertFalse(newProduct.isStaple)
        assertNull(newProduct.priceDiff)
        assertNull(newProduct.percentDiff)
        assertFalse(newProduct.isPriceSpike)

        // Test repeat product with price increase of 25% (Spike!)
        val existingProductSpike = MasterProduct(
            id = 2L,
            name = "পেঁয়াজ",
            normalizedName = "পেয়াজ",
            category = "veggies",
            defaultUnit = "কেজি",
            latestUnitPrice = 125.0,
            previousUnitPrice = 100.0,
            purchaseCount = 4,
            isStaple = true
        )
        assertFalse(existingProductSpike.isNewProduct)
        assertTrue(existingProductSpike.isStaple)
        assertEquals(25.0, existingProductSpike.priceDiff!!, 0.001)
        assertEquals(25.0, existingProductSpike.percentDiff!!, 0.001)
        assertTrue(existingProductSpike.isPriceSpike)

        // Test repeat product with slight price decrease
        val existingProductDrop = MasterProduct(
            id = 3L,
            name = "আলু",
            normalizedName = "আলু",
            category = "veggies",
            defaultUnit = "কেজি",
            latestUnitPrice = 50.0,
            previousUnitPrice = 60.0,
            purchaseCount = 5,
            isStaple = true
        )
        assertEquals(-10.0, existingProductDrop.priceDiff!!, 0.001)
        assertTrue(existingProductDrop.percentDiff!! < 0)
        assertFalse(existingProductDrop.isPriceSpike)
    }

    @Test
    fun testDepartmentClassification() {
        assertEquals("veggies", getMarketItemCategory("দেশি আলু"))
        assertEquals("veggies", getMarketItemCategory("কাঁচামরিচ"))
        assertEquals("veggies", getMarketItemCategory("রসুন"))
        assertEquals("groceries", getMarketItemCategory("তীর সয়াবিন তেল"))
        assertEquals("groceries", getMarketItemCategory("নাজিরশাইল চাল"))
        assertEquals("groceries", getMarketItemCategory("লবণ"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))
        assertEquals("meat_fish", getMarketItemCategory("রুই মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("দেশি মুরগি"))
        assertEquals("meat_fish", getMarketItemCategory("গরুর মাংস"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("মিল্কভিটা দুধ"))
    }

    @Test
    fun testUnitNormalizationAndRateCalculation() {
        // 250 gm for 50 tk -> base rate = 200 tk/kg
        val (rate1, unit1) = UnitConverterEngine.calculateBaseUnitPrice(50.0, 250.0, "গ্রাম")
        assertEquals(200.0, rate1, 0.001)
        assertEquals("কেজি", unit1)

        // 1 hali (4 pcs) for 60 tk -> base rate = 15 tk/pc
        val (rate2, unit2) = UnitConverterEngine.calculateBaseUnitPrice(60.0, 1.0, "হালি")
        assertEquals(15.0, rate2, 0.001)
        assertEquals("পিস", unit2)

        // 5 kg for 350 tk -> base rate = 70 tk/kg
        val (rate3, unit3) = UnitConverterEngine.calculateBaseUnitPrice(350.0, 5.0, "কেজি")
        assertEquals(70.0, rate3, 0.001)
        assertEquals("কেজি", unit3)
    }

    @Test
    fun testSchemaAndDatabaseConstants() {
        val constants = readFile("com/example/expensetracker/data/local/DatabaseConstants.kt")
        assertTrue(constants.contains("DATABASE_VERSION = 15"))
        assertTrue(constants.contains("TABLE_MASTER_PRODUCTS = \"master_products\""))
        assertTrue(constants.contains("COL_MP_NORMALIZED_NAME = \"normalized_name\""))
        assertTrue(constants.contains("COL_MP_IS_STAPLE = \"is_staple\""))
        assertTrue(constants.contains("COL_MP_PURCHASE_COUNT = \"purchase_count\""))

        val helper = readFile("com/example/expensetracker/data/local/ExpenseDatabaseHelper.kt")
        assertTrue(helper.contains("masterProductHelper"))
        assertTrue(helper.contains("getAllMasterProducts"))
        assertTrue(helper.contains("getStapleProducts"))

        val backup = readFile("com/example/expensetracker/data/local/DatabaseBackupHelper.kt")
        assertTrue(backup.contains("masterProducts = masterProducts"))
        assertTrue(backup.contains("productPriceHistory = allPriceHistory"))

        val restore = readFile("com/example/expensetracker/data/local/DatabaseRestoreHelper.kt")
        assertTrue(restore.contains("restorePriceHistoryAndMasterProducts"))
    }

    @Test
    fun testAbsenceOfSparkleIconsInMarketUi() {
        // Strict user guideline: Ensure no sparkle emojis or AutoAwesome AI icons are used in Market UI
        val addNewItemRow = readFile("com/example/expensetracker/ui/dashboard/MarketAddNewItemRow.kt")
        assertFalse(addNewItemRow.contains("AutoAwesome"))
        assertFalse(addNewItemRow.contains("✨"))

        val priceInsights = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertFalse(priceInsights.contains("AutoAwesome"))
        assertFalse(priceInsights.contains("✨"))

        val marketScreen = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        assertFalse(marketScreen.contains("AutoAwesome"))
        assertFalse(marketScreen.contains("✨"))
    }
}
