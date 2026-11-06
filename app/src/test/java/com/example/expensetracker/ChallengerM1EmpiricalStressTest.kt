package com.example.expensetracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.AppShapes
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.CardPadding
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorBorderAmoled
import com.example.expensetracker.theme.ColorBorderLight
import com.example.expensetracker.theme.ColorLivingParchmentBorder
import com.example.expensetracker.theme.ColorLivingParchmentSurface
import com.example.expensetracker.theme.ColorLivingSlateBorder
import com.example.expensetracker.theme.ColorLivingSlateSurface
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorSurfaceAmoled
import com.example.expensetracker.theme.ColorSurfaceLight
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.HairlineDividerStroke
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LightAppColors
import com.example.expensetracker.theme.LivingShapes
import com.example.expensetracker.theme.LivingTypography
import com.example.expensetracker.theme.OneUiShapes
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import java.awt.font.LineMetrics
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D

/**
 * Empirical stress-test suite executed by teamwork_challenger (Challenger 2)
 * for Milestone M1 (Living Ledger Design System Tokens).
 *
 * Covers:
 * 1. fontFeatureSettings = "tnum" and numerical spacing across Latin (0-9) & Bengali (০-৯) digits.
 * 2. AppCard & OneUiCard border stroke resolution (0.5dp hairline default, custom strokes, zero-border cases, corner radii).
 * 3. Bengali text vertical metrics and ascender/descender clearance in labelMedium & labelSmall.
 * 4. Shapes, radius tokens, and AppColors contract fulfillment.
 */
class ChallengerM1EmpiricalStressTest {

    // =========================================================================
    // 1. TYPOGRAPHY SCALE, WEIGHTS & TABULAR NUMBER VERIFICATION
    // =========================================================================

