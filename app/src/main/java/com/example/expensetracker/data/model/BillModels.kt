package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.UUID

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

        val calNow = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calDue = Calendar.getInstance().apply {
            timeInMillis = nextDueDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffDays = ((calDue.timeInMillis - calNow.timeInMillis) / (24L * 60 * 60 * 1000)).toInt()

        return when {
            diffDays < 0 -> BillPaymentStatus.OVERDUE
            diffDays in 0..4 -> BillPaymentStatus.DUE_SOON
            else -> BillPaymentStatus.UPCOMING
        }
    }

    fun getDaysUntilDue(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        val calNow = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calDue = Calendar.getInstance().apply {
            timeInMillis = nextDueDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return ((calDue.timeInMillis - calNow.timeInMillis) / (24L * 60 * 60 * 1000)).toInt()
    }
}

fun calculateInitialDueDate(frequency: BillFrequency, dueDay: Int, fromTimeMillis: Long = System.currentTimeMillis()): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = fromTimeMillis
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val currentDay = cal.get(Calendar.DAY_OF_MONTH)
    when (frequency) {
        BillFrequency.WEEKLY -> {
            cal.add(Calendar.WEEK_OF_YEAR, 1)
        }
        BillFrequency.MONTHLY -> {
            val maxDayThisMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val clampedDueDay = dueDay.coerceIn(1, maxDayThisMonth)
            if (currentDay > clampedDueDay) {
                cal.add(Calendar.MONTH, 1)
            }
            val maxDayTarget = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            cal.set(Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, maxDayTarget))
        }
        BillFrequency.YEARLY -> {
            if (cal.get(Calendar.DAY_OF_MONTH) > dueDay) {
                cal.add(Calendar.YEAR, 1)
            }
            val maxDayTarget = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            cal.set(Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, maxDayTarget))
        }
    }
    return cal.timeInMillis
}

@Serializable
data class BillSplitFriend(
    val id: String = UUID.randomUUID().toString(),
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
