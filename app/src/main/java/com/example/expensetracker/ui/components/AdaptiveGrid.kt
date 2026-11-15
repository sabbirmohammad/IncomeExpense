package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.SpacingCardGapCompact
import com.example.expensetracker.theme.SpacingCardGapMedium
import com.example.expensetracker.ui.adaptive.LocalWindowClass

/**
 * AdaptiveGrid:
 * Pulse Design System responsive grid helper:
 * - Automatically derives optimal column counts from LocalWindowClass
 * - Compact: compactColumns (1-2)
 * - Medium: mediumColumns (2-3)
 * - Expanded: expandedColumns (3-4)
 */
@Composable
fun AdaptiveGrid(
    modifier: Modifier = Modifier,
    compactColumns: Int = 1,
    mediumColumns: Int = 2,
    expandedColumns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical? = null,
    horizontalArrangement: Arrangement.Horizontal? = null,
    content: LazyGridScope.() -> Unit
) {
    val windowClass = LocalWindowClass.current
    val columns = when {
        windowClass.isExpanded -> expandedColumns
        windowClass.isMedium -> mediumColumns
        else -> compactColumns
    }
    val gap: Dp = if (windowClass.isCompact) SpacingCardGapCompact else SpacingCardGapMedium

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement ?: Arrangement.spacedBy(gap),
        horizontalArrangement = horizontalArrangement ?: Arrangement.spacedBy(gap),
        content = content
    )
}
