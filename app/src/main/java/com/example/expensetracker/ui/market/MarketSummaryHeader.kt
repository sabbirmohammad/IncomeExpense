package com.example.expensetracker.ui.market

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * High-density Summary Header for Market Lists (Target Design B2):
 * - Maximum ~96dp height.
 * - List title, bought/total progress.
 * - Estimated vs Spent metrics with tabular numbers.
 * - Thin 6dp linear progress indicator with rounded corners.
 * - Primary action button: "Start shopping" / "Continue shopping".
 */
@Composable
fun MarketSummaryHeader(
    listTitle: String,
    totalCount: Int,
    purchasedCount: Int,
    totalSpent: Double,
    estimatedBudget: Double?,
    currencySymbol: String,
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val isBangla = strings.isBangla

    val progress = if (totalCount > 0) purchasedCount.toFloat() / totalCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "marketSummaryProgress"
    )

    val isShoppingInProgress = purchasedCount > 0
    val isOverspent = estimatedBudget != null && estimatedBudget > 0 && totalSpent > estimatedBudget

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Row 1: Title & Primary "Start / Continue Shopping" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = listTitle.ifBlank { if (isBangla) "বাজারের ফর্দ" else "Market List" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Bought / Total text
                    val progressCountText = if (totalCount == 0) {
                        if (isBangla) "ফর্দে কোনো পণ্য নেই" else "No items in list"
                    } else {
                        val cTotal = if (isBangla) convertToBengaliDigits(totalCount.toString()) else totalCount.toString()
                        val cBought = if (isBangla) convertToBengaliDigits(purchasedCount.toString()) else purchasedCount.toString()
                        val percent = if (totalCount > 0) ((purchasedCount.toFloat() / totalCount) * 100).toInt() else 0
                        val pDigits = if (isBangla) convertToBengaliDigits(percent.toString()) else percent.toString()
                        if (isBangla) "$cBought/${cTotal}টি কেনা ($pDigits%)" else "$cBought/$cTotal bought ($pDigits%)"
                    }

                    Text(
                        text = progressCountText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (progress >= 1f && totalCount > 0) appColors.income else appColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Primary Start/Continue Shopping button (min 48dp touch target)
                Button(
                    onClick = {
                        haptics.mediumClick()
                        onStartShopping()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isShoppingInProgress) appColors.income else appColors.accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = if (isShoppingInProgress) Icons.Filled.PlayArrow else Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isShoppingInProgress) strings.marketContinueShopping else strings.marketStartShopping,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Thin 6dp Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(appColors.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = animatedProgress.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isOverspent) appColors.warning
                            else if (progress >= 1f && totalCount > 0) appColors.income
                            else appColors.accent
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 3: Estimated vs Spent comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${strings.marketSpent}: ${formatCurrency(totalSpent, currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (isOverspent) appColors.warning else appColors.income
                    )

                    if (estimatedBudget != null && estimatedBudget > 0) {
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = appColors.textMuted
                        )
                        Text(
                            text = "${strings.marketEstimated}: ${formatCurrency(estimatedBudget, currencySymbol)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "MarketSummaryHeader - Dark En", showBackground = true)
@Composable
fun PreviewMarketSummaryHeaderDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSummaryHeader(
                    listTitle = "Weekly Grocery",
                    totalCount = 8,
                    purchasedCount = 3,
                    totalSpent = 350.0,
                    estimatedBudget = 850.0,
                    currencySymbol = "৳",
                    onStartShopping = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "MarketSummaryHeader - Light Bn", showBackground = true)
@Composable
fun PreviewMarketSummaryHeaderLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSummaryHeader(
                    listTitle = "সকালের কাঁচাবাজার",
                    totalCount = 6,
                    purchasedCount = 4,
                    totalSpent = 420.0,
                    estimatedBudget = 500.0,
                    currencySymbol = "৳",
                    onStartShopping = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "MarketSummaryHeader - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketSummaryHeaderAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketSummaryHeader(
                    listTitle = "মাছ-মাংসের বাজার",
                    totalCount = 4,
                    purchasedCount = 0,
                    totalSpent = 0.0,
                    estimatedBudget = 1200.0,
                    currencySymbol = "৳",
                    onStartShopping = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
