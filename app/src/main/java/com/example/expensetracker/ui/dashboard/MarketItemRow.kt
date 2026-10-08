package com.example.expensetracker.ui.dashboard

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.formatCurrency

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
