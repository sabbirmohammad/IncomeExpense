package com.example.expensetracker.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun MetricChip(
    type: TransactionType,
    amount: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled,
    labelOverride: String? = null,
    isPrivacyMode: Boolean = false,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val isIncome = type == TransactionType.INCOME
    val tintColor = if (isIncome) appColors.income else appColors.expense
    val icon = if (isIncome) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward
    val strings = LocalAppStrings.current
    val label = labelOverride ?: if (isIncome) strings.income else strings.expense
    ActionMetricChip(
        label = label,
        amount = amount,
        currencySymbol = currencySymbol,
        icon = icon,
        tintColor = tintColor,
        modifier = modifier,
        useBengaliNumerals = useBengaliNumerals,
        isPrivacyMode = isPrivacyMode,
        compact = compact,
        onClick = onClick
    )
}

@Composable
fun ActionMetricChip(
    label: String,
    amount: Double,
    currencySymbol: String,
    icon: ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled,
    isPrivacyMode: Boolean = false,
    badgeText: String? = null,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val haptics = rememberAppHaptics()
    val bgTint = tintColor.copy(alpha = 0.12f)

    val animatedAmount by animateFloatAsState(
        targetValue = amount.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "metricAmountAnimation"
    )

    val displayAmountText = if (isPrivacyMode) {
        "$currencySymbol\u00A0••••"
    } else {
        formatCurrency(animatedAmount.toDouble(), currencySymbol, useBengaliNumerals)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (compact) 14.dp else 16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        bgTint,
                        bgTint.copy(alpha = 0.05f)
                    )
                )
            )
            .border(1.dp, tintColor.copy(alpha = 0.22f), RoundedCornerShape(if (compact) 14.dp else 16.dp))
            .then(
                if (onClick != null) {
                    Modifier
                        .minimumInteractiveComponentSize()
                        .clickable {
                            haptics.lightTick()
                            onClick()
                        }
                } else Modifier
            )
            .padding(
                horizontal = if (compact) 8.dp else 16.dp,
                vertical = if (compact) 12.dp else 16.dp
            )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (compact) 20.dp else 24.dp)
                            .clip(CircleShape)
                            .background(tintColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(if (compact) 12.dp else 14.dp)
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = if (compact) 11.5.sp else 12.5.sp
                        ),
                        color = tintColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tintColor.copy(alpha = 0.22f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = tintColor
                        )
                    }
                } else if (onClick != null) {
                    Box(
                        modifier = Modifier
                            .size(if (compact) 18.dp else 22.dp)
                            .clip(CircleShape)
                            .background(tintColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add $label",
                            tint = tintColor,
                            modifier = Modifier.size(if (compact) 11.dp else 14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))
            Text(
                text = displayAmountText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (compact) 15.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                ),
                color = LocalAppColors.current.textPrimary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
