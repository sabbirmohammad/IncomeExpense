package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import com.example.expensetracker.ui.components.PulseSnackbarHost
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
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import kotlinx.coroutines.launch

/**
 * TransactionsContent:
 * Extracted body of TransactionsScreen without top bar/back navigation,
 * suitable for hosting inside MoneyScreen's Activity segment or standalone.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsContent(
    viewModel: TransactionsViewModel,
    onEditTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    onAddTransactionForDate: ((Long) -> Unit)? = null
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
        groupTransactionsByDate(transactions, strings.today, strings.yesterday, strings.isBangla, userProfile.useBengaliNumerals)
    }

    val activeFilterCount = (if (selectedType != null || isMarketTripsView) 1 else 0) +
        (if (selectedCategoryId != null) 1 else 0) +
        (if (selectedAccountId != null) 1 else 0) +
        (if (selectedTimeFilter != TimeFilter.THIS_MONTH) 1 else 0) +
        (if (searchQuery.isNotBlank()) 1 else 0)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val dialogState = rememberTransactionsDialogState()
    var isSearchActive by remember { mutableStateOf(searchQuery.isNotBlank()) }

    Box(modifier = modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                onOpenCustomDateRange = { dialogState.showCustomDateRangeDialog = true },
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
                onOpenFilterSheet = { dialogState.showFilterSheet = true }
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isMarketTripsView) {
                MarketTripsHistoryView(
                    marketLists = marketLists,
                    currencySymbol = userProfile.currencySymbol,
                    isBangla = strings.isBangla,
                    useBengaliNumerals = userProfile.useBengaliNumerals || strings.isBangla,
                    strings = strings,
                    onDeleteTrip = { trip -> dialogState.tripToDelete = trip },
                    onBackToTransactions = { viewModel.setMarketTripsView(false) }
                )
            } else if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp), contentAlignment = Alignment.Center) {
                    val hasActiveFilters = activeFilterCount > 0
                    val hasAnyTransactions = allTransactions.isNotEmpty()
                    TransactionsEmptyStateCard(
                        hasActiveFilters = hasActiveFilters,
                        hasAnyTransactions = hasAnyTransactions,
                        strings = strings,
                        onResetFilters = {
                            viewModel.onSearchQueryChanged("")
                            viewModel.setTypeFilter(null)
                            viewModel.setCategoryFilter(null)
                            viewModel.setAccountFilter(null)
                            viewModel.setTimeFilter(TimeFilter.THIS_MONTH)
                            viewModel.setMarketTripsView(false)
                            isSearchActive = false
                        },
                        onViewAllTransactions = { viewModel.setTimeFilter(TimeFilter.ALL) },
                        onAddTransaction = { onAddTransactionForDate?.invoke(System.currentTimeMillis()) }
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    groupedTransactions.forEach { (dateGroup, itemsInGroup) ->
                        val net = itemsInGroup.sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }

                        item(key = "header_$dateGroup") {
                            TransactionDateGroupHeader(dateGroup, itemsInGroup.size, net, userProfile.currencySymbol, strings.isBangla)
                        }

                        item(key = "card_$dateGroup") {
                            TransactionGroupCard(
                                itemsInGroup = itemsInGroup,
                                marketLists = marketLists,
                                userProfile = userProfile,
                                isBangla = strings.isBangla,
                                onEditTransaction = onEditTransaction,
                                onDeleteTransaction = { deletedTx ->
                                    dialogState.txToDelete = deletedTx
                                },
                                onDeleteMarketTrip = { dialogState.tripToDelete = it },
                                onSelectTransaction = { dialogState.selectedTxForDetail = it },
                                hasMultipleAccounts = accounts.size > 1,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }

                    item(key = "bottom_spacer") {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        PulseSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
        )

        TransactionsSheetsHost(
            dialogState = dialogState,
            viewModel = viewModel,
            accounts = accounts,
            categories = categories,
            userProfile = userProfile,
            strings = strings,
            scope = scope,
            snackbarHostState = snackbarHostState,
            onEditTransaction = onEditTransaction,
            onResetSearchActive = { isSearchActive = false }
        )
    }
}
