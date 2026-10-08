package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketAddNewItemRow(
    listId: Long,
    isBangla: Boolean,
    onAddItem: (MarketItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    var newProductName by remember { mutableStateOf("") }
    var newProductQuantity by remember { mutableDoubleStateOf(1.0) }
    var newProductUnit by remember { mutableStateOf("কেজি") }
    var showUnitDialogForNewItem by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = LocalAppColors.current.background,
        border = BorderStroke(1.dp, LocalAppColors.current.border),
        modifier = modifier.fillMaxWidth()
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
                            listId = listId,
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
