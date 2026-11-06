package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.dashboard.MarketFinishButton

/**
 * Handcrafted Persistent In-Store Shopping Dock:
 * Pinned at the bottom thumb-zone of the active market shopping screen.
 * Displays:
 * - Running total spend with bold tabular numbers.
 * - Items progress ratio ("৪/৫টি সম্পন্ন").
 * - Single-thumb "বাজার সম্পন্ন" trigger embedding [MarketFinishButton].
 */
@Composable
fun MarketBottomTripSummaryBar(
    totalSpent: Double,
    totalCount: Int,
    purchasedCount: Int,
    currencySymbol: String,
    isBangla: Boolean,
    onFinishShopping: () -> Unit,
    modifier: Modifier = Modifier,
    finishButton: (@Composable () -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val progressPct = if (totalCount > 0) (purchasedCount * 100) / totalCount else 0
    val isAllDone = purchasedCount == totalCount && totalCount > 0

    val progressText = if (isBangla) {
        "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি সম্পন্ন (${convertToBengaliDigits(progressPct.toString())}%)"
    } else {
        "$purchasedCount/$totalCount Done ($progressPct%)"
    }

    Surface(
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Running Total & Progress Ratio Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Running Total
                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = if (isBangla) "চলতি মোট খরচ" else "Running Total",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textSecondary
                    )
                    Text(
                        text = formatCurrency(totalSpent, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (totalSpent > 0) appColors.income else appColors.textPrimary
                    )
                }

                // Progress Ratio Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isAllDone) appColors.income.copy(alpha = 0.12f) else appColors.accent.copy(alpha = 0.10f),
                    border = BorderStroke(
                        HairlineInkStroke,
                        if (isAllDone) appColors.income.copy(alpha = 0.3f) else appColors.accent.copy(alpha = 0.25f)
                    )
                ) {
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (isAllDone) appColors.income else appColors.accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Row 2: Single-Thumb "বাজার সম্পন্ন" Action
            if (finishButton != null) {
                finishButton()
            } else {
                MarketFinishButton(
                    isBangla = isBangla,
                    onComplete = onFinishShopping,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
