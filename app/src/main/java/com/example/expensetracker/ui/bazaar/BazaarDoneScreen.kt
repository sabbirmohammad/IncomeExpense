package com.example.expensetracker.ui.bazaar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.ui.adaptive.LocalWindowClass
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalReduceMotion
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.PulseTabularText
import com.example.expensetracker.ui.share.ShareExpenseSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarDoneScreen(
    listId: Long,
    onNavigateBackToHub: () -> Unit,
    onStartNewList: (Long) -> Unit,
    onViewInLedger: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    bazaarViewModel: BazaarViewModel = viewModel(),
    bazaarShopViewModel: BazaarShopViewModel = viewModel()
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val reduceMotion = LocalReduceMotion.current

    // Observe lists and settings from ViewModels
    val allLists by bazaarViewModel.allMarketLists.collectAsStateWithLifecycle()
    val isLoaded by bazaarViewModel.isMarketListsLoaded.collectAsStateWithLifecycle()
    val userProfile by bazaarViewModel.userProfile.collectAsStateWithLifecycle()
    val masterProducts by bazaarViewModel.masterProducts.collectAsStateWithLifecycle()
    val masterProductsMap by bazaarViewModel.masterProductsMap.collectAsStateWithLifecycle()
    val budgetCap = remember(listId) { bazaarShopViewModel.getBudgetCap(listId) }

    val isBangla = userProfile.appLanguage != "en"
    val useBengaliNumerals = userProfile.useBengaliNumerals || isBangla
    val isPrivacyMode by bazaarViewModel.isPrivacyMode.collectAsStateWithLifecycle()

    val currentList = remember(allLists, listId) { allLists.find { it.list.id == listId } }

    // Intercept back navigation: always return to Bazaar Hub (never pop into finished Shop checklist)
    BackHandler(enabled = true) {
        onNavigateBackToHub()
    }

    // Restore safety: if loaded and list is missing or not completed, safely pop to Hub
    LaunchedEffect(isLoaded, currentList) {
        if (isLoaded && (currentList == null || !currentList.list.isCompleted)) {
            onNavigateBackToHub()
        }
    }

    // Share Sheet state
    var showShareSheet by remember { mutableStateOf(false) }

    if (currentList == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = if (isBangla) "তথ্য লোড হচ্ছে..." else "Loading...", style = typography.bodyMedium, color = appColors.textMuted)
        }
        return
    }

    val items = currentList.items
    val purchasedItems = remember(items) { items.filter { it.isPurchased } }
    val unpurchasedCount = remember(items) { items.count { !it.isPurchased } }
    val totalSpent = remember(purchasedItems) { purchasedItems.sumOf { it.amount ?: 0.0 } }
    val linkedTxId = remember(purchasedItems) { purchasedItems.mapNotNull { it.transactionId }.firstOrNull() }
    val tripDate = currentList.list.createdAt

    // Insights derivation (Amendment C)
    val insights = remember(purchasedItems, budgetCap, totalSpent, tripDate, linkedTxId) {
        val historicalPriceEntries = purchasedItems.map { itm ->
            HistoricalPriceEntry(
                productName = itm.name,
                unitPrice = if (itm.quantity > 0 && (itm.amount ?: 0.0) > 0) (itm.amount ?: 0.0) / itm.quantity else 0.0,
                timestamp = tripDate,
                transactionId = linkedTxId
            )
        }
        BazaarShopLogic.calculateTripPriceInsights(
            tripItems = purchasedItems,
            historicalPrices = historicalPriceEntries,
            budgetCap = budgetCap,
            totalSpent = totalSpent,
            tripTimestamp = tripDate,
            tripTransactionId = linkedTxId
        )
    }

    // Responsive window class determination
    val windowClass = LocalWindowClass.current
    val isTwoColumn = windowClass.isExpanded

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "বাজার সমাপ্তি" else "Trip Summary",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBackToHub,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isBangla) "ফিরে যান" else "Back to Bazaar Hub",
                            tint = appColors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showShareSheet = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = if (isBangla) "শেয়ার করুন" else "Share Receipt",
                            tint = appColors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appColors.background)
            )
        },
        containerColor = appColors.background,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Confetti celebration (respecting reduce-motion)
            var confettiTrigger by remember { mutableStateOf(true) }
            if (!reduceMotion && confettiTrigger) {
                ConfettiCelebration(
                    trigger = confettiTrigger,
                    onAnimationEnd = { confettiTrigger = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isTwoColumn) {
                    // Medium / Expanded: Two column layout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Hero & Insights
                        Column(modifier = Modifier.weight(1f)) {
                            DoneHeroCard(
                                title = currentList.list.title,
                                totalSpent = totalSpent,
                                tripDate = tripDate,
                                isBangla = isBangla,
                                useBengaliNumerals = useBengaliNumerals,
                                isPrivacyMode = isPrivacyMode
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            DoneInsightsCard(
                                insights = insights,
                                isBangla = isBangla,
                                useBengaliNumerals = useBengaliNumerals,
                                isPrivacyMode = isPrivacyMode
                            )
                        }

                        // Right Column: Counts, Stalls, Actions
                        Column(modifier = Modifier.weight(1f)) {
                            DoneTripCountsCard(
                                purchasedCount = purchasedItems.size,
                                unpurchasedCount = unpurchasedCount,
                                isBangla = isBangla
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            DoneStallBreakdownCard(
                                items = purchasedItems,
                                masterProductsMap = masterProductsMap,
                                isBangla = isBangla,
                                useBengaliNumerals = useBengaliNumerals,
                                isPrivacyMode = isPrivacyMode
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DoneActionButtons(
                                linkedTxId = linkedTxId,
                                onShare = { showShareSheet = true },
                                onViewInLedger = { onViewInLedger(linkedTxId) },
                                onBackToHub = onNavigateBackToHub,
                                onStartNewList = {
                                    bazaarViewModel.createMarketList(
                                        title = if (isBangla) "নতুন বাজার" else "New Market List"
                                    ) { newId ->
                                        onStartNewList(newId)
                                    }
                                },
                                isBangla = isBangla
                            )
                        }
                    }
                } else {
                    // Compact: Single column layout
                    DoneHeroCard(
                        title = currentList.list.title,
                        totalSpent = totalSpent,
                        tripDate = tripDate,
                        isBangla = isBangla,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DoneInsightsCard(
                        insights = insights,
                        isBangla = isBangla,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DoneTripCountsCard(
                        purchasedCount = purchasedItems.size,
                        unpurchasedCount = unpurchasedCount,
                        isBangla = isBangla
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DoneStallBreakdownCard(
                        items = purchasedItems,
                        masterProductsMap = masterProductsMap,
                        isBangla = isBangla,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    DoneActionButtons(
                        linkedTxId = linkedTxId,
                        onShare = { showShareSheet = true },
                        onViewInLedger = { onViewInLedger(linkedTxId) },
                        onBackToHub = onNavigateBackToHub,
                        onStartNewList = {
                            bazaarViewModel.createMarketList(
                                title = if (isBangla) "নতুন বাজার" else "New Market List"
                            ) { newId ->
                                onStartNewList(newId)
                            }
                        },
                        isBangla = isBangla
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Reusable Share Slip Sheet
    if (showShareSheet) {
        ShareExpenseSheet(
            initialTargetDateMillis = tripDate,
            onDismiss = { showShareSheet = false }
        )
    }
}

@Composable
private fun DoneHeroCard(
    title: String,
    totalSpent: Double,
    tripDate: Long,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = appColors.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Checkmark celebration badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(appColors.accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isBangla) "বাজারের হিসাব সম্পন্ন হয়েছে!" else "Market Trip Completed!",
                style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                style = typography.bodyMedium,
                color = appColors.textMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total spent hero banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = appColors.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isBangla) "মোট খরচ" else "Total Spent",
                        style = typography.labelSmall,
                        color = appColors.textMuted
                    )
                    PulseTabularText(
                        text = formatAmount(totalSpent, isBangla, useBengaliNumerals, isPrivacyMode),
                        style = typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = appColors.accent
                    )
                    val dateFormatted = DateTimeHelper.formatDayGroup(tripDate, isBangla, useBengaliNumerals)
                    Text(
                        text = dateFormatted,
                        style = typography.labelSmall,
                        color = appColors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun DoneInsightsCard(
    insights: BazaarDoneInsights,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    // Show card if budget exists or price insights are available and latest
    val hasBudgetDelta = insights.budgetDelta != null
    val showPriceComparison = insights.isLatestTrip && (insights.cheaperCount > 0 || insights.pricierCount > 0)

    if (!hasBudgetDelta && !showPriceComparison) return

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isBangla) "বাজার বিশ্লেষণ ও অন্তর্দৃষ্টি" else "Trip Insights",
                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Budget Delta
            if (insights.budgetDelta != null) {
                val isSaved = insights.budgetDelta >= 0
                val deltaAbs = kotlin.math.abs(insights.budgetDelta)
                val deltaStr = if (deltaAbs % 1.0 == 0.0) deltaAbs.toInt().toString() else deltaAbs.toString()
                val deltaFormatted = if (useBengaliNumerals) DigitConverter.toBengaliDigits(deltaStr) else deltaStr

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSaved) appColors.accent.copy(alpha = 0.1f) else appColors.warning.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = if (isSaved) appColors.accent else appColors.warning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSaved) {
                            if (isBangla) "বাজেটের চেয়ে ৳$deltaFormatted সাশ্রয় হয়েছে!" else "৳$deltaFormatted under budget!"
                        } else {
                            if (isBangla) "বাজেটের চেয়ে ৳$deltaFormatted বেশি খরচ হয়েছে" else "৳$deltaFormatted over budget"
                        },
                        style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isSaved) appColors.accent else appColors.warning
                    )
                }
            }

            // Commodity Price Comparison vs Last Trip (Amendment C)
            if (showPriceComparison) {
                Spacer(modifier = Modifier.height(8.dp))
                if (insights.cheaperCount > 0) {
                    val countStr = if (useBengaliNumerals) DigitConverter.toBengaliDigits(insights.cheaperCount.toString()) else insights.cheaperCount.toString()
                    Text(
                        text = if (isBangla)
                            "• ${countStr}টি পণ্য আগেরবারের চেয়ে কম দামে কেনা হয়েছে (সাশ্রয়ী)"
                        else
                            "• $countStr items purchased cheaper than previous trip",
                        style = typography.bodySmall,
                        color = appColors.accent
                    )
                }
                if (insights.pricierCount > 0) {
                    val countStr = if (useBengaliNumerals) DigitConverter.toBengaliDigits(insights.pricierCount.toString()) else insights.pricierCount.toString()
                    Text(
                        text = if (isBangla)
                            "• ${countStr}টি পণ্যের দাম আগেরবারের চেয়ে বেশি ছিল"
                        else
                            "• $countStr items were pricier than previous trip",
                        style = typography.bodySmall,
                        color = appColors.warning
                    )
                }
            }
        }
    }
}

@Composable
private fun DoneTripCountsCard(
    purchasedCount: Int,
    unpurchasedCount: Int,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$purchasedCount",
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = appColors.accent
                )
                Text(
                    text = if (isBangla) "কেনা সম্পন্ন" else "Items Bought",
                    style = typography.labelSmall,
                    color = appColors.textMuted
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(appColors.border)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$unpurchasedCount",
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (unpurchasedCount > 0) appColors.warning else appColors.textMuted
                )
                Text(
                    text = if (isBangla) "বাকি / ফর্দে স্থানান্তরিত" else "Unbought / Carried",
                    style = typography.labelSmall,
                    color = appColors.textMuted
                )
            }
        }
    }
}

