package com.example.expensetracker.ui.market

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings

// =========================================================================
// PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "MarketTripFinishSummarySheet - Dark En", showBackground = true)
@Composable
fun PreviewMarketTripFinishSummaryDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketTripFinishSummarySheet(
                    trip = MarketListWithItems(
                        list = MarketList(
                            id = 1L,
                            title = "Weekly Bazar",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "Potatoes", quantity = 2.0, unit = "kg", isPurchased = true, amount = 80.0),
                            MarketItem(id = 2L, listId = 1L, name = "Onions", quantity = 1.0, unit = "kg", isPurchased = true, amount = 120.0),
                            MarketItem(id = 3L, listId = 1L, name = "Ginger", quantity = 250.0, unit = "g", isPurchased = false)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "Cash", type = AccountType.CASH, isDefault = true),
                        Account(id = 2L, name = "bKash", type = AccountType.MFS)
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    onDismiss = {},
                    onConfirmComplete = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketTripFinishSummarySheet - Light Bn", showBackground = true)
@Composable
fun PreviewMarketTripFinishSummaryLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketTripFinishSummarySheet(
                    trip = MarketListWithItems(
                        list = MarketList(
                            id = 1L,
                            title = "সকালের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 70.0),
                            MarketItem(id = 2L, listId = 1L, name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = true, amount = 90.0)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ ক্যাশ", type = AccountType.CASH, isDefault = true),
                        Account(id = 2L, name = "বিকাশ", type = AccountType.MFS)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onDismiss = {},
                    onConfirmComplete = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketTripFinishSummarySheet - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketTripFinishSummaryAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketTripFinishSummarySheet(
                    trip = MarketListWithItems(
                        list = MarketList(
                            id = 1L,
                            title = "মাছের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "রুই মাছ", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 700.0)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onDismiss = {},
                    onConfirmComplete = { _, _ -> }
                )
            }
        }
    }
}
