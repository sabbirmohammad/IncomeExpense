package com.example.expensetracker.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.adaptive.LocalWindowClass
import com.example.expensetracker.ui.adaptive.WindowHeightClass
import com.example.expensetracker.ui.adaptive.WindowWidthClass
import com.example.expensetracker.ui.components.pulseScreenInsets

/**
 * AdaptiveScaffold:
 * The responsive root shell of the Pulse Design Language (v7.0).
 *
 * Breakpoints (per user amendment 2):
 * - Rail: width in 600..839dp OR heightClass == Compact (landscape phone), regardless of width.
 * - Side Panel: width >= 840dp AND heightClass != Compact.
 * - Dock: width < 600dp AND heightClass != Compact.
 *
 * Visibility & Insets (per user amendment 5):
 * - Dock is hidden on pushed screens (state.pushedScreen != null) and when the IME is open.
 * - Content bottom padding on Compact: dock height (68dp) + 24dp = 92dp, centrally applied.
 * - Max content width: 720dp for Medium/Rail, 1200dp for Expanded/SidePanel.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdaptiveScaffold(
    selectedTab: PulseTab,
    onTabSelected: (PulseTab) -> Unit,
    onOpenAdd: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    pushedScreen: PushedScreen? = null,
    hasPlanAttention: Boolean = false,
    isBazaarTripActive: Boolean = false,
    userName: String = "",
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val windowClass = LocalWindowClass.current
    val appColors = LocalAppColors.current

    val isLandscapeCompact = windowClass.heightClass == WindowHeightClass.COMPACT
    val isSidePanel = windowClass.widthClass == WindowWidthClass.EXPANDED && !isLandscapeCompact
    val isRail = (windowClass.widthClass == WindowWidthClass.MEDIUM) || isLandscapeCompact
    val isDock = !isSidePanel && !isRail

    val isImeOpen = WindowInsets.isImeVisible
    val isPushed = pushedScreen != null
    val showDock = isDock && !isPushed && !isImeOpen

    val navBarsPadding = WindowInsets.navigationBars.asPaddingValues()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        when {
            isSidePanel -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .pulseScreenInsets()
                ) {
                    PulseSidePanel(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = onOpenAdd,
                        onProfileClick = onOpenSettings,
                        userName = userName,
                        hasPlanAttention = hasPlanAttention,
                        isBazaarTripActive = isBazaarTripActive
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 1200.dp)
                        ) {
                            content(PaddingValues(0.dp))
                        }
                    }
                }
            }

            isRail -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .pulseScreenInsets()
                ) {
                    PulseNavigationRail(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = onOpenAdd,
                        hasPlanAttention = hasPlanAttention,
                        isBazaarTripActive = isBazaarTripActive
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 720.dp)
                        ) {
                            content(PaddingValues(0.dp))
                        }
                    }
                }
            }

            else -> {
                // Compact: Bottom Dock
                val navBarBottom = navBarsPadding.calculateBottomPadding()
                // On 3-button nav (>32dp), keep clear of buttons. On gesture nav (~16-24dp), seat dock snug above gesture handle.
                val dockBottomPadding = if (navBarBottom > 32.dp) {
                    navBarBottom + 4.dp
                } else {
                    (navBarBottom - 6.dp).coerceAtLeast(12.dp)
                }
                val bottomPadding = if (showDock) (PulseDockHeight + dockBottomPadding + 10.dp) else 0.dp
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pulseScreenInsets()
                    ) {
                        content(PaddingValues(bottom = bottomPadding))
                    }

                    AnimatedVisibility(
                        visible = showDock,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = dockBottomPadding),
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        PulseDock(
                            selectedTab = selectedTab,
                            onTabSelected = onTabSelected,
                            onAddClick = onOpenAdd,
                            hasPlanAttention = hasPlanAttention,
                            isBazaarTripActive = isBazaarTripActive
                        )
                    }
                }
            }
        }

        // Snackbar host overlay
        val navBarBottom = navBarsPadding.calculateBottomPadding()
        val dockBottomPadding = if (navBarBottom > 32.dp) navBarBottom + 4.dp else (navBarBottom - 6.dp).coerceAtLeast(12.dp)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = if (showDock) (PulseDockHeight + dockBottomPadding + 16.dp) else 16.dp
                )
        ) {
            snackbarHost()
        }
    }
}

/**
 * AdaptiveScaffold overload taking [AdaptiveNavigationState].
 */
@Composable
fun AdaptiveScaffold(
    state: AdaptiveNavigationState,
    onOpenAdd: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    userName: String = "",
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    AdaptiveScaffold(
        selectedTab = state.selectedTab,
        onTabSelected = { state.selectTab(it) },
        onOpenAdd = onOpenAdd,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
        pushedScreen = state.pushedScreen,
        hasPlanAttention = state.hasPlanAttention,
        isBazaarTripActive = state.isBazaarTripActive,
        userName = userName,
        snackbarHost = snackbarHost,
        content = content
    )
}
