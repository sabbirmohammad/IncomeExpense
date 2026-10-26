package com.example.expensetracker.ui.savings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.expensetracker.theme.LocalAppColors

/**
 * Individual Savings Goal Item Card
 */
@Composable
fun SavingsGoalCard(
    goal: SavingsGoal,
    currencySymbol: String,
    isPrivacyMode: Boolean = false,
    monthlyIncome: Double = 0.0,
    monthlyExpense: Double = 0.0,
    onAddContribution: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val goalColor = remember(goal.colorHex) { parseGoalColor(goal.colorHex) }
    val icon = remember(goal.icon) { getSavingsGoalIcon(goal.icon) }
    val dateFormat = remember(strings.isBangla) {
        SimpleDateFormat("d MMM yyyy", DateTimeHelper.getLocale(strings.isBangla))
    }

    val animatedProgress by animateFloatAsState(
        targetValue = (goal.progressPercentage / 100f).coerceIn(0f, 1f),
        label = "goalProgress_${goal.id}"
    )

    val daysRemaining = remember(goal.targetDateMillis) {
        goal.targetDateMillis?.let { targetMillis ->
            val diff = targetMillis - System.currentTimeMillis()
            (diff / (1000 * 60 * 60 * 24)).toInt()
        }
    }

    AppCard(
        modifier = modifier.fillMaxWidth(),
        padding = 16.dp
    ) {
        Column {
            SavingsGoalCardHeader(
                goal = goal,
                goalColor = goalColor,
                icon = icon,
                daysRemaining = daysRemaining,
                onAddContribution = onAddContribution,
                onEdit = onEdit,
                onDelete = onDelete
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Progress Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(strings.savedAmount, fontSize = 11.sp, color = appColors.textMuted)
                    Text(
                        text = if (isPrivacyMode) "••••••" else formatCurrency(goal.savedAmount, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (goal.isCompleted) appColors.income else appColors.textPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${strings.targetAmount}: ${if (isPrivacyMode) "••••••" else formatCurrency(goal.targetAmount, currencySymbol)}",
                        fontSize = 11.sp,
                        color = appColors.textSecondary
                    )
                    Text(
                        text = if (strings.isBangla) {
                            "${convertToBengaliDigits(String.format(Locale.US, "%.0f", goal.progressPercentage))}%"
                        } else {
                            "${String.format(Locale.US, "%.0f", goal.progressPercentage)}%"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = goalColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = goalColor,
                trackColor = appColors.border
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (goal.targetDateMillis != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = appColors.textMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val formattedDate = dateFormat.format(Date(goal.targetDateMillis))
                        Text(
                            text = if (strings.isBangla) convertToBengaliDigits(formattedDate) else formattedDate,
                            fontSize = 11.sp,
                            color = appColors.textSecondary
                        )
                    }
                } else {
                    Text(
                        text = if (goal.remainingAmount > 0) {
                            "${if (strings.isBangla) "বাকি" else "Left"}: ${if (isPrivacyMode) "••••" else formatCurrency(goal.remainingAmount, currencySymbol, globalBengaliNumeralsEnabled || strings.isBangla)}"
                        } else {
                            strings.goalReachedCelebration
                        },
                        fontSize = 11.sp,
                        color = if (goal.remainingAmount <= 0) appColors.income else appColors.textMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = goalColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, goalColor.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onAddContribution() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.Add, null, tint = goalColor, modifier = Modifier.size(13.dp))
                        Text(
                            text = if (strings.isBangla) "জমানো" else "Contribute",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = goalColor
                        )
                    }
                }
            }
        }
    }
}
