package com.example.expensetracker

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.LightAppColors
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Validates WCAG AA contrast (>= 4.5:1 for normal text) for all key token pairs in Pulse Design System:
 * - textPrimary / background
 * - textSecondary / surface
 * - onAccent / accent
 * - white text on each module hero gradient's both stops (Home, Money, Bazaar, Plan)
 * - income / expense / warning text on surface
 */
class WcagContrastTokenTest {

    private fun relativeLuminance(color: Color): Double {
        fun channelLuminance(c: Float): Double {
            val v = c.toDouble()
            return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        val r = channelLuminance(color.red)
        val g = channelLuminance(color.green)
        val b = channelLuminance(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = relativeLuminance(fg)
        val l2 = relativeLuminance(bg)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun assertWcagAa(tag: String, fg: Color, bg: Color, minRatio: Double = 4.5) {
        val ratio = contrastRatio(fg, bg)
        assertTrue(
            "WCAG AA Failure for $tag: expected >= $minRatio:1, but got ${"%.2f".format(ratio)}:1",
            ratio >= minRatio
        )
    }

    @Test
    fun testLightPaletteContrast() {
        val colors = LightAppColors

        // 1. textPrimary on background
        assertWcagAa("Light textPrimary / background", colors.textPrimary, colors.background)

        // 2. textSecondary on surface
        assertWcagAa("Light textSecondary / surface", colors.textSecondary, colors.surface)

        // 3. onAccent on accent
        assertWcagAa("Light onAccent / accent", colors.onAccent, colors.accent)

        // 4. White text on each module hero gradient's both stops
        assertWcagAa("Light Home Hero Stop 1", Color.White, colors.heroHome.first)
        assertWcagAa("Light Home Hero Stop 2", Color.White, colors.heroHome.second)

        assertWcagAa("Light Money Hero Stop 1", Color.White, colors.heroMoney.first)
        assertWcagAa("Light Money Hero Stop 2", Color.White, colors.heroMoney.second)

        assertWcagAa("Light Bazaar Hero Stop 1", Color.White, colors.heroBazaar.first)
        assertWcagAa("Light Bazaar Hero Stop 2", Color.White, colors.heroBazaar.second)

        assertWcagAa("Light Plan Hero Stop 1", Color.White, colors.heroPlan.first)
        assertWcagAa("Light Plan Hero Stop 2", Color.White, colors.heroPlan.second)

        // 5. Semantic colors (income, expense, warning) on surface
        assertWcagAa("Light income / surface", colors.income, colors.surface)
        assertWcagAa("Light expense / surface", colors.expense, colors.surface)
        assertWcagAa("Light warning / surface", colors.warning, colors.surface)
    }

    @Test
    fun testDarkPaletteContrast() {
        val colors = DarkAppColors

        // 1. textPrimary on background
        assertWcagAa("Dark textPrimary / background", colors.textPrimary, colors.background)

        // 2. textSecondary on surface
        assertWcagAa("Dark textSecondary / surface", colors.textSecondary, colors.surface)

        // 3. onAccent on accent
        assertWcagAa("Dark onAccent / accent", colors.onAccent, colors.accent)

        // 4. White text on each module hero gradient's both stops
        assertWcagAa("Dark Home Hero Stop 1", Color.White, colors.heroHome.first)
        assertWcagAa("Dark Home Hero Stop 2", Color.White, colors.heroHome.second)

        assertWcagAa("Dark Money Hero Stop 1", Color.White, colors.heroMoney.first)
        assertWcagAa("Dark Money Hero Stop 2", Color.White, colors.heroMoney.second)

        assertWcagAa("Dark Bazaar Hero Stop 1", Color.White, colors.heroBazaar.first)
        assertWcagAa("Dark Bazaar Hero Stop 2", Color.White, colors.heroBazaar.second)

        assertWcagAa("Dark Plan Hero Stop 1", Color.White, colors.heroPlan.first)
        assertWcagAa("Dark Plan Hero Stop 2", Color.White, colors.heroPlan.second)

        // 5. Semantic colors on surface
        assertWcagAa("Dark income / surface", colors.income, colors.surface)
        assertWcagAa("Dark expense / surface", colors.expense, colors.surface)
        assertWcagAa("Dark warning / surface", colors.warning, colors.surface)
    }

    @Test
    fun testAmoledPaletteContrast() {
        val colors = AmoledAppColors

        // 1. textPrimary on background
        assertWcagAa("AMOLED textPrimary / background", colors.textPrimary, colors.background)

        // 2. textSecondary on surface
        assertWcagAa("AMOLED textSecondary / surface", colors.textSecondary, colors.surface)

        // 3. onAccent on accent
        assertWcagAa("AMOLED onAccent / accent", colors.onAccent, colors.accent)

        // 4. White text on each module hero gradient's both stops
        assertWcagAa("AMOLED Home Hero Stop 1", Color.White, colors.heroHome.first)
        assertWcagAa("AMOLED Home Hero Stop 2", Color.White, colors.heroHome.second)

        assertWcagAa("AMOLED Money Hero Stop 1", Color.White, colors.heroMoney.first)
        assertWcagAa("AMOLED Money Hero Stop 2", Color.White, colors.heroMoney.second)

        assertWcagAa("AMOLED Bazaar Hero Stop 1", Color.White, colors.heroBazaar.first)
        assertWcagAa("AMOLED Bazaar Hero Stop 2", Color.White, colors.heroBazaar.second)

        assertWcagAa("AMOLED Plan Hero Stop 1", Color.White, colors.heroPlan.first)
        assertWcagAa("AMOLED Plan Hero Stop 2", Color.White, colors.heroPlan.second)

        // 5. Semantic colors on surface
        assertWcagAa("AMOLED income / surface", colors.income, colors.surface)
        assertWcagAa("AMOLED expense / surface", colors.expense, colors.surface)
        assertWcagAa("AMOLED warning / surface", colors.warning, colors.surface)
    }
}
