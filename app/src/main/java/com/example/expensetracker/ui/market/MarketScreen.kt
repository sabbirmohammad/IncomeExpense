package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.ActiveMarketListsHubView
import com.example.expensetracker.ui.dashboard.CreateMarketListSection
import com.example.expensetracker.ui.dashboard.MarketAddNewItemRow
import com.example.expensetracker.ui.dashboard.MarketFinishButton
import com.example.expensetracker.ui.dashboard.MarketItemRow
import com.example.expensetracker.ui.dashboard.MarketPromptAmountDialog
import com.example.expensetracker.ui.dashboard.MarketSubstituteItemDialog
import com.example.expensetracker.ui.dashboard.MarketSummarySection
import com.example.expensetracker.ui.dashboard.ProductPriceInsightsContent
import com.example.expensetracker.ui.dashboard.DashboardViewModel

/**
 * Premium, dedicated full-screen Market Hub (বাজার).
 * Integrates:
 * 1. Active Shopping Checklists & Multi-List Hub (বাজারের ফর্দ)
 * 2. Commodity Price Intelligence & Inflation Tracker (বাজার দর ও দরদাম)
 * 3. Quick Bulk Natural-Language List Creator (নতুন ফর্দ তৈরি)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    val activeMarketLists by dashboardViewModel.activeMarketLists.collectAsStateWithLifecycle()
    val activeMarketList by dashboardViewModel.activeMarketList.collectAsStateWithLifecycle()
    val lastPurchasedRates by dashboardViewModel.lastPurchasedRates.collectAsStateWithLifecycle()
    val productPriceInsights by dashboardViewModel.productPriceInsights.collectAsStateWithLifecycle()
    val userProfile by dashboardViewModel.userProfile.collectAsStateWithLifecycle()

    val isBangla = strings.isBangla
    val currencySymbol = userProfile.currencySymbol

    // 0: Market Lists (Checklist & Trips), 1: Price Intelligence (বাজার দর), 2: Create New List
    var selectedTab by remember { mutableIntStateOf(0) }

    // Consolidated active lists
    val effectiveActiveLists = remember(activeMarketLists, activeMarketList) {
        if (activeMarketLists.isNotEmpty()) {
            activeMarketLists
        } else if (activeMarketList != null) {
            listOf(activeMarketList!!)
        } else {
            emptyList()
        }
    }

    // Selected list ID in Hub view (if single list, default to it)
    var selectedListId by remember(effectiveActiveLists.size) {
        mutableStateOf<Long?>(
            if (effectiveActiveLists.size == 1) effectiveActiveLists.first().list.id else null
        )
    }

    val currentViewingList = remember(effectiveActiveLists, selectedListId) {
        effectiveActiveLists.find { it.list.id == selectedListId }
            ?: (if (effectiveActiveLists.size == 1) effectiveActiveLists.first() else null)
    }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var itemToDeleteConfirm by remember { mutableStateOf<MarketItem?>(null) }
    var itemToSubstitute by remember { mutableStateOf<MarketItem?>(null) }
    var showForgotStaplesDialog by remember { mutableStateOf(false) }
    var listToDeleteConfirmId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
            .statusBarsPadding()
    ) {
        // 1. Top Bar Header
        MarketTopBar(
            isBangla = isBangla,
            activeListsCount = effectiveActiveLists.size,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onNavigateToSettings = onNavigateToSettings,
            onCreateNewClick = { selectedTab = 2 }
        )

        // 2. Segmented Pill Tabs
        MarketSegmentedPills(
            selectedTab = selectedTab,
            onTabSelected = {
                haptics.lightTick()
                selectedTab = it
            },
            isBangla = isBangla,
            activeListsCount = effectiveActiveLists.size,
            priceInsightsCount = productPriceInsights.size
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Tab Content
        when (selectedTab) {
            0 -> {
                // TAB 0: Active Market Lists & Shopping Checklist
                MarketListsTabContent(
                    effectiveActiveLists = effectiveActiveLists,
                    currentViewingList = currentViewingList,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    lastPurchasedRates = lastPurchasedRates,
                    productPriceInsights = productPriceInsights,
                    onSelectList = { listWithItems -> selectedListId = listWithItems.list.id },
                    onBackToAllLists = if (effectiveActiveLists.size > 1 && currentViewingList != null) {
                        { selectedListId = null }
                    } else null,
                    onCreateNewClick = { selectedTab = 2 },
                    onAddItem = { newItem, targetListId ->
                        dashboardViewModel.addMarketItem(newItem, targetListId)
                    },
                    onUpdateItem = { item -> dashboardViewModel.updateMarketItem(item) },
                    onDeleteItemClick = { item -> itemToDeleteConfirm = item },
                    onUnitClick = { item -> itemForUnitDialog = item },
                    onSubstituteClick = { item -> itemToSubstitute = item },
                    onPromptAmount = { item -> promptAmountForItem = item },
                    onUnmarkPurchased = { item -> dashboardViewModel.unmarkMarketItemPurchased(item) },
                    onFinishTripClick = {
                        val items = currentViewingList?.items ?: emptyList()
                        val purchased = items.filter { it.isPurchased }
                        val hasStaplePurchased = purchased.any {
                            val name = it.name.lowercase()
                            name.contains("মরিচ") || name.contains("লেবু") || name.contains("ধনে") ||
                            name.contains("আদা") || name.contains("রসুন") || name.contains("chili") || name.contains("lemon")
                        }
                        if (!hasStaplePurchased && items.size >= 3) {
                            showForgotStaplesDialog = true
                        } else if (currentViewingList != null) {
                            dashboardViewModel.completeActiveMarketList(currentViewingList.list.id)
                            selectedListId = null
                        }
                    },
                    onDeleteListClick = { listId -> listToDeleteConfirmId = listId }
                )
            }
            1 -> {
                // TAB 1: Market Price Intelligence & Trends
                ProductPriceInsightsContent(
                    priceInsights = productPriceInsights,
                    onFetchHistory = { productName -> dashboardViewModel.getProductPriceHistory(productName) },
                    isBangla = isBangla,
                    currencySymbol = currencySymbol,
                    onClose = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            2 -> {
                // TAB 2: Create New Market List (WhatsApp Paste or Manual)
                CreateMarketListSection(
                    isBangla = isBangla,
                    onListCreated = { title, items ->
                        dashboardViewModel.createMarketListWithBulk(title, items)
                        selectedTab = 0
                        selectedListId = null
                    }
                )
            }
        }
    }

    // Dialogs
    // 1. Unit & Quantity Dialog
    if (itemForUnitDialog != null) {
        val curItem = itemForUnitDialog!!
        UnitAndQuantityDialog(
            initialQuantity = curItem.quantity,
            initialUnit = curItem.unit,
            isBangla = isBangla,
            onDismiss = { itemForUnitDialog = null },
            onConfirm = { qty, unit ->
                dashboardViewModel.updateMarketItem(curItem.copy(quantity = qty, unit = unit))
                itemForUnitDialog = null
            }
        )
    }

    // 2. Amount Prompt Dialog when item purchased
    if (promptAmountForItem != null) {
        val targetItem = promptAmountForItem!!
        val lastRate = lastPurchasedRates[targetItem.name.lowercase().trim()]
        val targetCleanName = UnitConverterEngine.normalizeProductName(targetItem.name)
        val targetInsight = productPriceInsights[targetCleanName]
        MarketPromptAmountDialog(
            item = targetItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            lastRate = lastRate,
            priceInsight = targetInsight,
            onDismiss = { promptAmountForItem = null },
            onConfirm = { amt ->
                if (amt > 0) {
                    dashboardViewModel.markMarketItemPurchased(targetItem, amt)
                } else {
                    dashboardViewModel.markMarketItemPurchased(targetItem, 0.0)
                }
                promptAmountForItem = null
            }
        )
    }

    // 3. Item Substitute Dialog
    if (itemToSubstitute != null) {
        val curItem = itemToSubstitute!!
        MarketSubstituteItemDialog(
            item = curItem,
            isBangla = isBangla,
            onDismiss = { itemToSubstitute = null },
            onConfirmSubstitute = { newTitle ->
                dashboardViewModel.updateMarketItem(curItem.copy(name = newTitle))
                itemToSubstitute = null
            }
        )
    }

    // 4. "Forgot Staples?" Guard Dialog
    if (showForgotStaplesDialog && currentViewingList != null) {
        AlertDialog(
            onDismissRequest = { showForgotStaplesDialog = false },
            title = {
                Text(
                    text = if (isBangla) "কাঁচামরিচ বা লেবু কি নিয়েছেন?" else "Forgot Chili or Lemon?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "ফর্দে কোনো কাঁচামরিচ, ধনেপাতা বা লেবু কেনা চিহ্নিত নেই। আপনি কি এগুলো নিয়েছেন নাকি ফর্দ এখনই সম্পন্ন করবেন?"
                    else
                        "No chili, lemon, or coriander was marked as purchased. Did you already buy them or want to finish now?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotStaplesDialog = false
                        dashboardViewModel.completeActiveMarketList(currentViewingList.list.id)
                        selectedListId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.income),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isBangla) "হ্যাঁ, সম্পন্ন করুন" else "Yes, Finish Now",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotStaplesDialog = false }) {
                    Text(
                        text = if (isBangla) "না, ফর্দে যোগ করব" else "Add to List",
                        color = appColors.accent
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }

    // 5. Delete Item Confirmation Dialog
    if (itemToDeleteConfirm != null) {
        val itemToDelete = itemToDeleteConfirm!!
        AlertDialog(
            onDismissRequest = { itemToDeleteConfirm = null },
            title = {
                Text(
                    text = if (isBangla) "পণ্য মুছে ফেলতে চান?" else "Delete Item?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "'${itemToDelete.name}' (${itemToDelete.formatQuantity()}) পণ্যটি কি নিশ্চিতভাবে এই ফর্দ থেকে মুছে ফেলতে চান?"
                    else
                        "Are you sure you want to delete '${itemToDelete.name}' (${itemToDelete.formatQuantity()}) from this list?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dashboardViewModel.deleteMarketItem(itemToDelete.id)
                        itemToDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (isBangla) "মুছে ফেলুন" else "Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDeleteConfirm = null }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }

    // 6. Delete Entire List Confirmation Dialog
    if (listToDeleteConfirmId != null) {
        val listId = listToDeleteConfirmId!!
        val listToDelete = effectiveActiveLists.find { it.list.id == listId }
        val title = listToDelete?.list?.title ?: if (isBangla) "এই ফর্দটি" else "This list"
        AlertDialog(
            onDismissRequest = { listToDeleteConfirmId = null },
            title = {
                Text(
                    text = if (isBangla) "ফর্দ মুছে ফেলতে চান?" else "Delete Market List?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.expense
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "'$title' ফর্দটি মুছে ফেলা হলে এর ভেতরের সকল আইটেম ডিলিট হয়ে যাবে। আপনি কি নিশ্চিত?"
                    else
                        "Are you sure you want to delete '$title' and all items inside it?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dashboardViewModel.deleteActiveMarketList(listId)
                        if (selectedListId == listId) {
                            selectedListId = null
                        }
                        listToDeleteConfirmId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (isBangla) "মুছে ফেলুন" else "Delete List", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { listToDeleteConfirmId = null }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }
}

/**
 * Clean, modern One UI top bar for Market Screen.
 */
