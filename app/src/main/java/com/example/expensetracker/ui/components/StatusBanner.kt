package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapeChip

enum class StatusBannerVariant {
    Info,
    Warning,
    Error,
    Success
}

/**
 * V7 Status Banner:
 * Used for upcoming bills, balance warnings, sync errors, and success confirmations.
 */
@Composable
fun StatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    variant: StatusBannerVariant = StatusBannerVariant.Info,
    customIcon: ImageVector? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val containerColor = when (variant) {
        StatusBannerVariant.Info -> colors.infoContainer
        StatusBannerVariant.Warning -> colors.warningContainer
        StatusBannerVariant.Error -> colors.expenseContainer
        StatusBannerVariant.Success -> colors.accentContainer
    }

    val iconColor = when (variant) {
        StatusBannerVariant.Info -> colors.info
        StatusBannerVariant.Warning -> colors.warning
        StatusBannerVariant.Error -> colors.expense
        StatusBannerVariant.Success -> colors.accent
    }

    val defaultIcon = when (variant) {
        StatusBannerVariant.Info -> Icons.Outlined.Info
        StatusBannerVariant.Warning -> Icons.Outlined.Warning
        StatusBannerVariant.Error -> Icons.Outlined.ErrorOutline
        StatusBannerVariant.Success -> Icons.Outlined.CheckCircle
    }

    val icon = customIcon ?: defaultIcon

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeChip)
            .background(containerColor)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    .semantics { role = Role.Button }
                    .clickable {
                        haptics.lightTick()
                        onActionClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = iconColor
                )
            }
        }
    }
}
