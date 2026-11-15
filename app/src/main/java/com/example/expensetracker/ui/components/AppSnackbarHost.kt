package com.example.expensetracker.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Legacy wrapper around [PulseSnackbarHost] for backwards compatibility with un-redesigned screens.
 * To be removed in a later phase once all callers are migrated.
 */
@Deprecated(
    message = "Use PulseSnackbarHost directly for Pulse design language.",
    replaceWith = ReplaceWith("PulseSnackbarHost(hostState, modifier)")
)
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    PulseSnackbarHost(
        hostState = hostState,
        modifier = modifier
    )
}
