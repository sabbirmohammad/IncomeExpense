package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun CategoryTypeSegmentBar(
    totalCategoriesCount: Int,
    expenseCategoriesCount: Int,
    incomeCategoriesCount: Int,
    selectedTypeFilter: TransactionType?,
    onSelectTypeFilter: (TransactionType?) -> Unit,
    isBangla: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(appColors.surface)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CategoryFilterSegment(
            title = if (isBangla) "সব ($totalCategoriesCount)" else "All ($totalCategoriesCount)",
            isSelected = selectedTypeFilter == null,
            activeColor = appColors.accent,
            onClick = {
                haptics.lightTick()
                onSelectTypeFilter(null)
            },
            modifier = Modifier.weight(1f)
        )

        CategoryFilterSegment(
            title = if (isBangla) "ব্যয় ($expenseCategoriesCount)" else "Expense ($expenseCategoriesCount)",
            isSelected = selectedTypeFilter == TransactionType.EXPENSE,
            activeColor = appColors.expense,
            onClick = {
                haptics.lightTick()
                onSelectTypeFilter(TransactionType.EXPENSE)
            },
            modifier = Modifier.weight(1f)
        )

        CategoryFilterSegment(
            title = if (isBangla) "আয় ($incomeCategoriesCount)" else "Income ($incomeCategoriesCount)",
            isSelected = selectedTypeFilter == TransactionType.INCOME,
            activeColor = appColors.income,
            onClick = {
                haptics.lightTick()
                onSelectTypeFilter(TransactionType.INCOME)
            },
            modifier = Modifier.weight(1f)
        )
    }
}
