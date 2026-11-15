package com.example.expensetracker.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            BudgetHeaderTabs(
                selectedTab = selectedTab,
                isPrivacyMode = isPrivacyMode,
                savingsGoalsCount = savingsGoals.size,
                onMenuClick = onMenuClick,
                onNavigateBack = onNavigateBack,
                onNavigateToSettings = onNavigateToSettings,
                onTogglePrivacy = { viewModel.togglePrivacyMode() },
                onTabSelected = { viewModel.setSelectedTab(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == BudgetScreenTab.BUDGETS) {
                BudgetContent(viewModel = viewModel, modifier = Modifier.weight(1f))
            } else {
                SavingsGoalsContent(viewModel = viewModel, modifier = Modifier.weight(1f))
            }
        }
    }
}
