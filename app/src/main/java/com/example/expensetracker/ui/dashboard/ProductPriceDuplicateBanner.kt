package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.market.DuplicateCommodityGroup

@Composable
fun ProductPriceDuplicateBanner(
    duplicateGroups: List<DuplicateCommodityGroup>,
    isBangla: Boolean,
    onReviewClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.accent.copy(alpha = 0.08f),
        border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MergeType,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = if (isBangla) "ডুপ্লিকেট পণ্য পাওয়া গেছে" else "Duplicate items found",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = WeightBold, fontSize = 13.sp),
                        color = appColors.textPrimary
                    )
                    val exampleGroup = duplicateGroups.first()
                    val examples = exampleGroup.variants.take(2).joinToString(if (isBangla) " ও " else " & ") { it.name }
                    Text(
                        text = if (isBangla) "যেমন: $examples" else "e.g., $examples",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = {
                        haptics.lightTick()
                        onReviewClick()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp).minimumInteractiveComponentSize()
                ) {
                    Text(text = if (isBangla) "রিভিউ" else "Review", style = MaterialTheme.typography.labelSmall.copy(fontWeight = WeightBold, fontSize = 12.sp))
                }

                IconButton(onClick = onDismissClick, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = appColors.textSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
