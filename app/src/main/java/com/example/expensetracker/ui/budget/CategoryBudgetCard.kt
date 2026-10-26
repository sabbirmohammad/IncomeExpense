package com.example.expensetracker.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.AppProgressBar
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun CategoryBudgetCard(
    item: CategoryBudgetStatus,
    category: Category?,
    currencySymbol: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val catColor = category?.colorHex ?: "#3b6ef5"
    val barFillColor = when {
        item.isOverBudget -> appColors.expense
        item.progressPercent > 0.8f -> appColors.warning
        else -> appColors.income
    }

    AppCard(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CategoryIconBadge(
                        iconName = category?.icon ?: "MoreHoriz",
                        colorHex = catColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = getLocalizedCategoryName(item.budget.categoryName, strings.isBangla),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = if (strings.isBangla)
                                "${formatCurrency(item.spentAmount, currencySymbol)} / ${formatCurrency(item.budget.amount, currencySymbol)}"
                            else
                                "${formatCurrency(item.spentAmount, currencySymbol)} of ${formatCurrency(item.budget.amount, currencySymbol)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = appColors.expense,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AppProgressBar(
                progress = item.progressPercent,
                height = 6.dp,
                fillColor = barFillColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (strings.isBangla) "${(item.progressPercent * 100).toInt()}% ব্যবহৃত" else "${(item.progressPercent * 100).toInt()}% used",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = barFillColor
                )
                Text(
                    text = if (item.isOverBudget)
                        (if (strings.isBangla) "সীমা অতিক্রম করেছে!" else "Over limit!")
                    else
                        (if (strings.isBangla) "${formatCurrency(item.remainingAmount, currencySymbol)} বাকি" else "${formatCurrency(item.remainingAmount, currencySymbol)} remaining"),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (item.isOverBudget) appColors.expense else appColors.textSecondary
                )
            }
        }
    }
}
