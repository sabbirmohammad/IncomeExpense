package com.example.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal

/**
 * Safe to spend today info row per UX_FLOWS section 2 & Screenshot Patch C3:
 * Safe to spend today: ৳ ... (info row)
 */
@Composable
fun SafeToSpendRow(
    dailySafeAmount: Double,
    hasBudget: Boolean,
    currencySymbol: String,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPaddingHorizontal)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Savings,
                contentDescription = null,
                tint = appColors.accent,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = "${strings.safeToSpendToday}:",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val amountDisplay = if (!hasBudget) {
            strings.setBudgetTask
        } else if (isPrivacyMode) {
            "••••"
        } else {
            formatCurrency(dailySafeAmount, currencySymbol, useBengaliNumerals)
        }

        Text(
            text = amountDisplay,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum"
            ),
            color = if (!hasBudget) appColors.accent else appColors.textPrimary,
            softWrap = false
        )
    }
}
