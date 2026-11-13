package com.example.expensetracker.ui.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.bills.BillsScreen
import com.example.expensetracker.ui.bills.BillsViewModel
import com.example.expensetracker.ui.budget.BudgetScreen
import com.example.expensetracker.ui.budget.BudgetScreenTab
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.components.AppTopBar
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.ScrollableSegmentedControl
import com.example.expensetracker.ui.receivables.ReceivablesScreen
import com.example.expensetracker.ui.receivables.ReceivablesViewModel

/**
 * PlanScreen:
 * Living Ledger 2.0 Tab 5 (পরিকল্পনা):
 * - Top: AppTopBar (Plan / পরিকল্পনা)
 * - 4-Segment ScrollableSegmentedControl:
 *     0: Budgets (বাজেট)
 *     1: Goals (লক্ষ্য)
 *     2: Bills (বিল)
 *     3: Debts (ধার ও দেনা)
 * - Persists selected segment across navigation via rememberSaveable
 * - Handles deep links (e.g., bill reminder -> Bills, budget reminder -> Budgets)
 */
@Composable
fun PlanScreen(
    budgetViewModel: BudgetViewModel,
    billsViewModel: BillsViewModel,
    receivablesViewModel: ReceivablesViewModel,
    modifier: Modifier = Modifier,
    initialSegment: Int = 0
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    var selectedSegment by rememberSaveable { mutableIntStateOf(initialSegment) }

    val segmentTitles = remember(
        strings.planSegmentBudgets,
        strings.planSegmentGoals,
        strings.planSegmentBills,
        strings.planSegmentDebts
    ) {
        listOf(
            strings.planSegmentBudgets,
            strings.planSegmentGoals,
            strings.planSegmentBills,
            strings.planSegmentDebts
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        // AppTopBar (no back button on top-level destination)
        AppTopBar(
            title = strings.navPlan
        )

        // 4-way Segmented Control
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            ScrollableSegmentedControl(
                items = segmentTitles,
                selectedIndex = selectedSegment,
                onItemSelected = { index ->
                    selectedSegment = index
                    // Keep sub-tab in BudgetViewModel synchronized
                    if (index == 0) {
                        budgetViewModel.setSelectedTab(BudgetScreenTab.BUDGETS)
                    } else if (index == 1) {
                        budgetViewModel.setSelectedTab(BudgetScreenTab.SAVINGS_GOALS)
                    }
                }
            )
        }

        // Active Segment Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedSegment) {
                0 -> {
                    // Budgets
                    budgetViewModel.setSelectedTab(BudgetScreenTab.BUDGETS)
                    BudgetScreen(
                        viewModel = budgetViewModel
                    )
                }
                1 -> {
                    // Goals
                    budgetViewModel.setSelectedTab(BudgetScreenTab.SAVINGS_GOALS)
                    BudgetScreen(
                        viewModel = budgetViewModel
                    )
                }
                2 -> {
                    // Bills
                    BillsScreen(
                        viewModel = billsViewModel
                    )
                }
                3 -> {
                    // Debts (Receivables & Payables)
                    ReceivablesScreen(
                        viewModel = receivablesViewModel
                    )
                }
            }
        }
    }
}
