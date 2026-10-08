package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ExpenseRepository.updateProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateUserProfile(profile)
    if (rows > 0) {
        refreshUserProfile()
        triggerAutoSync()
    }
    rows > 0
}

suspend fun ExpenseRepository.setAppLanguage(languageCode: String): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateProfileLanguage(languageCode)
    if (rows > 0) refreshUserProfile()
    rows > 0
}

suspend fun ExpenseRepository.setGeminiApiKey(key: String?): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateGeminiApiKey(key?.trim()?.ifBlank { null })
    if (rows > 0) refreshUserProfile()
    rows > 0
}

suspend fun ExpenseRepository.setAiInsightsEnabled(enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateAiInsightsEnabled(enabled)
    if (rows > 0) refreshUserProfile()
    rows > 0
}

suspend fun ExpenseRepository.setAppTheme(theme: String): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateProfileTheme(theme)
    if (rows > 0) refreshUserProfile()
    rows > 0
}

suspend fun ExpenseRepository.setBengaliNumeralsEnabled(enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateProfileBengaliNumerals(enabled)
    if (rows > 0) refreshUserProfile()
    rows > 0
}
