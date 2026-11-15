package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.Strings

@Composable
fun CreateMarketListActionShortcuts(
    onToggleInlinePaste: () -> Unit,
    lastCompletedList: MarketListWithItems?,
    onRepeatLastList: (List<MarketItem>) -> Unit,
    strings: Strings,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            onClick = onToggleInlinePaste,
            shape = RoundedCornerShape(12.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 48.dp)
                .minimumInteractiveComponentSize()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentPaste,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = strings.marketPasteFromWhatsApp,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (lastCompletedList != null && lastCompletedList.items.isNotEmpty()) {
            Surface(
                onClick = {
                    haptics.mediumClick()
                    val itemsToAdd = lastCompletedList.items.map {
                        MarketItem(name = it.name, quantity = it.quantity, unit = it.unit)
                    }
                    onRepeatLastList(itemsToAdd)
                },
                shape = RoundedCornerShape(12.dp),
                color = appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = appColors.income,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.marketRepeatList,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
