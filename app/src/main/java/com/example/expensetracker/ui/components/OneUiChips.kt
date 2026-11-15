package com.example.expensetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
