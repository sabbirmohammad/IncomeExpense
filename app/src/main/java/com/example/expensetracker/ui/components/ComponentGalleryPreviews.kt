package com.example.expensetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.theme.ExpenseTrackerTheme

@Preview(name = "Dark - English", showBackground = true)
@Composable
fun PreviewComponentGalleryDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        ComponentGalleryScreen(isBengali = false)
    }
}

@Preview(name = "Light - English", showBackground = true)
@Composable
fun PreviewComponentGalleryLightEn() {
    ExpenseTrackerTheme(themeMode = "light") {
        ComponentGalleryScreen(isBengali = false)
    }
}

@Preview(name = "AMOLED - English", showBackground = true)
@Composable
fun PreviewComponentGalleryAmoledEn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        ComponentGalleryScreen(isBengali = false)
    }
}

@Preview(name = "Dark - Bengali", showBackground = true)
@Composable
fun PreviewComponentGalleryDarkBn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        ComponentGalleryScreen(isBengali = true)
    }
}

@Preview(name = "Light - Bengali", showBackground = true)
@Composable
fun PreviewComponentGalleryLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        ComponentGalleryScreen(isBengali = true)
    }
}