@Composable
private fun MarketTopBar(
    isBangla: Boolean,
    activeListsCount: Int,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToSettings: (() -> Unit)?,
    onCreateNewClick: () -> Unit
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(appColors.accent, Color(0xFF2563EB))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (isBangla) "বাজার হাব" else "Market Hub",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (activeListsCount > 0) {
                    if (isBangla) "${convertToBengaliDigits(activeListsCount.toString())}টি সক্রিয় ফর্দ চলমান" else "$activeListsCount active checklists"
                } else {
                    if (isBangla) "বাজারের ফর্দ ও নিত্যপণ্যের দরদাম" else "Shopping checklist & price trends"
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Quick Add List Button
            IconButton(
                onClick = onCreateNewClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(appColors.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = if (isBangla) "নতুন ফর্দ" else "New List",
                    tint = appColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (onNavigateToSettings != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(appColors.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = if (isBangla) "সেটিংস" else "Settings",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Segmented Pill Tab Bar for Market Hub.
 */
@Composable
private fun MarketSegmentedPills(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isBangla: Boolean,
    activeListsCount: Int,
    priceInsightsCount: Int
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tab 0: Market Lists
            MarketTabPill(
                title = if (isBangla) "বাজারের ফর্দ" else "Lists",
                badge = if (activeListsCount > 0) (if (isBangla) convertToBengaliDigits(activeListsCount.toString()) else activeListsCount.toString()) else null,
                icon = Icons.Outlined.Checklist,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )

            // Tab 1: Price Intelligence
            MarketTabPill(
                title = if (isBangla) "বাজার দর" else "Price Trends",
                badge = if (priceInsightsCount > 0) (if (isBangla) convertToBengaliDigits(priceInsightsCount.toString()) else priceInsightsCount.toString()) else null,
                icon = Icons.AutoMirrored.Outlined.TrendingUp,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )

            // Tab 2: Create List
            MarketTabPill(
                title = if (isBangla) "নতুন ফর্দ" else "New List",
                badge = null,
                icon = Icons.Filled.Add,
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(0.9f)
            )
        }
    }
}

@Composable
private fun MarketTabPill(
    title: String,
    badge: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) appColors.accent else Color.Transparent,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else appColors.textSecondary,
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = if (isSelected) Color.White else appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (badge != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else appColors.accent.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else appColors.accent
                    )
                }
            }
        }
    }
}

