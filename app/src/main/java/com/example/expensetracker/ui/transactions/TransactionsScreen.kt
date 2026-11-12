package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppBottomSheet
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.LocalAppStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onEditTransaction: (Transaction) -> Unit,
    onAddTransactionForDate: ((Long) -> Unit)? = null,
    onNavigateToSettings: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsStateWithLifecycle()
    val isMarketTripsView by viewModel.isMarketTripsView.collectAsStateWithLifecycle()
    val marketLists by viewModel.marketLists.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    val groupedTransactions = remember(transactions, strings.today, strings.yesterday, strings.isBangla, userProfile.useBengaliNumerals) {
        groupTransactionsByDate(
            transactions = transactions,
            todayLabel = strings.today,
            yesterdayLabel = strings.yesterday,
            isBangla = strings.isBangla,
            useBengaliNumerals = userProfile.useBengaliNumerals
        )
    }

    val activeFilterCount = (if (selectedType != null || isMarketTripsView) 1 else 0) +
        (if (selectedCategoryId != null) 1 else 0) +
        (if (selectedAccountId != null) 1 else 0) +
        (if (selectedTimeFilter != TimeFilter.THIS_MONTH) 1 else 0) +
        (if (searchQuery.isNotBlank()) 1 else 0)

    var showShareSheet by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var sharingTargetDateMillis by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCustomDateRangeDialog by remember { mutableStateOf(false) }
    var tripToDelete by remember { mutableStateOf<MarketListWithItems?>(null) }
    var isSearchActive by remember { mutableStateOf(searchQuery.isNotBlank()) }

    // V7 bottom sheet states: DetailSheet & FilterSheet
    var selectedTxForDetail by remember { mutableStateOf<Transaction?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TransactionsTopBar(
                title = strings.transactionsTitle,
                onMenuClick = onMenuClick,
                onExportClick = { showExportDialog = true },
                onShareClick = {
                    sharingTargetDateMillis = null
                    showShareSheet = true
                },
                onNavigateToSettings = onNavigateToSettings,
                onSearchClick = { isSearchActive = !isSearchActive },
                isSearchActive = isSearchActive
            )

            TransactionsFilterBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                isSearchActive = isSearchActive,
                onSearchActiveChanged = { isSearchActive = it },
                selectedType = selectedType,
                onTypeFilterChanged = { viewModel.setTypeFilter(it) },
                isMarketTripsView = isMarketTripsView,
                onMarketTripsViewChanged = { viewModel.setMarketTripsView(it) },
                selectedTimeFilter = selectedTimeFilter,
                onTimeFilterChanged = { viewModel.setTimeFilter(it) },
                accounts = accounts,
                selectedAccountId = selectedAccountId,
                onAccountFilterChanged = { viewModel.setAccountFilter(it) },
                onOpenCustomDateRange = { showCustomDateRangeDialog = true },
                onResetFilters = {
                    viewModel.onSearchQueryChanged("")
                    viewModel.setTypeFilter(null)
                    viewModel.setCategoryFilter(null)
                    viewModel.setAccountFilter(null)
                    viewModel.setTimeFilter(TimeFilter.THIS_MONTH)
                    viewModel.setMarketTripsView(false)
                    isSearchActive = false
                },
                activeFilterCount = activeFilterCount,
                strings = strings,
                onOpenFilterSheet = { showFilterSheet = true }
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isMarketTripsView) {
                MarketTripsHistoryView(
                    marketLists = marketLists,
                    currencySymbol = userProfile.currencySymbol,
                    isBangla = strings.isBangla,
                    useBengaliNumerals = userProfile.useBengaliNumerals || strings.isBangla,
                    strings = strings,
                    onDeleteTrip = { trip -> tripToDelete = trip },
                    onBackToTransactions = { viewModel.setMarketTripsView(false) }
                )
            } else if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val hasActiveFilters = activeFilterCount > 0
                    val hasAnyTransactions = allTransactions.isNotEmpty()
                    EmptyStateCard(
                        icon = Icons.Outlined.ReceiptLong,
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
                                hasActiveFilters -> {
                                    viewModel.onSearchQueryChanged("")
                                    viewModel.setTypeFilter(null)
                                    viewModel.setCategoryFilter(null)
                                    viewModel.setAccountFilter(null)
                                    viewModel.setTimeFilter(TimeFilter.THIS_MONTH)
                                    viewModel.setMarketTripsView(false)
                                    isSearchActive = false
                                }
                                hasAnyTransactions -> {
                                    viewModel.setTimeFilter(TimeFilter.ALL)
                                }
                                else -> {
                                    onAddTransactionForDate?.invoke(System.currentTimeMillis())
                                }
                            }
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    groupedTransactions.forEach { (dateGroup, itemsInGroup) ->
                        var groupExpense = 0.0
                        var groupIncome = 0.0
                        for (item in itemsInGroup) {
                            if (item.type == TransactionType.EXPENSE) groupExpense += item.amount
                            else if (item.type == TransactionType.INCOME) groupIncome += item.amount
                        }
                        val net = groupIncome - groupExpense

                        item(key = "header_$dateGroup") {
                            TransactionDateGroupHeader(
                                dateGroup = dateGroup,
                                itemsCount = itemsInGroup.size,
                                netAmount = net,
                                currencySymbol = userProfile.currencySymbol,
                                isBangla = strings.isBangla
                            )
                        }

                        item(key = "card_$dateGroup") {
                            TransactionGroupCard(
                                itemsInGroup = itemsInGroup,
                                marketLists = marketLists,
                                userProfile = userProfile,
                                isBangla = strings.isBangla,
                                onEditTransaction = onEditTransaction,
                                onDeleteTransaction = { deletedTx ->
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
                                onDeleteMarketTrip = { tripToDelete = it },
                                onSelectTransaction = { selectedTxForDetail = it },
                                hasMultipleAccounts = accounts.size > 1
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
        )

        // Detail Bottom Sheet when tapping a transaction row
        selectedTxForDetail?.let { tx ->
            AppBottomSheet(
                onDismissRequest = { selectedTxForDetail = null }
            ) {
                TransactionDetailSheet(
                    transaction = tx,
                    currencySymbol = userProfile.currencySymbol,
                    isBangla = strings.isBangla,
                    useBengaliNumerals = userProfile.useBengaliNumerals || strings.isBangla,
                    onEdit = {
                        selectedTxForDetail = null
                        onEditTransaction(it)
                    },
                    onDelete = { deletedTx ->
                        selectedTxForDetail = null
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
                    onDismiss = { selectedTxForDetail = null }
                )
            }
        }

        // Filter Bottom Sheet when tapping "Filters"
        if (showFilterSheet) {
            AppBottomSheet(
                onDismissRequest = { showFilterSheet = false }
            ) {
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
                        showFilterSheet = false
                        showCustomDateRangeDialog = true
                    },
                    onResetAll = {
                        viewModel.onSearchQueryChanged("")
                        viewModel.setTypeFilter(null)
                        viewModel.setCategoryFilter(null)
                        viewModel.setAccountFilter(null)
                        viewModel.setTimeFilter(TimeFilter.THIS_MONTH)
                        viewModel.setMarketTripsView(false)
                        isSearchActive = false
                    },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }

        TransactionsDialogs(
            showShareSheet = showShareSheet,
            onDismissShareSheet = {
                showShareSheet = false
                sharingTargetDateMillis = null
            },
            sharingTargetDateMillis = sharingTargetDateMillis,
            tripToDelete = tripToDelete,
            onDismissDeleteTrip = { tripToDelete = null },
            onConfirmDeleteTrip = { tripId ->
                viewModel.deleteMarketTrip(tripId)
                tripToDelete = null
            },
            showExportDialog = showExportDialog,
            onDismissExportDialog = { showExportDialog = false },
            onOpenShareSlipFromExport = {
                sharingTargetDateMillis = null
                showShareSheet = true
            },
            showCustomDateRangeDialog = showCustomDateRangeDialog,
            onDismissCustomDateRange = { showCustomDateRangeDialog = false },
            onConfirmDateRange = { start, end ->
                viewModel.setCustomDateRange(start, end)
            },
            transactions = transactions,
            userProfile = userProfile,
            strings = strings,
            scope = scope
        )
    }
}
