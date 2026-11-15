package com.example.expensetracker.ui.transactions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Transaction

class TransactionsDialogState {
    var showShareSheet by mutableStateOf(false)
    var showExportDialog by mutableStateOf(false)
    var sharingTargetDateMillis by mutableStateOf<Long?>(null)
    var showCustomDateRangeDialog by mutableStateOf(false)
    var tripToDelete by mutableStateOf<MarketListWithItems?>(null)
    var selectedTxForDetail by mutableStateOf<Transaction?>(null)
    var showFilterSheet by mutableStateOf(false)
}

@Composable
fun rememberTransactionsDialogState(): TransactionsDialogState = remember { TransactionsDialogState() }
