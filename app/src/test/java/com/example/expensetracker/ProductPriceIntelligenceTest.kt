package com.example.expensetracker

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    @Test
    fun testProductPriceInsightAvgUnitPriceAndFormatting() {
        val insight = ProductPriceInsight(
            productName = "পেঁয়াজ",
            currentUnitPrice = 120.0,
            previousUnitPrice = 110.0,
            baseUnit = "কেজি",
            priceDiff = 10.0,
            percentDiff = 9.09,
            minUnitPrice = 90.0,
            maxUnitPrice = 130.0,
            avgUnitPrice = 105.0,
            purchaseCount = 5,
            priceTrend = PriceTrend.UP
        )

        assertEquals("৳120/কেজি", insight.formatUnitRate())
        assertEquals("৳105/কেজি", insight.formatAvgRate())
        assertEquals(5, insight.purchaseCount)
        assertEquals(90.0, insight.minUnitPrice, 0.001)
        assertEquals(130.0, insight.maxUnitPrice, 0.001)
    }

    private fun readFile(relativePath: String): String {
        val file = java.io.File("src/main/java/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testDatabaseSchemaAndVersion14Migration() {
        val constants = readFile("com/example/expensetracker/data/local/DatabaseConstants.kt")
        assertTrue(constants.contains("DATABASE_VERSION = 15") || constants.contains("DATABASE_VERSION = 14"))
        assertTrue(constants.contains("TABLE_PRODUCT_PRICE_HISTORY = \"product_price_history\""))
        assertTrue(constants.contains("TABLE_MASTER_PRODUCTS = \"master_products\""))
        assertTrue(constants.contains("COL_PPH_BASE_QUANTITY = \"base_quantity\""))
        assertTrue(constants.contains("COL_PPH_BASE_UNIT = \"base_unit\""))
        assertTrue(constants.contains("COL_PPH_UNIT_PRICE = \"unit_price\""))

        val schema = readFile("com/example/expensetracker/data/local/DatabaseSchema.kt")
        assertTrue(schema.contains("createProductPriceHistory(db)"))
        assertTrue(schema.contains("createMasterProducts(db)"))
        assertTrue(schema.contains("CREATE TABLE \$TABLE_PRODUCT_PRICE_HISTORY"))
        assertTrue(schema.contains("CREATE TABLE \$TABLE_MASTER_PRODUCTS"))

        val migrations = readFile("com/example/expensetracker/data/local/DatabaseMigrations.kt")
        assertTrue(migrations.contains("upgradeToVersion14(db)"))
        assertTrue(migrations.contains("upgradeToVersion15(db, dbHelper)"))
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

    @Test
    fun testFindMatchingInsightExactAndFuzzy() {
        val insightAlu = ProductPriceInsight(
            productName = "আলু",
            currentUnitPrice = 60.0,
            baseUnit = "কেজি"
        )
        val insightGolAlu = ProductPriceInsight(
            productName = "গোল আলু",
            currentUnitPrice = 55.0,
            baseUnit = "কেজি"
        )
        val insightAmra = ProductPriceInsight(
            productName = "আমড়া",
            currentUnitPrice = 80.0,
            baseUnit = "কেজি"
        )
        val insightKachaMorich = ProductPriceInsight(
            productName = "কাঁচা মরিচ",
            currentUnitPrice = 200.0,
            baseUnit = "কেজি"
        )
        val insightEggs = ProductPriceInsight(
            productName = "eggs",
            currentUnitPrice = 12.0,
            baseUnit = "পিস"
        )

        val insights = mapOf(
            "আলু" to insightAlu,
            "গোল আলু" to insightGolAlu,
            "আমড়া" to insightAmra,
            "কাঁচা মরিচ" to insightKachaMorich,
            "eggs" to insightEggs
        )

        // 1. Exact match
        val matchAlu = UnitConverterEngine.findMatchingInsight("আলু", insights)
        assertNotNull(matchAlu)
        assertEquals("আলু", matchAlu?.productName)

        // 2. Case-insensitive exact match
        val matchEggs = UnitConverterEngine.findMatchingInsight("EGGS", insights)
        assertNotNull(matchEggs)
        assertEquals("eggs", matchEggs?.productName)

        // 3. Spacing-invariant match: "কাঁচামরিচ" matches "কাঁচা মরিচ"
        val matchMorich = UnitConverterEngine.findMatchingInsight("কাঁচামরিচ", insights)
        assertNotNull(matchMorich)
        assertEquals("কাঁচা মরিচ", matchMorich?.productName)

        // 4. Token subset match: "দেশি আলু" matches "আলু"
        val matchDeshiAlu = UnitConverterEngine.findMatchingInsight("দেশি আলু", insights)
        assertNotNull(matchDeshiAlu)
        assertEquals("আলু", matchDeshiAlu?.productName)

        // 5. CRITICAL: False substring match prevention!
        // "আম" must NOT match "আমড়া"
        val matchAam = UnitConverterEngine.findMatchingInsight("আম", insights)
        assertNull("Query 'আম' must not match 'আমড়া'", matchAam)

        // "আমড়া" must not match if only "আম" is present
        val insightsWithOnlyAam = mapOf(
            "আম" to ProductPriceInsight(productName = "আম", currentUnitPrice = 120.0, baseUnit = "কেজি")
        )
        val matchAmraOnAam = UnitConverterEngine.findMatchingInsight("আমড়া", insightsWithOnlyAam)
        assertNull("Query 'আমড়া' must not match 'আম'", matchAmraOnAam)

        // 6. Conflicting modifier prevention: "কাঁচা আম" must NOT match "কাঁচা মরিচ"
        val matchKachaAam = UnitConverterEngine.findMatchingInsight("কাঁচা আম", insights)
        assertNull("Query 'কাঁচা আম' must not match 'কাঁচা মরিচ'", matchKachaAam)

        // 7. Blank/Empty queries
        assertNull(UnitConverterEngine.findMatchingInsight("", insights))
        assertNull(UnitConverterEngine.findMatchingInsight("   ", insights))
        assertNull(UnitConverterEngine.findMatchingInsight("আলু", emptyMap()))
    }

    @Test
    fun testMarketItemRowUnpurchasedRightSideAndCleanSubtitle() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // Unpurchased right-side displays estimated cost pill
        assertTrue(
            "MarketItemRow must display estimated cost on right when unpurchased",
            content.contains("UnitConverterEngine.estimateCost") &&
            content.contains("estBadgeText") &&
            content.contains("সম্ভাব্য") &&
            content.contains("Est.")
        )

        // Unpurchased right-side displays last rate text below estimated pill
        assertTrue(
            "MarketItemRow must display last rate text below estimated pill",
            content.contains("lastRateResolved") &&
            content.contains("গত দর:") &&
            content.contains("Last:")
        )

        // Redundant subtitle text in middle column must be eliminated
        assertFalse(
            "MarketItemRow center column should not have redundant subtitleText variable",
            content.contains("val subtitleText =")
        )
    }

    @Test
    fun testMarketPromptAmountDialogSuggestedChip() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        assertTrue(
            "MarketPromptAmountDialog must suggest estimated amount as a quick chip",
            content.contains("estimatedCostSuggestion") &&
            content.contains("UnitConverterEngine.estimateCost") &&
            content.contains("promptAmountText = estAmtStr") &&
            content.contains("সম্ভাব্য দাম")
        )
    }

    @Test
    fun testCallingScreensPassMatchedInsightViaUnitConverterEngine() {
        val screenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        assertTrue(
            "MarketScreen must pass matched insight to MarketItemRow",
            screenContent.contains("priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)")
        )

        val sheetContent = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")
        assertTrue(
            "MarketListSheet must pass matched insight to MarketItemRow",
            sheetContent.contains("priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)")
        )

        val nativeContent = readFile("com/example/expensetracker/ui/dashboard/MarketNativeContent.kt")
        assertTrue(
            "MarketNativeContent must pass matched insight to MarketItemRow",
            nativeContent.contains("priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)")
        )
    }

    @Test
    fun testDistinctCommoditiesFalseMatchPrevention() {
        val insights = mapOf(
            "আলু" to ProductPriceInsight(productName = "আলু", currentUnitPrice = 60.0, baseUnit = "কেজি"),
            "কলা" to ProductPriceInsight(productName = "কলা", currentUnitPrice = 10.0, baseUnit = "পিস"),
            "কুমড়া" to ProductPriceInsight(productName = "কুমড়া", currentUnitPrice = 40.0, baseUnit = "কেজি"),
            "সয়াবিন তেল" to ProductPriceInsight(productName = "সয়াবিন তেল", currentUnitPrice = 180.0, baseUnit = "লিটার")
        )

        // "মিষ্টি আলু" (Sweet Potato) must NOT match "আলু" (Potato)
        val matchMistiAlu = UnitConverterEngine.findMatchingInsight("মিষ্টি আলু", insights)
        assertNull("Query 'মিষ্টি আলু' must not falsely match 'আলু'", matchMistiAlu)

        // "আলু" must NOT match if only "মিষ্টি আলু" is present
        val insightsWithOnlyMistiAlu = mapOf(
            "মিষ্টি আলু" to ProductPriceInsight(productName = "মিষ্টি আলু", currentUnitPrice = 120.0, baseUnit = "কেজি")
        )
        val matchAluOnMistiAlu = UnitConverterEngine.findMatchingInsight("আলু", insightsWithOnlyMistiAlu)
        assertNull("Query 'আলু' must not falsely match 'মিষ্টি আলু'", matchAluOnMistiAlu)

        // "কাঁচা কলা" (Plantain) must NOT match "কলা" (Banana)
        val matchKachaKola = UnitConverterEngine.findMatchingInsight("কাঁচা কলা", insights)
        assertNull("Query 'কাঁচা কলা' must not falsely match 'কলা'", matchKachaKola)

        // "চাল কুমড়া" must NOT match "কুমড়া"
        val matchChalKumra = UnitConverterEngine.findMatchingInsight("চাল কুমড়া", insights)
        assertNull("Query 'চাল কুমড়া' must not falsely match 'কুমড়া'", matchChalKumra)

        // "মিষ্টি কুমড়া" must NOT match "কুমড়া"
        val matchMistiKumra = UnitConverterEngine.findMatchingInsight("মিষ্টি কুমড়া", insights)
        assertNull("Query 'মিষ্টি কুমড়া' must not falsely match 'কুমড়া'", matchMistiKumra)

        // "সরিষার তেল" must NOT match "সয়াবিন তেল"
        val matchShorishaTel = UnitConverterEngine.findMatchingInsight("সরিষার তেল", insights)
        assertNull("Query 'সরিষার তেল' must not falsely match 'সয়াবিন তেল'", matchShorishaTel)
    }

    @Test
    fun testBengaliUnicodeNfcAndChandrabinduMatching() {
        // Test Unicode decomposed (ya + nukta \u09af\u09bc) vs precomposed (\u09df)
        val decomposedPeyaj = "পে\u09AF\u09BCাজ"
        val precomposedPeyaj = "পে\u09DFাজ"
        val insightPeyaj = ProductPriceInsight(
            productName = precomposedPeyaj,
            currentUnitPrice = 80.0,
            baseUnit = "কেজি"
        )
        val insights = mapOf(precomposedPeyaj to insightPeyaj)

        // Decomposed input must match precomposed insight through Unicode NFC normalization
        val matchDecomposed = UnitConverterEngine.findMatchingInsight(decomposedPeyaj, insights)
        assertNotNull("Decomposed Unicode must match precomposed Unicode via NFC", matchDecomposed)
        assertEquals(precomposedPeyaj, matchDecomposed?.productName)

        // Casual spelling without chandrabindu (e.g. "পেয়াজ" vs "পেঁয়াজ")
        val withoutChandrabindu = "পেয়াজ"
        val matchNoCb = UnitConverterEngine.findMatchingInsight(withoutChandrabindu, insights)
        assertNotNull("Query without chandrabindu must match item with chandrabindu", matchNoCb)
        assertEquals(precomposedPeyaj, matchNoCb?.productName)

        // "কাচামরিচ" (without chandrabindu) vs "কাঁচা মরিচ"
        val kachaMorichInsights = mapOf(
            "কাঁচা মরিচ" to ProductPriceInsight(productName = "কাঁচা মরিচ", currentUnitPrice = 200.0, baseUnit = "কেজি")
        )
        val matchNoCbMorich = UnitConverterEngine.findMatchingInsight("কাচামরিচ", kachaMorichInsights)
        assertNotNull("Query 'কাচামরিচ' must match 'কাঁচা মরিচ'", matchNoCbMorich)
        assertEquals("কাঁচা মরিচ", matchNoCbMorich?.productName)
    }

    @Test
    fun testMultiTokenCommodityAndGenericQualifiers() {
        val insights = mapOf(
            "আলু" to ProductPriceInsight(productName = "আলু", currentUnitPrice = 60.0, baseUnit = "কেজি"),
            "ডিম" to ProductPriceInsight(productName = "ডিম", currentUnitPrice = 12.0, baseUnit = "পিস"),
            "সয়াবিন তেল" to ProductPriceInsight(productName = "সয়াবিন তেল", currentUnitPrice = 180.0, baseUnit = "লিটার")
        )

        // Generic qualifiers like "দেশি", "বগুড়ার", "লাল", "ফার্মের" MUST match
        val matchDeshiAlu = UnitConverterEngine.findMatchingInsight("দেশি আলু", insights)
        assertNotNull(matchDeshiAlu)
        assertEquals("আলু", matchDeshiAlu?.productName)

        val matchBoguraAlu = UnitConverterEngine.findMatchingInsight("বগুড়ার আলু", insights)
        assertNotNull(matchBoguraAlu)
        assertEquals("আলু", matchBoguraAlu?.productName)

        val matchLalAlu = UnitConverterEngine.findMatchingInsight("লাল আলু", insights)
        assertNotNull(matchLalAlu)
        assertEquals("আলু", matchLalAlu?.productName)

        val matchFarmDim = UnitConverterEngine.findMatchingInsight("ফার্মের ডিম", insights)
        assertNotNull(matchFarmDim)
        assertEquals("ডিম", matchFarmDim?.productName)

        // Multi-token brand commodity match: "রূপচাঁদা সয়াবিন তেল" matches "সয়াবিন তেল"
        val matchRupchanda = UnitConverterEngine.findMatchingInsight("রূপচাঁদা সয়াবিন তেল", insights)
        assertNotNull(matchRupchanda)
        assertEquals("সয়াবিন তেল", matchRupchanda?.productName)

        // Reverse: query "সয়াবিন তেল" matches history "রূপচাঁদা সয়াবিন তেল"
        val brandInsights = mapOf(
            "রূপচাঁদা সয়াবিন তেল" to ProductPriceInsight(productName = "রূপচাঁদা সয়াবিন তেল", currentUnitPrice = 185.0, baseUnit = "লিটার")
        )
        val matchReverse = UnitConverterEngine.findMatchingInsight("সয়াবিন তেল", brandInsights)
        assertNotNull(matchReverse)
        assertEquals("রূপচাঁদা সয়াবিন তেল", matchReverse?.productName)

        // Parentheses and punctuation stripping: "আলু (দেশি - ২ কেজি)" matches "আলু"
        val matchParen = UnitConverterEngine.findMatchingInsight("আলু (দেশি - ২ কেজি)", insights)
        assertNotNull(matchParen)
        assertEquals("আলু", matchParen?.productName)

        val matchBracket = UnitConverterEngine.findMatchingInsight("ডিম [১২ পিস]", insights)
        assertNotNull(matchBracket)
        assertEquals("ডিম", matchBracket?.productName)
    }

    @Test
    fun testMarketSearchBarSleekProportionsAndDesign() {
        val searchBar = readFile("com/example/expensetracker/ui/components/MarketSearchBar.kt")
        assertTrue("MarketSearchBar must use compact 40.dp height by default", searchBar.contains("height: Dp = 40.dp"))
        assertTrue("MarketSearchBar must use RoundedCornerShape(12.dp)", searchBar.contains("shape: Shape = RoundedCornerShape(12.dp)"))
        assertTrue("MarketSearchBar must use BasicTextField with decorationBox for exact alignment", searchBar.contains("BasicTextField(") && searchBar.contains("decorationBox ="))
        assertTrue("MarketSearchBar must bind actualFocusRequester directly to BasicTextField", searchBar.contains(".focusRequester(actualFocusRequester)"))

        val marketScreen = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        assertTrue("MarketTopBar must pass focusRequester to MarketSearchBar", marketScreen.contains("focusRequester = focusRequester"))
        assertTrue("MarketTopBar must provide onSearch callback to dismiss IME", marketScreen.contains("onSearch = {"))
        assertTrue("MarketTopBar close button must use circular ArrowBack matching header icons", marketScreen.contains("Icons.AutoMirrored.Outlined.ArrowBack"))

        val insightsContent = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertTrue("ProductPriceInsightsContent must accept isSearchActive to avoid duplicate active filter chips", insightsContent.contains("isSearchActive: Boolean = false"))
        assertTrue("ProductPriceInsightsContent must hide filter chip while search bar is actively open", insightsContent.contains("effectiveSearchQuery.isNotBlank() && !isSearchActive"))
    }
}

