package com.example.expensetracker.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R

// =========================================================================
// TABULAR BILINGUAL TYPOGRAPHY SCALE (Pulse Design System)
// =========================================================================

const val TabularNumFeature = "tnum"

val WeightRegular   = FontWeight.Normal   // 400
val WeightMedium    = FontWeight.Medium   // 500
val WeightSemiBold  = FontWeight.SemiBold // 600
val WeightBold      = FontWeight.Bold     // 700

// Bundled Offline Font Families
// IMPORTANT: Hind Siliguri MUST be first for Bengali glyphs; Inter is Latin/digit fallback (tnum).
val HindSiliguriFontFamily = FontFamily(
    Font(R.font.hind_siliguri_regular, FontWeight.Normal),
    Font(R.font.hind_siliguri_medium, FontWeight.Medium),
    Font(R.font.hind_siliguri_semibold, FontWeight.SemiBold),
    Font(R.font.hind_siliguri_bold, FontWeight.Bold)
)

val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val AppFontFamily = FontFamily(
    Font(R.font.hind_siliguri_regular, FontWeight.Normal),
    Font(R.font.hind_siliguri_medium, FontWeight.Medium),
    Font(R.font.hind_siliguri_semibold, FontWeight.SemiBold),
    Font(R.font.hind_siliguri_bold, FontWeight.Bold),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

// Extra Pulse Specific Typography Styles
val TextStyleDisplayHero = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = WeightBold,
    fontSize = 44.sp,
    lineHeight = 52.sp,
    letterSpacing = (-0.5).sp,
    fontFeatureSettings = TabularNumFeature,
    color = Color.Unspecified
)

val TextStyleDisplayAmount = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = WeightBold,
    fontSize = 40.sp,
    lineHeight = 48.sp,
    letterSpacing = (-0.5).sp,
    fontFeatureSettings = TabularNumFeature,
    color = Color.Unspecified
)

val TextStyleNumberKeypad = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = WeightMedium,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    fontFeatureSettings = TabularNumFeature,
    color = Color.Unspecified
)

// Legacy Aliases
val StyleDisplayHero = TextStyleDisplayHero
val StyleDisplayAmount = TextStyleDisplayAmount
val StyleNumberKeypad = TextStyleNumberKeypad

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightBold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    displayMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightBold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    displaySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightBold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightMedium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightRegular,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightRegular,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightRegular,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightMedium,
        fontSize = 12.sp, // Pulse minimum 12sp anywhere
        lineHeight = 16.sp,
        fontFeatureSettings = TabularNumFeature,
        color = Color.Unspecified
    )
)

val LivingTypography = Typography
