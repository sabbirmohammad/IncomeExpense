package com.example.expensetracker.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.accounts.TransferFundsSheet
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.receivables.BillSplitterSheet
import com.example.expensetracker.ui.transactions.AddEditTransactionSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTransactionSheets(
    showGlobalAddSheet: Boolean,
    showBillSplitterSheet: Boolean,
    showTransferSheet: Boolean,
    transactionToEdit: Transaction?,
    initialDateForAdd: Long?,
    initialTypeForAdd: TransactionType,
    initialOpenMarketForAdd: Boolean = false,
    categories: List<Category>,
    accounts: List<Account>,
    userProfile: UserProfile,
    sheetState: SheetState,
    scope: CoroutineScope,
    strings: Strings,
    feedbackController: TransactionFeedbackController,
    dashboardViewModel: DashboardViewModel,
    onCloseAddSheet: () -> Unit,
    onCloseBillSplitterSheet: () -> Unit,
    onCloseTransferSheet: () -> Unit,
    onOpenMarketList: (() -> Unit)? = null
) {
    if (showGlobalAddSheet) {
        val activeMarketList by dashboardViewModel.activeMarketList.collectAsStateWithLifecycle()
        val lastPurchasedRates by dashboardViewModel.lastPurchasedRates.collectAsStateWithLifecycle()
        AddEditTransactionSheet(
            sheetState = sheetState,
            existingTransaction = transactionToEdit,
            initialDateMillis = initialDateForAdd,
            initialType = initialTypeForAdd,
            initialOpenMarketTab = initialOpenMarketForAdd,
            categories = categories,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            marketListWithItems = activeMarketList,
            lastPurchasedRates = lastPurchasedRates,
            onCompleteMarketListWithRollOver = { listId, rollOverItems ->
                dashboardViewModel.completeActiveMarketList(listId, rollOverItems) { result ->
                    if (result.success && result.totalSpent > 0) {
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.EXPENSE,
                                title = if (strings.isBangla) "বাজার সম্পন্ন ও নতুন ফর্দ তৈরি হয়েছে!" else "Market completed & rolled over!",
                                amount = result.totalSpent,
                                currencySymbol = userProfile.currencySymbol,
                                subtitle = if (strings.isBangla) "${convertToBengaliDigits(result.purchasedCount.toString())}টি সম্পন্ন · ${convertToBengaliDigits(rollOverItems.size.toString())}টি নতুন ফর্দে সংরক্ষিত"
                                          else "${result.purchasedCount} purchased · ${rollOverItems.size} rolled over"
                            )
                        )
                    }
                }
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onCloseAddSheet()
                }
            },
            onCreateMarketListWithBulk = { title, items ->
                dashboardViewModel.createMarketListWithBulk(title, items)
            },
            onAddMarketItem = { item ->
                dashboardViewModel.addMarketItem(item)
            },
            onUpdateMarketItem = { item ->
                dashboardViewModel.updateMarketItem(item)
            },
            onDeleteMarketItem = { itemId ->
                dashboardViewModel.deleteMarketItem(itemId)
            },
            onMarkMarketItemPurchased = { item, amount ->
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
            onUnmarkMarketItemPurchased = { item ->
                dashboardViewModel.unmarkMarketItemPurchased(item)
            },
            onCompleteMarketList = { listId ->
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
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onCloseAddSheet()
                }
            },
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onCloseAddSheet()
                }
            },
            onSave = { tx ->
                val isEditing = transactionToEdit != null
                if (isEditing) {
                    dashboardViewModel.updateTransaction(tx)
                } else {
                    dashboardViewModel.addTransaction(tx)
                }

                val rawCatName = categories.find { it.id == tx.categoryId }?.name ?: tx.categoryName
                val catName = getLocalizedCategoryName(rawCatName, strings.isBangla)
                val accName = accounts.find { it.id == tx.accountId }?.name ?: (tx.accountName ?: "")
                val subtitleInfo = if (accName.isNotBlank()) "$catName · $accName" else catName

                val feedbackType = if (tx.type == TransactionType.INCOME) {
                    FeedbackVisualType.INCOME
                } else {
                    FeedbackVisualType.EXPENSE
                }

                val feedbackTitle = when {
                    isEditing -> FeedbackStrings.transactionUpdated(strings.isBangla)
                    tx.type == TransactionType.INCOME -> FeedbackStrings.incomeAdded(strings.isBangla)
                    else -> FeedbackStrings.expenseRecorded(strings.isBangla)
                }

                onCloseAddSheet()
                feedbackController.show(
                    TransactionFeedbackData(
                        type = feedbackType,
                        title = feedbackTitle,
                        amount = tx.amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = subtitleInfo
                    )
                )
            },
            onDelete = { id ->
                dashboardViewModel.deleteTransaction(id)
                onCloseAddSheet()
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.TRANSACTION_DELETED,
                        title = FeedbackStrings.transactionDeleted(strings.isBangla),
                        subtitle = FeedbackStrings.itemDeleted(strings.isBangla)
                    )
                )
            },
            onOpenMarketList = onOpenMarketList
        )
    }

    if (showBillSplitterSheet) {
        BillSplitterSheet(
            accounts = accounts,
            categories = categories,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onCloseBillSplitterSheet,
            onConfirmSplit = { splitData ->
                dashboardViewModel.executeBillSplit(splitData)
                onCloseBillSplitterSheet()
                val totalPeople = splitData.friends.size + 1
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.BILL_SPLIT,
                        title = FeedbackStrings.billSplit(strings.isBangla),
                        amount = splitData.totalAmount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = if (strings.isBangla) "$totalPeople জনের মধ্যে স্প্লিট" else "$totalPeople people split"
                    )
                )
            }
        )
    }

    if (showTransferSheet) {
        TransferFundsSheet(
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onCloseTransferSheet,
            onTransfer = { fromId, toId, amount, note ->
                dashboardViewModel.transferFunds(fromId, toId, amount, note)
                onCloseTransferSheet()
                val fromAcc = accounts.find { it.id == fromId }?.name ?: ""
                val toAcc = accounts.find { it.id == toId }?.name ?: ""
                val transferSub = if (fromAcc.isNotBlank() && toAcc.isNotBlank()) "$fromAcc → $toAcc" else note
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.TRANSFER,
                        title = FeedbackStrings.transferSuccess(strings.isBangla),
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = transferSub.ifBlank { null }
                    )
                )
            }
        )
    }
}
