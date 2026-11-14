package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Full-screen Commodity Item Detail Drill-down (Target Design E):
 * - Hero latest price/unit, delta vs previous
 * - Range chips 7d / 30d / 90d / All
 * - Interactive line chart with tap tooltip
 * - Stats cards Min / Avg / Max
 * - Last 10 purchases (date, qty, price, rate)
 * - Actions: "Add to list", "Rename", "Merge with…"
 */
@Composable
fun MarketItemDetailScreen(
    commodity: MarketCommodityUiModel,
    allCommodities: List<MarketCommodityUiModel> = emptyList(),
    onFetchHistory: (String) -> List<ProductPriceRecord>,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onAddToList: (MarketCommodityUiModel) -> Unit,
    onRenameCommodity: ((oldName: String, newName: String) -> Unit)? = null,
    onMergeCommodity: ((sourceName: String, targetName: String) -> Unit)? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val state = rememberMarketItemDetailState(commodity, onFetchHistory)

    Surface(
        color = appColors.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            MarketItemDetailTopBar(
                commodity = commodity,
                isBangla = isBangla,
                onClose = onClose,
                onRenameClick = if (onRenameCommodity != null) { { state.showRenameDialog = true } } else null,
                onMergeClick = if (onMergeCommodity != null) { { state.showMergeWithDialog = true } } else null
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    HeroPriceCard(
                        commodity = commodity,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                }

                item {
                    MarketItemRangeSelector(
                        selectedRange = state.selectedRange,
                        onRangeSelected = { state.selectedRange = it },
                        isBangla = isBangla
                    )
                }

                item {
                    InteractivePriceChartCard(
                        records = state.activeRecords,
                        commodity = commodity,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                }

                item {
                    MarketItemStatsRow(
                        minRate = state.minRate,
                        avgRate = state.avgRate,
                        maxRate = state.maxRate,
                        unit = commodity.defaultUnit,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                }

                item {
                    Button(
                        onClick = {
                            haptics.mediumClick()
                            onAddToList(commodity)
                            state.isAddedToListFeedback = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isAddedToListFeedback) appColors.income else appColors.accent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isAddedToListFeedback) {
                                if (isBangla) "তালিকায় যুক্ত হয়েছে ✓" else "Added to list ✓"
                            } else {
                                if (isBangla) "তালিকায় যোগ করুন" else "Add to list"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = WeightBold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                marketRecentPurchasesSection(
                    records = state.allHistoryRecords,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    appColors = appColors
                )
            }
        }
    }

    if (state.showRenameDialog && onRenameCommodity != null) {
        CommodityRenameDialog(
            currentName = commodity.name,
            isBangla = isBangla,
            onDismiss = { state.showRenameDialog = false },
            onRename = onRenameCommodity
        )
    }

    if (state.showMergeWithDialog && onMergeCommodity != null) {
        CommodityMergeDialog(
            sourceName = commodity.name,
            allCommodities = allCommodities,
            isBangla = isBangla,
            onDismiss = { state.showMergeWithDialog = false },
            onMerge = onMergeCommodity
        )
    }
}
