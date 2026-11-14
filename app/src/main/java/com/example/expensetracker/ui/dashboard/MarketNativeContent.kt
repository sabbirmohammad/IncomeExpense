package com.example.expensetracker.ui.dashboard

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.*
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.*

@Composable
fun MarketNativeContent(
    marketListWithItems: MarketListWithItems?, currencySymbol: String = "৳", isBangla: Boolean = true,
    onCreateListWithBulk: (title: String, items: List<MarketItem>) -> Unit,
    onAddItem: (item: MarketItem) -> Unit, onUpdateItem: (item: MarketItem) -> Unit, onDeleteItem: (itemId: Long) -> Unit,
    onMarkPurchased: (item: MarketItem, amount: Double) -> Unit, onUnmarkPurchased: (item: MarketItem) -> Unit,
    onCompleteList: (listId: Long) -> Unit, modifier: Modifier = Modifier,
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(),
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    onFetchPriceHistory: ((productName: String) -> List<ProductPriceRecord>)? = null,
    onCompleteListWithRollOver: ((listId: Long, rollOverItems: List<MarketItem>) -> Unit)? = null
) {
    val appColors = LocalAppColors.current; val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current; val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedDepartment by rememberSaveable { mutableStateOf("all") }
    var targetBudget by rememberSaveable { mutableStateOf<Double?>(null) }; var showRollOverDialog by remember { mutableStateOf(false) }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var itemToDeleteConfirm by remember { mutableStateOf<MarketItem?>(null) }
    var itemToSubstitute by remember { mutableStateOf<MarketItem?>(null) }; var showForgotStaplesDialog by remember { mutableStateOf(false) }

    val items = marketListWithItems?.items ?: emptyList()

    val onShareList = {
        val sendIntent = Intent(Intent.ACTION_SEND).apply { putExtra(Intent.EXTRA_TEXT, generateMarketShareText(marketListWithItems?.list?.title ?: "", items, isBangla, targetBudget)); type = "text/plain" }
        context.startActivity(Intent.createChooser(sendIntent, if (isBangla) "বাজার ফর্দ শেয়ার করুন" else "Share Market Checklist"))
    }

    val renderItemRow: @Composable (MarketItem) -> Unit = { item ->
        val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
        val lastRateText = if (!item.isPurchased && lastRate != null) {
            val (rate, unit) = lastRate
            val rateStr = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
            if (isBangla) "গতবার: ৳${convertToBengaliDigits(rateStr)}/$unit" else "Last: ৳$rateStr/$unit"
        } else null

        MarketItemRow(
            item = item, currencySymbol = currencySymbol, isBangla = isBangla,
            onUnitClick = { itemForUnitDialog = item }, onAmountChanged = { onUpdateItem(item.copy(amount = it)) },
            onTickClick = {
                if (item.isPurchased) { haptics.lightTick(); onUnmarkPurchased(item) }
                else if (item.amount != null && item.amount > 0) { haptics.mediumClick(); onMarkPurchased(item, item.amount) }
                else { promptAmountForItem = item }
            },
            onDeleteClick = { itemToDeleteConfirm = item },
            onRequestDelete = {
                                    itemToDeleteConfirm = item
                                },
            onSubstituteClick = { itemToSubstitute = item },
            lastRateText = lastRateText,
            priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        MarketNativeTabsCapsule(selectedTab = selectedTab, onTabSelected = { selectedTab = it }, strings = strings, appColors = appColors, haptics = haptics)
        Spacer(modifier = Modifier.height(10.dp))
        if (selectedTab == 1) {
            MarketBulkPasteSection(isBangla = isBangla, onListImported = { title, importedItems -> onCreateListWithBulk(title, importedItems); selectedTab = 0 })
        } else {
            MarketSummarySection(
                totalCount = items.size, purchasedCount = items.count { it.isPurchased },
                totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 },
                currencySymbol = currencySymbol, isBangla = isBangla, targetBudget = targetBudget,
                estimatedBudget = remember(items, productPriceInsights, lastPurchasedRates) {
                    val est = MarketBudgetCalculator.calculateTotalEstimatedBudget(items, productPriceInsights, lastPurchasedRates)
                    if (est > 0) est else null
                },
                onSetTargetBudget = { targetBudget = it }, onShareClick = if (items.isNotEmpty()) onShareList else null
            )
            if (items.isNotEmpty()) {
                val itemsByDept = remember(items) { items.groupBy { getMarketItemCategory(it.name) } }
                Spacer(modifier = Modifier.height(8.dp))
                MarketNativeDepartmentFilterBar(
                    selectedDepartment = selectedDepartment, onSelectDepartment = { selectedDepartment = it },
                    itemsByDept = itemsByDept, totalCount = items.size, isBangla = isBangla,
                    appColors = appColors, haptics = haptics
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (items.isEmpty()) {
                    item {
                        Surface(shape = RoundedCornerShape(16.dp), color = appColors.surface, border = BorderStroke(1.dp, appColors.border), modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isBangla) "ফর্দ তৈরির ধরন বেছে নিন" else "Choose How to Create List", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp), color = appColors.textPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = if (isBangla) "আপনার পছন্দ অনুযায়ী সরাসরি একটি একটি করে লিখুন অথবা সম্পূর্ণ মেমো এক ক্লিকে পেস্ট করুন" else "Add items one by one below or paste a WhatsApp checklist", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = appColors.textSecondary, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(shape = RoundedCornerShape(12.dp), color = appColors.accent.copy(alpha = 0.08f), border = BorderStroke(1.2.dp, appColors.accent.copy(alpha = 0.5f)), modifier = Modifier.weight(1f).clickable { haptics.lightTick(); selectedTab = 0 }) {
                                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(appColors.accent.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Outlined.Checklist, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(20.dp)) }
                                            Text(text = if (isBangla) "সরাসরি ফর্দ" else "Manual List", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp), color = appColors.textPrimary)
                                            Text(text = if (isBangla) "নিচে লিখে যোগ করুন" else "Add items below", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), color = appColors.textSecondary, textAlign = TextAlign.Center)
                                        }
                                    }
                                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF25D366).copy(alpha = 0.08f), border = BorderStroke(1.2.dp, Color(0xFF25D366).copy(alpha = 0.5f)), modifier = Modifier.weight(1f).clickable { haptics.mediumClick(); selectedTab = 1 }) {
                                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF25D366).copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Filled.ContentPaste, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(20.dp)) }
                                            Text(text = if (isBangla) "লিস্ট পেস্ট" else "Paste List", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp), color = appColors.textPrimary)
                                            Text(text = if (isBangla) "মেমো পেস্ট করুন" else "Paste from WhatsApp", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), color = appColors.textSecondary, textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val itemsByDept = items.groupBy { getMarketItemCategory(it.name) }
                    if (selectedDepartment != "all") {
                        items(itemsByDept[selectedDepartment] ?: emptyList(), key = { it.id }) { renderItemRow(it) }
                    } else {
                        for (dept in ALL_DEPARTMENTS) {
                            val deptItems = itemsByDept[dept.key] ?: continue
                            if (deptItems.isEmpty()) continue
                            item(key = "header_${dept.key}") { MarketNativeDeptHeader(dept = dept, purchasedInDept = deptItems.count { it.isPurchased }, deptItemsCount = deptItems.size, isBangla = isBangla, appColors = appColors) }
                            items(deptItems, key = { it.id }) { renderItemRow(it) }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    MarketAddNewItemRow(
                        listId = marketListWithItems?.list?.id ?: 0L, isBangla = isBangla, onAddItem = onAddItem,
                        existingItemNames = items.map { it.name.lowercase() }.toSet(), productPriceInsights = productPriceInsights, currencySymbol = currencySymbol
                    )
                }
                if (items.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketFinishButton(isBangla = isBangla, onComplete = {
                            val unpurchased = items.filter { !it.isPurchased }
                            val hasStaplePurchased = items.any { it.isPurchased && (it.name.contains("মরিচ") || it.name.contains("লেবু") || it.name.contains("ধনে") || it.name.contains("আদা") || it.name.contains("রসুন") || it.name.lowercase().contains("chili") || it.name.lowercase().contains("lemon")) }
                            if (!hasStaplePurchased && items.size >= 3) showForgotStaplesDialog = true
                            else if (unpurchased.isNotEmpty() && onCompleteListWithRollOver != null) showRollOverDialog = true
                            else marketListWithItems?.list?.id?.let { onCompleteList(it) }
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    MarketNativeAllDialogs(
        itemForUnitDialog = itemForUnitDialog, onDismissUnitDialog = { itemForUnitDialog = null },
        onConfirmUnitDialog = { curItem, qty, unit -> onUpdateItem(curItem.copy(quantity = qty, unit = unit)); itemForUnitDialog = null },
        promptAmountForItem = promptAmountForItem, onDismissPromptDialog = { promptAmountForItem = null },
        onConfirmPromptDialog = { curItem, amt -> onMarkPurchased(curItem, amt); promptAmountForItem = null },
        itemToSubstitute = itemToSubstitute, onDismissSubstituteDialog = { itemToSubstitute = null },
        onConfirmSubstituteDialog = { curItem, title -> onUpdateItem(curItem.copy(name = title)); itemToSubstitute = null },
        showForgotStaplesDialog = showForgotStaplesDialog, onDismissForgotStaples = { showForgotStaplesDialog = false },
        onConfirmForgotStaples = {
            showForgotStaplesDialog = false
            if (items.any { !it.isPurchased } && onCompleteListWithRollOver != null) showRollOverDialog = true
            else marketListWithItems?.list?.id?.let { onCompleteList(it) }
        },
        showRollOverDialog = showRollOverDialog && marketListWithItems != null,
        unpurchasedCount = items.count { !it.isPurchased }, onDismissRollOver = { showRollOverDialog = false },
        onConfirmRollOverMove = { showRollOverDialog = false; marketListWithItems?.let { onCompleteListWithRollOver?.invoke(it.list.id, items.filter { itm -> !itm.isPurchased }) } },
        onConfirmRollOverFinishAnyway = { showRollOverDialog = false; marketListWithItems?.let { onCompleteList(it.list.id) } },
        itemToDeleteConfirm = itemToDeleteConfirm, onDismissDeleteConfirm = { itemToDeleteConfirm = null },
        onConfirmDelete = { id -> onDeleteItem(id); itemToDeleteConfirm = null },
        currencySymbol = currencySymbol, isBangla = isBangla, lastPurchasedRates = lastPurchasedRates,
        productPriceInsights = productPriceInsights, appColors = appColors, haptics = haptics
    )
}
