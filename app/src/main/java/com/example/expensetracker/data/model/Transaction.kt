package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
enum class TransactionType {
    EXPENSE,
    INCOME
}

@Serializable
enum class PaymentMethod(val displayName: String, val iconName: String) {
    CASH("Cash", "Cash"),
    BANK_TRANSFER("Bank", "AccountBalance"),
    CREDIT_CARD("Credit Card", "CreditCard"),
    DEBIT_CARD("Debit Card", "CreditCard"),
    BKASH("bKash", "PhoneAndroid"),
    NAGAD("Nagad", "PhoneAndroid"),
    OTHER("Other", "Payment");

    fun getDisplayName(isBangla: Boolean): String {
        return if (isBangla) {
            when (this) {
                CASH -> "নগদ টাকা"
                BANK_TRANSFER -> "ব্যাংক"
                CREDIT_CARD -> "ক্রেডিট কার্ড"
                DEBIT_CARD -> "ডেবিট কার্ড"
                BKASH -> "বিকাশ"
                NAGAD -> "নগদ (MFS)"
                OTHER -> "অন্যান্য"
            }
        } else {
            displayName
        }
    }
}

@Serializable
enum class TimeFilter(val displayName: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    THIS_YEAR("This Year"),
    ALL("All Time"),
    CUSTOM("Custom");

    fun getDisplayName(isBangla: Boolean): String {
        return if (isBangla) {
            when (this) {
                TODAY -> "আজ"
                THIS_WEEK -> "এই সপ্তাহ"
                THIS_MONTH -> "এই মাস"
                THIS_YEAR -> "এই বছর"
                ALL -> "সব সময়"
                CUSTOM -> "কাস্টম"
            }
        } else {
            displayName
        }
    }
}

@Serializable
data class Transaction(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val dateMillis: Long,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = "",
    val accountId: Long? = null,
    val accountName: String? = null,
    val receiptImagePath: String? = null
) {
    fun isAccountTransfer(): Boolean {
        return categoryName.equals("Transfer", ignoreCase = true) ||
                categoryName.equals("Account Transfer", ignoreCase = true) ||
                categoryName.equals("অ্যাকাউন্ট স্থানান্তর", ignoreCase = true) ||
                notes.startsWith("[Transfer]") ||
                title.startsWith("Transfer to ") ||
                title.startsWith("Transfer from ")
    }

    fun isDebtFlow(): Boolean {
        return categoryName.equals("Loan & Debt Given", ignoreCase = true) ||
                categoryName.equals("Debt Recovery", ignoreCase = true) ||
                categoryName.equals("ঋণ বা ধার প্রদান", ignoreCase = true) ||
                categoryName.equals("পাওনা টাকা আদায়", ignoreCase = true)
    }
}

@Serializable
data class UserProfile(
    val firstName: String = "Sabbir",
    val lastName: String = "",
    val name: String = "Sabbir",
    val currencyCode: String = "BDT",
    val currencySymbol: String = "৳",
    val avatarIndex: Int = 0,
    val googleAccountEmail: String? = null,
    val isAutoSyncEnabled: Boolean = true,
    val lastSyncMillis: Long = 0L,
    val appLanguage: String = "bn",
    val geminiApiKey: String? = null,
    val aiInsightsEnabled: Boolean = true,
    val appTheme: String = "dark",
    val useBengaliNumerals: Boolean = true,
    val isPrivacyModeEnabled: Boolean = false
) {
    val effectiveFirstName: String
        get() = if (firstName.isNotBlank()) firstName.trim() else (name.trim().split(Regex("\\s+")).firstOrNull() ?: "User")

    val dashboardFirstNameUpper: String
        get() = effectiveFirstName

    val displayName: String
        get() = when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "${firstName.trim()} ${lastName.trim()}"
            firstName.isNotBlank() -> firstName.trim()
            lastName.isNotBlank() -> lastName.trim()
            name.isNotBlank() -> name.trim()
            else -> "User"
        }

    val initials: String
        get() {
            val f = firstName.trim()
            val l = lastName.trim()
            return when {
                f.isNotEmpty() && l.isNotEmpty() -> "${f.first()}${l.first()}".uppercase(Locale.getDefault())
                f.isNotEmpty() -> f.take(2).uppercase(Locale.getDefault())
                name.trim().isNotEmpty() -> {
                    val parts = name.trim().split(Regex("\\s+"))
                    if (parts.size >= 2) {
                        "${parts[0].first()}${parts[1].first()}".uppercase(Locale.getDefault())
                    } else {
                        parts[0].take(2).uppercase(Locale.getDefault())
                    }
                }
                else -> "U"
            }
        }
}

@Serializable
data class ScannedReceiptData(
    val amount: Double? = null,
    val title: String? = null,
    val categoryName: String? = null,
    val dateMillis: Long? = null,
    val notes: String? = null,
    val paymentMethod: PaymentMethod? = null
)

@Serializable
data class ParsedTransactionResult(
    val amount: Double? = null,
    val title: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryName: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val accountName: String? = null,
    val notes: String? = null
)
