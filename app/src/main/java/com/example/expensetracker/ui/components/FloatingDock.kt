package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

/**
 * FloatingDock:
 * Pulse Design System Floating Navigation Dock:
 * - Height: 68dp
 * - Shape: RadiusPill (CircleShape)
 * - Margins: 16dp horizontal, 12dp bottom + nav-bar inset
 * - Surface with subtle elevation shadow on light theme
 */
@Composable
fun FloatingDock(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .then(
                if (!appColors.isDark) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = Color.Black.copy(alpha = 0.06f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    )
                } else Modifier
            )
            .clip(CircleShape)
            .background(appColors.surface)
            .height(68.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Preview(name = "FloatingDock - Dark")
@Composable
private fun FloatingDockDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        FloatingDock {
            Text(text = "Dock Items", color = LocalAppColors.current.textPrimary)
        }
    }
}

@Preview(name = "FloatingDock - Light")
@Composable
private fun FloatingDockLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        FloatingDock {
            Text(text = "Dock Items", color = LocalAppColors.current.textPrimary)
        }
    }
}
