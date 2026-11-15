package com.example.expensetracker.ui.share

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun SharePdfSummaryCard(
    periodTitle: String,
    periodSubtitle: String,
    totalIncome: Double,
    totalExpense: Double,
    transactions: List<Transaction>,
    userProfile: UserProfile,
    strings: Strings,
    pdfRed: Color,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(pdfRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = null,
                        tint = pdfRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.formatPdf,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$periodTitle • $periodSubtitle",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = appColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(strings.income, fontSize = 11.sp, color = appColors.textSecondary)
                    Text(
                        formatCurrency(totalIncome, userProfile.currencySymbol),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.income
                    )
                }
                Column {
                    Text(strings.expense, fontSize = 11.sp, color = appColors.textSecondary)
                    Text(
                        formatCurrency(totalExpense, userProfile.currencySymbol),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.expense
                    )
                }
                Column {
                    Text(strings.transactionsTitle, fontSize = 11.sp, color = appColors.textSecondary)
                    Text(
                        "${transactions.size} ${strings.recordsFound}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.textPrimary
                    )
                }
            }
        }
    }
}
