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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.LocalAppStrings
import kotlinx.coroutines.launch

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onEditTransaction: (Transaction) -> Unit,
    onAddTransactionForDate: ((Long) -> Unit)? = null,
    onNavigateToSettings: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val transactions by viewModel.filteredTransactions.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsState()
    val isMarketTripsView by viewModel.isMarketTripsView.collectAsState()
    val marketLists by viewModel.marketLists.collectAsState()

    val groupedTransactions = remember(transactions, strings.today, strings.yesterday, strings.isBangla, userProfile.useBengaliNumerals) {
        groupTransactionsByDate(
            transactions = transactions,
            todayLabel = strings.today,
            yesterdayLabel = strings.yesterday,
            isBangla = strings.isBangla,
            useBengaliNumerals = userProfile.useBengaliNumerals
        )
    }

    val activeFilterCount = (if (selectedType != null) 1 else 0) +
        (if (selectedTimeFilter != TimeFilter.ALL) 1 else 0) +
        (if (searchQuery.isNotBlank()) 1 else 0)

    var showShareSheet by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var sharingTargetDateMillis by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCustomDateRangeDialog by remember { mutableStateOf(false) }
    var tripToDelete by remember { mutableStateOf<MarketListWithItems?>(null) }
    var isSearchActive by remember { mutableStateOf(searchQuery.isNotBlank()) }

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
                onNavigateToSettings = onNavigateToSettings
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
                onOpenCustomDateRange = { showCustomDateRangeDialog = true },
                onResetFilters = {
                    viewModel.onSearchQueryChanged("")
                    viewModel.setTypeFilter(null)
                    viewModel.setCategoryFilter(null)
                    viewModel.setTimeFilter(TimeFilter.ALL)
                    viewModel.setMarketTripsView(false)
                    isSearchActive = false
                },
                activeFilterCount = activeFilterCount,
                strings = strings
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
                    EmptyStateCard(
                        icon = Icons.Outlined.ReceiptLong,
                        headline = strings.noTransactionsFound,
                        explanation = if (searchQuery.isNotEmpty() || selectedType != null || selectedCategoryId != null) {
                            strings.noTransactionsAdjustFilter
                        } else {
                            strings.startTrackingSubtitle
                        },
                        buttonText = strings.clearFilters,
                        onButtonClick = {
                            viewModel.onSearchQueryChanged("")
                            viewModel.setTypeFilter(null)
                            viewModel.setCategoryFilter(null)
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
                                onDeleteMarketTrip = { tripToDelete = it }
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
