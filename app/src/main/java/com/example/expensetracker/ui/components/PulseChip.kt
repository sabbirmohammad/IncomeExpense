package com.example.expensetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

enum class PulseChipTone {
    ACCENT,
    INCOME,
    EXPENSE,
    WARNING,
    INFO,
    NEUTRAL
}

/**
 * PulseChip:
 * Standard Chip for Pulse Design System:
 * - 40dp visual height
 * - 48x48dp minimum touch target via minimumInteractiveComponentSize()
 * - Soft pill shape
 * - Selected state tinted with semantic container colors
 * - Supports leading icon/emoji and trailing dismiss icon
 */
@Composable
fun PulseChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: PulseChipTone = PulseChipTone.ACCENT,
    leadingIcon: ImageVector? = null,
    leadingEmoji: String? = null,
    onRemoveClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current

    val (selectedContainer, selectedContent) = when (tone) {
        PulseChipTone.ACCENT -> appColors.accentContainer to appColors.accent
        PulseChipTone.INCOME -> appColors.incomeContainer to appColors.income
        PulseChipTone.EXPENSE -> appColors.expenseContainer to appColors.expense
        PulseChipTone.WARNING -> appColors.warningContainer to appColors.warning
        PulseChipTone.INFO -> appColors.infoContainer to appColors.info
        PulseChipTone.NEUTRAL -> appColors.surfaceVariant to appColors.textPrimary
    }

    val containerColor by animateColorAsState(
        targetValue = if (selected) selectedContainer else appColors.surfaceVariant,
        label = "PulseChipContainer"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) selectedContent else appColors.textSecondary,
        label = "PulseChipContent"
    )

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Checkbox,
                onClick = onClick
            )
            .semantics { this.selected = selected }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (leadingEmoji != null) {
                Text(text = leadingEmoji, fontSize = 14.sp)
            } else if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = contentColor
            )

            if (onRemoveClick != null) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable(
                            role = Role.Button,
                            onClick = onRemoveClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = contentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "PulseChip - Dark")
@Composable
private fun PulseChipDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.background, modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PulseChip(text = "Food", selected = true, onClick = {}, tone = PulseChipTone.ACCENT)
                PulseChip(text = "Travel", selected = false, onClick = {})
            }
        }
    }
}

@Preview(name = "PulseChip - Light Bengali")
@Composable
private fun PulseChipLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.background, modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PulseChip(text = "বাজার", selected = true, onClick = {}, tone = PulseChipTone.INCOME)
                PulseChip(text = "যাতায়াত", selected = false, onClick = {})
            }
        }
    }
}
