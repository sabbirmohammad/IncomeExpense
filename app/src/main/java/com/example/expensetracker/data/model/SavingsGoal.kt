package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SavingsGoal(
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val targetDateMillis: Long? = null,
    val createdDateMillis: Long = System.currentTimeMillis(),
    val icon: String = "savings",
    val colorHex: String = "#10B981",
    val notes: String = "",
    val isCompleted: Boolean = false
) {
    val progressPercentage: Float
        get() = if (targetAmount > 0) ((savedAmount / targetAmount).toFloat().coerceIn(0f, 1f)) else 0f

    val remainingAmount: Double
        get() = (targetAmount - savedAmount).coerceAtLeast(0.0)
}

@Serializable
data class SavingsGoalAiAnalysis(
    val feasibilityGrade: String = "Good",
    val requiredDailySavings: Double = 0.0,
    val requiredWeeklySavings: Double = 0.0,
    val requiredMonthlySavings: Double = 0.0,
    val estimatedCompletionMonths: Double = 0.0,
    val adviceTips: List<String> = emptyList()
)
