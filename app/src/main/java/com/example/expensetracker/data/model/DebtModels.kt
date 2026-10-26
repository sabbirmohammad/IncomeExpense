package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

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
