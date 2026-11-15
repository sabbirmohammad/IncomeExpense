package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Box(modifier = modifier.fillMaxSize().background(appColors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AccountsTopBar(
                title = strings.accountsTitle,
                onMenuClick = onMenuClick,
                onNavigateBack = onNavigateBack
            )

            AccountsContent(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
