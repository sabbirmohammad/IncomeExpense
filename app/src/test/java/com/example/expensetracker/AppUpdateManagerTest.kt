package com.example.expensetracker

import com.example.expensetracker.data.update.AppUpdateManager
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
}
