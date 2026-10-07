package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class AppLanguage(val code: String, val titleBn: String, val titleEn: String) {
    BANGLA("bn", "বাংলা", "Bengali"),
    ENGLISH("en", "English", "English");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return if (code.equals("en", ignoreCase = true)) ENGLISH else BANGLA
        }
    }
}

@Serializable
data class BackupPayload(
    val version: Int = 2,
    val exportDateMillis: Long,
    val profile: UserProfile,
    val categories: List<Category>,
    val transactions: List<Transaction>,
    val budgets: List<Budget>,
    val accounts: List<Account> = emptyList(),
    val receivables: List<Receivable> = emptyList(),
    val recurringBills: List<RecurringBill> = emptyList(),
    val payables: List<Payable> = emptyList(),
    val savingsGoals: List<SavingsGoal> = emptyList()
)
