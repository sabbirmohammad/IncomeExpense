package com.example.expensetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppChipVariant {
    Filter,
    Choice,
    Input
}

/**
 * Legacy wrapper around [PulseChip] for backwards compatibility with un-redesigned screens.
 * To be removed in a later phase once all callers are migrated.
 */
@Deprecated(
    message = "Use PulseChip directly for Pulse design language.",
    replaceWith = ReplaceWith("PulseChip(text = label, selected = selected, onClick = onClick, modifier = modifier, leadingIcon = leadingIcon, onRemoveClick = onRemoveClick)")
)
@Composable
fun AppChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppChipVariant = AppChipVariant.Filter,
    leadingIcon: ImageVector? = null,
    onRemoveClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    PulseChip(
        text = label,
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        tone = PulseChipTone.ACCENT,
        leadingIcon = leadingIcon,
        onRemoveClick = onRemoveClick
    )
}
