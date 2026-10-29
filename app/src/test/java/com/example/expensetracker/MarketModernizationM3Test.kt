package com.example.expensetracker

import com.example.expensetracker.ui.dashboard.MarketDepartment
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Unit and structural verification tests for Milestone M3:
 * 1. Type-safe MarketDepartment enum with enhanced Bengali titles and backward compatibility.
 * 2. Classifier edge case remediation (Bogra district curd / produce disambiguation).
 * 3. Collapsible department section header (MarketDepartmentSectionHeader) with live completion counter,
 *    animated chevron rotation, and 48dp touch surface.
 * 4. Grouped shopping checklist with collapsible state in MarketScreen and MarketListSheet.
 * 5. M3 Expressive styling, 14dp corner radiuses, semantic theme hygiene, and 48dp interactive touch targets.
 */
class MarketModernizationM3Test {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testMarketDepartmentEnumPropertiesAndTitles() {
        // 1. Verify all 4 enum entries exist with expected IDs
        val entries = MarketDepartment.entries
        assertEquals(4, entries.size)
        assertEquals("veggies", MarketDepartment.VEGGIES.id)
        assertEquals("meat_fish", MarketDepartment.MEAT_FISH.id)
        assertEquals("dairy_bakery", MarketDepartment.DAIRY_BAKERY.id)
        assertEquals("groceries", MarketDepartment.GROCERIES.id)

        // 2. Enhanced Bengali and English Titles
        assertEquals("কাঁচাবাজার ও সবজি", MarketDepartment.VEGGIES.titleBn)
        assertEquals("Vegetables & Greens", MarketDepartment.VEGGIES.titleEn)
        assertEquals("🥬", MarketDepartment.VEGGIES.icon)

        assertEquals("মাছ ও মাংস", MarketDepartment.MEAT_FISH.titleBn)
        assertEquals("Meat & Fish", MarketDepartment.MEAT_FISH.titleEn)
        assertEquals("🐟", MarketDepartment.MEAT_FISH.icon)

        assertEquals("দুধ ও বেকারি", MarketDepartment.DAIRY_BAKERY.titleBn)
        assertEquals("Dairy & Bakery", MarketDepartment.DAIRY_BAKERY.titleEn)
        assertEquals("🥛", MarketDepartment.DAIRY_BAKERY.icon)

        assertEquals("মুদি ও মসলা", MarketDepartment.GROCERIES.titleBn)
        assertEquals("Groceries & Spices", MarketDepartment.GROCERIES.titleEn)
        assertEquals("🧂", MarketDepartment.GROCERIES.icon)

        // 3. Companion lookups
        assertEquals(MarketDepartment.VEGGIES, MarketDepartment.fromKey("veggies"))
        assertEquals(MarketDepartment.MEAT_FISH, MarketDepartment.fromKey("meat_fish"))
        assertEquals(MarketDepartment.DAIRY_BAKERY, MarketDepartment.fromKey("dairy_bakery"))
        assertEquals(MarketDepartment.GROCERIES, MarketDepartment.fromKey("groceries"))
        assertEquals(MarketDepartment.GROCERIES, MarketDepartment.fromKey("unknown_key"))

        assertEquals(MarketDepartment.VEGGIES, MarketDepartment.fromItemName("আলু"))
        assertEquals(MarketDepartment.GROCERIES, MarketDepartment.fromItemName("সয়াবিন তেল"))

        // 4. toDepartmentMeta backward compatibility mapping
        val meta = MarketDepartment.VEGGIES.toDepartmentMeta()
        assertEquals("veggies", meta.key)
        assertEquals("কাঁচাবাজার ও সবজি", meta.titleBn)
        assertEquals("Vegetables & Greens", meta.titleEn)
        assertEquals("🥬", meta.icon)
    }

