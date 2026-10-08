package com.example.expensetracker.ui.savings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun GoalNotesAndSaveSection(
    notes: String,
    onNotesChanged: (String) -> Unit,
    errorMessage: String?,
    selectedColorHex: String,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text("${strings.note} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChanged,
            placeholder = { Text(if (strings.isBangla) "লক্ষ্য সম্পর্কিত বিবরণ..." else "Notes or motivation...", color = ColorTextMuted) },
            maxLines = 2,
            shape = RoundedCornerShape(SmallElementRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ColorBackground,
                unfocusedContainerColor = ColorBackground,
                focusedBorderColor = ColorAccent,
                unfocusedBorderColor = ColorBorder,
                focusedTextColor = ColorTextPrimary,
                unfocusedTextColor = ColorTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = ColorDangerExpense, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(SmallElementRadius),
            colors = ButtonDefaults.buttonColors(containerColor = parseGoalColor(selectedColorHex))
        ) {
            Text(
                text = strings.save,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
