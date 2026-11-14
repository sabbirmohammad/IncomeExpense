package com.example.expensetracker.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.LocalAppColors

/**
 * SavingsGoalsContent:
 * Extracted body of the Savings Goals tab in BudgetScreen without top bar/back navigation,
 * suitable for hosting inside PlanScreen's Goals segment or standalone.
 */
@Composable
fun SavingsGoalsContent(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()

    var showAddSavingsGoalSheet by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalForContribution by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalToDelete by remember { mutableStateOf<SavingsGoal?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
            .padding(horizontal = 16.dp)
    ) {
        SavingsGoalsTabContent(
            savingsGoals = savingsGoals,
            currencySymbol = userProfile.currencySymbol,
            isPrivacyMode = isPrivacyMode,
            onAddGoalClick = {
                goalToEdit = null
                showAddSavingsGoalSheet = true
            },
            onAddContribution = { goalForContribution = it },
            onEditGoal = {
                goalToEdit = it
                showAddSavingsGoalSheet = true
            },
            onDeleteGoal = { goalToDelete = it }
        )

        SavingsGoalModals(
            viewModel = viewModel,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            showAddSavingsGoalSheet = showAddSavingsGoalSheet,
            goalToEdit = goalToEdit,
            goalForContribution = goalForContribution,
            goalToDelete = goalToDelete,
            showConfetti = showConfetti,
            onDismissAddEdit = {
                showAddSavingsGoalSheet = false
                goalToEdit = null
            },
            onDismissContribution = { goalForContribution = null },
            onDismissDelete = { goalToDelete = null },
            onConfettiDismiss = { showConfetti = false },
            onGoalCelebrated = { showConfetti = true }
        )
    }
}
