package com.example.expensetracker.ui.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings

private val previewMockDetailCommodity = MarketCommodityUiModel(
    name = "কাঁচা মরিচ",
    category = "কাঁচাবাজার",
    defaultUnit = "কেজি",
    currentPrice = 200.0,
    previousPrice = 180.0,
    benchmarkPrice = 190.0,
    priceDiff = 20.0,
    percentDiff = 11.1,
    priceTrend = PriceTrend.UP,
    purchaseCount = 5,
    lastPurchasedTimestamp = System.currentTimeMillis(),
    recentPricePoints = listOf(160.0, 175.0, 180.0, 190.0, 200.0)
)

private val previewMockRecords = listOf(
    ProductPriceRecord(id = 1, productName = "কাঁচা মরিচ", rawQuantity = 0.5, rawUnit = "কেজি", baseQuantity = 0.5, baseUnit = "কেজি", totalAmount = 100.0, unitPrice = 200.0, timestamp = System.currentTimeMillis()),
    ProductPriceRecord(id = 2, productName = "কাঁচা মরিচ", rawQuantity = 1.0, rawUnit = "কেজি", baseQuantity = 1.0, baseUnit = "কেজি", totalAmount = 180.0, unitPrice = 180.0, timestamp = System.currentTimeMillis() - 86400000L * 3),
    ProductPriceRecord(id = 3, productName = "কাঁচা মরিচ", rawQuantity = 0.25, rawUnit = "কেজি", baseQuantity = 0.25, baseUnit = "কেজি", totalAmount = 45.0, unitPrice = 180.0, timestamp = System.currentTimeMillis() - 86400000L * 8)
)

@Preview(name = "ItemDetail - Dark English", showBackground = true)
@Composable
private fun PreviewItemDetailDarkEn() {
    ExpenseTrackerTheme(darkTheme = true) {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            MarketItemDetailScreen(
                commodity = previewMockDetailCommodity,
                onFetchHistory = { previewMockRecords },
                isBangla = false,
                onAddToList = {},
                onClose = {}
            )
        }
    }
}

@Preview(name = "ItemDetail - Light Bengali", showBackground = true)
@Composable
private fun PreviewItemDetailLightBn() {
    ExpenseTrackerTheme(darkTheme = false) {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            MarketItemDetailScreen(
                commodity = previewMockDetailCommodity,
                onFetchHistory = { previewMockRecords },
                isBangla = true,
                onAddToList = {},
                onClose = {}
            )
        }
    }
}

@Preview(name = "ItemDetail - AMOLED Bengali", showBackground = true)
@Composable
private fun PreviewItemDetailAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            MarketItemDetailScreen(
                commodity = previewMockDetailCommodity,
                onFetchHistory = { previewMockRecords },
                isBangla = true,
                onAddToList = {},
                onClose = {}
            )
        }
    }
}
