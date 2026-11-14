package com.example.expensetracker.ui.components

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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapeButton

/**
 * PulseSnackbarHost:
 * Pulse Design System Feedback Host:
 * - Rounded 16dp container (ShapeButton)
 * - Module/Brand accent CTA (Undo, Retry, Details)
 * - WCAG AA contrast against background
 * - Accessible LiveRegionMode.Polite announcement
 */
@Composable
fun PulseSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .padding(16.dp)
            .semantics {
                liveRegion = LiveRegionMode.Polite
            },
        snackbar = { data: SnackbarData ->
            Snackbar(
                shape = ShapeButton,
                containerColor = if (appColors.isDark) appColors.surfaceVariant else appColors.surface,
                contentColor = appColors.textPrimary,
                actionContentColor = appColors.accent,
                dismissActionContentColor = appColors.textSecondary,
                action = data.visuals.actionLabel?.let { actionLabel ->
                    {
                        TextButton(
                            onClick = { data.performAction() }
                        ) {
                            Text(
                                text = actionLabel,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = appColors.accent
                            )
                        }
                    }
                }
            ) {
                Text(
                    text = data.visuals.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = appColors.textPrimary
                )
            }
        }
    )
}

@Preview(name = "PulseSnackbarHost - Dark")
@Composable
private fun PulseSnackbarHostDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        PulseSnackbarHost(hostState = SnackbarHostState())
    }
}
