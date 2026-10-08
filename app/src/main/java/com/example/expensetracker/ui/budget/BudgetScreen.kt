package com.example.expensetracker.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val budgetStatuses by viewModel.budgetStatuses.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedBudgetToEdit by remember { mutableStateOf<Budget?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Budget?>(null) }

    val selectedTab by viewModel.selectedTab.collectAsState()
    val savingsGoals by viewModel.savingsGoals.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()

    var showAddSavingsGoalSheet by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalForContribution by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalToDelete by remember { mutableStateOf<SavingsGoal?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    val expenseCategories = remember(categories) { categories.filter { it.type == TransactionType.EXPENSE } }
    val overallStatus = remember(budgetStatuses) { budgetStatuses.find { it.budget.categoryId == null } }
    val categoryBudgetStatuses = remember(budgetStatuses) { budgetStatuses.filter { it.budget.categoryId != null } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
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
            BudgetListTabContent(
                overallStatus = overallStatus,
                categoryBudgetStatuses = categoryBudgetStatuses,
                categories = categories,
                currencySymbol = userProfile.currencySymbol,
                onEditOverallBudget = {
                    selectedBudgetToEdit = overallStatus?.budget
                    showAddDialog = true
                },
                onSetOverallBudget = {
                    selectedBudgetToEdit = null
                    showAddDialog = true
                },
                onAddCategoryBudget = {
                    selectedBudgetToEdit = null
                    showAddDialog = true
                },
                onEditCategoryBudget = {
                    selectedBudgetToEdit = it
                    showAddDialog = true
                },
                onDeleteCategoryBudget = { showDeleteConfirmDialog = it }
            )
        } else {
            SavingsGoalsTabContent(
                savingsGoals = savingsGoals,
                currencySymbol = userProfile.currencySymbol,
                isPrivacyMode = isPrivacyMode,
                geminiApiKey = userProfile.geminiApiKey,
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
        }
    }

    if (showAddDialog) {
        AddEditBudgetDialog(
            budgetToEdit = selectedBudgetToEdit,
            expenseCategories = expenseCategories,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showAddDialog = false },
            onSave = {
                viewModel.saveBudget(it)
                showAddDialog = false
            }
        )
    }

    if (showDeleteConfirmDialog != null) {
        val target = showDeleteConfirmDialog!!
        DeleteBudgetConfirmDialog(
            targetBudget = target,
            onDismiss = { showDeleteConfirmDialog = null },
            onConfirmDelete = {
                viewModel.deleteBudget(target.id)
                showDeleteConfirmDialog = null
            }
        )
    }

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