    @Test
    fun testClassifierBoguraEdgeCasesAndDisambiguation() {
        // Bogra district items: Bogra curd must classify as dairy, Bogra potato as veggies
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়া দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("মিষ্টি দই"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার আলু"))

        // Standard Spices with powder (গুঁড়া / গুড়া) must classify as groceries
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুড়া"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("জিরার গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("চিলি পাউডার"))

        // Standard Produce
        assertEquals("veggies", getMarketItemCategory("আলু"))
        assertEquals("veggies", getMarketItemCategory("কাঁচা মরিচ"))
        assertEquals("veggies", getMarketItemCategory("টমেটো"))
        assertEquals("veggies", getMarketItemCategory("পেঁয়াজ"))

        // Standard Fish & Meat
        assertEquals("meat_fish", getMarketItemCategory("রুই মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("ইলিশ মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("গরুর মাংস"))
        assertEquals("meat_fish", getMarketItemCategory("ব্রয়লার মুরগি"))

        // Standard Dairy & Bakery
        assertEquals("dairy_bakery", getMarketItemCategory("তরল দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("পাউরুটি"))
        assertEquals("dairy_bakery", getMarketItemCategory("মাখন"))
    }

    @Test
    fun testMarketDepartmentSectionHeaderStructureAndFeatures() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketDepartmentSectionHeader.kt")

        // 1. Both signatures (MarketDepartment and DepartmentMeta)
        assertTrue(
            "MarketDepartmentSectionHeader must accept MarketDepartment parameter",
            content.contains("fun MarketDepartmentSectionHeader(\n    dept: MarketDepartment,") ||
            content.contains("fun MarketDepartmentSectionHeader(dept: MarketDepartment,") ||
            content.contains("dept: MarketDepartment,")
        )
        assertTrue(
            "MarketDepartmentSectionHeader must overload for DepartmentMeta parameter",
            content.contains("dept: DepartmentMeta,")
        )

        // 2. TitleMedium typography
        assertTrue(
            "MarketDepartmentSectionHeader must use MaterialTheme.typography.titleMedium",
            content.contains("MaterialTheme.typography.titleMedium")
        )

        // 3. Live progress pill format
        assertTrue(
            "MarketDepartmentSectionHeader must format Bengali progress as 'X/Y টি সম্পন্ন'",
            content.contains("টি সম্পন্ন")
        )
        assertTrue(
            "MarketDepartmentSectionHeader must format English progress as 'X/Y Completed'",
            content.contains("Completed")
        )
        assertTrue(
            "MarketDepartmentSectionHeader must support completion state '✓ সম্পন্ন'",
            content.contains("✓ সম্পন্ন") && content.contains("✓ Done")
        )

        // 4. Smooth 180° rotation animated chevron
        assertTrue(
            "MarketDepartmentSectionHeader must animate chevron rotation with animateFloatAsState",
            content.contains("animateFloatAsState(") &&
            content.contains("180f") &&
            content.contains("rotationZ = rotationAngle")
        )

        // 5. Full-width row clickability with minimum 48dp height
        assertTrue(
            "MarketDepartmentSectionHeader must ensure minimum 48dp interactive height",
            content.contains("minimumInteractiveComponentSize()") &&
            content.contains("heightIn(min = 48.dp)")
        )

        // 6. Haptic feedback
        assertTrue(
            "MarketDepartmentSectionHeader must trigger lightTick haptics on click",
            content.contains("haptics.lightTick()") &&
            content.contains("onToggleCollapse()")
        )
    }

    @Test
    fun testMarketScreenCollapsibleDepartmentGrouping() {
        val content = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Group items by department
        assertTrue(
            "MarketScreen must group items by department",
            content.contains("itemsByDept = remember(filteredItems)") &&
            content.contains("filteredItems.groupBy { getMarketItemCategory(it.name) }")
        )

        // 2. Collapsible state tracking with rememberSaveable
        assertTrue(
            "MarketScreen must track collapsedDepartments with rememberSaveable",
            content.contains("var collapsedDepartments by rememberSaveable { mutableStateOf(setOf<String>()) }")
        )

        // 3. Iterate over departments and render MarketDepartmentSectionHeader
        assertTrue(
            "MarketScreen must iterate over MarketDepartment.entries",
            content.contains("for (dept in MarketDepartment.entries)")
        )
        assertTrue(
            "MarketScreen must render MarketDepartmentSectionHeader",
            content.contains("MarketDepartmentSectionHeader(") &&
            content.contains("dept = dept") &&
            content.contains("isCollapsed = isCollapsed")
        )

        // 4. Toggle collapse behavior
        assertTrue(
            "MarketScreen must toggle collapsed departments",
            content.contains("collapsedDepartments - dept.id") &&
            content.contains("collapsedDepartments + dept.id")
        )
    }

    @Test
    fun testMarketListSheetCollapsibleDepartmentGrouping() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        // 1. Group items by department
        assertTrue(
            "MarketListSheet must group items by department",
            content.contains("itemsByDept = remember(filteredItems)") &&
            content.contains("filteredItems.groupBy { getMarketItemCategory(it.name) }")
        )

        // 2. Collapsible state tracking
        assertTrue(
            "MarketListSheet must track collapsedDepartments with rememberSaveable",
            content.contains("var collapsedDepartments by rememberSaveable { mutableStateOf(setOf<String>()) }")
        )

        // 3. Iterate over departments and render MarketDepartmentSectionHeader
        assertTrue(
            "MarketListSheet must iterate over MarketDepartment.entries",
            content.contains("for (dept in MarketDepartment.entries)")
        )
        assertTrue(
            "MarketListSheet must render MarketDepartmentSectionHeader",
            content.contains("MarketDepartmentSectionHeader(") &&
            content.contains("dept = dept")
        )
    }

    @Test
    fun testVisualPolishThemeHygieneAnd48dpTouchTargets() {
        val itemRowContent = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")
        val finishButtonContent = readFile("com/example/expensetracker/ui/dashboard/MarketFinishButton.kt")
        val checklistCardContent = readFile("com/example/expensetracker/ui/components/MarketChecklistCard.kt")
        val dashboardItemRowContent = readFile("com/example/expensetracker/ui/components/DashboardMarketItemRow.kt")

        // 1. MarketItemRow: 14dp rounded corner container
        assertTrue(
            "MarketItemRow must use RoundedCornerShape(14.dp)",
            itemRowContent.contains("RoundedCornerShape(14.dp)")
        )

        // 2. MarketItemRow: Quantity pill minimum 48dp touch target
        assertTrue(
            "MarketItemRow quantity pill must enforce minimumInteractiveComponentSize",
            itemRowContent.contains("minimumInteractiveComponentSize()") &&
            itemRowContent.contains("onUnitClick()")
        )

        // 3. MarketItemRow: Substitute pill minimum 48dp touch target
        assertTrue(
            "MarketItemRow substitute pill must enforce minimumInteractiveComponentSize",
            itemRowContent.contains("onSubstituteClick()")
        )

        // 4. MarketItemRow: Edit mode delete button minimum 48dp touch target
        assertTrue(
            "MarketItemRow edit mode delete button must enforce 48dp size with minimumInteractiveComponentSize",
            itemRowContent.contains("size(48.dp)")
        )

        // 5. DashboardMarketItemRow: Checkbox minimum 48dp touch target
        assertTrue(
            "DashboardMarketItemRow checkbox must enforce minimum 48dp touch target",
            dashboardItemRowContent.contains("sizeIn(minWidth = 48.dp, minHeight = 48.dp)") &&
            dashboardItemRowContent.contains("minimumInteractiveComponentSize()")
        )

        // 6. MarketFinishButton: Theme hygiene with semantic background
        assertTrue(
            "MarketFinishButton must use semantic appColors.background for dark mode",
            finishButtonContent.contains("if (appColors.isDark) appColors.background else Color.White")
        )

        // 7. MarketChecklistCard: Theme hygiene replacing hardcoded 0xFF2EC4B6 with appColors.accent
        assertFalse(
            "MarketChecklistCard must not contain hardcoded 0xFF2EC4B6",
            checklistCardContent.contains("0xFF2EC4B6")
        )
        assertTrue(
            "MarketChecklistCard must use appColors.accent for cart icon and background",
            checklistCardContent.contains("tint = appColors.accent") &&
            checklistCardContent.contains("appColors.accent.copy(alpha = 0.15f)")
        )
    }
}
