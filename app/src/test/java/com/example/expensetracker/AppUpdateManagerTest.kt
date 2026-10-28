package com.example.expensetracker

import com.example.expensetracker.common.AppConstants
import com.example.expensetracker.data.update.AppUpdateManager
import com.example.expensetracker.data.update.UpdateState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdateManagerTest {

    @Test
    fun testIsNewerVersion_whenRemoteIsHigherMajor_returnsTrue() {
        assertTrue(AppUpdateManager.isNewerVersion(current = "5.0", remote = "6.0"))
        assertTrue(AppUpdateManager.isNewerVersion(current = "v5.0", remote = "v6.0"))
    }

    @Test
    fun testIsNewerVersion_whenRemoteIsHigherMinor_returnsTrue() {
        assertTrue(AppUpdateManager.isNewerVersion(current = "5.0", remote = "5.1"))
        assertTrue(AppUpdateManager.isNewerVersion(current = "v5.0", remote = "v5.1"))
        assertTrue(AppUpdateManager.isNewerVersion(current = "5.0.0", remote = "5.0.1"))
    }

    @Test
    fun testIsNewerVersion_whenRemoteIsSame_returnsFalse() {
        assertFalse(AppUpdateManager.isNewerVersion(current = "5.0", remote = "5.0"))
        assertFalse(AppUpdateManager.isNewerVersion(current = "v5.0", remote = "5.0"))
        assertFalse(AppUpdateManager.isNewerVersion(current = "5.0", remote = "v5.0"))
        assertFalse(AppUpdateManager.isNewerVersion(current = "5.0.0", remote = "5.0"))
    }

    @Test
    fun testIsNewerVersion_whenRemoteIsLower_returnsFalse() {
        assertFalse(AppUpdateManager.isNewerVersion(current = "5.1", remote = "5.0"))
        assertFalse(AppUpdateManager.isNewerVersion(current = "v5.0.1", remote = "v5.0.0"))
    }

    @Test
    fun testIsNewerVersion_withDifferentFormats() {
        assertTrue(AppUpdateManager.isNewerVersion(current = "v5.6", remote = "v5.7"))
        assertFalse(AppUpdateManager.isNewerVersion(current = "v5.7", remote = "v5.7"))
        assertFalse(AppUpdateManager.isNewerVersion(current = "5.7", remote = "v5.7"))
        assertTrue(AppUpdateManager.isNewerVersion(current = "5.7", remote = "5.7.1"))
    }

    @Test
    fun testAppConstantsVersionSync() {
        val appVersion = AppConstants.APP_VERSION
        assertTrue("APP_VERSION should start with v prefix", appVersion.startsWith("v"))
        assertEquals(appVersion, AppConstants.getAppVersion(null))
        assertEquals(appVersion.removePrefix("v"), AppConstants.getAppVersionClean(null))
    }

    @Test
    fun testIsNewerVersion_withVersionProgressions() {
        // Lower versions see current as newer
        assertTrue(AppUpdateManager.isNewerVersion(current = "6.0", remote = AppConstants.APP_VERSION))
        assertTrue(AppUpdateManager.isNewerVersion(current = "5.0", remote = AppConstants.APP_VERSION))

        // Same version is not newer
        assertFalse(AppUpdateManager.isNewerVersion(current = AppConstants.getAppVersionClean(null), remote = AppConstants.APP_VERSION))

        // Higher major or minor is recognized as newer
        assertTrue(AppUpdateManager.isNewerVersion(current = "6.4", remote = "6.5"))
        assertTrue(AppUpdateManager.isNewerVersion(current = "6.5", remote = "7.0"))
        assertTrue(AppUpdateManager.isNewerVersion(current = AppConstants.getAppVersionClean(null), remote = "99.0"))
    }

    @Test
    fun testUpdateStateUpToDate() {
        val state = UpdateState.UpToDate("6.4")
        assertEquals("6.4", state.currentVersion)
    }

    @Test
    fun testUpdateStateErrorRateLimit() {
        val state = UpdateState.Error("RATE_LIMIT", isRateLimited = true)
        assertEquals("RATE_LIMIT", state.message)
        assertTrue(state.isRateLimited)
    }
}
