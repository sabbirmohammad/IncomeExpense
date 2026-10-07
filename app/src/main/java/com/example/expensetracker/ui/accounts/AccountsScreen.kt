package com.example.expensetracker.ui.accounts

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData

@OptIn(ExperimentalMaterial3Api::class)
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
    val filterType by viewModel.selectedAccountFilter.collectAsState()
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val accountTransactions by viewModel.accountTransactions.collectAsState()

    var showAddEditSheet by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<Account?>(null) }
    var showTransferSheet by remember { mutableStateOf(false) }
    var showAccountDetailsSheet by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }
    var accountToReconcile by remember { mutableStateOf<Account?>(null) }

    val filteredAccounts = accounts

    val totalNetWorth = remember(accounts) { accounts.sumOf { it.currentBalance } }
    val totalBank = remember(accounts) { accounts.filter { it.type == AccountType.BANK }.sumOf { it.currentBalance } }
    val totalMfs = remember(accounts) { accounts.filter { it.type == AccountType.MFS }.sumOf { it.currentBalance } }
    val totalCash = remember(accounts) { accounts.filter { it.type == AccountType.CASH }.sumOf { it.currentBalance } }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        // 1. Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onMenuClick != null) {
                DrawerMenuButton(onClick = onMenuClick)
                Spacer(modifier = Modifier.width(12.dp))
            } else if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ColorTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = strings.accountsTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = ColorTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ScreenPaddingHorizontal)
        ) {
            // 2. Net Worth Summary Card (Redesigned like Dashboard BalanceCard with centered hero balance)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(Color(0xFF2E3A56), Color(0xFF1B2338))
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF151C2F), Color(0xFF0E1321))
                                )
                            )
                            .padding(vertical = 22.dp, horizontal = 18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header badge with glowing dot - CENTERED
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (totalNetWorth < 0) ColorDangerExpense else ColorAccent)
                                )
                                Text(
                                    text = if (strings.isBangla) "মোট নেট ব্যালেন্স" else "Total Net Balance",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.2.sp
                                    ),
                                    color = ColorTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Large Center Hero Balance
                            Text(
                                text = formatCurrency(totalNetWorth, userProfile.currencySymbol),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = ColorTextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HairlineDivider(color = Color(0xFF222B42))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Category Source Breakdown Chips (Banks, MFS, Cash)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SourceStatCard(
                                    modifier = Modifier.weight(1f),
                                    label = strings.banksTab,
                                    amount = totalBank,
                                    count = accounts.count { it.type == AccountType.BANK },
                                    symbol = userProfile.currencySymbol,
                                    icon = Icons.Outlined.AccountBalance,
                                    color = Color(0xFF3B8FF0),
                                    isBangla = strings.isBangla
                                )
                                SourceStatCard(
                                    modifier = Modifier.weight(1f),
                                    label = strings.mfsTab,
                                    amount = totalMfs,
                                    count = accounts.count { it.type == AccountType.MFS },
                                    symbol = userProfile.currencySymbol,
                                    icon = Icons.Outlined.PhoneAndroid,
                                    color = Color(0xFFE056FD),
                                    isBangla = strings.isBangla
                                )
                                SourceStatCard(
                                    modifier = Modifier.weight(1f),
                                    label = strings.cashTab,
                                    amount = totalCash,
                                    count = accounts.count { it.type == AccountType.CASH },
                                    symbol = userProfile.currencySymbol,
                                    icon = Icons.Outlined.Payments,
                                    color = Color(0xFF2EC4B6),
                                    isBangla = strings.isBangla
                                )
                            }
                        }
                    }
                }
            }

            // 3. Quick Action Buttons: Add Account & Transfer
            item {
                Spacer(modifier = Modifier.height(CardGap))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // + Add Account
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .clickable {
                                accountToEdit = null
                                showAddEditSheet = true
                            },
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = ColorAccent.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.addAccount,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorAccent
                            )
                        }
                    }

                    // Transfer Funds
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .clickable { showTransferSheet = true },
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = ColorSurface,
                        border = BorderStroke(1.dp, ColorBorder)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SwapHoriz,
                                contentDescription = null,
                                tint = ColorTextPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.transferFunds,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorTextPrimary
                            )
                        }
                    }
                }
            }

            // 4. Accounts List
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

            if (filteredAccounts.isEmpty()) {
                item {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        padding = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalance,
                                contentDescription = null,
                                tint = ColorTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (strings.isBangla) "কোনো অ্যাকাউন্ট পাওয়া যায়নি" else "No accounts found",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                color = ColorTextPrimary
                            )
                            Text(
                                text = if (strings.isBangla) "ব্যাংক বা এমএফএস ওয়ালেট যুক্ত করতে 'অ্যাকাউন্ট যোগ করুন' চাপুন।" else "Tap 'Add Account' to add your bank or MFS wallet.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredAccounts, key = { it.id }) { acc ->
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
                        onDelete = {
                            accountToDelete = acc
                        },
                        onReconcile = {
                            accountToReconcile = acc
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Modal: Add / Edit Account
    if (showAddEditSheet) {
        AddEditAccountSheet(
            account = accountToEdit,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = {
                showAddEditSheet = false
                accountToEdit = null
            },
            onSave = { acc ->
                if (accountToEdit != null) {
                    viewModel.updateAccount(acc)
                } else {
                    viewModel.addAccount(
                        name = acc.name,
                        type = acc.type,
                        institution = acc.institutionName,
                        accountNumber = acc.accountNumber,
                        initialBalance = acc.initialBalance,
                        colorHex = acc.colorHex
                    )
                }
                showAddEditSheet = false
                accountToEdit = null
            }
        )
    }

    // Modal: Transfer Funds
    if (showTransferSheet) {
        TransferFundsSheet(
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showTransferSheet = false },
            onTransfer = { fromId, toId, amount, note ->
                viewModel.transferFunds(fromId, toId, amount, note)
                showTransferSheet = false
            }
        )
    }

    // Modal: Account Detail & Transaction History
    if (showAccountDetailsSheet && selectedAccount != null) {
        AccountDetailSheet(
            account = selectedAccount!!,
            transactions = accountTransactions,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = {
                showAccountDetailsSheet = false
                viewModel.selectAccount(null)
            },
            onEdit = {
                accountToEdit = selectedAccount
                showAccountDetailsSheet = false
                showAddEditSheet = true
            },
            onReconcile = {
                accountToReconcile = selectedAccount
            }
        )
    }

    // Reconcile Balance Dialog
    if (accountToReconcile != null) {
        ReconcileBalanceDialog(
            account = accountToReconcile!!,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { accountToReconcile = null },
            onConfirm = { newBal, note ->
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
            }
        )
    }

    // Delete Confirmation Dialog
    if (accountToDelete != null) {
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            title = {
                Text(
                    text = if (strings.isBangla) "${accountToDelete!!.name} মুছে ফেলবেন?" else "Delete ${accountToDelete!!.name}?",
                    color = ColorTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla) "আপনি কি নিশ্চিতভাবে এই অ্যাকাউন্টটি মুছে ফেলতে চান? সংশ্লিষ্ট লেনদেনগুলো সংরক্ষিত থাকবে।" else "Are you sure you want to remove this account? Associated transactions will be kept and set to unlinked.",
                    color = ColorTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToDelete = null }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }
}

/**
 * Modern Card for an individual Bank/MFS/Cash Account
 */
@Composable
private fun AccountCardItem(
    account: Account,
    currencySymbol: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReconcile: () -> Unit
) {
    val strings = LocalAppStrings.current
    val brandStyle = BrandIdentityHelper.getBrandStyle(account)
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF131728),
        border = BorderStroke(1.dp, brandStyle.primaryColor.copy(alpha = 0.28f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            brandStyle.primaryColor.copy(alpha = 0.12f),
                            Color(0xFF131728).copy(alpha = 0.90f),
                            Color(0xFF0F1220)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Branded Icon Box
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(brandStyle.gradient)
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = brandStyle.icon,
                                contentDescription = null,
                                tint = if (brandStyle.isDarkCard) Color.White else Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = account.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (account.isDefault) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = ColorAccent.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "DEFAULT",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorAccent,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = when {
                                    account.institutionName.isNotBlank() && account.accountNumber.isNotBlank() ->
                                        "${account.institutionName} • •••• ${account.accountNumber.takeLast(4)}"
                                    account.institutionName.isNotBlank() -> account.institutionName
                                    account.accountNumber.isNotBlank() -> "•••• ${account.accountNumber.takeLast(4)}"
                                    else -> account.type.getDisplayName(strings.isBangla)
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = ColorTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Right side: Balance & 3-dot menu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatCurrency(account.currentBalance, currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (account.currentBalance >= 0) ColorTextPrimary else ColorDangerExpense
                            )
                            Text(
                                text = if (strings.isBangla) "ব্যালেন্স" else "Balance",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTextMuted
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = ColorTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(ColorSurface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text(if (strings.isBangla) "কার্ড ও ইতিহাস দেখুন" else "View Card & History", color = ColorTextPrimary) },
                                    leadingIcon = { Icon(Icons.Outlined.History, null, tint = ColorTextSecondary) },
                                    onClick = {
                                        showMenu = false
                                        onClick()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(strings.reconcileBalance, color = ColorAccent) },
                                    leadingIcon = { Icon(Icons.Outlined.AccountBalance, null, tint = ColorAccent) },
                                    onClick = {
                                        showMenu = false
                                        onReconcile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(strings.editAccount, color = ColorTextPrimary) },
                                    leadingIcon = { Icon(Icons.Outlined.Edit, null, tint = ColorTextSecondary) },
                                    onClick = {
                                        showMenu = false
                                        onEdit()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(strings.delete, color = ColorDangerExpense) },
                                    leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = ColorDangerExpense) },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Tag & Tap hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = brandStyle.primaryColor.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, brandStyle.primaryColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = when (account.type) {
                                AccountType.BANK -> if (strings.isBangla) "🏦 ব্যাংক অ্যাকাউন্ট" else "🏦 Bank Account"
                                AccountType.MFS -> if (strings.isBangla) "📱 মোবাইল ওয়ালেট" else "📱 Mobile Wallet"
                                AccountType.CASH -> if (strings.isBangla) "💵 ক্যাশ ইন হ্যান্ড" else "💵 Cash Wallet"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (brandStyle.isDarkCard) brandStyle.primaryColor else ColorAccent,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = if (strings.isBangla) "কার্ড দেখুন" else "View Card",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorAccent
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier
                                .size(11.dp)
                                .scale(scaleX = -1f, scaleY = 1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Realistic Digital Financial Card (Bank / Debit / Wallet / Cash)
 */
@Composable
fun DigitalAccountCard(
    account: Account,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val brandStyle = BrandIdentityHelper.getBrandStyle(account)
    val isLightContent = brandStyle.isDarkCard

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brandStyle.gradient)
        ) {
            // Decorative background circles for gloss/ambient depth
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset(x = 180.dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.07f))
            )
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 220.dp, y = 50.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Institution Name & Brand + EMV Chip / Type Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Brand Icon Badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = brandStyle.icon,
                                contentDescription = null,
                                tint = if (isLightContent) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (account.institutionName.isNotBlank()) account.institutionName else brandStyle.brandName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = if (isLightContent) Color.White else Color.Black
                            )
                            Text(
                                text = when (account.type) {
                                    AccountType.BANK -> "BANKING"
                                    AccountType.MFS -> "MOBILE WALLET"
                                    AccountType.CASH -> "PHYSICAL CASH"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.2.sp
                                ),
                                color = if (isLightContent) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Card Chip Graphic (Metallic EMV simulation) + Type Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // EMV Chip Simulation
                        Box(
                            modifier = Modifier
                                .size(width = 32.dp, height = 24.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFDF7A), Color(0xFFD4AF37), Color(0xFFB8860B))
                                    )
                                )
                                .border(0.5.dp, Color(0xFF8B6508), RoundedCornerShape(5.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 22.dp, height = 14.dp)
                                    .border(0.5.dp, Color(0xFF8B6508).copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            )
                        }

                        // Type Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLightContent) Color.White.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = when (account.type) {
                                    AccountType.BANK -> "DEBIT"
                                    AccountType.MFS -> "WALLET"
                                    AccountType.CASH -> "CASH"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = if (isLightContent) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Middle: Available Balance
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = if (strings.isBangla) "বর্তমান ব্যালেন্স" else "CURRENT BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.2.sp
                        ),
                        color = if (isLightContent) Color.White.copy(alpha = 0.75f) else Color.Black.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(account.currentBalance, currencySymbol),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = if (isLightContent) Color.White else Color.Black
                    )
                }

                // Bottom: Account Title & Account Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = if (strings.isBangla) "অ্যাকাউন্টের নাম" else "ACCOUNT NAME",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                letterSpacing = 1.sp
                            ),
                            color = if (isLightContent) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.55f)
                        )
                        Text(
                            text = account.name.uppercase(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = if (isLightContent) Color.White else Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (strings.isBangla) "অ্যাকাউন্ট নম্বর" else "ACCOUNT NO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                letterSpacing = 1.sp
                            ),
                            color = if (isLightContent) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.55f)
                        )
                        Text(
                            text = when {
                                account.accountNumber.length >= 8 ->
                                    "•••• •••• ${account.accountNumber.takeLast(4)}"
                                account.accountNumber.isNotBlank() ->
                                    "•••• ${account.accountNumber}"
                                account.type == AccountType.CASH ->
                                    "CASH WALLET"
                                else ->
                                    "•••• ${account.id.toString().padStart(4, '0')}"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.2.sp
                            ),
                            color = if (isLightContent) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Breakdown stat card inside top balance card
 */
@Composable
private fun SourceStatCard(
    modifier: Modifier = Modifier,
    label: String,
    amount: Double,
    count: Int,
    symbol: String,
    icon: ImageVector,
    color: Color,
    isBangla: Boolean
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF101524),
        border = BorderStroke(1.dp, color.copy(alpha = 0.22f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatCurrency(amount, symbol),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "$count ${if (isBangla) "টি" else "acc"}",
                fontSize = 9.5.sp,
                color = ColorTextMuted
            )
        }
    }
}

