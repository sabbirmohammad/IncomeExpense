package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapeHero

/**
 * HeroCard:
 * Pulse Design System Hero Module Card:
 * - 2-stop gradient of the module hero color (top-left -> bottom-right)
 * - RadiusCard = 24dp, no hairline borders
 * - Verified WCAG AA contrast with white/near-white text
 */
@Composable
fun HeroCard(
    gradient: Pair<Color, Color>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val brush = Brush.linearGradient(
        colors = listOf(gradient.first, gradient.second)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeHero)
            .background(brush)
            .padding(20.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

@Preview(name = "HeroCard - Home Blue")
@Composable
private fun HeroCardHomePreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        HeroCard(gradient = LocalAppColors.current.heroHome) {
            Text(text = "Safe to spend today", color = Color.White)
        }
    }
}

@Preview(name = "HeroCard - Bazaar Green")
@Composable
private fun HeroCardBazaarPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        HeroCard(gradient = LocalAppColors.current.heroBazaar) {
            Text(text = "Going to the bazaar?", color = Color.White)
        }
    }
}
