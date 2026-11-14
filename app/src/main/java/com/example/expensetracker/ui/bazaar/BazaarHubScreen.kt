package com.example.expensetracker.ui.bazaar

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.MarketListWithItems
import androidx.compose.material3.MaterialTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.adaptive.LocalWindowClass
import com.example.expensetracker.ui.adaptive.WindowWidthClass
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PulseTabularText
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.ProductPriceInsightsSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun BazaarHubScreen(
    bazaarViewModel: BazaarViewModel,
    onOpenPlanMode: (Long) -> Unit,
    onResumeShopping: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val strings = LocalAppStrings.current
    val windowClass = LocalWindowClass.current
    val isBangla = strings.isBangla
    val haptics = rememberAppHaptics()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allLists by bazaarViewModel.allMarketLists.collectAsStateWithLifecycle()
    val activeLists by bazaarViewModel.activeMarketLists.collectAsStateWithLifecycle()
    val activeList by bazaarViewModel.activeMarketList.collectAsStateWithLifecycle()
    val completedTrips by bazaarViewModel.completedTrips.collectAsStateWithLifecycle()
    val lastPurchasedRates by bazaarViewModel.lastPurchasedRates.collectAsStateWithLifecycle()
    val productPriceInsights by bazaarViewModel.productPriceInsights.collectAsStateWithLifecycle()
    val masterProducts by bazaarViewModel.masterProducts.collectAsStateWithLifecycle()
    val masterProductsMap by bazaarViewModel.masterProductsMap.collectAsStateWithLifecycle()
    val isPrivacyMode by bazaarViewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val userProfile by bazaarViewModel.userProfile.collectAsStateWithLifecycle()
    val budgetCaps by bazaarViewModel.budgetCaps.collectAsStateWithLifecycle()
    val pendingDeletedListIds by bazaarViewModel.pendingDeletedListIds.collectAsStateWithLifecycle()

    val effectiveActiveLists = remember(activeLists, activeList, pendingDeletedListIds) {
        val raw = if (activeLists.isNotEmpty()) activeLists else if (activeList != null) listOf(activeList!!) else emptyList()
        raw.filter { it.list.id !in pendingDeletedListIds }
    }

    // Active in-progress trip (has active list and at least one item bought or trip underway)
    val inProgressTrip = remember(effectiveActiveLists) {
        effectiveActiveLists.firstOrNull { it.items.any { item -> item.isPurchased } }
            ?: effectiveActiveLists.firstOrNull()
    }

    // Smart suggestions
    val smartSuggestions = remember(completedTrips) {
        BazaarDomainLogic.computeSmartSuggestions(completedTrips)
    }

    // Dialog & Sheet States
    var showPasteSheet by remember { mutableStateOf(false) }
    var showRepeatTripPicker by remember { mutableStateOf(false) }
    var showPriceInsightsSheet by remember { mutableStateOf(false) }

    // Privacy Reveal state (3 seconds)
    var isPrivacyRevealed by remember { mutableStateOf(false) }
    LaunchedEffect(isPrivacyRevealed) {
        if (isPrivacyRevealed) {
            delay(3000L)
            isPrivacyRevealed = false
        }
    }

    val isExpanded = windowClass.widthClass.isExpanded

    Surface(
        color = appColors.background,
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isExpanded) {
                // Two Column Layout for Tablets / Expanded screens
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left Column: Hero + Action Tiles + My Lists
                    LazyColumn(
                        modifier = Modifier.weight(1.1f),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            BazaarHubHeroCard(
                                inProgressTrip = inProgressTrip,
                                budgetCap = inProgressTrip?.let { budgetCaps[it.list.id] },
                                onResume = { inProgressTrip?.let { onResumeShopping(it.list.id) } },
                                isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                                onRevealPrivacy = { isPrivacyRevealed = true },
                                isBangla = isBangla,
                                useBengaliNumerals = userProfile.useBengaliNumerals
                            )
                        }

                        item {
                            BazaarActionTilesRow(
                                onPasteClick = { showPasteSheet = true },
                                onBuildClick = {
                                    bazaarViewModel.createMarketList(
                                        title = if (isBangla) "বাজারের ফর্দ" else "Market List"
                                    ) { newListId ->
                                        onOpenPlanMode(newListId)
                                    }
                                },
                                onRepeatClick = { showRepeatTripPicker = true },
                                isBangla = isBangla
                            )
                        }

                        item {
                            BazaarMyListsSection(
                                lists = effectiveActiveLists,
                                lastPurchasedRates = lastPurchasedRates,
                                masterProductsMap = masterProductsMap,
                                onOpenList = onOpenPlanMode,
                                onNewList = {
                                    bazaarViewModel.createMarketList(
                                        title = if (isBangla) "নতুন ফর্দ" else "New List"
                                    ) { onOpenPlanMode(it) }
                                },
                                onDuplicateList = { bazaarViewModel.duplicateMarketList(it) },
                                onDeleteList = { listId, title ->
                                    bazaarViewModel.deleteMarketListWithUndo(listId, title, { msg, act ->
                                        val res = snackbarHostState.showSnackbar(msg, act)
                                        res == SnackbarResult.ActionPerformed
                                    })
                                },
                                isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                                onRevealPrivacy = { isPrivacyRevealed = true },
                                isBangla = isBangla,
                                useBengaliNumerals = userProfile.useBengaliNumerals
                            )
                        }
                    }

                    // Right Column: Price Book Search + Recent Trips + Smart Suggestions
                    LazyColumn(
                        modifier = Modifier.weight(0.9f),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            BazaarPriceBookSearchCard(
                                onClick = { showPriceInsightsSheet = true },
                                isBangla = isBangla
                            )
                        }

                        if (completedTrips.isNotEmpty()) {
                            item {
                                BazaarRecentTripsSection(
                                    recentTrips = completedTrips.take(3),
                                    onBuyAgain = { trip ->
                                        bazaarViewModel.cloneTrip(trip.list.id) { newListId ->
                                            onOpenPlanMode(newListId)
                                        }
                                    },
                                    isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                                    onRevealPrivacy = { isPrivacyRevealed = true },
                                    isBangla = isBangla,
                                    useBengaliNumerals = userProfile.useBengaliNumerals
                                )
                            }
                        }

                        if (smartSuggestions.isNotEmpty()) {
                            item {
                                BazaarSmartSuggestionsSection(
                                    suggestions = smartSuggestions,
                                    onSuggestionClick = { itemText ->
                                        val targetListId = effectiveActiveLists.firstOrNull()?.list?.id
                                        if (targetListId != null) {
                                            bazaarViewModel.addMarketItem(
                                                targetListId,
                                                com.example.expensetracker.data.model.MarketItem(
                                                    name = itemText,
                                                    quantity = 1.0,
                                                    unit = "কেজি"
                                                )
                                            )
                                        } else {
                                            bazaarViewModel.createMarketList(
                                                title = if (isBangla) "বাজারের ফর্দ" else "Market List",
                                                items = listOf(
                                                    com.example.expensetracker.data.model.MarketItem(
                                                        name = itemText,
                                                        quantity = 1.0,
                                                        unit = "কেজি"
                                                    )
                                                )
                                            ) { onOpenPlanMode(it) }
                                        }
                                    },
                                    isBangla = isBangla
                                )
                            }
                        }
                    }
                }
            } else {
                // Compact / Medium: Single Scrollable Column
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // 1. Hero Card
                    item {
                        BazaarHubHeroCard(
                            inProgressTrip = inProgressTrip,
                            budgetCap = inProgressTrip?.let { budgetCaps[it.list.id] },
                            onResume = { inProgressTrip?.let { onResumeShopping(it.list.id) } },
                            isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                            onRevealPrivacy = { isPrivacyRevealed = true },
                            isBangla = isBangla,
                            useBengaliNumerals = userProfile.useBengaliNumerals
                        )
                    }

                    // 2. Three Big Action Tiles
                    item {
                        BazaarActionTilesRow(
                            onPasteClick = { showPasteSheet = true },
                            onBuildClick = {
                                bazaarViewModel.createMarketList(
                                    title = if (isBangla) "বাজারের ফর্দ" else "Market List"
                                ) { newListId ->
                                    onOpenPlanMode(newListId)
                                }
                            },
                            onRepeatClick = { showRepeatTripPicker = true },
                            isBangla = isBangla
                        )
                    }

                    // 3. My Lists Carousel
                    item {
                        BazaarMyListsSection(
                            lists = effectiveActiveLists,
                            lastPurchasedRates = lastPurchasedRates,
                            masterProductsMap = masterProductsMap,
                            onOpenList = onOpenPlanMode,
                            onNewList = {
                                bazaarViewModel.createMarketList(
                                    title = if (isBangla) "নতুন ফর্দ" else "New List"
                                ) { onOpenPlanMode(it) }
                            },
                            onDuplicateList = { bazaarViewModel.duplicateMarketList(it) },
                            onDeleteList = { listId, title ->
                                bazaarViewModel.deleteMarketListWithUndo(listId, title, { msg, act ->
                                    val res = snackbarHostState.showSnackbar(msg, act)
                                    res == SnackbarResult.ActionPerformed
                                })
                            },
                            isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                            onRevealPrivacy = { isPrivacyRevealed = true },
                            isBangla = isBangla,
                            useBengaliNumerals = userProfile.useBengaliNumerals
                        )
                    }

                    // 4. Price Book Search Card
                    item {
                        BazaarPriceBookSearchCard(
                            onClick = { showPriceInsightsSheet = true },
                            isBangla = isBangla
                        )
                    }

                    // 5. Recent Trips (last 3)
                    if (completedTrips.isNotEmpty()) {
                        item {
                            BazaarRecentTripsSection(
                                recentTrips = completedTrips.take(3),
                                onBuyAgain = { trip ->
                                    bazaarViewModel.cloneTrip(trip.list.id) { newListId ->
                                        onOpenPlanMode(newListId)
                                    }
                                },
                                isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                                onRevealPrivacy = { isPrivacyRevealed = true },
                                isBangla = isBangla,
                                useBengaliNumerals = userProfile.useBengaliNumerals
                            )
                        }
                    }

                    // 6. Smart Suggestions
                    if (smartSuggestions.isNotEmpty()) {
                        item {
                            BazaarSmartSuggestionsSection(
                                suggestions = smartSuggestions,
                                onSuggestionClick = { itemText ->
                                    val targetListId = effectiveActiveLists.firstOrNull()?.list?.id
                                    if (targetListId != null) {
                                        bazaarViewModel.addMarketItem(
                                            targetListId,
                                            com.example.expensetracker.data.model.MarketItem(
                                                name = itemText,
                                                quantity = 1.0,
                                                unit = "কেজি"
                                            )
                                        )
                                    } else {
                                        bazaarViewModel.createMarketList(
                                            title = if (isBangla) "বাজারের ফর্দ" else "Market List",
                                            items = listOf(
                                                com.example.expensetracker.data.model.MarketItem(
                                                    name = itemText,
                                                    quantity = 1.0,
                                                    unit = "কেজি"
                                                )
                                            )
                                        ) { onOpenPlanMode(it) }
                                    }
                                },
                                isBangla = isBangla
                            )
                        }
                    }
                }
            }

            // Floating Snackbar Host
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 80.dp)
                )
            }
        }
    }

    // Paste List Sheet
    if (showPasteSheet) {
        BazaarPasteSheet(
            onDismiss = { showPasteSheet = false },
            onCreateList = { title, items ->
                bazaarViewModel.createMarketList(title, items) { newListId ->
                    onOpenPlanMode(newListId)
                }
            },
            masterProductsMap = masterProductsMap,
            isBangla = isBangla,
            useBengaliNumerals = userProfile.useBengaliNumerals
        )
    }

    // Repeat Past Trip Picker Sheet
    if (showRepeatTripPicker) {
        RepeatTripPickerSheet(
            pastTrips = completedTrips.take(10),
            onDismiss = { showRepeatTripPicker = false },
            onSelectTrip = { trip ->
                showRepeatTripPicker = false
                bazaarViewModel.cloneTrip(trip.list.id) { newListId ->
                    onOpenPlanMode(newListId)
                }
            },
            isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
            onRevealPrivacy = { isPrivacyRevealed = true },
            isBangla = isBangla,
            useBengaliNumerals = userProfile.useBengaliNumerals
        )
    }

    // Price Book Commodity Insights Sheet (reused existing composable, replaced in 3c)
    if (showPriceInsightsSheet) {
        ProductPriceInsightsSheet(
            priceInsights = productPriceInsights,
            onFetchHistory = { pName -> emptyList() },
            isBangla = isBangla,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showPriceInsightsSheet = false }
        )
    }
}

