package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapeIconTile

/**
 * IconTile:
 * Pulse Design System leading icon / category tile:
 * - 40-44dp rounded-square container (RadiusIconTile = 14dp)
 * - Semantic container tint background
 * - Supports Material vector icons and Bazaar emoji
 */
@Composable
fun IconTile(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    emoji: String? = null,
    tint: Color = LocalAppColors.current.accent,
    containerColor: Color? = null,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    contentDescription: String? = null
) {
    val appColors = LocalAppColors.current
    val background = containerColor ?: tint.copy(alpha = if (appColors.isDark) 0.18f else 0.12f)

    Box(
        modifier = modifier
            .size(size)
            .clip(ShapeIconTile)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        if (emoji != null) {
            Text(
                text = emoji,
                fontSize = (size.value * 0.48f).sp
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Preview(name = "IconTile - Dark")
@Composable
private fun IconTileDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        IconTile(emoji = "🥬", tint = LocalAppColors.current.income)
    }
}

@Preview(name = "IconTile - Light")
@Composable
private fun IconTileLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        IconTile(emoji = "🛒", tint = LocalAppColors.current.accent)
    }
}
