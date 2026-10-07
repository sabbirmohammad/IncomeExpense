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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
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
    onClick: (() -> Unit)? = null
) {
    val haptics = rememberAppHaptics()
    val isIncome = type == TransactionType.INCOME
    val tintColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense
    val bgTint = tintColor.copy(alpha = 0.12f)
    val icon = if (isIncome) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward
    val strings = LocalAppStrings.current
    val label = labelOverride ?: if (isIncome) strings.income else strings.expense

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
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        bgTint,
                        bgTint.copy(alpha = 0.05f)
                    )
                )
            )
            .border(1.dp, tintColor.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        haptics.lightTick()
                        onClick()
                    }
                } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(tintColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp
                        ),
                        color = tintColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (onClick != null) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(tintColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add $label",
                            tint = tintColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = displayAmountText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
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
