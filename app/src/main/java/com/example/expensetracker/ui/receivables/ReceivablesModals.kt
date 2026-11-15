package com.example.expensetracker.ui.receivables

import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData

@Composable
fun ReceivablesModals(
    viewModel: ReceivablesViewModel,
    userProfile: UserProfile,
    accounts: List<Account>,
    categories: List<Category>,
    showAddEditSheet: Boolean,
    receivableToEdit: Receivable?,
    onDismissAddEdit: () -> Unit,
    receivableForPayment: Receivable?,
    onDismissPayment: () -> Unit,
    showSplitBillSheet: Boolean,
    onDismissSplitBill: () -> Unit,
    receivableToDelete: Receivable?,
    onDismissDeleteReceivable: () -> Unit,
    showAddEditPayableSheet: Boolean,
    payableToEdit: Payable?,
    onDismissAddEditPayable: () -> Unit,
    payableForPayment: Payable?,
    onDismissPayablePayment: () -> Unit,
    payableToDelete: Payable?,
    onDismissDeletePayable: () -> Unit,
    showConfetti: Boolean,
    onTriggerConfetti: () -> Unit,
    onConfettiEnd: () -> Unit
) {
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current

    if (showAddEditSheet) {
        AddEditReceivableSheet(
            receivable = receivableToEdit,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onDismissAddEdit,
            onSave = { name, phone, amount, dueMillis, note, deductAccId ->
                if (receivableToEdit != null) {
                    val updated = receivableToEdit.copy(
                        personName = name, phoneNumber = phone,
                        amount = amount, dueDateMillis = dueMillis, notes = note
                    )
                    viewModel.updateReceivable(updated)
                } else {
                    viewModel.addReceivable(name, phone, amount, dueMillis, note, deductAccId)
                }
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.GENERIC_SUCCESS,
                        title = FeedbackStrings.receivableAdded(strings.isBangla),
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = name
                    )
                )
                onDismissAddEdit()
            }
        )
    }

    if (receivableForPayment != null) {
        RecordPaymentSheet(
            receivable = receivableForPayment,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onDismissPayment,
            onConfirm = { amount, accountId, note ->
                val target = receivableForPayment
                val willSettle = (target.remainingAmount - amount) <= 0.001
                viewModel.recordPayment(target.id, amount, accountId, note)
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.DEBT_PAYMENT,
                        title = FeedbackStrings.debtPayment(strings.isBangla),
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = "${target.personName} · ${if (willSettle) strings.settledBadge else strings.partialBadge}"
                    )
                )
                onDismissPayment()
            }
        )
    }

    if (showSplitBillSheet) {
        BillSplitterSheet(
            accounts = accounts,
            categories = categories,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onDismissSplitBill,
            onConfirmSplit = { splitData ->
                viewModel.executeBillSplit(splitData)
                onDismissSplitBill()
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

    if (receivableToDelete != null) {
        ReceivableDeleteDialog(
            receivable = receivableToDelete,
            onDismiss = onDismissDeleteReceivable,
            onConfirmDelete = { target ->
                viewModel.deleteReceivable(target.id)
                onDismissDeleteReceivable()
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.ITEM_DELETED,
                        title = FeedbackStrings.itemDeleted(strings.isBangla),
                        subtitle = target.personName
                    )
                )
            }
        )
    }

    PayablesModals(
        viewModel = viewModel,
        userProfile = userProfile,
        accounts = accounts,
        showAddEditPayableSheet = showAddEditPayableSheet,
        payableToEdit = payableToEdit,
        onDismissAddEditPayable = onDismissAddEditPayable,
        payableForPayment = payableForPayment,
        onDismissPayablePayment = onDismissPayablePayment,
        payableToDelete = payableToDelete,
        onDismissDeletePayable = onDismissDeletePayable,
        onTriggerConfetti = onTriggerConfetti
    )

    ConfettiCelebration(
        trigger = showConfetti,
        onAnimationEnd = onConfettiEnd
    )
}
