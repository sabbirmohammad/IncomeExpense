package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.MovementFilter
import com.example.expensetracker.data.model.PriceTier
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.data.model.buildMarketCommodities
import com.example.expensetracker.data.model.computeBazaarSentimentPulse
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.theme.WeightRegular
import com.example.expensetracker.theme.WeightSemiBold
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.MarketSearchBar
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Deterministic sorting order for commodity price intelligence.
 */
enum class PriceSortOrder(
    val titleBn: String,
    val titleEn: String,
    val iconEmoji: String
) {
    RECENT("সাম্প্রতিক", "Recent", "🕒"),
    HIGHEST_HIKE("সর্বোচ্চ বৃদ্ধি", "Highest Hike", "🔺"),
    BIGGEST_DROP("সর্বোচ্চ সাশ্রয়", "Biggest Drop", "🔻"),
    MOST_PURCHASED("সর্বাধিক কেনা", "Most Purchased", "🛍️")
}

/**
 * Department-level aggregate inflation index and metadata.
 */
data class DepartmentInflationKpi(
    val department: DepartmentMeta,
    val trackedItemsCount: Int,
    val itemsWithHistoryCount: Int,
    val averageInflationPercent: Double?,
    val trend: PriceTrend
)

/**
 * Computes aggregate inflation index across standard grocery departments.
 * Safely guards against empty records and division-by-zero.
 */
fun computeDepartmentInflationKpis(
    priceInsights: Collection<ProductPriceInsight>
): List<DepartmentInflationKpi> {
    val grouped = priceInsights.groupBy { getMarketItemCategory(it.productName) }
    return ALL_DEPARTMENTS.map { dept ->
        val deptInsights = grouped[dept.key] ?: emptyList()
        val withHistory = deptInsights.filter {
            it.previousUnitPrice != null && it.previousUnitPrice > 0.0 &&
            !it.percentDiff.isNaN() && !it.percentDiff.isInfinite()
        }
        val avgPct = if (withHistory.isNotEmpty()) withHistory.map { it.percentDiff }.average() else null
        val trend = when {
            avgPct == null -> PriceTrend.STABLE
            avgPct > 0.5 -> PriceTrend.UP
            avgPct < -0.5 -> PriceTrend.DOWN
            else -> PriceTrend.STABLE
        }
        DepartmentInflationKpi(
            department = dept,
            trackedItemsCount = deptInsights.size,
            itemsWithHistoryCount = withHistory.size,
            averageInflationPercent = avgPct,
            trend = trend
        )
    }
}

/**
 * Computes aggregate inflation index across standard grocery departments from commodity models.
 */
fun computeCommodityDepartmentInflationKpis(
    commodities: Collection<MarketCommodityUiModel>
): List<DepartmentInflationKpi> {
    val grouped = commodities.groupBy { it.category }
    return ALL_DEPARTMENTS.map { dept ->
        val deptCommodities = grouped[dept.key] ?: emptyList()
        val withHistory = deptCommodities.filter {
            !it.isBenchmarkOnly && it.previousPrice != null && it.previousPrice > 0.0 &&
            !it.percentDiff.isNaN() && !it.percentDiff.isInfinite()
        }
        val avgPct = if (withHistory.isNotEmpty()) withHistory.map { it.percentDiff }.average() else null
        val trend = when {
            avgPct == null -> PriceTrend.STABLE
            avgPct > 0.5 -> PriceTrend.UP
            avgPct < -0.5 -> PriceTrend.DOWN
            else -> PriceTrend.STABLE
        }
        DepartmentInflationKpi(
            department = dept,
            trackedItemsCount = deptCommodities.size,
            itemsWithHistoryCount = withHistory.size,
            averageInflationPercent = avgPct,
            trend = trend
        )
    }
}

/**
 * Formats department inflation percentage into localized text.
 */
