package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold

@Composable
fun MarketDuplicateEmptyState(
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = appColors.income,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isBangla) "কোনো ডুপ্লিকেট পণ্য নেই" else "No duplicate items found",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = WeightBold,
                    fontSize = 15.sp
                ),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isBangla) "আপনার নিত্যপণ্যের ক্যাটালগ সম্পূর্ণ সাজানো রয়েছে।"
                else "Your commodity catalog is clean and normalized.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
