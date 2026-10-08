package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ApiConnectionTestResult
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted

@Composable
fun SettingsAiKeyActionsRow(
    apiKeyInput: String,
    isConfigured: Boolean,
    isTestingApi: Boolean,
    apiConnectionTest: ApiConnectionTestResult?,
    isBangla: Boolean,
    onClear: () -> Unit,
    onTest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clear Button
            OutlinedButton(
                onClick = onClear,
                enabled = apiKeyInput.isNotBlank() || isConfigured,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ColorDangerExpense
                ),
                border = BorderStroke(
                    1.dp,
                    if (apiKeyInput.isNotBlank() || isConfigured) ColorDangerExpense.copy(alpha = 0.55f) else ColorBorder.copy(alpha = 0.3f)
                ),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = if (apiKeyInput.isNotBlank() || isConfigured) ColorDangerExpense else ColorTextMuted,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBangla) "ক্লিয়ার" else "Clear",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp
                    ),
                    color = if (apiKeyInput.isNotBlank() || isConfigured) ColorDangerExpense else ColorTextMuted
                )
            }

            // Test Connection Button
            OutlinedButton(
                onClick = onTest,
                enabled = !isTestingApi && (apiKeyInput.isNotBlank() || isConfigured),
                modifier = Modifier
                    .weight(1.05f)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ColorAccent
                ),
                border = BorderStroke(
                    1.dp,
                    if (!isTestingApi && (apiKeyInput.isNotBlank() || isConfigured)) ColorAccent.copy(alpha = 0.55f) else ColorBorder.copy(alpha = 0.3f)
                ),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
            ) {
                if (isTestingApi) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 1.8.dp,
                        color = ColorAccent
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = if (apiKeyInput.isNotBlank() || isConfigured) ColorAccent else ColorTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBangla) "যাচাই" else "Test",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp
                    ),
                    color = if (!isTestingApi && (apiKeyInput.isNotBlank() || isConfigured)) ColorAccent else ColorTextMuted
                )
            }

            // Save Button
            Button(
                onClick = onSave,
                enabled = apiKeyInput.isNotBlank(),
                modifier = Modifier
                    .weight(1.25f)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAccent,
                    disabledContainerColor = ColorAccent.copy(alpha = 0.25f)
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = if (apiKeyInput.isNotBlank()) Color.White else ColorTextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBangla) "সেভ করুন" else "Save Key",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    ),
                    color = if (apiKeyInput.isNotBlank()) Color.White else ColorTextMuted
                )
            }
        }

        // Test Connection Result Banner
        if (apiConnectionTest != null) {
            val testRes = apiConnectionTest
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (testRes.isSuccess) ColorSuccessIncome.copy(alpha = 0.12f) else ColorDangerExpense.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, if (testRes.isSuccess) ColorSuccessIncome.copy(alpha = 0.35f) else ColorDangerExpense.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (testRes.isSuccess) Icons.Filled.CheckCircle else Icons.Outlined.Clear,
                        contentDescription = null,
                        tint = if (testRes.isSuccess) ColorSuccessIncome else ColorDangerExpense,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (testRes.isSuccess) {
                            if (isBangla)
                                "✓ সক্রিয় (${testRes.modelUsed} • ${testRes.latencyMs}ms)"
                            else
                                "✓ Active (${testRes.modelUsed} • ${testRes.latencyMs}ms)"
                        } else {
                            if (isBangla)
                                "✕ ত্রুটি: ${testRes.message}"
                            else
                                "✕ Error: ${testRes.message}"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        ),
                        color = if (testRes.isSuccess) ColorSuccessIncome else ColorDangerExpense
                    )
                }
            }
        }
    }
}
