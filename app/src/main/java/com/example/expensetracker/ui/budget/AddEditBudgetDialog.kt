package com.example.expensetracker.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBudgetDialog(
    budgetToEdit: Budget?,
    expenseCategories: List<Category>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Budget) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    var budgetAmountText by remember {
        mutableStateOf(budgetToEdit?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var selectedCatId by remember {
        mutableStateOf(budgetToEdit?.categoryId)
    }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = appColors.surfaceVariant,
        unfocusedContainerColor = appColors.surfaceVariant,
        focusedBorderColor = appColors.border,
        unfocusedBorderColor = appColors.border,
        focusedTextColor = appColors.textPrimary,
        unfocusedTextColor = appColors.textPrimary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface,
        titleContentColor = appColors.textPrimary,
        textContentColor = appColors.textSecondary,
        title = {
            Text(
                text = if (budgetToEdit != null)
                    (if (strings.isBangla) "বাজেট সম্পাদনা" else "Edit budget")
                else
                    (if (strings.isBangla) "বাজেট সীমা নির্ধারণ" else "Set budget limit"),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = if (strings.isBangla) "বাজেটের লক্ষ্য" else "Budget target",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    val selectedName = if (selectedCatId == null) {
                        strings.overallMonthlyBudget
                    } else {
                        expenseCategories.find { it.id == selectedCatId }?.getLocalizedName(strings.isBangla)
                            ?: if (strings.isBangla) "ক্যাটাগরি নির্বাচন করুন" else "Select category"
                    }

                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(SmallElementRadius),
                        colors = textFieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.background(appColors.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings.overallMonthlyBudget, color = appColors.textPrimary) },
                            onClick = {
                                selectedCatId = null
                                isDropdownExpanded = false
                            }
                        )
                        expenseCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.getLocalizedName(strings.isBangla), color = appColors.textPrimary) },
                                onClick = {
                                    selectedCatId = cat.id
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (strings.isBangla) "মাসিক বাজেট সীমা ($currencySymbol)" else "Monthly limit ($currencySymbol)",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = budgetAmountText,
                    onValueChange = { budgetAmountText = it.filter { c -> c.isDigit() || c == '.' } },
                    placeholder = { Text(if (strings.isBangla) "যেমন: ৫০০" else "e.g. 500", color = appColors.textMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(SmallElementRadius),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = budgetAmountText.toDoubleOrNull() ?: return@Button
                    val catName = if (selectedCatId == null) "Overall monthly" else expenseCategories.find { it.id == selectedCatId }?.name ?: "Category"
                    val cal = Calendar.getInstance()

                    val newBudget = Budget(
                        id = budgetToEdit?.id ?: 0L,
                        categoryId = selectedCatId,
                        categoryName = catName,
                        amount = amount,
                        month = cal.get(Calendar.MONTH),
                        year = cal.get(Calendar.YEAR)
                    )
                    onSave(newBudget)
                },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.save, color = androidx.compose.ui.graphics.Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        }
    )
}
