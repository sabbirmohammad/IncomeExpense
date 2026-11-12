package com.example.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion

/**
 * V7 Section Header:
 * - Clear title
 * - Optional primary action (e.g., "See all") with strict 48dp minimum touch target
 * - Optional collapsible mode with animated chevron
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    isCollapsible: Boolean = false,
    isExpanded: Boolean = true,
    onToggleExpand: (() -> Unit)? = null
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val motion = LocalMotion.current

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = motion.standardTween(),
        label = "chevronRotation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isCollapsible && onToggleExpand != null) {
                    Modifier.clickable {
                        haptics.lightTick()
                        onToggleExpand()
                    }
                } else Modifier
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (isCollapsible) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse $title" else "Expand $title",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(chevronRotation),
                tint = colors.textSecondary
            )
        } else if (actionText != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    .semantics { role = Role.Button }
                    .clickable {
                        haptics.lightTick()
                        onActionClick()
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = colors.accent
                )
            }
        }
    }
}
