package com.example.expensetracker.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun BillsScreen(
    viewModel: BillsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val bills by viewModel.recurringBills.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current
    val haptics = rememberAppHaptics()

    var showAddSheet by remember { mutableStateOf(false) }
    var billToEdit by remember { mutableStateOf<RecurringBill?>(null) }
    var billToPay by remember { mutableStateOf<RecurringBill?>(null) }
    var billToDelete by remember { mutableStateOf<RecurringBill?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    val totalMonthlyCommitments = remember(bills) {
        bills.filter { it.isActive }.sumOf {
            when (it.frequency) {
                BillFrequency.MONTHLY -> it.amount
                BillFrequency.WEEKLY -> it.amount * 4.33
                BillFrequency.YEARLY -> it.amount / 12.0
            }
        }
    }

    val overdueCount = remember(bills) {
        bills.count { it.isActive && it.getPaymentStatus() == BillPaymentStatus.OVERDUE }
    }
    val dueSoonCount = remember(bills) {
        bills.count { it.isActive && it.getPaymentStatus() == BillPaymentStatus.DUE_SOON }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        BillsTopBar(
            onNavigateBack = onNavigateBack,
            onMenuClick = onMenuClick,
            title = strings.billsTitle
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = ScreenPaddingHorizontal,
                    end = ScreenPaddingHorizontal,
                    top = 4.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    BillsSummaryHeroCard(
                        totalMonthlyCommitments = totalMonthlyCommitments,
                        currencySymbol = userProfile.currencySymbol,
                        activeBillsCount = bills.count { it.isActive },
                        overdueCount = overdueCount,
                        dueSoonCount = dueSoonCount
                    )
                }

                item {
                    AddRecurringBillButton(
                        onClick = {
                            haptics.mediumClick()
                            billToEdit = null
                            showAddSheet = true
                        }
                    )
                }

                item {
                    BillsSectionHeader(count = bills.size)
                }

                if (bills.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Outlined.ReceiptLong,
                            headline = strings.noRecurringBillsHint,
                            explanation = if (strings.isBangla)
                                "আপনার নিয়মিত ইউটিলিটি, ইন্টারনেট ও সাবস্ক্রিপশন বিল যোগ করুন।"
                            else
                                "Track all your recurring subscriptions and utility bills in one place.",
                            buttonText = strings.addBill,
                            onButtonClick = {
                                billToEdit = null
                                showAddSheet = true
                            }
                        )
                    }
                } else {
                    items(bills, key = { it.id }) { bill ->
                        RecurringBillCardItem(
                            bill = bill,
                            currencySymbol = userProfile.currencySymbol,
                            onPayNow = {
                                haptics.mediumClick()
                                billToPay = bill
                            },
                            onEdit = {
                                haptics.mediumClick()
                                billToEdit = bill
                                showAddSheet = true
                            },
                            onDelete = {
                                haptics.mediumClick()
                                billToDelete = bill
                            }
                        )
                    }
                }
            }

            ConfettiCelebration(
                trigger = showConfetti,
                onAnimationEnd = { showConfetti = false }
            )
        }
    }

    if (showAddSheet) {
        AddEditBillSheet(
            billToEdit = billToEdit,
            categories = categories,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showAddSheet = false },
            onSave = { newBill ->
                val isEditing = billToEdit != null
                if (isEditing) {
                    viewModel.updateBill(newBill)
                } else {
                    viewModel.addBill(newBill)
                }
                showAddSheet = false
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.GENERIC_SUCCESS,
                        title = if (isEditing) strings.editBill else strings.allRecurringBills,
                        amount = newBill.amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = newBill.title
                    )
                )
            }
        )
    }

    if (billToPay != null) {
        PayBillSheet(
            bill = billToPay!!,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { billToPay = null },
            onConfirmPay = { accId, accName ->
                val paidBill = billToPay!!
                viewModel.payBill(paidBill.id, accId, accName) {
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.BILL_PAID,
                            title = FeedbackStrings.billPaid(strings.isBangla),
                            amount = paidBill.amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = "${paidBill.title} · $accName"
                        )
                    )
                }
                billToPay = null
            }
        )
    }

    if (billToDelete != null) {
        BillDeleteConfirmDialog(
            bill = billToDelete!!,
            onDismiss = { billToDelete = null },
            onConfirm = {
                val toDelete = billToDelete!!
                val deletedTitle = toDelete.title
                haptics.warningFeedback()
                viewModel.deleteBill(toDelete.id)
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.ITEM_DELETED,
                        title = FeedbackStrings.itemDeleted(strings.isBangla),
                        subtitle = deletedTitle
                    )
                )
                billToDelete = null
            }
        )
    }
}