/**
 * Tab 0: Active Market Lists & Shopping Checklist View.
 */
@Composable
private fun MarketListsTabContent(
    effectiveActiveLists: List<MarketListWithItems>,
    currentViewingList: MarketListWithItems?,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    onSelectList: (MarketListWithItems) -> Unit,
    onBackToAllLists: (() -> Unit)?,
    onCreateNewClick: () -> Unit,
    onAddItem: (MarketItem, Long?) -> Unit,
    onUpdateItem: (MarketItem) -> Unit,
    onDeleteItemClick: (MarketItem) -> Unit,
    onUnitClick: (MarketItem) -> Unit,
    onSubstituteClick: (MarketItem) -> Unit,
    onPromptAmount: (MarketItem) -> Unit,
    onUnmarkPurchased: (MarketItem) -> Unit,
    onFinishTripClick: () -> Unit,
    onDeleteListClick: (Long) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    if (effectiveActiveLists.isEmpty()) {
        // Empty State: No active market checklists
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(appColors.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isBangla) "বর্তমানে কোনো সক্রিয় বাজার ফর্দ নেই" else "No active market checklist",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = appColors.textPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isBangla)
                        "সকালের কাঁচাবাজার, সাপ্তাহিক বা মাসিক মুদিবাজারের ফর্দ তৈরি করুন বা হোয়াটসঅ্যাপ টেক্সট পেস্ট করে নিমেষেই শুরু করুন।"
                    else
                        "Create morning, weekly, or monthly shopping lists or paste text from messaging apps to get started.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = appColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        haptics.mediumClick()
                        onCreateNewClick()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBangla) "নতুন বাজার ফর্দ তৈরি করুন" else "Create Shopping List",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else if (effectiveActiveLists.size > 1 && currentViewingList == null) {
        // Multi-List Directory View
        ActiveMarketListsHubView(
            activeLists = effectiveActiveLists,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onSelectList = onSelectList,
            onDeleteList = onDeleteListClick,
            onCreateNewClick = onCreateNewClick
        )
    } else if (currentViewingList != null) {
        // Single List Active Shopping Mode
        val items = currentViewingList.items
        val totalCount = items.size
        val purchasedCount = items.count { it.isPurchased }
        val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
        val totalEstimatedBudget = remember(items, productPriceInsights, lastPurchasedRates) {
            items.sumOf { itm ->
                if (itm.isPurchased) {
                    itm.amount ?: 0.0
                } else {
                    val cleanName = UnitConverterEngine.normalizeProductName(itm.name)
                    val insight = productPriceInsights[cleanName]
                    if (insight != null) {
                        UnitConverterEngine.estimateCost(insight.currentUnitPrice, itm.quantity, itm.unit)
                    } else {
                        val fallback = lastPurchasedRates[itm.name.lowercase().trim()]
                        if (fallback != null) {
                            UnitConverterEngine.estimateCost(fallback.first, itm.quantity, itm.unit)
                        } else 0.0
                    }
                }
            }
        }

        var filterMode by remember { mutableIntStateOf(0) } // 0: All, 1: Remaining, 2: Done

        val filteredItems = remember(items, filterMode) {
            when (filterMode) {
                1 -> items.filter { !it.isPurchased }
                2 -> items.filter { it.isPurchased }
                else -> items
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header strip if multiple lists exist (allows back or switching)
            if (effectiveActiveLists.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (onBackToAllLists != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = appColors.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onBackToAllLists() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back",
                                    tint = appColors.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBangla) "সকল ফর্দ (${convertToBengaliDigits(effectiveActiveLists.size.toString())})" else "All Lists (${effectiveActiveLists.size})",
                                    fontSize = 11.sp,
                                    color = appColors.textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Delete current list action
                    IconButton(
                        onClick = { onDeleteListClick(currentViewingList.list.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete List",
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Summary Section
            MarketSummarySection(
                totalCount = totalCount,
                purchasedCount = purchasedCount,
                totalSpent = totalSpent,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                estimatedBudget = if (totalEstimatedBudget > 0) totalEstimatedBudget else null
            )

            if (items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                // Filter Pills: All | Remaining | Done
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val remainingCount = totalCount - purchasedCount

                    // Filter 0: All
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (filterMode == 0) appColors.accent.copy(alpha = 0.15f) else Color.Transparent,
                        border = BorderStroke(1.dp, if (filterMode == 0) appColors.accent else appColors.border),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptics.lightTick()
                                filterMode = 0
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) "সব (${convertToBengaliDigits(totalCount.toString())})" else "All ($totalCount)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (filterMode == 0) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (filterMode == 0) appColors.accent else appColors.textSecondary
                            )
                        }
                    }

                    // Filter 1: Remaining
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (filterMode == 1) appColors.warning.copy(alpha = 0.15f) else Color.Transparent,
                        border = BorderStroke(1.dp, if (filterMode == 1) appColors.warning else appColors.border),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptics.lightTick()
                                filterMode = 1
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) "বাকি (${convertToBengaliDigits(remainingCount.toString())})" else "Left ($remainingCount)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (filterMode == 1) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (filterMode == 1) appColors.warning else appColors.textSecondary
                            )
                        }
                    }

                    // Filter 2: Purchased
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (filterMode == 2) appColors.income.copy(alpha = 0.15f) else Color.Transparent,
                        border = BorderStroke(1.dp, if (filterMode == 2) appColors.income else appColors.border),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptics.lightTick()
                                filterMode = 2
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) "কেনা হয়েছে (${convertToBengaliDigits(purchasedCount.toString())})" else "Done ($purchasedCount)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (filterMode == 2) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (filterMode == 2) appColors.income else appColors.textSecondary
                            )
                        }
                    }
                }
            }

            // Items List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filteredItems, key = { _, item -> item.id }) { _, item ->
                    val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                    val lastRateText = if (lastRate != null && lastRate.first > 0) {
                        val rateStr = if (lastRate.first % 1.0 == 0.0) lastRate.first.toInt().toString() else "%.1f".format(lastRate.first)
                        val rateBengali = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                        if (isBangla) "গত দর: ৳$rateBengali / ${lastRate.second}" else "Last: ৳$rateBengali / ${lastRate.second}"
                    } else null

                    MarketItemRow(
                        item = item,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla,
                        onUnitClick = { onUnitClick(item) },
                        onTickClick = {
                            if (!item.isPurchased) {
                                onPromptAmount(item)
                            } else {
                                onUnmarkPurchased(item)
                            }
                        },
                        onDeleteClick = { onDeleteItemClick(item) },
                        onSubstituteClick = { onSubstituteClick(item) },
                        lastRateText = lastRateText,
                        priceInsight = productPriceInsights[UnitConverterEngine.normalizeProductName(item.name)]
                    )
                }

                // Add item row at the end of the list
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    MarketAddNewItemRow(
                        listId = currentViewingList.list.id,
                        isBangla = isBangla,
                        onAddItem = { newItem ->
                            onAddItem(newItem, currentViewingList.list.id)
                        },
                        existingItemNames = items.map { it.name.lowercase() }.toSet(),
                        productPriceInsights = productPriceInsights
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Finish Trip Button inside scroll list
                if (items.isNotEmpty()) {
                    item {
                        MarketFinishButton(
                            isBangla = isBangla,
                            onComplete = onFinishTripClick
                        )
                        Spacer(modifier = Modifier.height(84.dp))
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(84.dp))
                    }
                }
            }
        }
    }
}
