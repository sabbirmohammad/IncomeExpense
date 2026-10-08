package com.example.expensetracker.ui.settings

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun CategoryDeleteConfirmDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirmDelete: (Category) -> Unit
) {
    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = ColorSurface,
        titleContentColor = ColorTextPrimary,
        textContentColor = ColorTextSecondary,
        title = {
            Text(
                text = if (strings.isBangla) "ক্যাটাগরি মুছে ফেলবেন?" else "Delete category?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        text = {
            Text(
                text = if (strings.isBangla) "আপনি কি নিশ্চিতভাবে \"${category.getLocalizedName(strings.isBangla)}\" ক্যাটাগরিটি মুছে ফেলতে চান? এই ক্যাটাগরির লেনদেনগুলো প্রভাবিত হতে পারে।"
                else "Are you sure you want to delete \"${category.name}\"? Any transactions in this category will be affected.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirmDelete(category) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.delete, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ColorTextSecondary)
            }
        }
    )
}
