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
 * Supported screen form factors for inset resolution.
 */
enum class PulseWindowFormat {
    COMPACT,            // Phone portrait
    COMPACT_LANDSCAPE,  // Phone landscape
    MEDIUM,             // Foldable / tablet portrait
    EXPANDED            // Tablet / desktop landscape
}

/**
 * Resolved inset configuration for a screen container.
 */
data class PulseScreenInsetsConfig(
    val applyTop: Boolean,
    val applyBottom: Boolean,
    val applyStart: Boolean,
    val applyEnd: Boolean,
    val isBottomHandledByDock: Boolean
)

/**
 * Pure inset policy defining which window inset sides are applied across form factors.
 */
object PulseInsetPolicy {
    fun resolveContentInsets(
        format: PulseWindowFormat,
        hasBottomDock: Boolean = true
    ): PulseScreenInsetsConfig {
        return when (format) {
            PulseWindowFormat.COMPACT -> PulseScreenInsetsConfig(
                applyTop = true,
                applyBottom = !hasBottomDock,
                applyStart = false,
                applyEnd = false,
                isBottomHandledByDock = hasBottomDock
            )
            PulseWindowFormat.COMPACT_LANDSCAPE -> PulseScreenInsetsConfig(
                applyTop = true,
                applyBottom = false,
                applyStart = true, // Left cutout & rail
                applyEnd = true,   // Right cutout & system gesture
                isBottomHandledByDock = hasBottomDock
            )
            PulseWindowFormat.MEDIUM -> PulseScreenInsetsConfig(
                applyTop = true,
                applyBottom = !hasBottomDock,
                applyStart = false, // Rail handles start inset
                applyEnd = true,
                isBottomHandledByDock = hasBottomDock
            )
            PulseWindowFormat.EXPANDED -> PulseScreenInsetsConfig(
                applyTop = true,
                applyBottom = false,
                applyStart = false, // Side panel handles start inset
                applyEnd = true,
                isBottomHandledByDock = false
            )
        }
    }
}

/**
 * Centrally applies safe drawing insets (status bar, display cutouts, and horizontal system bars)
 * to screens and content hosted by the shell.
 */
@Composable
fun Modifier.pulseScreenInsets(): Modifier = this
    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
    .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
