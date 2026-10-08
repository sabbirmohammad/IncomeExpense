package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.UserProfile

internal class UserProfileTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getUserProfile(): UserProfile {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_PROFILE, null, "$COL_PROFILE_ID = 1", null, null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                val emailCol = c.getColumnIndexOrThrow(COL_PROFILE_GOOGLE_EMAIL)
                val email = if (c.isNull(emailCol)) null else c.getString(emailCol)
                val rawName = c.getString(c.getColumnIndexOrThrow(COL_PROFILE_NAME))
                val firstNameCol = c.getColumnIndex(COL_PROFILE_FIRST_NAME)
                val lastNameCol = c.getColumnIndex(COL_PROFILE_LAST_NAME)
                val firstName = if (firstNameCol != -1 && !c.isNull(firstNameCol)) {
                    val fn = c.getString(firstNameCol)
                    if (fn.isNotBlank()) fn else (rawName.split(Regex("\\s+")).firstOrNull() ?: rawName)
                } else {
                    rawName.split(Regex("\\s+")).firstOrNull() ?: rawName
                }
                val lastName = if (lastNameCol != -1 && !c.isNull(lastNameCol)) {
                    c.getString(lastNameCol)
                } else {
                    val parts = rawName.split(Regex("\\s+"), limit = 2)
                    if (parts.size > 1) parts[1] else ""
                }
                val langCol = c.getColumnIndex(COL_PROFILE_LANGUAGE)
                val appLang = if (langCol != -1 && !c.isNull(langCol)) c.getString(langCol) else "bn"
                val keyCol = c.getColumnIndex(COL_PROFILE_GEMINI_KEY)
                var geminiKey = if (keyCol != -1 && !c.isNull(keyCol)) c.getString(keyCol)?.trim()?.ifBlank { null } else null
                val aiInsightsCol = c.getColumnIndex(COL_PROFILE_AI_INSIGHTS_ENABLED)
                val aiInsights = if (aiInsightsCol != -1 && !c.isNull(aiInsightsCol)) c.getInt(aiInsightsCol) == 1 else true
                val themeCol = c.getColumnIndex(COL_PROFILE_THEME)
                val appTheme = if (themeCol != -1 && !c.isNull(themeCol)) c.getString(themeCol) else "dark"
                val bengaliNumCol = c.getColumnIndex(COL_PROFILE_BENGALI_NUMERALS)
                val useBengaliNumerals = if (bengaliNumCol != -1 && !c.isNull(bengaliNumCol)) c.getInt(bengaliNumCol) == 1 else (appLang == "bn")

                if (geminiKey.isNullOrBlank()) {
                    val fallbackKey = dbHelper.getPersistedApiKey()
                    if (!fallbackKey.isNullOrBlank()) {
                        geminiKey = fallbackKey
                        try {
                            val healCv = ContentValues().apply {
                                put(COL_PROFILE_GEMINI_KEY, fallbackKey)
                            }
                            dbHelper.writableDatabase.update(TABLE_PROFILE, healCv, "$COL_PROFILE_ID = 1", null)
                        } catch (_: Exception) {}
                    }
                } else {
                    dbHelper.persistApiKey(geminiKey)
                }

