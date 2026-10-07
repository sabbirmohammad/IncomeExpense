package com.example.expensetracker.data.model

import androidx.compose.runtime.Immutable
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
data class Category(
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val colorHex: String,
    val isDefault: Boolean = false
) {
    fun getLocalizedName(isBangla: Boolean): String {
        return getLocalizedCategoryName(name, isBangla)
    }
}

fun getLocalizedCategoryName(categoryName: String, isBangla: Boolean): String {
    val trimmed = categoryName.trim()
    if (isBangla) {
        return when (trimmed) {
            "Food & Dining" -> "খাবার ও রেস্তোরাঁ"
            "Groceries" -> "বাজার ও সদাই"
            "Transportation" -> "যাতায়াত ও ভ্রমণ"
            "Shopping" -> "কেনাকাটা"
            "Housing & Rent" -> "বাসা ভাড়া ও বাসস্থান"
            "Bills & Utilities" -> "বিল ও ইউটিলিটি"
            "Entertainment" -> "বিনোদন"
            "Health & Care" -> "স্বাস্থ্য ও চিকিৎসা"
            "Education" -> "শিক্ষা ও বইপত্র"
            "Travel" -> "ভ্রমণ ও ট্যুর"
            "Loan & Debt Given" -> "ঋণ বা ধার প্রদান"
            "Other Expense" -> "অন্যান্য ব্যয়"
            "Salary" -> "বেতন"
            "Freelance" -> "ফ্রিল্যান্সিং"
            "Business" -> "ব্যবসা ও বাণিজ্য"
            "Investment" -> "বিনিয়োগ ও মুনাফা"
            "Debt Recovery" -> "পাওনা টাকা আদায়"
            "Bonus" -> "বোনাস ও উপহার"
            "Other Income" -> "অন্যান্য আয়"
            "Transfer", "Account Transfer" -> "অ্যাকাউন্ট স্থানান্তর"
            else -> categoryName
        }
    } else {
        return when (trimmed) {
            "খাবার ও রেস্তোরাঁ", "খাবার" -> "Food & Dining"
            "বাজার ও সদাই", "বাজার" -> "Groceries"
            "যাতায়াত ও ভ্রমণ", "যাতায়াত" -> "Transportation"
            "কেনাকাটা" -> "Shopping"
            "বাসা ভাড়া ও বাসস্থান", "বাসা ভাড়া" -> "Housing & Rent"
            "বিল ও ইউটিলিটি" -> "Bills & Utilities"
            "বিনোদন" -> "Entertainment"
            "স্বাস্থ্য ও চিকিৎসা", "চিকিৎসা" -> "Health & Care"
            "শিক্ষা ও বইপত্র", "শিক্ষা" -> "Education"
            "ভ্রমণ ও ট্যুর", "ভ্রমণ" -> "Travel"
            "ঋণ বা ধার প্রদান" -> "Loan & Debt Given"
            "অন্যান্য ব্যয়" -> "Other Expense"
            "বেতন" -> "Salary"
            "ফ্রিল্যান্সিং" -> "Freelance"
            "ব্যবসা ও বাণিজ্য", "ব্যবসা" -> "Business"
            "বিনিয়োগ ও মুনাফা", "বিনিয়োগ" -> "Investment"
            "পাওনা টাকা আদায়" -> "Debt Recovery"
            "বোনাস ও উপহার", "বোনাস" -> "Bonus"
            "অন্যান্য আয়" -> "Other Income"
            "অ্যাকাউন্ট স্থানান্তর" -> "Account Transfer"
            else -> categoryName
        }
    }
}

@Serializable
enum class AccountType(val displayName: String) {
    BANK("Bank Account"),
    MFS("MFS / Mobile Wallet"),
    CASH("Cash");

    fun getDisplayName(isBangla: Boolean): String {
        return if (isBangla) {
            when (this) {
                BANK -> "ব্যাংক অ্যাকাউন্ট"
                MFS -> "মোবাইল ওয়ালেট (MFS)"
                CASH -> "নগদ ক্যাশ"
            }
        } else {
            displayName
        }
    }
}

