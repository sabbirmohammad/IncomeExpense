package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun MarketTargetBudgetRow(
    targetBudget: Double?,
    estimatedBudget: Double?,
    totalSpent: Double,
    currencySymbol: String,
    isBangla: Boolean,
    onBudgetClick: () -> Unit,
    canSetBudget: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    if (targetBudget != null && targetBudget > 0) {
        val budgetRemaining = targetBudget - totalSpent
        val isOverBudget = budgetRemaining < 0
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .clickable(enabled = canSetBudget) { onBudgetClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = appColors.textSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = if (isBangla) "বাজেট: ${formatCurrency(targetBudget, currencySymbol)}"
                    else "Budget: ${formatCurrency(targetBudget, currencySymbol)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = appColors.textSecondary
                )
            }

            Text(
                text = if (isOverBudget) {
                    if (isBangla) "বাজেট ছাড়িয়েছে (+${formatCurrency(-budgetRemaining, currencySymbol)})"
                    else "Over budget (+${formatCurrency(-budgetRemaining, currencySymbol)})"
                } else {
                    if (isBangla) "বাকি: ${formatCurrency(budgetRemaining, currencySymbol)}"
                    else "Remaining: ${formatCurrency(budgetRemaining, currencySymbol)}"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isOverBudget) appColors.expense else appColors.income
            )
        }
    } else if (estimatedBudget != null && estimatedBudget > 0) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isBangla) "💡 আনুমানিক মোট বাজেট: ${formatCurrency(estimatedBudget, currencySymbol)}"
                    else "💡 Est. Budget: ${formatCurrency(estimatedBudget, currencySymbol)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.accent
                )
            }

            if (canSetBudget) {
                Text(
                    text = if (isBangla) "+ কাস্টম বাজেট" else "+ Custom Budget",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.textSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onBudgetClick() }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    } else if (canSetBudget) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = if (isBangla) "+ টার্গেট বাজেট নির্ধারণ" else "+ Set Target Budget",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.accent,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onBudgetClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}
