package com.example.expensetracker.ui.components

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.Category

/**
 * CategoryColorHelper provides a comprehensive color management engine
 * ensuring that every category has a unique, visually distinct color code.
 *
 * It provides:
 * 1. A curated palette of 49 high-contrast, visually separated colors across the color wheel.
 * 2. Intelligent unallocated color assignment for new categories.
 * 3. Conflict detection so that no two categories share the same color.
 * 4. Algorithmic fallback using Golden Ratio hue dispersion when custom categories exceed the curated palette.
 */
object CategoryColorHelper {

    /**
     * Curated list of 49 visually distinct, high-contrast colors.
     * Well-spaced across the 360-degree color wheel and tested for readability in both dark and light modes.
     */
    val distinctColors: List<String>
        get() = CategoryColorPalette.distinctColors


    /**
     * Normalizes a hex color string to uppercase "#RRGGBB".
     */
    fun normalizeHex(hex: String): String {
        val trimmed = hex.trim()
        val clean = if (trimmed.startsWith("#")) trimmed.substring(1) else trimmed
        return when (clean.length) {
            6 -> "#${clean.uppercase()}"
            8 -> "#${clean.substring(2).uppercase()}"
            else -> "#8B90A0"
        }
    }

    /**
     * Checks whether a given color hex is already in use by any existing category
     * (optionally excluding a specific category, e.g. when editing).
     */
    fun isColorTaken(
        colorHex: String,
        existingCategories: List<Category>,
        excludeCategoryId: Long? = null
    ): Boolean {
        val targetNorm = normalizeHex(colorHex)
        return existingCategories.any { cat ->
            (excludeCategoryId == null || cat.id != excludeCategoryId) &&
                    normalizeHex(cat.colorHex) == targetNorm
        }
    }

    /**
     * Returns the category currently using the specified color hex, if any
     * (optionally excluding a specific category).
     */
    fun getCategoryUsingColor(
        colorHex: String,
        existingCategories: List<Category>,
        excludeCategoryId: Long? = null
    ): Category? {
        val targetNorm = normalizeHex(colorHex)
        return existingCategories.find { cat ->
            (excludeCategoryId == null || cat.id != excludeCategoryId) &&
                    normalizeHex(cat.colorHex) == targetNorm
        }
    }

    /**
     * Returns all colors from the curated palette that are currently available (not assigned).
     */
    fun getAvailableColors(
        existingCategories: List<Category>,
        excludeCategoryId: Long? = null
    ): List<String> {
        val usedSet = existingCategories
            .filter { excludeCategoryId == null || it.id != excludeCategoryId }
            .map { normalizeHex(it.colorHex) }
            .toSet()

        return distinctColors.filter { normalizeHex(it) !in usedSet }
    }

    /**
     * Finds the first available unique color from the curated palette.
     * If all curated colors are in use, generates a new maximally distinct color.
     */
    fun getAvailableColor(
        existingCategories: List<Category>,
        excludeCategoryId: Long? = null
    ): String {
        val available = getAvailableColors(existingCategories, excludeCategoryId)
        if (available.isNotEmpty()) {
            return available.first()
        }

        // Curated palette exhausted: generate maximally distinct color
        val usedColors = existingCategories
            .filter { excludeCategoryId == null || it.id != excludeCategoryId }
            .map { normalizeHex(it.colorHex) }
            .toSet()

        return generateNextDistinctColor(usedColors)
    }

    fun calculateColorDistance(hex1: String, hex2: String): Double =
        ColorDistanceCalculator.calculateColorDistance(hex1, hex2)

    fun generateNextDistinctColor(existingColors: Collection<String>): String =
        ColorDistanceCalculator.generateNextDistinctColor(existingColors, distinctColors)

    fun rgbToHsv(r: Int, g: Int, b: Int): FloatArray =
        ColorDistanceCalculator.rgbToHsv(r, g, b)

    fun hsvToRgb(h: Float, s: Float, v: Float): IntArray =
        ColorDistanceCalculator.hsvToRgb(h, s, v)
}
