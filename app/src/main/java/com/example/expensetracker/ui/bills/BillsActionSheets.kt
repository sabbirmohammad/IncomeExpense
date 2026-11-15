package com.example.expensetracker.ui.bills

import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData

@Composable
fun BillsActionSheets(
    showAddSheet: Boolean,
    billToEdit: RecurringBill?,
    billToPay: RecurringBill?,
    viewModel: BillsViewModel,
    categories: List<Category>,
    accounts: List<Account>,
    currencySymbol: String,
    onDismissAdd: () -> Unit,
    onDismissPay: () -> Unit,
    onAddSaved: () -> Unit
) {
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current

    if (showAddSheet) {
        AddEditBillSheet(
            billToEdit = billToEdit,
            categories = categories,
            accounts = accounts,
            currencySymbol = currencySymbol,
            onDismiss = onDismissAdd,
            onSave = { newBill ->
                val isEditing = billToEdit != null && billToEdit.id != 0L
                if (isEditing) {
                    viewModel.updateBill(newBill)
                } else {
                    viewModel.addBill(newBill)
                }
                onAddSaved()
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.GENERIC_SUCCESS,
                        title = if (isEditing) strings.editBill else strings.allRecurringBills,
                        amount = newBill.amount,
                        currencySymbol = currencySymbol,
                        subtitle = newBill.title
                    )
                )
            }
        )
    }

    if (billToPay != null) {
        PayBillSheet(
            bill = billToPay,
            accounts = accounts,
            currencySymbol = currencySymbol,
            onDismiss = onDismissPay,
            onConfirmPay = { accId, accName ->
                viewModel.payBill(billToPay.id, accId, accName) {
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.BILL_PAID,
                            title = FeedbackStrings.billPaid(strings.isBangla),
                            amount = billToPay.amount,
                            currencySymbol = currencySymbol,
                            subtitle = "${billToPay.title} · $accName"
                        )
                    )
                }
                onDismissPay()
            }
        )
    }
}
