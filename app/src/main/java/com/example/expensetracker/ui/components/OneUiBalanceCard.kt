package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors

/**
 * Balance Card per Home Screen specification:
 * - Label "Total net balance" (12px secondary)
 * - Hero value (32-34px / 500, white) - single largest element on screen
 * - Two metric chips side by side: Income (green tint) and Expense (red tint)
 * - Budget progress: label row ("Budget · X% used" / "spent / limit") + progress bar
 */
@Composable
fun BalanceCard(
    netBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    budgetAmount: Double,
    budgetSpent: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || LocalAppStrings.current.isBangla,
    savingsRate: Int? = null,
    totalWalletBalance: Double? = null,
    periodLabel: String? = null,
    selectedTimeFilter: TimeFilter? = null,
    onTimeFilterSelected: ((TimeFilter) -> Unit)? = null,
    isPrivacyMode: Boolean = false,
    onTogglePrivacyMode: (() -> Unit)? = null,
    onIncomeClick: (() -> Unit)? = null,
    onExpenseClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    var showWalletAsHero by remember { mutableStateOf(totalWalletBalance != null) }

    val displayedHero = if (showWalletAsHero && totalWalletBalance != null) totalWalletBalance else netBalance
    val currentHeaderLabel = if (showWalletAsHero && totalWalletBalance != null) {
        strings.totalWalletBalance
    } else {
        strings.totalBalance
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(LocalAppColors.current.border, LocalAppColors.current.border.copy(alpha = 0.5f))
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            LocalAppColors.current.surface,
                            LocalAppColors.current.surfaceVariant
                        )
                    )
                )
                .padding(top = 34.dp, bottom = 32.dp, start = 22.dp, end = 22.dp)
        ) {
            Column {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BalanceCardHeaderRow(
                        currentHeaderLabel = currentHeaderLabel,
                        displayedHero = displayedHero,
                        isPrivacyMode = isPrivacyMode,
                        hasWalletBalance = totalWalletBalance != null,
                        onToggleWallet = { showWalletAsHero = !showWalletAsHero },
                        onTogglePrivacyMode = onTogglePrivacyMode,
                        strings = strings
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedContent(
                        targetState = displayedHero,
                        label = "heroBalance",
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) { balance ->
                        ResponsiveHeroBalance(
                            amount = balance,
                            currencySymbol = currencySymbol,
                            useBengaliNumerals = useBengaliNumerals,
                            isPrivacyMode = isPrivacyMode,
                            maxFontSize = 52.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Two metric chips side by side with interactive click
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricChip(
                        type = TransactionType.INCOME,
                        amount = totalIncome,
                        currencySymbol = currencySymbol,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode,
                        modifier = Modifier.weight(1f),
                        onClick = onIncomeClick
                    )
                    MetricChip(
                        type = TransactionType.EXPENSE,
                        amount = totalExpense,
                        currencySymbol = currencySymbol,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode,
                        modifier = Modifier.weight(1f),
                        onClick = onExpenseClick
                    )
                }

                // Budget progress row + progress bar
                BalanceCardBudgetProgress(
                    budgetAmount = budgetAmount,
                    budgetSpent = budgetSpent,
                    currencySymbol = currencySymbol,
                    useBengaliNumerals = useBengaliNumerals,
                    strings = strings
                )
            }
        }
    }
}
