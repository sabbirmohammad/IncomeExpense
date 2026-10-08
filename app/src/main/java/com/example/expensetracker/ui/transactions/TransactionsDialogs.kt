package com.example.expensetracker.ui.transactions

import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsDialogs(
    showShareSheet: Boolean,
    onDismissShareSheet: () -> Unit,
    sharingTargetDateMillis: Long?,
    tripToDelete: MarketListWithItems?,
    onDismissDeleteTrip: () -> Unit,
    onConfirmDeleteTrip: (Long) -> Unit,
    showExportDialog: Boolean,
    onDismissExportDialog: () -> Unit,
    onOpenShareSlipFromExport: () -> Unit,
    showCustomDateRangeDialog: Boolean,
    onDismissCustomDateRange: () -> Unit,
    onConfirmDateRange: (Long, Long) -> Unit,
    transactions: List<Transaction>,
    userProfile: UserProfile,
    strings: Strings,
    scope: CoroutineScope
) {
    val haptics = rememberAppHaptics()

    if (showShareSheet) {
        ShareExpenseSheet(
            initialPeriod = if (sharingTargetDateMillis != null) SharePeriodType.TODAY else SharePeriodType.THIS_MONTH,
            initialTargetDateMillis = sharingTargetDateMillis,
            onDismiss = onDismissShareSheet
        )
    }

    if (tripToDelete != null) {
        val target = tripToDelete
        TransactionMarketTripDeleteDialog(
            isBangla = strings.isBangla,
            onConfirm = {
                haptics.mediumClick()
                onConfirmDeleteTrip(target.list.id)
            },
            onDismiss = onDismissDeleteTrip
        )
    }

    if (showExportDialog) {
        TransactionExportDialog(
            transactions = transactions,
            userProfile = userProfile,
            strings = strings,
            scope = scope,
            onDismiss = onDismissExportDialog,
            onOpenShareSlip = onOpenShareSlipFromExport
        )
    }

    if (showCustomDateRangeDialog) {
        TransactionDateRangeDialog(
            strings = strings,
            onDismiss = onDismissCustomDateRange,
            onConfirmDateRange = onConfirmDateRange
        )
    }
}
