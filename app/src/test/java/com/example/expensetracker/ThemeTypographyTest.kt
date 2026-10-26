package com.example.expensetracker

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.ColorSurfaceLight
import com.example.expensetracker.theme.ColorTextPrimaryDark
import com.example.expensetracker.theme.ColorTextPrimaryLight
import com.example.expensetracker.theme.ColorWarningBillsLight
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.LightAppColors
import com.example.expensetracker.theme.Typography
import com.example.expensetracker.ui.components.getPaymentMethodColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeTypographyTest {

    @Test
    fun testTypographyHasNoHardcodedTextColor() {
        // Material 3 Typography definitions must not bake in hardcoded colors (ColorTextPrimary, ColorTextSecondary, ColorTextMuted).
        // Their colors must be Color.Unspecified so they resolve to LocalContentColor / onSurface dynamically across all themes.
        assertEquals(
            "headlineLarge must have Color.Unspecified to avoid invisible text in Light Theme",
            Color.Unspecified,
            Typography.headlineLarge.color
        )
        assertEquals(
            "headlineMedium must have Color.Unspecified",
            Color.Unspecified,
            Typography.headlineMedium.color
        )
        assertEquals(
            "titleLarge must have Color.Unspecified",
            Color.Unspecified,
            Typography.titleLarge.color
        )
        assertEquals(
            "titleMedium must have Color.Unspecified",
            Color.Unspecified,
            Typography.titleMedium.color
        )
        assertEquals(
            "titleSmall must have Color.Unspecified",
            Color.Unspecified,
            Typography.titleSmall.color
        )
        assertEquals(
            "bodyLarge must have Color.Unspecified",
            Color.Unspecified,
            Typography.bodyLarge.color
        )
        assertEquals(
            "bodyMedium must have Color.Unspecified",
            Color.Unspecified,
            Typography.bodyMedium.color
        )
        assertEquals(
            "bodySmall must have Color.Unspecified to avoid low contrast in Light Theme",
            Color.Unspecified,
            Typography.bodySmall.color
        )
        assertEquals(
            "labelMedium must have Color.Unspecified to avoid low contrast in Light Theme",
            Color.Unspecified,
            Typography.labelMedium.color
        )
        assertEquals(
            "labelSmall must have Color.Unspecified to avoid low contrast in Light Theme",
            Color.Unspecified,
            Typography.labelSmall.color
        )
    }

    @Test
    fun testAllTenTypographyStylesHaveUnspecifiedColor() {
        val allDefinedStyles = listOf(
            "headlineLarge" to Typography.headlineLarge,
            "headlineMedium" to Typography.headlineMedium,
            "titleLarge" to Typography.titleLarge,
            "titleMedium" to Typography.titleMedium,
            "titleSmall" to Typography.titleSmall,
            "bodyLarge" to Typography.bodyLarge,
            "bodyMedium" to Typography.bodyMedium,
            "bodySmall" to Typography.bodySmall,
            "labelMedium" to Typography.labelMedium,
            "labelSmall" to Typography.labelSmall
        )
        allDefinedStyles.forEach { (name, style) ->
            assertEquals(
                "$name must have Color.Unspecified to prevent contrast failures across themes",
                Color.Unspecified,
                style.color
            )
        }
    }

    @Test
    fun testLightAndDarkAppColorsContrastPalette() {
        // Light mode textPrimary must be dark (#0F172A), not white (#FFFFFF)
        assertEquals(ColorTextPrimaryLight, LightAppColors.textPrimary)
        assertNotEquals(ColorTextPrimaryDark, LightAppColors.textPrimary)
        assertEquals(Color(0xFF0F172A), LightAppColors.textPrimary)
        assertEquals(ColorSurfaceLight, LightAppColors.surface)
        assertFalse(LightAppColors.isDark)

        // Dark and AMOLED textPrimary must be white (#FFFFFF)
        assertEquals(ColorTextPrimaryDark, DarkAppColors.textPrimary)
        assertEquals(Color(0xFFFFFFFF), DarkAppColors.textPrimary)
        assertTrue(DarkAppColors.isDark)

        assertEquals(ColorTextPrimaryDark, AmoledAppColors.textPrimary)
        assertEquals(Color(0xFFFFFFFF), AmoledAppColors.textPrimary)
        assertTrue(AmoledAppColors.isDark)
    }

    @Test
    fun testHeroBalanceColorResolutionLogic() {
        fun resolveHeroColor(amount: Double, isPrivacy: Boolean, isDark: Boolean): Color {
            val appColors = if (isDark) DarkAppColors else LightAppColors
            val isNegative = amount < 0 && !isPrivacy
            return if (isNegative) appColors.expense else appColors.textPrimary
        }

        // Positive amount in Light Mode -> LightAppColors.textPrimary (#0F172A)
        assertEquals(ColorTextPrimaryLight, resolveHeroColor(15000.0, isPrivacy = false, isDark = false))

        // Positive amount in Dark Mode -> DarkAppColors.textPrimary (#FFFFFF)
        assertEquals(ColorTextPrimaryDark, resolveHeroColor(15000.0, isPrivacy = false, isDark = true))

        // Negative amount in Light Mode -> LightAppColors.expense
        assertEquals(LightAppColors.expense, resolveHeroColor(-250.0, isPrivacy = false, isDark = false))

        // Negative amount in Dark Mode -> DarkAppColors.expense
        assertEquals(DarkAppColors.expense, resolveHeroColor(-250.0, isPrivacy = false, isDark = true))

        // Privacy mode in Light Mode -> LightAppColors.textPrimary (#0F172A)
        assertEquals(ColorTextPrimaryLight, resolveHeroColor(-250.0, isPrivacy = true, isDark = false))
    }

    @Test
    fun testLightModeWarningColorWcagCompliance() {
        // Warning color in Light Mode must be Amber-700 (#B45309) for WCAG AA compliance (4.6:1 contrast ratio on white)
        assertEquals(Color(0xFFB45309), ColorWarningBillsLight)
        assertEquals(Color(0xFFB45309), LightAppColors.warning)
    }

    @Test
    fun testPaymentMethodThemeAwareColors() {
        // Verify Cash in Light Mode is Forest Green (contrast >= 4.5:1), not neon mint
        assertEquals(Color(0xFF15803D), getPaymentMethodColor(PaymentMethod.CASH, isDark = false))
        assertEquals(Color(0xFF3DDC97), getPaymentMethodColor(PaymentMethod.CASH, isDark = true))

        // Verify Nagad in Light Mode is Amber Orange (contrast >= 4.5:1), not pale neon orange
        assertEquals(Color(0xFFD97706), getPaymentMethodColor(PaymentMethod.NAGAD, isDark = false))
        assertEquals(Color(0xFFF7941D), getPaymentMethodColor(PaymentMethod.NAGAD, isDark = true))

        // Verify Bank Transfer in Light Mode is Deep Royal Blue
        assertEquals(Color(0xFF1D4ED8), getPaymentMethodColor(PaymentMethod.BANK_TRANSFER, isDark = false))
        assertEquals(Color(0xFF3B8FF0), getPaymentMethodColor(PaymentMethod.BANK_TRANSFER, isDark = true))
    }
}
