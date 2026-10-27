package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
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
    var expandedProductName by remember { mutableStateOf<String?>(null) }

    val allInsights = remember(priceInsights) { priceInsights.values.toList() }

    val filteredInsights = remember(allInsights, effectiveSearchQuery, selectedFilter) {
        allInsights.filter { insight ->
            val matchesQuery = effectiveSearchQuery.isBlank() || insight.productName.contains(effectiveSearchQuery.trim().lowercase())
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
        // Clean Header (without AI emojis or redundant subtitles)
        if (showHeader) {
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

                if (onClose != null) {
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
                } else if (allInsights.isNotEmpty()) {
                    Text(
                        text = if (isBangla) "${convertToBengaliDigits(allInsights.size.toString())}টি পণ্য" else "${allInsights.size} items",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Quick Stats Banner with Interactive One-Tap Filtering
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Total Products Card (Taps reset filter to All)
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedFilter = null
                },
                shape = RoundedCornerShape(12.dp),
                color = if (selectedFilter == null) appColors.accent.copy(alpha = 0.10f) else appColors.surface,
                border = BorderStroke(
                    if (selectedFilter == null) 1.5.dp else 1.dp,
                    if (selectedFilter == null) appColors.accent else appColors.border
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isBangla) "ট্র্যাককৃত পণ্য" else "Tracked Items",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (selectedFilter == null) appColors.accent else appColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBangla) "${convertToBengaliDigits(allInsights.size.toString())}টি" else "${allInsights.size}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedFilter == null) appColors.accent else appColors.textPrimary
                    )
                }
            }

            // Price Up Card (Taps toggle Price Up filter)
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedFilter = if (selectedFilter == PriceTrend.UP) null else PriceTrend.UP
                },
                shape = RoundedCornerShape(12.dp),
                color = appColors.expense.copy(alpha = if (selectedFilter == PriceTrend.UP) 0.16f else 0.08f),
                border = BorderStroke(
                    if (selectedFilter == PriceTrend.UP) 1.5.dp else 1.dp,
                    appColors.expense.copy(alpha = if (selectedFilter == PriceTrend.UP) 0.6f else 0.25f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = appColors.expense,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = if (isBangla) "দাম বেড়েছে" else "Price Up",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = appColors.expense
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBangla) "${convertToBengaliDigits(priceUpCount.toString())}টি" else "$priceUpCount",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = appColors.expense
                    )
                }
            }

            // Price Down Card (Taps toggle Price Down filter)
            Surface(
                onClick = {
                    haptics.lightTick()
                    selectedFilter = if (selectedFilter == PriceTrend.DOWN) null else PriceTrend.DOWN
                },
                shape = RoundedCornerShape(12.dp),
                color = appColors.income.copy(alpha = if (selectedFilter == PriceTrend.DOWN) 0.16f else 0.08f),
                border = BorderStroke(
                    if (selectedFilter == PriceTrend.DOWN) 1.5.dp else 1.dp,
                    appColors.income.copy(alpha = if (selectedFilter == PriceTrend.DOWN) 0.6f else 0.25f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingDown,
                            contentDescription = null,
                            tint = appColors.income,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = if (isBangla) "সাশ্রয়ী হয়েছে" else "Price Down",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = appColors.income
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBangla) "${convertToBengaliDigits(priceDownCount.toString())}টি" else "$priceDownCount",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = appColors.income
                    )
                }
            }
        }

            if (showInternalSearchBar) {
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = effectiveSearchQuery,
                    onValueChange = updateSearchQuery,
                    placeholder = {
                        Text(
                            text = if (isBangla) "পণ্য খুঁজুন (যেমন: আলু, তেল, চাল)..." else "Search product (e.g. Potato, Oil)...",
                            fontSize = 13.sp,
                            color = appColors.textSecondary.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (effectiveSearchQuery.isNotBlank()) {
                            IconButton(onClick = { updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = appColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = appColors.border,
                        focusedContainerColor = appColors.surface,
                        unfocusedContainerColor = appColors.surface
                    )
                )
            } else if (effectiveSearchQuery.isNotBlank()) {
                // When search is handled in top bar, show active filter chip
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    onClick = {
                        onOpenSearch?.invoke()
                    },
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
                                imageVector = Icons.Default.Search,
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

                        IconButton(
                            onClick = { updateSearchQuery("") },
                            modifier = Modifier.size(22.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = {
                            haptics.lightTick()
                            selectedFilter = null
                        },
                        label = { Text(text = if (isBangla) "সব পণ্য" else "All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = appColors.accent,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == PriceTrend.UP,
                        onClick = {
                            haptics.lightTick()
                            selectedFilter = if (selectedFilter == PriceTrend.UP) null else PriceTrend.UP
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = { Text(text = if (isBangla) "দাম বেড়েছে" else "Price Up") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = appColors.expense,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == PriceTrend.DOWN,
                        onClick = {
                            haptics.lightTick()
                            selectedFilter = if (selectedFilter == PriceTrend.DOWN) null else PriceTrend.DOWN
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.TrendingDown,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = { Text(text = if (isBangla) "সাশ্রয়ী হয়েছে" else "Price Down") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = appColors.income,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == PriceTrend.STABLE,
                        onClick = {
                            haptics.lightTick()
                            selectedFilter = if (selectedFilter == PriceTrend.STABLE) null else PriceTrend.STABLE
                        },
                        label = { Text(text = if (isBangla) "অপরিবর্তিত" else "Stable") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = appColors.surfaceVariant,
                            selectedLabelColor = appColors.textPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Products List
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
                            } else {
                                if (isBangla) "'${effectiveSearchQuery.trim()}' নামে কোনো পণ্য পাওয়া যায়নি" else "No items found matching '${effectiveSearchQuery.trim()}'"
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 84.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredInsights, key = { it.productName }) { insight ->
                        val isExpanded = expandedProductName == insight.productName
                        ProductPriceItemCard(
                            insight = insight,
                            isExpanded = isExpanded,
                            isBangla = isBangla,
                            onToggleExpand = {
                                haptics.lightTick()
                                expandedProductName = if (isExpanded) null else insight.productName
                            },
                            onFetchHistory = onFetchHistory
                        )
                    }
                }
            }
        }
    }

@Composable
private fun ProductPriceItemCard(
    insight: ProductPriceInsight,
    isExpanded: Boolean,
    isBangla: Boolean,
    onToggleExpand: () -> Unit,
    onFetchHistory: (String) -> List<ProductPriceRecord>
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onToggleExpand() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Product Name & Category
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insight.productName.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBangla) "${convertToBengaliDigits(insight.purchaseCount.toString())} বার কেনা হয়েছে"
                               else "${insight.purchaseCount} purchases logged",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = appColors.textSecondary
                    )
                }

                // Right: Current Rate & Trend Pill
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = insight.formatUnitRate(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp
                        ),
                        color = appColors.accent
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (insight.previousUnitPrice != null) {
                        val isUp = insight.isPriceUp
                        val isDown = insight.isPriceDown
                        val pillBg = if (isUp) appColors.expense.copy(alpha = 0.12f)
                                     else if (isDown) appColors.income.copy(alpha = 0.12f)
                                     else appColors.surfaceVariant
                        val pillColor = if (isUp) appColors.expense else if (isDown) appColors.income else appColors.textSecondary
                        val trendIcon = if (isUp) "🔺 " else if (isDown) "🔻 " else "➖ "

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = pillBg
                        ) {
                            Text(
                                text = "$trendIcon${insight.formatDiff()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = pillColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Text(
                            text = if (isBangla) "প্রথম ক্রয়" else "First purchase",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = appColors.textSecondary
                        )
                    }
                }
            }

            // Secondary Stats Bar
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = appColors.background.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, appColors.border.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val prevRateStr = insight.previousUnitPrice?.let {
                        if (it % 1.0 == 0.0) it.toInt().toString() else "%.1f".format(it)
                    }
                    val prevRateText = if (prevRateStr != null) {
                        "৳${if (isBangla) convertToBengaliDigits(prevRateStr) else prevRateStr}/${insight.baseUnit}"
                    } else (if (isBangla) "নেই" else "N/A")

                    Text(
                        text = if (isBangla) "গতবারের দর: $prevRateText" else "Last Rate: $prevRateText",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = appColors.textSecondary
                    )

                    // 30-Day Inflation/Deflation indicator
                    insight.oneMonthPercentDiff?.let { mPct ->
                        val mPctAbs = "%.1f".format(kotlin.math.abs(mPct))
                        val mPctText = if (isBangla) convertToBengaliDigits(mPctAbs) else mPctAbs
                        val mSign = if (mPct > 0) "+ " else "- "
                        val mColor = if (mPct > 0) appColors.expense else appColors.income
                        Text(
                            text = if (isBangla) "১ মাসে: $mSign$mPctText%" else "30d: $mSign$mPctText%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                            color = mColor
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expanded Purchase History Timeline
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val history = remember(insight.productName) { onFetchHistory(insight.productName) }

                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = if (isBangla) "বিগত ক্রয়ের রেকর্ড ও দরদাম" else "Purchase History & Price Timeline",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp),
                            color = appColors.accent
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    history.forEachIndexed { index, record ->
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
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateFormatted,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = appColors.textSecondary
                                )
                                Text(
                                    text = "$qtyText ${record.rawUnit} • মোট ৳$amtText",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = appColors.textSecondary.copy(alpha = 0.8f)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (index == 0) appColors.accent.copy(alpha = 0.12f) else appColors.background,
                                border = BorderStroke(1.dp, appColors.border)
                            ) {
                                Text(
                                    text = "৳$rateText/${record.baseUnit}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = if (index == 0) appColors.accent else appColors.textPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
