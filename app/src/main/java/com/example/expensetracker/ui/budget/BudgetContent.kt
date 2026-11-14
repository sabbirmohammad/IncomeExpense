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
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors

/**
 * BudgetContent:
 * Extracted body of the Budgets tab in BudgetScreen without top bar/back navigation,
 * suitable for hosting inside PlanScreen's Budgets segment or standalone.
 */
@Composable
fun BudgetContent(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val budgetStatuses by viewModel.budgetStatuses.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedBudgetToEdit by remember { mutableStateOf<Budget?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Budget?>(null) }

    val expenseCategories = remember(categories) { categories.filter { it.type == TransactionType.EXPENSE } }
    val overallStatus = remember(budgetStatuses) { budgetStatuses.find { it.budget.categoryId == null } }
    val categoryBudgetStatuses = remember(budgetStatuses) { budgetStatuses.filter { it.budget.categoryId != null } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
            .padding(horizontal = 16.dp)
    ) {
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
    }
}
