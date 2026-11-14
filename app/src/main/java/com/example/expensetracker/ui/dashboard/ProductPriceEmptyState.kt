package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold

@Composable
fun ProductPriceEmptyState(
    isBangla: Boolean,
    isFilterActive: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 48.dp, bottom = 48.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(appColors.surfaceVariant.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Analytics,
                    contentDescription = null,
                    tint = appColors.textSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (!isFilterActive) {
                    if (isBangla) "এখনো কোনো পণ্যের দর রেকর্ড হয়নি" else "No price records tracked yet"
                } else {
                    if (isBangla) "কোনো পণ্য পাওয়া যায়নি" else "No matching products found"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold, fontSize = 15.sp),
                color = appColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (!isFilterActive) {
                    if (isBangla) "বাজারের ফর্দ সম্পন্ন করার পর এখানে পণ্যের দামের তুলনামূলক চিত্র প্রদর্শিত হবে।"
                    else "Complete your market trips to start tracking item price trends."
                } else {
                    if (isBangla) "ফিল্টার পরিবর্তন করুন অথবা অন্য পণ্য অনুসন্ধান করুন।"
                    else "Try changing the filter or search for other products."
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }
    }
}
