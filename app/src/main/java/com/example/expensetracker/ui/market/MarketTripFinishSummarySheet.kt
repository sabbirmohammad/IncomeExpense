package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import kotlin.math.abs
import kotlin.math.roundToInt

enum class LeftoverHandlingOption {
    MOVE_TO_NEXT,
    KEEP_IN_LIST,
    DISCARD
}

data class PriceChangeInsight(
    val itemName: String,
    val diffAmount: Double,
    val diffPercentage: Double,
    val isIncrease: Boolean
)

/**
 * V7 Target Design D: Market Trip Finish Summary Sheet.
 * - Total spent amount
 * - Bought vs skipped counts
 * - Biggest price changes
 * - Account chips (defaulting to last used/default account)
 * - Date chip
 * - If items left: choose "Move to next list", "Keep in list", "Discard"
 * - Primary button "Save ৳... as expense" (reuses atomic trip completion)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketTripFinishSummarySheet(
    trip: MarketListWithItems,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirmComplete: (selectedAccountId: Long?, leftoverChoice: LeftoverHandlingOption) -> Unit,
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val defaultAccount = remember(accounts) {
        accounts.find { it.isDefault } ?: accounts.firstOrNull()
    }
    var selectedAccountId by remember(defaultAccount?.id) { mutableStateOf<Long?>(defaultAccount?.id) }

    val boughtItems = remember(trip.items) { trip.items.filter { it.isPurchased } }
    val unboughtItems = remember(trip.items) { trip.items.filter { !it.isPurchased } }
    val totalSpent = remember(boughtItems) { boughtItems.sumOf { it.amount ?: 0.0 } }

    var leftoverOption by remember { mutableStateOf(LeftoverHandlingOption.MOVE_TO_NEXT) }

    // Derive biggest price changes
    val priceChanges = remember(boughtItems, productPriceInsights) {
        val list = mutableListOf<PriceChangeInsight>()
        for (item in boughtItems) {
            val amt = item.amount ?: continue
            if (amt <= 0 || item.quantity <= 0) continue
            val (currentRate, _) = UnitConverterEngine.calculateBaseUnitPrice(amt, item.quantity, item.unit)
            val insight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
            val prevRate = insight?.currentUnitPrice ?: continue
            if (prevRate > 0) {
                val diff = currentRate - prevRate
                val pct = (diff / prevRate) * 100.0
                if (abs(pct) >= 3.0) {
                    list.add(
                        PriceChangeInsight(
                            itemName = item.name,
                            diffAmount = abs(diff),
                            diffPercentage = abs(pct),
                            isIncrease = diff > 0
                        )
                    )
                }
            }
        }
        list.sortedByDescending { it.diffPercentage }.take(3)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = appColors.cardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(38.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(appColors.divider)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.finishShopping,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = appColors.textPrimary
                    )
                    Text(
                        text = trip.list.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = appColors.textSecondary
                    )
                }

                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onDismiss()
                    },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary
                    )
                }
            }

            // Big Total Card (Thermal receipt summary feeling)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = appColors.surface,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${strings.marketSpent} (${strings.marketTotalPrice})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textSecondary
                    )

                    Text(
                        text = formatCurrency(totalSpent, currencySymbol),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textPrimary
                    )

                    // Counts row: Bought vs Skipped
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val boughtCountDigits = if (isBangla) convertToBengaliDigits(boughtItems.size.toString()) else boughtItems.size.toString()
                        val skippedCountDigits = if (isBangla) convertToBengaliDigits(unboughtItems.size.toString()) else unboughtItems.size.toString()

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = appColors.income.copy(alpha = 0.12f),
                            border = BorderStroke(HairlineInkStroke, appColors.income.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "✓ $boughtCountDigits ${strings.marketBought}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = appColors.income,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (unboughtItems.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = appColors.textSecondary.copy(alpha = 0.12f),
                                border = BorderStroke(HairlineInkStroke, appColors.divider)
                            ) {
                                Text(
                                    text = "$skippedCountDigits ${strings.marketLeft}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = appColors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Price Changes Intelligence (Highlights)
            if (priceChanges.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = appColors.surface,
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBangla) "দরের উল্লেখযোগ্য পরিবর্তন:" else "Notable Price Shifts:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = appColors.textPrimary
                        )

                        for (change in priceChanges) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = change.itemName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = appColors.textPrimary
                                )

                                val pctStr = change.diffPercentage.roundToInt().toString()
                                val pctDigits = if (isBangla) convertToBengaliDigits(pctStr) else pctStr
                                val diffStr = if (change.diffAmount % 1.0 == 0.0) change.diffAmount.toInt().toString() else "%.1f".format(change.diffAmount)
                                val diffDigits = if (isBangla) convertToBengaliDigits(diffStr) else diffStr

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (change.isIncrease) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = if (change.isIncrease) appColors.warning else appColors.income,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${if (change.isIncrease) "+" else "−"}$currencySymbol$diffDigits ($pctDigits%)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = if (change.isIncrease) appColors.warning else appColors.income
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Account Selector & Date Chip
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isBangla) "পরিশোধের অ্যাকাউন্ট:" else "Payment Account:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    color = appColors.textSecondary
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accounts) { acc ->
                        val isSelected = selectedAccountId == acc.id
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                selectedAccountId = acc.id
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) appColors.accentContainer else appColors.surface,
                            border = BorderStroke(
                                HairlineInkStroke,
                                if (isSelected) appColors.accent else appColors.divider
                            ),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 44.dp)
                                .minimumInteractiveComponentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val icon = when (acc.type) {
                                    AccountType.BANK -> Icons.Filled.AccountBalance
                                    AccountType.MFS -> Icons.Filled.CreditCard
                                    AccountType.CASH -> Icons.Filled.Payments
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) appColors.accent else appColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = acc.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) appColors.accent else appColors.textPrimary
                                )
                            }
                        }
                    }

                    // Date Chip
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = appColors.surface,
                            border = BorderStroke(HairlineInkStroke, appColors.divider),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 44.dp)
                                .minimumInteractiveComponentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = null,
                                    tint = appColors.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = DateTimeHelper.formatMarketTripTitle(System.currentTimeMillis(), isBangla),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = appColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Leftover items choice (only if unbought items remain)
            if (unboughtItems.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = appColors.surface,
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val unboughtCountDigits = if (isBangla) convertToBengaliDigits(unboughtItems.size.toString()) else unboughtItems.size.toString()
                        Text(
                            text = if (isBangla) "বাকি থাকা ($unboughtCountDigits) পণ্যের ক্ষেত্রে কি করবেন?" else "What to do with $unboughtCountDigits unbought item(s)?",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = appColors.textPrimary
                        )

                        // 3 Option Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // 1. Move to next list
                            val isMove = leftoverOption == LeftoverHandlingOption.MOVE_TO_NEXT
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    leftoverOption = LeftoverHandlingOption.MOVE_TO_NEXT
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isMove) appColors.accentContainer else Color.Transparent,
                                border = BorderStroke(
                                    HairlineInkStroke,
                                    if (isMove) appColors.accent else appColors.divider
                                ),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = strings.marketMoveToNextList,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (isMove) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isMove) appColors.accent else appColors.textSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // 2. Keep in list
                            val isKeep = leftoverOption == LeftoverHandlingOption.KEEP_IN_LIST
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    leftoverOption = LeftoverHandlingOption.KEEP_IN_LIST
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isKeep) appColors.accentContainer else Color.Transparent,
                                border = BorderStroke(
                                    HairlineInkStroke,
                                    if (isKeep) appColors.accent else appColors.divider
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = strings.marketKeepInList,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (isKeep) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isKeep) appColors.accent else appColors.textSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // 3. Discard
                            val isDiscard = leftoverOption == LeftoverHandlingOption.DISCARD
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    leftoverOption = LeftoverHandlingOption.DISCARD
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDiscard) appColors.warning.copy(alpha = 0.15f) else Color.Transparent,
                                border = BorderStroke(
                                    HairlineInkStroke,
                                    if (isDiscard) appColors.warning else appColors.divider
                                ),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = strings.marketDiscardLeftover,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (isDiscard) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isDiscard) appColors.warning else appColors.textSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Primary Finish Button: "Save ৳... as expense"
            val totalStr = formatCurrency(totalSpent, currencySymbol)
            Button(
                onClick = {
                    haptics.successFeedback()
                    onConfirmComplete(selectedAccountId, leftoverOption)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${strings.marketSaveAsExpense} ($totalStr)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// =========================================================================
// PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "MarketTripFinishSummarySheet - Dark En", showBackground = true)
@Composable
fun PreviewMarketTripFinishSummaryDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketTripFinishSummarySheet(
                    trip = MarketListWithItems(
                        list = com.example.expensetracker.data.model.MarketList(
                            id = 1L,
                            title = "Weekly Bazar",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "Potatoes", quantity = 2.0, unit = "kg", isPurchased = true, amount = 80.0),
                            MarketItem(id = 2L, listId = 1L, name = "Onions", quantity = 1.0, unit = "kg", isPurchased = true, amount = 120.0),
                            MarketItem(id = 3L, listId = 1L, name = "Ginger", quantity = 250.0, unit = "g", isPurchased = false)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "Cash", type = AccountType.CASH, isDefault = true),
                        Account(id = 2L, name = "bKash", type = AccountType.MFS)
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    onDismiss = {},
                    onConfirmComplete = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketTripFinishSummarySheet - Light Bn", showBackground = true)
@Composable
fun PreviewMarketTripFinishSummaryLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketTripFinishSummarySheet(
                    trip = MarketListWithItems(
                        list = com.example.expensetracker.data.model.MarketList(
                            id = 1L,
                            title = "সকালের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 70.0),
                            MarketItem(id = 2L, listId = 1L, name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = true, amount = 90.0)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ ক্যাশ", type = AccountType.CASH, isDefault = true),
                        Account(id = 2L, name = "বিকাশ", type = AccountType.MFS)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onDismiss = {},
                    onConfirmComplete = { _, _ -> }
                )
            }
        }
    }
}

@Preview(name = "MarketTripFinishSummarySheet - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketTripFinishSummaryAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketTripFinishSummarySheet(
                    trip = MarketListWithItems(
                        list = com.example.expensetracker.data.model.MarketList(
                            id = 1L,
                            title = "মাছের বাজার",
                            createdAt = System.currentTimeMillis()
                        ),
                        items = listOf(
                            MarketItem(id = 1L, listId = 1L, name = "রুই মাছ", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 700.0)
                        )
                    ),
                    accounts = listOf(
                        Account(id = 1L, name = "ক্যাশ", type = AccountType.CASH, isDefault = true)
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onDismiss = {},
                    onConfirmComplete = { _, _ -> }
                )
            }
        }
    }
}
