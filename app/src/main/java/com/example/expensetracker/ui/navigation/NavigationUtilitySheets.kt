package com.example.expensetracker.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.*
import com.example.expensetracker.ui.budget.CategoryBudgetStatus
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.UpcomingBudgetPromptDialog
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.dashboard.MarketListSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationUtilitySheets(
    showMarketListSheet: Boolean,
    activeMarketList: MarketListWithItems?,
    showUpcomingBudgetPrompt: Boolean,
    summary: FinancialSummary,
    userProfile: UserProfile,
    strings: Strings,
    feedbackController: TransactionFeedbackController,
    dashboardViewModel: DashboardViewModel,
    repository: ExpenseRepository,
    scope: CoroutineScope,
    onCloseMarketList: () -> Unit,
    onCloseBudgetPrompt: () -> Unit,
    onNavigateToBudgets: () -> Unit
) {
    if (showMarketListSheet) {
        val marketSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val activeMarketLists by dashboardViewModel.activeMarketLists.collectAsStateWithLifecycle()
        val lastPurchasedRates by dashboardViewModel.lastPurchasedRates.collectAsStateWithLifecycle()
        val productPriceInsights by dashboardViewModel.productPriceInsights.collectAsStateWithLifecycle()
        MarketListSheet(
            sheetState = marketSheetState,
            marketListWithItems = activeMarketList,
            activeMarketLists = activeMarketLists,
            currencySymbol = userProfile.currencySymbol,
            isBangla = strings.isBangla,
            lastPurchasedRates = lastPurchasedRates,
            productPriceInsights = productPriceInsights,
            onFetchPriceHistory = { pName -> dashboardViewModel.getProductPriceHistory(pName) },
            onDismiss = {
                scope.launch { marketSheetState.hide() }.invokeOnCompletion {
                    onCloseMarketList()
                }
            },
            onCreateListWithBulk = { title, items ->
                dashboardViewModel.createMarketListWithBulk(title, items)
            },
            onAddItem = { item, targetListId ->
                dashboardViewModel.addMarketItem(item, targetListId)
            },
            onUpdateItem = { item ->
                dashboardViewModel.updateMarketItem(item)
            },
            onDeleteItem = { itemId ->
                dashboardViewModel.deleteMarketItem(itemId)
            },
            onMarkPurchased = { item, amount ->
                dashboardViewModel.markMarketItemPurchased(item, amount)
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.EXPENSE,
                        title = FeedbackStrings.expenseRecorded(strings.isBangla),
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = "${item.name} (${item.formatQuantity()}) · ${if (strings.isBangla) "বাজার ও সদাই" else "Groceries"}"
                    )
                )
            },
            onUnmarkPurchased = { item ->
                dashboardViewModel.unmarkMarketItemPurchased(item)
            },
            onCompleteList = { listId ->
                dashboardViewModel.completeActiveMarketList(listId) { result ->
                    if (result.success && result.totalSpent > 0) {
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.EXPENSE,
                                title = if (strings.isBangla) "বাজার সম্পন্ন হয়েছে!" else "Shopping Completed!",
                                amount = result.totalSpent,
                                currencySymbol = userProfile.currencySymbol,
                                subtitle = if (strings.isBangla) "${convertToBengaliDigits(result.purchasedCount.toString())}টি পণ্য সম্পন্ন · মোট বাজার খরচ"
                                          else "${result.purchasedCount} items purchased · Total spent"
                            )
                        )
                    }
                }
                onCloseMarketList()
            },
            onDeleteList = { listId ->
                dashboardViewModel.deleteActiveMarketList(listId)
                onCloseMarketList()
            }
        )
    }

    if (showUpcomingBudgetPrompt) {
        val budgetViewModel: com.example.expensetracker.ui.budget.BudgetViewModel = viewModel()
        val budgetStatuses by budgetViewModel.budgetStatuses.collectAsStateWithLifecycle()
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val (fromMonth, fromYear, toMonth, toYear) = if (currentDay == maxDay) {
            val calNext = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
            listOf(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), calNext.get(Calendar.MONTH), calNext.get(Calendar.YEAR))
        } else {
            val calPrev = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
            listOf(calPrev.get(Calendar.MONTH), calPrev.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
        }

        val targetKey = "$toYear-$toMonth"
        val currentBudgetAmount = summary.budgetAmount
        val categoryBudgetCount = budgetStatuses.count { it.budget.categoryId != null }

        UpcomingBudgetPromptDialog(
            currentBudgetAmount = currentBudgetAmount,
            categoryBudgetCount = categoryBudgetCount,
            currencySymbol = userProfile.currencySymbol,
            isBangla = strings.isBangla,
            onKeepPreviousBudget = {
                scope.launch {
                    val success = repository.copyBudgets(fromMonth, fromYear, toMonth, toYear)
                    repository.markBudgetPromptHandled(targetKey)
                    onCloseBudgetPrompt()
                    if (success) {
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.GENERIC_SUCCESS,
                                title = strings.budgetCopiedSuccess,
                                amount = currentBudgetAmount,
                                currencySymbol = userProfile.currencySymbol
                            )
                        )
                    }
                }
            },
            onSetNewBudget = {
                repository.markBudgetPromptHandled(targetKey)
                onCloseBudgetPrompt()
                onNavigateToBudgets()
            },
            onDismiss = onCloseBudgetPrompt
        )
    }
}
