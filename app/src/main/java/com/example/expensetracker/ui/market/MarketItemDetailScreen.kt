package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MergeType
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.theme.WeightRegular
import com.example.expensetracker.theme.WeightSemiBold
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import kotlin.math.abs


/**
 * Full-screen Commodity Item Detail Drill-down (Target Design E):
 * - Hero latest price/unit, delta vs previous
 * - Range chips 7d / 30d / 90d / All
 * - Interactive line chart with tap tooltip
 * - Stats cards Min / Avg / Max
 * - Last 10 purchases (date, qty, price, rate)
 * - Actions: "Add to list", "Rename", "Merge with…"
 */
@Composable
fun MarketItemDetailScreen(
    commodity: MarketCommodityUiModel,
    allCommodities: List<MarketCommodityUiModel> = emptyList(),
    onFetchHistory: (String) -> List<ProductPriceRecord>,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onAddToList: (MarketCommodityUiModel) -> Unit,
    onRenameCommodity: ((oldName: String, newName: String) -> Unit)? = null,
    onMergeCommodity: ((sourceName: String, targetName: String) -> Unit)? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    var selectedRange by remember { mutableStateOf(PriceHistoryRange.ALL) }
    val allHistoryRecords = remember(commodity.name) { onFetchHistory(commodity.name) }

    // Filtered by range
    val filteredHistory = remember(allHistoryRecords, selectedRange) {
        if (selectedRange == PriceHistoryRange.ALL) {
            allHistoryRecords
        } else {
            val cutoff = System.currentTimeMillis() - (selectedRange.days.toLong() * 24 * 60 * 60 * 1000)
            allHistoryRecords.filter { it.timestamp >= cutoff }
        }
    }

    // Active records for chart & stats (fallback to all records if none in narrow window)
    val activeRecords = if (filteredHistory.isNotEmpty()) filteredHistory else allHistoryRecords

    // Min, Max, Avg rates
    val minRate = remember(activeRecords) {
        if (activeRecords.isNotEmpty()) activeRecords.minOf { it.unitPrice } else commodity.currentPrice
    }
    val maxRate = remember(activeRecords) {
        if (activeRecords.isNotEmpty()) activeRecords.maxOf { it.unitPrice } else commodity.currentPrice
    }
    val avgRate = remember(activeRecords) {
        if (activeRecords.isNotEmpty()) {
            val totalAmt = activeRecords.sumOf { it.totalAmount }
            val totalBase = activeRecords.sumOf { it.baseQuantity }
            if (totalBase > 0.0) totalAmt / totalBase else activeRecords.map { it.unitPrice }.average()
        } else commodity.benchmarkPrice
    }

    // Dialog states
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameInput by remember(commodity.name) { mutableStateOf(commodity.name) }
    var showMergeWithDialog by remember { mutableStateOf(false) }
    var mergeTargetName by remember { mutableStateOf<String?>(null) }
    var isAddedToListFeedback by remember { mutableStateOf(false) }

    Surface(
        color = appColors.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            onClose()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(appColors.cardBackground)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = if (isBangla) "ফিরে যান" else "Back",
                            tint = appColors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = commodity.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = WeightBold,
                                fontSize = 18.sp
                            ),
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (commodity.englishName.isNotBlank() && commodity.englishName != commodity.name) {
                                "${commodity.englishName} • ${commodity.category}"
                            } else commodity.category,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                // Header Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (onRenameCommodity != null) {
                        IconButton(
                            onClick = {
                                haptics.lightTick()
                                showRenameDialog = true
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = if (isBangla) "নাম পরিবর্তন" else "Rename",
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    if (onMergeCommodity != null) {
                        IconButton(
                            onClick = {
                                haptics.lightTick()
                                showMergeWithDialog = true
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MergeType,
                                contentDescription = if (isBangla) "মার্জ করুন" else "Merge with…",
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 2. Hero Section: Latest Price & Trend Delta
                item {
                    HeroPriceCard(
                        commodity = commodity,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                }

                // 3. Time Range Filter Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriceHistoryRange.entries.forEach { range ->
                            val isSelected = selectedRange == range
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    selectedRange = range
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) appColors.accent.copy(alpha = 0.12f) else appColors.cardBackground,
                                border = BorderStroke(HairlineInkStroke, if (isSelected) appColors.accent else appColors.divider),
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isBangla) range.titleBn else range.titleEn,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) WeightBold else WeightMedium,
                                            fontSize = 12.sp
                                        ),
                                        color = if (isSelected) appColors.accent else appColors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Interactive Line Chart with Tap Tooltip
                item {
                    InteractivePriceChartCard(
                        records = activeRecords,
                        commodity = commodity,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                }

                // 5. 3 Stats Cards: Min / Avg / Max
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMetricCard(
                            label = if (isBangla) "সর্বনিম্ন" else "Min",
                            rate = minRate,
                            unit = commodity.defaultUnit,
                            currencySymbol = currencySymbol,
                            color = appColors.income,
                            isBangla = isBangla,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            label = if (isBangla) "গড় দর" else "Average",
                            rate = avgRate,
                            unit = commodity.defaultUnit,
                            currencySymbol = currencySymbol,
                            color = appColors.accent,
                            isBangla = isBangla,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            label = if (isBangla) "সর্বোচ্চ" else "Max",
                            rate = maxRate,
                            unit = commodity.defaultUnit,
                            currencySymbol = currencySymbol,
                            color = appColors.expense,
                            isBangla = isBangla,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 6. Action Button: "Add to list"
                item {
                    Button(
                        onClick = {
                            haptics.mediumClick()
                            onAddToList(commodity)
                            isAddedToListFeedback = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAddedToListFeedback) appColors.income else appColors.accent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAddedToListFeedback) {
                                if (isBangla) "তালিকায় যুক্ত হয়েছে ✓" else "Added to list ✓"
                            } else {
                                if (isBangla) "তালিকায় যোগ করুন" else "Add to list"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = WeightBold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // 7. Last 10 Purchases Section
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val recentCount = minOf(10, allHistoryRecords.size)
                        val countText = if (isBangla) convertToBengaliDigits(recentCount.toString()) else recentCount.toString()
                        Text(
                            text = if (isBangla) "সাম্প্রতিক ক্রয়ের ইতিহাস (শেষ ${countText}টি)"
                            else "Recent Purchases (Last $countText)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = WeightBold,
                                fontSize = 14.5.sp
                            ),
                            color = appColors.textPrimary
                        )
                    }
                }

                if (allHistoryRecords.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = appColors.cardBackground,
                            border = BorderStroke(HairlineInkStroke, appColors.divider),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "কোনো অতীত ক্রয়ের রেকর্ড পাওয়া যায়নি" else "No purchase history found",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = appColors.textSecondary
                                )
                            }
                        }
                    }
                } else {
                    val last10 = allHistoryRecords.take(10)
                    itemsIndexed(last10) { index, record ->
                        PurchaseHistoryItemCard(
                            record = record,
                            isFirst = index == 0,
                            currencySymbol = currencySymbol,
                            isBangla = isBangla
                        )
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (showRenameDialog && onRenameCommodity != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = {
                Text(
                    text = if (isBangla) "পণ্যের নাম পরিবর্তন" else "Rename Item",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isBangla) "নতুন নাম লিখুন। সকল অতীত রেকর্ড এবং ফর্দে এটি আপডেট হবে।"
                        else "Enter a new name. All historical records will update cleanly.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary
                    )
                    OutlinedTextField(
                        value = renameInput,
                        onValueChange = { renameInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = renameInput.trim()
                        if (clean.isNotBlank() && clean != commodity.name) {
                            haptics.successFeedback()
                            onRenameCommodity(commodity.name, clean)
                        }
                        showRenameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (isBangla) "সংরক্ষণ করুন" else "Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }

    // Merge With Dialog
    if (showMergeWithDialog && onMergeCommodity != null) {
        val candidates = allCommodities.filter { it.name != commodity.name }
        var searchQuery by remember { mutableStateOf("") }
        val filteredCandidates = remember(candidates, searchQuery) {
            candidates.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showMergeWithDialog = false },
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
                            "'${commodity.name}' এর সকল অতীত দর ও রেকর্ড নির্বাচিত পণ্যের সাথে মার্জ হবে।"
                        else
                            "All records from '${commodity.name}' will be merged into the chosen target.",
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
                        if (target != null && target != commodity.name) {
                            haptics.successFeedback()
                            onMergeCommodity(commodity.name, target)
                        }
                        showMergeWithDialog = false
                    },
                    enabled = mergeTargetName != null,
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (isBangla) "মার্জ করুন" else "Merge", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMergeWithDialog = false }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }
}

