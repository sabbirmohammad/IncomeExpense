package com.example.expensetracker.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R

// =========================================================================
// TABULAR BILINGUAL TYPOGRAPHY SCALE ("Refined Ledger v7")
// =========================================================================

const val TabularNumFeature = "tnum"

// Explicit font weight tokens (400, 500, 600, 700)
val WeightRegular   = FontWeight.Normal   // 400
val WeightMedium    = FontWeight.Medium   // 500
val WeightSemiBold  = FontWeight.SemiBold // 600
val WeightBold      = FontWeight.Bold     // 700

// Bundled Offline Font Families (Inter for Latin/Digits, Hind Siliguri for Bengali)
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val HindSiliguriFontFamily = FontFamily(
    Font(R.font.hind_siliguri_regular, FontWeight.Normal),
    Font(R.font.hind_siliguri_medium, FontWeight.Medium),
    Font(R.font.hind_siliguri_semibold, FontWeight.SemiBold),
    Font(R.font.hind_siliguri_bold, FontWeight.Bold)
)

val AppFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.hind_siliguri_regular, FontWeight.Normal),
    Font(R.font.hind_siliguri_medium, FontWeight.Medium),
    Font(R.font.hind_siliguri_semibold, FontWeight.SemiBold),
    Font(R.font.hind_siliguri_bold, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightBold, // 700
        fontSize = 40.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature
    ),
    displayMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightBold, // 700
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightBold, // 700
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold, // 600
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold, // 600
        fontSize = 20.sp, // Sheet titles, section hero
        lineHeight = 26.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold, // 600
        fontSize = 16.sp, // Card titles, list primary text
        lineHeight = 22.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightMedium, // 500
        fontSize = 14.sp, // Row titles
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightRegular, // 400
        fontSize = 16.sp, // Inputs, notes
        lineHeight = 24.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightRegular, // 400
        fontSize = 14.sp, // Secondary text
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightRegular, // 400
        fontSize = 12.sp, // Meta, timestamps (min safe size for Bengali >= 12sp)
        lineHeight = 17.sp, // Line-height >= 16sp
        fontFeatureSettings = TabularNumFeature
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold, // 600
        fontSize = 14.sp, // Buttons
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightSemiBold, // 600
        fontSize = 12.sp, // Chips, badges (min safe size for Bengali >= 12sp)
        lineHeight = 16.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = WeightMedium, // 500
        fontSize = 12.sp, // Constrained to >= 12sp and line height >= 16sp for Bengali metadata
        lineHeight = 16.sp,
        fontFeatureSettings = TabularNumFeature
    )
)

val LivingTypography = Typography
