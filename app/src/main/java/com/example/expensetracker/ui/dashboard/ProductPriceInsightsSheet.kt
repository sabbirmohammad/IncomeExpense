package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.MarketSearchBar
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Interactive Commodity Price Intelligence & Trend Sheet.
 * Displays real-time unit price history, inflation/deflation deltas,
 * 30-day drift comparisons, and purchase timelines.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPriceInsightsSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    priceInsights: Map<String, ProductPriceInsight>,
    onFetchHistory: (productName: String) -> List<ProductPriceRecord>,
    isBangla: Boolean = true,
    currencySymbol: String = "৳",
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = appColors.background,
        dragHandle = null
    ) {
        ProductPriceInsightsContent(
            priceInsights = priceInsights,
            onFetchHistory = onFetchHistory,
            isBangla = isBangla,
            currencySymbol = currencySymbol,
            onClose = onDismiss,
            modifier = Modifier.fillMaxHeight(0.92f)
        )
    }
}

/**
 * Embeddable commodity price intelligence & trend content.
 * Can be hosted directly in full-screen tabs (e.g. MarketScreen) or in bottom sheets.
 */
@Composable
fun ProductPriceInsightsContent(
    priceInsights: Map<String, ProductPriceInsight>,
    onFetchHistory: (productName: String) -> List<ProductPriceRecord>,
    isBangla: Boolean = true,
    currencySymbol: String = "৳",
    onClose: (() -> Unit)? = null,
    searchQuery: String = "",
    onSearchQueryChange: ((String) -> Unit)? = null,
    showInternalSearchBar: Boolean = true,
    showHeader: Boolean = onClose != null,
    onOpenSearch: (() -> Unit)? = null,
    isSearchActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    var localSearchQuery by remember { mutableStateOf("") }
    val effectiveSearchQuery = if (onSearchQueryChange != null) searchQuery else localSearchQuery
    val updateSearchQuery: (String) -> Unit = { query ->
        if (onSearchQueryChange != null) {
            onSearchQueryChange(query)
        } else {
            localSearchQuery = query
        }
    }

    var selectedFilter by remember { mutableStateOf<PriceTrend?>(null) } // null = All
    var selectedInsightForDetail by remember { mutableStateOf<ProductPriceInsight?>(null) }

    val allInsights = remember(priceInsights) { priceInsights.values.toList() }

    val filteredInsights = remember(allInsights, effectiveSearchQuery, selectedFilter) {
        allInsights.filter { insight ->
            val matchesQuery = effectiveSearchQuery.isBlank() ||
                insight.productName.contains(effectiveSearchQuery.trim().lowercase()) ||
                insight.baseUnit.contains(effectiveSearchQuery.trim().lowercase())
            val matchesFilter = selectedFilter == null || insight.priceTrend == selectedFilter
            matchesQuery && matchesFilter
        }.sortedByDescending { it.lastPurchasedTimestamp }
    }

    val priceUpCount = remember(allInsights) { allInsights.count { it.priceTrend == PriceTrend.UP } }
    val priceDownCount = remember(allInsights) { allInsights.count { it.priceTrend == PriceTrend.DOWN } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (onClose != null) 16.dp else 4.dp)
    ) {
        // Modal Header (only shown when displayed inside a ModalBottomSheet with onClose != null)
        if (showHeader && onClose != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "দ্রব্যমূল্যের দরদাম ও বিশ্লেষণ" else "Price Intelligence & Trends",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = appColors.textPrimary
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(appColors.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Quick KPI Filter Bar (Compact 38.dp Ribbon)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All Items Filter Chip
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedFilter = null
                },
                shape = RoundedCornerShape(10.dp),
                color = if (selectedFilter == null) appColors.accent.copy(alpha = 0.12f) else appColors.surface,
                border = BorderStroke(
                    if (selectedFilter == null) 1.2.dp else 1.dp,
                    if (selectedFilter == null) appColors.accent else appColors.border
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBangla) "সকল (${convertToBengaliDigits(allInsights.size.toString())})"
                               else "All (${allInsights.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedFilter == null) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selectedFilter == null) appColors.accent else appColors.textSecondary
                    )
                }
            }

            // Price Up Filter Chip
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedFilter = if (selectedFilter == PriceTrend.UP) null else PriceTrend.UP
                },
                shape = RoundedCornerShape(10.dp),
                color = appColors.expense.copy(alpha = if (selectedFilter == PriceTrend.UP) 0.16f else 0.07f),
                border = BorderStroke(
                    if (selectedFilter == PriceTrend.UP) 1.2.dp else 1.dp,
                    appColors.expense.copy(alpha = if (selectedFilter == PriceTrend.UP) 0.8f else 0.25f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = appColors.expense,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBangla) "বেড়েছে (${convertToBengaliDigits(priceUpCount.toString())})"
                               else "Up (${priceUpCount})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedFilter == PriceTrend.UP) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = appColors.expense
                    )
                }
            }

            // Price Down Filter Chip
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedFilter = if (selectedFilter == PriceTrend.DOWN) null else PriceTrend.DOWN
                },
                shape = RoundedCornerShape(10.dp),
                color = appColors.income.copy(alpha = if (selectedFilter == PriceTrend.DOWN) 0.16f else 0.07f),
                border = BorderStroke(
                    if (selectedFilter == PriceTrend.DOWN) 1.2.dp else 1.dp,
                    appColors.income.copy(alpha = if (selectedFilter == PriceTrend.DOWN) 0.8f else 0.25f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.TrendingDown,
                        contentDescription = null,
                        tint = appColors.income,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBangla) "সাশ্রয়ী (${convertToBengaliDigits(priceDownCount.toString())})"
                               else "Down (${priceDownCount})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedFilter == PriceTrend.DOWN) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = appColors.income
                    )
                }
            }
        }

        if (showInternalSearchBar) {
            Spacer(modifier = Modifier.height(10.dp))
            MarketSearchBar(
                query = effectiveSearchQuery,
                onQueryChange = updateSearchQuery,
                placeholder = if (isBangla) "পণ্য খুঁজুন (যেমন: আলু, তেল, চাল)..." else "Search product (e.g. Potato, Oil)...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                height = 40.dp,
                shape = RoundedCornerShape(12.dp),
                onClear = { updateSearchQuery("") }
            )
        } else if (effectiveSearchQuery.isNotBlank() && !isSearchActive) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                onClick = { onOpenSearch?.invoke() },
                shape = RoundedCornerShape(10.dp),
                color = appColors.accent.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isBangla) "অনুসন্ধান: \"${effectiveSearchQuery.trim()}\" (${convertToBengaliDigits(filteredInsights.size.toString())}টি পণ্য)"
                                   else "Search: \"${effectiveSearchQuery.trim()}\" (${filteredInsights.size} items)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.accent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(appColors.accent.copy(alpha = 0.15f))
                            .clickable { updateSearchQuery("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = appColors.accent,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Products List (Fintech Stock Watchlist Rows)
        if (filteredInsights.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (allInsights.isEmpty()) {
                            if (isBangla) "এখনো কোনো পণ্যের দর রেকর্ড হয়নি" else "No price records tracked yet"
                        } else if (effectiveSearchQuery.isNotBlank()) {
                            if (isBangla) "'${effectiveSearchQuery.trim()}' নামে কোনো পণ্য পাওয়া যায়নি" else "No items found matching '${effectiveSearchQuery.trim()}'"
                        } else {
                            if (isBangla) "নির্বাচিত ফিল্টারে কোনো পণ্য পাওয়া যায়নি" else "No items found matching filter"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (allInsights.isEmpty()) {
                            if (isBangla) "বাজারের ফর্দ সম্পন্ন করার পর এখানে পণ্যের দামের তুলনামূলক চিত্র প্রদর্শিত হবে।"
                            else "Complete your market trips to start tracking item price trends."
                        } else {
                            if (isBangla) "বানান চেক করুন অথবা ফিল্টার পরিবর্তন করে আবার চেষ্টা করুন।"
                            else "Check spelling or change filter to try again."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredInsights, key = { it.productName }) { insight ->
                    ProductPriceStockRow(
                        insight = insight,
                        isBangla = isBangla,
                        currencySymbol = currencySymbol,
                        onClick = {
                            haptics.lightTick()
                            selectedInsightForDetail = insight
                        }
                    )
                }
            }
        }
    }

    // Detail Bottom Sheet when an item row is tapped
    if (selectedInsightForDetail != null) {
        ProductPriceDetailSheet(
            insight = selectedInsightForDetail!!,
            onFetchHistory = onFetchHistory,
            isBangla = isBangla,
            currencySymbol = currencySymbol,
            onDismiss = { selectedInsightForDetail = null }
        )
    }
}