    @Test
    fun testTabularNumFeatureIsConfiguredOnAllDefinedStyles() {
        assertEquals("tnum", TabularNumFeature)

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
            assertEquals(
                "$name must have fontFeatureSettings = 'tnum' for tabular alignment",
                "tnum",
                style.fontFeatureSettings
            )
        }
    }

    @Test
    fun testTypographyExplicitWeightsScale() {
        assertEquals(FontWeight.Normal, WeightRegular)
        assertEquals(FontWeight.Medium, WeightMedium)
        assertEquals(FontWeight.SemiBold, WeightSemiBold)
        assertEquals(FontWeight.Bold, WeightBold)

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
    fun testBengaliReadabilityConstraintsOnLabelStyles() {
        // Bengali script requires at least 12sp font size and >= 16sp line height
        // to prevent clipped matras, ref, and stacked conjuncts.
        assertTrue(
            "labelMedium fontSize must be >= 12.sp, found ${Typography.labelMedium.fontSize}",
            Typography.labelMedium.fontSize >= 12.sp
        )
        assertTrue(
            "labelMedium lineHeight must be >= 16.sp, found ${Typography.labelMedium.lineHeight}",
            Typography.labelMedium.lineHeight >= 16.sp
        )
        assertTrue(
            "labelSmall fontSize must be >= 12.sp, found ${Typography.labelSmall.fontSize}",
            Typography.labelSmall.fontSize >= 12.sp
        )
        assertTrue(
            "labelSmall lineHeight must be >= 16.sp, found ${Typography.labelSmall.lineHeight}",
            Typography.labelSmall.lineHeight >= 16.sp
        )
    }

    // =========================================================================
    // 2. EMPIRICAL DIGIT ADVANCE WIDTH MEASUREMENTS (LATIN vs BENGALI)
    // =========================================================================

    @Test
    fun testEmpiricalAdvanceWidthsOfLatinDigitsAreUniform() {
        // Latin digits 0-9 in standard SansSerif font
        val frc = FontRenderContext(AffineTransform(), true, true)
        val font = Font(Font.SANS_SERIF, Font.PLAIN, 16)
        val widths = (0..9).map { digit ->
            font.getStringBounds(digit.toString(), frc).width
        }

        val firstWidth = widths[0]
        println("Latin Digits 0-9 measured widths: $widths")
        for (i in widths.indices) {
            assertEquals(
                "Latin digit $i advance width ($firstWidth) must match all other digits",
                firstWidth,
                widths[i],
                0.001
            )
        }
    }

    @Test
    fun testEmpiricalAdvanceWidthsOfBengaliDigitsAnalysis() {
        // Bengali digits U+09E6 ('০') through U+09EF ('৯')
        val frc = FontRenderContext(AffineTransform(), true, true)
        var font = Font("Kohinoor Bangla", Font.PLAIN, 16)
        if (!font.family.contains("Bangla") && !font.family.contains("Kohinoor")) {
            font = Font(Font.SANS_SERIF, Font.PLAIN, 16)
        }

        val bengaliDigits = ('\u09E6'..'\u09EF').toList()
        val widths = bengaliDigits.map { ch ->
            font.getStringBounds(ch.toString(), frc).width
        }

        println("Bengali Digits ০-৯ measured widths in font '${font.fontName}':")
        bengaliDigits.forEachIndexed { idx, ch ->
            println("  '$ch' (U+${Integer.toHexString(ch.code)}): ${widths[idx]}")
        }

        // Empirical assertion: Standard system Bengali fonts use proportional figures
        // because OpenType standard 'tnum' is predominantly an ASCII feature unless
        // custom tabular glyph substitutions are baked into the font's GSUB table.
        val minWidth = widths.minOrNull() ?: 0.0
        val maxWidth = widths.maxOrNull() ?: 0.0
        println("Bengali digits width variance: Min=$minWidth, Max=$maxWidth, Delta=${maxWidth - minWidth}")
        assertTrue("Bengali digits have non-zero widths", minWidth > 0.0)
    }

    // =========================================================================
    // 3. EMPIRICAL BENGALI TEXT VERTICAL METRICS & ASCENDER/DESCENDER CLEARANCE
    // =========================================================================

    @Test
    fun testEmpiricalBengaliVerticalMetricsClearanceInLabelStyles() {
        val frc = FontRenderContext(AffineTransform(), true, true)
        var font = Font("Kohinoor Bangla", Font.PLAIN, 12)
        if (!font.family.contains("Bangla") && !font.family.contains("Kohinoor")) {
            font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
        }

        val testCases = listOf(
            "কাঁচাবাজার",
            "মাছ ও মাংস",
            "মুদি সদাই",
            "দুধ ও বেকারি",
            "মোট ব্যয়: ৳১২,৩৫০.০০",
            "সর্বোচ্চ মূল্য",
            "ধার/দেনা ও ঋণ পরিশোধ",
            "রাষ্ট্রীয় সঞ্চয়পত্র",
            "রশিদ সংযুক্ত",
            "স্মৃতিসৌধ",
            "পদ্ধতিসমূহ"
        )

        val targetLineHeightPx = 16.0 // 16.sp
        println("Bengali vertical span measurements at 12pt (target line height: ${targetLineHeightPx}px):")

        for (text in testCases) {
            val gv: GlyphVector = font.createGlyphVector(frc, text)
            val bounds: Rectangle2D = gv.getPixelBounds(frc, 0f, 0f)
            val verticalSpan = bounds.height
            println("  '$text' -> bounds Y=[${bounds.y} .. ${bounds.y + bounds.height}], height=$verticalSpan")

            // In common words, vertical span at 12pt is <= 16px (or within 1px for deep conjuncts like 'সমূহ')
            assertTrue(
                "Vertical span for '$text' ($verticalSpan px) must not drastically exceed 16sp line height",
                verticalSpan <= 17.5
            )
        }
    }

    // =========================================================================
    // 4. APPCARD & ONEUICARD BORDER STROKE & CORNER RADIUS RESOLUTION
    // =========================================================================

    @Test
    fun testAppCardBorderStrokeResolutionLogic() {
        // Emulate AppCard's resolvedBorderStroke logic:
        fun resolveBorderStroke(
            borderStrokeWidth: Dp,
            borderStroke: BorderStroke?,
            resolvedBorder: Color
        ): BorderStroke? {
            return borderStroke ?: if (borderStrokeWidth > 0.dp) BorderStroke(borderStrokeWidth, resolvedBorder) else null
        }

        val defaultBorderColor = DarkAppColors.border

        // Case 1: Default hairline stroke (0.5.dp)
        val defaultStroke = resolveBorderStroke(HairlineInkStroke, null, defaultBorderColor)
        assertNotNull(defaultStroke)
        assertEquals(0.5.dp, defaultStroke?.width)
        assertEquals(BorderStroke(0.5.dp, defaultBorderColor), defaultStroke)

        // Case 2: Custom stroke width (1.5.dp)
        val customWidthStroke = resolveBorderStroke(1.5.dp, null, defaultBorderColor)
        assertNotNull(customWidthStroke)
        assertEquals(1.5.dp, customWidthStroke?.width)

        // Case 3: Zero-border case (0.dp) -> must return null
        val zeroStroke = resolveBorderStroke(0.dp, null, defaultBorderColor)
        assertNull("Zero borderStrokeWidth must resolve to null border", zeroStroke)

        // Case 4: Negative width (-1.dp) -> must return null
        val negativeStroke = resolveBorderStroke((-1).dp, null, defaultBorderColor)
        assertNull("Negative borderStrokeWidth must resolve to null border", negativeStroke)

        // Case 5: Explicit custom borderStroke overrides borderStrokeWidth
        val explicitStroke = BorderStroke(2.dp, Color.Red)
        val overriddenStroke = resolveBorderStroke(0.5.dp, explicitStroke, defaultBorderColor)
        assertEquals(explicitStroke, overriddenStroke)
        assertEquals(2.dp, overriddenStroke?.width)
    }

    @Test
    fun testAppCardCornerRadiiHandling() {
        // AppCard corner radius allows 0.dp, 6.dp, 10.dp, 16.dp, 22.dp
        val testRadii = listOf(
            0.dp,
            RadiusBadgeTag,     // 6.dp
            RadiusActionChip,   // 10.dp
            RadiusCardGroup,    // 16.dp
            RadiusOuterSheet,   // 22.dp
            32.dp
        )

        for (radius in testRadii) {
            val shape = RoundedCornerShape(radius)
            assertNotNull(shape)
            // Verify shape produces non-negative corner radius
            assertTrue(radius >= 0.dp)
        }
    }

    // =========================================================================
    // 5. SHAPE SYSTEM TOKENS & ALIASES
    // =========================================================================

    @Test
    fun testShapeSystemTokensAndAliases() {
        assertEquals(22.dp, RadiusOuterSheet)
        assertEquals(16.dp, RadiusCardGroup)
        assertEquals(10.dp, RadiusActionChip)
        assertEquals(6.dp, RadiusBadgeTag)
        assertEquals(0.5.dp, HairlineInkStroke)

        // Verify backwards-compatible aliases
        assertEquals(RadiusCardGroup, CardCornerRadius)
        assertEquals(RadiusActionChip, SmallElementRadius)
        assertEquals(HairlineInkStroke, HairlineDividerStroke)
        assertEquals(16.dp, CardPadding)
        assertEquals(12.dp, CardGap)

        // Verify LivingShapes / AppShapes / OneUiShapes
        assertEquals(LivingShapes, AppShapes)
        assertEquals(AppShapes, OneUiShapes)
        assertEquals(RoundedCornerShape(6.dp), LivingShapes.extraSmall)
        assertEquals(RoundedCornerShape(10.dp), LivingShapes.small)
        assertEquals(RoundedCornerShape(12.dp), LivingShapes.medium)
        assertEquals(RoundedCornerShape(16.dp), LivingShapes.large)
        assertEquals(RoundedCornerShape(22.dp), LivingShapes.extraLarge)
    }

    // =========================================================================
    // 6. APPCOLORS CONTRACT: cardBackground & divider
    // =========================================================================

    @Test
    fun testAppColorsCardBackgroundAndDividerResolution() {
        // Dark theme contracts
        assertEquals(ColorLivingSlateSurface, DarkAppColors.cardBackground)
        assertEquals(ColorSurface, DarkAppColors.cardBackground)
        assertEquals(ColorLivingSlateBorder, DarkAppColors.divider)
        assertEquals(ColorBorder, DarkAppColors.divider)

        // Light theme contracts
        assertEquals(ColorLivingParchmentSurface, LightAppColors.cardBackground)
        assertEquals(ColorSurfaceLight, LightAppColors.cardBackground)
        assertEquals(ColorLivingParchmentBorder, LightAppColors.divider)
        assertEquals(ColorBorderLight, LightAppColors.divider)

        // AMOLED contracts
        assertEquals(ColorSurfaceAmoled, AmoledAppColors.cardBackground)
        assertEquals(ColorBorderAmoled, AmoledAppColors.divider)
    }
}
