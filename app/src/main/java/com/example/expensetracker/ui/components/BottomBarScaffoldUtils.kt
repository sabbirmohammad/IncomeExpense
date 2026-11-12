package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors

// =========================================================================
// C2 BOTTOM BAR UTILITIES ("Refined Ledger v7" Spec Patch)
// =========================================================================

val V7BottomBarHeight: Dp = 68.dp
val V7BottomBarExtraSpacing: Dp = 16.dp

/**
 * Computes the required bottom content padding so that scrollable content
 * never clips or ghosts behind the bottom navigation bar.
 * Formula: barHeight + navigationBarInsets + 16dp
 */
@Composable
fun rememberBottomBarPadding(): Dp {
    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return V7BottomBarHeight + navBarsBottom + V7BottomBarExtraSpacing
}

/**
 * Standard content padding for scrollable lists inside top-level screens.
 */
@Composable
fun rememberScrollableContentPadding(
    horizontal: Dp = 20.dp,
    top: Dp = 16.dp
): PaddingValues {
    val bottom = rememberBottomBarPadding()
    return PaddingValues(
        start = horizontal,
        top = top,
        end = horizontal,
        bottom = bottom
    )
}

/**
 * Opaque bottom bar surface container:
 * - Opaque surface background (no blur/translucency ghosting)
 * - 0.5dp top hairline divider
 * - Safe navigation bar inset padding
 */
@Composable
fun OpaqueBottomBarContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalAppColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        // Top 0.5dp hairline border
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            thickness = HairlineInkStroke,
            color = colors.border
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            content()
        }
    }
}
