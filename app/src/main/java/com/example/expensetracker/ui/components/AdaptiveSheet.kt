package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapeCard
import com.example.expensetracker.theme.ShapeSheet
import com.example.expensetracker.ui.adaptive.LocalWindowClass

/**
 * AdaptiveSheet:
 * Responsive form/sheet container for Pulse Design System:
 * - Compact (< 600dp): ModalBottomSheet with 28dp top radius & drag handle (max height 92%)
 * - Medium/Expanded (>= 600dp): Centered Dialog capped at 560dp with 24dp radius
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit
) {
    val windowClass = LocalWindowClass.current
    val appColors = LocalAppColors.current

    if (windowClass.isCompact) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            shape = ShapeSheet,
            containerColor = appColors.surface,
            contentColor = appColors.textPrimary,
            scrimColor = appColors.scrim,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                content = content
            )
        }
    } else {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth(0.9f)
                        .clip(ShapeCard),
                    shape = ShapeCard,
                    color = appColors.surface,
                    contentColor = appColors.textPrimary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        content = content
                    )
                }
            }
        }
    }
}
