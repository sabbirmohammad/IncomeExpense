package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

@Composable
internal fun DashboardDebtsCollapsible(
    debtBookSummary: DebtBookSummary,
    receivablesSummary: ReceivablesSummary,
    currencySymbol: String,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenFull: (() -> Unit)?,
    strings: Strings
) {
    val appColors = LocalAppColors.current
    val netFormatted = if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.netBalance, currencySymbol, useBengaliNumerals)
    DashboardCollapsibleSection(
        icon = Icons.AutoMirrored.Outlined.AssignmentReturn,
        title = strings.debtsLedgerTitle,
        metricText = "${strings.netPosition}: $netFormatted",
        metricColor = if (debtBookSummary.netBalance >= 0) appColors.income else appColors.expense,
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand,
        onOpenFull = onOpenFull
    ) {
        DashboardDebtsLedgerWidget(
            debtBookSummary = debtBookSummary,
            currencySymbol = currencySymbol,
            isPrivacyMode = isPrivacyMode,
            onNavigateToReceivables = onOpenFull,
            modifier = Modifier.padding(0.dp)
        )
    }
}

@Composable
internal fun DashboardSavingsCollapsible(
    savingsGoals: List<SavingsGoal>,
    currencySymbol: String,
    isBn: Boolean,
    isPrivacyMode: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenFull: (() -> Unit)?,
    strings: Strings
) {
    val appColors = LocalAppColors.current
    val completedGoalsCount = savingsGoals.count { it.isCompleted }
    val savingsMetric = if (isBn) {
        "${convertToBengaliDigits(completedGoalsCount.toString())}/${convertToBengaliDigits(savingsGoals.size.toString())}টি অর্জিত"
    } else {
        "$completedGoalsCount of ${savingsGoals.size} reached"
    }

    DashboardCollapsibleSection(
        icon = Icons.Outlined.Savings,
        title = strings.savingsGoalsTitle,
        metricText = savingsMetric,
        metricColor = appColors.income,
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand,
        onOpenFull = onOpenFull
    ) {
        DashboardSavingsGoalsWidget(
            savingsGoals = savingsGoals,
            currencySymbol = currencySymbol,
            isPrivacyMode = isPrivacyMode,
            onNavigateToBudgets = onOpenFull,
            modifier = Modifier.padding(0.dp)
        )
    }
}