fun formatInflationPct(pct: Double?, isBangla: Boolean): String {
    if (pct == null) return if (isBangla) "নতুন" else "New"
    val absVal = "%.1f".format(kotlin.math.abs(pct))
    val numStr = if (isBangla) convertToBengaliDigits(absVal) else absVal
    return when {
        pct > 0.05 -> "+$numStr%"
        pct < -0.05 -> "-$numStr%"
        else -> if (isBangla) "০.০%" else "0.0%"
    }
}

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
    masterProducts: List<MasterProduct> = emptyList(),
    onFetchHistory: (productName: String) -> List<ProductPriceRecord>,
    onAddCommodityToList: ((MarketCommodityUiModel) -> Unit)? = null,
    isBangla: Boolean = true,
    currencySymbol: String = "৳",
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = appColors.cardBackground,
        dragHandle = null
    ) {
        ProductPriceInsightsContent(
            priceInsights = priceInsights,
            masterProducts = masterProducts,
            onFetchHistory = onFetchHistory,
            onAddCommodityToList = onAddCommodityToList,
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
    masterProducts: List<MasterProduct> = emptyList(),
    onFetchHistory: (productName: String) -> List<ProductPriceRecord>,
    onAddCommodityToList: ((MarketCommodityUiModel) -> Unit)? = null,
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

    var selectedMovementFilter by remember { mutableStateOf(MovementFilter.ALL) }
    var selectedSortOrder by remember { mutableStateOf(PriceSortOrder.RECENT) }
    var selectedCommodityForDetail by remember { mutableStateOf<MarketCommodityUiModel?>(null) }

    val allCommodities = remember(masterProducts, priceInsights) {
        buildMarketCommodities(masterProducts, priceInsights)
    }

    val priceUpCount = remember(allCommodities) { allCommodities.count { it.isPriceUp } }
    val priceDownCount = remember(allCommodities) { allCommodities.count { it.isPriceDown } }

    val filteredCommodities = remember(
        allCommodities,
        effectiveSearchQuery,
        selectedMovementFilter,
        selectedSortOrder
    ) {
        allCommodities.filter { commodity ->
            val query = effectiveSearchQuery.trim().lowercase()
            val matchesQuery = query.isBlank() ||
                commodity.name.lowercase().contains(query) ||
                commodity.englishName.lowercase().contains(query) ||
                commodity.defaultUnit.lowercase().contains(query) ||
                commodity.category.lowercase().contains(query)

            val matchesMovement = when (selectedMovementFilter) {
                MovementFilter.ALL -> true
                MovementFilter.UP -> commodity.isPriceUp
                MovementFilter.DOWN -> commodity.isPriceDown
                MovementFilter.STABLE -> commodity.isPriceStable
            }
            matchesQuery && matchesMovement
        }.let { list ->
            when (selectedSortOrder) {
                PriceSortOrder.RECENT -> list.sortedWith(
                    compareByDescending<MarketCommodityUiModel> { it.lastPurchasedTimestamp }
                        .thenBy { it.name }
                )
                PriceSortOrder.HIGHEST_HIKE -> list.sortedWith(
                    compareByDescending<MarketCommodityUiModel> { it.percentDiff }
                        .thenByDescending { it.priceDiff }
                        .thenBy { it.name }
                )
                PriceSortOrder.BIGGEST_DROP -> list.sortedWith(
                    compareBy<MarketCommodityUiModel> { it.percentDiff }
                        .thenBy { it.priceDiff }
                        .thenBy { it.name }
                )
                PriceSortOrder.MOST_PURCHASED -> list.sortedWith(
                    compareByDescending<MarketCommodityUiModel> { it.purchaseCount }
                        .thenBy { it.name }
                )
            }
        }
    }

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
                        fontWeight = WeightBold,
                        fontSize = 16.sp
                    ),
                    color = appColors.textPrimary
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .background(appColors.cardBackground)
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
        }

        // Quick Trend KPI Filter Bar (All, Up, Down)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All Items Filter Chip
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedMovementFilter = MovementFilter.ALL
                },
                shape = RoundedCornerShape(10.dp),
                color = if (selectedMovementFilter == MovementFilter.ALL) appColors.accent.copy(alpha = 0.12f) else appColors.cardBackground,
                border = BorderStroke(
                    HairlineInkStroke,
                    if (selectedMovementFilter == MovementFilter.ALL) appColors.accent else appColors.divider
                ),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp)
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBangla) "সকল (${convertToBengaliDigits(allCommodities.size.toString())})"
                               else "All (${allCommodities.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (selectedMovementFilter == MovementFilter.ALL) WeightBold else WeightMedium,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = if (selectedMovementFilter == MovementFilter.ALL) appColors.accent else appColors.textSecondary
                    )
                }
            }

            // Price Up Filter Chip
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedMovementFilter = if (selectedMovementFilter == MovementFilter.UP) MovementFilter.ALL else MovementFilter.UP
                },
                shape = RoundedCornerShape(10.dp),
                color = appColors.expense.copy(alpha = if (selectedMovementFilter == MovementFilter.UP) 0.16f else 0.07f),
                border = BorderStroke(
                    HairlineInkStroke,
                    appColors.expense.copy(alpha = if (selectedMovementFilter == MovementFilter.UP) 0.8f else 0.35f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp)
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBangla) "বেড়েছে (${convertToBengaliDigits(priceUpCount.toString())})"
                               else "Hiked ($priceUpCount)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (selectedMovementFilter == MovementFilter.UP) WeightBold else WeightMedium,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = appColors.expense
                    )
                }
            }

            // Price Down Filter Chip
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedMovementFilter = if (selectedMovementFilter == MovementFilter.DOWN) MovementFilter.ALL else MovementFilter.DOWN
                },
                shape = RoundedCornerShape(10.dp),
                color = appColors.income.copy(alpha = if (selectedMovementFilter == MovementFilter.DOWN) 0.16f else 0.07f),
                border = BorderStroke(
                    HairlineInkStroke,
                    appColors.income.copy(alpha = if (selectedMovementFilter == MovementFilter.DOWN) 0.8f else 0.35f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp)
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBangla) "সাশ্রয়ী (${convertToBengaliDigits(priceDownCount.toString())})"
                               else "Dropped ($priceDownCount)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (selectedMovementFilter == MovementFilter.DOWN) WeightBold else WeightMedium,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = appColors.income
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Department & Sort Chips Row (Scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sort Order Chips
            PriceSortOrder.entries.forEach { sortOrder ->
                val isSelected = selectedSortOrder == sortOrder
                Surface(
                    onClick = {
                        haptics.lightTick()
                        selectedSortOrder = sortOrder
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) appColors.accent.copy(alpha = 0.14f) else appColors.cardBackground,
                    border = BorderStroke(HairlineInkStroke, if (isSelected) appColors.accent else appColors.divider),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = sortOrder.iconEmoji, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp))
                        Text(
                            text = if (isBangla) sortOrder.titleBn else sortOrder.titleEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) WeightBold else WeightRegular
                            ),
                            color = if (isSelected) appColors.accent else appColors.textSecondary
                        )
                    }
                }
            }
        }

        if (showInternalSearchBar) {
            Spacer(modifier = Modifier.height(8.dp))
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
                border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 44.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isBangla) "অনুসন্ধান: \"${effectiveSearchQuery.trim()}\" (${convertToBengaliDigits(filteredCommodities.size.toString())}টি পণ্য)"
                                   else "Search: \"${effectiveSearchQuery.trim()}\" (${filteredCommodities.size} items)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = WeightSemiBold,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = appColors.accent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .minimumInteractiveComponentSize()
                            .clickable { updateSearchQuery("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(appColors.accent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = appColors.accent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Products List (Fintech Stock Watchlist Rows)
        if (filteredCommodities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (allCommodities.isEmpty()) {
                            if (isBangla) "এখনো কোনো পণ্যের দর রেকর্ড হয়নি" else "No price records tracked yet"
                        } else if (effectiveSearchQuery.isNotBlank()) {
                            if (isBangla) "'${effectiveSearchQuery.trim()}' নামে কোনো পণ্য পাওয়া যায়নি"
                            else "No items found matching '${effectiveSearchQuery.trim()}'"
                        } else {
                            if (isBangla) "নির্বাচিত ফিল্টারে কোনো পণ্য পাওয়া যায়নি"
                            else "No items found matching filter"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = WeightSemiBold,
                            fontSize = 14.sp
                        ),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (allCommodities.isEmpty()) {
                            if (isBangla) "বাজারের ফর্দ সম্পন্ন করার পর এখানে পণ্যের দামের তুলনামূলক চিত্র প্রদর্শিত হবে।"
                            else "Complete your market trips to start tracking item price trends."
                        } else {
                            if (isBangla) "ফিল্টার পরিবর্তন করুন অথবা অন্য পণ্য অনুসন্ধান করুন।"
                            else "Try changing the filter or search for other products."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary.copy(alpha = 0.75f),
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
                items(filteredCommodities, key = { it.name }) { commodity ->
                    MarketCommodityStockRow(
                        commodity = commodity,
                        isBangla = isBangla,
                        currencySymbol = currencySymbol,
                        onAddToList = onAddCommodityToList?.let { addFn ->
                            { addFn(commodity) }
                        },
                        onClick = {
                            haptics.lightTick()
                            selectedCommodityForDetail = commodity
                        }
                    )
                }
            }
        }
    }

    // Detail Bottom Sheet when an item row is tapped
    if (selectedCommodityForDetail != null) {
        val commodity = selectedCommodityForDetail!!
        val syntheticInsight = commodity.rawInsight ?: ProductPriceInsight(
            productName = commodity.name,
            currentUnitPrice = commodity.currentPrice,
            previousUnitPrice = commodity.previousPrice,
            avgUnitPrice = commodity.benchmarkPrice,
            minUnitPrice = commodity.currentPrice,
            maxUnitPrice = commodity.currentPrice,
            priceDiff = commodity.priceDiff,
            percentDiff = commodity.percentDiff,
            priceTrend = commodity.priceTrend,
            purchaseCount = commodity.purchaseCount,
            lastPurchasedTimestamp = commodity.lastPurchasedTimestamp,
            baseUnit = commodity.defaultUnit,
            recentPricePoints = commodity.recentPricePoints
        )
        ProductPriceDetailSheet(
            insight = syntheticInsight,
            onFetchHistory = onFetchHistory,
            isBangla = isBangla,
            currencySymbol = currencySymbol,
            onDismiss = { selectedCommodityForDetail = null }
        )
    }
}

