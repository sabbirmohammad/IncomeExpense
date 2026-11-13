package com.example.expensetracker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.CategoryPaletteV7
import com.example.expensetracker.theme.ColorAccentContainerDark
import com.example.expensetracker.theme.ColorAccentContainerLight
import com.example.expensetracker.theme.ColorExpenseContainerDark
import com.example.expensetracker.theme.ColorExpenseContainerLight
import com.example.expensetracker.theme.ColorInfoContainerDark
import com.example.expensetracker.theme.ColorInfoContainerLight
import com.example.expensetracker.theme.ColorOnAccentDark
import com.example.expensetracker.theme.ColorOnAccentLight
import com.example.expensetracker.theme.ColorScrimDark
import com.example.expensetracker.theme.ColorScrimLight
import com.example.expensetracker.theme.ColorSurfaceRaisedDark
import com.example.expensetracker.theme.ColorSurfaceRaisedLight
import com.example.expensetracker.theme.ColorWarningContainerDark
import com.example.expensetracker.theme.ColorWarningContainerLight
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.LightAppColors
import com.example.expensetracker.theme.MotionConfig
import com.example.expensetracker.theme.RadiusL
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.theme.RadiusSheet
import com.example.expensetracker.theme.RadiusXS
import com.example.expensetracker.theme.Space12
import com.example.expensetracker.theme.Space16
import com.example.expensetracker.theme.Space20
import com.example.expensetracker.theme.Space24
import com.example.expensetracker.theme.Space32
import com.example.expensetracker.theme.Space4
import com.example.expensetracker.theme.Space48
import com.example.expensetracker.theme.Space8
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.Typography
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase1FoundationsV7Test {

    @Test
    fun testV7ColorTokensPresenceAndContrast() {
        // Dark tokens
        assertEquals(ColorSurfaceRaisedDark, DarkAppColors.surfaceRaised)
        assertEquals(ColorAccentContainerDark, DarkAppColors.accentContainer)
        assertEquals(ColorExpenseContainerDark, DarkAppColors.expenseContainer)
        assertEquals(ColorWarningContainerDark, DarkAppColors.warningContainer)
        assertEquals(ColorInfoContainerDark, DarkAppColors.infoContainer)
        assertEquals(ColorScrimDark, DarkAppColors.scrim)
        assertEquals(ColorOnAccentDark, DarkAppColors.onAccent)

        // Light tokens
        assertEquals(ColorSurfaceRaisedLight, LightAppColors.surfaceRaised)
        assertEquals(ColorAccentContainerLight, LightAppColors.accentContainer)
        assertEquals(ColorExpenseContainerLight, LightAppColors.expenseContainer)
        assertEquals(ColorWarningContainerLight, LightAppColors.warningContainer)
        assertEquals(ColorInfoContainerLight, LightAppColors.infoContainer)
        assertEquals(ColorScrimLight, LightAppColors.scrim)
        assertEquals(ColorOnAccentLight, LightAppColors.onAccent)

        // AMOLED tokens
        assertEquals(Color(0xFF0F131B), AmoledAppColors.surfaceRaised)
        assertEquals(Color(0xFF0B1F19), AmoledAppColors.accentContainer)
        assertEquals(Color(0xFF240E13), AmoledAppColors.expenseContainer)
        assertEquals(Color(0xFF241A0C), AmoledAppColors.warningContainer)
        assertEquals(Color(0xFF14162E), AmoledAppColors.infoContainer)

        // Focus ring token
        assertEquals(DarkAppColors.accent, DarkAppColors.focusRing)
        assertEquals(LightAppColors.accent, LightAppColors.focusRing)
        assertEquals(AmoledAppColors.accent, AmoledAppColors.focusRing)
    }

    @Test
    fun testV7CategoryPaletteCountAndColors() {
        assertEquals(12, CategoryPaletteV7.size)
        assertTrue(CategoryPaletteV7.contains(Color(0xFF10B981))) // Emerald
        assertTrue(CategoryPaletteV7.contains(Color(0xFFF43F5E))) // Terracotta Rose
        assertTrue(CategoryPaletteV7.contains(Color(0xFFF59E0B))) // Turmeric
        assertTrue(CategoryPaletteV7.contains(Color(0xFF6366F1))) // Iris Blue
    }

    @Test
    fun testV7SpacingGrid() {
        assertEquals(4.dp, Space4)
        assertEquals(8.dp, Space8)
        assertEquals(12.dp, Space12)
        assertEquals(16.dp, Space16)
        assertEquals(20.dp, Space20)
        assertEquals(24.dp, Space24)
        assertEquals(32.dp, Space32)
        assertEquals(48.dp, Space48)
    }

    @Test
    fun testV7RadiusTokens() {
        assertEquals(6.dp, RadiusXS)
        assertEquals(12.dp, RadiusS)
        assertEquals(16.dp, RadiusM)
        assertEquals(24.dp, RadiusL)
        assertEquals(24.dp, RadiusSheet)
    }

    @Test
    fun testMotionConfigRespectsReduceMotion() {
        val normalMotion = MotionConfig(reduceMotion = false)
        assertEquals(120, normalMotion.durationFast)
        assertEquals(220, normalMotion.durationStandard)
        assertEquals(340, normalMotion.durationEmphasized)

        val reducedMotion = MotionConfig(reduceMotion = true)
        assertEquals(0, reducedMotion.durationFast)
        assertEquals(0, reducedMotion.durationStandard)
        assertEquals(0, reducedMotion.durationEmphasized)
    }

    @Test
    fun testTypographyHasTabularFiguresAndBilingualMinimums() {
        // Tabular numerals verification
        assertEquals(TabularNumFeature, Typography.displayLarge.fontFeatureSettings)
        assertEquals(TabularNumFeature, Typography.displayMedium.fontFeatureSettings)
        assertEquals(TabularNumFeature, Typography.headlineLarge.fontFeatureSettings)
        assertEquals(TabularNumFeature, Typography.titleMedium.fontFeatureSettings)
        assertEquals(TabularNumFeature, Typography.bodySmall.fontFeatureSettings)

        // Bengali minimum font size >= 12sp and line height >= 16sp
        assertTrue(Typography.bodySmall.fontSize.value >= 12f)
        assertTrue(Typography.bodySmall.lineHeight.value >= 16f)
        assertTrue(Typography.labelMedium.fontSize.value >= 12f)
        assertTrue(Typography.labelMedium.lineHeight.value >= 16f)
        assertTrue(Typography.labelSmall.fontSize.value >= 12f)
        assertTrue(Typography.labelSmall.lineHeight.value >= 16f)
    }

    @Test
    fun testBengaliNumeralFormatting() {
        assertEquals("১২৩", convertToBengaliDigits("123"))
        assertEquals("৳ ১,২৫০", formatCurrency(1250.0, symbol = "৳", useBengaliNumerals = true))
        assertEquals("৳ 1,250", formatCurrency(1250.0, symbol = "৳", useBengaliNumerals = false))
    }

    @Test
    fun testV7TypographyDisplayAmountAndNumberKeypad() {
        assertEquals(40.sp, com.example.expensetracker.theme.StyleDisplayAmount.fontSize)
        assertEquals(46.sp, com.example.expensetracker.theme.StyleDisplayAmount.lineHeight)
        assertEquals(TabularNumFeature, com.example.expensetracker.theme.StyleDisplayAmount.fontFeatureSettings)

        assertEquals(22.sp, com.example.expensetracker.theme.StyleNumberKeypad.fontSize)
        assertEquals(28.sp, com.example.expensetracker.theme.StyleNumberKeypad.lineHeight)
        assertEquals(TabularNumFeature, com.example.expensetracker.theme.StyleNumberKeypad.fontFeatureSettings)
    }

    @Test
    fun testV7RadiusPillAndRowPadding() {
        assertEquals(999.dp, com.example.expensetracker.theme.RadiusPill)
        assertEquals(12.dp, com.example.expensetracker.theme.ListRowPaddingVertical)
    }

    @Test
    fun testV7Phase1ComponentsFilesPresence() {
        val componentsDir = java.io.File("app/src/main/java/com/example/expensetracker/ui/components")
        val altDir = java.io.File("src/main/java/com/example/expensetracker/ui/components")
        val targetDir = if (componentsDir.exists()) componentsDir else altDir

        assertTrue(java.io.File(targetDir, "AppNumericKeypad.kt").exists())
        assertTrue(java.io.File(targetDir, "AmountDisplay.kt").exists())
        assertTrue(java.io.File(targetDir, "StatTile.kt").exists())
        assertTrue(java.io.File(targetDir, "AppTopBar.kt").exists())
        assertTrue(java.io.File(targetDir, "StarterEmptyState.kt").exists())
        assertTrue(java.io.File(targetDir, "SegmentedControl.kt").exists())
        assertTrue(java.io.File(targetDir, "AppChip.kt").exists())
        assertTrue(java.io.File(targetDir, "SectionHeader.kt").exists())
        assertTrue(java.io.File(targetDir, "AppSnackbarHost.kt").exists())
    }
}
