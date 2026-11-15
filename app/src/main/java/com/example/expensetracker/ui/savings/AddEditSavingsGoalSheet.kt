package com.example.expensetracker.ui.savings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.toParsedAmountOrNull

/**
 * Add or Edit Savings Goal Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSavingsGoalSheet(
    goal: SavingsGoal?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Long?, String, String, String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    var title by remember { mutableStateOf(goal?.title ?: "") }
    var targetAmountText by remember {
        mutableStateOf(goal?.targetAmount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var initialSavedText by remember {
        mutableStateOf(goal?.savedAmount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var targetDateMillis by remember { mutableStateOf(goal?.targetDateMillis) }
    var selectedIcon by remember { mutableStateOf(goal?.icon ?: "savings") }
    var selectedColorHex by remember { mutableStateOf(goal?.colorHex ?: "#3B82F6") }
    var notes by remember { mutableStateOf(goal?.notes ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (goal != null)
                        (if (strings.isBangla) "সঞ্চয় লক্ষ্য সম্পাদনা" else "Edit Savings Goal")
                    else
                        strings.addSavingsGoal,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = appColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            GoalTitleField(
                title = title,
                onTitleChange = { title = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Target Amount with Calculator
            GoalTargetAmountSection(
                targetAmountText = targetAmountText,
                currencySymbol = currencySymbol,
                onTargetAmountChanged = { targetAmountText = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Initial Saved Amount (Optional)
            if (goal == null) {
                GoalInitialSavedField(
                    initialSavedText = initialSavedText,
                    onInitialSavedChange = { initialSavedText = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Target Date Selector
            GoalDateSelectorRow(
                targetDateMillis = targetDateMillis,
                onOpenDatePicker = { showDatePicker = true },
                onClearDate = { targetDateMillis = null }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Icon Picker
            Text(if (strings.isBangla) "আইকন নির্বাচন করুন" else "Select Icon", fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            GoalIconPickerRow(
                selectedIcon = selectedIcon,
                selectedColorHex = selectedColorHex,
                onIconSelected = { selectedIcon = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Color Picker
            Text(if (strings.isBangla) "রং নির্বাচন করুন" else "Select Color", fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            GoalColorPickerRow(
                selectedColorHex = selectedColorHex,
                onColorSelected = { selectedColorHex = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Notes and Save Action
            GoalNotesAndSaveSection(
                notes = notes,
                onNotesChanged = { notes = it },
                errorMessage = errorMessage,
                selectedColorHex = selectedColorHex,
                onSaveClick = {
                    if (title.isBlank()) {
                        errorMessage = if (strings.isBangla) "লক্ষ্যের একটি নাম লিখুন" else "Please enter a goal title"
                        return@GoalNotesAndSaveSection
                    }
                    val targetAmt = targetAmountText.toParsedAmountOrNull()
                    if (targetAmt == null || targetAmt <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@GoalNotesAndSaveSection
                    }
                    val initAmt = initialSavedText.toParsedAmountOrNull() ?: 0.0
                    onSave(title.trim(), targetAmt, initAmt, targetDateMillis, selectedIcon, selectedColorHex, notes.trim())
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        GoalDatePickerDialog(
            targetDateMillis = targetDateMillis,
            onDismiss = { showDatePicker = false },
            onDateConfirmed = { targetDateMillis = it }
        )
    }
}
