package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.CardPadding
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.HairlineDividerStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius

/**
 * Standard Surface Card per specification:
 * #141826 surface, 16px corner radius, 16px padding, #232838 border.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    cornerRadius: Dp = CardCornerRadius,
    padding: Dp = CardPadding,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val resolvedBg = if (backgroundColor != Color.Unspecified) backgroundColor else LocalAppColors.current.surface
    val resolvedBorder = if (borderColor != Color.Unspecified) borderColor else LocalAppColors.current.border

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = resolvedBg),
        border = BorderStroke(1.dp, resolvedBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        onClick = onClick,
        content = content
    )
}

/**
 * 0.5px hairline divider per spec.
 */
@Composable
fun HairlineDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val resolvedColor = if (color != Color.Unspecified) color else LocalAppColors.current.border
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = HairlineDividerStroke,
        color = resolvedColor
    )
}

/**
 * Empty state card per spec:
 * Dashed border #2a2f40, centered icon badge, bold one-line headline,
 * one-line explanation, solid accent CTA button below.
 */
@Composable
fun EmptyStateCard(
    icon: ImageVector = Icons.Outlined.ReceiptLong,
    headline: String,
    explanation: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                )
                drawRoundRect(
                    color = colors.border,
                    cornerRadius = CornerRadius(CardCornerRadius.toPx(), CardCornerRadius.toPx()),
                    style = stroke
                )
            }
            .clip(RoundedCornerShape(CardCornerRadius))
            .background(colors.surface)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(SmallElementRadius))
                    .background(ColorAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ColorAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
            }
        }
    }
}
