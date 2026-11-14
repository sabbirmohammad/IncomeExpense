package com.example.expensetracker.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

@Composable
internal fun DashboardMarketCollapsible(
    effectiveMarketList: MarketListWithItems,
    activeMarketLists: List<MarketListWithItems>,
    currencySymbol: String,
    isBn: Boolean,
    isPrivacyMode: Boolean,
    useBengaliNumerals: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenMarketList: () -> Unit,
    selectedDashboardListId: Long?,
    onSelectListId: (Long) -> Unit,
    viewModel: DashboardViewModel,
    haptics: AppHaptics
) {
    val feedbackController = LocalFeedbackController.current
    val marketMetric = "${effectiveMarketList.purchasedItems}/${effectiveMarketList.totalItems} ${if (isBn) "পণ্য" else "items"} · ${if (isPrivacyMode) "••••" else formatCurrency(effectiveMarketList.totalSpent, currencySymbol, useBengaliNumerals)}"

    DashboardCollapsibleSection(
        icon = Icons.Outlined.ShoppingCart,
        title = effectiveMarketList.list.title.ifBlank { if (isBn) "বাজার ফর্দ" else "Market List" },
        metricText = marketMetric,
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand,
        onOpenFull = onOpenMarketList
    ) {
        DashboardActiveMarketListContent(
            activeMarketLists = activeMarketLists,
            effectiveMarketList = effectiveMarketList,
            currencySymbol = currencySymbol,
            isBn = isBn,
            haptics = haptics,
            selectedListId = selectedDashboardListId,
            onSelectListId = onSelectListId,
            onOpenMarketList = onOpenMarketList,
            onMarkPurchased = { item, amt ->
                viewModel.markMarketItemPurchased(item, amt)
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.EXPENSE,
                        title = FeedbackStrings.expenseRecorded(isBn),
                        amount = amt,
                        currencySymbol = currencySymbol,
                        subtitle = "${item.name} (${item.formatQuantity()}) · ${if (isBn) "বাজার ও সদাই" else "Groceries"}"
                    )
                )
            },
            onUnmarkPurchased = { viewModel.unmarkMarketItemPurchased(it) },
            onUpdateItemAmount = { item, amt -> viewModel.updateMarketItem(item.copy(amount = amt)) },
            onCompleteList = { listId ->
                viewModel.completeActiveMarketList(listId) { result ->
                    if (result.success && result.totalSpent > 0) {
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.EXPENSE,
                                title = if (isBn) "বাজার সম্পন্ন হয়েছে!" else "Shopping Completed!",
                                amount = result.totalSpent,
                                currencySymbol = currencySymbol,
                                subtitle = if (isBn) "${convertToBengaliDigits(result.purchasedCount.toString())}টি পণ্য সম্পন্ন · মোট বাজার খরচ"
                                else "${result.purchasedCount} items purchased · Total spent"
                            )
                        )
                    }
                }
            }
        )
    }
}
