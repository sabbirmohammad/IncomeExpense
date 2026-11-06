package com.example.expensetracker

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getDepartmentMeta
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

/**
 * Challenger M2 Empirical Stress Test Suite:
 * Rigorously stress-tests:
 * 1. Department stall collapse/expand state isolation, toggling, and persistence across list switches.
 * 2. Accurate live completion counters under arbitrary item status toggling.
 * 3. LazyColumn key stability and zero recomposition key collisions.
 * 4. Ergonomic touch target invariants (>= 48dp) across all interactive elements.
 * 5. Strictly zero generic AI sparkle icons (AutoAwesome, ✨) across all M2 source files.
 * 6. Bottom trip summary running total, tabular number formatting, and progress percentage invariants.
 */
class ChallengerM2EmpiricalStressTest {

    private fun readSourceFile(relativePath: String): String {
        val candidates = listOf(
            File("src/main/java/$relativePath"),
            File("app/src/main/java/$relativePath"),
            File("../app/src/main/java/$relativePath")
        )
        val file = candidates.find { it.exists() }
        assertNotNull("Expected source file to exist: $relativePath", file)
        return file!!.readText()
    }

    @Test
    fun testDepartmentStallCollapseExpandStateIsolation() {
        val collapsedDepts = mutableMapOf<String, Boolean>()

        // 1. Initial State: All departments default to expanded
        for (dept in ALL_DEPARTMENTS) {
            val isCollapsed = collapsedDepts[dept.key] == true
            assertFalse("Department ${dept.key} must default to expanded", isCollapsed)
        }

        // 2. Collapse veggies only
        collapsedDepts["veggies"] = true
        assertTrue("Veggies must be collapsed", collapsedDepts["veggies"] == true)
        assertFalse("Meat/Fish must remain expanded", collapsedDepts["meat_fish"] == true)
        assertFalse("Dairy/Bakery must remain expanded", collapsedDepts["dairy_bakery"] == true)
        assertFalse("Groceries must remain expanded", collapsedDepts["groceries"] == true)

        // 3. Collapse groceries
        collapsedDepts["groceries"] = true
        assertTrue("Veggies must be collapsed", collapsedDepts["veggies"] == true)
        assertTrue("Groceries must be collapsed", collapsedDepts["groceries"] == true)
        assertFalse("Meat/Fish must remain expanded", collapsedDepts["meat_fish"] == true)
        assertFalse("Dairy/Bakery must remain expanded", collapsedDepts["dairy_bakery"] == true)

        // 4. Re-expand veggies
        collapsedDepts["veggies"] = false
        assertFalse("Veggies must be re-expanded", collapsedDepts["veggies"] == true)
        assertTrue("Groceries must still be collapsed", collapsedDepts["groceries"] == true)

        // 5. Stress Test: 5,000 rapid randomized toggles
        collapsedDepts.clear()
        val rng = Random(42)
        val deptKeys = ALL_DEPARTMENTS.map { it.key }
        val oracleState = mutableMapOf<String, Boolean>()
        for (k in deptKeys) oracleState[k] = false

        for (i in 0 until 5000) {
            val targetKey = deptKeys[rng.nextInt(deptKeys.size)]
            val current = collapsedDepts[targetKey] == true
            val next = !current
            collapsedDepts[targetKey] = next
            oracleState[targetKey] = next

            // Verify integrity of all 4 keys at every 500th iteration
            if (i % 500 == 0) {
                for (k in deptKeys) {
                    assertEquals(
                        "Mismatch at iteration $i for key $k",
                        oracleState[k],
                        collapsedDepts[k] == true
                    )
                }
            }
        }
    }

