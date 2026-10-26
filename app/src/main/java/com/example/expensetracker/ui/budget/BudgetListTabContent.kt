package com.example.expensetracker.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun BudgetListTabContent(
    overallStatus: CategoryBudgetStatus?,
    categoryBudgetStatuses: List<CategoryBudgetStatus>,
    categories: List<Category>,
    currencySymbol: String,
    onEditOverallBudget: () -> Unit,
    onSetOverallBudget: () -> Unit,
    onAddCategoryBudget: () -> Unit,
    onEditCategoryBudget: (Budget) -> Unit,
    onDeleteCategoryBudget: (Budget) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overall Monthly Budget Status Card
        item {
            OverallBudgetCard(
                overallStatus = overallStatus,
                currencySymbol = currencySymbol,
                onEditBudgetClick = onEditOverallBudget,
                onSetBudgetClick = onSetOverallBudget
            )
        }

        // Section header: "Category budgets" + "N active" badge + Add button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.categoryBudgets,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        ),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .background(appColors.surfaceVariant)
                            .border(1.dp, appColors.border, RoundedCornerShape(SmallElementRadius))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (strings.isBangla) "${categoryBudgetStatuses.size}টি সক্রিয়" else "${categoryBudgetStatuses.size} active",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                IconButton(onClick = onAddCategoryBudget) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add category budget",
                        tint = appColors.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Category budget cards or Empty State
        if (categoryBudgetStatuses.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    headline = strings.setFirstCategoryBudget,
                    explanation = strings.setFirstCategoryBudgetHint,
                    buttonText = strings.addCategoryBudget,
                    onButtonClick = onAddCategoryBudget
                )
            }
        } else {
            items(categoryBudgetStatuses, key = { it.budget.id }) { item ->
                val cat = categories.find { it.id == item.budget.categoryId }
                CategoryBudgetCard(
                    item = item,
                    category = cat,
                    currencySymbol = currencySymbol,
                    onEditClick = { onEditCategoryBudget(item.budget) },
                    onDeleteClick = { onDeleteCategoryBudget(item.budget) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}
