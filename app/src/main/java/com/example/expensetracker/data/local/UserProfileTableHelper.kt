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
                val themeCol = c.getColumnIndex(COL_PROFILE_THEME)
                val appTheme = if (themeCol != -1 && !c.isNull(themeCol)) c.getString(themeCol) else "dark"
                val bengaliNumCol = c.getColumnIndex(COL_PROFILE_BENGALI_NUMERALS)
                val useBengaliNumerals = if (bengaliNumCol != -1 && !c.isNull(bengaliNumCol)) c.getInt(bengaliNumCol) == 1 else (appLang == "bn")

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
                    appTheme = appTheme,
                    useBengaliNumerals = useBengaliNumerals,
                    isPrivacyModeEnabled = dbHelper.isPrivacyModeEnabled()
                )
            }
        }
        return UserProfile(isPrivacyModeEnabled = dbHelper.isPrivacyModeEnabled())
    }

    fun updateUserProfile(profile: UserProfile): Int {
        val db = dbHelper.writableDatabase
        dbHelper.setPrivacyModeEnabled(profile.isPrivacyModeEnabled)

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
