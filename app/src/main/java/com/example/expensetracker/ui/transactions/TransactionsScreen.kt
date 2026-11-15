package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onEditTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    onAddTransactionForDate: ((Long) -> Unit)? = null,
    onNavigateToSettings: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var isSearchActive by remember { mutableStateOf(searchQuery.isNotBlank()) }

    Box(modifier = modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TransactionsTopBar(
                title = strings.transactionsTitle,
                onMenuClick = onMenuClick,
                onExportClick = {},
                onShareClick = {},
                onNavigateToSettings = onNavigateToSettings,
                onSearchClick = { isSearchActive = !isSearchActive },
                isSearchActive = isSearchActive
            )

            TransactionsContent(
                viewModel = viewModel,
                onEditTransaction = onEditTransaction,
                onAddTransactionForDate = onAddTransactionForDate,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
