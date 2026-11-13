package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

/**
 * AppAddFab:
 * Living Ledger 2.0 Extended Floating Action Button:
 * - Height: 56dp, Min width: 56dp
 * - Shape: Pill (28dp radius)
 * - Accent background with crisp white content
 * - Elevation: 6dp with soft ambient emerald glow in Dark/AMOLED
 * - Context-aware label that expands when idle/scrolling up, collapses to icon when scrolling down
 * - Primary entry for creation actions without hidden gestures
 */
@Composable
fun AppAddFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    expanded: Boolean = true,
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = label ?: "Add",
    containerColor: Color = LocalAppColors.current.accent,
    contentColor: Color = Color.White
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "fabScale"
    )

    val shadowAmbient = if (colors.isDark) containerColor.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.15f)
    val shadowSpot = if (colors.isDark) containerColor.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
            .scale(scaleAnim)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = shadowAmbient,
                spotColor = shadowSpot
            )
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.mediumClick()
                    onClick()
                }
            )
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = if (expanded && !label.isNullOrBlank()) 18.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            AnimatedVisibility(
                visible = expanded && !label.isNullOrBlank(),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                if (!label.isNullOrBlank()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor,
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "AppAddFab - Dark Expanded En")
@Composable
private fun AppAddFabDarkExpandedPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.background, modifier = Modifier.padding(16.dp)) {
            AppAddFab(
                onClick = {},
                label = "＋ Add",
                expanded = true
            )
        }
    }
}

@Preview(name = "AppAddFab - Light Expanded Bn")
@Composable
private fun AppAddFabLightExpandedPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.background, modifier = Modifier.padding(16.dp)) {
            AppAddFab(
                onClick = {},
                label = "＋ যোগ করুন",
                expanded = true
            )
        }
    }
}

@Preview(name = "AppAddFab - AMOLED Collapsed")
@Composable
private fun AppAddFabAmoledCollapsedPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.background, modifier = Modifier.padding(16.dp)) {
            AppAddFab(
                onClick = {},
                label = "＋ Add",
                expanded = false
            )
        }
    }
}