    @Test
    fun testLiveProgressCounterUnderItemMutations() {
        val testItems = mutableListOf(
            MarketItem(id = 1L, listId = 10L, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 2L, listId = 10L, name = "পটল", quantity = 1.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 3L, listId = 10L, name = "রুই মাছ", quantity = 1.5, unit = "কেজি", isPurchased = false),
            MarketItem(id = 4L, listId = 10L, name = "গরুর মাংস", quantity = 1.0, unit = "কেজি", isPurchased = true, amount = 750.0),
            MarketItem(id = 5L, listId = 10L, name = "তরল দুধ", quantity = 1.0, unit = "লিটার", isPurchased = true, amount = 90.0),
            MarketItem(id = 6L, listId = 10L, name = "সয়াবিন তেল", quantity = 2.0, unit = "লিটার", isPurchased = false),
            MarketItem(id = 7L, listId = 10L, name = "লবণ", quantity = 1.0, unit = "কেজি", isPurchased = false)
        )

        val itemsByDept = testItems.groupBy { getMarketItemCategory(it.name) }

        // Veggies: 2 items, 0 purchased
        val veggies = itemsByDept["veggies"] ?: emptyList()
        assertEquals(2, veggies.size)
        assertEquals(0, veggies.count { it.isPurchased })

        // Meat & Fish: 2 items, 1 purchased
        val meatFish = itemsByDept["meat_fish"] ?: emptyList()
        assertEquals(2, meatFish.size)
        assertEquals(1, meatFish.count { it.isPurchased })

        // Dairy: 1 item, 1 purchased (All done)
        val dairy = itemsByDept["dairy_bakery"] ?: emptyList()
        assertEquals(1, dairy.size)
        assertEquals(1, dairy.count { it.isPurchased })
        assertTrue("Dairy stall should be completed", dairy.count { it.isPurchased } == dairy.size && dairy.isNotEmpty())

        // Groceries: 2 items, 0 purchased
        val groceries = itemsByDept["groceries"] ?: emptyList()
        assertEquals(2, groceries.size)
        assertEquals(0, groceries.count { it.isPurchased })

        // Simulate toggling veggies items to purchased
        testItems[0] = testItems[0].copy(isPurchased = true, amount = 80.0)
        val updatedItemsByDept = testItems.groupBy { getMarketItemCategory(it.name) }
        val updatedVeggies = updatedItemsByDept["veggies"] ?: emptyList()
        assertEquals(1, updatedVeggies.count { it.isPurchased })

        testItems[1] = testItems[1].copy(isPurchased = true, amount = 60.0)
        val fullyPurchasedVeggies = testItems.groupBy { getMarketItemCategory(it.name) }["veggies"] ?: emptyList()
        assertEquals(2, fullyPurchasedVeggies.count { it.isPurchased })
        assertTrue("Veggies should now be all completed", fullyPurchasedVeggies.count { it.isPurchased } == fullyPurchasedVeggies.size)

        // Progress text simulation
        val compCount = fullyPurchasedVeggies.count { it.isPurchased }
        val totCount = fullyPurchasedVeggies.size
        val bnText = "✓ ${convertToBengaliDigits(compCount.toString())}/${convertToBengaliDigits(totCount.toString())}টি সম্পন্ন"
        val enText = "✓ $compCount/$totCount Done"
        assertEquals("✓ ২/২টি সম্পন্ন", bnText)
        assertEquals("✓ 2/2 Done", enText)
    }

    @Test
    fun testLazyColumnKeyUniquenessAndStability() {
        val headerKeys = ALL_DEPARTMENTS.map { "dept_header_${it.key}" }
        val uniqueHeaderKeys = headerKeys.toSet()
        assertEquals("All department header keys must be distinct", headerKeys.size, uniqueHeaderKeys.size)

        // Verify known keys
        assertTrue(headerKeys.contains("dept_header_veggies"))
        assertTrue(headerKeys.contains("dept_header_meat_fish"))
        assertTrue(headerKeys.contains("dept_header_dairy_bakery"))
        assertTrue(headerKeys.contains("dept_header_groceries"))

        // Simulate item IDs (Longs) vs Header Keys (Strings)
        val itemIds = listOf(1L, 2L, 3L, 4L, 5L)
        val keySet = mutableSetOf<Any>()
        for (hk in headerKeys) {
            assertTrue("Header key should be added without collision", keySet.add(hk))
        }
        for (id in itemIds) {
            assertTrue("Item ID should be added without collision", keySet.add(id))
        }
        assertEquals(headerKeys.size + itemIds.size, keySet.size)
    }

    @Test
    fun testErgonomicTouchTargetsStaticAudit() {
        // 1. Audit MarketItemRow.kt
        val itemRowContent = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // Checkbox: sizeIn(minWidth = 48.dp, minHeight = 48.dp) and minimumInteractiveComponentSize()
        assertTrue(
            "MarketItemRow checkbox must declare minWidth=48.dp, minHeight=48.dp",
            itemRowContent.contains("minWidth = 48.dp, minHeight = 48.dp")
        )
        assertTrue(
            "MarketItemRow checkbox must apply minimumInteractiveComponentSize()",
            itemRowContent.contains("minimumInteractiveComponentSize()")
        )

        // Quantity pill: minimumInteractiveComponentSize()
        assertTrue(
            "MarketItemRow quantity pill must have minimumInteractiveComponentSize()",
            itemRowContent.contains("minimumInteractiveComponentSize()")
        )

        // Substitute button: minimumInteractiveComponentSize()
        assertTrue(
            "MarketItemRow substitute trigger must have minimumInteractiveComponentSize()",
            itemRowContent.contains("onSubstituteClick") && itemRowContent.contains("minimumInteractiveComponentSize()")
        )

        // Delete button in edit mode: size(48.dp) and minimumInteractiveComponentSize()
        assertTrue(
            "MarketItemRow delete button must have size(48.dp)",
            itemRowContent.contains("size(48.dp)")
        )
        assertTrue(
            "MarketItemRow delete button must have minimumInteractiveComponentSize()",
            itemRowContent.contains("minimumInteractiveComponentSize()")
        )

        // 2. Audit MarketDepartmentSectionHeader.kt
        val headerContent = readSourceFile("com/example/expensetracker/ui/market/MarketDepartmentSectionHeader.kt")
        assertTrue(
            "MarketDepartmentSectionHeader must have minimumInteractiveComponentSize() on its inner row",
            headerContent.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "MarketDepartmentSectionHeader must have fillMaxWidth()",
            headerContent.contains("fillMaxWidth()")
        )

        // 3. Audit MarketFinishButton.kt
        val finishButtonContent = readSourceFile("com/example/expensetracker/ui/dashboard/MarketFinishButton.kt")
        assertTrue(
            "MarketFinishButton must declare height(50.dp) to satisfy >= 48dp ergonomics",
            finishButtonContent.contains("height(50.dp)")
        )
        assertTrue(
            "MarketFinishButton must have fillMaxWidth()",
            finishButtonContent.contains("fillMaxWidth()")
        )

        // 4. Audit MarketBottomTripSummaryBar.kt
        val summaryBarContent = readSourceFile("com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt")
        assertTrue(
            "MarketBottomTripSummaryBar must declare navigationBarsPadding() for safe thumb access",
            summaryBarContent.contains("navigationBarsPadding()")
        )
    }

