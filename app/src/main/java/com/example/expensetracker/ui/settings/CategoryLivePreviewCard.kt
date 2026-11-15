package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun CategoryLivePreviewCard(
    catIcon: String,
    catColor: String,
    catName: String,
    catType: TransactionType,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SmallElementRadius))
            .background(appColors.surfaceVariant)
            .border(1.dp, appColors.border, RoundedCornerShape(SmallElementRadius))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIconBadge(
            iconName = catIcon,
            colorHex = catColor,
            size = 40.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = if (catName.isBlank()) (if (strings.isBangla) "ক্যাটাগরি প্রিভিউ" else "Category preview") else catName,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                color = appColors.textPrimary
            )
            Text(
                text = if (strings.isBangla) (if (catType == TransactionType.INCOME) "আয় ক্যাটাগরি" else "ব্যয় ক্যাটাগরি")
                else "${catType.name.lowercase().replaceFirstChar { it.uppercase() }} Category",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = appColors.textSecondary
            )
        }
    }
}