@Composable
fun BazaarHubHeroCard(
    inProgressTrip: MarketListWithItems?,
    budgetCap: Double?,
    onResume: () -> Unit,
    isPrivacyMode: Boolean,
    onRevealPrivacy: () -> Unit,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    // Leaf-green gradient stops: #065F46 -> #059669
    val heroGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF065F46), Color(0xFF059669))
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(heroGradient)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (inProgressTrip != null && inProgressTrip.items.any { it.isPurchased }) {
                // Trip in progress state
                val boughtCount = inProgressTrip.items.count { it.isPurchased }
                val totalCount = inProgressTrip.items.size
                val totalSpent = inProgressTrip.items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = inProgressTrip.list.title,
                            style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val progressStr = if (useBengaliNumerals || isBangla) {
                            "${DigitConverter.toBengaliDigits(boughtCount.toString())}/${DigitConverter.toBengaliDigits(totalCount.toString())} সম্পন্ন"
                        } else {
                            "$boughtCount/$totalCount done"
                        }
                        Text(
                            text = progressStr,
                            style = typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Resume shopping button
                    Button(
                        onClick = onResume,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF065F46)
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "কেনাকাটায় ফিরুন" else "Resume shopping",
                            style = typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Spent amount line
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onRevealPrivacy() }
                ) {
                    val spentNum = if (totalSpent % 1.0 == 0.0) "${totalSpent.toInt()}" else "%.1f".format(totalSpent)
                    val spentStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(spentNum) else spentNum

                    Text(
                        text = if (isBangla) "এখন পর্যন্ত খরচ: " else "Spent so far: ",
                        style = typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    PulseTabularText(
                        text = if (isPrivacyMode) "••••••" else "৳$spentStr",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    if (budgetCap != null && budgetCap > 0.0) {
                        val capNum = if (budgetCap % 1.0 == 0.0) "${budgetCap.toInt()}" else "%.1f".format(budgetCap)
                        val capStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(capNum) else capNum
                        Text(
                            text = " (বাজেট ৳$capStr)",
                            style = typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                // No active trip state
                Text(
                    text = if (isBangla) "আজ কি বাজার করবেন?" else "Going to the bazaar?",
                    style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBangla) "ফর্দ সাজান, দরদাম দেখুন ও স্বাচ্ছন্দ্যে বাজার সম্পন্ন করুন" else "Build your list, check prices & shop effortlessly",
                    style = typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun BazaarActionTilesRow(
    onPasteClick: () -> Unit,
    onBuildClick: () -> Unit,
    onRepeatClick: () -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. Paste List Tile
        BazaarActionTile(
            title = if (isBangla) "লিস্ট পেস্ট" else "Paste list",
            subtitle = if (isBangla) "WhatsApp বা মেসেজ" else "WhatsApp/SMS",
            icon = Icons.Default.ContentPaste,
            onClick = onPasteClick,
            modifier = Modifier.weight(1f)
        )

        // 2. Build List Tile
        BazaarActionTile(
            title = if (isBangla) "লিস্ট বানান" else "Build list",
            subtitle = if (isBangla) "ক্যাটালগ থেকে" else "From catalog",
            icon = Icons.Default.Edit,
            onClick = onBuildClick,
            modifier = Modifier.weight(1f)
        )

        // 3. Repeat Trip Tile
        BazaarActionTile(
            title = if (isBangla) "আগের বাজার" else "Repeat trip",
            subtitle = if (isBangla) "কপি করে শুরু" else "Clone past list",
            icon = Icons.Default.History,
            onClick = onRepeatClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BazaarActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = appColors.surface,
        tonalElevation = 2.dp,
        modifier = modifier
            .heightIn(min = 96.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = title,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = "$title, $subtitle"
            }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = typography.bodySmall.copy(fontSize = 11.sp),
                color = appColors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BazaarMyListsSection(
    lists: List<MarketListWithItems>,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    masterProductsMap: Map<String, com.example.expensetracker.data.model.MasterProduct>,
    onOpenList: (Long) -> Unit,
    onNewList: () -> Unit,
    onDuplicateList: (Long) -> Unit,
    onDeleteList: (Long, String) -> Unit,
    isPrivacyMode: Boolean,
    onRevealPrivacy: () -> Unit,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isBangla) "আমার ফর্দসমূহ" else "My lists",
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )

            Text(
                text = if (useBengaliNumerals || isBangla) "${DigitConverter.toBengaliDigits(lists.size.toString())}টি ফর্দ" else "${lists.size} lists",
                style = typography.bodySmall,
                color = appColors.textMuted
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(lists, key = { it.list.id }) { listWithItems ->
                var showCardMenu by remember { mutableStateOf(false) }

                val (estimateSummary, _) = remember(listWithItems.items) {
                    BazaarDomainLogic.calculateMarketListEstimate(
                        items = listWithItems.items,
                        lastPurchasedRates = lastPurchasedRates,
                        masterProducts = masterProductsMap
                    )
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = appColors.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .width(220.dp)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Open ${listWithItems.list.title}",
                            onClick = { onOpenList(listWithItems.list.id) }
                        )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = listWithItems.list.title,
                                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Box {
                                IconButton(
                                    onClick = { showCardMenu = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Options",
                                        tint = appColors.textMuted
                                    )
                                }

                                DropdownMenu(
                                    expanded = showCardMenu,
                                    onDismissRequest = { showCardMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(if (isBangla) "শেয়ার করুন" else "Share as text") },
                                        onClick = {
                                            showCardMenu = false
                                            val shareText = BazaarDomainLogic.formatListAsWhatsAppText(
                                                listTitle = listWithItems.list.title,
                                                items = listWithItems.items,
                                                masterProductsMap = masterProductsMap,
                                                isBangla = isBangla
                                            )
                                            if (shareText.isNotBlank()) {
                                                val intent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(intent, null))
                                            }
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text(if (isBangla) "ডুপ্লিকেট করুন" else "Duplicate") },
                                        onClick = {
                                            showCardMenu = false
                                            onDuplicateList(listWithItems.list.id)
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text(if (isBangla) "মুছুন" else "Delete", color = appColors.expense) },
                                        onClick = {
                                            showCardMenu = false
                                            onDeleteList(listWithItems.list.id, listWithItems.list.title)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val countStr = if (useBengaliNumerals || isBangla) {
                            "${DigitConverter.toBengaliDigits(listWithItems.items.size.toString())}টি পণ্য"
                        } else {
                            "${listWithItems.items.size} items"
                        }
                        Text(
                            text = countStr,
                            style = typography.bodySmall,
                            color = appColors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val estNum = if (estimateSummary.totalEstimatedPrice % 1.0 == 0.0) {
                            "${estimateSummary.totalEstimatedPrice.toInt()}"
                        } else {
                            "%.1f".format(estimateSummary.totalEstimatedPrice)
                        }
                        val estStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(estNum) else estNum

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onRevealPrivacy() }
                        ) {
                            Text(
                                text = "আনুমানিক: ",
                                style = typography.bodySmall,
                                color = appColors.textMuted
                            )
                            PulseTabularText(
                                text = if (isPrivacyMode) "••••••" else "৳$estStr",
                                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = appColors.textPrimary
                            )
                        }
                    }
                }
            }

            // End card: "＋ New list"
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = appColors.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                    modifier = Modifier
                        .width(140.dp)
                        .height(110.dp)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Create new list",
                            onClick = onNewList
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = appColors.accent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "নতুন ফর্দ" else "New list",
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = appColors.accent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BazaarPriceBookSearchCard(
    onClick: () -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = appColors.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClickLabel = "Open price book",
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = "Search item price in price book"
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = appColors.textSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBangla) "দামের খাতা (Price Book)" else "Commodity Price Book",
                    style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
                Text(
                    text = if (isBangla) "আলু, চাল, পেঁয়াজ, তেলের দরদাম ও ইতিহাস…" else "Search prices & trends for potatoes, rice, oil…",
                    style = typography.bodySmall,
                    color = appColors.textMuted
                )
            }
        }
    }
}

@Composable
fun BazaarRecentTripsSection(
    recentTrips: List<MarketListWithItems>,
    onBuyAgain: (MarketListWithItems) -> Unit,
    isPrivacyMode: Boolean,
    onRevealPrivacy: () -> Unit,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isBangla) "সাম্প্রতিক বাজার" else "Recent trips",
            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = appColors.textPrimary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        for (trip in recentTrips) {
            val totalSpent = trip.items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
            val totalNum = if (totalSpent % 1.0 == 0.0) "${totalSpent.toInt()}" else "%.1f".format(totalSpent)
            val totalStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(totalNum) else totalNum
            val dateStr = DateTimeHelper.formatMarketTripTitle(trip.list.createdAt, isBangla, useBengaliNumerals)

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = appColors.surface,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = trip.list.title,
                            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$dateStr · ${trip.items.size}টি পণ্য",
                            style = typography.bodySmall,
                            color = appColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onRevealPrivacy() }
                        ) {
                            Text(text = "খরচ: ", style = typography.bodySmall, color = appColors.textMuted)
                            PulseTabularText(
                                text = if (isPrivacyMode) "••••••" else "৳$totalStr",
                                style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = appColors.textPrimary
                            )
                        }
                    }

                    // Buy again button
                    Button(
                        onClick = { onBuyAgain(trip) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.surfaceVariant,
                            contentColor = appColors.textPrimary
                        ),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "আবার কিনুন" else "Buy again",
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BazaarSmartSuggestionsSection(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val haptics = rememberAppHaptics()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isBangla) "আজ সাধারণত কেনেন:" else "You usually buy today:",
            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = appColors.textSecondary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (item in suggestions.take(4)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surfaceVariant,
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onSuggestionClick(item)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item,
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatTripPickerSheet(
    pastTrips: List<MarketListWithItems>,
    onDismiss: () -> Unit,
    onSelectTrip: (MarketListWithItems) -> Unit,
    isPrivacyMode: Boolean,
    onRevealPrivacy: () -> Unit,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    modifier: Modifier = Modifier
) {
    val windowClass = LocalWindowClass.current
    val isDialog = !windowClass.widthClass.isCompact
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    val content = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = if (isBangla) "আগের বাজার থেকে বেছে নিন" else "Select a Past Trip to Repeat",
                style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (pastTrips.isEmpty()) {
                Text(
                    text = if (isBangla) "কোনো পূর্ববর্তী বাজার পাওয়া যায়নি।" else "No past completed trips found.",
                    style = typography.bodyMedium,
                    color = appColors.textMuted
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pastTrips, key = { it.list.id }) { trip ->
                        val totalSpent = trip.items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
                        val totalNum = if (totalSpent % 1.0 == 0.0) "${totalSpent.toInt()}" else "%.1f".format(totalSpent)
                        val totalStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(totalNum) else totalNum
                        val dateStr = DateTimeHelper.formatMarketTripTitle(trip.list.createdAt, isBangla, useBengaliNumerals)

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = appColors.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTrip(trip) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = trip.list.title,
                                        style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = appColors.textPrimary
                                    )
                                    Text(
                                        text = "$dateStr · ${trip.items.size}টি পণ্য",
                                        style = typography.bodySmall,
                                        color = appColors.textMuted
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onRevealPrivacy() }
                                ) {
                                    PulseTabularText(
                                        text = if (isPrivacyMode) "••••••" else "৳$totalStr",
                                        style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = appColors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = appColors.surface,
                modifier = Modifier.width(480.dp)
            ) {
                content()
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = appColors.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            content()
        }
    }
}