/**
 * Add / Edit Account Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddEditAccountSheet(
    account: Account?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Account) -> Unit
) {
    val strings = LocalAppStrings.current
    var type by remember { mutableStateOf(account?.type ?: AccountType.BANK) }
    var name by remember { mutableStateOf(account?.name ?: "") }
    var institution by remember { mutableStateOf(account?.institutionName ?: "") }
    var accountNumber by remember { mutableStateOf(account?.accountNumber ?: "") }
    var initialBalanceText by remember {
        mutableStateOf(account?.initialBalance?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "0")
    }
    var selectedColor by remember { mutableStateOf(account?.colorHex ?: "#3B8FF0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val presetColors = listOf("#3B8FF0", "#2EC4B6", "#E056FD", "#F0A63D", "#3DDC97", "#FF758F", "#9D4EDD", "#F0716B")

    // Preset institutions suggestions
    val bankSuggestions = listOf("City Bank", "Brac Bank", "Dutch-Bangla Bank", "Eastern Bank", "Islami Bank", "Standard Chartered", "Sonali Bank", "UCB")
    val mfsSuggestions = listOf("bKash", "Nagad", "Rocket", "Upay", "Cellfin", "SureCash")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (account != null) strings.editAccount else strings.addAccount,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = ColorTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Type Selector
            Text(strings.accountTypeLabel, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(AccountType.BANK, AccountType.MFS, AccountType.CASH).forEach { t ->
                    val isSelected = type == t
                    Surface(
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = if (isSelected) ColorAccent else ColorBackground,
                        border = if (isSelected) null else BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                type = t
                                if (name.isBlank() || name == "Bank Account" || name == "bKash" || name == "Cash in Hand") {
                                    name = when (t) {
                                        AccountType.BANK -> "City Bank"
                                        AccountType.MFS -> "bKash"
                                        AccountType.CASH -> "Cash in Hand"
                                    }
                                    institution = when (t) {
                                        AccountType.BANK -> "City Bank"
                                        AccountType.MFS -> "bKash"
                                        AccountType.CASH -> "Cash"
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (t) {
                                    AccountType.BANK -> Icons.Outlined.AccountBalance
                                    AccountType.MFS -> Icons.Outlined.PhoneAndroid
                                    AccountType.CASH -> Icons.Outlined.Payments
                                },
                                contentDescription = null,
                                tint = if (isSelected) Color.White else ColorTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (t) {
                                    AccountType.BANK -> strings.banksTab
                                    AccountType.MFS -> strings.mfsTab
                                    AccountType.CASH -> strings.cashTab
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else ColorTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Provider Chips
            if (type != AccountType.CASH) {
                val suggestions = if (type == AccountType.BANK) bankSuggestions else mfsSuggestions
                Text(strings.quickSelectProvider, fontSize = 11.sp, color = ColorTextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.forEach { s ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (institution == s) ColorAccent.copy(alpha = 0.2f) else ColorBackground,
                            border = BorderStroke(1.dp, if (institution == s) ColorAccent else ColorBorder),
                            modifier = Modifier.clickable {
                                institution = s
                                if (name.isBlank() || bankSuggestions.contains(name) || mfsSuggestions.contains(name)) {
                                    name = s
                                }
                            }
                        ) {
                            Text(
                                text = s,
                                fontSize = 11.sp,
                                color = if (institution == s) ColorAccent else ColorTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Account Name Field
            Text(strings.accountName, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(if (strings.isBangla) "যেমন: ব্র্যাক স্যালারি অ্যাকাউন্ট, বিকাশ পার্সোনাল" else "e.g. Brac Salary Account, bKash Personal", color = ColorTextMuted) },
                singleLine = true,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Institution Name Field
            if (type != AccountType.CASH) {
                Text(strings.institutionName, fontSize = 12.sp, color = ColorTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    placeholder = { Text(if (strings.isBangla) "যেমন: ডাচ-বাংলা ব্যাংক, বিকাশ" else "e.g. Dutch-Bangla Bank, bKash", color = ColorTextMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(SmallElementRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ColorBackground,
                        unfocusedContainerColor = ColorBackground,
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = ColorBorder,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Account Number / Phone
                val accNumberLabel = if (strings.isBangla) {
                    if (type == AccountType.BANK) "অ্যাকাউন্ট বা কার্ড নম্বর (ঐচ্ছিক)" else "মোবাইল বা ওয়ালেট নম্বর (ঐচ্ছিক)"
                } else {
                    if (type == AccountType.BANK) "Account Number or Card (Optional)" else "Phone or Wallet Number (Optional)"
                }
                Text(accNumberLabel, fontSize = 12.sp, color = ColorTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    placeholder = { Text(if (strings.isBangla) (if (type == AccountType.BANK) "উদাঃ ১৫৮৯ ..." else "উদাঃ ০১৭XXXXXXXX") else (if (type == AccountType.BANK) "e.g. 1589 ..." else "e.g. 017XXXXXXXX"), color = ColorTextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(SmallElementRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ColorBackground,
                        unfocusedContainerColor = ColorBackground,
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = ColorBorder,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Initial Balance Field
            Text("${strings.initialBalance} ($currencySymbol)", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = initialBalanceText,
                onValueChange = { initialBalanceText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " } },
                placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = ColorTextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            CalculatorPreviewBadge(
                expression = initialBalanceText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    initialBalanceText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = initialBalanceText,
                onExpressionChanged = { initialBalanceText = it },
                onApplyResult = { evaluated ->
                    initialBalanceText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Color Picker
            Text(strings.accountColorTheme, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                presetColors.forEach { cHex ->
                    val isPicked = selectedColor.equals(cHex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CategoryIconHelper.parseColor(cHex))
                            .clickable { selectedColor = cHex }
                            .then(
                                if (isPicked) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPicked) {
                            Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = ColorDangerExpense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = if (strings.isBangla) "অনুগ্রহ করে অ্যাকাউন্টের নাম লিখুন" else "Please enter an account name"
                        return@Button
                    }
                    val initialBal = initialBalanceText.toParsedAmountOrNull() ?: 0.0
                    val updated = (account ?: Account(name = name, type = type)).copy(
                        name = name.trim(),
                        type = type,
                        institutionName = institution.trim(),
                        accountNumber = accountNumber.trim(),
                        initialBalance = initialBal,
                        colorHex = selectedColor
                    )
                    onSave(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Text(
                    text = if (account != null) strings.updateAccount else strings.saveAccount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



/**
 * Account Detail Sheet: Shows dedicated digital card, financial flow summary, and transaction history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountDetailSheet(
    account: Account,
    transactions: List<Transaction>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onReconcile: () -> Unit
) {
    val strings = LocalAppStrings.current

    val totalInflow = remember(transactions) {
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val totalOutflow = remember(transactions) {
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Sheet Header with Title & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (strings.isBangla) "অ্যাকাউন্ট ও কার্ড বিস্তারিত" else "Account & Card Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onReconcile) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalance,
                            contentDescription = strings.reconcileBalance,
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = ColorTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = ColorTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // DEDICATED REALISTIC DIGITAL CARD
            DigitalAccountCard(
                account = account,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Metric Summary (Initial, Inflow, Outflow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AccountStatMiniCard(
                    modifier = Modifier.weight(1f),
                    label = if (strings.isBangla) "প্রারম্ভিক" else "Initial",
                    amount = formatCurrency(account.initialBalance, currencySymbol),
                    color = ColorTextSecondary
                )
                AccountStatMiniCard(
                    modifier = Modifier.weight(1f),
                    label = if (strings.isBangla) "মোট জমা" else "Inflow",
                    amount = "+${formatCurrency(totalInflow, currencySymbol)}",
                    color = ColorSuccessIncome
                )
                AccountStatMiniCard(
                    modifier = Modifier.weight(1f),
                    label = if (strings.isBangla) "মোট খরচ" else "Outflow",
                    amount = "-${formatCurrency(totalOutflow, currencySymbol)}",
                    color = ColorDangerExpense
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = onReconcile,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorAccent),
                border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.45f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.reconcileBalance,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (strings.isBangla) "লেনদেনের ইতিহাস (${transactions.size})" else "TRANSACTIONS HISTORY (${transactions.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextMuted,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (strings.isBangla) "এই অ্যাকাউন্টের সাথে এখনও কোনো লেনদেন নেই।" else "No transactions linked to this account yet.",
                        color = ColorTextMuted,
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                ) {
                    itemsIndexed(transactions) { index, tx ->
                        val isIncome = tx.type == TransactionType.INCOME
                        val amountFormatted = "${if (isIncome) "+" else "-"} ${formatCurrency(tx.amount, currencySymbol, globalBengaliNumeralsEnabled || strings.isBangla)}"
                        val amountColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense

                        ListRow(
                            iconName = tx.categoryIcon,
                            categoryColorHex = tx.categoryColor,
                            title = tx.title,
                            meta = "${getLocalizedCategoryName(tx.categoryName, strings.isBangla)} • ${DateTimeHelper.formatItemDateTime(tx.dateMillis, strings.isBangla, globalBengaliNumeralsEnabled || strings.isBangla)}",
                            amountText = amountFormatted,
                            amountColor = amountColor,
                            paymentMethod = tx.paymentMethod
                        )
                        if (index < transactions.lastIndex) {
                            HairlineDivider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Account financial stat mini card
 */
