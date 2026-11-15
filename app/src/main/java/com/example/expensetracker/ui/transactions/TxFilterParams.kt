package com.example.expensetracker.ui.transactions

import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType

internal data class TxFilterParams(
    val query: String,
    val type: TransactionType?,
    val categoryId: Long?,
    val accountId: Long?,
    val timeFilter: TimeFilter
)
