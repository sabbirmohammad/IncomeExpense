package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Docked Persistent Bottom Trip Summary Bar.
 * - Displays running spend total in prominent bold headline typography.
 * - Displays dynamic count progress pill (e.g. ৮/১২ সম্পন্ন (৬৭%)).
 * - Offers a high-visibility 1-Tap Checkout button with celebratory haptics.
 * - Sits persistently in the thumb-zone for one-handed market shopping trips.
 */
@Composable
fun MarketBottomTripSummaryBar(
    totalSpent: Double,
    purchasedCount: Int,
    totalCount: Int,
    isBangla: Boolean,
    onFinishShopping: () -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "৳"
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val progressRatio = if (totalCount > 0) (purchasedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 0f
    val progressPercent = (progressRatio * 100).toInt()
    val isAllDone = totalCount > 0 && purchasedCount >= totalCount

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, appColors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Running spend total & progress pill
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Small contextual spend caption
                    Text(
                        text = if (isBangla) "চলতি বাজার খরচ" else "Trip Spend Total",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textSecondary
                    )

                    // Bold headline typography for running total spend
                    Text(
                        text = formatCurrency(totalSpent, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = appColors.textPrimary
                    )

                    // Dynamic Count Progress Pill
                    val countDisplay = if (isBangla) {
                        "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())} সম্পন্ন (${convertToBengaliDigits(progressPercent.toString())}%)"
                    } else {
                        "$purchasedCount/$totalCount Done ($progressPercent%)"
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAllDone) appColors.income.copy(alpha = 0.15f) else appColors.accent.copy(alpha = 0.12f),
                            border = BorderStroke(
                                0.8.dp,
                                if (isAllDone) appColors.income.copy(alpha = 0.35f) else appColors.accent.copy(alpha = 0.25f)
                            )
                        ) {
                            Text(
                                text = countDisplay,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isAllDone) appColors.income else appColors.accent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right: High-visibility 1-Tap Checkout Button
                Button(
                    onClick = {
                        haptics.successFeedback()
                        onFinishShopping()
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.income,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = if (isBangla) "বাজার সম্পন্ন করুন" else "Checkout",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            }

            // Sleek micro progress bar at the bottom of the card
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = if (isAllDone) appColors.income else appColors.accent,
                trackColor = appColors.border.copy(alpha = 0.4f)
            )
        }
    }
}