@Composable
private fun DoneStallBreakdownCard(
    items: List<MarketItem>,
    masterProductsMap: Map<String, com.example.expensetracker.data.model.MasterProduct>?,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    val stallTotals = remember(items, masterProductsMap) {
        val map = mutableMapOf<String, Double>()
        for (item in items) {
            val dept = BazaarDomainLogic.classifyStallWithOverride(item.name, masterProductsMap)
            val amt = item.amount ?: 0.0
            map[dept] = (map[dept] ?: 0.0) + amt
        }
        map.toList().filter { it.second > 0.0 }
    }

    if (stallTotals.isEmpty()) return

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isBangla) "দোকান / স্টল ভিত্তিক খরচ" else "Stall Spending Breakdown",
                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for ((stallKey, amount) in stallTotals) {
                    val stallName = when (stallKey) {
                        "veggies" -> if (isBangla) "🥬 শাকসবজি ও কাঁচাবাজার" else "🥬 Veggies & Greens"
                        "meat_fish" -> if (isBangla) "🐟 মাছ ও মাংস" else "🐟 Meat & Fish"
                        "dairy_bakery" -> if (isBangla) "🥛 দুধ ও বেকারি" else "🥛 Dairy & Bakery"
                        "groceries" -> if (isBangla) "🧂 মুদি সদাই" else "🧂 Groceries"
                        else -> if (isBangla) "🛍️ অন্যান্য স্টল" else "🛍️ Other Items"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stallName, style = typography.bodySmall, color = appColors.textPrimary)
                        PulseTabularText(
                            text = formatAmount(amount, isBangla, useBengaliNumerals, isPrivacyMode),
                            style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoneActionButtons(
    linkedTxId: Long?,
    onShare: () -> Unit,
    onViewInLedger: () -> Unit,
    onBackToHub: () -> Unit,
    onStartNewList: () -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Share receipt button
        Button(
            onClick = onShare,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isBangla) "রশিদ শেয়ার করুন" else "Share Receipt Slip",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        // View in Ledger (shown only if expense was created)
        if (linkedTxId != null) {
            OutlinedButton(
                onClick = onViewInLedger,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp), tint = appColors.textPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "লেজারে দেখুন" else "View in Ledger",
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = appColors.textPrimary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Back to Bazaar Hub
            OutlinedButton(
                onClick = onBackToHub,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border)
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp), tint = appColors.textPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "বাজারে ফিরুন" else "Back to Hub",
                    style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = appColors.textPrimary
                )
            }

            // Create New List (creates in DB, then navigates to Plan screen)
            Button(
                onClick = onStartNewList,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.surfaceVariant)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = appColors.textPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "নতুন ফর্দ" else "New List",
                    style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            }
        }
    }
}

private fun formatAmount(
    amount: Double,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    mask: Boolean
): String {
    if (mask) return "••••"
    val amtStr = if (amount % 1.0 == 0.0) "${amount.toLong()}" else String.format(java.util.Locale.US, "%.2f", amount)
    val formatted = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(amtStr) else amtStr
    return "৳$formatted"
}
