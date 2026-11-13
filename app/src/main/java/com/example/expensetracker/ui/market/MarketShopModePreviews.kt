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

@Preview(name = "MarketShopModeView - Dark En", showBackground = true)
@Composable
fun PreviewMarketShopModeDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketShopModeView(
                    activeList = MarketListWithItems(
                        list = MarketList(
                            id = 1L,
                            title = "Morning Market",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "Potatoes", quantity = 2.0, unit = "kg", isPurchased = true, amount = 80.0),
                            MarketItem(id = 2L, listId = 1L, name = "Onions", quantity = 1.0, unit = "kg", isPurchased = false),
                            MarketItem(id = 3L, listId = 1L, name = "Eggs", quantity = 1.0, unit = "hali", isPurchased = false)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "Cash", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    onExitShopMode = {},
                    onUpdateItemAmount = { _, _ -> },
                    onUpdateItemQuantityAndUnit = { _, _, _ -> },
                    onQuickMarkPurchased = {},
                    onUnmarkPurchased = {},
                    onDeleteItem = {},
                    onAddItem = {},
                    onCompleteTrip = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketShopModeView - Light Bn", showBackground = true)
@Composable
fun PreviewMarketShopModeLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketShopModeView(
                    activeList = MarketListWithItems(
                        list = MarketList(
                            id = 1L,
                            title = "সকালের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 70.0),
                            MarketItem(id = 2L, listId = 1L, name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = false)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onExitShopMode = {},
                    onUpdateItemAmount = { _, _ -> },
                    onUpdateItemQuantityAndUnit = { _, _, _ -> },
                    onQuickMarkPurchased = {},
                    onUnmarkPurchased = {},
                    onDeleteItem = {},
                    onAddItem = {},
                    onCompleteTrip = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketShopModeView - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketShopModeAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketShopModeView(
                    activeList = MarketListWithItems(
                        list = MarketList(
                            id = 1L,
                            title = "মাছ-মাংসের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "রুই মাছ", quantity = 1.5, unit = "কেজি", isPurchased = true, amount = 500.0)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onExitShopMode = {},
                    onUpdateItemAmount = { _, _ -> },
                    onUpdateItemQuantityAndUnit = { _, _, _ -> },
                    onQuickMarkPurchased = {},
                    onUnmarkPurchased = {},
                    onDeleteItem = {},
                    onAddItem = {},
                    onCompleteTrip = { _, _ -> }
                )
            }
        }
    }
}
