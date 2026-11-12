package com.example.expensetracker

import com.example.expensetracker.common.UnitConverterEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Phase 5 Unit Verification Test Suite:
 * Verifies Transactions & Market redesign per UX_FLOWS sections 4 & 5 and Screenshot Patch C5 & C6:
 * - Transactions: large title header, search toggle, overflow menu, profile avatar removed
 * - Single Filters button -> FilterSheet + active removable chips under header
 * - Day-grouped surface with dividers without heavy outlines + day net total
 * - TransactionSwipeRow: category icon, title, subtitle (category only if different), right-aligned amount with tnum
 * - TransactionDetailSheet: full details + Delete (with undo) & Edit actions
 * - Market: segmented pills (Lists · Prices · New), 50dp finish button with running total, commodity deduplication
 */
class Phase5TransactionsAndMarketV7Test {

    private fun findSourceFile(relativePath: String): File {
        val cleanPath = relativePath.removePrefix("app/")
        val direct = File(cleanPath)
        if (direct.exists()) return direct
        val appPrefixed = File("app/$cleanPath")
        if (appPrefixed.exists()) return appPrefixed
        val dotPrefixed = File("../$cleanPath")
        if (dotPrefixed.exists()) return dotPrefixed
        return direct
    }

    @Test
    fun testTransactionsTopBarSpecifications() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionsTopBar.kt")
        assertTrue("TransactionsTopBar.kt must exist", file.exists())
        val content = file.readText()

        // 1. Must include search toggle icon
        assertTrue("TransactionsTopBar must support onSearchClick", content.contains("onSearchClick"))
        assertTrue("TransactionsTopBar must use minimumInteractiveComponentSize()", content.contains("minimumInteractiveComponentSize()"))

        // 2. Must include overflow menu with export and share
        assertTrue("TransactionsTopBar must have overflow menu", content.contains("MoreVert") && content.contains("DropdownMenu"))
        assertTrue("TransactionsTopBar must support export statement in overflow", content.contains("exportStatement") && content.contains("onExportClick"))
        assertTrue("TransactionsTopBar must support share in overflow", content.contains("shareImage") && content.contains("onShareClick"))

