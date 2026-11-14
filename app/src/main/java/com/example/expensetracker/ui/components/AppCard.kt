package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.CardPadding
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors

/**
 * V7 Card Variants: Flat (default), Raised (sheets & heroes), Tinted (colored container).
 */
enum class AppCardVariant {
    Flat,
    Raised,
    Tinted
}

/**
 * Standard Surface Card per Living Ledger / Refined Ledger v7 specification:
 * - Flat: Tonal separation in Dark/AMOLED (no border outline per C1); hairline border in Light.
 * - Raised: Soft elevation + surfaceRaised background.
 * - Tinted: Container colored surface.
 * - Corner radius: 16dp (RadiusM) per C1 patch.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: AppCardVariant = AppCardVariant.Flat,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    cornerRadius: Dp = CardCornerRadius,
    padding: Dp = CardPadding,
    borderStrokeWidth: Dp = HairlineInkStroke,
    borderStroke: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalAppColors.current

    val resolvedBg = when {
        backgroundColor != Color.Unspecified -> backgroundColor
        variant == AppCardVariant.Raised -> colors.surfaceRaised
        variant == AppCardVariant.Tinted -> colors.accentContainer
        else -> colors.surface
    }

    val resolvedBorderStroke = when {
        borderStroke != null -> borderStroke
        borderColor != Color.Unspecified -> BorderStroke(borderStrokeWidth, borderColor)
        variant == AppCardVariant.Flat && !colors.isDark -> BorderStroke(HairlineInkStroke, colors.border)
        else -> null
    }

    val resolvedElevation = if (variant == AppCardVariant.Raised) 4.dp else 0.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = resolvedBg),
        border = resolvedBorderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = resolvedElevation)
    ) {
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

// Backwards compatibility alias
@Composable
fun OneUiCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    borderStrokeWidth: Dp = HairlineInkStroke,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        borderStrokeWidth = borderStrokeWidth,
        onClick = onClick,
        content = content
    )
}
