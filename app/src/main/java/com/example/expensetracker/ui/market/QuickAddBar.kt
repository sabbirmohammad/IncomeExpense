package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * High-density Quick-Add Bar for Bazar Lists (Target Design B3):
 * - Always visible under summary header.
 * - Single text field: "Add item…" / "আইটেম যোগ করুন…".
 * - Live autocomplete chips from price history / master catalog (name, last price, usual unit).
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
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var inputText by remember { mutableStateOf("") }
    val isBangla = strings.isBangla

    // Autocomplete candidate suggestions matching query
    val suggestions = remember(inputText, productPriceInsights, masterProducts, stapleProducts) {
        val q = inputText.trim()
        if (q.length < 1) {
            emptyList()
        } else {
            val key = UnitConverterEngine.canonicalCommodityKey(q)
            // Combine insights + masterProducts + staples
            val pool = mutableMapOf<String, Pair<Double?, String>>() // name -> (price, unit)
            for ((name, insight) in productPriceInsights) {
                pool[name] = Pair(insight.currentUnitPrice, insight.baseUnit)
            }
            for (mp in masterProducts) {
                if (!pool.containsKey(mp.name)) {
                    val rate = lastPurchasedRates[mp.name.lowercase().trim()]
                    val price = if (mp.latestUnitPrice > 0) mp.latestUnitPrice else rate?.first
                    pool[mp.name] = Pair(price, mp.defaultUnit)
                }
            }
            for (sp in stapleProducts) {
                if (!pool.containsKey(sp.name)) {
                    val rate = lastPurchasedRates[sp.name.lowercase().trim()]
                    val price = if (sp.latestUnitPrice > 0) sp.latestUnitPrice else rate?.first
                    pool[sp.name] = Pair(price, sp.defaultUnit)
                }
            }

            pool.entries.filter { (name, _) ->
                val candKey = UnitConverterEngine.canonicalCommodityKey(name)
                candKey.contains(key, ignoreCase = true) || name.contains(q, ignoreCase = true)
            }.take(6).map { (name, meta) ->
                Triple(name, meta.first, meta.second)
            }
        }
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
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = appColors.surface,
            border = BorderStroke(HairlineInkStroke, appColors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick-add text field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = strings.marketAddItemPlaceholder,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = appColors.textMuted
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (inputText.isNotBlank()) {
                                submitItem(inputText, null, null)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = appColors.accent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = appColors.textPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Add button (48dp hit area)
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            submitItem(inputText, null, null)
                        }
                    },
                    enabled = inputText.isNotBlank(),
                    modifier = Modifier
                        .size(44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank()) appColors.accent
                                else appColors.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = if (isBangla) "যোগ করুন" else "Add",
                            tint = if (inputText.isNotBlank()) Color.White else appColors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // WhatsApp/SMS bulk paste trigger (48dp hit area)
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onOpenPasteSheet()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(appColors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentPaste,
                            contentDescription = strings.marketPasteFromWhatsApp,
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        // Autocomplete suggestions strip
        AnimatedVisibility(
            visible = suggestions.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(suggestions, key = { it.first }) { (sugName, price, unit) ->
                    val priceLabel = if (price != null && price > 0) {
                        val pStr = if (price % 1.0 == 0.0) price.toInt().toString() else "%.1f".format(price)
                        val pDigits = if (isBangla) convertToBengaliDigits(pStr) else pStr
                        "৳$pDigits/$unit"
                    } else unit

                    Surface(
                        onClick = {
                            submitItem(sugName, 1.0, unit)
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = appColors.accentContainer.copy(alpha = 0.35f),
                        border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 36.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = sugName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = "($priceLabel)",
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
    }
}

// =========================================================================
// PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "QuickAddBar - Dark En", showBackground = true)
@Composable
fun PreviewQuickAddBarDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                QuickAddBar(
                    onAddItem = {},
                    onIncrementExistingItem = { _, _ -> },
                    existingItems = emptyList(),
                    productPriceInsights = emptyMap(),
                    onOpenPasteSheet = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "QuickAddBar - Light Bn", showBackground = true)
@Composable
fun PreviewQuickAddBarLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                QuickAddBar(
                    onAddItem = {},
                    onIncrementExistingItem = { _, _ -> },
                    existingItems = emptyList(),
                    productPriceInsights = emptyMap(),
                    onOpenPasteSheet = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(name = "QuickAddBar - AMOLED Bn", showBackground = true)
@Composable
fun PreviewQuickAddBarAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                QuickAddBar(
                    onAddItem = {},
                    onIncrementExistingItem = { _, _ -> },
                    existingItems = emptyList(),
                    productPriceInsights = emptyMap(),
                    onOpenPasteSheet = {},
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
