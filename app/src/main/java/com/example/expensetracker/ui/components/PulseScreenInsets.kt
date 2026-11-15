package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Centrally applies safe drawing insets (status bar, display cutouts, and horizontal system bars)
 * to screens and content hosted by the shell.
 *
 * Rules:
 * 1. Every screen gets insets from the shell via [pulseScreenInsets].
 * 2. Individual screens must NOT add redundant status bar padding or hacks.
 * 3. Bottom docks and cart bars handle the bottom/navigation bar insets separately.
 * 4. Sheets and dialogs attach to separate windows and must not double-pad.
 */
@Composable
fun Modifier.pulseScreenInsets(): Modifier = this
    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
    .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
