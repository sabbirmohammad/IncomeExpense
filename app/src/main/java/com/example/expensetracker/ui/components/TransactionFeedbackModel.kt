package com.example.expensetracker.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

enum class FeedbackVisualType {
    INCOME,
    EXPENSE,
    TRANSFER,
    BILL_PAID,
    BILL_SPLIT,
    DEBT_PAYMENT,
    TRANSACTION_DELETED,
    ITEM_DELETED,
    GENERIC_SUCCESS
}

data class TransactionFeedbackData(
    val type: FeedbackVisualType,
    val title: String,
    val amount: Double? = null,
    val currencySymbol: String = "৳",
    val subtitle: String? = null,
    val extraDetails: String? = null,
    val durationMillis: Long = 2400L
)

class TransactionFeedbackController {
    var currentFeedback by mutableStateOf<TransactionFeedbackData?>(null)
        private set

    fun show(feedback: TransactionFeedbackData) {
        currentFeedback = feedback
    }

    fun dismiss() {
        currentFeedback = null
    }
}

val LocalFeedbackController = staticCompositionLocalOf<TransactionFeedbackController> {
    TransactionFeedbackController()
}

object FeedbackStrings {
    fun expenseRecorded(isBangla: Boolean) = if (isBangla) "খরচ সংরক্ষিত হয়েছে!" else "Expense Recorded!"
    fun incomeAdded(isBangla: Boolean) = if (isBangla) "আয় যোগ করা হয়েছে!" else "Income Added!"
    fun transferSuccess(isBangla: Boolean) = if (isBangla) "ফান্ড ট্রান্সফার সফল হয়েছে!" else "Funds Transferred!"
    fun billPaid(isBangla: Boolean) = if (isBangla) "বিল পরিশোধ সম্পন্ন হয়েছে!" else "Bill Paid Successfully!"
    fun billSplit(isBangla: Boolean) = if (isBangla) "বিল স্প্লিট সংরক্ষিত হয়েছে!" else "Bill Split Recorded!"
    fun debtPayment(isBangla: Boolean) = if (isBangla) "পাওনা টাকা আদায় হয়েছে!" else "Debt Payment Recorded!"
    fun receivableAdded(isBangla: Boolean) = if (isBangla) "পাওনা রেকর্ড করা হয়েছে!" else "Receivable Recorded!"
    fun transactionUpdated(isBangla: Boolean) = if (isBangla) "লেনদেন আপডেট হয়েছে!" else "Transaction Updated!"
    fun transactionDeleted(isBangla: Boolean) = if (isBangla) "লেনদেন মুছে ফেলা হয়েছে" else "Transaction Deleted"
    fun itemDeleted(isBangla: Boolean) = if (isBangla) "মুছে ফেলা হয়েছে" else "Deleted Successfully"
    fun tapToDismiss(isBangla: Boolean) = if (isBangla) "বন্ধ করতে যেকোনো জায়গায় ট্যাপ করুন" else "Tap anywhere to dismiss"
}

