package com.example.expensetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.theme.ExpenseTrackerTheme

@Preview(name = "AppTopBar - Dark with Back and Actions", showBackground = true)
@Composable
private fun AppTopBarDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        AppTopBar(
            title = "Ledger",
            subtitle = "All accounts · November",
            onBackClick = {},
            action1Icon = Icons.Default.Search,
            action1ContentDescription = "Search",
            onAction1Click = {},
            action2Icon = Icons.Default.MoreVert,
            action2ContentDescription = "More",
            onAction2Click = {},
            showHairline = true
        )
    }
}

@Preview(name = "AppTopBar - Light Bn Simple", showBackground = true)
@Composable
private fun AppTopBarLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        AppTopBar(
            title = "খাতা ও হিসাব",
            action1Icon = Icons.Default.Search,
            onAction1Click = {}
        )
    }
}

@Preview(name = "AppTopBar - AMOLED Bn with Back", showBackground = true)
@Composable
private fun AppTopBarAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        AppTopBar(
            title = "সেটিংস",
            onBackClick = {},
            backContentDescription = "ফিরে যান"
        )
    }
}

@Preview(name = "AppTopBar - System En Scrolled", showBackground = true)
@Composable
private fun AppTopBarSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        AppTopBar(
            title = "Market Intelligence",
            showHairline = true
        )
    }
}
