package com.example.expensetracker.ui.dashboard

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Native Market Content Composable that embeds seamlessly inside
 * bottom sheets and containers without closing or bouncing across screens.
 * Features:
 * - Department grouping and interactive filtering
 * - Last purchase rate display per item
 * - Real-time target budget tracking
 * - Native WhatsApp / SMS checklist sharing
 * - Unpurchased item smart roll-over
 */
@Composable
fun MarketNativeContent(
    marketListWithItems: MarketListWithItems?,
    currencySymbol: String = "৳",
    isBangla: Boolean = true,
    onCreateListWithBulk: (title: String, items: List<MarketItem>) -> Unit,
    onAddItem: (item: MarketItem) -> Unit,
    onUpdateItem: (item: MarketItem) -> Unit,
    onDeleteItem: (itemId: Long) -> Unit,
    onMarkPurchased: (item: MarketItem, amount: Double) -> Unit,
    onUnmarkPurchased: (item: MarketItem) -> Unit,
    onCompleteList: (listId: Long) -> Unit,
    modifier: Modifier = Modifier,
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(),
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    onFetchPriceHistory: ((productName: String) -> List<ProductPriceRecord>)? = null,
    onCompleteListWithRollOver: ((listId: Long, rollOverItems: List<MarketItem>) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }

    var selectedDepartment by rememberSaveable { mutableStateOf("all") }
    var targetBudget by rememberSaveable { mutableStateOf<Double?>(null) }
    var showRollOverDialog by remember { mutableStateOf(false) }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var itemToDeleteConfirm by remember { mutableStateOf<MarketItem?>(null) }
    var itemToSubstitute by remember { mutableStateOf<MarketItem?>(null) }
    var showForgotStaplesDialog by remember { mutableStateOf(false) }

    val items = marketListWithItems?.items ?: emptyList()

    val onShareList = {
        val shareText = generateMarketShareText(
            listTitle = marketListWithItems?.list?.title ?: "",
            items = items,
            isBangla = isBangla,
            targetBudget = targetBudget
        )
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooserTitle = if (isBangla) "বাজার ফর্দ শেয়ার করুন" else "Share Market Checklist"
        context.startActivity(Intent.createChooser(sendIntent, chooserTitle))
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Sub-Tabs Capsule: [ 0: Checklist ] [ 1: Paste List ]
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LocalAppColors.current.background,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 0) appColors.accent else Color.Transparent)
                        .clickable {
                            haptics.lightTick()
                            selectedTab = 0
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Checklist,
                            contentDescription = null,
                            tint = if (selectedTab == 0) Color.White else appColors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = strings.createList,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selectedTab == 0) Color.White else appColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 1) Color(0xFF25D366) else Color.Transparent)
                        .clickable {
                            haptics.lightTick()
                            selectedTab = 1
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentPaste,
                            contentDescription = null,
                            tint = if (selectedTab == 1) Color.White else appColors.textSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = strings.pasteList,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selectedTab == 1) Color.White else appColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 1) {
            MarketBulkPasteSection(
                isBangla = isBangla,
                onListImported = { title, importedItems ->
                    onCreateListWithBulk(title, importedItems)
                    selectedTab = 0
                }
            )
        } else {
            val totalCount = items.size
            val purchasedCount = items.count { it.isPurchased }
            val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
            val totalEstimatedBudget = remember(items, productPriceInsights, lastPurchasedRates) {
                items.sumOf { itm ->
                    if (itm.isPurchased) {
                        itm.amount ?: 0.0
                    } else {
                        val insight = UnitConverterEngine.findMatchingInsight(itm.name, productPriceInsights)
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

            MarketSummarySection(
                totalCount = totalCount,
                purchasedCount = purchasedCount,
                totalSpent = totalSpent,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                targetBudget = targetBudget,
                estimatedBudget = if (totalEstimatedBudget > 0) totalEstimatedBudget else null,
                onSetTargetBudget = { targetBudget = it },
                onShareClick = if (items.isNotEmpty()) onShareList else null
            )

            if (items.isNotEmpty()) {
                val itemsByDept = remember(items) {
                    items.groupBy { getMarketItemCategory(it.name) }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Department Filter Pills Bar
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        val isAllSelected = selectedDepartment == "all"
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAllSelected) appColors.accent else appColors.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, if (isAllSelected) appColors.accent else appColors.border),
                            modifier = Modifier.clickable {
                                haptics.lightTick()
                                selectedDepartment = "all"
                            }
                        ) {
                            Text(
                                text = if (isBangla) "🛒 সব (${convertToBengaliDigits(totalCount.toString())})"
                                       else "🛒 All ($totalCount)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isAllSelected) Color.White else appColors.textSecondary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }

                    items(ALL_DEPARTMENTS.filter { itemsByDept[it.key]?.isNotEmpty() == true }) { dept ->
                        val isSelected = selectedDepartment == dept.key
                        val deptCount = itemsByDept[dept.key]?.size ?: 0
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) appColors.accent else appColors.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, if (isSelected) appColors.accent else appColors.border),
                            modifier = Modifier.clickable {
                                haptics.lightTick()
                                selectedDepartment = dept.key
                            }
                        ) {
                            val title = if (isBangla) dept.titleBn else dept.titleEn
                            val countStr = if (isBangla) convertToBengaliDigits(deptCount.toString()) else deptCount.toString()
                            Text(
                                text = "${dept.icon} $title ($countStr)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else appColors.textSecondary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (items.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = appColors.surface,
                            border = BorderStroke(1.dp, appColors.border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isBangla) "ফর্দ তৈরির ধরন বেছে নিন" else "Choose How to Create List",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = appColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBangla)
                                        "আপনার পছন্দ অনুযায়ী সরাসরি একটি একটি করে লিখুন অথবা সম্পূর্ণ মেমো এক ক্লিকে পেস্ট করুন"
                                    else
                                        "Add items one by one below or paste a WhatsApp checklist",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = appColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Option 1: Manual Checklist
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = appColors.accent.copy(alpha = 0.08f),
                                        border = BorderStroke(1.2.dp, appColors.accent.copy(alpha = 0.5f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                haptics.lightTick()
                                                selectedTab = 0
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(appColors.accent.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Checklist,
                                                    contentDescription = null,
                                                    tint = appColors.accent,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Text(
                                                text = if (isBangla) "সরাসরি ফর্দ" else "Manual List",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                ),
                                                color = appColors.textPrimary
                                            )
                                            Text(
                                                text = if (isBangla) "নিচে লিখে যোগ করুন" else "Add items below",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                color = appColors.textSecondary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                    // Option 2: Bulk Text Paste
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF25D366).copy(alpha = 0.08f),
                                        border = BorderStroke(1.2.dp, Color(0xFF25D366).copy(alpha = 0.5f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                haptics.mediumClick()
                                                selectedTab = 1
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF25D366).copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.ContentPaste,
                                                    contentDescription = null,
                                                    tint = Color(0xFF25D366),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Text(
                                                text = if (isBangla) "লিস্ট পেস্ট" else "Paste List",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                ),
                                                color = appColors.textPrimary
                                            )
                                            Text(
                                                text = if (isBangla) "মেমো পেস্ট করুন" else "Paste from WhatsApp",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                color = appColors.textSecondary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val itemsByDept = items.groupBy { getMarketItemCategory(it.name) }

                    if (selectedDepartment != "all") {
                        val filteredItems = itemsByDept[selectedDepartment] ?: emptyList()
                        items(filteredItems, key = { it.id }) { item ->
                            val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                            val lastRateText = if (!item.isPurchased && lastRate != null) {
                                val (rate, unit) = lastRate
                                val rateStr = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
                                if (isBangla) "গতবার: ৳${convertToBengaliDigits(rateStr)}/$unit"
                                else "Last: ৳$rateStr/$unit"
                            } else null

                            MarketItemRow(
                                item = item,
                                currencySymbol = currencySymbol,
                                isBangla = isBangla,
                                onUnitClick = { itemForUnitDialog = item },
                                onAmountChanged = { newAmt ->
                                    onUpdateItem(item.copy(amount = newAmt))
                                },
                                onTickClick = {
                                    if (item.isPurchased) {
                                        haptics.lightTick()
                                        onUnmarkPurchased(item)
                                    } else {
                                        if (item.amount != null && item.amount > 0) {
                                            haptics.mediumClick()
                                            onMarkPurchased(item, item.amount)
                                        } else {
                                            promptAmountForItem = item
                                        }
                                    }
                                },
                                onDeleteClick = {
                                    itemToDeleteConfirm = item
                                },
                                onRequestDelete = {
                                    itemToDeleteConfirm = item
                                },
                                onSubstituteClick = {
                                    itemToSubstitute = item
                                },
                                lastRateText = lastRateText,
                                priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
                            )
                        }
                    } else {
                        // All view with Department Section Headers
                        for (dept in ALL_DEPARTMENTS) {
                            val deptItems = itemsByDept[dept.key] ?: continue
                            if (deptItems.isEmpty()) continue

                            val purchasedInDept = deptItems.count { it.isPurchased }
                            val isDeptCompleted = purchasedInDept == deptItems.size

                            item(key = "header_${dept.key}") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp, start = 2.dp, end = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = dept.icon, fontSize = 15.sp)
                                        Text(
                                            text = if (isBangla) dept.titleBn else dept.titleEn,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = appColors.textPrimary
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isDeptCompleted) appColors.income.copy(alpha = 0.15f) else appColors.surfaceVariant,
                                        border = BorderStroke(1.dp, if (isDeptCompleted) appColors.income.copy(alpha = 0.3f) else appColors.border)
                                    ) {
                                        Text(
                                            text = if (isDeptCompleted) {
                                                if (isBangla) "✓ সম্পন্ন" else "✓ Done"
                                            } else {
                                                if (isBangla) "${convertToBengaliDigits(purchasedInDept.toString())}/${convertToBengaliDigits(deptItems.size.toString())}"
                                                else "$purchasedInDept/${deptItems.size}"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = if (isDeptCompleted) appColors.income else appColors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            items(deptItems, key = { it.id }) { item ->
                                val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                                val lastRateText = if (!item.isPurchased && lastRate != null) {
                                    val (rate, unit) = lastRate
                                    val rateStr = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
                                    if (isBangla) "গতবার: ৳${convertToBengaliDigits(rateStr)}/$unit"
                                    else "Last: ৳$rateStr/$unit"
                                } else null

                                MarketItemRow(
                                    item = item,
                                    currencySymbol = currencySymbol,
                                    isBangla = isBangla,
                                    onUnitClick = { itemForUnitDialog = item },
                                    onAmountChanged = { newAmt ->
                                        onUpdateItem(item.copy(amount = newAmt))
                                    },
                                    onTickClick = {
                                        if (item.isPurchased) {
                                            haptics.lightTick()
                                            onUnmarkPurchased(item)
                                        } else {
                                            if (item.amount != null && item.amount > 0) {
                                                haptics.mediumClick()
                                                onMarkPurchased(item, item.amount)
                                            } else {
                                                promptAmountForItem = item
                                            }
                                        }
                                    },
                                    onDeleteClick = {
                                        itemToDeleteConfirm = item
                                    },
                                    onRequestDelete = {
                                        itemToDeleteConfirm = item
                                    },
                                    onSubstituteClick = {
                                        itemToSubstitute = item
                                    },
                                    lastRateText = lastRateText,
                                    priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    MarketAddNewItemRow(
                        listId = marketListWithItems?.list?.id ?: 0L,
                        isBangla = isBangla,
                        onAddItem = onAddItem,
                        existingItemNames = items.map { it.name.lowercase() }.toSet(),
                        productPriceInsights = productPriceInsights,
                        currencySymbol = currencySymbol
                    )
                }

                if (items.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketFinishButton(
                            isBangla = isBangla,
                            onComplete = {
                                val unpurchased = items.filter { !it.isPurchased }
                                val purchased = items.filter { it.isPurchased }
                                val hasStaplePurchased = purchased.any {
                                    val name = it.name.lowercase()
                                    name.contains("মরিচ") || name.contains("লেবু") || name.contains("ধনে") ||
                                    name.contains("আদা") || name.contains("রসুন") || name.contains("chili") || name.contains("lemon")
                                }
                                if (!hasStaplePurchased && items.size >= 3) {
                                    showForgotStaplesDialog = true
                                } else if (unpurchased.isNotEmpty() && onCompleteListWithRollOver != null) {
                                    showRollOverDialog = true
                                } else {
                                    marketListWithItems?.list?.id?.let { onCompleteList(it) }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (itemForUnitDialog != null) {
        val curItem = itemForUnitDialog!!
        UnitAndQuantityDialog(
            initialQuantity = curItem.quantity,
            initialUnit = curItem.unit,
            isBangla = isBangla,
            onDismiss = { itemForUnitDialog = null },
            onConfirm = { qty, unit ->
                onUpdateItem(curItem.copy(quantity = qty, unit = unit))
                itemForUnitDialog = null
            }
        )
    }

    if (promptAmountForItem != null) {
        val curItem = promptAmountForItem!!
        val lastRate = lastPurchasedRates[curItem.name.lowercase().trim()]
        val curInsight = UnitConverterEngine.findMatchingInsight(curItem.name, productPriceInsights)
        MarketPromptAmountDialog(
            item = curItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            lastRate = lastRate,
            priceInsight = curInsight,
            onDismiss = { promptAmountForItem = null },
            onConfirm = { amt ->
                haptics.mediumClick()
                onMarkPurchased(curItem, amt)
                promptAmountForItem = null
            }
        )
    }

    // "Forgot Small Staples?" Guard Dialog before finishing shopping
    if (showForgotStaplesDialog) {
        val unpurchased = items.filter { !it.isPurchased }
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
                        if (unpurchased.isNotEmpty() && onCompleteListWithRollOver != null) {
                            showRollOverDialog = true
                        } else {
                            marketListWithItems?.list?.id?.let { onCompleteList(it) }
                        }
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

    // Smart Item Substitution Dialog
    if (itemToSubstitute != null) {
        val curItem = itemToSubstitute!!
        MarketSubstituteItemDialog(
            item = curItem,
            isBangla = isBangla,
            onDismiss = { itemToSubstitute = null },
            onConfirmSubstitute = { newTitle ->
                onUpdateItem(curItem.copy(name = newTitle))
                itemToSubstitute = null
            }
        )
    }

    // Roll-Over Dialog for unpurchased items
    if (showRollOverDialog && marketListWithItems != null) {
        val unpurchased = items.filter { !it.isPurchased }
        AlertDialog(
            onDismissRequest = { showRollOverDialog = false },
            title = {
                Text(
                    text = if (isBangla) "বাজার সম্পন্নকরণ" else "Finish Shopping",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "আপনার ফর্দে ${convertToBengaliDigits(unpurchased.size.toString())}টি পণ্য এখনো কেনা হয়নি। আপনি কি এগুলো পরবর্তী নতুন বাজার ফর্দে স্থানান্তর করতে চান?"
                    else
                        "${unpurchased.size} items were not purchased. Do you want to move them to a new market checklist?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRollOverDialog = false
                        onCompleteListWithRollOver?.invoke(marketListWithItems.list.id, unpurchased)
                    }
                ) {
                    Text(if (isBangla) "নতুন ফর্দে নিন" else "Move to Next List", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            showRollOverDialog = false
                            onCompleteList(marketListWithItems.list.id)
                        }
                    ) {
                        Text(if (isBangla) "না, সম্পন্ন করুন" else "Finish Anyway")
                    }
                    TextButton(onClick = { showRollOverDialog = false }) {
                        Text(if (isBangla) "বাতিল" else "Cancel")
                    }
                }
            }
        )
    }

    // Safety Dialog: Confirm Item Deletion (Protects against accidental swipe/touch)
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
                        haptics.warningFeedback()
                        onDeleteItem(itemToDelete.id)
                        itemToDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isBangla) "মুছে ফেলুন" else "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
}

fun generateMarketShareText(
    listTitle: String,
    items: List<MarketItem>,
    isBangla: Boolean,
    targetBudget: Double? = null
): String {
    val sb = StringBuilder()
    sb.append("🛒 ")
    val title = if (listTitle.isNotBlank() &&
        listTitle != "আজকের বাজার" &&
        listTitle != "বাজারের ফর্দ" &&
        listTitle != "Market Checklist"
    ) listTitle else if (isBangla) "আজকের বাজার ফর্দ" else "Market Checklist"
    sb.append(title)
    sb.append(":\n\n")

    val purchased = items.filter { it.isPurchased }
    val remaining = items.filter { !it.isPurchased }
    val totalSpent = purchased.sumOf { it.amount ?: 0.0 }

    val grouped = items.groupBy { getMarketItemCategory(it.name) }
    for (dept in ALL_DEPARTMENTS) {
        val deptItems = grouped[dept.key] ?: continue
        if (deptItems.isEmpty()) continue
        sb.append("${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}:\n")
        for (item in deptItems) {
            val check = if (item.isPurchased) "☑" else "☐"
            sb.append("$check ${item.name} (${item.formatQuantity()})")
            if (item.amount != null && item.amount > 0) {
                val amtStr = if (item.amount % 1.0 == 0.0) item.amount.toInt().toString() else item.amount.toString()
                sb.append(" - ৳$amtStr")
            }
            sb.append("\n")
        }
        sb.append("\n")
    }

    if (purchased.isNotEmpty()) {
        val spentFormatted = if (totalSpent % 1.0 == 0.0) totalSpent.toInt().toString() else "%.1f".format(totalSpent)
        sb.append(if (isBangla) "💰 মোট খরচ: ৳${convertToBengaliDigits(spentFormatted)} (${convertToBengaliDigits(purchased.size.toString())}টি কেনা)\n"
                  else "💰 Total Spent: ৳$spentFormatted (${purchased.size} bought)\n")
    }

    if (remaining.isNotEmpty()) {
        sb.append(if (isBangla) "⏳ বাকি আছে: ${convertToBengaliDigits(remaining.size.toString())}টি পণ্য\n"
                  else "⏳ Remaining: ${remaining.size} items\n")
    }

    if (targetBudget != null && targetBudget > 0) {
        val remainingBudget = targetBudget - totalSpent
        val remFormatted = if (remainingBudget % 1.0 == 0.0) remainingBudget.toInt().toString() else "%.1f".format(remainingBudget)
        val budFormatted = if (targetBudget % 1.0 == 0.0) targetBudget.toInt().toString() else "%.1f".format(targetBudget)
        val remText = if (isBangla) convertToBengaliDigits(remFormatted) else remFormatted
        val budText = if (isBangla) convertToBengaliDigits(budFormatted) else budFormatted
        sb.append(if (isBangla) "🎯 বাজেট: ৳$budText (অবশিষ্ট: ৳$remText)\n" else "🎯 Budget: ৳$budText (Remaining: ৳$remText)\n")
    }

    return sb.toString().trim()
}