                return UserProfile(
                    firstName = firstName.ifBlank { "Sabbir" },
                    lastName = lastName,
                    name = rawName,
                    currencyCode = c.getString(c.getColumnIndexOrThrow(COL_PROFILE_CURRENCY_CODE)),
                    currencySymbol = c.getString(c.getColumnIndexOrThrow(COL_PROFILE_CURRENCY_SYMBOL)),
                    avatarIndex = c.getInt(c.getColumnIndexOrThrow(COL_PROFILE_AVATAR)),
                    googleAccountEmail = email,
                    isAutoSyncEnabled = c.getInt(c.getColumnIndexOrThrow(COL_PROFILE_AUTO_SYNC)) == 1,
                    lastSyncMillis = c.getLong(c.getColumnIndexOrThrow(COL_PROFILE_LAST_SYNC)),
                    appLanguage = appLang,
                    geminiApiKey = geminiKey,
                    aiInsightsEnabled = aiInsights,
                    appTheme = appTheme,
                    useBengaliNumerals = useBengaliNumerals,
                    isPrivacyModeEnabled = dbHelper.isPrivacyModeEnabled()
                )
            }
        }
        val fallbackKey = dbHelper.getPersistedApiKey()
        return UserProfile(geminiApiKey = fallbackKey, isPrivacyModeEnabled = dbHelper.isPrivacyModeEnabled())
    }

    fun updateUserProfile(profile: UserProfile): Int {
        val db = dbHelper.writableDatabase
        dbHelper.setPrivacyModeEnabled(profile.isPrivacyModeEnabled)
        val fallbackKey = dbHelper.getPersistedApiKey()
        val effectiveApiKey = profile.geminiApiKey?.trim()?.ifBlank { null } ?: fallbackKey

        if (!effectiveApiKey.isNullOrBlank()) {
            dbHelper.persistApiKey(effectiveApiKey)
        }

        val cv = ContentValues().apply {
            put(COL_PROFILE_NAME, profile.name)
            put(COL_PROFILE_FIRST_NAME, profile.firstName)
            put(COL_PROFILE_LAST_NAME, profile.lastName)
            put(COL_PROFILE_CURRENCY_CODE, profile.currencyCode)
            put(COL_PROFILE_CURRENCY_SYMBOL, profile.currencySymbol)
            put(COL_PROFILE_AVATAR, profile.avatarIndex)
            if (profile.googleAccountEmail != null) {
                put(COL_PROFILE_GOOGLE_EMAIL, profile.googleAccountEmail)
            } else {
                putNull(COL_PROFILE_GOOGLE_EMAIL)
            }
            put(COL_PROFILE_AUTO_SYNC, if (profile.isAutoSyncEnabled) 1 else 0)
            put(COL_PROFILE_LAST_SYNC, profile.lastSyncMillis)
            put(COL_PROFILE_LANGUAGE, profile.appLanguage)
            if (effectiveApiKey != null) {
                put(COL_PROFILE_GEMINI_KEY, effectiveApiKey)
            } else {
                putNull(COL_PROFILE_GEMINI_KEY)
            }
            put(COL_PROFILE_AI_INSIGHTS_ENABLED, if (profile.aiInsightsEnabled) 1 else 0)
            put(COL_PROFILE_THEME, profile.appTheme)
            put(COL_PROFILE_BENGALI_NUMERALS, if (profile.useBengaliNumerals) 1 else 0)
        }
        var rows = db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
        if (rows == 0) {
            cv.put(COL_PROFILE_ID, 1)
            val insertId = db.insertWithOnConflict(TABLE_PROFILE, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            if (insertId != -1L) rows = 1
        }
        try {
            val cur = db.rawQuery("PRAGMA wal_checkpoint(FULL)", null)
            cur.close()
        } catch (_: Exception) {}
        return rows
    }

    fun updateProfileLanguage(languageCode: String): Int {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_LANGUAGE, languageCode)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }

    fun updateGeminiApiKey(key: String?): Int {
        val cleanKey = key?.trim()?.ifBlank { null }
        dbHelper.persistApiKey(cleanKey)

        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            if (cleanKey != null) put(COL_PROFILE_GEMINI_KEY, cleanKey) else putNull(COL_PROFILE_GEMINI_KEY)
        }
        var rows = db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
        if (rows == 0) {
            val insertCv = ContentValues().apply {
                put(COL_PROFILE_ID, 1)
                put(COL_PROFILE_NAME, "Sabbir")
                put(COL_PROFILE_FIRST_NAME, "Sabbir")
                put(COL_PROFILE_LAST_NAME, "")
                put(COL_PROFILE_CURRENCY_CODE, "BDT")
                put(COL_PROFILE_CURRENCY_SYMBOL, "৳")
                put(COL_PROFILE_AVATAR, 0)
                put(COL_PROFILE_AUTO_SYNC, 1)
                put(COL_PROFILE_LAST_SYNC, 0L)
                put(COL_PROFILE_LANGUAGE, "bn")
                if (cleanKey != null) put(COL_PROFILE_GEMINI_KEY, cleanKey) else putNull(COL_PROFILE_GEMINI_KEY)
                put(COL_PROFILE_AI_INSIGHTS_ENABLED, 1)
            }
            val insertId = db.insertWithOnConflict(TABLE_PROFILE, null, insertCv, SQLiteDatabase.CONFLICT_REPLACE)
            if (insertId != -1L) rows = 1
        }
        try {
            val cur = db.rawQuery("PRAGMA wal_checkpoint(FULL)", null)
            cur.close()
        } catch (_: Exception) {}
        return rows
    }

    fun updateAiInsightsEnabled(enabled: Boolean): Int {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_AI_INSIGHTS_ENABLED, if (enabled) 1 else 0)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }

    fun updateProfileTheme(theme: String): Int {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_THEME, theme)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }

    fun updateProfileBengaliNumerals(enabled: Boolean): Int {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_BENGALI_NUMERALS, if (enabled) 1 else 0)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }
}
