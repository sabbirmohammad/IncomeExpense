package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightRegular
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun CommodityMergeDialog(
    sourceName: String,
    allCommodities: List<MarketCommodityUiModel>,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onMerge: (sourceName: String, targetName: String) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val candidates = allCommodities.filter { it.name != sourceName }
    var searchQuery by remember { mutableStateOf("") }
    val filteredCandidates = remember(candidates, searchQuery) {
        candidates.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
    }
    var mergeTargetName by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "অন্য পণ্যের সাথে মার্জ করুন" else "Merge with Another Item",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold),
                color = appColors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBangla)
                        "'$sourceName' এর সকল অতীত দর ও রেকর্ড নির্বাচিত পণ্যের সাথে মার্জ হবে।"
                    else
                        "All records from '$sourceName' will be merged into the chosen target.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (isBangla) "পণ্য খুঁজুন..." else "Search target…") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(filteredCandidates) { _, candidate ->
                        val isSelected = mergeTargetName == candidate.name
                        Surface(
                            onClick = { mergeTargetName = candidate.name },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) appColors.accent.copy(alpha = 0.12f) else appColors.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(HairlineInkStroke, if (isSelected) appColors.accent else appColors.divider),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 44.dp)
                                .minimumInteractiveComponentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = candidate.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) WeightBold else WeightRegular,
                                        fontSize = 13.5.sp
                                    ),
                                    color = appColors.textPrimary
                                )
                                val countText = if (isBangla) convertToBengaliDigits(candidate.purchaseCount.toString()) else candidate.purchaseCount.toString()
                                Text(
                                    text = "$countText logs",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontFeatureSettings = TabularNumFeature
                                    ),
                                    color = appColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = mergeTargetName
                    if (target != null && target != sourceName) {
                        haptics.successFeedback()
                        onMerge(sourceName, target)
                    }
                    onDismiss()
                },
                enabled = mergeTargetName != null,
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = if (isBangla) "মার্জ করুন" else "Merge", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = appColors.surface
    )
}
