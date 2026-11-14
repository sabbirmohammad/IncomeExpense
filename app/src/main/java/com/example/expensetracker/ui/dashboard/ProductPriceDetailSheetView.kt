package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPriceDetailSheetView(
    insight: ProductPriceInsight,
    onFetchHistory: (String) -> List<ProductPriceRecord>,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = appColors.cardBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(appColors.divider)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insight.productName.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = WeightBold,
                            fontSize = 19.sp
                        ),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBangla) "পরিমাপক একক: ${insight.baseUnit} • ${convertToBengaliDigits(insight.purchaseCount.toString())} বার ক্রয় রেকর্ড"
                               else "Base Unit: ${insight.baseUnit} • ${insight.purchaseCount} purchases logged",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = appColors.textSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .background(appColors.cardBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProductPriceDetailStatsSection(
                insight = insight,
                isBangla = isBangla,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(14.dp))

            val history = remember(insight.productName) { onFetchHistory(insight.productName) }
            ProductPriceHistorySection(
                history = history,
                isBangla = isBangla,
                currencySymbol = currencySymbol,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
