package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ApiConnectionTestResult
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary

@Composable
fun SettingsAiCardHeader(
    isBangla: Boolean,
    isTestingApi: Boolean,
    isConfigured: Boolean,
    apiConnectionTest: ApiConnectionTestResult?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                ColorAccent.copy(alpha = 0.25f),
                                Color(0xFF8B5CF6).copy(alpha = 0.25f)
                            )
                        )
                    )
                    .border(1.dp, ColorAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = ColorAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = if (isBangla) "স্মার্ট রসিদ স্ক্যানার (Gemini API)" else "Receipt Scanner (Gemini API)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ColorTextPrimary
                )
                Text(
                    text = if (isBangla) "রসিদের ছবি ও টেক্সট থেকে দ্রুত হিসাব তুলতে" else "For receipt OCR & smart quick text input",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = ColorTextSecondary
                )
            }
        }

        val statusText: String
        val statusColor: Color
        val statusBg: Color
        when {
            isTestingApi -> {
                statusText = if (isBangla) "যাচাই হচ্ছে..." else "Testing..."
                statusColor = ColorAccent
                statusBg = ColorAccent.copy(alpha = 0.12f)
            }
            apiConnectionTest?.isSuccess == true -> {
                statusText = if (isBangla) "সংযুক্ত (${apiConnectionTest.latencyMs}ms)" else "Active (${apiConnectionTest.latencyMs}ms)"
                statusColor = ColorSuccessIncome
                statusBg = ColorSuccessIncome.copy(alpha = 0.12f)
            }
            apiConnectionTest?.isSuccess == false -> {
                statusText = if (isBangla) "সংযোগ ব্যর্থ" else "Failed"
                statusColor = ColorDangerExpense
                statusBg = ColorDangerExpense.copy(alpha = 0.12f)
            }
            isConfigured -> {
                statusText = if (isBangla) "সংযুক্ত" else "Active"
                statusColor = ColorSuccessIncome
                statusBg = ColorSuccessIncome.copy(alpha = 0.12f)
            }
            else -> {
                statusText = if (isBangla) "অসংযুক্ত" else "Offline"
                statusColor = ColorTextMuted
                statusBg = ColorSurface
            }
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = statusBg,
            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                )
            }
        }
    }
}
