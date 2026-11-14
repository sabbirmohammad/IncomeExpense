package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.ActiveMarketListDashboardCard
import com.example.expensetracker.ui.components.AppHaptics

@Composable
internal fun DashboardActiveMarketListContent(
    activeMarketLists: List<MarketListWithItems>,
    effectiveMarketList: MarketListWithItems,
    currencySymbol: String,
    isBn: Boolean,
    haptics: AppHaptics,
    selectedListId: Long?,
    onSelectListId: (Long) -> Unit,
    onOpenMarketList: () -> Unit,
    onMarkPurchased: (MarketItem, Double) -> Unit,
    onUnmarkPurchased: (MarketItem) -> Unit,
    onUpdateItemAmount: (MarketItem, Double?) -> Unit,
    onCompleteList: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    if (activeMarketLists.size > 1) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(activeMarketLists, key = { it.list.id }) { aList ->
                val isSelected = aList.list.id == effectiveMarketList.list.id
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) appColors.accent.copy(alpha = 0.15f) else appColors.surface,
                    border = BorderStroke(1.dp, if (isSelected) appColors.accent else appColors.border),
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onSelectListId(aList.list.id)
                    }
                ) {
                    Text(
                        text = aList.list.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) appColors.accent else appColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }

    ActiveMarketListDashboardCard(
        marketListWithItems = effectiveMarketList,
        currencySymbol = currencySymbol,
        isBangla = isBn,
        onOpenFullList = onOpenMarketList,
        onMarkPurchased = onMarkPurchased,
        onUnmarkPurchased = onUnmarkPurchased,
        onUpdateItemAmount = onUpdateItemAmount,
        onCompleteList = onCompleteList
    )
}
