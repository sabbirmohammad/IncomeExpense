package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

/**
 * AppScaffold:
 * Clean, lightweight Scaffold host replacing legacy Navigation3DScaffold:
 * - Direct composition of bottomBar, fab, snackbarHost, and content
 * - Consistently respects safe system insets without heavy 3D transforms or GPU overhead
 */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        Scaffold(
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            containerColor = appColors.background,
            contentColor = appColors.textPrimary,
            content = content
        )

        // FAB positioned at bottom-end, 16dp from end, 16dp above bottom bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = 16.dp + 64.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
        ) {
            fab()
        }
    }
}
