package com.example.expensetracker.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.SegmentedControl
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
    var selectedSegment by remember { mutableIntStateOf(0) }

    val summaryStats = remember(bills) { calculateBillsSummary(bills) }
    val filteredBills = remember(bills, selectedSegment) { filterBillsBySegment(bills, selectedSegment) }

    Column(modifier = Modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        BillsTopBar(onNavigateBack = onNavigateBack, onMenuClick = onMenuClick, title = strings.billsTitle)

        Box(modifier = Modifier.fillMaxSize().widthIn(max = 840.dp).align(Alignment.CenterHorizontally)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = ScreenPaddingHorizontal, end = ScreenPaddingHorizontal, top = 4.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(key = "hero_card") {
                    BillsSummaryHeroCard(
                        totalMonthlyCommitments = summaryStats.totalMonthlyCommitments,
                        currencySymbol = userProfile.currencySymbol,
                        activeBillsCount = bills.count { it.isActive },
                        overdueCount = summaryStats.overdueCount,
                        dueSoonCount = summaryStats.dueSoonCount
                    )
                }

                item(key = "add_button") {
                    AddRecurringBillButton(onClick = { haptics.mediumClick(); billToEdit = null; showAddSheet = true })
                }

                item(key = "segmented_control") {
                    SegmentedControl(
                        items = listOf(strings.tabUpcoming, strings.tabPaid, strings.tabAll),
                        selectedIndex = selectedSegment,
                        onItemSelected = { haptics.lightTick(); selectedSegment = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item(key = "section_header") {
                    BillsSectionHeader(count = filteredBills.size)
                }

                if (filteredBills.isEmpty()) {
                    item(key = "empty_state") {
                        BillsEmptyStateCard(onAddClick = { billToEdit = null; showAddSheet = true })
                    }

                    if (bills.isEmpty()) {
                        item(key = "templates") {
                            val templates = listOf(
                                strings.templateElectricity to "Electricity",
                                strings.templateGas to "Gas",
                                strings.templateWifi to "Wi-Fi",
                                strings.templateRent to "House Rent",
                                strings.templateRecharge to "Mobile Recharge",
                                strings.templateSubscription to "Subscription"
                            )
                            BillsTemplateRow(templates = templates) { label, defaultTitle ->
                                billToEdit = RecurringBill(
                                    title = if (strings.isBangla) label else defaultTitle,
                                    amount = 0.0,
                                    categoryId = 0L,
                                    categoryName = "Bills",
                                    categoryIcon = "ReceiptLong",
                                    frequency = BillFrequency.MONTHLY,
                                    dueDay = 1
                                )
                                showAddSheet = true
                            }
                        }
                    }
                } else {
                    items(filteredBills, key = { it.id }) { bill ->
                        RecurringBillCardItem(
                            bill = bill,
                            currencySymbol = userProfile.currencySymbol,
                            onPayNow = { haptics.mediumClick(); billToPay = bill },
                            onEdit = { haptics.mediumClick(); billToEdit = bill; showAddSheet = true },
                            onDelete = { haptics.mediumClick(); billToDelete = bill }
                        )
                    }
                }
            }

            ConfettiCelebration(trigger = showConfetti, onAnimationEnd = { showConfetti = false })
        }
    }

    BillsActionSheets(
        showAddSheet = showAddSheet,
        billToEdit = billToEdit,
        billToPay = billToPay,
        viewModel = viewModel,
        categories = categories,
        accounts = accounts,
        currencySymbol = userProfile.currencySymbol,
        onDismissAdd = { showAddSheet = false },
        onDismissPay = { billToPay = null },
        onAddSaved = { showAddSheet = false }
    )

    if (billToDelete != null) {
        BillDeleteConfirmDialog(
            bill = billToDelete!!,
            onDismiss = { billToDelete = null },
            onConfirm = {
                val toDelete = billToDelete!!
                haptics.warningFeedback()
                viewModel.deleteBill(toDelete.id)
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.ITEM_DELETED,
                        title = FeedbackStrings.itemDeleted(strings.isBangla),
                        subtitle = toDelete.title
                    )
                )
                billToDelete = null
            }
        )
    }
}
