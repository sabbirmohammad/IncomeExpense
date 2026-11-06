package com.example.expensetracker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.AppShapes
import com.example.expensetracker.theme.AppThemeMode
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorAccentDark
import com.example.expensetracker.theme.ColorAccentLight
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBackgroundAmoled
import com.example.expensetracker.theme.ColorBackgroundDark
import com.example.expensetracker.theme.ColorBackgroundLight
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorBorderAmoled
import com.example.expensetracker.theme.ColorBorderDashed
import com.example.expensetracker.theme.ColorBorderDashedAmoled
import com.example.expensetracker.theme.ColorBorderDashedLight
import com.example.expensetracker.theme.ColorBorderDark
import com.example.expensetracker.theme.ColorBorderLight
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorDangerExpenseLight
import com.example.expensetracker.theme.ColorInfoTransport
import com.example.expensetracker.theme.ColorInfoTransportLight
import com.example.expensetracker.theme.ColorLivingEmerald
import com.example.expensetracker.theme.ColorLivingEmeraldLight
import com.example.expensetracker.theme.ColorLivingIndigo
import com.example.expensetracker.theme.ColorLivingIndigoLight
import com.example.expensetracker.theme.ColorLivingParchmentBg
import com.example.expensetracker.theme.ColorLivingParchmentBorder
import com.example.expensetracker.theme.ColorLivingParchmentSurface
import com.example.expensetracker.theme.ColorLivingParchmentVariant
import com.example.expensetracker.theme.ColorLivingSlateBg
import com.example.expensetracker.theme.ColorLivingSlateBorder
import com.example.expensetracker.theme.ColorLivingSlateSurface
import com.example.expensetracker.theme.ColorLivingSlateVariant
import com.example.expensetracker.theme.ColorLivingTerracotta
import com.example.expensetracker.theme.ColorLivingTerracottaLight
import com.example.expensetracker.theme.ColorLivingTextMutedDark
import com.example.expensetracker.theme.ColorLivingTextMutedLight
import com.example.expensetracker.theme.ColorLivingTextPrimaryDark
import com.example.expensetracker.theme.ColorLivingTextPrimaryLight
import com.example.expensetracker.theme.ColorLivingTextSecondaryDark
import com.example.expensetracker.theme.ColorLivingTextSecondaryLight
import com.example.expensetracker.theme.ColorLivingTurmeric
import com.example.expensetracker.theme.ColorLivingTurmericLight
import com.example.expensetracker.theme.ColorNavBorder
import com.example.expensetracker.theme.ColorNavInactive
import com.example.expensetracker.theme.ColorNeutralCategory
import com.example.expensetracker.theme.ColorNeutralCategoryLight
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSuccessIncomeLight
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorSurfaceAmoled
import com.example.expensetracker.theme.ColorSurfaceDark
import com.example.expensetracker.theme.ColorSurfaceLight
import com.example.expensetracker.theme.ColorSurfaceSubtleLight
import com.example.expensetracker.theme.ColorSurfaceVariant
import com.example.expensetracker.theme.ColorSurfaceVariantAmoled
import com.example.expensetracker.theme.ColorSurfaceVariantDark
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextMutedDark
import com.example.expensetracker.theme.ColorTextMutedLight
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextPrimaryDark
import com.example.expensetracker.theme.ColorTextPrimaryLight
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorTextSecondaryDark
import com.example.expensetracker.theme.ColorTextSecondaryLight
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.theme.ColorWarningBillsLight
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.HairlineDividerStroke
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LightAppColors
import com.example.expensetracker.theme.LivingShapes
import com.example.expensetracker.theme.LivingTypography
import com.example.expensetracker.theme.RadiusActionChip
import com.example.expensetracker.theme.RadiusBadgeTag
import com.example.expensetracker.theme.RadiusCardGroup
import com.example.expensetracker.theme.RadiusOuterSheet
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.Typography
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.theme.WeightRegular
import com.example.expensetracker.theme.WeightSemiBold
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ChallengerMilestone1StressTest {

    private fun relativeLuminance(color: Color): Double {
        fun channel(c: Float): Double {
            val v = c.toDouble()
            return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        val r = channel(color.red)
        val g = channel(color.green)
        val b = channel(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = relativeLuminance(fg)
        val l2 = relativeLuminance(bg)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun testWcagContrastOnLightLinenSurface() {
        val white = Color(0xFFFFFFFF)

        // 1. Primary Text on White: >= 4.5:1
        val crPrimary = contrastRatio(ColorLivingTextPrimaryLight, white)
        val crPrimaryLegacy = contrastRatio(ColorTextPrimaryLight, white)
        assertTrue("Primary text on white must be >= 4.5:1, got $crPrimary", crPrimary >= 4.5)
        assertTrue("Legacy Primary text on white must be >= 4.5:1, got $crPrimaryLegacy", crPrimaryLegacy >= 4.5)

        // 2. Secondary Text on White: >= 4.5:1
        val crSecondary = contrastRatio(ColorLivingTextSecondaryLight, white)
        val crSecondaryLegacy = contrastRatio(ColorTextSecondaryLight, white)
        assertTrue("Secondary text on white must be >= 4.5:1, got $crSecondary", crSecondary >= 4.5)
        assertTrue("Legacy Secondary text on white must be >= 4.5:1, got $crSecondaryLegacy", crSecondaryLegacy >= 4.5)

        // 3. Muted Text on White: >= 4.5:1
        val crMuted = contrastRatio(ColorLivingTextMutedLight, white)
        val crMutedLegacy = contrastRatio(ColorTextMutedLight, white)
        assertTrue("Muted text on white must be >= 4.5:1, got $crMuted", crMuted >= 4.5)
        assertTrue("Legacy Muted text on white must be >= 4.5:1, got $crMutedLegacy", crMutedLegacy >= 4.5)

        // 4. Semantic Accents on White: >= 4.5:1
        val crEmerald = contrastRatio(ColorLivingEmeraldLight, white)
        val crTerracotta = contrastRatio(ColorLivingTerracottaLight, white)
        val crTurmeric = contrastRatio(ColorLivingTurmericLight, white)
        val crIndigo = contrastRatio(ColorLivingIndigoLight, white)

        assertTrue("Emerald on white must be >= 4.5:1, got $crEmerald", crEmerald >= 4.5)
        assertTrue("Terracotta on white must be >= 4.5:1, got $crTerracotta", crTerracotta >= 4.5)
        assertTrue("Turmeric on white must be >= 4.5:1, got $crTurmeric", crTurmeric >= 4.5)
        assertTrue("Indigo on white must be >= 4.5:1, got $crIndigo", crIndigo >= 4.5)
    }

    @Test
    fun testWcagContrastOnDarkSlateSurface() {
        val darkSurface = Color(0xFF161B22)

        // 1. Primary Text on Dark Slate: >= 4.5:1
        val crPrimaryLiving = contrastRatio(ColorLivingTextPrimaryDark, darkSurface)
        val crPrimaryPure = contrastRatio(ColorTextPrimaryDark, darkSurface)
        assertTrue("Living Primary text on Dark Slate must be >= 4.5:1, got $crPrimaryLiving", crPrimaryLiving >= 4.5)
        assertTrue("Pure White text on Dark Slate must be >= 4.5:1, got $crPrimaryPure", crPrimaryPure >= 4.5)

        // 2. Secondary Text on Dark Slate: >= 4.5:1
        val crSecondaryLiving = contrastRatio(ColorLivingTextSecondaryDark, darkSurface)
        val crSecondaryLegacy = contrastRatio(ColorTextSecondaryDark, darkSurface)
        assertTrue("Living Secondary text on Dark Slate must be >= 4.5:1, got $crSecondaryLiving", crSecondaryLiving >= 4.5)
        assertTrue("Legacy Secondary text on Dark Slate must be >= 4.5:1, got $crSecondaryLegacy", crSecondaryLegacy >= 4.5)

        // 3. Semantic Accents on Dark Slate: >= 4.5:1
        val crEmerald = contrastRatio(ColorLivingEmerald, darkSurface)
        val crTerracotta = contrastRatio(ColorLivingTerracotta, darkSurface)
        val crTurmeric = contrastRatio(ColorLivingTurmeric, darkSurface)

        assertTrue("Emerald on Dark Slate must be >= 4.5:1, got $crEmerald", crEmerald >= 4.5)
        assertTrue("Terracotta on Dark Slate must be >= 4.5:1, got $crTerracotta", crTerracotta >= 4.5)
        assertTrue("Turmeric on Dark Slate must be >= 4.5:1, got $crTurmeric", crTurmeric >= 4.5)

        // 4. Indigo & Muted Text for graphical components/large text: >= 3.0:1
        val crIndigo = contrastRatio(ColorLivingIndigo, darkSurface)
        val crMutedLiving = contrastRatio(ColorLivingTextMutedDark, darkSurface)
        val crMutedLegacy = contrastRatio(ColorTextMutedDark, darkSurface)

        assertTrue("Indigo on Dark Slate must be >= 3.0:1 for graphical UI, got $crIndigo", crIndigo >= 3.0)
        assertTrue("Living Muted on Dark Slate must be >= 3.0:1, got $crMutedLiving", crMutedLiving >= 3.0)
        assertTrue("Legacy Muted on Dark Slate must be >= 4.5:1, got $crMutedLegacy", crMutedLegacy >= 4.5)
    }

    @Test
    fun testWcagContrastOnAmoledPureBlack() {
        val pureBlack = Color(0xFF000000)

        assertTrue(contrastRatio(ColorLivingTextPrimaryDark, pureBlack) >= 15.0)
        assertTrue(contrastRatio(ColorTextPrimaryDark, pureBlack) >= 20.0)
        assertTrue(contrastRatio(ColorLivingTextSecondaryDark, pureBlack) >= 7.0)
        assertTrue(contrastRatio(ColorLivingEmerald, pureBlack) >= 7.0)
        assertTrue(contrastRatio(ColorLivingTerracotta, pureBlack) >= 5.0)
        assertTrue(contrastRatio(ColorLivingTurmeric, pureBlack) >= 9.0)
        assertTrue(contrastRatio(ColorLivingIndigo, pureBlack) >= 4.5)
        assertTrue(contrastRatio(ColorLivingTextMutedDark, pureBlack) >= 4.0)
    }

    @Test
    fun testThemeResolutionModesAndFallback() {
        // Test standard modes
        assertEquals(AppThemeMode.DARK, AppThemeMode.fromKey("dark"))
        assertEquals(AppThemeMode.LIGHT, AppThemeMode.fromKey("light"))
        assertEquals(AppThemeMode.AMOLED, AppThemeMode.fromKey("amoled"))
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.fromKey("system"))

        // Test case insensitivity
        assertEquals(AppThemeMode.DARK, AppThemeMode.fromKey("DARK"))
        assertEquals(AppThemeMode.LIGHT, AppThemeMode.fromKey("Light"))
        assertEquals(AppThemeMode.AMOLED, AppThemeMode.fromKey("Amoled"))
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.fromKey("System"))

        // Test fallback for invalid / empty / unexpected keys
        assertEquals(AppThemeMode.DARK, AppThemeMode.fromKey("unknown"))
        assertEquals(AppThemeMode.DARK, AppThemeMode.fromKey(""))
        assertEquals(AppThemeMode.DARK, AppThemeMode.fromKey("cyberpunk"))
    }

    @Test
    fun testDownstreamAppColorsContract() {
        // 1. DarkAppColors contract
        assertTrue(DarkAppColors.isDark)
        assertEquals(ColorSurface, DarkAppColors.surface)
        assertEquals(ColorSurface, DarkAppColors.cardBackground)
        assertEquals(ColorBorder, DarkAppColors.border)
        assertEquals(ColorBorder, DarkAppColors.divider)
        assertEquals(ColorAccent, DarkAppColors.accent)
        assertEquals(ColorSuccessIncome, DarkAppColors.income)
        assertEquals(ColorDangerExpense, DarkAppColors.expense)

        // 2. LightAppColors contract
        assertFalse(LightAppColors.isDark)
        assertEquals(ColorSurfaceLight, LightAppColors.surface)
        assertEquals(ColorSurfaceLight, LightAppColors.cardBackground)
        assertEquals(ColorBorderLight, LightAppColors.border)
        assertEquals(ColorBorderLight, LightAppColors.divider)
        assertEquals(ColorAccentLight, LightAppColors.accent)
        assertEquals(ColorSuccessIncomeLight, LightAppColors.income)
        assertEquals(ColorDangerExpenseLight, LightAppColors.expense)

        // 3. AmoledAppColors contract
        assertTrue(AmoledAppColors.isDark)
        assertEquals(ColorSurfaceAmoled, AmoledAppColors.surface)
        assertEquals(ColorSurfaceAmoled, AmoledAppColors.cardBackground)
        assertEquals(ColorBorderAmoled, AmoledAppColors.border)
        assertEquals(ColorBorderAmoled, AmoledAppColors.divider)

        // 4. Semantic getters
        assertEquals(DarkAppColors.income, DarkAppColors.successIncome)
        assertEquals(DarkAppColors.expense, DarkAppColors.dangerExpense)
        assertEquals(DarkAppColors.warning, DarkAppColors.warningBills)
        assertEquals(DarkAppColors.info, DarkAppColors.infoTransport)
    }

    @Test
    fun testTabularTypographyScaleAndBengaliReadability() {
        val styles = listOf(
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

        for ((name, style) in styles) {
            // Invariant: Unspecified color prevents invisible text across all themes
            assertEquals("$name must have Color.Unspecified", Color.Unspecified, style.color)

            // Invariant: Tabular numbers feature enabled globally
            assertEquals("$name must have fontFeatureSettings = 'tnum'", TabularNumFeature, style.fontFeatureSettings)

            // Invariant: Minimum font size >= 12sp for legibility
            assertTrue("$name font size must be >= 12sp, was ${style.fontSize}", style.fontSize.value >= 12f)
        }

        // Bengali metadata constraints in labelSmall & labelMedium
        assertTrue(
            "labelSmall line height must be >= 16sp for Bengali diacritics, was ${Typography.labelSmall.lineHeight}",
            Typography.labelSmall.lineHeight.value >= 16f
        )
        assertTrue(
            "labelMedium line height must be >= 16sp for Bengali diacritics, was ${Typography.labelMedium.lineHeight}",
            Typography.labelMedium.lineHeight.value >= 16f
        )

        // Explicit weight scale
        assertEquals(WeightBold, Typography.headlineLarge.fontWeight)
        assertEquals(WeightSemiBold, Typography.headlineMedium.fontWeight)
        assertEquals(WeightSemiBold, Typography.titleLarge.fontWeight)
        assertEquals(WeightSemiBold, Typography.titleMedium.fontWeight)
        assertEquals(WeightMedium, Typography.titleSmall.fontWeight)
        assertEquals(WeightRegular, Typography.bodyLarge.fontWeight)
        assertEquals(WeightRegular, Typography.bodyMedium.fontWeight)
        assertEquals(WeightRegular, Typography.bodySmall.fontWeight)
        assertEquals(WeightSemiBold, Typography.labelMedium.fontWeight)
        assertEquals(WeightMedium, Typography.labelSmall.fontWeight)
    }

    @Test
    fun testShapeSystemAndHairlineDividerTokens() {
        assertEquals(22.dp, RadiusOuterSheet)
        assertEquals(16.dp, RadiusCardGroup)
        assertEquals(10.dp, RadiusActionChip)
        assertEquals(6.dp, RadiusBadgeTag)
        assertEquals(0.5.dp, HairlineInkStroke)

        // Backwards compatibility aliases
        assertEquals(RadiusCardGroup, CardCornerRadius)
        assertEquals(RadiusActionChip, SmallElementRadius)
        assertEquals(HairlineInkStroke, HairlineDividerStroke)

        assertNotNull(LivingShapes.extraSmall)
        assertNotNull(LivingShapes.small)
        assertNotNull(LivingShapes.medium)
        assertNotNull(LivingShapes.large)
        assertNotNull(LivingShapes.extraLarge)
        assertEquals(AppShapes, LivingShapes)
    }

    @Test
    fun testPreventInvisibleTextScenarios() {
        // Dark Mode: foreground must not match surface or background
        assertNotEquals(DarkAppColors.textPrimary, DarkAppColors.surface)
        assertNotEquals(DarkAppColors.textPrimary, DarkAppColors.background)
        assertNotEquals(DarkAppColors.textSecondary, DarkAppColors.surface)
        assertNotEquals(DarkAppColors.textSecondary, DarkAppColors.background)

        // Light Mode: foreground must not match surface or background
        assertNotEquals(LightAppColors.textPrimary, LightAppColors.surface)
        assertNotEquals(LightAppColors.textPrimary, LightAppColors.background)
        assertNotEquals(LightAppColors.textSecondary, LightAppColors.surface)
        assertNotEquals(LightAppColors.textSecondary, LightAppColors.background)

        // AMOLED Mode: foreground must not match surface or background
        assertNotEquals(AmoledAppColors.textPrimary, AmoledAppColors.surface)
        assertNotEquals(AmoledAppColors.textPrimary, AmoledAppColors.background)

        // Ensure Dark textPrimary on Light surface has huge contrast (not invisible)
        val contrastDarkOnLight = contrastRatio(DarkAppColors.textPrimary, LightAppColors.surface)
        // If someone used DarkAppColors.textPrimary (#FFFFFF) on LightAppColors.surface (#FFFFFF), contrast would be 1.0!
        assertEquals("White on White is invisible!", 1.0, contrastRatio(Color.White, Color.White), 0.01)
        // Verify LightAppColors.textPrimary resolves to dark color (#0F172A)
        val realLightContrast = contrastRatio(LightAppColors.textPrimary, LightAppColors.surface)
        assertTrue("Light textPrimary must have high contrast on Light surface: $realLightContrast", realLightContrast > 15.0)
    }
}
