package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusBadgeTag
import com.example.expensetracker.theme.TabularNumFeature

/**
 * StatTile:
 * Living Ledger 2.0 KPI Tile:
 * - Label: bodySmall, textSecondary
 * - Value: titleLarge with tabular numerals
 * - Optional Delta Chip: emerald (+) / terracotta (-) tinted badge
 * - Strict >= 48dp touch target when clickable
 * - TalkBack semantics merged for seamless readout
 */
@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LocalAppColors.current.textPrimary,
    delta: String? = null,
    isPositiveDelta: Boolean? = null,
    onClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val clickableModifier = if (onClick != null) {
        Modifier
            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                haptics.lightTick()
                onClick()
            }
            .semantics { role = Role.Button }
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .then(clickableModifier)
            .padding(vertical = 4.dp)
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            color = appColors.textSecondary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(3.dp))

        // 2. Value and Optional Delta Chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = valueColor,
                maxLines = 1
            )

            if (delta != null) {
                val deltaBg = when (isPositiveDelta) {
                    true -> appColors.accentContainer
                    false -> appColors.expenseContainer
                    else -> appColors.surfaceVariant
                }
                val deltaColor = when (isPositiveDelta) {
                    true -> appColors.income
                    false -> appColors.expense
                    else -> appColors.textSecondary
                }

                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(RadiusBadgeTag))
                        .background(deltaBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = delta,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = deltaColor
                    )
                }
            }
        }
    }
}

// =========================================================================
// PREVIEWS IN ALL 4 THEMES (EN + BN)
// =========================================================================

@Preview(name = "StatTile - Dark En", showBackground = true)
@Composable
private fun StatTileDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "Monthly Expense",
                value = "৳ 24,500",
                valueColor = LocalAppColors.current.expense,
                delta = "-8%",
                isPositiveDelta = false
            )
        }
    }
}

@Preview(name = "StatTile - Light Bn", showBackground = true)
@Composable
private fun StatTileLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "মোট আয়",
                value = "৳ ৫০,০০০",
                valueColor = LocalAppColors.current.income,
                delta = "+১২%",
                isPositiveDelta = true
            )
        }
    }
}

@Preview(name = "StatTile - AMOLED Bn", showBackground = true)
@Composable
private fun StatTileAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "দৈনিক গড় খরচ",
                value = "৳ ১,১৫০"
            )
        }
    }
}

@Preview(name = "StatTile - System En Clickable", showBackground = true)
@Composable
private fun StatTileSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StatTile(
                label = "Savings Rate",
                value = "34.5%",
                valueColor = LocalAppColors.current.accent,
                onClick = {}
            )
        }
    }
}
