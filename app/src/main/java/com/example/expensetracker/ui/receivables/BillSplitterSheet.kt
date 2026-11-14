package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.BillSplitFriend
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSplitterSheet(
    accounts: List<Account>,
    categories: List<Category>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirmSplit: (BillSplitData) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val context = LocalContext.current

    var totalAmountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf(if (strings.isBangla) "গ্রুপ আড্ডা" else "Group Hangout") }
    var selectedAccount by remember {
        mutableStateOf(accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull())
    }
    val defaultCategory = categories.find { it.name.contains("Food", ignoreCase = true) || it.name.contains("Dining", ignoreCase = true) }
        ?: categories.firstOrNull()

    val friends = remember {
        mutableStateListOf(
            BillSplitFriend(name = if (strings.isBangla) "বন্ধু ১" else "Friend 1", amount = 0.0),
            BillSplitFriend(name = if (strings.isBangla) "বন্ধু ২" else "Friend 2", amount = 0.0)
        )
    }

    var isSplitEqually by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val totalBill = totalAmountText.toParsedAmountOrNull() ?: 0.0
    val totalPeople = friends.size + 1
    val equalShare = if (totalPeople > 0 && totalBill > 0) totalBill / totalPeople else 0.0
    val myShare = if (isSplitEqually) equalShare else (totalBill - friends.sumOf { it.amount }).coerceAtLeast(0.0)
    val friendsTotal = if (isSplitEqually) equalShare * friends.size else friends.sumOf { it.amount }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = appColors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BillSplitterHeader(title = strings.splitBillTitle, onClose = onDismiss, closeDesc = strings.close)

            Spacer(modifier = Modifier.height(16.dp))

            BillSplitterAmountInput(
                amountText = totalAmountText,
                onAmountChange = { totalAmountText = it },
                currencySymbol = currencySymbol,
                label = strings.totalBill,
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(14.dp))

            BillSplitterAccountAndNoteSection(
                accounts = accounts,
                selectedAccount = selectedAccount,
                currencySymbol = currencySymbol,
                onAccountSelect = { selectedAccount = it },
                note = note,
                onNoteChange = { note = it },
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(14.dp))

            BillSplitterModeToggle(
                isSplitEqually = isSplitEqually,
                isBangla = strings.isBangla,
                onSplitModeChange = { isSplitEqually = it },
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(12.dp))

            BillSplitterSummaryCard(
                myShareLabel = strings.myShare,
                myShare = myShare,
                friendsCount = friends.size,
                friendsTotalShare = friendsTotal,
                currencySymbol = currencySymbol,
                isBangla = strings.isBangla
            )

            Spacer(modifier = Modifier.height(14.dp))

            BillSplitterFriendsSection(
                friends = friends,
                isBangla = strings.isBangla,
                isSplitEqually = isSplitEqually,
                equalShare = equalShare,
                currencySymbol = currencySymbol,
                addFriendLabel = strings.addFriend,
                haptics = haptics
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage ?: "", color = appColors.expense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val billShareMessage = createBillShareMessage(strings.isBangla, note, totalBill, equalShare, currencySymbol)
            BillSplitterShareRow(
                context = context,
                billShareMessage = billShareMessage,
                copiedText = strings.copiedToClipboard,
                isBangla = strings.isBangla,
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(12.dp))

            BillSplitterConfirmButton(
                totalBill = totalBill,
                selectedAccount = selectedAccount,
                defaultCategory = defaultCategory,
                myShare = myShare,
                friends = friends,
                isSplitEqually = isSplitEqually,
                equalShare = equalShare,
                note = note,
                isBangla = strings.isBangla,
                buttonText = strings.sendToReceivables,
                onError = { errorMessage = it },
                onSuccess = { splitData ->
                    onConfirmSplit(splitData)
                    onDismiss()
                },
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