@Serializable
data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val accountNumber: String = "",
    val institutionName: String = "",
    val initialBalance: Double = 0.0,
    val currentBalance: Double = 0.0,
    val colorHex: String = "#3B8FF0",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getLocalizedName(isBangla: Boolean): String {
        return getLocalizedAccountName(name, isBangla)
    }
}

fun getLocalizedAccountName(accountName: String?, isBangla: Boolean): String {
    if (accountName.isNullOrBlank()) return if (isBangla) "অ্যাকাউন্ট" else "Account"
    val trimmed = accountName.trim()
    if (isBangla) {
        return when (trimmed) {
            "Cash in Hand", "Cash", "cash" -> "নগদ ক্যাশ"
            "Bank Account", "Bank", "bank" -> "ব্যাংক অ্যাকাউন্ট"
            "bKash Personal", "bKash", "bkash" -> "বিকাশ ওয়ালেট"
            "Nagad Personal", "Nagad", "nagad" -> "নগদ ওয়ালেট"
            "Rocket Personal", "Rocket", "rocket" -> "রকেট ওয়ালেট"
            "Upay Personal", "Upay", "upay" -> "উপায় ওয়ালেট"
            "City Bank" -> "সিটি ব্যাংক"
            "Dutch Bangla Bank", "DBBL" -> "ডাচ বাংলা ব্যাংক"
            "BRAC Bank", "Brac Bank" -> "ব্র্যাক ব্যাংক"
            "Islami Bank", "IBBL" -> "ইসলামী ব্যাংক"
            else -> accountName
        }
    } else {
        return when (trimmed) {
            "নগদ ক্যাশ", "ক্যাশ", "নগদ টাকা", "ক্যাশ ইন হ্যান্ড" -> "Cash in Hand"
            "ব্যাংক অ্যাকাউন্ট", "ব্যাংক" -> "Bank Account"
            "বিকাশ ওয়ালেট", "বিকাশ" -> "bKash Personal"
            "নগদ ওয়ালেট", "নগদ (MFS)" -> "Nagad Personal"
            "রকেট ওয়ালেট", "রকেট" -> "Rocket Personal"
            "উপায় ওয়ালেট", "উপায়" -> "Upay Personal"
            "সিটি ব্যাংক" -> "City Bank"
            "ডাচ বাংলা ব্যাংক" -> "Dutch Bangla Bank"
            "ব্র্যাক ব্যাংক" -> "BRAC Bank"
            "ইসলামী ব্যাংক" -> "Islami Bank"
            else -> accountName
        }
    }
}

@Serializable
enum class ReceivableStatus(val displayName: String) {
    PENDING("Pending"),
    PARTIAL("Partially Paid"),
    SETTLED("Settled / Received");

    fun getDisplayName(isBangla: Boolean): String {
        return if (isBangla) {
            when (this) {
                PENDING -> "বকেয়া"
                PARTIAL -> "আংশিক আদায়"
                SETTLED -> "পরিশোধিত"
            }
        } else {
            displayName
        }
    }
}

@Serializable
data class Receivable(
    val id: Long = 0,
    val personName: String,
    val phoneNumber: String = "",
    val amount: Double,
    val amountReceived: Double = 0.0,
    val dueDateMillis: Long? = null,
    val createdDateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val status: ReceivableStatus = ReceivableStatus.PENDING
) {
    val remainingAmount: Double
        get() = (amount - amountReceived).coerceAtLeast(0.0)

    val isSettled: Boolean
        get() = remainingAmount <= 0.001 || status == ReceivableStatus.SETTLED
}

data class ReceivablesSummary(
    val totalLent: Double = 0.0,
    val totalCollected: Double = 0.0,
    val remainingDue: Double = 0.0,
    val pendingCount: Int = 0
)

@Serializable
enum class PayableStatus(val displayName: String) {
    PENDING("Pending"),
    PARTIAL("Partially Repaid"),
    SETTLED("Settled / Repaid");

    fun getDisplayName(isBangla: Boolean): String {
        return if (isBangla) {
            when (this) {
                PENDING -> "পরিশোধ বাকি"
                PARTIAL -> "আংশিক পরিশোধিত"
                SETTLED -> "সম্পূর্ণ পরিশোধিত"
            }
        } else {
            displayName
        }
    }
}

