package com.example.expensetracker.ui.components

import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import java.util.Calendar

/**
 * Smart recommendation engine for selecting default categories.
 * Enforces C4 rule:
 * 1. Last used category for this type + this time of day (within ±2 hours).
 * 2. Else most frequent category for this type.
 * 3. Else "Other" / "অন্যান্য".
 * 4. Never alphabetical first.
 */
object CategoryRecommendationEngine {

    fun findSmartDefaultCategory(
        categories: List<Category>,
        type: TransactionType,
        recentTransactions: List<Transaction> = emptyList(),
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): Category? {
        val matchingCategories = categories.filter { it.type == type }
        if (matchingCategories.isEmpty()) return null

        val categoryById = matchingCategories.associateBy { it.id }
        val typeTransactions = recentTransactions.filter { it.type == type }

        // 1. Last used category for this type + this time of day (±2 hours)
        if (typeTransactions.isNotEmpty()) {
            val timeOfDayTxs = typeTransactions.filter { tx ->
                val cal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
                val txHour = cal.get(Calendar.HOUR_OF_DAY)
                val diff = kotlin.math.abs(txHour - currentHour)
                diff <= 2 || diff >= 22 // Wrap around midnight
            }
            val matchFromTime = timeOfDayTxs.maxByOrNull { it.dateMillis }?.let { categoryById[it.categoryId] }
            if (matchFromTime != null) {
                return matchFromTime
            }

            // 2. Most frequent category for this type
            val categoryCounts = typeTransactions.groupingBy { it.categoryId }.eachCount()
            val mostFrequentCatId = categoryCounts.maxByOrNull { it.value }?.key
            val matchFromFreq = mostFrequentCatId?.let { categoryById[it] }
            if (matchFromFreq != null) {
                return matchFromFreq
            }
        }

        // 3. Fallback: "Other" or "অন্যান্য" or "General"
        val otherCategory = matchingCategories.find {
            it.name.equals("Other", ignoreCase = true) ||
            it.name.equals("Others", ignoreCase = true) ||
            it.name.contains("অন্যান্য") ||
            it.name.equals("General", ignoreCase = true)
        }
        if (otherCategory != null) {
            return otherCategory
        }

        // 4. Default: first available category in database order (not sorted alphabetically)
        return matchingCategories.firstOrNull()
    }
}
