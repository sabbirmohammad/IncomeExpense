package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.dashboard.DepartmentMeta

@Preview(name = "MarketSectionHeader - Dark En", showBackground = true)
@Composable
fun PreviewMarketSectionHeaderDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSectionHeader(
                    dept = DepartmentMeta("veggies", "কাঁচাবাজার", "Vegetables", "🥬"),
                    completedCount = 2,
                    totalCount = 3,
                    isExpanded = true,
                    onToggleExpand = {},
                    isBangla = false,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "MarketSectionHeader - Light Bn", showBackground = true)
@Composable
fun PreviewMarketSectionHeaderLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSectionHeader(
                    dept = DepartmentMeta("meat_fish", "মাছ ও মাংস", "Meat & Fish", "🐟"),
                    completedCount = 2,
                    totalCount = 2,
                    isExpanded = true,
                    onToggleExpand = {},
                    isBangla = true,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "MarketSectionHeader - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketSectionHeaderAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSectionHeader(
                    dept = DepartmentMeta("groceries", "মুদি সদাই", "Groceries", "🧂"),
                    completedCount = 0,
                    totalCount = 5,
                    isExpanded = false,
                    onToggleExpand = {},
                    isBangla = true,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
