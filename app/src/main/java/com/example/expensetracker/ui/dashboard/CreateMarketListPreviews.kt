package com.example.expensetracker.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings

@Preview(name = "CreateMarketListSection - Dark En", showBackground = true)
@Composable
fun PreviewCreateMarketListDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            CreateMarketListSection(
                isBangla = false,
                onListCreated = { _, _ -> }
            )
        }
    }
}

@Preview(name = "CreateMarketListSection - Light Bn", showBackground = true)
@Composable
fun PreviewCreateMarketListLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            CreateMarketListSection(
                isBangla = true,
                onListCreated = { _, _ -> }
            )
        }
    }
}

@Preview(name = "CreateMarketListSection - AMOLED Bn", showBackground = true)
@Composable
fun PreviewCreateMarketListAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            CreateMarketListSection(
                isBangla = true,
                onListCreated = { _, _ -> }
            )
        }
    }
}
