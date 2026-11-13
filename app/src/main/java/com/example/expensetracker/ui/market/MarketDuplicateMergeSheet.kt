package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.theme.WeightRegular
import com.example.expensetracker.theme.WeightSemiBold
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Group of duplicate commodity entries identified by canonical key normalization.
 */
data class DuplicateCommodityGroup(
    val canonicalKey: String,
    val primaryName: String,
    val variants: List<MarketCommodityUiModel>
)

/**
 * Scans list of commodities and groups spelling/whitespace/ZWJ duplicates.
 */
fun findDuplicateCommodities(commodities: List<MarketCommodityUiModel>): List<DuplicateCommodityGroup> {
    val grouped = commodities.groupBy { UnitConverterEngine.canonicalCommodityKey(it.name) }
    return grouped.filter { (_, items) ->
        items.map { it.name.trim().lowercase() }.distinct().size > 1
    }.map { (key, items) ->
        val sorted = items.sortedByDescending { it.purchaseCount }
        DuplicateCommodityGroup(
            canonicalKey = key,
            primaryName = sorted.first().name,
            variants = sorted
        )
    }
}

/**
 * Modal Bottom Sheet for reviewing and merging duplicate commodity names (Target Design E).
 * Retains 100% of price history and never deletes without confirmation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketDuplicateMergeSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    commodities: List<MarketCommodityUiModel>,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onMergeItems: (sourceName: String, targetName: String) -> Unit,
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val duplicateGroups = remember(commodities) { findDuplicateCommodities(commodities) }

    // Map of group key to chosen target name
    val selectedTargetNames = remember(duplicateGroups) {
        mutableStateMapOf<String, String>().apply {
            duplicateGroups.forEach { group ->
                put(group.canonicalKey, group.primaryName)
            }
        }
    }

    var groupToConfirmMerge by remember { mutableStateOf<DuplicateCommodityGroup?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = appColors.cardBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(appColors.divider)
                )
            }

            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(appColors.accent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.MergeType,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isBangla) "ডুপ্লিকেট পণ্য মার্জ করুন" else "Merge Duplicate Items",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = WeightBold,
                                fontSize = 17.sp
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = if (isBangla)
                                "${convertToBengaliDigits(duplicateGroups.size.toString())}টি গ্রুপে ডুপ্লিকেট পাওয়া গেছে"
                            else
                                "${duplicateGroups.size} duplicate group(s) detected",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = appColors.textSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Informational Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.accent.copy(alpha = 0.08f),
                border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isBangla)
                            "একই পণ্যের বিভিন্ন বানান বা স্পেসের তারতম্য থাকলে তা একটি প্রমিত নামে মার্জ করুন। মার্জ করলে অতীত সব কেনাকাটার ইতিহাস ও দর অক্ষুণ্ণ থাকবে।"
                        else
                            "Consolidate variant spellings and spaced duplicates into one primary name. All price history and purchase records are completely retained.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        ),
                        color = appColors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (duplicateGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = appColors.income,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBangla) "কোনো ডুপ্লিকেট পণ্য নেই" else "No duplicate items found",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = WeightBold,
                                fontSize = 15.sp
                            ),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "আপনার নিত্যপণ্যের ক্যাটালগ সম্পূর্ণ সাজানো রয়েছে।"
                            else "Your commodity catalog is clean and normalized.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(duplicateGroups, key = { it.canonicalKey }) { group ->
                        val chosenTarget = selectedTargetNames[group.canonicalKey] ?: group.primaryName
                        DuplicateGroupCard(
                            group = group,
                            chosenTarget = chosenTarget,
                            onSelectTarget = { newTarget ->
                                haptics.lightTick()
                                selectedTargetNames[group.canonicalKey] = newTarget
                            },
                            onMergeClick = {
                                groupToConfirmMerge = group
                            },
                            isBangla = isBangla,
                            currencySymbol = currencySymbol
                        )
                    }
                }
            }
        }
    }

    // Merge Confirmation Dialog
    if (groupToConfirmMerge != null) {
        val group = groupToConfirmMerge!!
        val chosenTarget = selectedTargetNames[group.canonicalKey] ?: group.primaryName
        val sourcesToMerge = group.variants.filter { it.name != chosenTarget }

        AlertDialog(
            onDismissRequest = { groupToConfirmMerge = null },
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
                        sourcesToMerge.forEach { source ->
                            onMergeItems(source.name, chosenTarget)
                        }
                        groupToConfirmMerge = null
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
                TextButton(onClick = { groupToConfirmMerge = null }) {
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
}

/**
 * Individual Duplicate Group Card with radio selectors for canonical name.
 */
