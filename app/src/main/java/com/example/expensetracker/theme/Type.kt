package com.example.expensetracker.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// =========================================================================
// TABULAR BILINGUAL TYPOGRAPHY SCALE ("The Living Ledger")
// =========================================================================

const val TabularNumFeature = "tnum"

// Explicit font weight tokens (400, 500, 600, 700)
val WeightRegular   = FontWeight.Normal   // 400
val WeightMedium    = FontWeight.Medium   // 500
val WeightSemiBold  = FontWeight.SemiBold // 600
val WeightBold      = FontWeight.Bold     // 700

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightBold, // 700
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumFeature
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightSemiBold, // 600
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightSemiBold, // 600
        fontSize = 18.sp, // Screen title: 18sp / 600
        lineHeight = 24.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightSemiBold, // 600
        fontSize = 15.sp, // Card value: 15sp / 600
        lineHeight = 22.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightMedium, // 500
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightRegular, // 400
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightRegular, // 400
        fontSize = 13.5.sp, // Body / list text: 13.5sp / 400
        lineHeight = 19.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightRegular, // 400
        fontSize = 12.sp, // Screen subtitle: 12sp / 400
        lineHeight = 17.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightSemiBold, // 600
        fontSize = 12.sp, // Minimum safe readability size for Bengali
        lineHeight = 16.sp,
        fontFeatureSettings = TabularNumFeature
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = WeightMedium, // 500
        fontSize = 12.sp, // Constrained to >= 12sp and line height >= 16sp for Bengali metadata
        lineHeight = 16.sp,
        fontFeatureSettings = TabularNumFeature
    )
)

val LivingTypography = Typography

