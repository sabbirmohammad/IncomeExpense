package com.example.expensetracker.ui.market

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import kotlinx.coroutines.launch

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * V7 Target Design D: Immersive Shop Mode.
 * - Full screen: global bottom bar HIDDEN.
 * - Top bar: ✕ Exit, list title, bought/total, search toggle.
 * - Screen kept awake via FLAG_KEEP_SCREEN_ON.
 * - 72dp rows with larger text, pending items first, bought items collapsed at bottom.
 * - Sticky bottom dock (above system nav): animated total spent count-up, count ratio,
 *   estimated budget comparison (turns warning color + text when Total > Estimated),
 *   and primary "Finish ✓" button.
 * - Tapping item row opens MarketPriceEntrySheet.
 * - Finishing opens MarketTripFinishSummarySheet with atomic completion.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketShopModeView(
    activeList: MarketListWithItems,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    onExitShopMode: () -> Unit,
    onUpdateItemAmount: (MarketItem, Double) -> Unit,
    onUpdateItemQuantityAndUnit: (MarketItem, Double, String) -> Unit,
    onQuickMarkPurchased: (MarketItem) -> Unit,
    onUnmarkPurchased: (MarketItem) -> Unit,
    onDeleteItem: (MarketItem) -> Unit,
    onAddItem: (MarketItem) -> Unit,
    onCompleteTrip: (selectedAccountId: Long?, rollOverItems: List<MarketItem>) -> Unit,
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Keep screen awake while active
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Intercept back button to safely exit Shop Mode
    BackHandler {
        onExitShopMode()
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    var isBoughtCollapsed by rememberSaveable { mutableStateOf(false) }
    var activePriceSheetItem by remember { mutableStateOf<MarketItem?>(null) }
    var showFinishSummarySheet by remember { mutableStateOf(false) }

    val allItems = activeList.items
    val totalCount = allItems.size
    val boughtItems = remember(allItems) { allItems.filter { it.isPurchased } }
    val unboughtItems = remember(allItems) { allItems.filter { !it.isPurchased } }
    val purchasedCount = boughtItems.size

    val totalSpent = remember(boughtItems) { boughtItems.sumOf { it.amount ?: 0.0 } }
    val estimatedBudget = remember(allItems, lastPurchasedRates, productPriceInsights) {
        allItems.sumOf { item ->
            val rate = lastPurchasedRates[item.name.lowercase().trim()]?.first
                ?: productPriceInsights[UnitConverterEngine.normalizeProductName(item.name)]?.currentUnitPrice
            if (rate != null && rate > 0.0 && item.quantity > 0.0) {
                UnitConverterEngine.estimateCost(rate, item.quantity, item.unit)
            } else 0.0
        }
    }

    val isOverspent = estimatedBudget > 0 && totalSpent > estimatedBudget
    val animatedTotalSpent by animateFloatAsState(
        targetValue = totalSpent.toFloat(),
        animationSpec = tween(300),
        label = "animatedTotalSpent"
    )

    // Filter items based on search query
    val filteredPending = remember(unboughtItems, searchQuery) {
        if (searchQuery.isBlank()) unboughtItems
        else unboughtItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }
    val filteredBought = remember(boughtItems, searchQuery) {
        if (searchQuery.isBlank()) boughtItems
        else boughtItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = appColors.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // 1. Top Bar: ✕ Exit, Title, Bought/Total, Search
                Surface(
                    color = appColors.cardBackground,
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // ✕ Exit Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        haptics.lightTick()
                                        onExitShopMode()
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = strings.marketExitShopMode,
                                        tint = appColors.textPrimary
                                    )
                                }

                                Text(
                                    text = activeList.list.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = appColors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Right controls: Bought/Total counter + Search toggle
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val pCountStr = if (isBangla) convertToBengaliDigits(purchasedCount.toString()) else purchasedCount.toString()
                                val tCountStr = if (isBangla) convertToBengaliDigits(totalCount.toString()) else totalCount.toString()

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = appColors.accentContainer.copy(alpha = 0.4f),
                                    border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "$pCountStr/$tCountStr",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = appColors.accent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        haptics.lightTick()
                                        isSearchActive = !isSearchActive
                                        if (!isSearchActive) searchQuery = ""
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = if (isSearchActive) Icons.Filled.Close else Icons.Outlined.Search,
                                        contentDescription = "Search",
                                        tint = if (isSearchActive) appColors.accent else appColors.textSecondary
                                    )
                                }
                            }
                        }

                        // Search Bar (Expandable)
                        AnimatedVisibility(
                            visible = isSearchActive,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = strings.search,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                        color = appColors.textMuted
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = appColors.textMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = appColors.surface,
                                    unfocusedContainerColor = appColors.surface,
                                    focusedBorderColor = appColors.accent,
                                    unfocusedBorderColor = appColors.divider,
                                    focusedTextColor = appColors.textPrimary,
                                    unfocusedTextColor = appColors.textPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .height(52.dp)
                            )
                        }
                    }
                }

                // 2. Scrollable Items (72dp Rows, Pending First, Bought Collapsed at Bottom)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Pending (Unbought) Items
                    itemsIndexed(filteredPending, key = { _, item -> item.id }) { _, item ->
                        val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                        val lastRateText = if (lastRate != null && lastRate.first > 0) {
                            val rateStr = if (lastRate.first % 1.0 == 0.0) lastRate.first.toInt().toString() else "%.1f".format(lastRate.first)
                            val rateBengali = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                            if (isBangla) "গতবার ৳$rateBengali/${lastRate.second}" else "Last ৳$rateBengali/${lastRate.second}"
                        } else null

                        MarketShopItemRow(
                            item = item,
                            currencySymbol = currencySymbol,
                            isBangla = isBangla,
                            lastRateText = lastRateText,
                            onRowClick = {
                                haptics.lightTick()
                                activePriceSheetItem = item
                            },
                            onTickClick = {
                                haptics.mediumClick()
                                onQuickMarkPurchased(item)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${item.name} ${strings.marketBought}",
                                        actionLabel = strings.undo,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onUnmarkPurchased(item)
                                    }
                                }
                            },
                            onDelete = {
                                haptics.mediumClick()
                                onDeleteItem(item)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${item.name} ${strings.delete}",
                                        actionLabel = strings.undo,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onAddItem(item)
                                    }
                                }
                            }
                        )
                    }

                    // Bought Items Collapsible Section
                    if (filteredBought.isNotEmpty()) {
                        item(key = "shop_bought_header") {
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    isBoughtCollapsed = !isBoughtCollapsed
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = appColors.cardBackground,
                                border = BorderStroke(HairlineInkStroke, appColors.divider),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val countDigits = if (isBangla) convertToBengaliDigits(filteredBought.size.toString()) else filteredBought.size.toString()
                                    Text(
                                        text = "${strings.marketBought} ($countDigits)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = appColors.income
                                    )

                                    val arrowRotation by animateFloatAsState(
                                        targetValue = if (isBoughtCollapsed) -90f else 0f,
                                        animationSpec = tween(220),
                                        label = "arrowRotation"
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = if (isBoughtCollapsed) "Expand" else "Collapse",
                                        tint = appColors.textSecondary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .rotate(arrowRotation)
                                    )
                                }
                            }
                        }

                        if (!isBoughtCollapsed) {
                            itemsIndexed(filteredBought, key = { _, item -> item.id }) { _, item ->
                                val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                                val lastRateText = if (lastRate != null && lastRate.first > 0) {
                                    val rateStr = if (lastRate.first % 1.0 == 0.0) lastRate.first.toInt().toString() else "%.1f".format(lastRate.first)
                                    val rateBengali = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                                    if (isBangla) "গতবার ৳$rateBengali/${lastRate.second}" else "Last ৳$rateBengali/${lastRate.second}"
                                } else null

                                MarketShopItemRow(
                                    item = item,
                                    currencySymbol = currencySymbol,
                                    isBangla = isBangla,
                                    lastRateText = lastRateText,
                                    onRowClick = {
                                        haptics.lightTick()
                                        activePriceSheetItem = item
                                    },
                                    onTickClick = {
                                        haptics.mediumClick()
                                        onUnmarkPurchased(item)
                                    },
                                    onDelete = {
                                        haptics.mediumClick()
                                        onDeleteItem(item)
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                // 3. Sticky Bottom Dock (Above System Navigation Bar)
                Surface(
                    color = if (isOverspent) appColors.warning.copy(alpha = 0.14f) else appColors.cardBackground,
                    border = BorderStroke(
                        HairlineInkStroke,
                        if (isOverspent) appColors.warning else appColors.divider
                    ),
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left Column: Total count-up + Bought ratio + Est comparison
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val totalSpentFormatted = formatCurrency(animatedTotalSpent.toDouble(), currencySymbol)
                            Text(
                                text = "${strings.total} $totalSpentFormatted",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = if (isOverspent) appColors.warning else appColors.textPrimary
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val pCount = if (isBangla) convertToBengaliDigits(purchasedCount.toString()) else purchasedCount.toString()
                                val tCount = if (isBangla) convertToBengaliDigits(totalCount.toString()) else totalCount.toString()
                                Text(
                                    text = "$pCount/$tCount ${strings.marketBought}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = appColors.textSecondary
                                )

                                if (isOverspent) {
                                    val overDiff = totalSpent - estimatedBudget
                                    val overStr = formatCurrency(overDiff, currencySymbol)
                                    Text(
                                        text = "• ⚠️ +$overStr",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = appColors.warning
                                    )
                                } else if (estimatedBudget > 0) {
                                    val estStr = formatCurrency(estimatedBudget, currencySymbol)
                                    Text(
                                        text = "• ${strings.marketEstimated}: $estStr",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = appColors.textMuted
                                    )
                                }
                            }
                        }

                        // Right Column: Primary "Finish ✓" Button
                        Button(
                            onClick = {
                                haptics.mediumClick()
                                showFinishSummarySheet = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = strings.marketFinish,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Snackbar Host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp)
            )
        }
    }

    // Interactive Price Bottom Sheet
    activePriceSheetItem?.let { currentItem ->
        val nextPendingItem = remember(allItems, currentItem.id) {
            allItems.firstOrNull { it.id != currentItem.id && !it.isPurchased }
        }

        MarketPriceEntrySheet(
            item = currentItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            hasNextPendingItem = nextPendingItem != null,
            onDismiss = {
                activePriceSheetItem = null
            },
            onSaveItem = { updatedQty, updatedUnit, totalAmount, shouldAdvanceToNext ->
                // Update quantity and unit if modified
                if (updatedQty != currentItem.quantity || updatedUnit != currentItem.unit) {
                    onUpdateItemQuantityAndUnit(currentItem, updatedQty, updatedUnit)
                }
                // Save purchased amount
                onUpdateItemAmount(currentItem, totalAmount)

                if (shouldAdvanceToNext && nextPendingItem != null) {
                    activePriceSheetItem = nextPendingItem
                } else {
                    activePriceSheetItem = null
                }
            },
            lastRate = lastPurchasedRates[currentItem.name.lowercase().trim()],
            priceInsight = UnitConverterEngine.findMatchingInsight(currentItem.name, productPriceInsights)
        )
    }

    // Finish Trip Summary Sheet
    if (showFinishSummarySheet) {
        MarketTripFinishSummarySheet(
            trip = activeList,
            accounts = accounts,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onDismiss = { showFinishSummarySheet = false },
            onConfirmComplete = { selectedAccountId, leftoverOption ->
                showFinishSummarySheet = false
                val rollOverItems = when (leftoverOption) {
                    LeftoverHandlingOption.MOVE_TO_NEXT -> unboughtItems
                    LeftoverHandlingOption.KEEP_IN_LIST -> emptyList()
                    LeftoverHandlingOption.DISCARD -> emptyList()
                }
                onCompleteTrip(selectedAccountId, rollOverItems)
                onExitShopMode()
            },
            productPriceInsights = productPriceInsights
        )
    }
}

/**
 * 72dp Row for Shop Mode per Target Design D.
 * Larger text, visual 28dp checkbox (>=48dp hit area), never ellipsized name,
 * muted last rate, price/estimate display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketShopItemRow(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onRowClick: () -> Unit,
    onTickClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    lastRateText: String? = null
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onTickClick()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> appColors.income.copy(alpha = 0.2f)
                SwipeToDismissBoxValue.EndToStart -> appColors.warning.copy(alpha = 0.2f)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = appColors.income)
                } else if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = appColors.warning)
                }
            }
        },
        modifier = modifier
    ) {
        Surface(
            onClick = onRowClick,
            shape = RoundedCornerShape(12.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Checkbox (28dp visual, 48dp touch target)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        onClick = onTickClick,
                        shape = CircleShape,
                        color = if (item.isPurchased) appColors.income else Color.Transparent,
                        border = BorderStroke(
                            2.dp,
                            if (item.isPurchased) appColors.income else appColors.divider
                        ),
                        modifier = Modifier
                            .size(28.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        if (item.isPurchased) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Bought",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Middle: Name (up to 2 lines, never ellipsized) + Qty chip + Last rate
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp,
                                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = if (item.isPurchased) appColors.textMuted else appColors.textPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Inline Quantity Chip
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = appColors.surface,
                                border = BorderStroke(HairlineInkStroke, appColors.divider)
                            ) {
                                Text(
                                    text = item.formatQuantity(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = if (item.isPurchased) appColors.textMuted else appColors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (lastRateText != null) {
                            Text(
                                text = lastRateText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }

                // Right: Amount Display (Paid or Estimate)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (item.isPurchased && item.amount != null && item.amount > 0) {
                        Text(
                            text = formatCurrency(item.amount, currencySymbol),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.income
                        )
                    } else {
                        Text(
                            text = if (isBangla) "দরদাম ✎" else "Price ✎",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "MarketShopModeView - Dark En", showBackground = true)
@Composable
fun PreviewMarketShopModeDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketShopModeView(
                    activeList = MarketListWithItems(
                        list = com.example.expensetracker.data.model.MarketList(
                            id = 1L,
                            title = "Morning Market",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "Potatoes", quantity = 2.0, unit = "kg", isPurchased = true, amount = 80.0),
                            MarketItem(id = 2L, listId = 1L, name = "Onions", quantity = 1.0, unit = "kg", isPurchased = false),
                            MarketItem(id = 3L, listId = 1L, name = "Eggs", quantity = 1.0, unit = "hali", isPurchased = false)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "Cash", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    onExitShopMode = {},
                    onUpdateItemAmount = { _, _ -> },
                    onUpdateItemQuantityAndUnit = { _, _, _ -> },
                    onQuickMarkPurchased = {},
                    onUnmarkPurchased = {},
                    onDeleteItem = {},
                    onAddItem = {},
                    onCompleteTrip = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketShopModeView - Light Bn", showBackground = true)
@Composable
fun PreviewMarketShopModeLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketShopModeView(
                    activeList = MarketListWithItems(
                        list = com.example.expensetracker.data.model.MarketList(
                            id = 1L,
                            title = "সকালের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 70.0),
                            MarketItem(id = 2L, listId = 1L, name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = false)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onExitShopMode = {},
                    onUpdateItemAmount = { _, _ -> },
                    onUpdateItemQuantityAndUnit = { _, _, _ -> },
                    onQuickMarkPurchased = {},
                    onUnmarkPurchased = {},
                    onDeleteItem = {},
                    onAddItem = {},
                    onCompleteTrip = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketShopModeView - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketShopModeAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketShopModeView(
                    activeList = MarketListWithItems(
                        list = com.example.expensetracker.data.model.MarketList(
                            id = 1L,
                            title = "মাছ-মাংসের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "রুই মাছ", quantity = 1.5, unit = "কেজি", isPurchased = true, amount = 500.0)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onExitShopMode = {},
                    onUpdateItemAmount = { _, _ -> },
                    onUpdateItemQuantityAndUnit = { _, _, _ -> },
                    onQuickMarkPurchased = {},
                    onUnmarkPurchased = {},
                    onDeleteItem = {},
                    onAddItem = {},
                    onCompleteTrip = { _, _ -> }
                )
            }
        }
    }
}
