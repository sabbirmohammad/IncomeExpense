package com.example.expensetracker.ui.dashboard

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

@Preview(name = "MarketItemRow - Dark En", showBackground = true)
@Composable
fun PreviewMarketItemRowDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketItemRow(
                    item = MarketItem(
                        id = 1L,
                        listId = 1L,
                        name = "Potato",
                        quantity = 2.0,
                        unit = "kg",
                        isPurchased = false
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    onUnitClick = {},
                    onTickClick = {},
                    lastRateText = "Last: ৳30/kg"
                )
            }
        }
    }
}

@Preview(name = "MarketItemRow - Light Bn (Purchased)", showBackground = true)
@Composable
fun PreviewMarketItemRowLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketItemRow(
                    item = MarketItem(
                        id = 2L,
                        listId = 1L,
                        name = "দেশি পেঁয়াজ",
                        quantity = 1.0,
                        unit = "কেজি",
                        isPurchased = true,
                        amount = 110.0
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onUnitClick = {},
                    onTickClick = {},
                    lastRateText = "গতবার: ৳১১০/কেজি"
                )
            }
        }
    }
}

@Preview(name = "MarketItemRow - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketItemRowAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketItemRow(
                    item = MarketItem(
                        id = 3L,
                        listId = 1L,
                        name = "কাঁচা মরিচ",
                        quantity = 250.0,
                        unit = "গ্রাম",
                        isPurchased = false
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onUnitClick = {},
                    onTickClick = {},
                    lastRateText = "গতবার: ৳৫০/২৫০ গ্রাম"
                )
            }
        }
    }
}
