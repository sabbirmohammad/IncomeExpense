package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import com.example.expensetracker.data.model.MarketCostEstimationResult
import com.example.expensetracker.ui.components.LocalAppStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketListSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    marketListWithItems: MarketListWithItems?,
    currencySymbol: String = "৳",
    isBangla: Boolean = true,
    geminiApiKey: String? = null,
    onDismiss: () -> Unit,
    onCreateListWithBulk: (title: String, items: List<MarketItem>) -> Unit,
    onAddItem: (item: MarketItem) -> Unit,
    onUpdateItem: (item: MarketItem) -> Unit,
    onDeleteItem: (itemId: Long) -> Unit,
    onMarkPurchased: (item: MarketItem, amount: Double) -> Unit,
    onUnmarkPurchased: (item: MarketItem) -> Unit,
    onCompleteList: (listId: Long) -> Unit
) {
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    // Tab state: 0 = Checklist (3 columns), 1 = Bulk Paste (WhatsApp), 2 = AI Assistant
    var selectedTab by remember {
        mutableIntStateOf(if (marketListWithItems == null || marketListWithItems.items.isEmpty()) 1 else 0)
    }

    var bulkInputText by remember { mutableStateOf("") }
    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountText by remember { mutableStateOf("") }

    // Manual add item state
    var newProductName by remember { mutableStateOf("") }
    var newProductQuantity by remember { mutableStateOf(1.0) }
    var newProductUnit by remember { mutableStateOf("কেজি") }
    var showUnitDialogForNewItem by remember { mutableStateOf(false) }

    // Category filter state: null = All, "veggies", "meat_fish", "groceries", "dairy_bakery"
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }



    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LocalAppColors.current.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LocalAppColors.current.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2EC4B6).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF2EC4B6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBangla) "বাজারের ফর্দ" else "Market Checklist",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = ColorTextPrimary
                        )
                        val listDate = marketListWithItems?.list?.createdAt ?: System.currentTimeMillis()
                        val syncedDateText = remember(listDate, isBangla) {
                            DateTimeHelper.formatDayGroup(listDate, isBangla)
                        }
                        Text(
                            text = "📅 $syncedDateText",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorAccent
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = ColorTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs Capsule: [ 0: Checklist ] [ 1: Paste List ] [ 2: AI Assistant ]
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
                    // Tab 0: Checklist
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) ColorAccent else Color.Transparent)
                            .clickable {
                                haptics.lightTick()
                                selectedTab = 0
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.createList,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selectedTab == 0) Color.White else ColorTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Tab 1: Paste List
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
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentPaste,
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color.White else ColorTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = strings.pasteList,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (selectedTab == 1) Color.White else ColorTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }


                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TAB CONTENT
            if (selectedTab == 1) {
                // ==========================================
                // TAB 1: BULK PASTE (WHATSAPP / NOTES)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = if (isBangla) "হোয়াটসঅ্যাপ থেকে কপি করা পুরো লিস্টটি এখানে পেস্ট করুন:"
                        else "Paste your copied WhatsApp/Notes list below:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = bulkInputText,
                        onValueChange = { bulkInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = {
                            Text(
                                text = if (isBangla)
                                    "যেমন:\n১. আলু ২ কেজি\n২. পেঁয়াজ ১ কেজি\n৩. সয়াবিন তেল ২ লিটার\n৪. ডিম ১ ডজন\n৫. সাবান ২ পিস\n৬. মুরগি দেড় কেজি\n৭. কাঁচামরিচ ২৫০ গ্রাম"
                                else
                                    "e.g.\nPotato 2kg\nOnion 1 kg\nSoybean oil 2L\nEggs 12 pcs\nSoap 2 pcs\nChicken 1.5 kg",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = ColorTextSecondary.copy(alpha = 0.6f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF25D366),
                            unfocusedBorderColor = LocalAppColors.current.border,
                            focusedContainerColor = LocalAppColors.current.background,
                            unfocusedContainerColor = LocalAppColors.current.background
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (bulkInputText.isNotBlank()) {
                                haptics.mediumClick()
                                val parsedItems = MarketListParser.parseBulkText(bulkInputText)
                                if (parsedItems.isNotEmpty()) {
                                    val defaultTitle = DateTimeHelper.formatMarketTripTitle(System.currentTimeMillis(), isBangla)
                                    onCreateListWithBulk(defaultTitle, parsedItems)
                                    bulkInputText = ""
                                    selectedTab = 0 // Switch to checklist view
                                }
                            }
                        },
                        enabled = bulkInputText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            disabledContainerColor = Color(0xFF25D366).copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isBangla) "ফর্দ যুক্ত করুন" else "Import Market List",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // TAB 0: 3-COLUMN CHECKLIST
                // ==========================================
                val items = marketListWithItems?.items ?: emptyList()
                val totalCount = items.size
                val purchasedCount = items.count { it.isPurchased }
                val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
                val progress = if (totalCount > 0) purchasedCount.toFloat() / totalCount.toFloat() else 0f

                // Summary Progress Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LocalAppColors.current.background,
                    border = BorderStroke(1.dp, LocalAppColors.current.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla)
                                    "কেনা সম্পন্ন: ${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি"
                                else
                                    "Purchased: $purchasedCount/$totalCount items",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorTextPrimary
                            )

                            Text(
                                text = "${if (isBangla) "মোট খরচ: " else "Total Spent: "}${formatCurrency(totalSpent, currencySymbol)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ColorSuccessIncome
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ColorSuccessIncome,
                            trackColor = LocalAppColors.current.border
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Chips
                val categories = listOf(
                    null to (if (isBangla) "সব" else "All"),
                    "veggies" to strings.categoryVeggies,
                    "meat_fish" to strings.categoryMeatFish,
                    "groceries" to strings.categoryGroceries,
                    "dairy_bakery" to strings.categoryDairyBakery
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { (catKey, label) ->
                        val isSelected = selectedCategoryFilter == catKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ColorAccent.copy(alpha = 0.15f) else LocalAppColors.current.background,
                            border = BorderStroke(1.dp, if (isSelected) ColorAccent else LocalAppColors.current.border),
                            modifier = Modifier.clickable {
                                haptics.lightTick()
                                selectedCategoryFilter = catKey
                            }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) ColorAccent else ColorTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Table Column Header (Clean & Professional)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LocalAppColors.current.background.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = strings.marketColItem,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = ColorTextSecondary,
                            modifier = Modifier.weight(1.3f)
                        )
                        Text(
                            text = strings.marketColQty,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = ColorTextSecondary,
                            modifier = Modifier.weight(1.1f)
                        )
                        Row(
                            modifier = Modifier.weight(1.2f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = strings.marketColPrice,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = ColorTextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = strings.marketColDone,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = ColorTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(28.dp)) // space for delete action
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Filtered Items List
                val displayedItems = remember(items, selectedCategoryFilter) {
                    if (selectedCategoryFilter == null) items
                    else items.filter { getMarketItemCategory(it.name) == selectedCategoryFilter }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(displayedItems, key = { _, itm -> itm.id }) { _, item ->
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
                                        // Prompt for amount
                                        promptAmountForItem = item
                                        promptAmountText = ""
                                    }
                                }
                            },
                            onDeleteClick = {
                                haptics.lightTick()
                                onDeleteItem(item.id)
                            }
                        )
                    }

                    // Manual Add New Row at bottom of list
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LocalAppColors.current.background,
                            border = BorderStroke(1.dp, LocalAppColors.current.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Product Name Input
                                OutlinedTextField(
                                    value = newProductName,
                                    onValueChange = { newProductName = it },
                                    placeholder = {
                                        Text(
                                            text = if (isBangla) "নতুন পণ্য..." else "New item...",
                                            fontSize = 12.sp,
                                            color = ColorTextSecondary
                                        )
                                    },
                                    modifier = Modifier.weight(1.3f),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ColorAccent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    )
                                )

                                // Unit & Qty chip selector
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = LocalAppColors.current.surface,
                                    border = BorderStroke(1.dp, LocalAppColors.current.border),
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .clickable { showUnitDialogForNewItem = true }
                                ) {
                                    Text(
                                        text = "${if (newProductQuantity % 1.0 == 0.0) newProductQuantity.toInt() else newProductQuantity} $newProductUnit",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = ColorAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Add button
                                IconButton(
                                    onClick = {
                                        if (newProductName.isNotBlank()) {
                                            haptics.lightTick()
                                            val newItem = MarketItem(
                                                listId = marketListWithItems?.list?.id ?: 0L,
                                                name = newProductName.trim(),
                                                quantity = newProductQuantity,
                                                unit = newProductUnit
                                            )
                                            onAddItem(newItem)
                                            newProductName = ""
                                            newProductQuantity = 1.0
                                            newProductUnit = "কেজি"
                                        }
                                    },
                                    enabled = newProductName.isNotBlank(),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (newProductName.isNotBlank()) ColorAccent else LocalAppColors.current.border)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add Item",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Finish / Complete Shopping Trip Button
                if (items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            haptics.mediumClick()
                            marketListWithItems?.list?.id?.let { onCompleteList(it) }
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorSuccessIncome)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = if (isBangla) "✓ বাজার সম্পন্ন করুন" else "✓ Finish Shopping",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Unit & Quantity Popup for existing item
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

    // Unit & Quantity Popup for new item
    if (showUnitDialogForNewItem) {
        UnitAndQuantityDialog(
            initialQuantity = newProductQuantity,
            initialUnit = newProductUnit,
            isBangla = isBangla,
            onDismiss = { showUnitDialogForNewItem = false },
            onConfirm = { qty, unit ->
                newProductQuantity = qty
                newProductUnit = unit
                showUnitDialogForNewItem = false
            }
        )
    }

    // Prompt for Amount Dialog when ticking an item with no price
    if (promptAmountForItem != null) {
        val targetItem = promptAmountForItem!!
        Dialog(onDismissRequest = { promptAmountForItem = null }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = LocalAppColors.current.surface,
                border = BorderStroke(1.dp, LocalAppColors.current.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "${targetItem.name} (${targetItem.formatQuantity()})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) "পণ্যটি কেনার জন্য কত টাকা খরচ হয়েছে লিখুন:" else "Enter the purchase amount:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = promptAmountText,
                        onValueChange = { promptAmountText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = {
                            Text(
                                text = currencySymbol,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ColorAccent
                                )
                            )
                        },
                        placeholder = { Text(if (isBangla) "০.০০" else "0.00") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = LocalAppColors.current.border,
                            focusedContainerColor = LocalAppColors.current.background,
                            unfocusedContainerColor = LocalAppColors.current.background
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { promptAmountForItem = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = if (isBangla) "বাতিল" else "Cancel")
                        }

                        Button(
                            onClick = {
                                val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(promptAmountText)
                                val amt = cleanNum.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    haptics.mediumClick()
                                    onMarkPurchased(targetItem, amt)
                                    promptAmountForItem = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                        ) {
                            Text(text = if (isBangla) "যুক্ত করুন" else "Log Expense")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3-Column Item Row Component:
 * Col 1: Product Name (line-through if purchased)
 * Col 2: Quantity & Unit clickable pill (triggers popup)
 * Col 3: Amount field + Tick checkmark button (auto adds to expense)
 */
@Composable
fun MarketItemRow(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onUnitClick: () -> Unit,
    onAmountChanged: (Double) -> Unit,
    onTickClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var amountText by remember(item.amount) {
        mutableStateOf(item.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (item.isPurchased) ColorSuccessIncome.copy(alpha = 0.06f) else LocalAppColors.current.surface,
        border = BorderStroke(
            1.dp,
            if (item.isPurchased) ColorSuccessIncome.copy(alpha = 0.3f) else LocalAppColors.current.border
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Column 1: Product Name
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.5.sp,
                    fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (item.isPurchased) ColorTextSecondary else ColorTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1.3f)
            )

            // Column 2: Quantity & Unit Pill (Click to open Unit popup)
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (item.isPurchased) LocalAppColors.current.background
                        else ColorAccent.copy(alpha = 0.12f)
                    )
                    .clickable(enabled = !item.isPurchased) { onUnitClick() }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = item.formatQuantity(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (item.isPurchased) ColorTextSecondary else ColorAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!item.isPurchased) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            // Column 3: Amount field + Tick checkmark
            Row(
                modifier = Modifier.weight(1.2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (item.isPurchased) {
                    Text(
                        text = formatCurrency(item.amount ?: 0.0, currencySymbol),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorSuccessIncome,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(it)
                            cleanNum.toDoubleOrNull()?.let { amt -> onAmountChanged(amt) }
                        },
                        placeholder = {
                            Text(
                                text = currencySymbol,
                                fontSize = 11.sp,
                                color = ColorTextSecondary
                            )
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorTextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = LocalAppColors.current.border,
                            focusedContainerColor = LocalAppColors.current.background,
                            unfocusedContainerColor = LocalAppColors.current.background
                        )
                    )
                }

                // Tick Checkmark button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isPurchased) ColorSuccessIncome
                            else LocalAppColors.current.background
                        )
                        .border(
                            width = 1.dp,
                            color = if (item.isPurchased) ColorSuccessIncome else ColorBorder,
                            shape = CircleShape
                        )
                        .clickable { onTickClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Mark Purchased",
                        tint = if (item.isPurchased) Color.White else ColorTextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Delete Action Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Delete",
                    tint = ColorDangerExpense.copy(alpha = 0.7f),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

/**
 * Heuristic classifier to categorize market items into standard grocery departments:
 * - veggies (শাকসবজি, কাঁচাবাজার)
 * - meat_fish (মাছ, মাংস)
 * - dairy_bakery (দুধ, ডিম, বেকারি)
 * - groceries (চাল, ডাল, তেল, মশলা ও অন্যান্য মুদি)
 */
private fun getMarketItemCategory(name: String): String {
    val lower = name.lowercase()
    return when {
        lower.contains("শাক") || lower.contains("আলু") || lower.contains("পেঁয়াজ") || lower.contains("মরিচ") ||
        lower.contains("টমেটো") || lower.contains("বেগুন") || lower.contains("পটল") || lower.contains("লাউ") ||
        lower.contains("কুমড়া") || lower.contains("শসা") || lower.contains("গাজর") || lower.contains("রসুন") ||
        lower.contains("আদা") || lower.contains("লেবু") || lower.contains("সবজি") || lower.contains("কলা") ||
        lower.contains("ধনেপাতা") || lower.contains("কাঁচা") || lower.contains("vegetable") || lower.contains("potato") ||
        lower.contains("onion") || lower.contains("tomato") || lower.contains("chilli") -> "veggies"

        lower.contains("মাছ") || lower.contains("মাংস") || lower.contains("মুরগি") || lower.contains("গরু") ||
        lower.contains("খাসি") || lower.contains("ইলিশ") || lower.contains("রুই") || lower.contains("কাতলা") ||
        lower.contains("চিংড়ি") || lower.contains("fish") || lower.contains("meat") || lower.contains("chicken") ||
        lower.contains("beef") || lower.contains("mutton") || lower.contains("prawn") || lower.contains("shrimp") -> "meat_fish"

        lower.contains("দুধ") || lower.contains("ডিম") || lower.contains("দই") || lower.contains("মাখন") ||
        lower.contains("পনির") || lower.contains("রুটি") || lower.contains("বিস্কুট") || lower.contains("কেক") ||
        lower.contains("milk") || lower.contains("egg") || lower.contains("bread") || lower.contains("butter") ||
        lower.contains("cheese") || lower.contains("yogurt") || lower.contains("biscuit") -> "dairy_bakery"

        else -> "groceries"
    }
}