@Serializable
data class Payable(
    val id: Long = 0,
    val personName: String,
    val phoneNumber: String = "",
    val amount: Double,
    val amountPaid: Double = 0.0,
    val dueDateMillis: Long? = null,
    val createdDateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val status: PayableStatus = PayableStatus.PENDING
) {
    val remainingAmount: Double
        get() = (amount - amountPaid).coerceAtLeast(0.0)

    val isSettled: Boolean
        get() = remainingAmount <= 0.001 || status == PayableStatus.SETTLED
}

data class PayablesSummary(
    val totalBorrowed: Double = 0.0,
    val totalRepaid: Double = 0.0,
    val remainingDue: Double = 0.0,
    val pendingCount: Int = 0
)

data class DebtBookSummary(
    val totalReceivable: Double = 0.0,
    val totalPayable: Double = 0.0
) {
    val netBalance: Double
        get() = totalReceivable - totalPayable
}

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
    /**
     * Checks whether this transaction is an internal fund transfer between accounts.
     * Internal transfers shift balances between accounts without altering net worth,
     * and should not inflate monthly living expenses or income.
     */
    fun isAccountTransfer(): Boolean {
        return categoryName.equals("Transfer", ignoreCase = true) ||
                categoryName.equals("Account Transfer", ignoreCase = true) ||
                categoryName.equals("অ্যাকাউন্ট স্থানান্তর", ignoreCase = true) ||
                notes.startsWith("[Transfer]") ||
                title.startsWith("Transfer to ") ||
                title.startsWith("Transfer from ")
    }

    /**
     * Loans given and debt repayments are capital transfers, not operational living expenses/income.
     */
    fun isDebtFlow(): Boolean {
        return categoryName.equals("Loan & Debt Given", ignoreCase = true) ||
                categoryName.equals("Debt Recovery", ignoreCase = true) ||
                categoryName.equals("ঋণ বা ধার প্রদান", ignoreCase = true) ||
                categoryName.equals("পাওনা টাকা আদায়", ignoreCase = true)
    }
}


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
    /**
     * Resolves the effective first name.
     * Uses [firstName] if non-blank, otherwise takes the first word of [name].
     */
    val effectiveFirstName: String
        get() = if (firstName.isNotBlank()) firstName.trim() else (name.trim().split(Regex("\\s+")).firstOrNull() ?: "User")

    /**
     * Resolves the user's name exactly as typed (preserves exact casing: lowercase, uppercase, mixed).
     */
    val dashboardFirstNameUpper: String
        get() = effectiveFirstName

    /**
     * Resolves the full display name (First + Last, or whatever is non-blank).
     */
    val displayName: String
        get() = when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "${firstName.trim()} ${lastName.trim()}"
            firstName.isNotBlank() -> firstName.trim()
            lastName.isNotBlank() -> lastName.trim()
            name.isNotBlank() -> name.trim()
            else -> "User"
        }

    /**
     * Resolves 1-2 character initials for avatar badge representation.
     */
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

@Immutable
data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val budgetAmount: Double = 0.0,
    val budgetSpent: Double = 0.0,
    val budgetRemaining: Double = 0.0,
    val budgetPercent: Float = 0f,
    val budgetOverspent: Double = 0.0
)

/**
 * Consolidates a list of transactions into a [FinancialSummary],
 * ignoring internal transfers between accounts and debt flows so spending and income metrics stay accurate.
 */
