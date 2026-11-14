package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

@Preview(name = "CategoryPicker - Light En", showBackground = true)
@Composable
private fun CategoryPickerLightPreview() {
    val sampleCategories = listOf(
        Category(1, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#10B981"),
        Category(2, "Dining", TransactionType.EXPENSE, "Restaurant", "#F97316"),
        Category(3, "Transport", TransactionType.EXPENSE, "DirectionsCar", "#0EA5E9"),
        Category(4, "Rent", TransactionType.EXPENSE, "Home", "#8B5CF6"),
        Category(5, "Bills", TransactionType.EXPENSE, "Receipt", "#F59E0B"),
        Category(6, "Health", TransactionType.EXPENSE, "LocalHospital", "#EC4899"),
        Category(7, "Shopping", TransactionType.EXPENSE, "ShoppingBag", "#14B8A6"),
        Category(8, "Other", TransactionType.EXPENSE, "MoreHoriz", "#64748B")
    )
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            CategoryPicker(
                categories = sampleCategories,
                selectedCategory = sampleCategories.first(),
                onCategorySelected = {},
                isBangla = false
            )
        }
    }
}
