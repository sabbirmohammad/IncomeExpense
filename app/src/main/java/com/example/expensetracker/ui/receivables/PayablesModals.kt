package com.example.expensetracker.ui.receivables

import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData

@Composable
fun PayablesModals(
    viewModel: ReceivablesViewModel,
    userProfile: UserProfile,
    accounts: List<Account>,
    showAddEditPayableSheet: Boolean,
    payableToEdit: Payable?,
    onDismissAddEditPayable: () -> Unit,
    payableForPayment: Payable?,
    onDismissPayablePayment: () -> Unit,
    payableToDelete: Payable?,
    onDismissDeletePayable: () -> Unit,
    onTriggerConfetti: () -> Unit
) {
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current

    if (showAddEditPayableSheet) {
        AddEditPayableSheet(
            payable = payableToEdit,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onDismissAddEditPayable,
            onSave = { name, phone, amount, dueMillis, note, depositAccId ->
                if (payableToEdit != null) {
                    val updated = payableToEdit.copy(
                        personName = name, phoneNumber = phone,
                        amount = amount, dueDateMillis = dueMillis, notes = note
                    )
                    viewModel.updatePayable(updated)
                } else {
                    viewModel.addPayable(name, phone, amount, dueMillis, note, depositAccId)
                }
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.GENERIC_SUCCESS,
                        title = if (strings.isBangla) "দেনা হিসাব সফল হয়েছে" else "Payable saved",
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = name
                    )
                )
                onDismissAddEditPayable()
            }
        )
    }

    if (payableForPayment != null) {
        RecordPayablePaymentSheet(
            payable = payableForPayment,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = onDismissPayablePayment,
            onConfirm = { amount, accountId, note ->
                val target = payableForPayment
                val willSettle = (target.remainingAmount - amount) <= 0.001
                viewModel.recordPayablePayment(target.id, amount, accountId, note)
                if (willSettle) onTriggerConfetti()
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.DEBT_PAYMENT,
                        title = if (strings.isBangla) "দেনা পরিশোধ সফল হয়েছে" else "Debt payment recorded",
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = "${target.personName} · ${if (willSettle) strings.settledBadge else strings.partialBadge}"
                    )
                )
                onDismissPayablePayment()
            }
        )
    }

    if (payableToDelete != null) {
        PayableDeleteDialog(
            payable = payableToDelete,
            onDismiss = onDismissDeletePayable,
            onConfirmDelete = { target ->
                viewModel.deletePayable(target.id)
                onDismissDeletePayable()
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
}
