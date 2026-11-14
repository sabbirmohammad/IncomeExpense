package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

@Preview(name = "AmountKeypad - Light En", showBackground = true)
@Composable
private fun AmountKeypadLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountKeypad(
                expression = "120+85",
                onExpressionChange = {},
                isBangla = false
            )
        }
    }
}

@Preview(name = "AmountKeypad - Dark Bn", showBackground = true)
@Composable
private fun AmountKeypadDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountKeypad(
                expression = "450",
                onExpressionChange = {},
                isBangla = true
            )
        }
    }
}
