package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.rememberAppHaptics

fun getMarketQuickSuggestions(
    isBangla: Boolean,
    stapleProducts: List<MasterProduct>
): List<Triple<String, Double, String>> {
    if (stapleProducts.isNotEmpty()) {
        return stapleProducts.map { mp ->
            val defaultQty = when (mp.defaultUnit) {
                "গ্রাম", "gm", "g" -> 250.0
                "ডজন", "dozen" -> 1.0
                else -> 1.0
            }
            Triple(mp.name, defaultQty, mp.defaultUnit)
        }
    }
    return if (isBangla) {
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

@Composable
fun MarketQuickSuggestionsRow(
    quickSuggestions: List<Triple<String, Double, String>>,
    existingItemNames: Set<String>,
    listId: Long,
    onAddItem: (MarketItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val availableSuggestions = remember(quickSuggestions, existingItemNames) {
        quickSuggestions.filter { (name, _, _) -> !existingItemNames.contains(name.lowercase()) }
    }

    if (availableSuggestions.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            items(availableSuggestions) { (sugName, sugQty, sugUnit) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 32.dp)
                        .clickable {
                            haptics.lightTick()
                            onAddItem(MarketItem(listId = listId, name = sugName, quantity = sugQty, unit = sugUnit))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
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
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