/**
 * High-density, professional Fintech Commodity Stock Row (R2).
 * Displays: Product name, English subtitle, benchmark/purchase count, current rate,
 * cubic sparkline, trend badge, and actionable "+ Add to List" quick button.
 */
@Composable
fun MarketCommodityStockRow(
    commodity: MarketCommodityUiModel,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onAddToList: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var isAddedFeedback by remember { mutableStateOf(false) }

    val currentRateFormatted = if (commodity.currentPrice % 1.0 == 0.0) {
        commodity.currentPrice.toInt().toString()
    } else {
        "%.1f".format(commodity.currentPrice)
    }
    val currentRateText = if (isBangla) convertToBengaliDigits(currentRateFormatted) else currentRateFormatted

    val subtitleText = if (commodity.isBenchmarkOnly || commodity.previousPrice == null) {
        if (isBangla) "বাজার রেফারেন্স রেট • ${commodity.englishName}"
        else "Market Reference Rate • ${commodity.englishName}"
    } else {
        val avgFormatted = if (commodity.benchmarkPrice % 1.0 == 0.0) commodity.benchmarkPrice.toInt().toString() else "%.1f".format(commodity.benchmarkPrice)
        val avgText = if (isBangla) convertToBengaliDigits(avgFormatted) else avgFormatted
        val countText = if (isBangla) convertToBengaliDigits(commodity.purchaseCount.toString()) else commodity.purchaseCount.toString()
        if (isBangla) "$countText বার কেনা • গড় $currencySymbol$avgText/${commodity.defaultUnit}"
        else "$countText logged • Avg $currencySymbol$avgText/${commodity.defaultUnit}"
    }

    val deptMeta = getDepartmentMeta(commodity.category)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Neutral Item Avatar (first letter on tinted circle per V7 C6)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(appColors.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                val initialLetter = commodity.name.trim().take(1).uppercase()
                Text(
                    text = initialLetter,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = WeightBold
                    ),
                    color = appColors.accent
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Middle Column: Product Name, English Subtitle & Meta
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = commodity.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = WeightBold,
                            fontSize = 15.sp
                        ),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (commodity.isBenchmarkOnly) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = appColors.accent.copy(alpha = 0.12f),
                            border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isBangla) "বাজার দর" else "Standard",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = WeightBold
                                ),
                                color = appColors.accent,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = WeightRegular,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right Column: Sparkline, Rate & Trend Pill, and Quick Add Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sparkline
                ProductPriceSparkline(
                    points = commodity.recentPricePoints,
                    trend = commodity.priceTrend,
                    modifier = Modifier
                        .width(36.dp)
                        .height(22.dp)
                )

                // Rate and Trend
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$currencySymbol$currentRateText",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = WeightBold,
                                fontSize = 14.5.sp,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = "/${commodity.defaultUnit}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = WeightMedium
                            ),
                            color = appColors.textSecondary,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }

                    // Trend badge
                    if (!commodity.isBenchmarkOnly && commodity.previousPrice != null) {
                        val isUp = commodity.isPriceUp
                        val isDown = commodity.isPriceDown
                        val pillColor = if (isUp) appColors.expense else if (isDown) appColors.income else appColors.textSecondary
                        val pillBg = if (isUp) appColors.expense.copy(alpha = 0.12f)
                                     else if (isDown) appColors.income.copy(alpha = 0.12f)
                                     else appColors.surfaceVariant

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = pillBg,
                            modifier = Modifier.defaultMinSize(minWidth = 52.dp)
                        ) {
                            Text(
                                text = commodity.formatDiff(isBangla),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = WeightBold,
                                    fontSize = 12.sp,
                                    fontFeatureSettings = TabularNumFeature
                                ),
                                color = pillColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = appColors.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.defaultMinSize(minWidth = 50.dp)
                        ) {
                            Text(
                                text = commodity.priceTier.titleBn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = WeightMedium,
                                    fontSize = 12.sp
                                ),
                                color = appColors.textSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Actionable Quick Add to List Button (F7 / R2)
                if (onAddToList != null) {
                    IconButton(
                        onClick = {
                            haptics.mediumClick()
                            onAddToList()
                            isAddedFeedback = true
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isAddedFeedback) appColors.income.copy(alpha = 0.15f)
                                else appColors.accent.copy(alpha = 0.12f)
                            )
                    ) {
                        Icon(
                            imageVector = if (isAddedFeedback) Icons.Filled.Check else Icons.Filled.Add,
                            contentDescription = "Add to Market List",
                            tint = if (isAddedFeedback) appColors.income else appColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * High-density, professional Fintech Stock Watchlist Row.
 * Displays: Product name, purchase count, average rate, current rate, multi-point cubic sparkline, and trend badge.
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
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .minimumInteractiveComponentSize()
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
                            fontWeight = WeightBold,
                            fontSize = 15.sp
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
                            border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isBangla) "নতুন" else "New",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = WeightBold
                                ),
                                color = appColors.accent,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (isBangla) "$purchaseCountText বার কেনা • গড় $currencySymbol$avgRateText/${insight.baseUnit}"
                           else "$purchaseCountText logged • Avg $currencySymbol$avgRateText/${insight.baseUnit}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = WeightRegular,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right Column: Sparkline, Price, and Stock Trend Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Multi-Point Cubic Bézier Sparkline inside stock row
                ProductPriceSparkline(
                    insight = insight,
                    modifier = Modifier
                        .width(42.dp)
                        .height(24.dp)
                )

                // Latest Price Column
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$currencySymbol$currentRateText",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = WeightBold,
                                fontSize = 15.sp,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = "/${insight.baseUnit}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = WeightMedium
                            ),
                            color = appColors.textSecondary,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }

                    val dateText = DateTimeHelper.formatDayGroup(insight.lastPurchasedTimestamp, isBangla)
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = WeightRegular,
                            fontFeatureSettings = TabularNumFeature
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
                    val safePct = if (insight.percentDiff.isNaN() || insight.percentDiff.isInfinite()) 0.0 else kotlin.math.abs(insight.percentDiff)
                    val pctAbs = "%.1f".format(safePct)
                    val pctText = if (isBangla) convertToBengaliDigits(pctAbs) else pctAbs
                    val sign = if (isUp) "+" else if (isDown) "-" else ""

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = pillBg,
                        border = BorderStroke(HairlineInkStroke, pillColor.copy(alpha = 0.3f)),
                        modifier = Modifier.defaultMinSize(minWidth = 64.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (iconVector != null) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = null,
                                    tint = pillColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = "$sign$diffText ($pctText%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = WeightBold,
                                    fontSize = 12.sp,
                                    fontFeatureSettings = TabularNumFeature
                                ),
                                color = pillColor
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(HairlineInkStroke, appColors.divider),
                        modifier = Modifier.defaultMinSize(minWidth = 56.dp)
                    ) {
                        Text(
                            text = if (isBangla) "১ম ক্রয়" else "1st buy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = WeightMedium,
                                fontSize = 12.sp
                            ),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
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
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = appColors.cardBackground,
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
                        .background(appColors.divider)
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
                            fontWeight = WeightBold,
                            fontSize = 19.sp
                        ),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBangla) "পরিমাপক একক: ${insight.baseUnit} • ${convertToBengaliDigits(insight.purchaseCount.toString())} বার ক্রয় রেকর্ড"
                               else "Base Unit: ${insight.baseUnit} • ${insight.purchaseCount} purchases logged",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = appColors.textSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .background(appColors.cardBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expanded Historical Sparkline
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "মূল্য পরিবর্তনের গতিপ্রকৃতি" else "Price Trajectory",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = WeightSemiBold,
                                fontSize = 12.sp
                            ),
                            color = appColors.textSecondary
                        )
                        Text(
                            text = if (isBangla) "সর্বশেষ ১০টি রেকর্ড" else "Last 10 records",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = appColors.textSecondary.copy(alpha = 0.7f)
                        )
                    }

                    ProductPriceSparkline(
                        insight = insight,
                        modifier = Modifier
                            .width(88.dp)
                            .height(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    color = appColors.cardBackground,
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
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
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = WeightSemiBold, fontSize = 12.sp),
                                color = appColors.textPrimary
                            )
                            insight.oneMonthPercentDiff?.let { mPct ->
                                val safeMPct = if (mPct.isNaN() || mPct.isInfinite()) 0.0 else kotlin.math.abs(mPct)
                                val mPctAbs = "%.1f".format(safeMPct)
                                val mPctText = if (isBangla) convertToBengaliDigits(mPctAbs) else mPctAbs
                                val mSign = if (mPct > 0) "+ " else "- "
                                val mColor = if (mPct > 0) appColors.expense else appColors.income
                                Text(
                                    text = if (isBangla) "৩০ দিনে: $mSign$mPctText%" else "30d: $mSign$mPctText%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = WeightBold,
                                        fontSize = 12.sp,
                                        fontFeatureSettings = TabularNumFeature
                                    ),
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
                                .background(appColors.divider.copy(alpha = 0.35f))
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
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "ক্রয়ের বিস্তারিত টাইমলাইন (${convertToBengaliDigits(history.size.toString())}টি)"
                           else "Purchase History Timeline (${history.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = WeightBold,
                        fontSize = 14.sp
                    ),
                    color = appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
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
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
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
                                            fontSize = 12.sp,
                                            fontWeight = WeightMedium
                                        ),
                                        color = appColors.textPrimary
                                    )
                                    Text(
                                        text = "$qtyText ${record.rawUnit} • মোট $currencySymbol$amtText",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontFeatureSettings = TabularNumFeature
                                        ),
                                        color = appColors.textSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (index == 0) appColors.accent.copy(alpha = 0.12f) else appColors.cardBackground,
                                    border = BorderStroke(HairlineInkStroke, if (index == 0) appColors.accent.copy(alpha = 0.35f) else appColors.divider)
                                ) {
                                    Text(
                                        text = "$currencySymbol$rateText/${record.baseUnit}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = WeightBold,
                                            fontSize = 12.sp,
                                            fontFeatureSettings = TabularNumFeature
                                        ),
                                        color = if (index == 0) appColors.accent else appColors.textPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = WeightBold,
                    fontSize = 15.sp,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = WeightSemiBold,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = subColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Multi-point vector sparkline curve (↗ ↘ ➔) representing recent commodity price dynamics.
 * Uses monotone Hermite cubic Bézier splines (cubicTo with midpoint control points),
 * vertical gradient area fill, endpoint dot, and zero-span flatness protection.
 * Supports N < 2, N = 2, and N > 2 price points without coordinate overshoot.
 */
@Composable
fun ProductPriceSparkline(
    points: List<Double>,
    trend: PriceTrend,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strokeColor = when (trend) {
        PriceTrend.UP -> appColors.expense
        PriceTrend.DOWN -> appColors.income
        PriceTrend.STABLE -> appColors.accent
    }
    val trendArrow = when (trend) {
        PriceTrend.UP -> "↗"
        PriceTrend.DOWN -> "↘"
        PriceTrend.STABLE -> "➔"
    }

    val rawPoints = when {
        points.size >= 2 -> points
        points.size == 1 -> listOf(points[0], points[0])
        else -> listOf(1.0, 1.0)
    }

    val minP = rawPoints.minOrNull() ?: 0.0
    val maxP = rawPoints.maxOrNull() ?: 1.0
    val isFlat = (maxP - minP) < 0.0001
    val span = if (isFlat) 1.0 else (maxP - minP)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(modifier = modifier) {
            val width = size.width
            val height = size.height
            val padX = 2.dp.toPx()
            val padY = 3.dp.toPx()
            val usableWidth = (width - (padX * 2)).coerceAtLeast(1f)
            val usableHeight = (height - (padY * 2)).coerceAtLeast(1f)

            val coords = rawPoints.mapIndexed { idx, p ->
                val x = padX + (idx.toFloat() / (rawPoints.size - 1)) * usableWidth
                val y = if (isFlat) height / 2f else height - padY - (((p - minP) / span) * usableHeight).toFloat()
                Offset(x, y)
            }

            // Monotone cubic Bézier path
            val strokePath = Path().apply {
                moveTo(coords[0].x, coords[0].y)
                for (i in 0 until coords.size - 1) {
                    val p0 = coords[i]
                    val p1 = coords[i + 1]
                    val midX = (p0.x + p1.x) / 2f
                    cubicTo(
                        x1 = midX, y1 = p0.y,
                        x2 = midX, y2 = p1.y,
                        x3 = p1.x, y3 = p1.y
                    )
                }
            }

            // Area Gradient Fill
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(coords.last().x, height)
                lineTo(coords.first().x, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        strokeColor.copy(alpha = 0.28f),
                        strokeColor.copy(alpha = 0.02f)
                    ),
                    startY = padY,
                    endY = height
                )
            )

            // Stroke
            drawPath(
                path = strokePath,
                color = strokeColor,
                style = Stroke(
                    width = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // Endpoint Dot
            drawCircle(
                color = strokeColor,
                radius = 2.2.dp.toPx(),
                center = coords.last()
            )
        }

        Text(
            text = trendArrow,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = WeightBold,
                fontSize = 12.sp,
                fontFeatureSettings = TabularNumFeature
            ),
            color = strokeColor
        )
    }
}

/**
 * Multi-Point Cubic Bézier Sparkline: renders smooth trends for product insights.
 */
@Composable
fun ProductPriceSparkline(
    insight: ProductPriceInsight,
    modifier: Modifier = Modifier
) {
    val rawPoints = when {
        insight.recentPricePoints.size >= 2 -> insight.recentPricePoints
        insight.recentPricePoints.size == 1 -> listOf(insight.recentPricePoints[0], insight.recentPricePoints[0])
        else -> {
            val p1 = insight.oneMonthAgoUnitPrice ?: insight.previousUnitPrice ?: insight.minUnitPrice
            val p2 = insight.previousUnitPrice ?: ((insight.minUnitPrice + insight.maxUnitPrice) / 2.0)
            val p3 = insight.currentUnitPrice
            listOf(p1, p2, p3)
        }
    }
    ProductPriceSparkline(points = rawPoints, trend = insight.priceTrend, modifier = modifier)
}
