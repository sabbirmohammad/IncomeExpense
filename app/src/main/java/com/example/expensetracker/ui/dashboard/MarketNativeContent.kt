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
    onCompleteListWithRollOver: ((listId: Long, rollOverItems: List<MarketItem>) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    var selectedTab by remember {
        mutableIntStateOf(if (marketListWithItems == null || marketListWithItems.items.isEmpty()) 1 else 0)
    }

    var selectedDepartment by rememberSaveable { mutableStateOf("all") }
    var targetBudget by rememberSaveable { mutableStateOf<Double?>(null) }
    var showRollOverDialog by remember { mutableStateOf(false) }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }

    val items = marketListWithItems?.items ?: emptyList()

    val onShareList = {
        val shareText = generateMarketShareText(
            listTitle = marketListWithItems?.list?.title ?: "",
            items = items,
            isBangla = isBangla
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

            MarketSummarySection(
                totalCount = totalCount,
                purchasedCount = purchasedCount,
                totalSpent = totalSpent,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                targetBudget = targetBudget,
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isBangla) "বর্তমানে কোনো বাজার ফর্দ সক্রিয় নেই" else "No active market list",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = appColors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "নিচে নতুন পণ্য যোগ করুন অথবা ওপরের 'লিস্ট পেস্ট' থেকে যোগ করুন"
                                       else "Add items below or paste a WhatsApp list above",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = appColors.textSecondary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
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
                                    haptics.lightTick()
                                    onDeleteItem(item.id)
                                },
                                lastRateText = lastRateText
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
                                        haptics.lightTick()
                                        onDeleteItem(item.id)
                                    },
                                    lastRateText = lastRateText
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
                        existingItemNames = items.map { it.name.lowercase() }.toSet()
                    )
                }

                if (items.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketFinishButton(
                            isBangla = isBangla,
                            onComplete = {
                                val unpurchased = items.filter { !it.isPurchased }
                                if (unpurchased.isNotEmpty() && onCompleteListWithRollOver != null) {
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
        MarketPromptAmountDialog(
            item = promptAmountForItem!!,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onDismiss = { promptAmountForItem = null },
            onConfirm = { amt ->
                onMarkPurchased(promptAmountForItem!!, amt)
                promptAmountForItem = null
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
}

fun generateMarketShareText(
    listTitle: String,
    items: List<MarketItem>,
    isBangla: Boolean
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
    return sb.toString().trim()
}
