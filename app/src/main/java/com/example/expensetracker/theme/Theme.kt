package com.example.expensetracker.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.expensetracker.ui.adaptive.LocalWindowClass
import com.example.expensetracker.ui.adaptive.rememberWindowClass

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

private val PulseDarkColorScheme = darkColorScheme(
    primary = ColorPulseAccentDark,
    onPrimary = ColorPulseOnAccentDark,
    primaryContainer = ColorPulseAccentDark.copy(alpha = 0.18f),
    onPrimaryContainer = ColorPulseAccentDark,
    secondary = ColorPulseIncomeDark,
    onSecondary = Color(0xFF04130D),
    tertiary = ColorPulseExpenseDark,
    onTertiary = Color(0xFF2C0A0E),
    background = ColorPulseBackgroundDark,
    onBackground = ColorPulseTextPrimaryDark,
    surface = ColorPulseSurfaceDark,
    onSurface = ColorPulseTextPrimaryDark,
    surfaceVariant = ColorPulseSurfaceVariantDark,
    onSurfaceVariant = ColorPulseTextSecondaryDark,
    outline = ColorPulseBorderDark,
    outlineVariant = ColorPulseBorderDashedDark
)

private val PulseAmoledColorScheme = darkColorScheme(
    primary = ColorPulseAccentDark,
    onPrimary = ColorPulseOnAccentDark,
    primaryContainer = ColorPulseAccentDark.copy(alpha = 0.18f),
    onPrimaryContainer = ColorPulseAccentDark,
    secondary = ColorPulseIncomeDark,
    onSecondary = Color(0xFF04130D),
    tertiary = ColorPulseExpenseDark,
    onTertiary = Color(0xFF2C0A0E),
    background = ColorPulseBackgroundAmoled,
    onBackground = Color(0xFFFFFFFF),
    surface = ColorPulseSurfaceAmoled,
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = ColorPulseSurfaceVariantAmoled,
    onSurfaceVariant = ColorPulseTextSecondaryDark,
    outline = ColorPulseBorderAmoled,
    outlineVariant = ColorPulseBorderDashedAmoled
)

private val PulseLightColorScheme = lightColorScheme(
    primary = ColorPulseAccentLight,
    onPrimary = ColorPulseOnAccentLight,
    primaryContainer = ColorPulseAccentLight.copy(alpha = 0.12f),
    onPrimaryContainer = ColorPulseAccentLight,
    secondary = ColorPulseIncomeLight,
    onSecondary = Color.White,
    tertiary = ColorPulseExpenseLight,
    onTertiary = Color.White,
    background = ColorPulseBackgroundLight,
    onBackground = ColorPulseTextPrimaryLight,
    surface = ColorPulseSurfaceLight,
    onSurface = ColorPulseTextPrimaryLight,
    surfaceVariant = ColorPulseSurfaceVariantLight,
    onSurfaceVariant = ColorPulseTextSecondaryLight,
    outline = ColorPulseBorderLight,
    outlineVariant = ColorPulseBorderDashedLight
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
        AppThemeMode.LIGHT -> PulseLightColorScheme to LightAppColors
        AppThemeMode.AMOLED -> PulseAmoledColorScheme to AmoledAppColors
        AppThemeMode.SYSTEM -> if (systemInDark) PulseDarkColorScheme to DarkAppColors else PulseLightColorScheme to LightAppColors
        AppThemeMode.DARK -> PulseDarkColorScheme to DarkAppColors
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
    val windowClass = rememberWindowClass()

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppSpacing provides AppSpacing(),
        LocalMotion provides motionConfig,
        LocalReduceMotion provides motionConfig.reduceMotion,
        LocalWindowClass provides windowClass
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            shapes = AppShapes
        ) {
            CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides appColors.textPrimary,
                content = content
            )
        }
    }
}