@Composable
private fun AccountStatMiniCard(
    modifier: Modifier = Modifier,
    label: String,
    amount: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ColorBackground,
        border = BorderStroke(1.dp, ColorBorder.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = ColorTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = amount,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Dialog to reconcile an account's actual physical balance with the app's tracked balance.
 * Automatically computes difference and creates an adjustment transaction.
 */
@Composable
private fun ReconcileBalanceDialog(
    account: Account,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    var newBalanceText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    val currentBal = account.currentBalance
    val parsedNewBal = newBalanceText.toDoubleOrNull()
    val diff = if (parsedNewBal != null) parsedNewBal - currentBal else null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface,
        title = {
            Column {
                Text(
                    text = strings.reconcileBalance,
                    color = appColors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = account.name,
                    color = appColors.textSecondary,
                    fontSize = 13.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Current App Balance Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = appColors.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (strings.isBangla) "অ্যাপের বর্তমান ব্যালেন্স" else "Current App Balance",
                            color = appColors.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatCurrency(currentBal, currencySymbol),
                            color = appColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // New Actual Balance Input
                OutlinedTextField(
                    value = newBalanceText,
                    onValueChange = { newBalanceText = it },
                    label = { Text(strings.actualCurrentBalance) },
                    placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = appColors.border,
                        focusedLabelColor = ColorAccent,
                        unfocusedLabelColor = appColors.textMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Difference indicator badge
                if (diff != null) {
                    val isGain = diff > 0
                    val isLoss = diff < 0
                    val diffColor = when {
                        isGain -> ColorSuccessIncome
                        isLoss -> ColorDangerExpense
                        else -> appColors.textMuted
                    }
                    val diffText = when {
                        isGain -> "+ ${formatCurrency(diff, currencySymbol)}"
                        isLoss -> "- ${formatCurrency(kotlin.math.abs(diff), currencySymbol)}"
                        else -> if (strings.isBangla) "কোনো পার্থক্য নেই" else "0 difference (Balances match)"
                    }
                    val diffTypeLabel = when {
                        isGain -> if (strings.isBangla) "সমন্বয় আয় হিসেবে যোগ হবে" else "Will log as adjustment income"
                        isLoss -> if (strings.isBangla) "সমন্বয় ব্যয় হিসেবে বাদ হবে" else "Will log as adjustment expense"
                        else -> ""
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = diffColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, diffColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.balanceDifference,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = appColors.textSecondary
                                )
                                Text(
                                    text = diffText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = diffColor
                                )
                            }
                            if (diffTypeLabel.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = diffTypeLabel,
                                    fontSize = 10.5.sp,
                                    color = diffColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Optional Note
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(if (strings.isBangla) "নোট (ঐচ্ছিক)" else "Note (Optional)") },
                    placeholder = { Text(strings.reconciliationNote) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = appColors.border,
                        focusedLabelColor = ColorAccent,
                        unfocusedLabelColor = appColors.textMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (parsedNewBal != null) {
                        onConfirm(parsedNewBal, noteText)
                    }
                },
                enabled = parsedNewBal != null && kotlin.math.abs(parsedNewBal - currentBal) > 0.001,
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Text(strings.confirm, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        }
    )
}

