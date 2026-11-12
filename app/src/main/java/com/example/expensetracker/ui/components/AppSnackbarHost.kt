package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapeChip

/**
 * V7 Styled Snackbar Host:
 * - High contrast surfaceRaised container
 * - Accent colored action button (e.g., Undo, Retry)
 * - 12dp radius (ShapeChip)
 * - Accessible LiveRegionMode.Polite announcement
 */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .padding(16.dp)
            .semantics {
                liveRegion = LiveRegionMode.Polite
            },
        snackbar = { data: SnackbarData ->
            val borderStroke = if (!colors.isDark) BorderStroke(HairlineInkStroke, colors.border) else null
            Snackbar(
                shape = ShapeChip,
                containerColor = colors.surfaceRaised,
                contentColor = colors.textPrimary,
                actionContentColor = colors.accent,
                dismissActionContentColor = colors.textSecondary,
                action = data.visuals.actionLabel?.let { actionLabel ->
                    {
                        TextButton(
                            onClick = { data.performAction() }
                        ) {
                            Text(
                                text = actionLabel,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = colors.accent
                            )
                        }
                    }
                }
            ) {
                Text(
                    text = data.visuals.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = colors.textPrimary
                )
            }
        }
    )
}
