package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

// =========================================================================
// PREVIEWS IN ALL 4 THEMES (EN + BN)
// =========================================================================

@Preview(name = "StatTile - Dark En", showBackground = true)
@Composable
private fun StatTileDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "Monthly Expense",
                value = "৳ 24,500",
                valueColor = LocalAppColors.current.expense,
                delta = "-8%",
                isPositiveDelta = false
            )
        }
    }
}

@Preview(name = "StatTile - Light Bn", showBackground = true)
@Composable
private fun StatTileLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "মোট আয়",
                value = "৳ ৫০,০০০",
                valueColor = LocalAppColors.current.income,
                delta = "+১২%",
                isPositiveDelta = true
            )
        }
    }
}

@Preview(name = "StatTile - AMOLED Bn", showBackground = true)
@Composable
private fun StatTileAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "দৈনিক গড় খরচ",
                value = "৳ ১,১৫০"
            )
        }
    }
}

@Preview(name = "StatTile - System En Clickable", showBackground = true)
@Composable
private fun StatTileSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "Savings Rate",
                value = "34.5%",
                valueColor = LocalAppColors.current.accent,
                onClick = {}
            )
        }
    }
}
