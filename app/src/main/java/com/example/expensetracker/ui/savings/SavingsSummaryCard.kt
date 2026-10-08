package com.example.expensetracker.ui.savings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Locale

/**
 * Hero summary card displaying overall savings goal metrics
 */
@Composable
fun SavingsSummaryCard(
    goals: List<SavingsGoal>,
    currencySymbol: String,
    isPrivacyMode: Boolean = false,
    onAddGoalClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val totalTarget = remember(goals) { goals.sumOf { it.targetAmount } }
    val totalSaved = remember(goals) { goals.sumOf { it.savedAmount } }
    val overallProgress = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toFloat().coerceIn(0f, 100f) else 0f
    val completedCount = remember(goals) { goals.count { it.isCompleted } }

    val animatedProgress by animateFloatAsState(
        targetValue = overallProgress / 100f,
        label = "overallSavingsProgress"
    )

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 18.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Savings,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.savingsGoalsTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = if (strings.isBangla) {
                                "${convertToBengaliDigits(completedCount.toString())}টি লক্ষ্য পূরণ / ${convertToBengaliDigits(goals.size.toString())}টি মোট"
                            } else {
                                "$completedCount completed / ${goals.size} total"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorAccent.copy(alpha = 0.12f),
                    modifier = Modifier.clickable { onAddGoalClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.Add, null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                        Text(
                            text = if (strings.isBangla) "নতুন লক্ষ্য" else "New Goal",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total figures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = strings.savedAmount,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isPrivacyMode) "••••••" else formatCurrency(totalSaved, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorSuccessIncome
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${strings.targetAmount}: ${if (isPrivacyMode) "••••••" else formatCurrency(totalTarget, currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                    Text(
                        text = if (strings.isBangla) {
                            "${convertToBengaliDigits(String.format(Locale.US, "%.1f", overallProgress))}% সঞ্চিত"
                        } else {
                            "${String.format(Locale.US, "%.1f", overallProgress)}% Saved"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ColorAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorAccent,
                trackColor = ColorBorder
            )
        }
    }
}
