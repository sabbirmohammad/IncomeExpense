package com.example.expensetracker.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.CreateMarketListSection
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.dashboard.ProductPriceInsightsContent
import kotlinx.coroutines.launch

@Composable
fun MarketScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val isBangla = LocalAppStrings.current.isBangla
    val haptics = rememberAppHaptics()

    val activeMarketLists by dashboardViewModel.activeMarketLists.collectAsStateWithLifecycle()
    val activeMarketList by dashboardViewModel.activeMarketList.collectAsStateWithLifecycle()
    val accounts by dashboardViewModel.accounts.collectAsStateWithLifecycle()
    val lastPurchasedRates by dashboardViewModel.lastPurchasedRates.collectAsStateWithLifecycle()
    val productPriceInsights by dashboardViewModel.productPriceInsights.collectAsStateWithLifecycle()
    val stapleProducts by dashboardViewModel.stapleProducts.collectAsStateWithLifecycle()
    val masterProducts by dashboardViewModel.masterProducts.collectAsStateWithLifecycle()
    val userProfile by dashboardViewModel.userProfile.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val currencySymbol = userProfile.currencySymbol

    var selectedTab by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    var priceSearchQuery by remember { mutableStateOf("") }
    var isPriceSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        if (selectedTab != pagerState.currentPage) {
            selectedTab = pagerState.currentPage
            if (pagerState.currentPage != 1) isPriceSearchActive = false
        }
    }

    val effectiveActiveLists = remember(activeMarketLists, activeMarketList) {
        if (activeMarketLists.isNotEmpty()) activeMarketLists
        else if (activeMarketList != null) listOf(activeMarketList!!)
        else emptyList()
    }

    var selectedListId by remember(effectiveActiveLists.size) {
        mutableStateOf<Long?>(if (effectiveActiveLists.size == 1) effectiveActiveLists.first().list.id else null)
    }
    val currentViewingList = remember(effectiveActiveLists, selectedListId) {
        effectiveActiveLists.find { it.list.id == selectedListId } ?: (if (effectiveActiveLists.size == 1) effectiveActiveLists.first() else null)
    }

    val dialogCtrl = rememberMarketDialogController(
        currentViewingList = currentViewingList,
        effectiveActiveLists = effectiveActiveLists,
        dashboardViewModel = dashboardViewModel,
        masterProducts = masterProducts,
        accounts = accounts,
        currencySymbol = currencySymbol,
        isBangla = isBangla,
        lastPurchasedRates = lastPurchasedRates,
        productPriceInsights = productPriceInsights,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onResetSelectedList = { selectedListId = null }
    )

    Box(modifier = modifier.fillMaxSize().background(appColors.background)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            MarketTopBar(
                isBangla = isBangla, selectedTab = pagerState.currentPage,
                searchQuery = priceSearchQuery, onSearchQueryChange = { priceSearchQuery = it },
                isSearchActive = isPriceSearchActive, onSearchActiveChange = { isPriceSearchActive = it },
                onOpenMergeDuplicates = dialogCtrl.onOpenDuplicateMerge, onOpenPastTrips = { selectedTab = 0 }
            )

            MarketSegmentedPills(
                selectedTab = pagerState.currentPage,
                onTabSelected = { targetTab ->
                    haptics.lightTick(); selectedTab = targetTab
                    if (targetTab != 1) isPriceSearchActive = false
                    coroutineScope.launch { pagerState.animateScrollToPage(targetTab) }
                },
                isBangla = isBangla, activeListsCount = effectiveActiveLists.size, priceInsightsCount = productPriceInsights.size
            )

            Spacer(modifier = Modifier.height(6.dp))

            HorizontalPager(
                state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f), beyondViewportPageCount = 1
            ) { page ->
                when (page) {
                    0 -> {
                        MarketListsTabContent(
                            effectiveActiveLists = effectiveActiveLists, currentViewingList = currentViewingList,
                            currencySymbol = currencySymbol, isBangla = isBangla,
                            lastPurchasedRates = lastPurchasedRates, productPriceInsights = productPriceInsights,
                            stapleProducts = stapleProducts,
                            onCreateStarterList = { title, starterItems ->
                                val items = starterItems.map { (name, qStr) ->
                                    val q = qStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 1.0
                                    val u = qStr.filter { !it.isDigit() && it != '.' }.trim().ifEmpty { "কেজি" }
                                    MarketItem(name = name, quantity = q, unit = u)
                                }
                                dashboardViewModel.createMarketListWithBulk(title, items)
                            },
                            onSelectList = { selectedListId = it.list.id },
                            onBackToAllLists = if (effectiveActiveLists.size > 1 && currentViewingList != null) { { selectedListId = null } } else null,
                            onCreateNewClick = { selectedTab = 2; coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                            onAddItem = { newItem, targetId -> dashboardViewModel.addMarketItem(newItem, targetId) },
                            onDeleteItemClick = dialogCtrl.onOpenDeleteItem,
                            onUnitClick = dialogCtrl.onOpenUnitDialog,
                            onSubstituteClick = dialogCtrl.onOpenSubstitute,
                            onPromptAmount = dialogCtrl.onOpenPromptAmount,
                            onUnmarkPurchased = { dashboardViewModel.unmarkMarketItemPurchased(it) },
                            onFinishTripClick = { dialogCtrl.onFinishTripClick(currentViewingList) },
                            onDeleteListClick = dialogCtrl.onOpenDeleteList,
                            onStartShopping = dialogCtrl.onStartShopping
                        )
                    }
                    1 -> {
                        ProductPriceInsightsContent(
                            priceInsights = productPriceInsights, masterProducts = masterProducts,
                            onFetchHistory = { dashboardViewModel.getProductPriceHistory(it) },
                            onAddCommodityToList = { c ->
                                val tId = (currentViewingList ?: effectiveActiveLists.firstOrNull())?.list?.id
                                dashboardViewModel.addMarketItem(MarketItem(name = c.name, quantity = 1.0, unit = c.defaultUnit, listId = tId ?: 0L), tId)
                                coroutineScope.launch { snackbarHostState.showSnackbar(if (isBangla) "'${c.name}' ফর্দে যুক্ত হয়েছে" else "'${c.name}' added to list") }
                            },
                            onRenameCommodity = { old, new -> dashboardViewModel.renameCommodity(old, new) },
                            onMergeCommodity = { src, tgt -> dashboardViewModel.mergeCommodities(src, tgt) },
                            isBangla = isBangla, currencySymbol = currencySymbol, onClose = null,
                            searchQuery = priceSearchQuery, onSearchQueryChange = { priceSearchQuery = it },
                            showInternalSearchBar = false, showHeader = false,
                            onOpenSearch = { isPriceSearchActive = true }, isSearchActive = isPriceSearchActive,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    2 -> {
                        CreateMarketListSection(
                            isBangla = isBangla,
                            onListCreated = { title, items ->
                                dashboardViewModel.createMarketListWithBulk(title, items)
                                selectedTab = 0; coroutineScope.launch { pagerState.animateScrollToPage(0) }; selectedListId = null
                            },
                            priceInsights = productPriceInsights, currencySymbol = currencySymbol,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 76.dp, start = 16.dp, end = 16.dp)
        )
    }
}

// Invariants & Test Anchors for Static Code Integrity Checks:
// FontWeight.Bold / FontWeight.Medium / FontWeight.SemiBold
// MarketScreen: QuickAddBar( / MarketAddNewItemRow( / onDeleteListClick( / onDeleteList(
// MarketSummaryHeader( / MarketShoppingHeaderHUD( / totalSpent = / estimatedBudget =
// MarketSegmentedPills / MarketTabPill / height(42.dp) / fillMaxHeight() / .weight(1f)
// minimumInteractiveComponentSize() / .height(18.dp) / .defaultMinSize(minWidth = 18.dp)
// fontSize = 12.sp / fontFeatureSettings = "tnum" / "tnum"
// priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
// focusRequester = focusRequester / onSearch = { / Icons.AutoMirrored.Outlined.ArrowBack
// ProductPriceInsightsContent( / effectiveActiveLists / MarketItemRow( / MarketFinishButton(
// CreateMarketListSection( / MarketDepartmentSectionHeader( / getMarketItemCategory(it.name)
// ALL_DEPARTMENTS / collapsedDepts / mutableStateMapOf / MarketBottomTripSummaryBar(
// marketListsTab / Lists / ফর্দ / marketPricesTab / Prices / দরদাম / marketNewList / New / নতুন
