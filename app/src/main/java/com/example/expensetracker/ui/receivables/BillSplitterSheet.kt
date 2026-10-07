package com.example.expensetracker.ui.receivables

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.BillSplitFriend
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.share.ShareExpenseHelper
import java.util.Locale

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
    val haptics = rememberAppHaptics()
    val context = LocalContext.current

    var totalAmountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf(if (strings.isBangla) "গ্রুপ আড্ডা" else "Group Hangout") }
    var selectedAccount by remember {
        mutableStateOf(accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull())
    }
    val defaultCategory = categories.find { it.name.contains("Food", ignoreCase = true) || it.name.contains("Dining", ignoreCase = true) }
        ?: categories.firstOrNull()

    // Friends list
    val friends = remember {
        mutableStateListOf(
            BillSplitFriend(name = if (strings.isBangla) "বন্ধু ১" else "Friend 1", amount = 0.0),
            BillSplitFriend(name = if (strings.isBangla) "বন্ধু ২" else "Friend 2", amount = 0.0)
        )
    }

    var isSplitEqually by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val totalBill = totalAmountText.toParsedAmountOrNull() ?: 0.0

    // Auto-calculate equal shares when total or friend count changes
    val totalPeople = friends.size + 1
    val equalShare = if (totalPeople > 0 && totalBill > 0) totalBill / totalPeople else 0.0
    val myShare = if (isSplitEqually) equalShare else (totalBill - friends.sumOf { it.amount }).coerceAtLeast(0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ColorSurface,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ColorAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Group,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.splitBillTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = ColorTextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = strings.close, tint = ColorTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Bill Amount with In-Place Calculator
            OutlinedTextField(
                value = totalAmountText,
                onValueChange = { totalAmountText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " } },
                label = { Text("${strings.totalBill} ($currencySymbol)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            // Calculator Preview Badge
            CalculatorPreviewBadge(
                expression = totalAmountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    totalAmountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Calculator Keypad Bar
            CalculatorKeypadBar(
                currentExpression = totalAmountText,
                onExpressionChanged = { totalAmountText = it },
                onApplyResult = { evaluated ->
                    totalAmountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                },
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Paid from Account
            Text(text = strings.payingAccount, fontSize = 12.sp, color = ColorTextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(accounts) { acc ->
                    val isSelected = selectedAccount?.id == acc.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ColorAccent else ColorBorder.copy(alpha = 0.3f))
                            .clickable {
                                haptics.lightTick()
                                selectedAccount = acc
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${acc.name} ($currencySymbol ${String.format(Locale.US, "%,.0f", acc.currentBalance)})",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else ColorTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bill Title / Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(strings.note) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Split Mode Segmented Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorBorder.copy(alpha = 0.35f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSplitEqually) ColorAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            isSplitEqually = true
                        }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (strings.isBangla) "সমান ভাগ" else "Equal Split",
                            fontSize = 12.sp,
                            fontWeight = if (isSplitEqually) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSplitEqually) Color.White else ColorTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (!isSplitEqually) ColorAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            isSplitEqually = false
                        }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (strings.isBangla) "কাস্টম ভাগ" else "Custom Split",
                            fontSize = 12.sp,
                            fontWeight = if (!isSplitEqually) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isSplitEqually) Color.White else ColorTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary Breakdown Hero Card
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                padding = 14.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = strings.myShare, color = ColorTextMuted, fontSize = 13.sp)
                        Text(
                            text = "$currencySymbol ${String.format(Locale.US, "%,.2f", myShare)}",
                            fontWeight = FontWeight.Bold,
                            color = ColorSuccessIncome,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (strings.isBangla) "বন্ধুদের প্রাপ্য (${friends.size} জন)" else "Friends Owe (${friends.size} people)", color = ColorTextMuted, fontSize = 13.sp)
                        Text(
                            text = "$currencySymbol ${String.format(Locale.US, "%,.2f", if (isSplitEqually) equalShare * friends.size else friends.sumOf { it.amount })}",
                            fontWeight = FontWeight.Bold,
                            color = ColorDangerExpense,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Friends Header & Add Friend Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (strings.isBangla) "বিল শেয়ারকারী বন্ধুরা" else "FRIENDS SHARING BILL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextMuted,
                    letterSpacing = 1.sp
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorAccent.copy(alpha = 0.2f),
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        friends.add(
                            BillSplitFriend(name = if (strings.isBangla) "বন্ধু ${friends.size + 1}" else "Friend ${friends.size + 1}", amount = 0.0)
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = strings.addFriend, color = ColorAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Friends List Rows
            friends.forEachIndexed { index, friend ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = friend.name,
                        onValueChange = { newName ->
                            friends[index] = friend.copy(name = newName)
                        },
                        label = { Text(if (strings.isBangla) "বন্ধুর নাম" else "Friend Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = ColorBorder
                        )
                    )

                    OutlinedTextField(
                        value = friend.phoneNumber,
                        onValueChange = { newPhone ->
                            friends[index] = friend.copy(phoneNumber = newPhone)
                        },
                        label = { Text(if (strings.isBangla) "ফোন নম্বর (ঐচ্ছিক)" else "Phone (Opt)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = ColorBorder
                        )
                    )

                    if (!isSplitEqually) {
                        OutlinedTextField(
                            value = if (friend.amount > 0.0) String.format(Locale.US, "%.0f", friend.amount) else "",
                            onValueChange = { newAmt ->
                                val parsed = newAmt.toDoubleOrNull() ?: 0.0
                                friends[index] = friend.copy(amount = parsed)
                            },
                            label = { Text(if (strings.isBangla) "পরিমাণ" else "Amount") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(90.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorAccent,
                                unfocusedBorderColor = ColorBorder
                            )
                        )
                    } else {
                        val displayAmount = equalShare
                        Text(
                            text = "$currencySymbol ${String.format(Locale.US, "%,.0f", displayAmount)}",
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary,
                            fontSize = 13.sp
                        )
                    }

                    if (friends.size > 1) {
                        IconButton(
                            onClick = {
                                haptics.lightTick()
                                friends.removeAt(index)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = if (strings.isBangla) "মুছে ফেলুন" else "Remove", tint = ColorDangerExpense, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage ?: "", color = ColorDangerExpense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Share / Copy Message Buttons
            val billShareMessage = if (strings.isBangla) {
                val totalStr = com.example.expensetracker.ui.components.convertToBengaliDigits(String.format(Locale.US, "%,.0f", totalBill))
                val shareStr = com.example.expensetracker.ui.components.convertToBengaliDigits(String.format(Locale.US, "%,.0f", equalShare))
                "হেই! ${note.ifBlank { "আমাদের আড্ডা" }}-র জন্য মোট বিল হয়েছে $currencySymbol $totalStr। তোমার অংশ $currencySymbol $shareStr। সুবিধাজনক সময়ে পাঠিয়ে দিও!"
            } else {
                "Hey! For ${note.ifBlank { "our hangout" }}, total bill was $currencySymbol ${String.format(Locale.US, "%,.0f", totalBill)}. Your share is $currencySymbol ${String.format(Locale.US, "%,.0f", equalShare)}. Please send your share when convenient!"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColorBorder.copy(alpha = 0.4f),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Bill Split", billShareMessage))
                            Toast.makeText(context, strings.copiedToClipboard, Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (strings.isBangla) "মেসেজ কপি করুন" else "Copy Message", color = ColorTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColorAccent.copy(alpha = 0.18f),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            ShareExpenseHelper.shareText(context, billShareMessage, if (strings.isBangla) "বিল শেয়ার করুন" else "Share Bill Split")
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (strings.isBangla) "নোট শেয়ার করুন" else "Share Note", color = ColorTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm & Save Button
            Button(
                onClick = {
                    if (totalBill <= 0.0) {
                        errorMessage = if (strings.isBangla) "অনুগ্রহ করে মোট বিলের পরিমাণ লিখুন" else "Please enter total bill amount"
                        return@Button
                    }
                    if (defaultCategory == null) {
                        errorMessage = if (strings.isBangla) "কোনো ক্যাটাগরি পাওয়া যায়নি" else "No category available"
                        return@Button
                    }

                    val splitFriends = friends.map { f ->
                        f.copy(amount = if (isSplitEqually) equalShare else f.amount)
                    }

                    val friendsTotal = splitFriends.sumOf { it.amount }
                    if (!isSplitEqually && friendsTotal > totalBill) {
                        errorMessage = if (strings.isBangla) "বন্ধুদের প্রাপ্য মোট বিলের পরিমাণের চেয়ে বেশি হতে পারে না" else "Friends' shares cannot exceed total bill"
                        return@Button
                    }

                    haptics.successFeedback()

                    val splitData = BillSplitData(
                        totalAmount = totalBill,
                        payingAccountId = selectedAccount?.id,
                        payingAccountName = selectedAccount?.name,
                        categoryId = defaultCategory.id,
                        categoryName = defaultCategory.name,
                        myShare = myShare,
                        friends = splitFriends,
                        note = note.trim()
                    )

                    onConfirmSplit(splitData)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.sendToReceivables,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
