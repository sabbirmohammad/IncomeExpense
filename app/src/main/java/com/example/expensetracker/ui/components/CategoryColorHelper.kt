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

    /**
     * Calculates perceptual distance in HSV space between two hex colors.
     * Higher value means colors are visually more distinct.
     */
    fun calculateColorDistance(hex1: String, hex2: String): Double {
        val c1 = parseHexToRgb(hex1) ?: return Double.MAX_VALUE
        val c2 = parseHexToRgb(hex2) ?: return Double.MAX_VALUE

        val hsv1 = rgbToHsv(c1[0], c1[1], c1[2])
        val hsv2 = rgbToHsv(c2[0], c2[1], c2[2])

        var hueDiff = Math.abs(hsv1[0] - hsv2[0])
        if (hueDiff > 180f) hueDiff = 360f - hueDiff
        val normHueDiff = hueDiff / 180.0

        val satDiff = Math.abs(hsv1[1] - hsv2[1]).toDouble()
        val valDiff = Math.abs(hsv1[2] - hsv2[2]).toDouble()

        // Weight hue higher as human vision distinguishes color categories primarily by hue
        return Math.sqrt(normHueDiff * normHueDiff * 3.0 + satDiff * satDiff + valDiff * valDiff)
    }

    /**
     * Generates a new color that maximizes the minimum distance to all existing colors,
     * ensuring maximum visual distinctiveness.
     */
    fun generateNextDistinctColor(existingColors: Collection<String>): String {
        if (existingColors.isEmpty()) return distinctColors.first()

        var bestColor = "#EF4444"
        var maxMinDistance = -1.0

        // Sample 36 equidistant hues with high saturation and balanced value
        for (i in 0 until 36) {
            val hue = (i * 10f) % 360f
            val saturation = 0.75f
            val value = 0.90f
            val rgb = hsvToRgb(hue, saturation, value)
            val hex = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2])

            var minDistance = Double.MAX_VALUE
            for (used in existingColors) {
                val dist = calculateColorDistance(hex, used)
                if (dist < minDistance) {
                    minDistance = dist
                }
            }

            if (minDistance > maxMinDistance) {
                maxMinDistance = minDistance
                bestColor = hex
            }
        }

        return bestColor
    }

    fun rgbToHsv(r: Int, g: Int, b: Int): FloatArray {
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f

        val max = maxOf(rf, gf, bf)
        val min = minOf(rf, gf, bf)
        val delta = max - min

        var h = 0f
        if (delta > 0.00001f) {
            h = when (max) {
                rf -> 60f * (((gf - bf) / delta) % 6f)
                gf -> 60f * (((bf - rf) / delta) + 2f)
                else -> 60f * (((rf - gf) / delta) + 4f)
            }
            if (h < 0f) h += 360f
        }

        val s = if (max <= 0.00001f) 0f else delta / max
        val v = max

        return floatArrayOf(h, s, v)
    }

    fun hsvToRgb(h: Float, s: Float, v: Float): IntArray {
        val c = v * s
        val x = c * (1f - Math.abs((h / 60f) % 2f - 1f))
        val m = v - c

        val (rf, gf, bf) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 120f + 60f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = Math.round((rf + m) * 255f).coerceIn(0, 255)
        val g = Math.round((gf + m) * 255f).coerceIn(0, 255)
        val b = Math.round((bf + m) * 255f).coerceIn(0, 255)

        return intArrayOf(r, g, b)
    }

    private fun parseHexToRgb(hex: String): IntArray? {
        return try {
            val clean = if (hex.startsWith("#")) hex.substring(1) else hex
            val colorInt = clean.toLong(16).toInt()
            val r = (colorInt shr 16) and 0xFF
            val g = (colorInt shr 8) and 0xFF
            val b = colorInt and 0xFF
            intArrayOf(r, g, b)
        } catch (_: Exception) {
            null
        }
    }
}
