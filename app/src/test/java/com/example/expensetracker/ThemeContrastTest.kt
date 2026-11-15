package com.example.expensetracker

import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.AppThemeMode
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.LightAppColors
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure behavioral test verifying status-bar icon contrast calculation follows the APP theme,
 * not system theme, ensuring readability across Light, Dark, and AMOLED modes.
 */
class ThemeContrastTest {

    // Pure contrast resolver matching Theme.kt:108
    private fun resolveLightStatusBars(mode: AppThemeMode, systemInDark: Boolean): Boolean {
        val appColors = when (mode) {
            AppThemeMode.LIGHT -> LightAppColors
            AppThemeMode.AMOLED -> AmoledAppColors
            AppThemeMode.SYSTEM -> if (systemInDark) DarkAppColors else LightAppColors
            AppThemeMode.DARK -> DarkAppColors
        }
        // isAppearanceLightStatusBars = true means dark icons (for light backgrounds)
        // isAppearanceLightStatusBars = false means light icons (for dark backgrounds)
        return !appColors.isDark
    }

    @Test
    fun testAppDarkOnSystemLightHasReadableLightIcons() {
        // App is Dark, System is Light -> status bar background is dark -> icons MUST be light/white (isAppearanceLightStatusBars = false)
        val lightBars = resolveLightStatusBars(AppThemeMode.DARK, systemInDark = false)
        assertFalse("App Dark mode must request light/white status bar icons even if system is light", lightBars)
    }

    @Test
    fun testAppLightOnSystemDarkHasReadableDarkIcons() {
        // App is Light, System is Dark -> status bar background is light -> icons MUST be dark/black (isAppearanceLightStatusBars = true)
        val lightBars = resolveLightStatusBars(AppThemeMode.LIGHT, systemInDark = true)
        assertTrue("App Light mode must request dark status bar icons even if system is dark", lightBars)
    }

    @Test
    fun testAmoledModeHasReadableLightIcons() {
        val lightBars = resolveLightStatusBars(AppThemeMode.AMOLED, systemInDark = false)
        assertFalse("AMOLED mode must request white status bar icons on pitch-black background", lightBars)
    }

    @Test
    fun testSystemModeFollowsSystemTheme() {
        val whenSystemDark = resolveLightStatusBars(AppThemeMode.SYSTEM, systemInDark = true)
        assertFalse(whenSystemDark)

        val whenSystemLight = resolveLightStatusBars(AppThemeMode.SYSTEM, systemInDark = false)
        assertTrue(whenSystemLight)
    }
}
