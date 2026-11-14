package com.example.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.accounts.TransferFundsSheet
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.receivables.BillSplitterSheet

@Composable
fun NavigationSplitAndTransferSheets(
    showBillSplitterSheet: Boolean,
    showTransferSheet: Boolean,
    accounts: List<Account>,
    categories: List<Category>,
    userProfile: UserProfile,
    strings: Strings,
    feedbackController: TransactionFeedbackController,
    dashboardViewModel: DashboardViewModel,
    onCloseBillSplitterSheet: () -> Unit,
    onCloseTransferSheet: () -> Unit
) {
    if (showBillSplitterSheet) {
        BillSplitterSheet(
            accounts = accounts,
            categories = categories,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onCloseBillSplitterSheet,
            onConfirmSplit = { splitData ->
                dashboardViewModel.executeBillSplit(splitData)
                onCloseBillSplitterSheet()
                val totalPeople = splitData.friends.size + 1
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.BILL_SPLIT,
                        title = FeedbackStrings.billSplit(strings.isBangla),
                        amount = splitData.totalAmount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = if (strings.isBangla) "$totalPeople জনের মধ্যে স্প্লিট" else "$totalPeople people split"
                    )
                )
            }
        )
    }

    if (showTransferSheet) {
        TransferFundsSheet(
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onCloseTransferSheet,
            onTransfer = { fromId, toId, amount, note ->
                dashboardViewModel.transferFunds(fromId, toId, amount, note)
                onCloseTransferSheet()
                val fromAcc = accounts.find { it.id == fromId }?.name ?: ""
                val toAcc = accounts.find { it.id == toId }?.name ?: ""
                val transferSub = if (fromAcc.isNotBlank() && toAcc.isNotBlank()) "$fromAcc → $toAcc" else note
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.TRANSFER,
                        title = FeedbackStrings.transferSuccess(strings.isBangla),
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = transferSub.ifBlank { null }
                    )
                )
            }
        )
    }
}
