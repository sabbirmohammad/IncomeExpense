package com.example.expensetracker.ui.market

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings

private val previewMockCommodities = listOf(
    MarketCommodityUiModel(
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
        lastPurchasedTimestamp = System.currentTimeMillis()
    ),
    MarketCommodityUiModel(
        name = "কাঁচামরিচ",
        category = "কাঁচাবাজার",
        defaultUnit = "কেজি",
        currentPrice = 195.0,
        previousPrice = 190.0,
        benchmarkPrice = 192.0,
        priceDiff = 5.0,
        percentDiff = 2.6,
        priceTrend = PriceTrend.UP,
        purchaseCount = 2,
        lastPurchasedTimestamp = System.currentTimeMillis() - 86400000L
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "DuplicateMerge - Dark English", showBackground = true)
@Composable
private fun PreviewDuplicateMergeDarkEn() {
    ExpenseTrackerTheme(darkTheme = true) {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketDuplicateMergeSheet(
                    commodities = previewMockCommodities,
                    isBangla = false,
                    onMergeItems = { _, _ -> },
                    onDismiss = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "DuplicateMerge - Light Bengali", showBackground = true)
@Composable
private fun PreviewDuplicateMergeLightBn() {
    ExpenseTrackerTheme(darkTheme = false) {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketDuplicateMergeSheet(
                    commodities = previewMockCommodities,
                    isBangla = true,
                    onMergeItems = { _, _ -> },
                    onDismiss = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "DuplicateMerge - AMOLED Bengali", showBackground = true)
@Composable
private fun PreviewDuplicateMergeAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketDuplicateMergeSheet(
                    commodities = previewMockCommodities,
                    isBangla = true,
                    onMergeItems = { _, _ -> },
                    onDismiss = {}
                )
            }
        }
    }
}
