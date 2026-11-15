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

@Preview(name = "QuickAddBar - Dark En", showBackground = true)
@Composable
fun PreviewQuickAddBarDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                QuickAddBar(
                    onAddItem = {},
                    onIncrementExistingItem = { _, _ -> },
                    existingItems = emptyList(),
                    productPriceInsights = emptyMap(),
                    onOpenPasteSheet = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "QuickAddBar - Light Bn", showBackground = true)
@Composable
fun PreviewQuickAddBarLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                QuickAddBar(
                    onAddItem = {},
                    onIncrementExistingItem = { _, _ -> },
                    existingItems = emptyList(),
                    productPriceInsights = emptyMap(),
                    onOpenPasteSheet = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "QuickAddBar - AMOLED Bn", showBackground = true)
@Composable
fun PreviewQuickAddBarAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                QuickAddBar(
                    onAddItem = {},
                    onIncrementExistingItem = { _, _ -> },
                    existingItems = emptyList(),
                    productPriceInsights = emptyMap(),
                    onOpenPasteSheet = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
