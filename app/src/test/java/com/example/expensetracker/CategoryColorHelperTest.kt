package com.example.expensetracker

import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.CategoryColorHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryColorHelperTest {

    @Test
    fun testCuratedColorsAreUnique() {
        val colors = CategoryColorHelper.distinctColors
        val normalized = colors.map { CategoryColorHelper.normalizeHex(it) }
        val unique = normalized.toSet()
        assertEquals("All curated distinct colors must be unique", normalized.size, unique.size)
        assertTrue("Curated distinct colors must have at least 40 colors", colors.size >= 40)
    }

    @Test
    fun testIsColorTaken() {
        val categories = listOf(
            Category(1, "Food", TransactionType.EXPENSE, "Restaurant", "#EF4444", true),
            Category(2, "Shopping", TransactionType.EXPENSE, "ShoppingBag", "#DB2777", true),
            Category(3, "Salary", TransactionType.INCOME, "Work", "#22C55E", true)
        )

        // Exact match
        assertTrue(CategoryColorHelper.isColorTaken("#EF4444", categories))
        assertTrue(CategoryColorHelper.isColorTaken("#db2777", categories)) // case-insensitive

        // Exclude category (e.g. editing Category 1)
        assertFalse(CategoryColorHelper.isColorTaken("#EF4444", categories, excludeCategoryId = 1))
        assertTrue(CategoryColorHelper.isColorTaken("#DB2777", categories, excludeCategoryId = 1))

        // Unused color
        assertFalse(CategoryColorHelper.isColorTaken("#0284C7", categories))
    }

    @Test
    fun testGetCategoryUsingColor() {
        val categories = listOf(
            Category(1, "Food", TransactionType.EXPENSE, "Restaurant", "#EF4444", true),
            Category(2, "Shopping", TransactionType.EXPENSE, "ShoppingBag", "#DB2777", true)
        )

        val foodCat = CategoryColorHelper.getCategoryUsingColor("#ef4444", categories)
        assertNotNull(foodCat)
        assertEquals("Food", foodCat?.name)

        val unusedCat = CategoryColorHelper.getCategoryUsingColor("#0284C7", categories)
        assertNull(unusedCat)
    }

    @Test
    fun testGetAvailableColor() {
        val categories = listOf(
            Category(1, "Food", TransactionType.EXPENSE, "Restaurant", CategoryColorHelper.distinctColors[0], true),
            Category(2, "Shopping", TransactionType.EXPENSE, "ShoppingBag", CategoryColorHelper.distinctColors[1], true)
        )

        val available = CategoryColorHelper.getAvailableColor(categories)
        assertNotEquals(CategoryColorHelper.distinctColors[0], available)
        assertNotEquals(CategoryColorHelper.distinctColors[1], available)
        assertEquals(CategoryColorHelper.distinctColors[2], available)
    }

    @Test
    fun testNormalizeHex() {
        assertEquals("#EF4444", CategoryColorHelper.normalizeHex("#ef4444"))
        assertEquals("#EF4444", CategoryColorHelper.normalizeHex("ef4444"))
        assertEquals("#EF4444", CategoryColorHelper.normalizeHex("#FFEF4444"))
        assertEquals("#8B90A0", CategoryColorHelper.normalizeHex("invalid"))
    }

    @Test
    fun testColorDistanceCalculation() {
        // Red vs Green should have high distance
        val distRedGreen = CategoryColorHelper.calculateColorDistance("#EF4444", "#22C55E")
        // Red vs Pink should have smaller distance than Red vs Green
        val distRedPink = CategoryColorHelper.calculateColorDistance("#EF4444", "#F87171")

        assertTrue("Red vs Green distance ($distRedGreen) should be greater than Red vs Pink ($distRedPink)",
            distRedGreen > distRedPink)
    }

    @Test
    fun testFallbackGenerationWhenPaletteExhausted() {
        val allCurated = CategoryColorHelper.distinctColors.map { CategoryColorHelper.normalizeHex(it) }.toSet()
        val generated = CategoryColorHelper.generateNextDistinctColor(allCurated)

        assertNotNull(generated)
        assertTrue(generated.startsWith("#"))
        assertEquals(7, generated.length)
    }
}