fun List<Transaction>.toFinancialSummary(overallBudget: Double = 0.0): FinancialSummary {
    var totalIncome = 0.0
    var totalExpense = 0.0

    for (tx in this) {
        if (tx.isAccountTransfer() || tx.isDebtFlow()) continue
        if (tx.type == TransactionType.INCOME) {
            totalIncome += tx.amount
        } else {
            totalExpense += tx.amount
        }
    }

    val netBalance = totalIncome - totalExpense
    val budgetRemaining = if (overallBudget > 0) (overallBudget - totalExpense).coerceAtLeast(0.0) else 0.0
    val budgetOverspent = if (overallBudget > 0 && totalExpense > overallBudget) totalExpense - overallBudget else 0.0
    val budgetPercent = if (overallBudget > 0) ((totalExpense / overallBudget).toFloat()).coerceIn(0f, 2f) else 0f

    return FinancialSummary(
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        netBalance = netBalance,
        budgetAmount = overallBudget,
        budgetSpent = totalExpense,
        budgetRemaining = budgetRemaining,
        budgetPercent = budgetPercent,
        budgetOverspent = budgetOverspent
    )
}


@Immutable
data class CategorySpend(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)

@Immutable
data class DailySpend(
    val dateMillis: Long,
    val dayLabel: String,
    val income: Double,
    val expense: Double
)

@Immutable
data class AnalyticsOverview(
    val dailyAverageSpend: Double = 0.0,
    val savingsRate: Int = 0,
    val highestExpenseAmount: Double = 0.0,
    val highestExpenseTitle: String = "",
    val totalTransactionsCount: Int = 0,
    val periodChangePercent: Double? = null,
    val isPeriodChangeFavorable: Boolean = true
)

@Immutable
data class PaymentMethodSpend(
    val paymentMethod: PaymentMethod,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)

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
enum class BillFrequency(val displayName: String, val bnDisplayName: String) {
    MONTHLY("Monthly", "প্রতি মাসে"),
    WEEKLY("Weekly", "প্রতি সপ্তাহে"),
    YEARLY("Yearly", "প্রতি বছরে")
}

@Serializable
enum class BillPaymentStatus {
    PAID, OVERDUE, DUE_SOON, UPCOMING
}

