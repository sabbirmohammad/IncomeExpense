package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.market.MarketParsedTagChip

enum class CreateMarketListStep { TITLE, METHOD, ITEMS }

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun CreateMarketListSection(
    isBangla: Boolean, onListCreated: (title: String, items: List<MarketItem>) -> Unit,
    priceInsights: Map<String, ProductPriceInsight> = emptyMap(), currencySymbol: String = "৳",
    lastCompletedList: MarketListWithItems? = null, onOpenBulkPasteSheet: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val defaultTitle = remember(isBangla) { CreateMarketListHelper.resolveDefaultTitle(isBangla) }
    var listTitle by remember { mutableStateOf(defaultTitle) }
    var itemInputText by remember { mutableStateOf("") }
    var selectedQuantityText by remember { mutableStateOf("১ কেজি") }
    var isClipboardDismissed by remember { mutableStateOf(false) }
    var showInlinePasteBox by remember { mutableStateOf(false) }
    val manualItems = remember { mutableStateOf(mutableListOf<MarketItem>()) }

    val quickTitles = listOf(Pair("সকালের কাঁচাবাজার", Icons.Outlined.ShoppingCart), Pair("বিকালের মুদি", Icons.Outlined.LocalGroceryStore), Pair("সাপ্তাহিক সদাই", Icons.Outlined.ShoppingBag), Pair("মাছ ও মাংস", Icons.Outlined.Checklist))
    val quickQuantities = listOf("১ কেজি", "২ কেজি", "৫০০ গ্রাম", "২৫০ গ্রাম", "১ লিটার", "১ ডজন", "৪ পিস")
    val quickStaples = listOf("আলু" to "২ কেজি", "পেঁয়াজ" to "১ কেজি", "কাঁচামরিচ" to "২৫০ গ্রাম", "ডিম" to "১ ডজন", "সয়াবিন তেল" to "২ লিটার", "রসুন" to "২৫০ গ্রাম", "আদা" to "২৫০ গ্রাম", "লবণ" to "১ কেজি")

    val addManualItem: (String, String) -> Unit = { rawName, qtyStr ->
        val trimmed = rawName.trim()
        if (trimmed.isNotBlank()) {
            haptics.mediumClick()
            val (name, qty, unit) = CreateMarketListHelper.parseNameAndQty(trimmed, qtyStr)
            val key = UnitConverterEngine.canonicalCommodityKey(name)
            val existingIndex = manualItems.value.indexOfFirst { UnitConverterEngine.canonicalCommodityKey(it.name) == key }
            if (existingIndex >= 0) {
                manualItems.value = manualItems.value.toMutableList().apply { this[existingIndex] = this[existingIndex].copy(quantity = this[existingIndex].quantity + qty) }
            } else {
                val itemsToAdd = listOf(MarketItem(name = name, quantity = qty, unit = unit))
                manualItems.value = (manualItems.value + itemsToAdd).toMutableList()
            }
            itemInputText = ""
        }
    }

    Box(modifier = modifier.fillMaxSize().background(appColors.background)) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().imePadding().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                CreateMarketListTitleInput(
                    listTitle = listTitle, onTitleChange = { listTitle = it }, defaultTitle = defaultTitle,
                    quickTitles = quickTitles, isBangla = isBangla, appColors = appColors, haptics = haptics, modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                CreateMarketListActionShortcuts(
                    onToggleInlinePaste = { if (onOpenBulkPasteSheet != null) onOpenBulkPasteSheet() else showInlinePasteBox = !showInlinePasteBox },
                    lastCompletedList = lastCompletedList,
                    onRepeatLastList = { itemsToAdd -> manualItems.value = (manualItems.value + itemsToAdd).toMutableList() },
                    strings = strings, appColors = appColors, haptics = haptics
                )
            }
            if (showInlinePasteBox) {
                item {
                    CreateMarketListInlinePasteBox(
                        isBangla = isBangla,
                        onImportText = { text ->
                            val parsed = MarketListParser.parseBulkText(text)
                            if (parsed.isNotEmpty()) {
                                manualItems.value = (manualItems.value + parsed).toMutableList()
                                showInlinePasteBox = false
                            }
                        },
                        appColors = appColors
                    )
                }
            }
            if (false) { item { FlowRow { MarketParsedTagChip(item = MarketItem(name = "আলু", quantity = 1.0, unit = "কেজি"), isBangla = isBangla, onRemove = {}) } } }
            // Added Items Section
            if (manualItems.value.isNotEmpty()) {
                item {
                    val countStr = if (isBangla) convertToBengaliDigits(manualItems.value.size.toString()) else manualItems.value.size.toString()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (isBangla) "ফর্দের পণ্যসমূহ" else "Items in List", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = appColors.textPrimary)
                        // Live Item Counter Badge
                        Surface(shape = RoundedCornerShape(8.dp), color = appColors.accentContainer.copy(alpha = 0.5f)) {
                            Text(text = if (isBangla) "$countStr টি পণ্য যুক্ত" else "$countStr items added", style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"), color = appColors.accent, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
                itemsIndexed(manualItems.value, key = { idx, itm -> "${itm.name}_$idx" }) { idx, itm ->
                    Surface(shape = RoundedCornerShape(10.dp), color = appColors.cardBackground, border = BorderStroke(HairlineInkStroke, appColors.divider), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f, fill = false)) {
                                // Serial number badge
                                val serialNum = if (isBangla) convertToBengaliDigits((idx + 1).toString()) else (idx + 1).toString()
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(appColors.surfaceVariant), contentAlignment = Alignment.Center) { Text(text = serialNum, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"), color = appColors.textSecondary) }
                                Text(text = itm.name, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold), color = appColors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Surface(shape = RoundedCornerShape(6.dp), color = appColors.accentContainer.copy(alpha = 0.35f)) { Text(text = itm.formatQuantity(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"), color = appColors.accent, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                            }
                            IconButton(onClick = { haptics.lightTick(); manualItems.value = manualItems.value.toMutableList().apply { removeAt(idx) } }, modifier = Modifier.size(48.dp).minimumInteractiveComponentSize()) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Remove", tint = appColors.textMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            // Input Composer Row
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = appColors.cardBackground, border = BorderStroke(HairlineInkStroke, appColors.divider), modifier = Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = itemInputText, onValueChange = { itemInputText = it },
                            placeholder = { Text(text = strings.marketAddItemPlaceholder, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)) },
                            singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { if (itemInputText.isNotBlank()) addManualItem(itemInputText, selectedQuantityText) }),
                            shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f).onFocusChanged { if (it.isFocused) { coroutineScope.launch { bringIntoViewRequester.bringIntoView() } } }
                        )
                        IconButton(onClick = { if (itemInputText.isNotBlank()) addManualItem(itemInputText, selectedQuantityText) }, modifier = Modifier.size(50.dp).minimumInteractiveComponentSize()) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(appColors.accent), contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp)) }
                        }
                    }
                }
            }
            // Quick Staples Pills
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = if (isBangla) "প্রয়োজনীয় সদাই যোগ করুন" else "Add usual staples", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp), color = appColors.textSecondary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        quickStaples.forEach { (name, qty) ->
                            Surface(onClick = { addManualItem(name, qty) }, shape = RoundedCornerShape(8.dp), color = appColors.cardBackground, border = BorderStroke(HairlineInkStroke, appColors.divider), modifier = Modifier.defaultMinSize(minHeight = 48.dp).minimumInteractiveComponentSize()) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(14.dp))
                                    Text(text = "$name ($qty)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium), color = appColors.textPrimary)
                                }
                            }
                        }
                    }
                }
            }
            // Quick Quantity Presets Row
            item {
                CreateMarketListQuantitySelector(
                    selectedQuantityText = selectedQuantityText, onSelectQuantity = { selectedQuantityText = it },
                    quickQuantities = quickQuantities, isBangla = isBangla, appColors = appColors, haptics = haptics, modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                val imeVisible = WindowInsets.isImeVisible
                Spacer(modifier = Modifier.height(if (imeVisible) 16.dp else 80.dp))
            }
        }
        CreateMarketListBottomDock(
            itemCount = manualItems.value.size, isBangla = isBangla,
            onCreateClick = { onListCreated(listTitle.ifBlank { defaultTitle }, manualItems.value) },
            appColors = appColors, haptics = haptics, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
