package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: Long = 0,
    val categoryId: Long? = null,
    val categoryName: String = "Overall Monthly",
    val amount: Double,
    val month: Int,
    val year: Int
)

@Serializable
data class AiBudgetRecommendation(
    val categoryName: String,
    val recommendedAmount: Double,
    val currentSpend: Double = 0.0,
    val reason: String = ""
)
