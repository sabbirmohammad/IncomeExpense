package com.example.expensetracker.ui.budget

import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName

object ExpenseCategoryMatcher {
    fun findMatchingExpenseCategory(categoryName: String, categories: List<Category>): Category? {
        val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
        val trimmed = categoryName.trim()
        if (trimmed.isBlank()) return null

        // 1. Direct match with category.name (case-insensitive)
        expenseCategories.find { it.name.equals(trimmed, ignoreCase = true) }?.let { return it }

        // 2. Direct match with localized category name in Bangla or English
        val localizedEn = getLocalizedCategoryName(trimmed, false)
        val localizedBn = getLocalizedCategoryName(trimmed, true)
        expenseCategories.find {
            it.name.equals(localizedEn, ignoreCase = true) || it.name.equals(localizedBn, ignoreCase = true)
        }?.let { return it }

        // 3. Match category's localized names with the input name
        expenseCategories.find { cat ->
            val catEn = getLocalizedCategoryName(cat.name, false)
            val catBn = getLocalizedCategoryName(cat.name, true)
            catEn.equals(trimmed, ignoreCase = true) ||
            catBn.equals(trimmed, ignoreCase = true) ||
            catEn.equals(localizedEn, ignoreCase = true) ||
            catBn.equals(localizedBn, ignoreCase = true)
        }?.let { return it }

        // 4. Normalized keyword matching (handle & vs and, punctuation, partial match)
        fun norm(s: String) = s.lowercase()
            .replace("&", "and")
            .replace(Regex("""[^a-zA-Z0-9\u0980-\u09FF]"""), "")

        val normInput = norm(trimmed)
        val normEn = norm(localizedEn)
        val normBn = norm(localizedBn)

        expenseCategories.find { cat ->
            val normCat = norm(cat.name)
            val normCatEn = norm(getLocalizedCategoryName(cat.name, false))
            val normCatBn = norm(getLocalizedCategoryName(cat.name, true))

            normCat == normInput || normCat == normEn || normCat == normBn ||
            normCatEn == normInput || normCatEn == normEn ||
            normCatBn == normInput || normCatBn == normBn ||
            (normInput.length >= 4 && (normCat.contains(normInput) || normCatEn.contains(normInput) || normCatBn.contains(normInput))) ||
            (normCat.length >= 4 && normInput.contains(normCat))
        }?.let { return it }

        return null
    }
}
