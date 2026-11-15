package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

@Preview(name = "StarterEmptyState - Dark En Ledger", showBackground = true)
@Composable
private fun StarterEmptyStateDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "No transactions yet",
                description = "Start your ledger by adding your first daily expense",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = listOf(
                    StarterSuggestion("Bazaar"),
                    StarterSuggestion("Transport / Rickshaw"),
                    StarterSuggestion("Mobile recharge"),
                    StarterSuggestion("Tea & snacks")
                ),
                onSuggestionClick = {}
            )
        }
    }
}

@Preview(name = "StarterEmptyState - Light Bn Bills", showBackground = true)
@Composable
private fun StarterEmptyStateLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "কোনো বিল তালিকাভুক্ত নেই",
                description = "প্রতিমাসের নিয়মিত ইউটিলিটি বিল যোগ করে রিমাইন্ডার পান",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = listOf(
                    StarterSuggestion("বিদ্যুৎ বিল"),
                    StarterSuggestion("গ্যাস বিল"),
                    StarterSuggestion("ইন্টারনেট"),
                    StarterSuggestion("বাসা ভাড়া")
                ),
                onSuggestionClick = {}
            )
        }
    }
}

@Preview(name = "StarterEmptyState - AMOLED Bn Goals", showBackground = true)
@Composable
private fun StarterEmptyStateAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "কোনো সঞ্চয় লক্ষ্য তৈরি করা হয়নি",
                description = "ভবিষ্যতের লক্ষ্য নির্ধারণ করে নিয়মিত সঞ্চয় করুন",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = listOf(
                    StarterSuggestion("জরুরি তহবিল"),
                    StarterSuggestion("ঈদ কেনাকাটা"),
                    StarterSuggestion("হজ্জ / ওমরাহ")
                ),
                onSuggestionClick = {}
            )
        }
    }
}

@Preview(name = "StarterEmptyState - System En Action", showBackground = true)
@Composable
private fun StarterEmptyStateSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "No budget limits set",
                description = "Set category caps to track monthly expenditures calmly",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = emptyList(),
                onSuggestionClick = {},
                actionButtonText = "+ Create First Budget",
                onActionClick = {}
            )
        }
    }
}
