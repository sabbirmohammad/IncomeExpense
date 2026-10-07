package com.example.expensetracker.ui.dashboard

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
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun DashboardSavingsGoalsWidget(
    savingsGoals: List<SavingsGoal>,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    onNavigateToBudgets: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val totalSavedGoals = remember(savingsGoals) { savingsGoals.sumOf { it.savedAmount } }
    val totalTargetGoals = remember(savingsGoals) { savingsGoals.sumOf { it.targetAmount } }
    val overallGoalProgress = if (totalTargetGoals > 0) ((totalSavedGoals / totalTargetGoals) * 100).toFloat().coerceIn(0f, 100f) else 0f
    val completedGoalsCount = remember(savingsGoals) { savingsGoals.count { it.isCompleted } }

    AppCard(
        modifier = modifier
            .padding(horizontal = ScreenPaddingHorizontal)
            .clickable { onNavigateToBudgets?.invoke() },
        padding = 14.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Savings,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = strings.savingsGoalsTitle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isPrivacyMode) "••••••"
                            else "${formatCurrency(totalSavedGoals, currencySymbol)} / ${formatCurrency(totalTargetGoals, currencySymbol)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorSuccessIncome
                        )
                        Text(
                            text = if (strings.isBangla) {
                                "${convertToBengaliDigits(completedGoalsCount.toString())}টি অর্জিত · ${convertToBengaliDigits(savingsGoals.size.toString())}টি মোট লক্ষ্য"
                            } else {
                                "$completedGoalsCount reached · ${savingsGoals.size} total goal(s)"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = ColorTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { overallGoalProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorSuccessIncome,
                trackColor = ColorBorder
            )
        }
    }
}

@Composable
fun HomeInsightCard(
    icon: ImageVector,
    iconColor: Color,
    headline: String,
    sentence: String,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        padding = 14.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(SmallElementRadius))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sentence,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ColorTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
