package com.example.expensetracker.ui.transactions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.Strings

@Composable
fun TransactionsEmptyStateCard(
    hasActiveFilters: Boolean,
    hasAnyTransactions: Boolean,
    strings: Strings,
    onResetFilters: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onAddTransaction: () -> Unit
) {
    EmptyStateCard(
        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
        headline = when {
            hasActiveFilters -> strings.noTransactionsFound
            hasAnyTransactions -> if (strings.isBangla) "চলতি মাসে কোনো লেনদেন নেই" else "No transactions this month"
            else -> strings.noTransactionsFound
        },
        explanation = when {
            hasActiveFilters -> strings.noTransactionsAdjustFilter
            hasAnyTransactions -> if (strings.isBangla) "পূর্ববর্তী লেনদেন দেখতে সব সময়ের ফিল্টার নির্বাচন করুন অথবা নতুন লেনদেন যোগ করুন।" else "Select all time to view past records or add a new transaction."
            else -> strings.startTrackingSubtitle
        },
        buttonText = when {
            hasActiveFilters -> strings.clearFilters
            hasAnyTransactions -> if (strings.isBangla) "সব লেনদেন দেখুন" else "View All Transactions"
            else -> strings.addTransaction
        },
        onButtonClick = {
            when {
                hasActiveFilters -> onResetFilters()
                hasAnyTransactions -> onViewAllTransactions()
                else -> onAddTransaction()
            }
        }
    )
}
