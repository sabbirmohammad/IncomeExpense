package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CategoryColorHelper
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun AddEditCategoryDialog(
    category: Category?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (name: String, type: TransactionType, icon: String, colorHex: String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val isEditing = category != null

    val initialColor = remember(category, categories) {
        category?.colorHex ?: CategoryColorHelper.getAvailableColor(categories)
    }
    var catName by remember { mutableStateOf(category?.name ?: "") }
    var catType by remember { mutableStateOf(category?.type ?: TransactionType.EXPENSE) }
    var catIcon by remember { mutableStateOf(category?.icon ?: "ShoppingCart") }
    var catColor by remember(category) { mutableStateOf(initialColor) }
    var colorWarningMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface,
        titleContentColor = appColors.textPrimary,
        textContentColor = appColors.textSecondary,
        title = {
            Text(
                text = if (isEditing) (if (strings.isBangla) "ক্যাটাগরি সম্পাদনা" else "Edit category")
                else (if (strings.isBangla) "নতুন ক্যাটাগরি" else "New category"),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Live Category Preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(SmallElementRadius))
                        .background(appColors.surfaceVariant)
                        .border(1.dp, appColors.border, RoundedCornerShape(SmallElementRadius))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconBadge(
                        iconName = catIcon,
                        colorHex = catColor,
                        size = 40.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (catName.isBlank()) (if (strings.isBangla) "ক্যাটাগরি প্রিভিউ" else "Category preview") else catName,
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = if (strings.isBangla) (if (catType == TransactionType.INCOME) "আয় ক্যাটাগরি" else "ব্যয় ক্যাটাগরি")
                            else "${catType.name.lowercase().replaceFirstChar { it.uppercase() }} Category",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (strings.isBangla) "ক্যাটাগরির নাম" else "Category name",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = catName,
                    onValueChange = { catName = it },
                    placeholder = { Text(if (strings.isBangla) "যেমন: বাজার, জিম, রেস্তোরাঁ" else "e.g. Groceries, Gym, Dining", color = appColors.textMuted) },
                    shape = RoundedCornerShape(SmallElementRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = appColors.surfaceVariant,
                        unfocusedContainerColor = appColors.surfaceVariant,
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = appColors.border,
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (strings.isBangla) "ক্যাটাগরির ধরন" else "Category type",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillFilterChip(
                        text = strings.expense,
                        selected = catType == TransactionType.EXPENSE,
                        onClick = { catType = TransactionType.EXPENSE }
                    )
                    PillFilterChip(
                        text = strings.income,
                        selected = catType == TransactionType.INCOME,
                        onClick = { catType = TransactionType.INCOME }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                CategoryIconPickerSection(
                    selectedIcon = catIcon,
                    onIconSelected = { catIcon = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                CategoryColorPickerSection(
                    selectedColor = catColor,
                    onColorSelected = { catColor = it },
                    categories = categories,
                    editingCategoryId = category?.id,
                    colorWarningMessage = colorWarningMessage,
                    onWarningChange = { colorWarningMessage = it },
                    haptics = haptics
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (catName.isNotBlank()) {
                        val conflictingCat = CategoryColorHelper.getCategoryUsingColor(catColor, categories, category?.id)
                        if (conflictingCat != null) {
                            colorWarningMessage = strings.colorUsedByFormat(conflictingCat.getLocalizedName(strings.isBangla))
                            haptics.lightTick()
                            return@Button
                        }
                        onSave(catName.trim(), catType, catIcon, catColor)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.save, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        }
    )
}
