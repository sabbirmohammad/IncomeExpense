package com.example.expensetracker.ui.transactions

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.AppBottomSheet
import com.example.expensetracker.ui.components.Strings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsSheetsHost(
    dialogState: TransactionsDialogState,
    viewModel: TransactionsViewModel,
    accounts: List<Account>,
    categories: List<Category>,
    userProfile: UserProfile,
    strings: Strings,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onEditTransaction: (Transaction) -> Unit,
    onResetSearchActive: () -> Unit
) {
    dialogState.selectedTxForDetail?.let { tx ->
        AppBottomSheet(onDismissRequest = { dialogState.selectedTxForDetail = null }) {
            TransactionDetailSheet(
                transaction = tx,
                currencySymbol = userProfile.currencySymbol,
                isBangla = strings.isBangla,
                useBengaliNumerals = userProfile.useBengaliNumerals || strings.isBangla,
                onEdit = {
                    dialogState.selectedTxForDetail = null
                    onEditTransaction(it)
                },
                onDelete = { deletedTx ->
                    dialogState.selectedTxForDetail = null
                    viewModel.deleteTransaction(deletedTx)
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = strings.transactionDeleted,
                            actionLabel = strings.undo,
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.restoreTransaction(deletedTx)
                        }
                    }
                },
                onDismiss = { dialogState.selectedTxForDetail = null }
            )
        }
    }

    if (dialogState.showFilterSheet) {
        val selectedType = viewModel.selectedType.value
        val isMarketTripsView = viewModel.isMarketTripsView.value
        val selectedTimeFilter = viewModel.selectedTimeFilter.value
        val selectedAccountId = viewModel.selectedAccountId.value
        val selectedCategoryId = viewModel.selectedCategoryId.value

        AppBottomSheet(onDismissRequest = { dialogState.showFilterSheet = false }) {
            TransactionFilterSheet(
                selectedType = selectedType,
                onTypeSelected = { viewModel.setTypeFilter(it) },
                isMarketTripsView = isMarketTripsView,
                onMarketTripsViewChanged = { viewModel.setMarketTripsView(it) },
                selectedTimeFilter = selectedTimeFilter,
                onTimeFilterSelected = { viewModel.setTimeFilter(it) },
                accounts = accounts,
                selectedAccountId = selectedAccountId,
                onAccountSelected = { viewModel.setAccountFilter(it) },
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { viewModel.setCategoryFilter(it) },
                onOpenCustomDateRange = {
                    dialogState.showFilterSheet = false
                    dialogState.showCustomDateRangeDialog = true
                },
                onResetAll = {
                    viewModel.onSearchQueryChanged("")
                    viewModel.setTypeFilter(null)
                    viewModel.setCategoryFilter(null)
                    viewModel.setAccountFilter(null)
                    viewModel.setTimeFilter(TimeFilter.THIS_MONTH)
                    viewModel.setMarketTripsView(false)
                    onResetSearchActive()
                },
                onDismiss = { dialogState.showFilterSheet = false }
            )
        }
    }

    TransactionsDialogs(
        showShareSheet = dialogState.showShareSheet,
        onDismissShareSheet = {
            dialogState.showShareSheet = false
            dialogState.sharingTargetDateMillis = null
        },
        sharingTargetDateMillis = dialogState.sharingTargetDateMillis,
        tripToDelete = dialogState.tripToDelete,
        onDismissDeleteTrip = { dialogState.tripToDelete = null },
        onConfirmDeleteTrip = { tripId ->
            viewModel.deleteMarketTrip(tripId)
            dialogState.tripToDelete = null
        },
        showExportDialog = dialogState.showExportDialog,
        onDismissExportDialog = { dialogState.showExportDialog = false },
        onOpenShareSlipFromExport = {
            dialogState.sharingTargetDateMillis = null
            dialogState.showShareSheet = true
        },
        showCustomDateRangeDialog = dialogState.showCustomDateRangeDialog,
        onDismissCustomDateRange = { dialogState.showCustomDateRangeDialog = false },
        onConfirmDateRange = { start, end -> viewModel.setCustomDateRange(start, end) },
        transactions = viewModel.filteredTransactions.value,
        userProfile = userProfile,
        strings = strings,
        scope = scope
    )
}
