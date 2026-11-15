package com.example.expensetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.LocalAppColors

/**
 * Legacy wrapper around [PulseSegmented] for backwards compatibility with un-redesigned screens.
 * To be removed in a later phase once all callers are migrated.
 */
@Deprecated(
    message = "Use PulseSegmented directly for Pulse design language.",
    replaceWith = ReplaceWith("PulseSegmented(itemCount = items.size, selectedIndex = selectedIndex, onIndexSelected = onItemSelected, label = { items[it] }, modifier = modifier, scrollable = true)")
)
@Composable
fun ScrollableSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = LocalAppColors.current.accent,
    selectedContainerColor: Color = LocalAppColors.current.accentContainer
) {
    PulseSegmented(
        itemCount = items.size,
        selectedIndex = selectedIndex,
        onIndexSelected = onItemSelected,
        label = { items[it] },
        modifier = modifier,
        scrollable = true
    )
}