        // 3. Profile avatar must be REMOVED from transactions header per C5
        assertFalse("TransactionsTopBar must NOT render ProfileAvatarButton per C5", content.contains("ProfileAvatarButton("))
    }

    @Test
    fun testTransactionsFilterBarAndFilterSheet() {
        val filterBarFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionsFilterBar.kt")
        assertTrue("TransactionsFilterBar.kt must exist", filterBarFile.exists())
        val filterBarContent = filterBarFile.readText()

        // 1. Single Filters button with badge and sheet opener
        assertTrue("FilterBar must support onOpenFilterSheet", filterBarContent.contains("onOpenFilterSheet"))
        assertTrue("FilterBar must display filter button with badge", filterBarContent.contains("FilterList") && filterBarContent.contains("activeFilterCount"))

        // 2. Removable chips under the header
        assertTrue("FilterBar must define RemovableFilterChip", filterBarContent.contains("RemovableFilterChip"))

        // 3. FilterSheet specification
        val sheetFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionFilterSheet.kt")
        assertTrue("TransactionFilterSheet.kt must exist", sheetFile.exists())
        val sheetContent = sheetFile.readText()

        assertTrue("FilterSheet must support TransactionType selection", sheetContent.contains("onTypeSelected") && sheetContent.contains("allTypes"))
        assertTrue("FilterSheet must support TimeFilter selection", sheetContent.contains("onTimeFilterSelected"))
        assertTrue("FilterSheet must support Account selection", sheetContent.contains("onAccountSelected"))
        assertTrue("FilterSheet must support Category selection", sheetContent.contains("onCategorySelected"))
        assertTrue("FilterSheet must support clear all / reset", sheetContent.contains("onResetAll"))
    }

    @Test
    fun testTransactionSwipeRowAndDetailSheet() {
        val rowFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionSwipeRow.kt")
        assertTrue("TransactionSwipeRow.kt must exist", rowFile.exists())
        val rowContent = rowFile.readText()

        // 1. Subtitle = category only if different from title, + time per C5
        assertTrue(
            "TransactionSwipeRow must omit redundant category when matching title",
            rowContent.contains("equals(categoryLocalized, ignoreCase = true)")
        )

        // 2. Account pill shown only when multiple accounts exist
        assertTrue(
            "TransactionSwipeRow must support conditional account pill",
            rowContent.contains("showAccountPill")
        )

        // 3. Tap opens detail sheet / row click
        assertTrue(
            "TransactionSwipeRow must support onRowClick",
            rowContent.contains("onRowClick")
        )

        // 4. DetailSheet specification
        val detailSheetFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionDetailSheet.kt")
        assertTrue("TransactionDetailSheet.kt must exist", detailSheetFile.exists())
        val detailContent = detailSheetFile.readText()

        assertTrue("DetailSheet must use tabular numbers (tnum)", detailContent.contains("\"tnum\""))
        assertTrue("DetailSheet must support Edit action", detailContent.contains("onEdit") && detailContent.contains("editTransaction"))
        assertTrue("DetailSheet must support Delete action", detailContent.contains("onDelete") && detailContent.contains("Destructive"))
        assertTrue("DetailSheet must display metadata rows", detailContent.contains("TransactionDetailInfoRow"))
    }

    @Test
    fun testTransactionDayGroupHeaderNetTotalTabularFigures() {
        val headerFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionDateGroupHeader.kt")
        assertTrue("TransactionDateGroupHeader.kt must exist", headerFile.exists())
        val content = headerFile.readText()

        assertTrue(
            "TransactionDateGroupHeader must format net total with tabular numbers (tnum)",
            content.contains("\"tnum\"")
        )
    }

    @Test
    fun testMarketScreenSegmentedPillsShortLabels() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/market/MarketScreen.kt")
        assertTrue("MarketScreen.kt must exist", file.exists())
        val content = file.readText()

        // Short labels per C6: Lists / ফর্দ, Prices / দরদাম, New / নতুন
        assertTrue("Market tabs must have short Lists label", content.contains("\"ফর্দ\"") && content.contains("\"Lists\""))
        assertTrue("Market tabs must have short Prices label", content.contains("\"দরদাম\"") && content.contains("\"Prices\""))
        assertTrue("Market tabs must have short New label", content.contains("\"নতুন\"") && content.contains("\"New\""))
    }

    @Test
    fun testMarketFinishButtonRunningTotal() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/MarketFinishButton.kt")
        assertTrue("MarketFinishButton.kt must exist", file.exists())
        val content = file.readText()

        assertTrue("MarketFinishButton must declare height(50.dp)", content.contains("height(50.dp)"))
        assertTrue("MarketFinishButton must support totalSpent parameter", content.contains("totalSpent"))
        assertTrue("MarketFinishButton must support isAllPurchased parameter", content.contains("isAllPurchased"))
        assertTrue("MarketFinishButton must use single Check icon", content.contains("Icons.Filled.Check"))
    }

    @Test
    fun testCommodityDeduplicationNormalization() {
        // Test C6 deduplication requirement:
        // "normalize commodity names (trim, collapse spaces, NFC-normalize Bengali, ignore ZWJ/ZWNJ)
        // so 'কাঁচা মরিচ' and 'কাঁচামরিচ' merge"
        val variantWithSpace = "কাঁচা মরিচ"
        val variantWithoutSpace = "কাঁচামরিচ"
        val variantWithZWJ = "কাঁচা\u200Dমরিচ"
        val variantWithExtraSpaces = "  কাঁচা   মরিচ  "

        val key1 = UnitConverterEngine.canonicalCommodityKey(variantWithSpace)
        val key2 = UnitConverterEngine.canonicalCommodityKey(variantWithoutSpace)
        val key3 = UnitConverterEngine.canonicalCommodityKey(variantWithZWJ)
        val key4 = UnitConverterEngine.canonicalCommodityKey(variantWithExtraSpaces)

        assertEquals("কাঁচামরিচ variants must produce identical canonical keys", key1, key2)
        assertEquals("ZWJ variant must produce identical canonical key", key1, key3)
        assertEquals("Spaced variant must produce identical canonical key", key1, key4)
    }

    @Test
    fun testCommodityNeutralItemAvatar() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertTrue("ProductPriceInsightsSheet.kt must exist", file.exists())
        val content = file.readText()

        // Neutral item avatar (first letter on tinted circle per C6)
        assertTrue(
            "ProductPriceInsightsSheet must render neutral item avatar circle",
            content.contains("commodity.name.trim().take(1).uppercase()") && content.contains("accentContainer")
        )
    }
}
