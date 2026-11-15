package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

@Preview(name = "AppNumericKeypad - Dark En", showBackground = true)
@Composable
private fun AppNumericKeypadDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "1200+350",
                onExpressionChange = {},
                isBangla = false
            )
        }
    }
}

@Preview(name = "AppNumericKeypad - Light Bn", showBackground = true)
@Composable
private fun AppNumericKeypadLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "5000",
                onExpressionChange = {},
                isBangla = true
            )
        }
    }
}

@Preview(name = "AppNumericKeypad - AMOLED Bn", showBackground = true)
@Composable
private fun AppNumericKeypadAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "750",
                onExpressionChange = {},
                isBangla = true
            )
        }
    }
}

@Preview(name = "AppNumericKeypad - System En", showBackground = true)
@Composable
private fun AppNumericKeypadSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "950÷2",
                onExpressionChange = {},
                isBangla = false
            )
        }
    }
}
