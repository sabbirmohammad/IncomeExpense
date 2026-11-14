package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun CreateMarketListQuantitySelector(
    selectedQuantityText: String,
    onSelectQuantity: (String) -> Unit,
    quickQuantities: List<String>,
    isBangla: Boolean,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = if (isBangla) "পরিমাণ নির্বাচন" else "Select Quantity",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
            color = appColors.textSecondary
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            items(quickQuantities) { qtyStr ->
                val isSelected = selectedQuantityText == qtyStr
                Surface(
                    onClick = { haptics.lightTick(); onSelectQuantity(qtyStr) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) appColors.accentContainer else appColors.surfaceVariant,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp).minimumInteractiveComponentSize()
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(
                            text = qtyStr,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) appColors.accent else appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
