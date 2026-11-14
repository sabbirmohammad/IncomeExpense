package com.example.expensetracker.ui.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

/**
 * Modern, Ultra-Slim Market Summary Card.
 * Displays progress, total spent, target budget tracker, and quick sharing.
 */
@Composable
fun MarketSummarySection(
    totalCount: Int,
    purchasedCount: Int,
    totalSpent: Double,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier,
    selectedCategoryFilter: String? = null,
    onCategorySelected: ((String?) -> Unit)? = null,
    targetBudget: Double? = null,
    estimatedBudget: Double? = null,
    onSetTargetBudget: ((Double?) -> Unit)? = null,
    onShareClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    if (totalCount == 0) return

    val progress = if (totalCount > 0) purchasedCount.toFloat() / totalCount.toFloat() else 0f
    val percent = (progress * 100).toInt()
    var showBudgetDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (progress >= 1f) appColors.income else appColors.accent)
                    )
                    Text(
                        text = if (isBangla)
                            "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি কেনা সম্পন্ন (${convertToBengaliDigits(percent.toString())}%)"
                        else
                            "$purchasedCount/$totalCount items ($percent%)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.textPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isBangla) "মোট খরচ:" else "Spent:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = appColors.textSecondary
                    )
                    Text(
                        text = formatCurrency(totalSpent, currencySymbol),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.income
                    )

                    if (onShareClick != null) {
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = appColors.accent,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = appColors.income,
                trackColor = appColors.border.copy(alpha = 0.5f)
            )

            MarketTargetBudgetRow(
                targetBudget = targetBudget,
                estimatedBudget = estimatedBudget,
                totalSpent = totalSpent,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                onBudgetClick = { showBudgetDialog = true },
                canSetBudget = onSetTargetBudget != null
            )
        }
    }

    if (showBudgetDialog && onSetTargetBudget != null) {
        MarketTargetBudgetDialog(
            targetBudget = targetBudget,
            isBangla = isBangla,
            onSetTargetBudget = onSetTargetBudget,
            onDismiss = { showBudgetDialog = false }
        )
    }
}
