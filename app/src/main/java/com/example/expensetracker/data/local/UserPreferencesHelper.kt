package com.example.expensetracker.data.local

import android.content.Context

internal class UserPreferencesHelper(private val context: Context) {

    fun isPrivacyModeEnabled(): Boolean {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getBoolean(PREF_KEY_PRIVACY_MODE, false)
        } catch (_: Exception) {
            false
        }
    }

    fun setPrivacyModeEnabled(enabled: Boolean) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_KEY_PRIVACY_MODE, enabled).apply()
        } catch (_: Exception) {}
    }

    fun getLastBudgetPromptKey(): String? {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getString(PREF_KEY_LAST_BUDGET_PROMPT, null)
        } catch (_: Exception) {
            null
        }
    }

    fun setLastBudgetPromptKey(key: String) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(PREF_KEY_LAST_BUDGET_PROMPT, key).apply()
        } catch (_: Exception) {}
    }

    fun getLastBudgetNotifiedKey(): String? {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getString(PREF_KEY_LAST_BUDGET_NOTIFIED, null)
        } catch (_: Exception) {
            null
        }
    }

    fun setLastBudgetNotifiedKey(key: String) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(PREF_KEY_LAST_BUDGET_NOTIFIED, key).apply()
        } catch (_: Exception) {}
    }
}
