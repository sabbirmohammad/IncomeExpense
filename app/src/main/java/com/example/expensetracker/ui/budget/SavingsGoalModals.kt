package com.example.expensetracker.ui.budget

import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.savings.AddEditSavingsGoalSheet
import com.example.expensetracker.ui.savings.AddSavingsContributionSheet

@Composable
fun SavingsGoalModals(
    viewModel: BudgetViewModel,
    accounts: List<Account>,
    currencySymbol: String,
    showAddSavingsGoalSheet: Boolean,
    goalToEdit: SavingsGoal?,
    goalForContribution: SavingsGoal?,
    goalToDelete: SavingsGoal?,
    showConfetti: Boolean,
    onDismissAddEdit: () -> Unit,
    onDismissContribution: () -> Unit,
    onDismissDelete: () -> Unit,
    onConfettiDismiss: () -> Unit,
    onGoalCelebrated: () -> Unit
) {
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current

    if (showAddSavingsGoalSheet) {
        AddEditSavingsGoalSheet(
            goal = goalToEdit,
            currencySymbol = currencySymbol,
            onDismiss = onDismissAddEdit,
            onSave = { title, targetAmt, initAmt, targetDate, icon, colorHex, notes ->
                if (goalToEdit != null) {
                    val updated = goalToEdit.copy(
                        title = title,
                        targetAmount = targetAmt,
                        savedAmount = initAmt,
                        targetDateMillis = targetDate,
                        icon = icon,
                        colorHex = colorHex,
                        notes = notes
                    )
                    viewModel.updateSavingsGoal(updated)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = if (strings.isBangla) "লক্ষ্য আপডেট করা হয়েছে" else "Goal updated",
                            amount = targetAmt,
                            currencySymbol = currencySymbol,
                            subtitle = title
                        )
                    )
                } else {
                    viewModel.addSavingsGoal(title, targetAmt, initAmt, targetDate, icon, colorHex, notes)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = if (strings.isBangla) "নতুন সঞ্চয় লক্ষ্য যুক্ত হয়েছে" else "Savings goal added",
                            amount = targetAmt,
                            currencySymbol = currencySymbol,
                            subtitle = title
                        )
                    )
                }
                onDismissAddEdit()
            }
        )
    }

    if (goalForContribution != null) {
        AddSavingsContributionSheet(
            goal = goalForContribution,
            accounts = accounts,
            currencySymbol = currencySymbol,
            onDismiss = onDismissContribution,
            onConfirm = { amount, fromAccountId ->
                val target = goalForContribution
                val willReach = (target.remainingAmount - amount) <= 0.001
                viewModel.addSavingsContribution(target.id, amount, fromAccountId)
                if (willReach) {
                    onGoalCelebrated()
                }
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.GENERIC_SUCCESS,
                        title = if (willReach) strings.goalReachedCelebration else strings.addSavingsContribution,
                        amount = amount,
                        currencySymbol = currencySymbol,
                        subtitle = target.title
                    )
                )
                onDismissContribution()
            }
        )
    }

    if (goalToDelete != null) {
        DeleteGoalConfirmDialog(
            targetGoal = goalToDelete,
            onDismiss = onDismissDelete,
            onConfirmDelete = {
                viewModel.deleteSavingsGoal(goalToDelete.id)
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.ITEM_DELETED,
                        title = FeedbackStrings.itemDeleted(strings.isBangla),
                        subtitle = goalToDelete.title
                    )
                )
                onDismissDelete()
            }
        )
    }

    ConfettiCelebration(
        trigger = showConfetti,
        onAnimationEnd = onConfettiDismiss
    )
}
