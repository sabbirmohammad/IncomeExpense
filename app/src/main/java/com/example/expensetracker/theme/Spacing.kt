package com.example.expensetracker.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =========================================================================
// 4-PT SPACING GRID ("Refined Ledger v7")
// =========================================================================

val Space2  = 2.dp
val Space4  = 4.dp
val Space8  = 8.dp
val Space12 = 12.dp
val Space16 = 16.dp
val Space20 = 20.dp
val Space24 = 24.dp
val Space32 = 32.dp
val Space48 = 48.dp

// Semantic Spacing
val SpacingCardPadding        = 16.dp
val SpacingCardGap            = 12.dp
val SpacingSectionGap         = 24.dp
val SpacingMinTouchTarget     = 48.dp
val SpacingListRowOneLineMin  = 56.dp
val SpacingListRowTwoLineMin  = 64.dp

data class AppSpacing(
    val space2: Dp = Space2,
    val space4: Dp = Space4,
    val space8: Dp = Space8,
    val space12: Dp = Space12,
    val space16: Dp = Space16,
    val space20: Dp = Space20,
    val space24: Dp = Space24,
    val space32: Dp = Space32,
    val space48: Dp = Space48,
    val screenHorizontal: Dp = Space20,
    val cardPadding: Dp = SpacingCardPadding,
    val cardGap: Dp = SpacingCardGap,
    val sectionGap: Dp = SpacingSectionGap,
    val minTouchTarget: Dp = SpacingMinTouchTarget
)

val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }

/**
 * Returns dynamic screen horizontal padding (20dp on phones, 24dp on tablets >= 600dp).
 */
@Composable
fun rememberResponsiveHorizontalPadding(): Dp {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp >= 600) 24.dp else 20.dp
}
