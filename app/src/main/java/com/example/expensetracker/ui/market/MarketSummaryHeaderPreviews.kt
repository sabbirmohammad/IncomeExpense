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

@Preview(name = "MarketSummaryHeader - Dark En", showBackground = true)
@Composable
fun PreviewMarketSummaryHeaderDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSummaryHeader(
                    listTitle = "Weekly Grocery",
                    totalCount = 8,
                    purchasedCount = 3,
                    totalSpent = 350.0,
                    estimatedBudget = 850.0,
                    currencySymbol = "৳",
                    onStartShopping = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "MarketSummaryHeader - Light Bn", showBackground = true)
@Composable
fun PreviewMarketSummaryHeaderLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSummaryHeader(
                    listTitle = "সকালের কাঁচাবাজার",
                    totalCount = 6,
                    purchasedCount = 4,
                    totalSpent = 420.0,
                    estimatedBudget = 500.0,
                    currencySymbol = "৳",
                    onStartShopping = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "MarketSummaryHeader - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketSummaryHeaderAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSummaryHeader(
                    listTitle = "মাছ-মাংসের বাজার",
                    totalCount = 4,
                    purchasedCount = 0,
                    totalSpent = 0.0,
                    estimatedBudget = 1200.0,
                    currencySymbol = "৳",
                    onStartShopping = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
