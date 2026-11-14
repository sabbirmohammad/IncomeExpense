package com.example.expensetracker.ui.budget

import com.example.expensetracker.data.model.Budget

enum class BudgetScreenTab {
    BUDGETS,
    SAVINGS_GOALS
}

data class CategoryBudgetStatus(
    val budget: Budget,
    val spentAmount: Double,
    val remainingAmount: Double,
    val progressPercent: Float,
    val isOverBudget: Boolean
)
