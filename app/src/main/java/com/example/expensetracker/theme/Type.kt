package com.example.expensetracker.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Typography specification: system sans-serif, two weights only — 400 regular, 500 medium.
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontFeatureSettings = "tnum",
        color = ColorTextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontFeatureSettings = "tnum",
        color = ColorTextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 18.sp, // Screen title: 18px / 500
        lineHeight = 24.sp,
        fontFeatureSettings = "tnum",
        color = ColorTextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 16.sp, // Card value: 15-20px / 500
        lineHeight = 22.sp,
        fontFeatureSettings = "tnum",
        color = ColorTextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFeatureSettings = "tnum",
        color = ColorTextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = ColorTextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 13.sp, // Body / list text: 13px / 400
        lineHeight = 18.sp,
        color = ColorTextPrimary
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 12.sp, // Screen subtitle: 12px / 400
        lineHeight = 16.sp,
        color = ColorTextSecondary
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = ColorTextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 11.sp, // Caption / meta: 11px / 400
        lineHeight = 14.sp,
        color = ColorTextMuted
    )
)

