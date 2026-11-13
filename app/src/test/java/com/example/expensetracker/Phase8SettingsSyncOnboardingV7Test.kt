package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase8SettingsSyncOnboardingV7Test {

    private val basePath: String = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Target file ${file.path} must exist", file.exists())
        return file.readText()
    }

    @Test
    fun testSyncStatusCardInvariants() {
        val content = readFile("com/example/expensetracker/ui/components/SyncStatusCard.kt")

        assertTrue("SyncStatusCard must exist and display last sync time with tabular numbers", content.contains("fontFeatureSettings = \"tnum\""))
        assertTrue("SyncStatusCard Backup Now button must be >= 48dp", content.contains(".height(48.dp)"))
        assertTrue("SyncStatusCard Restore button must be >= 48dp", content.contains("restoreFromDrive") && content.contains(".height(48.dp)"))
        assertTrue("SyncStatusCard Connect button must be >= 48dp", content.contains("connectGoogleDrive") && content.contains(".height(48.dp)"))
        assertTrue("SyncStatusCard must support expandable error details", content.contains("errorMessage") && content.contains("syncErrorDetected"))
        assertTrue("SyncStatusCard must support email middle truncation", content.contains("truncateMiddle") && content.contains("showFullEmail"))
    }

    @Test
    fun testSettingsScreenNoToastForSync() {
        val content = readFile("com/example/expensetracker/ui/settings/SettingsScreen.kt")

        assertFalse("SettingsScreen must not use Toast for Drive sync results", content.contains("Toast.makeText"))
        assertTrue("SettingsScreen must host AppSnackbarHost", content.contains("AppSnackbarHost("))
        assertTrue("SettingsScreen must integrate SyncStatusCard", content.contains("SyncStatusCard("))
        assertTrue("SettingsScreen must include Appearance section", content.contains("SettingsThemeCard("))
        assertTrue("SettingsScreen must include Language & Numerals section", content.contains("SettingsBengaliNumeralsCard("))
        assertTrue("SettingsScreen must include Category Management section", content.contains("SettingsCategoriesCard("))
    }

    @Test
    fun testBengaliNumeralsSwitchContrastAndA11y() {
        val content = readFile("com/example/expensetracker/ui/settings/SettingsBengaliNumeralsCard.kt")

        assertTrue("SettingsBengaliNumeralsCard switch must include TalkBack stateDescription", content.contains("stateDescription ="))
        assertTrue("Switch track must have high contrast against surface", content.contains("uncheckedTrackColor = appColors.surfaceVariant"))
    }

    @Test
    fun testOnboardingScreen3ScreenSkim() {
        val content = readFile("com/example/expensetracker/ui/onboarding/OnboardingScreen.kt")

        assertTrue("Onboarding must have Track page", content.contains("onboardingTrackTitle"))
        assertTrue("Onboarding must have Plan page", content.contains("onboardingPlanTitle"))
        assertTrue("Onboarding must have Bazaar page", content.contains("onboardingBazaarTitle"))
        assertTrue("Skip button must have 48dp height", content.contains(".height(48.dp)") && content.contains("onboardingSkip"))
        assertTrue("Primary action button must have 48dp height", content.contains(".height(48.dp)") && content.contains("onboardingGetStarted"))
        assertTrue("Onboarding must have 3-dot page indicator", content.contains("dotWidth") && content.contains("pages.size"))
        assertTrue("Onboarding must respect reduceMotion", content.contains("motion.reduceMotion"))
    }

    @Test
    fun testOnboardingNavigationWiring() {
        val content = readFile("com/example/expensetracker/ui/navigation/AppNavigation.kt")

        assertTrue("AppNavigation must check isOnboardingCompleted", content.contains("isOnboardingCompleted"))
        assertTrue("AppNavigation must render OnboardingScreen", content.contains("OnboardingScreen("))
        assertTrue("AppNavigation must set onboarding completed upon finish", content.contains("setOnboardingCompleted(true)"))
    }

    @Test
    fun testSettingsProfileCardEmailMiddleTruncationAndEditTarget() {
        val content = readFile("com/example/expensetracker/ui/settings/SettingsProfileCard.kt")

        assertTrue("SettingsProfileCard must support email middle truncation", content.contains("showFullEmail"))
        assertTrue("SettingsProfileCard Edit button must ensure >= 48dp touch target", content.contains("minimumInteractiveComponentSize()"))
    }

    @Test
    fun testAppStringsBilingualOnboardingAndSync() {
        val content = readFile("com/example/expensetracker/ui/components/AppStrings.kt")

        assertTrue("AppStrings must define onboardingSkip", content.contains("onboardingSkip"))
        assertTrue("AppStrings must define onboardingNext", content.contains("onboardingNext"))
        assertTrue("AppStrings must define onboardingGetStarted", content.contains("onboardingGetStarted"))
        assertTrue("AppStrings must define onboardingTrackTitle", content.contains("onboardingTrackTitle"))
        assertTrue("AppStrings must define onboardingPlanTitle", content.contains("onboardingPlanTitle"))
        assertTrue("AppStrings must define onboardingBazaarTitle", content.contains("onboardingBazaarTitle"))
        assertTrue("AppStrings must define syncStatusTitle", content.contains("syncStatusTitle"))
        assertTrue("AppStrings must define syncAutoDailyTitle", content.contains("syncAutoDailyTitle"))
    }
}
