package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun SubstituteActionButtons(
    isConfirmEnabled: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, appColors.border)
        ) {
            Text(
                text = if (isBangla) "বাতিল" else "Cancel",
                color = appColors.textSecondary,
                fontSize = 13.sp
            )
        }

        Button(
            onClick = onConfirm,
            enabled = isConfirmEnabled,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
        ) {
            Text(
                text = if (isBangla) "বদলান" else "Replace",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
