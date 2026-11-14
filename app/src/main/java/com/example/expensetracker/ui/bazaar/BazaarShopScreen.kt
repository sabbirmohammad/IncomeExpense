package com.example.expensetracker.ui.bazaar

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketCompletionResult
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.adaptive.LocalWindowClass
import com.example.expensetracker.ui.components.ProgressBarThreshold
import com.example.expensetracker.ui.components.PulseTabularText
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.classifyStallWithOverride
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BazaarShopScreen(
    listId: Long,
    bazaarShopViewModel: BazaarShopViewModel,
    onNavigateBack: () -> Unit,
    onTripCompleted: (MarketCompletionResult) -> Unit,
    modifier: Modifier = Modifier,
    wakeLockManager: ScreenWakeLockManager = AndroidScreenWakeLockManager
) {
    val windowClass = LocalWindowClass.current
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Keep Screen Awake via ScreenWakeLockManager (Amendment 8)
    val activity = remember(context) { AndroidScreenWakeLockManager.findActivity(context) }
    DisposableEffect(activity, wakeLockManager) {
        if (activity != null) {
            wakeLockManager.acquire(activity)
        }
        onDispose {
            if (activity != null) {
                wakeLockManager.release(activity)
            }
        }
    }

    // 2. Data Subscriptions
    val allLists by bazaarShopViewModel.allMarketLists.collectAsStateWithLifecycle()
    val isMarketListsLoaded by bazaarShopViewModel.isMarketListsLoaded.collectAsStateWithLifecycle()
    val lastPurchasedRates by bazaarShopViewModel.lastPurchasedRates.collectAsStateWithLifecycle()
    val productPriceInsights by bazaarShopViewModel.productPriceInsights.collectAsStateWithLifecycle()
    val masterProductsMap by bazaarShopViewModel.masterProductsMap.collectAsStateWithLifecycle()
    val stapleProducts by bazaarShopViewModel.stapleProducts.collectAsStateWithLifecycle()
    val isPrivacyMode by bazaarShopViewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val userProfile by bazaarShopViewModel.userProfile.collectAsStateWithLifecycle()
    val accounts by bazaarShopViewModel.accounts.collectAsStateWithLifecycle(emptyList())

    val skippedItemIds by bazaarShopViewModel.skippedItemIds.collectAsStateWithLifecycle()
    val isAutoAdvanceEnabled by bazaarShopViewModel.isAutoAdvanceEnabled.collectAsStateWithLifecycle()
    val itemPricingMode by bazaarShopViewModel.itemPricingMode.collectAsStateWithLifecycle()
    val pendingHighlightItemId by bazaarShopViewModel.pendingHighlightItemId.collectAsStateWithLifecycle()
    val activePricePadItem by bazaarShopViewModel.activePricePadItem.collectAsStateWithLifecycle()
    val isReceiptExpanded by bazaarShopViewModel.isReceiptExpanded.collectAsStateWithLifecycle()

    val isBangla = userProfile.appLanguage != "en"
    val useBengaliNumerals = isBangla

    // Current Trip Lookup
    val currentTrip = allLists.find { it.list.id == listId }

    // Fallback: If list does not exist after loading finishes, safely pop back
    LaunchedEffect(isMarketListsLoaded, currentTrip) {
        if (isMarketListsLoaded && currentTrip == null) {
            onNavigateBack()
        }
    }

    val items = currentTrip?.items ?: emptyList()
    val budgetCap = remember(listId, currentTrip) {
        bazaarShopViewModel.getBudgetCap(listId)
    }

    // Intercept back key to save trip progress and pop back
    BackHandler {
        onNavigateBack()
    }

    // 3. Stall Definitions & Grouping
    val standardStallKeys = listOf(
        "🥬 শাকসবজি",
        "🐟 মাছ ও মাংস",
        "🥛 দুধ ও ডেয়ারি",
        "🧂 মুদি সদাই"
    )

    // Filter items into active (unskipped) and skipped
    val activeItems = items.filter { it.id !in skippedItemIds }
    val skippedItems = items.filter { it.id in skippedItemIds }

    val itemsByStall = remember(activeItems, masterProductsMap) {
        activeItems.groupBy { item ->
            classifyStallWithOverride(item.name, masterProductsMap)
        }
    }

    // Progress Calculations
    val totalSpent = remember(items) {
        items.filter { it.isPurchased && it.amount != null }.sumOf { it.amount ?: 0.0 }
    }
    val purchasedCount = remember(items) {
        items.count { it.isPurchased }
    }
    val totalItemCount = items.size

    val budgetProgress = remember(totalSpent, budgetCap) {
        if (budgetCap != null && budgetCap > 0.0) {
            (totalSpent / budgetCap).toFloat()
        } else 0f
    }

    // 4. LazyList State & Scroll Auto-advance
    val listState = rememberLazyListState()

    // Map stall index to first visible item position
    val stallIndices = remember(itemsByStall) {
        val map = mutableMapOf<String, Int>()
        var runningIndex = 1 // 0 is top strip / forgot staples
        ALL_DEPARTMENTS.forEach { dept ->
            val stallList = itemsByStall[dept.key]
            if (!stallList.isNullOrEmpty()) {
                map[dept.key] = runningIndex
                runningIndex += 1 + stallList.size // 1 for header + items
            }
        }
        map
    }

    // Auto-advance scroll effect
    LaunchedEffect(pendingHighlightItemId) {
        val targetId = pendingHighlightItemId ?: return@LaunchedEffect
        val stall = activeItems.find { it.id == targetId }?.let { classifyStallWithOverride(it.name, masterProductsMap) }
        val stallStartIndex = stallIndices[stall]
        if (stallStartIndex != null) {
            val stallItems = itemsByStall[stall] ?: emptyList()
            val itemOffset = stallItems.indexOfFirst { it.id == targetId }
            if (itemOffset >= 0) {
                listState.animateScrollToItem((stallStartIndex + 1 + itemOffset).coerceAtLeast(0))
            }
        }
        delay(1200L)
        bazaarShopViewModel.clearHighlight()
    }

    // 5. Pre-checkout Dialog State (Amendment 11)
    var showCheckoutDialog by rememberSaveable { mutableStateOf(false) }
    var carryOverUnpurchased by rememberSaveable { mutableStateOf(true) }
    var selectedAccountId by rememberSaveable {
        mutableLongStateOf(accounts.firstOrNull()?.id ?: 0L)
    }

    // 6. Forgot Staples Strip State (Amendment 5)
    var isForgotStaplesDismissed by rememberSaveable { mutableStateOf(false) }

    val missingStaples = remember(purchasedCount, isForgotStaplesDismissed, stapleProducts, items, productPriceInsights) {
        if (purchasedCount < 3 || isForgotStaplesDismissed) {
            emptyList()
        } else {
            val currentNames = items.map { it.name.trim().lowercase() }.toSet()
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 3600 * 1000L)
            stapleProducts.filter { staple ->
                val norm = staple.normalizedName.lowercase().trim()
                val notInList = norm !in currentNames && staple.name.lowercase().trim() !in currentNames
                val insight = productPriceInsights[norm]
                val notRecentlyPurchased = insight == null || insight.lastPurchasedTimestamp < sevenDaysAgo
                notInList && notRecentlyPurchased
            }.take(6)
        }
    }

    // 7. Impromptu Add Item Dialog State
    var showAddImpromptuDialog by rememberSaveable { mutableStateOf(false) }
    var impromptuName by rememberSaveable { mutableStateOf("") }
    var impromptuQty by rememberSaveable { mutableStateOf("1") }
    var impromptuUnit by rememberSaveable { mutableStateOf("কেজি") }

    // Undo Snackbar Helper
    val onShowUndoSnackbar: (MarketItem, String, () -> Unit) -> Unit = { _, message, undoAction ->
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (isBangla) "বাতিল" else "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                undoAction()
            }
        }
    }

    // Layout configuration (Amendment 6)
    val isLandscapePhone = windowClass.heightClass.isCompact && !windowClass.isCompact
    val useTwoColumn = !windowClass.isCompact || isLandscapePhone
    val leftColumnWeight = if (windowClass.isExpanded) 0.65f else 0.60f
    val rightColumnWeight = if (windowClass.isExpanded) 0.35f else 0.40f

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            // Immersive Top Strip with Budget Progress (Amendment 1 & 7)
            Surface(
                color = appColors.surface,
                tonalElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isBangla) "বাজার থামিয়ে ফিরে যান" else "Pause trip and go back",
                                    tint = appColors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = currentTrip?.list?.title ?: (if (isBangla) "বাজারের ফর্দ" else "Market List"),
                                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = appColors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = appColors.accent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isBangla) "কেনাকাটা মোড চালু (স্ক্রিন সজাগ)" else "Shop Mode (Screen awake)",
                                        style = typography.bodySmall.copy(fontSize = 11.sp),
                                        color = appColors.textMuted
                                    )
                                }
                            }
                        }

                        // Auto-advance toggle (Amendment 9)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { bazaarShopViewModel.setAutoAdvanceEnabled(!isAutoAdvanceEnabled) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = if (isBangla) "অটো-স্ক্রল টগল" else "Toggle auto advance",
                                    tint = if (isAutoAdvanceEnabled) appColors.accent else appColors.textMuted
                                )
                            }
                        }
                    }

                    // Budget Status & Progress Bar (Amendment 7)
                    Spacer(modifier = Modifier.height(4.dp))
                    val spentStr = if (totalSpent % 1.0 == 0.0) "${totalSpent.toInt()}" else "%.1f".format(totalSpent)
                    val spentDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(spentStr) else spentStr

                    if (budgetCap != null && budgetCap > 0.0) {
                        val capStr = if (budgetCap % 1.0 == 0.0) "${budgetCap.toInt()}" else "%.1f".format(budgetCap)
                        val capDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(capStr) else capStr
                        val pctRemaining = ((1f - budgetProgress) * 100).toInt()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PulseTabularText(
                                text = if (isPrivacyMode) "••••" else if (isBangla) "খরচ: ৳$spentDisplay / ৳$capDisplay" else "Spent: ৳$spentDisplay / ৳$capDisplay",
                                style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = appColors.textPrimary
                            )
                            val statusText = when {
                                budgetProgress >= 1f -> if (isBangla) "বাজেট শেষ!" else "Over budget!"
                                else -> if (isBangla) "$pctRemaining% বাকি" else "$pctRemaining% left"
                            }
                            Text(
                                text = statusText,
                                style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (budgetProgress >= 1f) appColors.expense else appColors.income
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        ProgressBarThreshold(progress = budgetProgress)
                    } else {
                        // No budget cap -> show only total spent text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PulseTabularText(
                                text = if (isPrivacyMode) "••••" else if (isBangla) "মোট খরচ: ৳$spentDisplay" else "Total spent: ৳$spentDisplay",
                                style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = appColors.textPrimary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Cart Bar on Compact devices (Amendment 6)
            if (!useTwoColumn) {
                Surface(
                    color = appColors.surface,
                    tonalElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Expandable Receipt Panel
                        AnimatedVisibility(
                            visible = isReceiptExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            LiveReceiptPanelContent(
                                items = items,
                                totalSpent = totalSpent,
                                budgetCap = budgetCap,
                                isBangla = isBangla,
                                useBengaliNumerals = useBengaliNumerals,
                                isPrivacyMode = isPrivacyMode,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 260.dp)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // Collapsed Bar Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 64.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Tap to toggle expanded receipt
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(role = Role.Button, onClick = { bazaarShopViewModel.toggleReceiptExpanded() })
                            ) {
                                Icon(
                                    imageVector = if (isReceiptExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                    contentDescription = "Toggle receipt",
                                    tint = appColors.accent,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    val countDisplay = if (useBengaliNumerals || isBangla) {
                                        "${DigitConverter.toBengaliDigits(purchasedCount.toString())}/${DigitConverter.toBengaliDigits(totalItemCount.toString())}"
                                    } else "$purchasedCount/$totalItemCount"

                                    Text(
                                        text = if (isBangla) "পণ্য: $countDisplay" else "Items: $countDisplay",
                                        style = typography.bodySmall,
                                        color = appColors.textMuted
                                    )
                                    val spentStr2 = if (totalSpent % 1.0 == 0.0) "${totalSpent.toInt()}" else "%.1f".format(totalSpent)
                                    val spentDisp2 = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(spentStr2) else spentStr2
                                    PulseTabularText(
                                        text = if (isPrivacyMode) "••••" else "৳$spentDisp2",
                                        style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = appColors.accent
                                    )
                                }
                            }

                            // Finish Trip / Checkout button (48dp height)
                            Button(
                                onClick = { showCheckoutDialog = true },
                                modifier = Modifier.heightIn(min = 48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "চেকআউট" else "Finish Trip",
                                    style = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = appColors.onAccent
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (useTwoColumn) {
            // Two-column responsive layout for Medium / Expanded (Amendment 6)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Left Column: Stall Route & Item Checklist
                Box(
                    modifier = Modifier
                        .weight(leftColumnWeight)
                        .fillMaxHeight()
                ) {
                    ShopChecklistColumn(
                        listId = listId,
                        listState = listState,
                        itemsByStall = itemsByStall,
                        skippedItems = skippedItems,
                        allItems = items,
                        masterProductsMap = masterProductsMap,
                        skippedItemIds = skippedItemIds,
                        lastPurchasedRates = lastPurchasedRates,
                        pendingHighlightItemId = pendingHighlightItemId,
                        missingStaples = missingStaples,
                        isBangla = isBangla,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode,
                        bazaarShopViewModel = bazaarShopViewModel,
                        onShowUndoSnackbar = onShowUndoSnackbar,
                        onDismissForgotStaples = { isForgotStaplesDismissed = true },
                        onOpenImpromptuDialog = { showAddImpromptuDialog = true },
                        onScrollToStall = { stallKey ->
                            stallIndices[stallKey]?.let { idx ->
                                coroutineScope.launch { listState.animateScrollToItem(idx) }
                            }
                        }
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(appColors.border)
                )

                // Right Column: Live Receipt & Budget Panel
                Surface(
                    color = appColors.surface,
                    modifier = Modifier
                        .weight(rightColumnWeight)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        LiveReceiptPanelContent(
                            items = items,
                            totalSpent = totalSpent,
                            budgetCap = budgetCap,
                            isBangla = isBangla,
                            useBengaliNumerals = useBengaliNumerals,
                            isPrivacyMode = isPrivacyMode,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Finish Trip Button (48dp height)
                        Button(
                            onClick = { showCheckoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "কেনাকাটা সম্পন্ন করুন" else "Finish Shopping Trip",
                                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = appColors.onAccent
                            )
                        }
                    }
                }
            }
        } else {
            // Compact Single Column Layout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ShopChecklistColumn(
                    listId = listId,
                    listState = listState,
                    itemsByStall = itemsByStall,
                    skippedItems = skippedItems,
                    allItems = items,
                    masterProductsMap = masterProductsMap,
                    skippedItemIds = skippedItemIds,
                    lastPurchasedRates = lastPurchasedRates,
                    pendingHighlightItemId = pendingHighlightItemId,
                    missingStaples = missingStaples,
                    isBangla = isBangla,
                    useBengaliNumerals = useBengaliNumerals,
                    isPrivacyMode = isPrivacyMode,
                    bazaarShopViewModel = bazaarShopViewModel,
                    onShowUndoSnackbar = onShowUndoSnackbar,
                    onDismissForgotStaples = { isForgotStaplesDismissed = true },
                    onOpenImpromptuDialog = { showAddImpromptuDialog = true },
                    onScrollToStall = { stallKey ->
                        stallIndices[stallKey]?.let { idx ->
                            coroutineScope.launch { listState.animateScrollToItem(idx) }
                        }
                    }
                )
            }
        }
    }

    // 8. Active Price Pad Sheet Modal (AdaptiveSheet or Centered Dialog)
    activePricePadItem?.let { item ->
        val itemMode = itemPricingMode[item.id] ?: PricingMode.PER_UNIT
        val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
        val insight = productPriceInsights[item.name.lowercase().trim()]

        BazaarPricePadSheet(
            item = item,
            initialMode = itemMode,
            lastRate = lastRate,
            priceInsight = insight,
            onConfirm = { updatedItem, amount, mode ->
                bazaarShopViewModel.confirmPricePad(updatedItem, amount, mode)
            },
            onDismiss = { bazaarShopViewModel.closePricePad() },
            onMarkNotAvailable = { bazaarShopViewModel.markNotAvailable(item) },
            onRemoveItem = { bazaarShopViewModel.deleteItem(item) },
            onUndoPurchase = if (item.isPurchased) {
                {
                    bazaarShopViewModel.undoPurchase(item)
                    bazaarShopViewModel.closePricePad()
                }
            } else null,
            isBangla = isBangla,
            useBengaliNumerals = useBengaliNumerals,
            isPrivacyMode = isPrivacyMode
        )
    }

    // 9. Pre-checkout Confirmation Dialog (Amendment 11 & 14)
    if (showCheckoutDialog) {
        val unpurchasedItems = items.filter { !it.isPurchased || it.id in skippedItemIds }
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = {
                Text(
                    text = if (isBangla) "কেনাকাটা সম্পন্ন করবেন?" else "Finish shopping trip?",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (unpurchasedItems.isNotEmpty()) {
                        val unCount = unpurchasedItems.size
                        val unCountDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(unCount.toString()) else unCount.toString()
                        Text(
                            text = if (isBangla) "$unCountDisplay টি পণ্য কেনা হয়নি বা স্কিপ করা হয়েছে।" else "$unCountDisplay items were not bought or skipped.",
                            style = typography.bodyMedium,
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { carryOverUnpurchased = !carryOverUnpurchased }
                        ) {
                            Checkbox(
                                checked = carryOverUnpurchased,
                                onCheckedChange = { carryOverUnpurchased = it }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) "বাকি পণ্য দিয়ে নতুন ফর্দ তৈরি করুন" else "Carry over unbought items to a new list",
                                style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = appColors.textPrimary
                            )
                        }
                    } else {
                        Text(
                            text = if (isBangla) "সব পণ্য কেনা সম্পন্ন হয়েছে! খরচ সমন্বয় করবেন?" else "All items purchased! Ready to record trip?",
                            style = typography.bodyMedium,
                            color = appColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Account Selection
                    if (accounts.isNotEmpty()) {
                        Text(
                            text = if (isBangla) "যে অ্যাকাউন্ট থেকে খরচ হবে:" else "Deduct from account:",
                            style = typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            accounts.forEach { acc ->
                                val isSelected = acc.id == selectedAccountId
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) appColors.accent else appColors.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) appColors.accent else appColors.border),
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .clickable { selectedAccountId = acc.id }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = acc.name,
                                            style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) appColors.onAccent else appColors.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutDialog = false
                        bazaarShopViewModel.completeTripWithCarryOver(
                            listId = listId,
                            accountId = selectedAccountId.takeIf { it > 0L },
                            carryOverUnpurchased = carryOverUnpurchased,
                            onFinished = { result ->
                                onTripCompleted(result)
                            },
                            onCarryOverFailed = { snapshot ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (isBangla) "বাকি পণ্য ফর্দে নিতে সমস্যা হয়েছে" else "Failed to carry over unbought items"
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                ) {
                    Text(text = if (isBangla) "সম্পন্ন করুন" else "Finish", color = appColors.onAccent)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCheckoutDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            }
        )
    }

    // 10. Impromptu Add Item Dialog
    if (showAddImpromptuDialog) {
        AlertDialog(
            onDismissRequest = { showAddImpromptuDialog = false },
            title = {
                Text(
                    text = if (isBangla) "নতুন পণ্য যোগ করুন" else "Add Item to List",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = impromptuName,
                        onValueChange = { impromptuName = it },
                        label = { Text(if (isBangla) "পণ্যের নাম" else "Item name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = impromptuQty,
                            onValueChange = { impromptuQty = it },
                            label = { Text(if (isBangla) "পরিমাণ" else "Qty") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = impromptuUnit,
                            onValueChange = { impromptuUnit = it },
                            label = { Text(if (isBangla) "একক" else "Unit") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = impromptuQty.toDoubleOrNull() ?: 1.0
                        if (impromptuName.isNotBlank() && qty > 0.0) {
                            bazaarShopViewModel.addImpromptuItem(listId, impromptuName, qty, impromptuUnit)
                            impromptuName = ""
                            impromptuQty = "1"
                            showAddImpromptuDialog = false
                        }
                    },
                    modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                ) {
                    Text(text = if (isBangla) "যোগ করুন" else "Add", color = appColors.onAccent)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddImpromptuDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            }
        )
    }
}

/**
 * Main Shop Checklist Column containing:
 * - Sticky Stall-route chips
 * - Forgot-staples strip
 * - Stall-grouped items
 * - Impromptu add item button
 * - Skipped items section
 */
@Composable
private fun ShopChecklistColumn(
    listId: Long,
    listState: androidx.compose.foundation.lazy.LazyListState,
    itemsByStall: Map<String, List<MarketItem>>,
    skippedItems: List<MarketItem>,
    allItems: List<MarketItem>,
    masterProductsMap: Map<String, com.example.expensetracker.data.model.MasterProduct>,
    skippedItemIds: Set<Long>,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    pendingHighlightItemId: Long?,
    missingStaples: List<com.example.expensetracker.data.model.MasterProduct>,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    bazaarShopViewModel: BazaarShopViewModel,
    onShowUndoSnackbar: (MarketItem, String, () -> Unit) -> Unit,
    onDismissForgotStaples: () -> Unit,
    onOpenImpromptuDialog: () -> Unit,
    onScrollToStall: (String) -> Unit
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Sticky Stall-Route Chips (Amendment 2)
        item(key = "stall_route_chips") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ALL_DEPARTMENTS.forEach { dept ->
                    val progress = BazaarShopLogic.calculateStallProgress(dept.key, allItems, masterProductsMap, skippedItemIds)
                    val stallTitle = "${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}"
                    val doneDisp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(progress.doneCount.toString()) else progress.doneCount.toString()
                    val totDisp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(progress.totalCount.toString()) else progress.totalCount.toString()

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (progress.isAllDone) appColors.accent.copy(alpha = 0.15f) else appColors.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (progress.isAllDone) appColors.accent else appColors.border),
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .clickable(role = Role.Button, onClick = { onScrollToStall(dept.key) })
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stallTitle,
                                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (progress.isAllDone) appColors.accent else appColors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (progress.isAllDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed stall",
                                    tint = appColors.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "($doneDisp/$totDisp)",
                                    style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = appColors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Forgot-Staples Strip (Amendment 5)
        if (missingStaples.isNotEmpty()) {
            item(key = "forgot_staples_strip") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = appColors.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBangla) "ভুলে গেলেন কি? (সাধারণ সদাই)" else "Forgot something? (Staples)",
                                style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = appColors.accent
                            )
                            IconButton(
                                onClick = onDismissForgotStaples,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss staples", tint = appColors.textMuted)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            missingStaples.forEach { staple ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = appColors.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.accent.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .clickable {
                                            bazaarShopViewModel.addImpromptuItem(
                                                listId = listId,
                                                name = staple.name,
                                                qty = 1.0,
                                                unit = staple.defaultUnit.ifBlank { "কেজি" }
                                            )
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = staple.name,
                                            style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = appColors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = appColors.accent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Stall-Grouped Items (Amendment 1, 2 & 10)
        ALL_DEPARTMENTS.forEach { dept ->
            val stallItems = itemsByStall[dept.key]
            if (!stallItems.isNullOrEmpty()) {
                val stallTitle = "${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}"
                // Section Header
                item(key = "header_${dept.key}") {
                    val progress = BazaarShopLogic.calculateStallProgress(dept.key, allItems, masterProductsMap, skippedItemIds)
                    val doneDisp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(progress.doneCount.toString()) else progress.doneCount.toString()
                    val totDisp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(progress.totalCount.toString()) else progress.totalCount.toString()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stallTitle,
                            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = "$doneDisp/$totDisp",
                            style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = appColors.textMuted
                        )
                    }
                }

                // Items in this stall
                items(stallItems, key = { it.id }) { item ->
                    val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                    val isHighlighted = item.id == pendingHighlightItemId

                    BazaarShopItemCard(
                        item = item,
                        lastRate = lastRate,
                        isHighlighted = isHighlighted,
                        onCardClick = { bazaarShopViewModel.openPricePad(item) },
                        onCheckCircleClick = {
                            bazaarShopViewModel.swipeRightDone(item, onShowUndoSnackbar)
                        },
                        onSwipeRightDone = {
                            bazaarShopViewModel.swipeRightDone(item, onShowUndoSnackbar)
                        },
                        onSwipeLeftSkip = {
                            bazaarShopViewModel.swipeLeftSkip(item, onShowUndoSnackbar)
                        },
                        onEditPrice = { bazaarShopViewModel.openPricePad(item) },
                        onMarkNotAvailable = { bazaarShopViewModel.markNotAvailable(item) },
                        onRemoveItem = { bazaarShopViewModel.deleteItem(item) },
                        isBangla = isBangla,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode
                    )
                }
            }
        }

        // 4. Impromptu Add Item Trigger
        item(key = "add_impromptu_button") {
            OutlinedButton(
                onClick = onOpenImpromptuDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "হাতে হাতে নতুন পণ্য যোগ করুন" else "Add extra item on the fly",
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // 5. Missed / Skipped Section (Amendment 10)
        if (skippedItems.isNotEmpty()) {
            item(key = "skipped_items_section") {
                var isSkippedExpanded by rememberSaveable { mutableStateOf(false) }
                val skipCount = skippedItems.size
                val skipCountDisp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(skipCount.toString()) else skipCount.toString()

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = appColors.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSkippedExpanded = !isSkippedExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBangla) "বাদ দেওয়া আইটেম ($skipCountDisp)" else "Skipped Items ($skipCountDisp)",
                                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = appColors.textMuted
                            )
                            Icon(
                                imageVector = if (isSkippedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle skipped",
                                tint = appColors.textMuted
                            )
                        }

                        if (isSkippedExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            skippedItems.forEach { skippedItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = skippedItem.name,
                                            style = typography.bodyMedium.copy(
                                                textDecoration = TextDecoration.LineThrough
                                            ),
                                            color = appColors.textMuted
                                        )
                                        Text(
                                            text = "${skippedItem.quantity} ${skippedItem.unit}",
                                            style = typography.bodySmall,
                                            color = appColors.textMuted
                                        )
                                    }

                                    // Re-add button (48dp height)
                                    Button(
                                        onClick = { bazaarShopViewModel.undoSkip(skippedItem.id) },
                                        modifier = Modifier.heightIn(min = 48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = appColors.surfaceVariant)
                                    ) {
                                        Text(
                                            text = if (isBangla) "পুনরায় যোগ করুন" else "Re-add",
                                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = appColors.accent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom spacing for comfortable scrolling past floating cart bar
        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

/**
 * Live Receipt Panel Content used in:
 * - Expandable bottom sheet on Compact
 * - Permanent right column on Medium / Expanded
 */
@Composable
private fun LiveReceiptPanelContent(
    items: List<MarketItem>,
    totalSpent: Double,
    budgetCap: Double?,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    val purchasedItems = remember(items) { items.filter { it.isPurchased } }

    Column(modifier = modifier) {
        Text(
            text = if (isBangla) "ক্রয় বিবরণী (লাইভ রশিদ)" else "Live Trip Receipt",
            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = appColors.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (purchasedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "এখনও কোনো পণ্য কেনা হয়নি" else "No items marked as bought yet",
                    style = typography.bodySmall,
                    color = appColors.textMuted
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                purchasedItems.forEach { pItem ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${pItem.name} (${pItem.quantity} ${pItem.unit})",
                            style = typography.bodySmall,
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        val amt = pItem.amount ?: 0.0
                        val amtStr = if (amt % 1.0 == 0.0) "${amt.toInt()}" else "%.1f".format(amt)
                        val amtDisp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(amtStr) else amtStr
                        PulseTabularText(
                            text = if (isPrivacyMode) "••••" else "৳$amtDisp",
                            style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Total Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isBangla) "সর্বমোট:" else "Total:",
                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
            val spentStr = if (totalSpent % 1.0 == 0.0) "${totalSpent.toInt()}" else "%.1f".format(totalSpent)
            val spentDisp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(spentStr) else spentStr
            PulseTabularText(
                text = if (isPrivacyMode) "••••" else "৳$spentDisp",
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.accent
            )
        }
    }
}
