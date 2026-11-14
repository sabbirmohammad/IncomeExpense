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

    fun isOnboardingCompleted(): Boolean {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getBoolean(PREF_KEY_ONBOARDING_COMPLETED, false)
        } catch (_: Exception) {
            false
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_KEY_ONBOARDING_COMPLETED, completed).apply()
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

    fun getMarketListBudget(listId: Long): Double? {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val key = "market_list_budget_$listId"
            if (prefs.contains(key)) {
                val value = prefs.getFloat(key, -1f)
                if (value >= 0f) value.toDouble() else null
            } else null
        } catch (_: Exception) {
            null
        }
    }

    fun setMarketListBudget(listId: Long, budget: Double?) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val key = "market_list_budget_$listId"
            if (budget != null && budget >= 0.0) {
                prefs.edit().putFloat(key, budget.toFloat()).apply()
            } else {
                prefs.edit().remove(key).apply()
            }
        } catch (_: Exception) {}
    }

    fun deleteMarketListBudget(listId: Long) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().remove("market_list_budget_$listId").apply()
        } catch (_: Exception) {}
    }
}
