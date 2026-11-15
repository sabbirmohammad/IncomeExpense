package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

@Preview(name = "SegmentedControl - Dark 3 Segments", showBackground = true)
@Composable
private fun SegmentedControlDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            SegmentedControl(
                items = listOf("Transactions", "Insights", "Reports"),
                selectedIndex = 0,
                onItemSelected = {}
            )
        }
    }
}

@Preview(name = "SegmentedControl - Light Bn 4 Segments", showBackground = true)
@Composable
private fun SegmentedControlLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            SegmentedControl(
                items = listOf("বাজেট", "লক্ষ্য", "বিল", "ধার ও দেনা"),
                selectedIndex = 1,
                onItemSelected = {}
            )
        }
    }
}

@Preview(name = "ScrollableSegmentedControl - AMOLED Bn", showBackground = true)
@Composable
private fun ScrollableSegmentedControlAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            ScrollableSegmentedControl(
                items = listOf("বাজেট", "লক্ষ্য", "বিল", "ধার ও দেনা"),
                selectedIndex = 0,
                onItemSelected = {}
            )
        }
    }
}

@Preview(name = "ScrollableSegmentedControl - System En", showBackground = true)
@Composable
private fun ScrollableSegmentedControlSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            ScrollableSegmentedControl(
                items = listOf("Budgets", "Goals", "Bills", "Debts"),
                selectedIndex = 2,
                onItemSelected = {}
            )
        }
    }
}
