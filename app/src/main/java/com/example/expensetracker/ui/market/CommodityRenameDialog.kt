package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun CommodityRenameDialog(
    currentName: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onRename: (oldName: String, newName: String) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var renameInput by remember(currentName) { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "পণ্যের নাম পরিবর্তন" else "Rename Item",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold),
                color = appColors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isBangla) "নতুন নাম লিখুন। সকল অতীত রেকর্ড এবং ফর্দে এটি আপডেট হবে।"
                    else "Enter a new name. All historical records will update cleanly.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clean = renameInput.trim()
                    if (clean.isNotBlank() && clean != currentName) {
                        haptics.successFeedback()
                        onRename(currentName, clean)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = if (isBangla) "সংরক্ষণ করুন" else "Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = appColors.surface
    )
}
