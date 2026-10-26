package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun DashboardMarketItemRow(
    item: MarketItem,
    currencySymbol: String,
    onAmountChanged: (Double?) -> Unit,
    onMarkPurchased: (Double) -> Unit,
    onUnmarkPurchased: () -> Unit,
    onRowClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var inlineAmountText by remember(item.amount) {
        mutableStateOf(item.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (item.isPurchased) appColors.income.copy(alpha = 0.05f) else LocalAppColors.current.background,
        border = BorderStroke(
            1.dp,
            if (item.isPurchased) appColors.income.copy(alpha = 0.25f) else LocalAppColors.current.border
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Tick / Check circle
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isPurchased) appColors.income else LocalAppColors.current.surface
                        )
                        .border(
                            width = 1.dp,
                            color = if (item.isPurchased) appColors.income else LocalAppColors.current.border,
                            shape = CircleShape
                        )
                        .clickable {
                            if (item.isPurchased) {
                                haptics.lightTick()
                                onUnmarkPurchased()
                            } else {
                                val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(inlineAmountText)
                                val amt = cleanNum.toDoubleOrNull()
                                if (amt != null && amt > 0) {
                                    haptics.mediumClick()
                                    onMarkPurchased(amt)
                                } else {
                                    onRowClick()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isPurchased) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Name & Qty
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (item.isPurchased) appColors.textSecondary else appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Qty badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (item.isPurchased) LocalAppColors.current.surface else appColors.accent.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = item.formatQuantity(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (item.isPurchased) appColors.textSecondary else appColors.accent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Price display or entry
            if (item.isPurchased) {
                Text(
                    text = formatCurrency(item.amount ?: 0.0, currencySymbol),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.income
                )
            } else {
                OutlinedTextField(
                    value = inlineAmountText,
                    onValueChange = {
                        inlineAmountText = it
                        val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(it)
                        val amt = cleanNum.toDoubleOrNull()
                        onAmountChanged(amt)
                    },
                    placeholder = { Text(currencySymbol, fontSize = 11.sp, color = appColors.textSecondary) },
                    modifier = Modifier.width(72.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedContainerColor = LocalAppColors.current.surface,
                        unfocusedContainerColor = LocalAppColors.current.surface
                    )
                )
            }
        }
    }
}
