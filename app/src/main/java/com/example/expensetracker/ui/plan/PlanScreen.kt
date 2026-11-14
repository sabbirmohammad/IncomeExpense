package com.example.expensetracker.ui.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.bills.BillsContent
import com.example.expensetracker.ui.bills.BillsViewModel
import com.example.expensetracker.ui.budget.BudgetContent
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.budget.SavingsGoalsContent
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PulseSegmented
import com.example.expensetracker.ui.navigation.PlanSegment
import com.example.expensetracker.ui.receivables.ReceivablesContent
import com.example.expensetracker.ui.receivables.ReceivablesViewModel

/**
 * PlanScreen:
 * Pulse Tab 4 (Plan / পরিকল্পনা):
 * - 4-Segment PulseSegmented:
 *     0: Budgets (বাজেট)
 *     1: Bills (বিল)
 *     2: Goals (লক্ষ্য)
 *     3: Debts (ধার ও দেনা)
 * - Persists selected segment across navigation via AdaptiveNavigationState / rememberSaveable
 * - Handles deep links (e.g., bill reminder -> Bills, budget reminder -> Budgets)
 */
@Composable
fun PlanScreen(
    modifier: Modifier = Modifier,
    selectedSegment: PlanSegment = PlanSegment.BUDGETS,
    onSegmentSelected: (PlanSegment) -> Unit = {},
    budgetViewModel: BudgetViewModel = viewModel(),
    billsViewModel: BillsViewModel = viewModel(),
    receivablesViewModel: ReceivablesViewModel = viewModel(),
    initialSegment: Int = 0
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val segmentTitles = remember(
        strings.planSegmentBudgets,
        strings.planSegmentBills,
        strings.planSegmentGoals,
        strings.planSegmentDebts
    ) {
        listOf(
            strings.planSegmentBudgets,
            strings.planSegmentBills,
            strings.planSegmentGoals,
            strings.planSegmentDebts
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        // 4-way Pulse Segmented Control
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            PulseSegmented(
                items = segmentTitles,
                selectedIndex = when (selectedSegment) {
                    PlanSegment.BUDGETS -> 0
                    PlanSegment.BILLS -> 1
                    PlanSegment.GOALS -> 2
                    PlanSegment.DEBTS -> 3
                },
                onItemSelected = { index ->
                    val target = when (index) {
                        0 -> PlanSegment.BUDGETS
                        1 -> PlanSegment.BILLS
                        2 -> PlanSegment.GOALS
                        else -> PlanSegment.DEBTS
                    }
                    onSegmentSelected(target)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Active Segment Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedSegment) {
                PlanSegment.BUDGETS -> {
                    BudgetContent(viewModel = budgetViewModel)
                }
                PlanSegment.BILLS -> {
                    BillsContent(viewModel = billsViewModel)
                }
                PlanSegment.GOALS -> {
                    SavingsGoalsContent(viewModel = budgetViewModel)
                }
                PlanSegment.DEBTS -> {
                    ReceivablesContent(viewModel = receivablesViewModel)
                }
            }
        }
    }
}
