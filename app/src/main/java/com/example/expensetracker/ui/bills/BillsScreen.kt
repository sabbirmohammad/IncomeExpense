package com.example.expensetracker.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun BillsScreen(
    viewModel: BillsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current

    Box(modifier = modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            BillsTopBar(onNavigateBack = onNavigateBack, onMenuClick = onMenuClick, title = strings.billsTitle)

            BillsContent(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