@Composable
private fun DuplicateGroupCard(
    group: DuplicateCommodityGroup,
    chosenTarget: String,
    onSelectTarget: (String) -> Unit,
    onMergeClick: () -> Unit,
    isBangla: Boolean,
    currencySymbol: String
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "প্রমিত নাম নির্বাচন করুন:" else "Choose primary name:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = WeightSemiBold,
                        fontSize = 12.sp
                    ),
                    color = appColors.textSecondary
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = appColors.accent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isBangla) "${convertToBengaliDigits(group.variants.size.toString())}টি সংস্করণ"
                        else "${group.variants.size} variants",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = WeightBold,
                            fontSize = 12.sp,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = appColors.accent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Variant Options with Radio Buttons
            group.variants.forEach { variant ->
                val isSelected = variant.name == chosenTarget
                val countText = if (isBangla) convertToBengaliDigits(variant.purchaseCount.toString()) else variant.purchaseCount.toString()
                val rateText = if (isBangla) convertToBengaliDigits(variant.currentPrice.toInt().toString()) else variant.currentPrice.toInt().toString()

                Surface(
                    onClick = { onSelectTarget(variant.name) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) appColors.accent.copy(alpha = 0.08f) else appColors.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(HairlineInkStroke, if (isSelected) appColors.accent.copy(alpha = 0.4f) else appColors.divider),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .defaultMinSize(minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectTarget(variant.name) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = appColors.accent,
                                unselectedColor = appColors.textSecondary
                            ),
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = variant.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) WeightBold else WeightMedium,
                                    fontSize = 14.sp
                                ),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "$countText বার কেনা • দর $currencySymbol$rateText/${variant.defaultUnit}"
                                else "$countText logged • $currencySymbol$rateText/${variant.defaultUnit}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontFeatureSettings = TabularNumFeature
                                ),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preview and Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalPurchases = group.variants.sumOf { it.purchaseCount }
                val totalText = if (isBangla) convertToBengaliDigits(totalPurchases.toString()) else totalPurchases.toString()

                Text(
                    text = if (isBangla) "মার্জের পর: ${totalText}টি রেকর্ড থাকবে" else "Result: $totalPurchases total records",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.textSecondary
                )

                Button(
                    onClick = onMergeClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Text(
                        text = if (isBangla) "মার্জ করুন" else "Merge",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = WeightBold,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Previews for Multi-Theme Verification
// -----------------------------------------------------------------------------

private val previewMockCommodities = listOf(
    MarketCommodityUiModel(
        name = "কাঁচা মরিচ",
        category = "কাঁচাবাজার",
        defaultUnit = "কেজি",
        currentPrice = 200.0,
        previousPrice = 180.0,
        benchmarkPrice = 190.0,
        priceDiff = 20.0,
        percentDiff = 11.1,
        priceTrend = PriceTrend.UP,
        purchaseCount = 5,
        lastPurchasedTimestamp = System.currentTimeMillis()
    ),
    MarketCommodityUiModel(
        name = "কাঁচামরিচ",
        category = "কাঁচাবাজার",
        defaultUnit = "কেজি",
        currentPrice = 195.0,
        previousPrice = 190.0,
        benchmarkPrice = 192.0,
        priceDiff = 5.0,
        percentDiff = 2.6,
        priceTrend = PriceTrend.UP,
        purchaseCount = 2,
        lastPurchasedTimestamp = System.currentTimeMillis() - 86400000L
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "DuplicateMerge - Dark English", showBackground = true)
@Composable
private fun PreviewDuplicateMergeDarkEn() {
    ExpenseTrackerTheme(darkTheme = true) {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketDuplicateMergeSheet(
                    commodities = previewMockCommodities,
                    isBangla = false,
                    onMergeItems = { _, _ -> },
                    onDismiss = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "DuplicateMerge - Light Bengali", showBackground = true)
@Composable
private fun PreviewDuplicateMergeLightBn() {
    ExpenseTrackerTheme(darkTheme = false) {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketDuplicateMergeSheet(
                    commodities = previewMockCommodities,
                    isBangla = true,
                    onMergeItems = { _, _ -> },
                    onDismiss = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "DuplicateMerge - AMOLED Bengali", showBackground = true)
@Composable
private fun PreviewDuplicateMergeAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketDuplicateMergeSheet(
                    commodities = previewMockCommodities,
                    isBangla = true,
                    onMergeItems = { _, _ -> },
                    onDismiss = {}
                )
            }
        }
    }
}
