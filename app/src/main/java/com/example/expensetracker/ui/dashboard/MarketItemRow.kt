package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.formatCurrency

/**
 * Modern OneUI-Style Checklist Task Item Card:
 * - Left: Circular Checkbox for instant completion
 * - Center: Product Name with Quantity pill underneath
 * - Right: Price chip/input field and subtle delete action
 */
@Composable
fun MarketItemRow(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onUnitClick: () -> Unit,
    onAmountChanged: (Double) -> Unit,
    onTickClick: () -> Unit,
    onDeleteClick: () -> Unit,
    lastRateText: String? = null
) {
    val appColors = LocalAppColors.current
    var amountText by remember(item.amount) {
        mutableStateOf(item.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (item.isPurchased) appColors.income.copy(alpha = 0.08f) else appColors.surface,
        border = BorderStroke(
            1.dp,
            if (item.isPurchased) appColors.income.copy(alpha = 0.25f) else appColors.border
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Sleek Circular Checkbox
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .clickable { onTickClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isPurchased) appColors.income else Color.Transparent
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (item.isPurchased) appColors.income else appColors.border,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isPurchased) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Purchased",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // 2. Product Name & Quantity Pill (Center Column)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = !item.isPurchased) { onUnitClick() },
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.5.sp,
                        fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (item.isPurchased) appColors.textSecondary else appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Quantity badge & last rate
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (item.isPurchased) appColors.background
                                else appColors.accent.copy(alpha = 0.10f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.formatQuantity(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (item.isPurchased) appColors.textSecondary else appColors.accent
                        )
                        if (!item.isPurchased) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit quantity",
                                tint = appColors.accent.copy(alpha = 0.8f),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    if (!item.isPurchased && lastRateText != null) {
                        Text(
                            text = lastRateText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = appColors.textSecondary.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 3. Amount Field / Display (Compact height: 32dp, comfortable width: 96dp)
            if (item.isPurchased) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.income.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, appColors.income.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .width(96.dp)
                        .height(32.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatCurrency(item.amount ?: 0.0, currencySymbol),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = appColors.income
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.background,
                    border = BorderStroke(
                        1.dp,
                        if (amountText.isNotEmpty()) appColors.accent else appColors.border
                    ),
                    modifier = Modifier
                        .width(96.dp)
                        .height(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (amountText.isEmpty()) {
                            Text(
                                text = if (isBangla) "৳ দাম" else "$ Price",
                                fontSize = 11.5.sp,
                                color = appColors.textSecondary.copy(alpha = 0.6f)
                            )
                        }
                        BasicTextField(
                            value = amountText,
                            onValueChange = {
                                amountText = it
                                if (it.isBlank()) {
                                    onAmountChanged(0.0)
                                } else {
                                    val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(it)
                                    cleanNum.toDoubleOrNull()?.let { amt -> onAmountChanged(amt) }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = appColors.textPrimary
                            ),
                            cursorBrush = SolidColor(appColors.accent),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 4. Subtle Delete Button
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete",
                        tint = appColors.expense.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
