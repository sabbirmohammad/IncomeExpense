package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.data.model.*
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.MarketSearchBar
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.market.MarketDuplicateMergeSheet
import com.example.expensetracker.ui.market.MarketItemDetailScreen
import com.example.expensetracker.ui.market.findDuplicateCommodities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPriceInsightsMainView(
    priceInsights: Map<String, ProductPriceInsight>,
    masterProducts: List<MasterProduct>,
    onFetchHistory: (String) -> List<ProductPriceRecord>,
    onAddCommodityToList: ((MarketCommodityUiModel) -> Unit)?,
    onRenameCommodity: ((String, String) -> Unit)?,
    onMergeCommodity: ((String, String) -> Unit)?,
    isBangla: Boolean,
    currencySymbol: String,
    onClose: (() -> Unit)?,
    searchQuery: String,
    onSearchQueryChange: ((String) -> Unit)?,
    showInternalSearchBar: Boolean,
    showHeader: Boolean,
    onOpenSearch: (() -> Unit)?,
    isSearchActive: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    var localSearchQuery by remember { mutableStateOf("") }
    val effectiveSearchQuery = if (onSearchQueryChange != null) searchQuery else localSearchQuery
    val updateSearchQuery: (String) -> Unit = { q ->
        if (onSearchQueryChange != null) onSearchQueryChange(q) else localSearchQuery = q
    }

    var selectedMovementFilter by remember { mutableStateOf(MovementFilter.ALL) }
    var selectedSortOrder by remember { mutableStateOf(PriceSortOrder.RECENT) }
    var selectedCommodityForDetail by remember { mutableStateOf<MarketCommodityUiModel?>(null) }
    var showDuplicateMergeSheet by remember { mutableStateOf(false) }
    var showDuplicateBanner by rememberSaveable { mutableStateOf(true) }

    val allCommodities = remember(masterProducts, priceInsights) { buildMarketCommodities(masterProducts, priceInsights) }
    val duplicateGroups = remember(allCommodities) { findDuplicateCommodities(allCommodities) }
    val priceUpCount = remember(allCommodities) { allCommodities.count { it.isPriceUp } }
    val priceDownCount = remember(allCommodities) { allCommodities.count { it.isPriceDown } }

    val filteredCommodities = remember(allCommodities, effectiveSearchQuery, selectedMovementFilter, selectedSortOrder) {
        allCommodities.filter { c ->
            val q = effectiveSearchQuery.trim().lowercase()
            val matchesQuery = q.isBlank() || c.name.lowercase().contains(q) || c.englishName.lowercase().contains(q) ||
                c.defaultUnit.lowercase().contains(q) || c.category.lowercase().contains(q)
            val matchesMovement = when (selectedMovementFilter) {
                MovementFilter.ALL -> true
                MovementFilter.UP -> c.isPriceUp
                MovementFilter.DOWN -> c.isPriceDown
                MovementFilter.STABLE -> c.isPriceStable
            }
            matchesQuery && matchesMovement
        }.let { list ->
            when (selectedSortOrder) {
                PriceSortOrder.RECENT -> list.sortedWith(compareByDescending<MarketCommodityUiModel> { it.lastPurchasedTimestamp }.thenBy { it.name })
                PriceSortOrder.HIGHEST_HIKE -> list.sortedWith(compareByDescending<MarketCommodityUiModel> { it.percentDiff }.thenByDescending { it.priceDiff }.thenBy { it.name })
                PriceSortOrder.BIGGEST_DROP -> list.sortedWith(compareBy<MarketCommodityUiModel> { it.percentDiff }.thenBy { it.priceDiff }.thenBy { it.name })
                PriceSortOrder.MOST_PURCHASED -> list.sortedWith(compareByDescending<MarketCommodityUiModel> { it.purchaseCount }.thenBy { it.name })
                PriceSortOrder.ALPHABETICAL -> list.sortedWith(compareBy { it.name.lowercase() })
            }
        }
    }

    val groupedCommodities = remember(filteredCommodities) { filteredCommodities.groupBy { it.category } }
    val collapsedDepartments = remember { mutableStateMapOf<String, Boolean>() }

    Column(modifier = modifier.fillMaxWidth().padding(top = if (onClose != null) 16.dp else 4.dp)) {
        if (showHeader && onClose != null) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (isBangla) "দ্রব্যমূল্যের দরদাম ও বিশ্লেষণ" else "Price Intelligence & Trends", style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold, fontSize = 16.sp), color = appColors.textPrimary)
                IconButton(onClick = onClose, modifier = Modifier.size(48.dp).minimumInteractiveComponentSize().clip(CircleShape).background(appColors.cardBackground)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = appColors.textSecondary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (showInternalSearchBar) {
            MarketSearchBar(query = effectiveSearchQuery, onQueryChange = updateSearchQuery, placeholder = if (isBangla) "পণ্য খুঁজুন (যেমন: আলু, তেল, চাল)..." else "Search product (e.g. Potato, Oil)...", modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), height = 40.dp, shape = RoundedCornerShape(12.dp), onClear = { updateSearchQuery("") })
            Spacer(modifier = Modifier.height(8.dp))
        } else if (effectiveSearchQuery.isNotBlank() && !isSearchActive) {
            ProductPriceSearchActiveBanner(effectiveSearchQuery = effectiveSearchQuery, itemsCount = filteredCommodities.size, isBangla = isBangla, onOpenSearch = onOpenSearch, onClearQuery = { updateSearchQuery("") })
            Spacer(modifier = Modifier.height(8.dp))
        }

        ProductPriceFilterControls(
            selectedMovementFilter = selectedMovementFilter, onMovementFilterChange = { selectedMovementFilter = it },
            selectedSortOrder = selectedSortOrder, onSortOrderChange = { selectedSortOrder = it },
            totalCommoditiesCount = allCommodities.size, priceUpCount = priceUpCount, priceDownCount = priceDownCount,
            isBangla = isBangla
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (duplicateGroups.isNotEmpty() && showDuplicateBanner) {
            ProductPriceDuplicateBanner(
                duplicateGroups = duplicateGroups, isBangla = isBangla,
                onReviewClick = { showDuplicateMergeSheet = true },
                onDismissClick = { showDuplicateBanner = false }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (filteredCommodities.isEmpty()) {
            ProductPriceEmptyState(isBangla = isBangla, isFilterActive = effectiveSearchQuery.isNotBlank() || selectedMovementFilter != MovementFilter.ALL)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                groupedCommodities.forEach { (category, deptCommodities) ->
                    val deptMeta = getDepartmentMeta(category)
                    val isCollapsed = collapsedDepartments[category] ?: false

                    item(key = "dept_header_$category") {
                        PriceDepartmentHeader(
                            department = deptMeta, count = deptCommodities.size, isExpanded = !isCollapsed,
                            onToggle = { haptics.lightTick(); collapsedDepartments[category] = !isCollapsed },
                            isBangla = isBangla
                        )
                    }

                    if (!isCollapsed) {
                        items(deptCommodities, key = { it.name }) { commodity ->
                            MarketCommodityStockRow(
                                commodity = commodity, isBangla = isBangla, currencySymbol = currencySymbol,
                                onAddToList = onAddCommodityToList?.let { addFn -> { addFn(commodity) } },
                                onClick = { haptics.lightTick(); selectedCommodityForDetail = commodity }
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedCommodityForDetail != null) {
        Dialog(
            onDismissRequest = { selectedCommodityForDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            MarketItemDetailScreen(
                commodity = selectedCommodityForDetail!!, allCommodities = allCommodities,
                onFetchHistory = onFetchHistory, isBangla = isBangla, currencySymbol = currencySymbol,
                onAddToList = { item -> onAddCommodityToList?.invoke(item) },
                onRenameCommodity = onRenameCommodity, onMergeCommodity = onMergeCommodity,
                onClose = { selectedCommodityForDetail = null }
            )
        }
    }

    if (showDuplicateMergeSheet) {
        MarketDuplicateMergeSheet(
            commodities = allCommodities, isBangla = isBangla, currencySymbol = currencySymbol,
            onMergeItems = { src, tgt -> onMergeCommodity?.invoke(src, tgt) },
            onDismiss = { showDuplicateMergeSheet = false }
        )
    }
}
