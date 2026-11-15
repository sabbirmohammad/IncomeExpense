package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivablesScreen(
    viewModel: ReceivablesViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ReceivablesTopBar(
                selectedTab = selectedTab,
                isPrivacyMode = isPrivacyMode,
                onTogglePrivacy = { viewModel.togglePrivacyMode() },
                onSplitBillClick = {},
                onQuickAddClick = {},
                onNavigateBack = onNavigateBack,
                onMenuClick = onMenuClick
            )

            ReceivablesContent(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
