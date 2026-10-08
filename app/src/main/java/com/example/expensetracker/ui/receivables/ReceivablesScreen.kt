package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivablesScreen(
    viewModel: ReceivablesViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val receivables by viewModel.receivables.collectAsState()
    val summary by viewModel.receivablesSummary.collectAsState()
    val payables by viewModel.payables.collectAsState()
    val payablesSummary by viewModel.payablesSummary.collectAsState()
    val debtBookSummary by viewModel.debtBookSummary.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val generatedReminder by viewModel.generatedReminder.collectAsState()
    val isGeneratingReminder by viewModel.isGeneratingReminder.collectAsState()
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    var showAddEditSheet by remember { mutableStateOf(false) }
    var showAddEditPayableSheet by remember { mutableStateOf(false) }
    var showSplitBillSheet by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var receivableToEdit by remember { mutableStateOf<Receivable?>(null) }
    var receivableForPayment by remember { mutableStateOf<Receivable?>(null) }
    var receivableToDelete by remember { mutableStateOf<Receivable?>(null) }
    var payableToEdit by remember { mutableStateOf<Payable?>(null) }
    var payableForPayment by remember { mutableStateOf<Payable?>(null) }
    var payableToDelete by remember { mutableStateOf<Payable?>(null) }
    var selectedReceivableForReminder by remember { mutableStateOf<Receivable?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        ReceivablesTopBar(
            selectedTab = selectedTab,
            isPrivacyMode = isPrivacyMode,
            onTogglePrivacy = { viewModel.togglePrivacyMode() },
            onSplitBillClick = { showSplitBillSheet = true },
            onQuickAddClick = {
                if (selectedTab == DebtLedgerTab.RECEIVABLES) {
                    receivableToEdit = null
                    showAddEditSheet = true
                } else {
                    payableToEdit = null
                    showAddEditPayableSheet = true
                }
            },
            onNavigateBack = onNavigateBack,
            onMenuClick = onMenuClick
        )

        ReceivablesTabsAndNetBanner(
            selectedTab = selectedTab,
            onTabSelected = { viewModel.setSelectedTab(it) },
            summary = summary,
            payablesSummary = payablesSummary,
            debtBookSummary = debtBookSummary,
            isPrivacyMode = isPrivacyMode,
            currencySymbol = userProfile.currencySymbol
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ScreenPaddingHorizontal)
        ) {
            if (selectedTab == DebtLedgerTab.RECEIVABLES) {
                receivablesTabContent(
                    summary = summary,
                    receivables = receivables,
                    currencySymbol = userProfile.currencySymbol,
                    isPrivacyMode = isPrivacyMode,
                    strings = strings,
                    context = context,
                    onAddReceivable = {
                        receivableToEdit = null
                        showAddEditSheet = true
                    },
                    onRecordPayment = { receivableForPayment = it },
                    onEdit = {
                        receivableToEdit = it
                        showAddEditSheet = true
                    },
                    onDelete = { receivableToDelete = it },
                    onGenerateReminder = {
                        selectedReceivableForReminder = it
                        viewModel.generatePoliteReminder(it)
                    }
                )
            } else {
                payablesTabContent(
                    summary = payablesSummary,
                    payables = payables,
                    currencySymbol = userProfile.currencySymbol,
                    isPrivacyMode = isPrivacyMode,
                    strings = strings,
                    onAddPayable = {
                        payableToEdit = null
                        showAddEditPayableSheet = true
                    },
                    onRecordPayment = { payableForPayment = it },
                    onEdit = {
                        payableToEdit = it
                        showAddEditPayableSheet = true
                    },
                    onDelete = { payableToDelete = it }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    ReceivablesModals(
        viewModel = viewModel,
        userProfile = userProfile,
        accounts = accounts,
        categories = categories,
        showAddEditSheet = showAddEditSheet,
        receivableToEdit = receivableToEdit,
        onDismissAddEdit = { showAddEditSheet = false },
        receivableForPayment = receivableForPayment,
        onDismissPayment = { receivableForPayment = null },
        showSplitBillSheet = showSplitBillSheet,
        onDismissSplitBill = { showSplitBillSheet = false },
        receivableToDelete = receivableToDelete,
        onDismissDeleteReceivable = { receivableToDelete = null },
        showAddEditPayableSheet = showAddEditPayableSheet,
        payableToEdit = payableToEdit,
        onDismissAddEditPayable = {
            showAddEditPayableSheet = false
            payableToEdit = null
        },
        payableForPayment = payableForPayment,
        onDismissPayablePayment = { payableForPayment = null },
        payableToDelete = payableToDelete,
        onDismissDeletePayable = { payableToDelete = null },
        selectedReceivableForReminder = selectedReceivableForReminder,
        onDismissReminder = {
            selectedReceivableForReminder = null
            viewModel.clearGeneratedReminder()
        },
        generatedReminder = generatedReminder,
        isGeneratingReminder = isGeneratingReminder,
        showConfetti = showConfetti,
        onTriggerConfetti = { showConfetti = true },
        onConfettiEnd = { showConfetti = false }
    )
}
