package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusPill
import com.example.expensetracker.theme.ShapePill

enum class AppButtonVariant {
    Primary,
    Secondary,
    Tertiary,
    Destructive
}

enum class AppButtonSize(val height: Dp) {
    Standard(48.dp),
    Large(56.dp)
}

/**
 * V7 Universal Button:
 * Meets WCAG 2.2 AA touch target (min 48dp), pill shape, loading state, icon slot, haptic integration.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    size: AppButtonSize = AppButtonSize.Standard,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = true
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val containerColor = when (variant) {
        AppButtonVariant.Primary -> colors.accent
        AppButtonVariant.Secondary -> colors.surfaceVariant
        AppButtonVariant.Tertiary -> Color.Transparent
        AppButtonVariant.Destructive -> colors.expense
    }

    val contentColor = when (variant) {
        AppButtonVariant.Primary -> colors.onAccent
        AppButtonVariant.Secondary -> colors.textPrimary
        AppButtonVariant.Tertiary -> colors.accent
        AppButtonVariant.Destructive -> Color.White
    }

    val disabledContainerColor = when (variant) {
        AppButtonVariant.Tertiary -> Color.Transparent
        else -> colors.surfaceVariant.copy(alpha = 0.5f)
    }

    val disabledContentColor = colors.textMuted

    val borderStroke = if (variant == AppButtonVariant.Secondary && !colors.isDark) {
        BorderStroke(HairlineInkStroke, colors.border)
    } else {
        null
    }

    Button(
        onClick = {
            if (!loading && enabled) {
                haptics.mediumClick()
                onClick()
            }
        },
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(size.height)
            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
        enabled = enabled && !loading,
        shape = ShapePill,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        ),
        border = borderStroke,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = if (size == AppButtonSize.Large) 16.sp else 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = contentColor
                )

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                }
            }
        }
    }
}
