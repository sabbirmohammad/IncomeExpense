package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketAddNewItemRow(
    listId: Long,
    isBangla: Boolean,
    onAddItem: (MarketItem) -> Unit,
    modifier: Modifier = Modifier,
    existingItemNames: Set<String> = emptySet(),
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    stapleProducts: List<com.example.expensetracker.data.model.MasterProduct> = emptyList(),
    currencySymbol: String = "৳"
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var newProductName by remember { mutableStateOf("") }
    var newProductQuantity by remember { mutableDoubleStateOf(1.0) }
    var newProductUnit by remember(isBangla) { mutableStateOf(if (isBangla) "কেজি" else "kg") }
    var showUnitDialogForNewItem by remember { mutableStateOf(false) }

    val defaultUnit = if (isBangla) "কেজি" else "kg"
    val submitNewItem = {
        if (newProductName.isNotBlank()) {
            haptics.lightTick()
            val text = newProductName.trim()
            val parsed = MarketListParser.parseSingleLine(text)
            val isCustomQuantityOrUnit = newProductQuantity != 1.0 || newProductUnit != defaultUnit
            val finalName = if (isCustomQuantityOrUnit) text else (parsed?.name ?: text)
            val finalQty = if (isCustomQuantityOrUnit) newProductQuantity else (parsed?.quantity ?: newProductQuantity)
            val finalUnit = if (isCustomQuantityOrUnit) newProductUnit else (parsed?.unit ?: newProductUnit)

            val newItem = MarketItem(
                listId = listId,
                name = finalName,
                quantity = finalQty,
                unit = finalUnit
            )
            onAddItem(newItem)
            newProductName = ""
            newProductQuantity = 1.0
            newProductUnit = defaultUnit
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LocalAppColors.current.surface,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Product Name Input
                OutlinedTextField(
                    value = newProductName,
                    onValueChange = { newProductName = it },
                    placeholder = {
                        Text(
                            text = if (isBangla) "নতুন পণ্য লিখুন (যেমন: আলু)..." else "New item (e.g. Potato)...",
                            fontSize = 13.sp,
                            color = appColors.textSecondary.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submitNewItem() }),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Unit & Qty selector chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LocalAppColors.current.background,
                    border = BorderStroke(1.dp, LocalAppColors.current.border),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { showUnitDialogForNewItem = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val qtyRaw = if (newProductQuantity % 1.0 == 0.0) newProductQuantity.toInt().toString() else newProductQuantity.toString()
                        val qtyDisplay = if (isBangla) convertToBengaliDigits(qtyRaw) else qtyRaw
                        Text(
                            text = "$qtyDisplay $newProductUnit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.accent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Add button
                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                        .clickable(enabled = newProductName.isNotBlank()) {
                            submitNewItem()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (newProductName.isNotBlank()) appColors.accent else LocalAppColors.current.border.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Item",
                            tint = if (newProductName.isNotBlank()) Color.White else appColors.textSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        // Smart live price estimation preview while typing!
        val matchedInsight = remember(newProductName, productPriceInsights) {
            UnitConverterEngine.findMatchingInsight(newProductName, productPriceInsights)
        }

        if (matchedInsight != null) {
            val estimatedCost = UnitConverterEngine.estimateCost(
                matchedInsight.currentUnitPrice,
                newProductQuantity,
                newProductUnit
            )
            val estFormatted = if (estimatedCost % 1.0 == 0.0) estimatedCost.toLong().toString() else "%.0f".format(estimatedCost)
            val estText = if (isBangla) com.example.expensetracker.ui.components.convertToBengaliDigits(estFormatted) else estFormatted

            val rateStr = if (matchedInsight.currentUnitPrice % 1.0 == 0.0) {
                matchedInsight.currentUnitPrice.toLong().toString()
            } else {
                "%.1f".format(matchedInsight.currentUnitPrice)
            }
            val rateNum = if (isBangla) com.example.expensetracker.ui.components.convertToBengaliDigits(rateStr) else rateStr

            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (matchedInsight.isPriceUp && matchedInsight.percentDiff >= 20.0) appColors.expense.copy(alpha = 0.08f) else appColors.accent.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, if (matchedInsight.isPriceUp && matchedInsight.percentDiff >= 20.0) appColors.expense.copy(alpha = 0.35f) else appColors.accent.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "পূর্বের দর: $currencySymbol$rateNum/${matchedInsight.baseUnit}" else "Last rate: $currencySymbol$rateNum/${matchedInsight.baseUnit}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                        Text(
                            text = if (isBangla) "সম্ভাব্য: $currencySymbol$estText" else "Est: $currencySymbol$estText",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp),
                            color = appColors.accent
                        )
                    }

                    // Price Spike Warning Alert (No sparkles, clean warning)
                    if (matchedInsight.isPriceUp && matchedInsight.percentDiff >= 20.0) {
                        val pctFormatted = if (matchedInsight.percentDiff % 1.0 == 0.0) matchedInsight.percentDiff.toInt().toString() else "%.0f".format(matchedInsight.percentDiff)
                        val pctNum = if (isBangla) convertToBengaliDigits(pctFormatted) else pctFormatted
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "সতর্কতা: গতবারের চেয়ে দর $pctNum% বেশি ছিল" else "Notice: Prior rate jumped by $pctNum%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.expense
                        )
                    }
                }
            }
        }

        // Quick Suggestions Row (Powered by Master Product Staples & Frequent Items)
        val quickSuggestions = remember(isBangla, stapleProducts) {
            if (stapleProducts.isNotEmpty()) {
                stapleProducts.map { mp ->
                    val defaultQty = when (mp.defaultUnit) {
                        "গ্রাম", "gm", "g" -> 250.0
                        "ডজন", "dozen" -> 1.0
                        else -> 1.0
                    }
                    Triple(mp.name, defaultQty, mp.defaultUnit)
                }
            } else if (isBangla) {
                listOf(
                    Triple("আলু", 1.0, "কেজি"),
                    Triple("পেঁয়াজ", 1.0, "কেজি"),
                    Triple("কাঁচামরিচ", 250.0, "গ্রাম"),
                    Triple("ডিম", 1.0, "ডজন"),
                    Triple("সয়াবিন তেল", 2.0, "লিটার"),
                    Triple("রসুন", 250.0, "গ্রাম"),
                    Triple("আদা", 250.0, "গ্রাম"),
                    Triple("চাল", 5.0, "কেজি"),
                    Triple("মসুর ডাল", 1.0, "কেজি"),
                    Triple("চিনি", 1.0, "কেজি"),
                    Triple("লবণ", 1.0, "কেজি"),
                    Triple("তরল দুধ", 1.0, "লিটার"),
                    Triple("মুরগি", 1.5, "কেজি")
                )
            } else {
                listOf(
                    Triple("Potato", 1.0, "kg"),
                    Triple("Onion", 1.0, "kg"),
                    Triple("Green Chilli", 250.0, "gm"),
                    Triple("Eggs", 12.0, "pcs"),
                    Triple("Soybean Oil", 2.0, "L"),
                    Triple("Garlic", 250.0, "gm"),
                    Triple("Ginger", 250.0, "gm"),
                    Triple("Rice", 5.0, "kg"),
                    Triple("Lentils", 1.0, "kg"),
                    Triple("Sugar", 1.0, "kg"),
                    Triple("Salt", 1.0, "kg"),
                    Triple("Milk", 1.0, "L"),
                    Triple("Chicken", 1.5, "kg")
                )
            }
        }

        val availableSuggestions = remember(quickSuggestions, existingItemNames) {
            quickSuggestions.filter { (name, _, _) -> !existingItemNames.contains(name.lowercase()) }
        }

        if (availableSuggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableSuggestions) { (sugName, sugQty, sugUnit) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, appColors.border),
                        modifier = Modifier.clickable {
                            haptics.lightTick()
                            onAddItem(MarketItem(listId = listId, name = sugName, quantity = sugQty, unit = sugUnit))
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "$sugName (${if (sugQty % 1.0 == 0.0) sugQty.toInt() else sugQty} $sugUnit)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = appColors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }

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
}
