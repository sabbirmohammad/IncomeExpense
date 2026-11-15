package com.example.expensetracker.ui.bazaar

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AdaptiveSheet
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.PulseKeypad
import com.example.expensetracker.ui.components.PulseTabularText
import java.util.Calendar

private enum class ActiveKeypadTarget {
    NONE,
    TOTAL_PAID,
    OTHER_COSTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarCheckoutSheet(
    items: List<MarketItem>,
    accounts: List<Account>,
    initialAccountId: Long?,
    budgetCap: Double?,
    onConfirmCheckout: (
        accountId: Long?,
        customTotal: Double?,
        otherCosts: Double,
        dateMillis: Long,
        note: String,
        carryOver: Boolean
    ) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false,
    isPrivacyMode: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    // 1. Financial state & editing
    val initialPurchasedSum = remember(items) {
        items.filter { it.isPurchased && (it.amount ?: 0.0) > 0.0 }.sumOf { it.amount ?: 0.0 }
    }

    var customTotalAmount by remember(initialPurchasedSum) { mutableStateOf<Double?>(null) }
    var otherCosts by remember { mutableDoubleStateOf(0.0) }
    var activeKeypadTarget by remember { mutableStateOf(ActiveKeypadTarget.NONE) }
    var keypadExpression by remember { mutableStateOf("") }

    // 2. Summary & Calculations (Amendment A, B, G)
    val checkoutSummary = remember(items, customTotalAmount, otherCosts) {
        BazaarShopLogic.calculateCheckoutSummary(
            items = items,
            customTotal = customTotalAmount,
            otherCosts = otherCosts
        )
    }

    // 3. Target Wallet (default = last-used or default account)
    val defaultAccount = remember(accounts, initialAccountId) {
        if (initialAccountId != null) accounts.find { it.id == initialAccountId }
        else accounts.find { it.isDefault } ?: accounts.firstOrNull()
    }
    var selectedAccountId by remember(defaultAccount?.id) { mutableStateOf(defaultAccount?.id) }
    val selectedAccount = accounts.find { it.id == selectedAccountId }

    val isInsufficientBalance = remember(selectedAccount, checkoutSummary.finalExpenseAmount) {
        selectedAccount != null && selectedAccount.currentBalance < checkoutSummary.finalExpenseAmount
    }

    // 4. Date & Time
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // 5. Note & Carry-over switch (ON by default)
    var tripNote by remember { mutableStateOf("") }
    var carryOverUnpurchased by remember { mutableStateOf(true) }

    // 6. Explicit zero total confirmation (Amendment A)
    var isZeroTotalConfirmed by remember { mutableStateOf(false) }

    val canCompleteTrip = remember(checkoutSummary, isZeroTotalConfirmed) {
        if (!checkoutSummary.isCompleteEnabled) false
        else if (checkoutSummary.requiresZeroConfirm) isZeroTotalConfirmed
        else true
    }

    AdaptiveSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title and Close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBangla) "বাজার সমাপ্তি ও পরিশোধ" else "Checkout & Complete Trip",
                        style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary
                    )
                    Text(
                        text = if (isBangla) "হিসাব যাচাই ও ওয়ালেট চার্জ নিশ্চিত করুন" else "Review total, other costs & wallet charge",
                        style = typography.bodySmall,
                        color = appColors.textMuted
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = if (isBangla) "বন্ধ করুন" else "Close",
                        tint = appColors.textMuted
                    )
                }
            }

            // Summary Badges / Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val boughtText = if (isBangla) "${checkoutSummary.purchasedCount}টি কেনা হয়েছে" else "${checkoutSummary.purchasedCount} items bought"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.accent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.accent.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = boughtText, style = typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = appColors.accent)
                    }
                }

                val unpurchasedCount = items.size - checkoutSummary.purchasedCount
                if (unpurchasedCount > 0) {
                    val skippedText = if (isBangla) "${unpurchasedCount}টি বাকি" else "${unpurchasedCount} unbought"
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = appColors.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = skippedText,
                            style = typography.labelMedium,
                            color = appColors.textMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                if (budgetCap != null && budgetCap > 0.0) {
                    val capStr = if (budgetCap % 1.0 == 0.0) budgetCap.toInt().toString() else budgetCap.toString()
                    val capFormatted = if (useBengaliNumerals) DigitConverter.toBengaliDigits(capStr) else capStr
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = appColors.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = (if (isBangla) "বাজেট: ৳" else "Budget: ৳") + capFormatted,
                            style = typography.labelMedium,
                            color = appColors.textMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Unpriced items warning chip (Amendment B)
            if (checkoutSummary.unpricedCount > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.warning.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.warning.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = appColors.warning, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla)
                                "${checkoutSummary.unpricedCount}টি পণ্যের দাম নির্ধারণ করা হয়নি (খরচে ৳০ হিসেবে গণ্য হবে)"
                            else
                                "${checkoutSummary.unpricedCount} items have no price (omitted from expense amount)",
                            style = typography.bodySmall,
                            color = appColors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Summary Card: Base Item Sum, Edited Total, Other Costs, Live Final
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = appColors.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Line 1: Items sum
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "পণ্যের মোট মূল্য (যোগফল)" else "Purchased items sum",
                            style = typography.bodyMedium,
                            color = appColors.textMuted
                        )
                        PulseTabularText(
                            text = formatAmount(
                                amount = checkoutSummary.itemSum,
                                isBangla = isBangla,
                                useBengaliNumerals = useBengaliNumerals,
                                mask = isPrivacyMode && activeKeypadTarget == ActiveKeypadTarget.NONE
                            ),
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = appColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Line 2: Editable Total Paid
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (activeKeypadTarget == ActiveKeypadTarget.TOTAL_PAID) appColors.accent.copy(alpha = 0.08f) else appColors.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeKeypadTarget == ActiveKeypadTarget.TOTAL_PAID) appColors.accent else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (activeKeypadTarget == ActiveKeypadTarget.TOTAL_PAID) {
                                    activeKeypadTarget = ActiveKeypadTarget.NONE
                                } else {
                                    activeKeypadTarget = ActiveKeypadTarget.TOTAL_PAID
                                    keypadExpression = if (checkoutSummary.editedTotal % 1.0 == 0.0)
                                        checkoutSummary.editedTotal.toInt().toString()
                                    else
                                        checkoutSummary.editedTotal.toString()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isBangla) "পণ্য পরিশোধ (মোট)" else "Total paid for items",
                                    style = typography.labelSmall,
                                    color = appColors.textMuted
                                )
                                if (checkoutSummary.adjustment != 0.0) {
                                    val adjSign = if (checkoutSummary.adjustment > 0) "+৳" else "-৳"
                                    val absAdj = kotlin.math.abs(checkoutSummary.adjustment)
                                    val adjStr = if (absAdj % 1.0 == 0.0) absAdj.toInt().toString() else absAdj.toString()
                                    Text(
                                        text = (if (isBangla) "সমন্বয়: " else "Adjustment: ") + "$adjSign$adjStr",
                                        style = typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (checkoutSummary.adjustment < 0) appColors.accent else appColors.warning
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PulseTabularText(
                                    text = formatAmount(
                                        amount = checkoutSummary.editedTotal,
                                        isBangla = isBangla,
                                        useBengaliNumerals = useBengaliNumerals,
                                        mask = isPrivacyMode && activeKeypadTarget != ActiveKeypadTarget.TOTAL_PAID
                                    ),
                                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = appColors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = if (isBangla) "সম্পাদনা" else "Edit",
                                    tint = if (activeKeypadTarget == ActiveKeypadTarget.TOTAL_PAID) appColors.accent else appColors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Line 3: Other Costs (Rickshaw, bag, tip)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (activeKeypadTarget == ActiveKeypadTarget.OTHER_COSTS) appColors.accent.copy(alpha = 0.08f) else appColors.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeKeypadTarget == ActiveKeypadTarget.OTHER_COSTS) appColors.accent else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (activeKeypadTarget == ActiveKeypadTarget.OTHER_COSTS) {
                                    activeKeypadTarget = ActiveKeypadTarget.NONE
                                } else {
                                    activeKeypadTarget = ActiveKeypadTarget.OTHER_COSTS
                                    keypadExpression = if (otherCosts > 0) {
                                        if (otherCosts % 1.0 == 0.0) otherCosts.toInt().toString() else otherCosts.toString()
                                    } else ""
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isBangla) "অন্যান্য খরচ (রিকশা/ব্যাগ)" else "Other costs (rickshaw/bag)",
                                    style = typography.labelSmall,
                                    color = appColors.textMuted
                                )
                                Text(
                                    text = if (otherCosts <= 0) (if (isBangla) "ঐচ্ছিক" else "Optional") else (if (isBangla) "অতিরিক্ত যুক্ত" else "Added"),
                                    style = typography.labelSmall,
                                    color = appColors.textMuted
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PulseTabularText(
                                    text = formatAmount(
                                        amount = checkoutSummary.otherCosts,
                                        isBangla = isBangla,
                                        useBengaliNumerals = useBengaliNumerals,
                                        mask = isPrivacyMode && activeKeypadTarget != ActiveKeypadTarget.OTHER_COSTS
                                    ),
                                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = appColors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = if (isBangla) "সম্পাদনা" else "Edit",
                                    tint = if (activeKeypadTarget == ActiveKeypadTarget.OTHER_COSTS) appColors.accent else appColors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Line 4: Final Total to Charge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(appColors.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isBangla) "মোট চার্জ (খরচ হিসেবে যুক্ত হবে)" else "Final total to be charged",
                                    style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = appColors.textPrimary
                                )
                                Text(
                                    text = if (isBangla) "পণ্য পরিশোধ + অন্যান্য খরচ" else "Items total + other costs",
                                    style = typography.labelSmall,
                                    color = appColors.textMuted
                                )
                            }
                            PulseTabularText(
                                text = formatAmount(
                                    amount = checkoutSummary.finalExpenseAmount,
                                    isBangla = isBangla,
                                    useBengaliNumerals = useBengaliNumerals,
                                    mask = isPrivacyMode && activeKeypadTarget == ActiveKeypadTarget.NONE
                                ),
                                style = typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = appColors.accent
                            )
                        }
                    }
                }
            }

            // Inline PulseKeypad when editing Total or Other costs
            AnimatedVisibility(visible = activeKeypadTarget != ActiveKeypadTarget.NONE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    val targetTitle = when (activeKeypadTarget) {
                        ActiveKeypadTarget.TOTAL_PAID -> if (isBangla) "মোট পরিশোধের পরিমাণ লিখুন (৳):" else "Enter total paid amount (৳):"
                        ActiveKeypadTarget.OTHER_COSTS -> if (isBangla) "অন্যান্য খরচের পরিমাণ লিখুন (৳):" else "Enter other costs (৳):"
                        ActiveKeypadTarget.NONE -> ""
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = targetTitle, style = typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = appColors.textPrimary)
                        TextButton(onClick = { activeKeypadTarget = ActiveKeypadTarget.NONE }) {
                            Text(text = if (isBangla) "সম্পন্ন" else "Done", color = appColors.accent)
                        }
                    }

                    PulseKeypad(
                        expression = keypadExpression,
                        onExpressionChange = { newExpr ->
                            keypadExpression = newExpr
                            val parsed = newExpr.toDoubleOrNull()
                            if (activeKeypadTarget == ActiveKeypadTarget.TOTAL_PAID) {
                                customTotalAmount = parsed
                            } else if (activeKeypadTarget == ActiveKeypadTarget.OTHER_COSTS) {
                                otherCosts = parsed ?: 0.0
                            }
                        },
                        onDone = {
                            activeKeypadTarget = ActiveKeypadTarget.NONE
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Target Wallet selector (chips with balance and inline warning)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isBangla) "পরিশোধের ওয়ালেট" else "Target Payment Wallet",
                    style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (acc in accounts) {
                        val isSelected = acc.id == selectedAccountId
                        val accBorder by animateColorAsState(
                            targetValue = if (isSelected) appColors.accent else appColors.border,
                            label = "walletBorder"
                        )
                        val accBg by animateColorAsState(
                            targetValue = if (isSelected) appColors.accent.copy(alpha = 0.12f) else appColors.surface,
                            label = "walletBg"
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = accBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, accBorder),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clickable { selectedAccountId = acc.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = if (isSelected) appColors.accent else appColors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = acc.name,
                                        style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (isSelected) appColors.accent else appColors.textPrimary
                                    )
                                    PulseTabularText(
                                        text = formatAmount(
                                            amount = acc.currentBalance,
                                            isBangla = isBangla,
                                            useBengaliNumerals = useBengaliNumerals,
                                            mask = isPrivacyMode
                                        ),
                                        style = typography.labelSmall,
                                        color = appColors.textMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Balance warning
                if (isInsufficientBalance) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.warning.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, appColors.warning.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = appColors.warning, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) "ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই (ব্যালেন্স ঋণাত্মক হবে)" else "Insufficient balance in selected wallet (will become negative)",
                                style = typography.labelSmall,
                                color = appColors.warning
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Date picker chip & Note field row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date picker button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clickable {
                            val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val newCal = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, y)
                                        set(Calendar.MONTH, m)
                                        set(Calendar.DAY_OF_MONTH, d)
                                    }
                                    selectedDateMillis = newCal.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val formattedDate = DateTimeHelper.formatDayGroup(selectedDateMillis, isBangla, useBengaliNumerals)
                        Text(text = formattedDate, style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = appColors.textPrimary)
                    }
                }

                // Trip Note TextField
                OutlinedTextField(
                    value = tripNote,
                    onValueChange = { tripNote = it },
                    placeholder = {
                        Text(text = if (isBangla) "নোট (ঐচ্ছিক)" else "Note (optional)", style = typography.bodySmall, color = appColors.textMuted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = appColors.border,
                        focusedContainerColor = appColors.surface,
                        unfocusedContainerColor = appColors.surface
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Carry-over unpurchased items switch (Amendment A, C)
            val unboughtCount = items.size - checkoutSummary.purchasedCount
            if (unboughtCount > 0) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = appColors.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) "বাকি পণ্য নতুন ফর্দে নিন" else "Carry over unbought items",
                                style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = if (isBangla)
                                    "অক্রয়কৃত ${unboughtCount}টি পণ্য নিয়ে স্বয়ংক্রিয়ভাবে পরবর্তী ফর্দ তৈরি হবে"
                                else
                                    "A new checklist will be created for the $unboughtCount unbought items",
                                style = typography.labelSmall,
                                color = appColors.textMuted
                            )
                        }
                        Switch(
                            checked = carryOverUnpurchased,
                            onCheckedChange = { carryOverUnpurchased = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = appColors.accent
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Zero total confirmation prompt if items exist and total is set to 0 (Amendment A)
            if (checkoutSummary.requiresZeroConfirm) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.warning.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, appColors.warning.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isZeroTotalConfirmed,
                            onCheckedChange = { isZeroTotalConfirmed = it },
                            colors = CheckboxDefaults.colors(checkedColor = appColors.warning)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla)
                                "পণ্য থাকা সত্ত্বেও মোট খরচ ৳০ নির্ধারণ করা হয়েছে (ফ্রি বাজার)। আমি এটি নিশ্চিত করছি।"
                            else
                                "Total cost is set to ৳0 despite items bought. Confirm zero expense.",
                            style = typography.bodySmall,
                            color = appColors.textPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Validation message hint if button is disabled
            if (checkoutSummary.validationMessage != null && !canCompleteTrip) {
                Text(
                    text = checkoutSummary.validationMessage,
                    style = typography.bodySmall,
                    color = appColors.warning,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Primary Completion Button
            val buttonAmountStr = if (checkoutSummary.finalExpenseAmount % 1.0 == 0.0)
                checkoutSummary.finalExpenseAmount.toInt().toString()
            else
                checkoutSummary.finalExpenseAmount.toString()
            val buttonAmountFormatted = if (useBengaliNumerals) DigitConverter.toBengaliDigits(buttonAmountStr) else buttonAmountStr

            Button(
                onClick = {
                    onConfirmCheckout(
                        selectedAccountId,
                        customTotalAmount,
                        otherCosts,
                        selectedDateMillis,
                        tripNote.trim(),
                        carryOverUnpurchased
                    )
                },
                enabled = canCompleteTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.accent,
                    disabledContainerColor = appColors.surfaceVariant
                )
            ) {
                Text(
                    text = if (isBangla) "ট্রিপ সম্পন্ন করুন - ৳$buttonAmountFormatted" else "Complete Trip - ৳$buttonAmountFormatted",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (canCompleteTrip) Color.White else appColors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatAmount(
    amount: Double,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    mask: Boolean
): String {
    if (mask) return "••••"
    val amtStr = if (amount % 1.0 == 0.0) "${amount.toLong()}" else String.format(java.util.Locale.US, "%.2f", amount)
    val formatted = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(amtStr) else amtStr
    return "৳$formatted"
}
