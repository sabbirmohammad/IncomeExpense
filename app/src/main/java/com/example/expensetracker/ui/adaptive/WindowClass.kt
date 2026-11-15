package com.example.expensetracker.ui.adaptive

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window width size classes based on standard Material 3 breakpoints:
 * - Compact: width < 600dp (phones in portrait)
 * - Medium: 600dp <= width < 840dp (foldables, small tablets, phones in landscape)
 * - Expanded: width >= 840dp (tablets, desktop mode)
 */
enum class WindowWidthClass {
    COMPACT,
    MEDIUM,
    EXPANDED;

    val isCompact: Boolean get() = this == COMPACT
    val isMedium: Boolean get() = this == MEDIUM
    val isExpanded: Boolean get() = this == EXPANDED

    companion object {
        fun fromWidth(width: Dp): WindowWidthClass = when {
            width < 600.dp -> COMPACT
            width < 840.dp -> MEDIUM
            else -> EXPANDED
        }
    }
}

/**
 * Window height size classes:
 * - Compact: height < 480dp (landscape phones)
 * - Medium: 480dp <= height <= 900dp
 * - Expanded: height > 900dp
 */
enum class WindowHeightClass {
    COMPACT,
    MEDIUM,
    EXPANDED;

    val isCompact: Boolean get() = this == COMPACT
    val isMedium: Boolean get() = this == MEDIUM
    val isExpanded: Boolean get() = this == EXPANDED

    companion object {
        fun fromHeight(height: Dp): WindowHeightClass = when {
            height < 480.dp -> COMPACT
            height <= 900.dp -> MEDIUM
            else -> EXPANDED
        }
    }
}

/**
 * Responsive WindowClass descriptor for the current composition.
 */
@Immutable
data class WindowClass(
    val widthClass: WindowWidthClass = WindowWidthClass.COMPACT,
    val heightClass: WindowHeightClass = WindowHeightClass.MEDIUM,
    val widthDp: Dp = 360.dp,
    val heightDp: Dp = 640.dp,
    val isLandscape: Boolean = false
) {
    val isCompact: Boolean get() = widthClass.isCompact
    val isMedium: Boolean get() = widthClass.isMedium
    val isExpanded: Boolean get() = widthClass.isExpanded

    companion object {
        fun calculate(width: Dp, height: Dp, orientation: Int = Configuration.ORIENTATION_PORTRAIT): WindowClass {
            val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE || width > height
            return WindowClass(
                widthClass = WindowWidthClass.fromWidth(width),
                heightClass = WindowHeightClass.fromHeight(height),
                widthDp = width,
                heightDp = height,
                isLandscape = isLandscape
            )
        }
    }
}

/**
 * CompositionLocal providing the current window size class.
 */
val LocalWindowClass = staticCompositionLocalOf { WindowClass() }

/**
 * Calculates and remembers the [WindowClass] from the current [LocalConfiguration].
 */
@Composable
fun rememberWindowClass(): WindowClass {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp
    val height = configuration.screenHeightDp.dp
    return WindowClass.calculate(
        width = width,
        height = height,
        orientation = configuration.orientation
    )
}
