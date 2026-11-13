package com.example.expensetracker.ui.market

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings

@Preview(name = "MarketPriceEntrySheet - Dark En", showBackground = true)
@Composable
fun PreviewMarketPriceEntrySheetDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketPriceEntrySheet(
                    item = MarketItem(
                        id = 1L,
                        listId = 1L,
                        name = "Potatoes",
                        quantity = 2.0,
                        unit = "kg",
                        amount = null
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    hasNextPendingItem = true,
                    onDismiss = {},
                    onSaveItem = { _, _, _, _ -> },
                    lastRate = Pair(30.0, "kg")
                )
            }
        }
    }
}

@Preview(name = "MarketPriceEntrySheet - Light Bn", showBackground = true)
@Composable
fun PreviewMarketPriceEntrySheetLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketPriceEntrySheet(
                    item = MarketItem(
                        id = 2L,
                        listId = 1L,
                        name = "দেশি পেঁয়াজ",
                        quantity = 1.0,
                        unit = "কেজি",
                        amount = 120.0
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    hasNextPendingItem = true,
                    onDismiss = {},
                    onSaveItem = { _, _, _, _ -> },
                    lastRate = Pair(110.0, "কেজি")
                )
            }
        }
    }
}

@Preview(name = "MarketPriceEntrySheet - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketPriceEntrySheetAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketPriceEntrySheet(
                    item = MarketItem(
                        id = 3L,
                        listId = 1L,
                        name = "রুই মাছ",
                        quantity = 1.5,
                        unit = "কেজি",
                        amount = null
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    hasNextPendingItem = false,
                    onDismiss = {},
                    onSaveItem = { _, _, _, _ -> },
                    lastRate = Pair(350.0, "কেজি")
                )
            }
        }
    }
}
