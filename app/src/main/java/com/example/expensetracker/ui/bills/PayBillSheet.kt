package com.example.expensetracker.ui.bills

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayBillSheet(
    bill: RecurringBill,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirmPay: (accountId: Long?, accountName: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    var selectedAccount by remember {
        mutableStateOf(accounts.find { it.id == bill.accountId } ?: accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F131D),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(ColorBorder)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (strings.isBangla) "বিল পরিশোধ করুন" else "Pay Recurring Bill",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = ColorTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppCard(
                modifier = Modifier.fillMaxWidth(),
                padding = 14.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bill.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = ColorTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = getLocalizedCategoryName(bill.categoryName, strings.isBangla),
                            fontSize = 12.sp,
                            color = ColorTextMuted
                        )
                    }
                    Text(
                        text = formatCurrency(bill.amount, currencySymbol),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColorDangerExpense
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (accounts.isNotEmpty()) {
                Text(
                    text = if (strings.isBangla) "পরিশোধের অ্যাকাউন্ট:" else "Pay From Account:",
                    fontSize = 12.sp,
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.forEach { acc ->
                        val isSelected = selectedAccount?.id == acc.id
                        val brandStyle = BrandIdentityHelper.getBrandStyle(acc)

                        Surface(
                            shape = RoundedCornerShape(SmallElementRadius),
                            color = if (isSelected) brandStyle.primaryColor.copy(alpha = 0.2f) else Color(0xFF0F131D),
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) brandStyle.primaryColor else ColorBorder),
                            modifier = Modifier.clickable {
                                haptics.lightTick()
                                selectedAccount = acc
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(brandStyle.gradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = brandStyle.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = acc.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        ),
                                        color = ColorTextPrimary
                                    )
                                    Text(
                                        text = formatCurrency(acc.currentBalance, currencySymbol),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (isSelected) brandStyle.primaryColor else ColorTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    haptics.successFeedback()
                    onConfirmPay(selectedAccount?.id, selectedAccount?.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccessIncome)
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (strings.isBangla) "পরিশোধ নিশ্চিত করুন" else "Confirm & Pay",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
