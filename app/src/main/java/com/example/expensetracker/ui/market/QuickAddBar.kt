package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * High-density Quick-Add Bar for Bazar Lists (Target Design B3):
 * - Always visible under summary header.
 * - Single text field: "Add item…" / "আইটেম যোগ করুন…".
 * - Live autocomplete chips from price history / master catalog.
 * - Enter or tap '+' adds the item in 1 action.
 * - Paste icon button opens WhatsApp/SMS parser sheet.
 * - Adding an existing name increments quantity instead of duplicating.
 */
@Composable
fun QuickAddBar(
    onAddItem: (MarketItem) -> Unit,
    onIncrementExistingItem: (MarketItem, Double) -> Unit,
    existingItems: List<MarketItem>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    masterProducts: List<MasterProduct> = emptyList(),
    stapleProducts: List<MasterProduct> = emptyList(),
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(),
    onOpenPasteSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var inputText by remember { mutableStateOf("") }
    val isBangla = strings.isBangla

    val suggestions = remember(inputText, productPriceInsights, masterProducts, stapleProducts) {
        filterAutocompleteSuggestions(
            query = inputText,
            productPriceInsights = productPriceInsights,
            masterProducts = masterProducts,
            stapleProducts = stapleProducts,
            lastPurchasedRates = lastPurchasedRates
        )
    }

    val submitItem: (String, Double?, String?) -> Unit = { rawText, forcedQty, forcedUnit ->
        val trimmed = rawText.trim()
        if (trimmed.isNotBlank()) {
            haptics.mediumClick()
            val parsed = MarketListParser.parseSingleLine(trimmed)
            val name = parsed?.name ?: trimmed
            val qty = forcedQty ?: parsed?.quantity ?: 1.0
            val unit = forcedUnit ?: parsed?.unit ?: run {
                val matched = UnitConverterEngine.findMatchingInsight(name, productPriceInsights)
                matched?.baseUnit ?: lastPurchasedRates[name.lowercase().trim()]?.second ?: "কেজি"
            }

            val canonicalKey = UnitConverterEngine.canonicalCommodityKey(name)
            val existing = existingItems.find {
                UnitConverterEngine.canonicalCommodityKey(it.name) == canonicalKey
            }

            if (existing != null) {
                onIncrementExistingItem(existing, qty)
            } else {
                onAddItem(
                    MarketItem(
                        name = name,
                        quantity = qty,
                        unit = unit,
                        isPurchased = false
                    )
                )
            }
            inputText = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        QuickAddInputField(
            inputText = inputText,
            onInputTextChange = { inputText = it },
            onSubmit = {
                submitItem(inputText, null, null)
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            onOpenPasteSheet = onOpenPasteSheet
        )

        QuickAddSuggestionsStrip(
            suggestions = suggestions,
            isBangla = isBangla,
            onSuggestionClick = { name, _, unit ->
                submitItem(name, 1.0, unit)
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        )
    }
}
