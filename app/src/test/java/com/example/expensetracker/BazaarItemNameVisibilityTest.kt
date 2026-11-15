package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.bazaar.BazaarDomainLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BazaarItemNameVisibilityTest {

    private fun readFile(relativePath: String): String {
        val root = File(System.getProperty("user.dir") ?: ".")
        var target = File(root, "app/src/main/java/$relativePath")
        if (!target.exists() && root.name == "app") {
            target = File(root, "src/main/java/$relativePath")
        }
        if (target.exists()) return target.readText()
        val alt = File(root, "src/main/java/$relativePath")
        if (alt.exists()) return alt.readText()
        throw IllegalArgumentException("File not found: $relativePath at ${target.absolutePath}")
    }

    @Test
    fun testSmartAddProducesNonEmptyNames() {
        val inputs = listOf(
            "আলু ২ কেজি",
            "পেঁয়াজ ১ কেজি",
            "সয়াবিন তেল ৫ লিটার",
            "ডিম ১ ডজন",
            "গরুর মাংস ১.৫ কেজি",
            "কাঁচামরিচ ২৫০ গ্রাম",
            "Salt 1 kg",
            "Chicken 2 pcs"
        )

        for (input in inputs) {
            val parsed = MarketListParser.parseSingleLine(input)
            assertTrue("Parsed item must not be null for: $input", parsed != null)
            assertFalse("Parsed item name must not be blank for: $input", parsed!!.name.isBlank())
            assertTrue("Quantity must be positive for: $input", parsed.quantity > 0)
            assertFalse("Unit must not be blank for: $input", parsed.unit.isBlank())
        }
    }

    @Test
    fun testCatalogAddsProduceNonEmptyNames() {
        val products = listOf(
            MasterProduct(id = 1L, name = "আলু", normalizedName = "alu", defaultUnit = "কেজি"),
            MasterProduct(id = 2L, name = "রুই মাছ", normalizedName = "rui_mach", defaultUnit = "কেজি"),
            MasterProduct(id = 3L, name = "দেশি মুরগি", normalizedName = "deshi_murgi", defaultUnit = "পিস")
        )

        val existingItems = emptyList<MarketItem>()
        for (prod in products) {
            val updated = BazaarDomainLogic.addOrIncrementCatalogItem(existingItems, prod, 100L)
            assertEquals("Should produce 1 item", 1, updated.size)
            assertEquals("Item name must match product name", prod.name, updated[0].name)
            assertFalse("Item name must not be blank", updated[0].name.isBlank())
        }
    }

    @Test
    fun testPlanItemRowInvariants() {
        val content = readFile("com/example/expensetracker/ui/bazaar/BazaarPlanScreen.kt")
        assertTrue(
            "PlanItemRow must explicitly set contentColor to avoid dark-on-dark invisible text",
            content.contains("contentColor = appColors.textPrimary")
        )
        assertTrue(
            "PlanItemRow must contain preview for 320dp width with 200% font scale",
            content.contains("PlanItemRowLightPreview") && content.contains("fontScale = 2.0f")
        )
    }
}
