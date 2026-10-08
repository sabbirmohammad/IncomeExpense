package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.theme.ScreenPaddingHorizontal

@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current
    val accounts by viewModel.accounts.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val accountTransactions by viewModel.accountTransactions.collectAsState()

    var showAddEditSheet by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<Account?>(null) }
    var showTransferSheet by remember { mutableStateOf(false) }
    var showAccountDetailsSheet by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }
    var accountToReconcile by remember { mutableStateOf<Account?>(null) }

    val totalNetWorth = remember(accounts) { accounts.sumOf { it.currentBalance } }
    val totalBank = remember(accounts) { accounts.filter { it.type == AccountType.BANK }.sumOf { it.currentBalance } }
    val totalMfs = remember(accounts) { accounts.filter { it.type == AccountType.MFS }.sumOf { it.currentBalance } }
    val totalCash = remember(accounts) { accounts.filter { it.type == AccountType.CASH }.sumOf { it.currentBalance } }

    Column(modifier = Modifier.fillMaxSize().background(ColorBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenPaddingHorizontal, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onMenuClick != null) {
                DrawerMenuButton(onClick = onMenuClick)
                Spacer(modifier = Modifier.width(12.dp))
            } else if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ColorTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = strings.accountsTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                color = ColorTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = ScreenPaddingHorizontal)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                AccountsHeroCard(
                    accounts = accounts,
                    totalNetWorth = totalNetWorth,
                    totalBank = totalBank,
                    totalMfs = totalMfs,
                    totalCash = totalCash,
                    currencySymbol = userProfile.currencySymbol
                )
            }

            item {
                Spacer(modifier = Modifier.height(CardGap))
                AccountsQuickActionsRow(
                    onAddAccountClick = {
                        accountToEdit = null
                        showAddEditSheet = true
                    },
                    onTransferFundsClick = { showTransferSheet = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (strings.isBangla) "সকল অ্যাকাউন্ট" else "ALL ACCOUNTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextMuted,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (accounts.isEmpty()) {
                item { AccountsEmptyState() }
            } else {
                items(accounts, key = { it.id }) { acc ->
                    AccountCardItem(
                        account = acc,
                        currencySymbol = userProfile.currencySymbol,
                        onClick = {
                            viewModel.selectAccount(acc)
                            showAccountDetailsSheet = true
                        },
                        onEdit = {
                            accountToEdit = acc
                            showAddEditSheet = true
                        },
                        onDelete = { accountToDelete = acc },
                        onReconcile = { accountToReconcile = acc }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    AccountsModals(
        showAddEditSheet = showAddEditSheet,
        accountToEdit = accountToEdit,
        onDismissAddEdit = {
            showAddEditSheet = false
            accountToEdit = null
        },
        onSaveAccount = { acc ->
            if (accountToEdit != null) {
                viewModel.updateAccount(acc)
            } else {
                viewModel.addAccount(acc.name, acc.type, acc.institutionName, acc.accountNumber, acc.initialBalance, acc.colorHex)
            }
            showAddEditSheet = false
            accountToEdit = null
        },
        showTransferSheet = showTransferSheet,
        onDismissTransfer = { showTransferSheet = false },
        onTransferFunds = { fromId, toId, amount, note ->
            viewModel.transferFunds(fromId, toId, amount, note)
            showTransferSheet = false
        },
        showAccountDetailsSheet = showAccountDetailsSheet,
        selectedAccount = selectedAccount,
        accountTransactions = accountTransactions,
        onDismissDetails = {
            showAccountDetailsSheet = false
            viewModel.selectAccount(null)
        },
        onEditFromDetails = {
            accountToEdit = selectedAccount
            showAccountDetailsSheet = false
            showAddEditSheet = true
        },
        onReconcileFromDetails = { accountToReconcile = selectedAccount },
        accountToReconcile = accountToReconcile,
        onDismissReconcile = { accountToReconcile = null },
        onConfirmReconcile = { newBal, note ->
            val acc = accountToReconcile!!
            accountToReconcile = null
            viewModel.reconcileBalance(acc, newBal, note)
            feedbackController.show(
                TransactionFeedbackData(
                    type = FeedbackVisualType.GENERIC_SUCCESS,
                    title = strings.reconcileSuccess,
                    subtitle = acc.name
                )
            )
        },
        currencySymbol = userProfile.currencySymbol,
        accounts = accounts
    )

    if (accountToDelete != null) {
        AccountDeleteConfirmationDialog(
            account = accountToDelete!!,
            onDismiss = { accountToDelete = null },
            onConfirmDelete = {
                val deletedName = accountToDelete!!.name
                viewModel.deleteAccount(accountToDelete!!.id)
                accountToDelete = null
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.ITEM_DELETED,
                        title = FeedbackStrings.itemDeleted(strings.isBangla),
                        subtitle = deletedName
                    )
                )
            }
        )
    }
}
