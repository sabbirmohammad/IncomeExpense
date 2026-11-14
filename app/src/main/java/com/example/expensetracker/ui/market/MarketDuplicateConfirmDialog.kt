package com.example.expensetracker.ui.market

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketDuplicateConfirmDialog(
    group: DuplicateCommodityGroup,
    chosenTarget: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirmMerge: (sources: List<MarketCommodityUiModel>, target: String) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val sourcesToMerge = group.variants.filter { it.name != chosenTarget }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "মার্জ নিশ্চিত করুন" else "Confirm Merge",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold),
                color = appColors.textPrimary
            )
        },
        text = {
            val totalPurchases = group.variants.sumOf { it.purchaseCount }
            val countText = if (isBangla) convertToBengaliDigits(totalPurchases.toString()) else totalPurchases.toString()
            val sourcesList = sourcesToMerge.joinToString(", ") { "'${it.name}'" }

            Text(
                text = if (isBangla)
                    "$sourcesList পণ্যগুলোর সকল অতীত দরদাম ও রেকর্ড '$chosenTarget' নামে মার্জ হবে (মোট $countText বার কেনা)। এই পরিবর্তন সব ফর্দে কার্যকর হবে।"
                else
                    "All records from $sourcesList will be merged into '$chosenTarget' ($countText total purchases). No price history will be deleted.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = appColors.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    haptics.successFeedback()
                    onConfirmMerge(sourcesToMerge, chosenTarget)
                },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isBangla) "হ্যাঁ, মার্জ করুন" else "Yes, Merge",
                    fontWeight = WeightBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isBangla) "বাতিল" else "Cancel",
                    color = appColors.textSecondary
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = appColors.surface
    )
}