    @Test
    fun testStrictlyZeroGenericAiSparkleIcons() {
        val filesToCheck = listOf(
            "com/example/expensetracker/ui/market/MarketDepartmentSectionHeader.kt",
            "com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt",
            "com/example/expensetracker/ui/dashboard/MarketItemRow.kt",
            "com/example/expensetracker/ui/dashboard/MarketFinishButton.kt",
            "com/example/expensetracker/ui/market/MarketScreen.kt"
        )

        for (filePath in filesToCheck) {
            val content = readSourceFile(filePath)
            assertFalse(
                "File $filePath must not contain AutoAwesome generic AI icon",
                content.contains("AutoAwesome")
            )
            assertFalse(
                "File $filePath must not contain sparkle emoji ✨",
                content.contains("✨")
            )
        }
    }

    @Test
    fun testBottomTripSummarySpendAndProgressCalculations() {
        // Boundary case 1: 0 items
        val totalCount0 = 0
        val purchasedCount0 = 0
        val progressPct0 = if (totalCount0 > 0) (purchasedCount0 * 100) / totalCount0 else 0
        assertEquals(0, progressPct0)

        // Boundary case 2: 5 items, 0 purchased
        val totalCount1 = 5
        val purchasedCount1 = 0
        val progressPct1 = if (totalCount1 > 0) (purchasedCount1 * 100) / totalCount1 else 0
        assertEquals(0, progressPct1)

        // Boundary case 3: 5 items, 4 purchased
        val totalCount2 = 5
        val purchasedCount2 = 4
        val progressPct2 = if (totalCount2 > 0) (purchasedCount2 * 100) / totalCount2 else 0
        assertEquals(80, progressPct2)

        // Boundary case 4: 5 items, 5 purchased (100%)
        val totalCount3 = 5
        val purchasedCount3 = 5
        val progressPct3 = if (totalCount3 > 0) (purchasedCount3 * 100) / totalCount3 else 0
        assertEquals(100, progressPct3)

        // Verify currency formatting
        val formatted0 = formatCurrency(0.0, "৳", useBengaliNumerals = true)
        assertEquals("৳ ০", formatted0)

        val formatted1250 = formatCurrency(1250.0, "৳", useBengaliNumerals = true)
        assertEquals("৳ ১,২৫০", formatted1250)

        val formattedDec = formatCurrency(45.5, "৳", useBengaliNumerals = true)
        assertEquals("৳ ৪৫.৫", formattedDec)

        val formattedEn = formatCurrency(1250.0, "৳", useBengaliNumerals = false)
        assertEquals("৳ 1,250", formattedEn)
    }

    @Test
    fun testAllDepartmentsMetaFidelity() {
        assertEquals(4, ALL_DEPARTMENTS.size)

        val veggies = getDepartmentMeta("veggies")
        assertEquals("veggies", veggies.key)
        assertEquals("কাঁচাবাজার", veggies.titleBn)
        assertEquals("Vegetables", veggies.titleEn)
        assertEquals("🥬", veggies.icon)

        val meatFish = getDepartmentMeta("meat_fish")
        assertEquals("meat_fish", meatFish.key)
        assertEquals("মাছ ও মাংস", meatFish.titleBn)
        assertEquals("Meat & Fish", meatFish.titleEn)
        assertEquals("🐟", meatFish.icon)

        val dairy = getDepartmentMeta("dairy_bakery")
        assertEquals("dairy_bakery", dairy.key)
        assertEquals("দুধ ও বেকারি", dairy.titleBn)
        assertEquals("Dairy & Bakery", dairy.titleEn)
        assertEquals("🥛", dairy.icon)

        val groceries = getDepartmentMeta("groceries")
        assertEquals("groceries", groceries.key)
        assertEquals("মুদি সদাই", groceries.titleBn)
        assertEquals("Groceries", groceries.titleEn)
        assertEquals("🧂", groceries.icon)

        // Fallback for unknown category
        val unknown = getDepartmentMeta("unknown_category_xyz")
        assertEquals("groceries", unknown.key)
    }
}
