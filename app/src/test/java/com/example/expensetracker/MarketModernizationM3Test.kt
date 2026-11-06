package com.example.expensetracker

import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Unit and structural verification tests for Milestone M3:
 * 1. Bogra district classifier edge cases and spice powder disambiguation.
 * 2. MarketParsedTagChip component structure, living ledger tokens, and 48dp touch target.
 * 3. CreateMarketListSection notepad styling, bulk tag inspector, 48dp targets, and test invariants.
 * 4. MarketPromptAmountDialog living ledger tokens, 48dp operator/quick chips, unified dual-rate strip, >=12sp typography, and invariants.
 * 5. MarketTripCompletionSheet thermal receipt styling, perforated divider, monospace ledger, ink stamp, and 48dp targets.
 * 6. Zero AI sparkles across all M3 components.
 */
class MarketModernizationM3Test {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testClassifierBograEdgeCasesAndDisambiguation() {
        // 1. Bogra district items: Bogra curd must classify as dairy, Bogra potato as veggies
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়া দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("মিষ্টি দই"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার আলু"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার লাল আলু"))

        // 2. Standard Spices with powder (গুঁড়া / গুড়া / powder) must classify as groceries
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুড়া"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("জিরার গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("চিলি পাউডার"))
        assertEquals("groceries", getMarketItemCategory("বগুড়ার মরিচ গুড়া"))

        // 3. Dairy powders must correctly classify as dairy_bakery, not groceries
        assertEquals("dairy_bakery", getMarketItemCategory("গুঁড়া দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("milk powder"))
        assertEquals("dairy_bakery", getMarketItemCategory("তরল দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))

        // 4. Standard veggies, fish/meat, and groceries
        assertEquals("veggies", getMarketItemCategory("আলু"))
        assertEquals("veggies", getMarketItemCategory("পেঁয়াজ"))
        assertEquals("veggies", getMarketItemCategory("কাঁচা মরিচ"))
        assertEquals("meat_fish", getMarketItemCategory("রুই মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("মুরগির মাংস"))
        assertEquals("groceries", getMarketItemCategory("সয়াবিন তেল"))
        assertEquals("groceries", getMarketItemCategory("মসুর ডাল"))
    }

    @Test
    fun testMarketParsedTagChipStructureAndTokens() {
        val content = readFile("com/example/expensetracker/ui/market/MarketParsedTagChip.kt")

        // Living Ledger tokens
        assertTrue("MarketParsedTagChip must use appColors.cardBackground", content.contains("appColors.cardBackground"))
        assertTrue("MarketParsedTagChip must use HairlineInkStroke", content.contains("HairlineInkStroke"))
        assertTrue("MarketParsedTagChip must use appColors.divider", content.contains("appColors.divider"))

        // Department icon & format quantity
        assertTrue("MarketParsedTagChip must use getDepartmentMeta", content.contains("getDepartmentMeta"))
        assertTrue("MarketParsedTagChip must use formatQuantity", content.contains("item.formatQuantity()"))

        // 48dp minimum touch target
        assertTrue("MarketParsedTagChip must enforce minimumInteractiveComponentSize", content.contains("minimumInteractiveComponentSize()"))

        // Zero AI sparkles
        assertFalse("MarketParsedTagChip must not contain AutoAwesome", content.contains("AutoAwesome"))
        assertFalse("MarketParsedTagChip must not contain sparkle emoji", content.contains("✨"))
    }

    @Test
    fun testCreateMarketListSectionLivingLedgerAndInvariants() {
        val content = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")

        // 1. Living Ledger tokens
        assertTrue("CreateMarketListSection must use appColors.cardBackground", content.contains("appColors.cardBackground"))
        assertTrue("CreateMarketListSection must use HairlineInkStroke", content.contains("HairlineInkStroke"))
        assertTrue("CreateMarketListSection must use appColors.divider", content.contains("appColors.divider"))

        // 2. Interactive Bulk Tag Inspector
        assertTrue("CreateMarketListSection must integrate MarketParsedTagChip", content.contains("MarketParsedTagChip"))
        assertTrue("CreateMarketListSection must use FlowRow for tags", content.contains("FlowRow"))

        // 3. 48dp touch targets
        assertTrue("CreateMarketListSection must enforce minimumInteractiveComponentSize", content.contains("minimumInteractiveComponentSize()"))
        assertTrue("Quick staples must have 48dp minHeight", content.contains("defaultMinSize(minHeight = 48.dp)"))

        // 4. Typography >= 12sp constraint
        assertFalse("CreateMarketListSection must not have 9.5sp", content.contains("9.5.sp"))
        assertFalse("CreateMarketListSection must not have 10sp", content.contains("10.sp"))
        assertFalse("CreateMarketListSection must not have 10.5sp", content.contains("10.5.sp"))
        assertFalse("CreateMarketListSection must not have 11sp", content.contains("11.sp"))
        assertFalse("CreateMarketListSection must not have 11.5sp", content.contains("11.5.sp"))

        // 5. Structural test invariants
        assertTrue(
            "Must define CreateMarketListStep enum with TITLE, ITEMS",
            content.contains("enum class CreateMarketListStep") &&
            content.contains("TITLE") &&
            content.contains("ITEMS")
        )
        assertTrue(
            "Must offer quick title chips (সকালের কাঁচাবাজার, বিকালের মুদি)",
            content.contains("সকালের কাঁচাবাজার") && content.contains("বিকালের মুদি")
        )
        assertTrue(
            "Must support unified omnibox with bulk parser",
            content.contains("MarketListParser.parseBulkText") &&
            content.contains("addManualItem")
        )
        assertTrue(
            "Must support manual item addition with quick staples",
            content.contains("quickStaples") && content.contains("addManualItem")
        )
        assertTrue(
            "Must provide quick quantity selector chips with ১ কেজি",
            content.contains("quickQuantities") && content.contains("১ কেজি")
        )
        assertTrue(
            "Must append new items in chat-style order",
            content.contains("manualItems.value = (manualItems.value + itemsToAdd).toMutableList()")
        )
        assertTrue(
            "Step 2 header must display clean item counter badge",
            content.contains("Live Item Counter Badge") &&
            content.contains("টি পণ্য যুক্ত")
        )
        val addedItemsIndex = content.indexOf("Added Items Section")
        val quickStaplesIndex = content.indexOf("Quick Staples Pills")
        assertTrue(
            "Added Items section must appear before Quick Staples pills in manual entry",
            addedItemsIndex in 1 until quickStaplesIndex
        )
        assertTrue(
            "Added items must display serial number badge",
            content.contains("Serial number badge") &&
            content.contains("convertToBengaliDigits((idx + 1).toString())")
        )
        assertTrue(
            "Must track dismissible clipboard banner",
            content.contains("isClipboardDismissed")
        )

        // 6. Zero AI sparkles
        assertFalse("CreateMarketListSection must not contain AutoAwesome", content.contains("AutoAwesome"))
        assertFalse("CreateMarketListSection must not contain sparkle emoji", content.contains("✨"))
    }

    @Test
    fun testMarketPromptAmountDialogLivingLedgerAndInvariants() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        // 1. Living Ledger tokens
        assertTrue("MarketPromptAmountDialog must use appColors.cardBackground", content.contains("appColors.cardBackground"))
        assertTrue("MarketPromptAmountDialog must use HairlineInkStroke", content.contains("HairlineInkStroke"))
        assertTrue("MarketPromptAmountDialog must use appColors.divider", content.contains("appColors.divider"))

        // 2. 48dp minimum touch targets
        assertTrue("MarketPromptAmountDialog must enforce minimumInteractiveComponentSize", content.contains("minimumInteractiveComponentSize()"))
        assertTrue("Operator chips must have minHeight 48dp", content.contains("defaultMinSize(minHeight = 48.dp)"))

        // 3. Tabular numbers and typography >= 12sp constraint
        assertTrue("Must use tabular numbers", content.contains("\"tnum\""))
        assertFalse("Must not have 10sp text", content.contains("10.sp"))
        assertFalse("Must not have 11sp text", content.contains("11.sp"))
        assertFalse("Must not have 11.5sp text", content.contains("11.5.sp"))

        // 4. Structural test invariants
        assertTrue(
            "Must use FocusRequester for auto-focusing keyboard",
            content.contains("FocusRequester()") && content.contains("focusRequester.requestFocus()")
        )
        assertTrue(
            "Must provide quick increment chips (+50, +100, +200, +500)",
            content.contains("listOf(50, 100, 200, 500)")
        )
        assertTrue(
            "Must have Done action and submitAmount",
            content.contains("imeAction = ImeAction.Done") && content.contains("KeyboardActions(onDone = { submitAmount() })")
        )
        assertTrue(
            "Must allow marking without price",
            content.contains("onConfirm(0.0)")
        )
        assertTrue(
            "Must evaluate math expressions using CalculatorEvaluator",
            content.contains("CalculatorEvaluator.evaluate(promptAmountText)")
        )
        assertTrue(
            "Must show live expression preview chip",
            content.contains("evaluatedAmount") || content.contains("isExpression")
        )
        assertTrue(
            "Must offer math operator quick buttons (×, +, ÷, −)",
            content.contains("\"×\"") && content.contains("\"+\"") && content.contains("\"÷\"") && content.contains("\"−\"")
        )
        assertTrue(
            "Must accept lastRate parameter",
            content.contains("lastRate: Pair<Double, String>? = null")
        )
        assertTrue(
            "Must alert on price difference",
            content.contains("percentDiff") && content.contains("diffRate")
        )
        assertTrue(
            "Must use calculateBaseUnitPrice",
            content.contains("calculateBaseUnitPrice")
        )
        assertTrue(
            "Must accept priceInsight",
            content.contains("priceInsight: ProductPriceInsight?")
        )
        assertTrue(
            "Must suggest estimated amount as quick chip",
            content.contains("estimatedCostSuggestion") &&
            content.contains("UnitConverterEngine.estimateCost") &&
            content.contains("promptAmountText = estAmtStr") &&
            content.contains("সম্ভাব্য দাম")
        )

        // 5. Zero AI sparkles
        assertFalse("MarketPromptAmountDialog must not contain AutoAwesome", content.contains("AutoAwesome"))
        assertFalse("MarketPromptAmountDialog must not contain sparkle emoji", content.contains("✨"))
    }

    @Test
    fun testMarketTripCompletionSheetThermalReceiptAndInvariants() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")

        // 1. Thermal receipt styling & Living Ledger tokens
        assertTrue("MarketTripCompletionSheet must use appColors.cardBackground", content.contains("appColors.cardBackground"))
        assertTrue("MarketTripCompletionSheet must use HairlineInkStroke", content.contains("HairlineInkStroke"))
        assertTrue("MarketTripCompletionSheet must use appColors.divider", content.contains("appColors.divider"))

        // 2. Perforated dashed divider line
        assertTrue("MarketTripCompletionSheet must use dashPathEffect for perforated divider", content.contains("dashPathEffect"))

        // 3. Monospace items ledger with dot leaders
        assertTrue("Must use FontFamily.Monospace", content.contains("FontFamily.Monospace"))
        assertTrue("Must use tabular numbers", content.contains("\"tnum\""))
        assertTrue("Must use dot leaders in monospace ledger", content.contains("· · ·"))

        // 4. Celebratory ink stamp
        assertTrue("Must contain PAID stamp", content.contains("PAID") || content.contains("পরিশোধিত"))
        assertTrue("Must rotate ink stamp by -12 degrees", content.contains("rotationZ = -12f"))

        // 5. Payment selector touch targets
        assertTrue("Payment selector must enforce minimumInteractiveComponentSize", content.contains("minimumInteractiveComponentSize()"))
        assertTrue("Payment selector must have minHeight 48dp", content.contains("defaultMinSize(minHeight = 48.dp)"))

        // 6. Typography >= 12sp constraint
        assertFalse("MarketTripCompletionSheet must not have 10sp", content.contains("10.sp"))
        assertFalse("MarketTripCompletionSheet must not have 11sp", content.contains("11.sp"))
        assertFalse("MarketTripCompletionSheet must not have 11.5sp", content.contains("11.5.sp"))

        // 7. Zero AI sparkles
        assertFalse("MarketTripCompletionSheet must not contain AutoAwesome", content.contains("AutoAwesome"))
        assertFalse("MarketTripCompletionSheet must not contain sparkle emoji", content.contains("✨"))
    }
}
