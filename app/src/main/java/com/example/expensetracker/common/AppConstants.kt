package com.example.expensetracker.common

import android.content.Context

/**
 * Global constants for the Expense Tracker application.
 */
object AppConstants {
    const val APP_VERSION = "v6.8.1"
    const val APP_NAME = "Expense Tracker"

    /**
     * Resolves the actual runtime application version name from the Android PackageManager.
     * Falls back to [APP_VERSION] if Context is unavailable or reading fails.
     */
    fun getAppVersion(context: Context? = null): String {
        if (context == null) return APP_VERSION
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val name = pInfo.versionName
            if (!name.isNullOrBlank()) {
                if (name.startsWith("v", ignoreCase = true)) name else "v$name"
            } else {
                APP_VERSION
            }
        } catch (_: Exception) {
            APP_VERSION
        }
    }

    /**
     * Returns the version string without any leading 'v' or 'V' prefix.
     */
    fun getAppVersionClean(context: Context? = null): String {
        return getAppVersion(context).removePrefix("v").removePrefix("V")
    }
}
