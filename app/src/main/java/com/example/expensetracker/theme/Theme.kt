package com.example.expensetracker.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    val income: Color = ColorSuccessIncome,
    val expense: Color = ColorDangerExpense,
    val warning: Color = ColorWarningBills,
    val info: Color = ColorInfoTransport,
    val neutral: Color = ColorNeutralCategory,
    val navBorder: Color = ColorNavBorder,
    val navInactive: Color = ColorNavInactive,
    val isDark: Boolean = true,
    val cardBackground: Color = surface,
    val divider: Color = border,
    // V7 tokens ("Refined Ledger")
    val surfaceRaised: Color = if (isDark) ColorSurfaceRaisedDark else ColorSurfaceRaisedLight,
    val accentContainer: Color = if (isDark) ColorAccentContainerDark else ColorAccentContainerLight,
    val expenseContainer: Color = if (isDark) ColorExpenseContainerDark else ColorExpenseContainerLight,
    val warningContainer: Color = if (isDark) ColorWarningContainerDark else ColorWarningContainerLight,
    val infoContainer: Color = if (isDark) ColorInfoContainerDark else ColorInfoContainerLight,
    val scrim: Color = if (isDark) ColorScrimDark else ColorScrimLight,
    val onAccent: Color = if (isDark) ColorOnAccentDark else ColorOnAccentLight
) {
    val successIncome: Color get() = income
    val dangerExpense: Color get() = expense
    val warningBills: Color get() = warning
    val infoTransport: Color get() = info
}

val DarkAppColors = AppColors(
    background = ColorBackground,
    surface = ColorSurface,
    surfaceVariant = ColorSurfaceVariant,
    border = ColorBorder,
    borderDashed = ColorBorderDashed,
    textPrimary = ColorTextPrimary,
    textSecondary = ColorLivingTextSecondaryDark,
    textMuted = ColorLivingTextMutedDark,
    accent = ColorAccent,
    income = ColorSuccessIncome,
    expense = ColorDangerExpense,
    warning = ColorWarningBills,
    info = ColorInfoTransport,
    neutral = ColorNeutralCategory,
    navBorder = ColorNavBorder,
    navInactive = ColorNavInactive,
    isDark = true,
    cardBackground = ColorSurface,
    divider = ColorBorder,
    surfaceRaised = ColorSurfaceRaisedDark,
    accentContainer = ColorAccentContainerDark,
    expenseContainer = ColorExpenseContainerDark,
    warningContainer = ColorWarningContainerDark,
    infoContainer = ColorInfoContainerDark,
    scrim = ColorScrimDark,
    onAccent = ColorOnAccentDark
)

val AmoledAppColors = AppColors(
    background = ColorBackgroundAmoled,
    surface = ColorSurfaceAmoled,
    surfaceVariant = ColorSurfaceVariantAmoled,
    border = ColorBorderAmoled,
    borderDashed = ColorBorderDashedAmoled,
    textPrimary = ColorTextPrimary,
    textSecondary = ColorLivingTextSecondaryDark,
    textMuted = ColorLivingTextMutedDark,
    accent = ColorAccent,
    income = ColorSuccessIncome,
    expense = ColorDangerExpense,
    warning = ColorWarningBills,
    info = ColorInfoTransport,
    neutral = ColorNeutralCategory,
    navBorder = ColorBorderAmoled,
    navInactive = ColorNavInactive,
    isDark = true,
    cardBackground = ColorSurfaceAmoled,
    divider = ColorBorderAmoled,
    surfaceRaised = ColorSurfaceRaisedAmoled,
    accentContainer = ColorAccentContainerAmoled,
    expenseContainer = ColorExpenseContainerAmoled,
    warningContainer = ColorWarningContainerAmoled,
    infoContainer = ColorInfoContainerAmoled,
    scrim = ColorScrimAmoled,
    onAccent = ColorOnAccentAmoled
)

val LightAppColors = AppColors(
    background = ColorBackgroundLight,
    surface = ColorSurfaceLight,
    surfaceVariant = ColorSurfaceSubtleLight,
    border = ColorBorderLight,
    borderDashed = ColorBorderDashedLight,
    textPrimary = ColorTextPrimaryLight,
    textSecondary = ColorLivingTextSecondaryLight,
    textMuted = ColorLivingTextMutedLight,
    accent = ColorAccentLight,
    income = ColorSuccessIncomeLight,
    expense = ColorDangerExpenseLight,
    warning = ColorWarningBillsLight,
    info = ColorInfoTransportLight,
    neutral = ColorNeutralCategoryLight,
    navBorder = ColorBorderLight,
    navInactive = ColorTextMutedLight,
    isDark = false,
    cardBackground = ColorSurfaceLight,
    divider = ColorBorderLight,
    surfaceRaised = ColorSurfaceRaisedLight,
    accentContainer = ColorAccentContainerLight,
    expenseContainer = ColorExpenseContainerLight,
    warningContainer = ColorWarningContainerLight,
    infoContainer = ColorInfoContainerLight,
    scrim = ColorScrimLight,
    onAccent = ColorOnAccentLight
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
    surfaceVariant = ColorSurfaceVariant,
    onSurfaceVariant = ColorLivingTextSecondaryDark,
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
    surfaceVariant = ColorSurfaceVariantAmoled,
    onSurfaceVariant = ColorLivingTextSecondaryDark,
    outline = ColorBorderAmoled,
    outlineVariant = ColorBorderDashedAmoled
)

private val AppLightColorScheme = lightColorScheme(
    primary = ColorAccentLight,
    onPrimary = Color.White,
    primaryContainer = ColorAccentLight.copy(alpha = 0.12f),
    onPrimaryContainer = ColorAccentLight,
    secondary = ColorSuccessIncomeLight,
    onSecondary = Color.White,
    tertiary = ColorDangerExpenseLight,
    onTertiary = Color.White,
    background = ColorBackgroundLight,
    onBackground = ColorTextPrimaryLight,
    surface = ColorSurfaceLight,
    onSurface = ColorTextPrimaryLight,
    surfaceVariant = ColorSurfaceSubtleLight,
    onSurfaceVariant = ColorLivingTextSecondaryLight,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !appColors.isDark
                insetsController.isAppearanceLightNavigationBars = !appColors.isDark
            }
        }
    }

    val motionConfig = rememberSystemMotionConfig()

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppSpacing provides AppSpacing(),
        LocalMotion provides motionConfig
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}
