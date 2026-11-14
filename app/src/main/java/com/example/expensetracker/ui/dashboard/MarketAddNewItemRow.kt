package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import com.example.expensetracker.data.model.MasterProduct
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
    stapleProducts: List<MasterProduct> = emptyList(),
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
            val isCustom = newProductQuantity != 1.0 || newProductUnit != defaultUnit
            val finalName = if (isCustom) text else (parsed?.name ?: text)
            val finalQty = if (isCustom) newProductQuantity else (parsed?.quantity ?: newProductQuantity)
            val finalUnit = if (isCustom) newProductUnit else (parsed?.unit ?: newProductUnit)
            onAddItem(MarketItem(listId = listId, name = finalName, quantity = finalQty, unit = finalUnit))
            newProductName = ""
            newProductQuantity = 1.0
            newProductUnit = defaultUnit
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = appColors.surface,
            border = BorderStroke(1.dp, appColors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.background,
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.minimumInteractiveComponentSize().clickable { showUnitDialogForNewItem = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val qtyRaw = if (newProductQuantity % 1.0 == 0.0) newProductQuantity.toInt().toString() else newProductQuantity.toString()
                        val qtyDisplay = if (isBangla) convertToBengaliDigits(qtyRaw) else qtyRaw
                        Text(text = "$qtyDisplay $newProductUnit", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum"), color = appColors.accent, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Icon(Icons.Filled.KeyboardArrowDown, null, tint = appColors.accent, modifier = Modifier.size(13.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                        .clickable(enabled = newProductName.isNotBlank()) { submitNewItem() },
                    contentAlignment = Alignment.Center
                ) {
                    val addBg = if (newProductName.isNotBlank()) appColors.accent else appColors.border.copy(alpha = 0.4f)
                    val addTint = if (newProductName.isNotBlank()) Color.White else appColors.textSecondary
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(addBg), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Add, "Add Item", tint = addTint, modifier = Modifier.size(17.dp))
                    }
                }
            }
        }

        val matchedInsight = remember(newProductName, productPriceInsights) {
            UnitConverterEngine.findMatchingInsight(newProductName, productPriceInsights)
        }
        if (matchedInsight != null) {
            val estimatedCost = UnitConverterEngine.estimateCost(
                matchedInsight.currentUnitPrice,
                newProductQuantity,
                newProductUnit
            )
            MarketLivePriceEstimatePreview(
                matchedInsight = matchedInsight,
                estimatedCost = estimatedCost,
                currencySymbol = currencySymbol,
                isBangla = isBangla
            )
        }

        val quickSuggestions = remember(isBangla, stapleProducts) {
            getMarketQuickSuggestions(isBangla, stapleProducts)
        }
        MarketQuickSuggestionsRow(
            quickSuggestions = quickSuggestions,
            existingItemNames = existingItemNames,
            listId = listId,
            onAddItem = onAddItem
        )
    }

    if (showUnitDialogForNewItem) {
        UnitAndQuantityDialog(
            initialQuantity = newProductQuantity, initialUnit = newProductUnit, isBangla = isBangla,
            onDismiss = { showUnitDialogForNewItem = false },
            onConfirm = { qty, unit ->
                newProductQuantity = qty
                newProductUnit = unit
                showUnitDialogForNewItem = false
            }
        )
    }
}
