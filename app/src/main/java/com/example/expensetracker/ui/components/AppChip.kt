package com.example.expensetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.FastEasing
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import com.example.expensetracker.theme.ShapeChip

enum class AppChipVariant {
    Filter,
    Choice,
    Input
}

/**
 * V7 Universal Chip:
 * Strict WCAG 2.2 touch target >= 48dp, 12dp radius, high contrast selected state, haptic feedback.
 */
@Composable
fun AppChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppChipVariant = AppChipVariant.Filter,
    leadingIcon: ImageVector? = null,
    onRemoveClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val motion = LocalMotion.current

    val animDuration = motion.durationFast

    val targetBg = when {
        selected -> colors.accentContainer
        else -> colors.surfaceVariant
    }

    val targetContent = when {
        selected -> colors.accent
        else -> colors.textPrimary
    }

    val animatedBg by animateColorAsState(
        targetValue = targetBg,
        animationSpec = motion.fastTween(),
        label = "chipBg"
    )

    val animatedContent by animateColorAsState(
        targetValue = targetContent,
        animationSpec = motion.fastTween(),
        label = "chipContent"
    )

    val borderStroke = when {
        selected -> BorderStroke(1.dp, colors.accent)
        !colors.isDark -> BorderStroke(HairlineInkStroke, colors.border)
        else -> null
    }

    // Outer box guarantees minimum 48dp hit area for accessibility
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics {
                role = Role.Checkbox
                stateDescription = if (selected) "Selected" else "Not selected"
            }
            .clickable(
                enabled = enabled,
                onClick = {
                    haptics.lightTick()
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner visual chip
        Row(
            modifier = Modifier
                .clip(ShapeChip)
                .background(animatedBg)
                .then(if (borderStroke != null) Modifier.border(borderStroke, ShapeChip) else Modifier)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected && variant == AppChipVariant.Filter) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = animatedContent
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = animatedContent
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = animatedContent
            )

            if (onRemoveClick != null && variant == AppChipVariant.Input) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove $label",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            haptics.lightTick()
                            onRemoveClick()
                        },
                    tint = animatedContent
                )
            }
        }
    }
}
