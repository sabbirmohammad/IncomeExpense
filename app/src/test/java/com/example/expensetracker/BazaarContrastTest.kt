package com.example.expensetracker

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.LightAppColors
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies WCAG 2.1 AA contrast compliance (>= 4.5:1 text, >= 3:1 graphical)
 * for Bazaar module colors across Light, Dark, and AMOLED themes.
 */
class BazaarContrastTest {

    private fun linearize(channel: Float): Double {
        return if (channel <= 0.03928) {
            channel / 12.92
        } else {
            ((channel + 0.055) / 1.055).pow(2.4)
        }
    }

    private fun luminance(color: Color): Double {
        val r = linearize(color.red)
        val g = linearize(color.green)
        val b = linearize(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = luminance(fg)
        val l2 = luminance(bg)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun blend(fg: Color, bg: Color, alpha: Float): Color {
        val r = fg.red * alpha + bg.red * (1f - alpha)
        val g = fg.green * alpha + bg.green * (1f - alpha)
        val b = fg.blue * alpha + bg.blue * (1f - alpha)
        return Color(r, g, b)
    }

    private fun verifyBazaarContrastForTheme(themeName: String, colors: AppColors) {
        // 1. OnBazaarAccent on BazaarAccent (Checkout button text, check mark) must pass AA >= 4.5:1
        val onAccentRatio = contrastRatio(colors.onBazaarAccent, colors.bazaarAccent)
        assertTrue(
            "$themeName: onBazaarAccent on bazaarAccent must be >= 4.5:1 (was $onAccentRatio)",
            onAccentRatio >= 4.5
        )

        // 2. Check icon on Check Circle must pass graphical contrast >= 3.0:1
        val checkIconRatio = contrastRatio(colors.onBazaarAccent, colors.bazaarAccent)
        assertTrue(
            "$themeName: check icon on check circle must pass graphical >= 3.0:1 (was $checkIconRatio)",
            checkIconRatio >= 3.0
        )

        // 3. BazaarAccent against Surface must pass AA >= 4.5:1
        val accentOnSurfaceRatio = contrastRatio(colors.bazaarAccent, colors.surface)
        assertTrue(
            "$themeName: bazaarAccent on surface must be >= 4.5:1 (was $accentOnSurfaceRatio)",
            accentOnSurfaceRatio >= 4.5
        )

        // 4. Text on Done Item Card (opaque surface + 12-18% bazaarAccent tint)
        val tintAlpha = if (colors.isDark) 0.18f else 0.12f
        val blendedCardBg = blend(colors.bazaarAccent, colors.surface, tintAlpha)

        val textPrimaryRatio = contrastRatio(colors.textPrimary, blendedCardBg)
        assertTrue(
            "$themeName: textPrimary on done card tint must be >= 4.5:1 (was $textPrimaryRatio)",
            textPrimaryRatio >= 4.5
        )

        val textSecondaryRatio = contrastRatio(colors.textSecondary, blendedCardBg)
        assertTrue(
            "$themeName: textSecondary on done card tint must be >= 4.5:1 (was $textSecondaryRatio)",
            textSecondaryRatio >= 4.5
        )
    }

    @Test
    fun testBazaarColorsContrastComplianceLight() {
        verifyBazaarContrastForTheme("Light", LightAppColors)
    }

    @Test
    fun testBazaarColorsContrastComplianceDark() {
        verifyBazaarContrastForTheme("Dark", DarkAppColors)
    }

    @Test
    fun testBazaarColorsContrastComplianceAmoled() {
        verifyBazaarContrastForTheme("AMOLED", AmoledAppColors)
    }
}
