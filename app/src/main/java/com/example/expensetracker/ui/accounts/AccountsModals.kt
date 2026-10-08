package com.example.expensetracker.ui.accounts

import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Transaction

@Composable
fun AccountsModals(
    showAddEditSheet: Boolean,
    accountToEdit: Account?,
    onDismissAddEdit: () -> Unit,
    onSaveAccount: (Account) -> Unit,
    showTransferSheet: Boolean,
    onDismissTransfer: () -> Unit,
    onTransferFunds: (Long, Long, Double, String) -> Unit,
    showAccountDetailsSheet: Boolean,
    selectedAccount: Account?,
    accountTransactions: List<Transaction>,
    onDismissDetails: () -> Unit,
    onEditFromDetails: () -> Unit,
    onReconcileFromDetails: () -> Unit,
    accountToReconcile: Account?,
    onDismissReconcile: () -> Unit,
    onConfirmReconcile: (Double, String) -> Unit,
    currencySymbol: String,
    accounts: List<Account>
) {
    if (showAddEditSheet) {
        AddEditAccountSheet(
            account = accountToEdit,
            currencySymbol = currencySymbol,
            onDismiss = onDismissAddEdit,
            onSave = onSaveAccount
        )
    }

    if (showTransferSheet) {
        TransferFundsSheet(
            accounts = accounts,
            currencySymbol = currencySymbol,
            onDismiss = onDismissTransfer,
            onTransfer = onTransferFunds
        )
    }

    if (showAccountDetailsSheet && selectedAccount != null) {
        AccountDetailSheet(
            account = selectedAccount,
            transactions = accountTransactions,
            currencySymbol = currencySymbol,
            onDismiss = onDismissDetails,
            onEdit = onEditFromDetails,
            onReconcile = onReconcileFromDetails
        )
    }

    if (accountToReconcile != null) {
        ReconcileBalanceDialog(
            account = accountToReconcile,
            currencySymbol = currencySymbol,
            onDismiss = onDismissReconcile,
            onConfirm = onConfirmReconcile
        )
    }
}
