package com.example.expensetracker.ui.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.savings.SavingsGoalCard
import com.example.expensetracker.ui.savings.SavingsSummaryCard
import java.util.Locale
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun SavingsGoalsTabContent(
    savingsGoals: List<SavingsGoal>,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    onAddGoalClick: () -> Unit,
    onAddContribution: (SavingsGoal) -> Unit,
    onEditGoal: (SavingsGoal) -> Unit,
    onDeleteGoal: (SavingsGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SavingsSummaryCard(
                goals = savingsGoals,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                onAddGoalClick = onAddGoalClick
            )
        }

        if (savingsGoals.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(CardGap))
                EmptyStateCard(
                    icon = Icons.Outlined.Savings,
                    headline = strings.noSavingsGoalsFound,
                    explanation = strings.noSavingsGoalsHint,
                    buttonText = "+ ${strings.addSavingsGoal}",
                    onButtonClick = onAddGoalClick
                )
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${strings.savingsGoalsTitle.uppercase(Locale.getDefault())} (${if (strings.isBangla) convertToBengaliDigits(savingsGoals.size.toString()) else savingsGoals.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.textSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "+ ${strings.addSavingsGoal}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.accent,
                        modifier = Modifier.clickable { onAddGoalClick() }
                    )
                }
            }

            items(savingsGoals, key = { it.id }) { goal ->
                SavingsGoalCard(
                    goal = goal,
                    currencySymbol = currencySymbol,
                    isPrivacyMode = isPrivacyMode,
                    onAddContribution = { onAddContribution(goal) },
                    onEdit = { onEditGoal(goal) },
                    onDelete = { onDeleteGoal(goal) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}
