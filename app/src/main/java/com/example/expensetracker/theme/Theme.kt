package com.example.expensetracker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val key: String) {
    SYSTEM("system"),
    DARK("dark"),
    LIGHT("light"),
    AMOLED("amoled");

    companion object {
        fun fromKey(key: String): AppThemeMode =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: DARK
    }
}

data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderDashed: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accent: Color,
    val navBorder: Color,
    val navInactive: Color,
    val isDark: Boolean
)

val DarkAppColors = AppColors(
    background = ColorBackground,
    surface = ColorSurface,
    surfaceVariant = ColorSurface,
    border = ColorBorder,
    borderDashed = ColorBorderDashed,
    textPrimary = ColorTextPrimary,
    textSecondary = ColorTextSecondary,
    textMuted = ColorTextMuted,
    accent = ColorAccent,
    navBorder = ColorNavBorder,
    navInactive = ColorNavInactive,
    isDark = true
)

val AmoledAppColors = AppColors(
    background = ColorBackgroundAmoled,
    surface = ColorSurfaceAmoled,
    surfaceVariant = ColorSurfaceAmoled,
    border = ColorBorderAmoled,
    borderDashed = ColorBorderDashedAmoled,
    textPrimary = ColorTextPrimary,
    textSecondary = ColorTextSecondary,
    textMuted = ColorTextMuted,
    accent = ColorAccent,
    navBorder = ColorBorderAmoled,
    navInactive = ColorNavInactive,
    isDark = true
)

val LightAppColors = AppColors(
    background = ColorBackgroundLight,
    surface = ColorSurfaceLight,
    surfaceVariant = ColorSurfaceSubtleLight,
    border = ColorBorderLight,
    borderDashed = ColorBorderDashedLight,
    textPrimary = ColorTextPrimaryLight,
    textSecondary = ColorTextSecondaryLight,
    textMuted = ColorTextMutedLight,
    accent = ColorAccentLight,
    navBorder = ColorBorderLight,
    navInactive = ColorTextMutedLight,
    isDark = false
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

private val AppDarkColorScheme = darkColorScheme(
    primary = ColorAccent,
    onPrimary = Color.White,
    primaryContainer = ColorAccent.copy(alpha = 0.15f),
    onPrimaryContainer = ColorAccent,
    secondary = ColorSuccessIncome,
    onSecondary = Color.White,
    tertiary = ColorDangerExpense,
    onTertiary = Color.White,
    background = ColorBackground,
    onBackground = ColorTextPrimary,
    surface = ColorSurface,
    onSurface = ColorTextPrimary,
    surfaceVariant = ColorSurface,
    onSurfaceVariant = ColorTextSecondary,
    outline = ColorBorder,
    outlineVariant = ColorBorderDashed
)

private val AppAmoledColorScheme = darkColorScheme(
    primary = ColorAccent,
    onPrimary = Color.White,
    primaryContainer = ColorAccent.copy(alpha = 0.15f),
    onPrimaryContainer = ColorAccent,
    secondary = ColorSuccessIncome,
    onSecondary = Color.White,
    tertiary = ColorDangerExpense,
    onTertiary = Color.White,
    background = ColorBackgroundAmoled,
    onBackground = ColorTextPrimary,
    surface = ColorSurfaceAmoled,
    onSurface = ColorTextPrimary,
    surfaceVariant = ColorSurfaceAmoled,
    onSurfaceVariant = ColorTextSecondary,
    outline = ColorBorderAmoled,
    outlineVariant = ColorBorderDashedAmoled
)

private val AppLightColorScheme = lightColorScheme(
    primary = ColorAccentLight,
    onPrimary = Color.White,
    primaryContainer = ColorAccentLight.copy(alpha = 0.12f),
    onPrimaryContainer = ColorAccentLight,
    secondary = ColorSuccessIncome,
    onSecondary = Color.White,
    tertiary = ColorDangerExpense,
    onTertiary = Color.White,
    background = ColorBackgroundLight,
    onBackground = ColorTextPrimaryLight,
    surface = ColorSurfaceLight,
    onSurface = ColorTextPrimaryLight,
    surfaceVariant = ColorSurfaceSubtleLight,
    onSurfaceVariant = ColorTextSecondaryLight,
    outline = ColorBorderLight,
    outlineVariant = ColorBorderDashedLight
)

@Composable
fun ExpenseTrackerTheme(
    themeMode: String = "dark",
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val mode = AppThemeMode.fromKey(themeMode)
    val (scheme, appColors) = when (mode) {
        AppThemeMode.LIGHT -> AppLightColorScheme to LightAppColors
        AppThemeMode.AMOLED -> AppAmoledColorScheme to AmoledAppColors
        AppThemeMode.SYSTEM -> if (systemInDark) AppDarkColorScheme to DarkAppColors else AppLightColorScheme to LightAppColors
        AppThemeMode.DARK -> AppDarkColorScheme to DarkAppColors
    }

    CompositionLocalProvider(
        LocalAppColors provides appColors
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}