@Serializable
data class RecurringBill(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryIcon: String = "Receipt",
    val categoryColor: String = "#3B8FF0",
    val accountId: Long? = null,
    val accountName: String? = null,
    val frequency: BillFrequency = BillFrequency.MONTHLY,
    val dueDay: Int = 1, // 1 to 31
    val nextDueDateMillis: Long = System.currentTimeMillis(),
    val lastPaidDateMillis: Long? = null,
    val isActive: Boolean = true,
    val notes: String = ""
) {
    fun getPaymentStatus(currentTimeMillis: Long = System.currentTimeMillis()): BillPaymentStatus {
        if (!isActive) return BillPaymentStatus.UPCOMING

        val calNow = java.util.Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val calDue = java.util.Calendar.getInstance().apply {
            timeInMillis = nextDueDateMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val diffDays = ((calDue.timeInMillis - calNow.timeInMillis) / (24L * 60 * 60 * 1000)).toInt()

        return when {
            diffDays < 0 -> BillPaymentStatus.OVERDUE
            diffDays in 0..4 -> BillPaymentStatus.DUE_SOON
            else -> BillPaymentStatus.UPCOMING
        }
    }

    fun getDaysUntilDue(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        val calNow = java.util.Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val calDue = java.util.Calendar.getInstance().apply {
            timeInMillis = nextDueDateMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return ((calDue.timeInMillis - calNow.timeInMillis) / (24L * 60 * 60 * 1000)).toInt()
    }
}

fun calculateInitialDueDate(frequency: BillFrequency, dueDay: Int, fromTimeMillis: Long = System.currentTimeMillis()): Long {
    val cal = java.util.Calendar.getInstance().apply {
        timeInMillis = fromTimeMillis
        set(java.util.Calendar.HOUR_OF_DAY, 9)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    val currentDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
    when (frequency) {
        BillFrequency.WEEKLY -> {
            cal.add(java.util.Calendar.WEEK_OF_YEAR, 1)
        }
        BillFrequency.MONTHLY -> {
            val maxDayThisMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            val clampedDueDay = dueDay.coerceIn(1, maxDayThisMonth)
            if (currentDay > clampedDueDay) {
                cal.add(java.util.Calendar.MONTH, 1)
            }
            val maxDayTarget = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            cal.set(java.util.Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, maxDayTarget))
        }
        BillFrequency.YEARLY -> {
            if (cal.get(java.util.Calendar.DAY_OF_MONTH) > dueDay) {
                cal.add(java.util.Calendar.YEAR, 1)
            }
            val maxDayTarget = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            cal.set(java.util.Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, maxDayTarget))
        }
    }
    return cal.timeInMillis
}

@Serializable
data class BillSplitFriend(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val phoneNumber: String = "",
    val amount: Double = 0.0
)

@Serializable
data class BillSplitData(
    val totalAmount: Double,
    val payingAccountId: Long?,
    val payingAccountName: String?,
    val categoryId: Long,
    val categoryName: String,
    val myShare: Double,
    val friends: List<BillSplitFriend>,
    val note: String = ""
)

@Serializable
data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AiInsightItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val type: String = "INFO",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AiBudgetRecommendation(
    val categoryName: String,
    val recommendedAmount: Double,
    val currentSpend: Double = 0.0,
    val reason: String = ""
)

@Serializable
data class AiFinancialAudit(
    val healthScore: Int = 80,
    val statusGrade: String = "Good",
    val summaryEvaluation: String = "",
    val detectedLeaksOrSpikes: List<String> = emptyList(),
    val actionableTips: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
enum class ApiTestStatusType {
    SUCCESS,
    INVALID_KEY,
    QUOTA_EXCEEDED,
    NETWORK_ERROR,
    ERROR
}

@Serializable
data class ApiConnectionTestResult(
    val isSuccess: Boolean,
    val statusType: ApiTestStatusType,
    val latencyMs: Long = 0L,
    val modelUsed: String = "",
    val message: String = ""
)

@Serializable
data class MarketItem(
    val id: Long = 0,
    val listId: Long = 0,
    val name: String,
    val quantity: Double = 1.0,
    val unit: String = "কেজি",
    val amount: Double? = null,
    val isPurchased: Boolean = false,
    val transactionId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formatQuantity(): String {
        return if (quantity % 1.0 == 0.0) {
            "${quantity.toInt()} $unit"
        } else {
            "$quantity $unit"
        }
    }
}

@Serializable
data class MarketList(
    val id: Long = 0,
    val title: String = "বাজারের ফর্দ",
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)

data class MarketListWithItems(
    val list: MarketList,
    val items: List<MarketItem>
) {
    val totalItems: Int get() = items.size
    val purchasedItems: Int get() = items.count { it.isPurchased }
    val remainingItems: Int get() = items.count { !it.isPurchased }
    val totalSpent: Double get() = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
    val progress: Float get() = if (totalItems > 0) purchasedItems.toFloat() / totalItems.toFloat() else 0f
    val isAllPurchased: Boolean get() = items.isNotEmpty() && items.all { it.isPurchased }
}

@Serializable
data class MarketCompletionResult(
    val success: Boolean = false,
    val totalSpent: Double = 0.0,
    val purchasedCount: Int = 0,
    val transactionId: Long? = null
)

@Serializable
data class MarketCostEstimationResult(
    val estimatedTotalMin: Double = 0.0,
    val estimatedTotalMax: Double = 0.0,
    val itemPrices: Map<String, Double> = emptyMap(),
    val advice: String = ""
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

@Serializable
data class SavingsGoalAiAnalysis(
    val feasibilityGrade: String = "Good",
    val requiredDailySavings: Double = 0.0,
    val requiredWeeklySavings: Double = 0.0,
    val requiredMonthlySavings: Double = 0.0,
    val estimatedCompletionMonths: Double = 0.0,
    val adviceTips: List<String> = emptyList()
)

@Serializable
data class DebtPortfolioAiAdvice(
    val recoveryPriority: List<String> = emptyList(),
    val healthSummary: String = "",
    val actionSteps: List<String> = emptyList()
)

@Serializable
data class BillCashFlowAiForecast(
    val isBufferSafe: Boolean = true,
    val upcomingTotal: Double = 0.0,
    val availableLiquid: Double = 0.0,
    val alertMessage: String = "",
    val hasShortfall: Boolean = false,
    val shortfallAmount: Double = 0.0,
    val warningMessage: String = "",
    val suggestedTransfers: List<String> = emptyList(),
    val safetyTips: List<String> = emptyList()
)