/**
 * High-density, professional Fintech Stock Watchlist Row.
 * Displays: Product name, purchase count, average rate, current rate, and color-coded trend badge.
 */
@Composable
private fun ProductPriceStockRow(
    insight: ProductPriceInsight,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current

    val currentRateFormatted = if (insight.currentUnitPrice % 1.0 == 0.0) {
        insight.currentUnitPrice.toInt().toString()
    } else {
        "%.1f".format(insight.currentUnitPrice)
    }
    val currentRateText = if (isBangla) convertToBengaliDigits(currentRateFormatted) else currentRateFormatted

    val avgRateFormatted = if (insight.avgUnitPrice % 1.0 == 0.0) {
        insight.avgUnitPrice.toInt().toString()
    } else {
        "%.1f".format(insight.avgUnitPrice)
    }
    val avgRateText = if (isBangla) convertToBengaliDigits(avgRateFormatted) else avgRateFormatted

    val purchaseCountText = if (isBangla) convertToBengaliDigits(insight.purchaseCount.toString()) else insight.purchaseCount.toString()

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border.copy(alpha = 0.65f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Column: Name & Purchase Count + Avg Price
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = insight.productName.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        ),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (insight.purchaseCount <= 1) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = appColors.accent.copy(alpha = 0.12f),
                            border = BorderStroke(0.8.dp, appColors.accent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isBangla) "নতুন" else "New",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = appColors.accent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.5.dp))

                Text(
                    text = if (isBangla) "$purchaseCountText বার কেনা • গড় $currencySymbol$avgRateText/${insight.baseUnit}"
                           else "$purchaseCountText logged • Avg $currencySymbol$avgRateText/${insight.baseUnit}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right Column: Price and Stock Trend Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Latest Price Column
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$currencySymbol$currentRateText",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = "/${insight.baseUnit}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textSecondary,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }

                    val dateText = DateTimeHelper.formatDayGroup(insight.lastPurchasedTimestamp, isBangla)
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = appColors.textSecondary.copy(alpha = 0.75f)
                    )
                }

                // Trend Pill (Stock Ticker Badge)
                if (insight.previousUnitPrice != null) {
                    val isUp = insight.isPriceUp
                    val isDown = insight.isPriceDown
                    val pillBg = if (isUp) appColors.expense.copy(alpha = 0.12f)
                                 else if (isDown) appColors.income.copy(alpha = 0.12f)
                                 else appColors.surfaceVariant
                    val pillColor = if (isUp) appColors.expense else if (isDown) appColors.income else appColors.textSecondary
                    val iconVector = if (isUp) Icons.AutoMirrored.Outlined.TrendingUp
                                     else if (isDown) Icons.AutoMirrored.Outlined.TrendingDown
                                     else null

                    val diffAbs = kotlin.math.abs(insight.priceDiff)
                    val diffFormatted = if (diffAbs % 1.0 == 0.0) diffAbs.toInt().toString() else "%.1f".format(diffAbs)
                    val diffText = if (isBangla) convertToBengaliDigits(diffFormatted) else diffFormatted
                    val pctAbs = "%.1f".format(kotlin.math.abs(insight.percentDiff))
                    val pctText = if (isBangla) convertToBengaliDigits(pctAbs) else pctAbs
                    val sign = if (isUp) "+" else if (isDown) "-" else ""

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = pillBg,
                        border = BorderStroke(0.8.dp, pillColor.copy(alpha = 0.25f)),
                        modifier = Modifier.defaultMinSize(minWidth = 62.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (iconVector != null) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = null,
                                    tint = pillColor,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = "$sign$diffText ($pctText%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = pillColor
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(0.8.dp, appColors.border.copy(alpha = 0.5f)),
                        modifier = Modifier.defaultMinSize(minWidth = 52.dp)
                    ) {
                        Text(
                            text = if (isBangla) "১ম ক্রয়" else "1st buy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            ),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.5.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Detailed Modal Bottom Sheet opened when clicking an item row.
 * Offers deep analytics: 4-KPI Metric Grid, Price Range Bar, 30-Day Inflation, and Purchase History.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPriceDetailSheet(
    insight: ProductPriceInsight,
    onFetchHistory: (String) -> List<ProductPriceRecord>,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = appColors.background,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
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
                        .background(appColors.border)
                )
            }

            // Header Row: Product Name, Details & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insight.productName.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        ),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBangla) "পরিমাপক একক: ${insight.baseUnit} • ${convertToBengaliDigits(insight.purchaseCount.toString())} বার ক্রয় রেকর্ড"
                               else "Base Unit: ${insight.baseUnit} • ${insight.purchaseCount} purchases logged",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = appColors.textSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(appColors.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Key Metrics Grid (2 x 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1: Current Rate
                PriceMetricCard(
                    title = if (isBangla) "সর্বশেষ দর" else "Latest Rate",
                    value = insight.formatUnitRate(),
                    subValue = if (insight.previousUnitPrice != null) insight.formatDiff() else (if (isBangla) "প্রথম ক্রয়" else "First buy"),
                    subColor = if (insight.isPriceUp) appColors.expense else if (insight.isPriceDown) appColors.income else appColors.textSecondary,
                    modifier = Modifier.weight(1f)
                )

                // Metric 2: Average Rate
                PriceMetricCard(
                    title = if (isBangla) "গড় দর" else "Average Rate",
                    value = insight.formatAvgRate(),
                    subValue = if (isBangla) "${convertToBengaliDigits(insight.purchaseCount.toString())} বার ক্রয়ের গড়" else "Based on ${insight.purchaseCount} buys",
                    subColor = appColors.accent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 3: Min Rate
                val minFormatted = if (insight.minUnitPrice % 1.0 == 0.0) insight.minUnitPrice.toInt().toString() else "%.1f".format(insight.minUnitPrice)
                val minText = if (isBangla) convertToBengaliDigits(minFormatted) else minFormatted
                PriceMetricCard(
                    title = if (isBangla) "সর্বনিম্ন দর" else "Lowest Rate",
                    value = "$currencySymbol$minText/${insight.baseUnit}",
                    subValue = if (isBangla) "সর্বকালের সর্বনিম্ন" else "All-time low",
                    subColor = appColors.income,
                    modifier = Modifier.weight(1f)
                )

                // Metric 4: Max Rate
                val maxFormatted = if (insight.maxUnitPrice % 1.0 == 0.0) insight.maxUnitPrice.toInt().toString() else "%.1f".format(insight.maxUnitPrice)
                val maxText = if (isBangla) convertToBengaliDigits(maxFormatted) else maxFormatted
                PriceMetricCard(
                    title = if (isBangla) "সর্বোচ্চ দর" else "Highest Rate",
                    value = "$currencySymbol$maxText/${insight.baseUnit}",
                    subValue = if (isBangla) "সর্বকালের সর্বোচ্চ" else "All-time peak",
                    subColor = appColors.expense,
                    modifier = Modifier.weight(1f)
                )
            }

            // Visual Min-to-Max Price Range Scale Bar
            val minRate = insight.minUnitPrice
            val maxRate = insight.maxUnitPrice
            val curRate = insight.currentUnitPrice

            if (minRate > 0.0 && maxRate >= minRate) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surface,
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "দরের পরিসীমা (Min - Max Range)" else "Price Range (Min - Max)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp),
                                color = appColors.textPrimary
                            )
                            insight.oneMonthPercentDiff?.let { mPct ->
                                val mPctAbs = "%.1f".format(kotlin.math.abs(mPct))
                                val mPctText = if (isBangla) convertToBengaliDigits(mPctAbs) else mPctAbs
                                val mSign = if (mPct > 0) "+ " else "- "
                                val mColor = if (mPct > 0) appColors.expense else appColors.income
                                Text(
                                    text = if (isBangla) "৩০ দিনে: $mSign$mPctText%" else "30d: $mSign$mPctText%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                    color = mColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val fraction = if (maxRate > minRate) {
                            ((curRate - minRate) / (maxRate - minRate)).coerceIn(0.0, 1.0).toFloat().coerceIn(0.01f, 1.0f)
                        } else 0.5f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(appColors.border.copy(alpha = 0.35f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                appColors.income.copy(alpha = 0.45f),
                                                appColors.accent.copy(alpha = 0.3f),
                                                appColors.expense.copy(alpha = 0.45f)
                                            )
                                        )
                                    )
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (insight.priceTrend) {
                                                PriceTrend.UP -> appColors.expense
                                                PriceTrend.DOWN -> appColors.income
                                                PriceTrend.STABLE -> appColors.accent
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Purchase History Section
            Spacer(modifier = Modifier.height(14.dp))
            val history = remember(insight.productName) { onFetchHistory(insight.productName) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "ক্রয়ের বিস্তারিত টাইমলাইন (${convertToBengaliDigits(history.size.toString())}টি)"
                           else "Purchase History Timeline (${history.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.surface,
                border = BorderStroke(1.dp, appColors.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "কোনো অতীত ক্রয়ের রেকর্ড পাওয়া যায়নি" else "No purchase history found",
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(history) { index, record ->
                            val dateFormatted = DateTimeHelper.formatDayGroup(record.timestamp, isBangla)
                            val rateFormatted = if (record.unitPrice % 1.0 == 0.0) record.unitPrice.toInt().toString() else "%.1f".format(record.unitPrice)
                            val rateText = if (isBangla) convertToBengaliDigits(rateFormatted) else rateFormatted
                            val amtFormatted = if (record.totalAmount % 1.0 == 0.0) record.totalAmount.toInt().toString() else "%.1f".format(record.totalAmount)
                            val amtText = if (isBangla) convertToBengaliDigits(amtFormatted) else amtFormatted
                            val qtyFormatted = if (record.rawQuantity % 1.0 == 0.0) record.rawQuantity.toInt().toString() else "%.1f".format(record.rawQuantity)
                            val qtyText = if (isBangla) convertToBengaliDigits(qtyFormatted) else qtyFormatted

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = dateFormatted,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = appColors.textPrimary
                                    )
                                    Text(
                                        text = "$qtyText ${record.rawUnit} • মোট $currencySymbol$amtText",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                        color = appColors.textSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (index == 0) appColors.accent.copy(alpha = 0.12f) else appColors.background,
                                    border = BorderStroke(1.dp, if (index == 0) appColors.accent.copy(alpha = 0.35f) else appColors.border)
                                ) {
                                    Text(
                                        text = "$currencySymbol$rateText/${record.baseUnit}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp
                                        ),
                                        color = if (index == 0) appColors.accent else appColors.textPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Metric card component for 2x2 grid inside detail sheet.
 */
@Composable
private fun PriceMetricCard(
    title: String,
    value: String,
    subValue: String,
    subColor: Color,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = subColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 3-point mini vector sparkline curve (↗ ↘ ➔) representing recent commodity price dynamics.
 * - Point 1: 30 days prior (or baseline/earliest price)
 * - Point 2: Immediately preceding unit rate
 * - Point 3: Current unit rate
 */
@Composable
fun ProductPriceSparkline(
    insight: ProductPriceInsight,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strokeColor = when (insight.priceTrend) {
        PriceTrend.UP -> appColors.expense
        PriceTrend.DOWN -> appColors.income
        PriceTrend.STABLE -> appColors.accent
    }
    val trendArrow = when (insight.priceTrend) {
        PriceTrend.UP -> "↗"
        PriceTrend.DOWN -> "↘"
        PriceTrend.STABLE -> "➔"
    }

    val p1 = insight.oneMonthAgoUnitPrice ?: insight.previousUnitPrice ?: insight.minUnitPrice
    val p2 = insight.previousUnitPrice ?: ((insight.minUnitPrice + insight.maxUnitPrice) / 2.0)
    val p3 = insight.currentUnitPrice

    val points = listOf(p1, p2, p3)
    val minP = points.minOrNull() ?: 0.0
    val maxP = points.maxOrNull() ?: 1.0
    val span = if (maxP > minP) (maxP - minP) else 1.0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Canvas(modifier = modifier) {
            val width = size.width
            val height = size.height
            val padY = 3.dp.toPx()
            val usableHeight = (height - (padY * 2)).coerceAtLeast(1f)

            val isFlat = maxP <= minP
            val midY = height / 2f

            val x0 = 2.dp.toPx()
            val y0 = if (isFlat) midY else height - padY - (((p1 - minP) / span) * usableHeight).toFloat()

            val x1 = width / 2f
            val y1 = if (isFlat) midY else height - padY - (((p2 - minP) / span) * usableHeight).toFloat()

            val x2 = width - 4.dp.toPx()
            val y2 = if (isFlat) midY else height - padY - (((p3 - minP) / span) * usableHeight).toFloat()

            val path = Path().apply {
                moveTo(x0, y0)
                quadraticTo(x1, y1, x2, y2)
            }

            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            drawCircle(
                color = strokeColor,
                radius = 2.5.dp.toPx(),
                center = Offset(x2, y2)
            )
        }

        Text(
            text = trendArrow,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp
            ),
            color = strokeColor
        )
    }
}
