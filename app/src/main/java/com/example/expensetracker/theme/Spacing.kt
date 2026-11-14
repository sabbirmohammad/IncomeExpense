package com.example.expensetracker.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.adaptive.LocalWindowClass

// =========================================================================
// 4-DP SPACING GRID (Pulse Design System)
// =========================================================================

val Space2  = 2.dp
val Space4  = 4.dp
val Space8  = 8.dp
val Space12 = 12.dp
val Space16 = 16.dp
val Space20 = 20.dp
val Space24 = 24.dp
val Space28 = 28.dp
val Space32 = 32.dp
val Space48 = 48.dp
val Space56 = 56.dp
val Space64 = 64.dp

// Semantic Spacing
val SpacingCardPadding        = 16.dp
val SpacingCardGapCompact     = 12.dp
val SpacingCardGapMedium      = 16.dp
val SpacingCardGap            = SpacingCardGapCompact
val SpacingSectionGap         = 24.dp
val SpacingMinTouchTarget     = 48.dp
val SpacingListRowOneLineMin  = 56.dp
val SpacingListRowTwoLineMin  = 64.dp

@Immutable
data class AppSpacing(
    val space2: Dp = Space2,
    val space4: Dp = Space4,
    val space8: Dp = Space8,
    val space12: Dp = Space12,
    val space16: Dp = Space16,
    val space20: Dp = Space20,
    val space24: Dp = Space24,
    val space28: Dp = Space28,
    val space32: Dp = Space32,
    val space48: Dp = Space48,
    val space56: Dp = Space56,
    val screenHorizontalCompact: Dp = 16.dp,
    val screenHorizontalMedium: Dp = 24.dp,
    val screenHorizontalExpanded: Dp = 32.dp,
    val cardPadding: Dp = SpacingCardPadding,
    val cardGap: Dp = SpacingCardGap,
    val sectionGap: Dp = SpacingSectionGap,
    val minTouchTarget: Dp = SpacingMinTouchTarget
) {
    val screenHorizontal: Dp get() = screenHorizontalCompact
}

val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }

/**
 * Returns responsive screen horizontal padding according to LocalWindowClass:
 * - Compact: 16dp
 * - Medium: 24dp
 * - Expanded: 32dp
 */
@Composable
fun rememberResponsiveHorizontalPadding(): Dp {
    val windowClass = LocalWindowClass.current
    return when {
        windowClass.isExpanded -> 32.dp
        windowClass.isMedium -> 24.dp
        else -> 16.dp
    }
}

/**
 * Returns responsive gap between cards:
 * - Compact: 12dp
 * - Medium/Expanded: 16dp
 */
@Composable
fun rememberResponsiveCardGap(): Dp {
    val windowClass = LocalWindowClass.current
    return if (windowClass.isCompact) SpacingCardGapCompact else SpacingCardGapMedium
}
