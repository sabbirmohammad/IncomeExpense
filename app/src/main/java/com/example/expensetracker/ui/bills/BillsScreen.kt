package com.example.expensetracker.ui.bills

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.calculateInitialDueDate
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
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsScreen(
    viewModel: BillsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val bills by viewModel.recurringBills.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current
    val haptics = rememberAppHaptics()

    var showAddSheet by remember { mutableStateOf(false) }
    var billToEdit by remember { mutableStateOf<RecurringBill?>(null) }
    var billToPay by remember { mutableStateOf<RecurringBill?>(null) }
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

    val totalLiquidBalance = remember(accounts) { accounts.sumOf { it.currentBalance } }
    val upcomingBillsTotal = remember(bills) {
        bills.filter { it.isActive && (it.getPaymentStatus() == BillPaymentStatus.OVERDUE || it.getPaymentStatus() == BillPaymentStatus.DUE_SOON) }.sumOf { it.amount }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        // 1. Clean Top Header: Drawer Menu or Back Button + Clean Bold Title + Quick Add Action (No odd subtitle)
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
                text = strings.billsTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = ColorTextPrimary
            )
        }

        // 2. Responsive Centered Content Container (Caps at 840dp on tablets/wide screens)
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
                // Hero Commitments Card with Ambient Depth
                item {
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFF141829),
                        borderColor = Color(0xFF282F46),
                        padding = 16.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.totalMonthlyCommitment,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = ColorTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatCurrency(totalMonthlyCommitments, userProfile.currencySymbol),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ColorTextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ColorAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ReceiptLong,
                                        contentDescription = null,
                                        tint = ColorAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HairlineDivider(color = Color(0xFF232838))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Metric pills row: active count + due alert badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(ColorAccent)
                                    )
                                    Text(
                                        text = "${bills.count { it.isActive }} ${strings.activeSubscriptions}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = ColorTextSecondary
                                    )
                                }

                                if (overdueCount > 0) {
                                    Surface(
                                        shape = CircleShape,
                                        color = ColorDangerExpense.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Warning,
                                                contentDescription = null,
                                                tint = ColorDangerExpense,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "$overdueCount ${if (strings.isBangla) "বকেয়া" else "overdue"}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = ColorDangerExpense
                                            )
                                        }
                                    }
                                } else if (dueSoonCount > 0) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF7941D).copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, Color(0xFFF7941D).copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.NotificationsActive,
                                                contentDescription = null,
                                                tint = Color(0xFFF7941D),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "$dueSoonCount ${if (strings.isBangla) "তাগিদ" else "due soon"}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color(0xFFF7941D)
                                            )
                                        }
                                    }
                                }
                            }


                        }
                    }
                }

                // Dedicated Responsive "+ Add Recurring Bill" Action Button
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = ColorAccent.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptics.mediumClick()
                                billToEdit = null
                                showAddSheet = true
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(ColorAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (strings.isBangla) "নতুন রিকারিং বিল যোগ করুন" else "Add New Recurring Bill",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorAccent
                            )
                        }
                    }
                }

                // Section Title Row (Clean, no odd subtitle)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.allRecurringBills,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary
                        )

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F131D),
                            border = BorderStroke(1.dp, ColorBorder)
                        ) {
                            Text(
                                text = "${bills.size}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Bills List or Polished Empty State
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
                                val deletedTitle = bill.title
                                haptics.warningFeedback()
                                viewModel.deleteBill(bill.id)
                                feedbackController.show(
                                    TransactionFeedbackData(
                                        type = FeedbackVisualType.ITEM_DELETED,
                                        title = FeedbackStrings.itemDeleted(strings.isBangla),
                                        subtitle = deletedTitle
                                    )
                                )
                            }
                        )
                    }
                }
            }

            // Confetti Overlay
            ConfettiCelebration(
                trigger = showConfetti,
                onAnimationEnd = { showConfetti = false }
            )
        }
    }



    // Add / Edit Sheet
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

    // Pay Bill Confirmation Sheet
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
}

/**
 * 100% Responsive, Beautiful Recurring Bill Card Item
 */
@Composable
private fun RecurringBillCardItem(
    bill: RecurringBill,
    currencySymbol: String,
    onPayNow: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current
    val categoryColor = CategoryIconHelper.parseColor(bill.categoryColor)

    val paymentStatus = bill.getPaymentStatus()
    val daysUntilDue = bill.getDaysUntilDue()
    val isDueSoon = paymentStatus == BillPaymentStatus.DUE_SOON
    val isOverdue = paymentStatus == BillPaymentStatus.OVERDUE
    val now = System.currentTimeMillis()
    val isPaidRecently = bill.lastPaidDateMillis != null && (now - bill.lastPaidDateMillis < 28L * 24 * 60 * 60 * 1000) && bill.nextDueDateMillis > now

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: Category Icon + Title/Details + Amount & Dropdown Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .background(categoryColor.copy(alpha = 0.16f))
                            .border(1.dp, categoryColor.copy(alpha = 0.35f), RoundedCornerShape(SmallElementRadius)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = CategoryIconHelper.getIcon(bill.categoryIcon),
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bill.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = ColorTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val freqText = if (strings.isBangla) bill.frequency.bnDisplayName else bill.frequency.displayName
                            Text(
                                text = "$freqText • ${if (strings.isBangla) "${bill.dueDay} তারিখ" else "Day ${bill.dueDay}"}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!bill.accountName.isNullOrBlank()) {
                                Text(
                                    text = "• ${bill.accountName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = ColorAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatCurrency(bill.amount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = ColorTextPrimary,
                        maxLines = 1,
                        softWrap = false
                    )

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF0F131D))
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.editBill, color = ColorTextPrimary) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = ColorAccent) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.delete, color = ColorDangerExpense) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ColorDangerExpense) },
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
            HairlineDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Status Badge (left) & 1-Tap Action Button (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status Tag
                val (statusText, statusBg, statusColor) = when {
                    isOverdue -> Triple(strings.overdueBadgeText, ColorDangerExpense.copy(alpha = 0.15f), ColorDangerExpense)
                    isDueSoon -> Triple(
                        if (daysUntilDue == 0) (if (strings.isBangla) "আজই প্রদেয়" else "Due Today")
                        else (if (strings.isBangla) "$daysUntilDue দিনের মধ্যে প্রদেয়" else "Due in $daysUntilDue days"),
                        ColorWarningBills.copy(alpha = 0.15f),
                        ColorWarningBills
                    )
                    isPaidRecently -> Triple(
                        strings.paidThisMonth,
                        ColorSuccessIncome.copy(alpha = 0.15f),
                        ColorSuccessIncome
                    )
                    else -> Triple(
                        if (strings.isBangla) "আসন্ন (${bill.dueDay} তারিখ)" else "Upcoming (Day ${bill.dueDay})",
                        ColorBorder.copy(alpha = 0.4f),
                        ColorTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = statusColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // 1-Tap Pay Action Button
                if (isPaidRecently) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorSuccessIncome.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, ColorSuccessIncome.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = ColorSuccessIncome,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (strings.isBangla) "পরিশোধিত" else "Paid",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorSuccessIncome
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onPayNow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOverdue) ColorDangerExpense else if (isDueSoon) ColorWarningBills else ColorAccent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = strings.payNow,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 100% Responsive Add / Edit Bill Sheet with Segmented Frequency, Calculator, and Branded Accounts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditBillSheet(
    billToEdit: RecurringBill?,
    categories: List<Category>,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (RecurringBill) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    var title by remember { mutableStateOf(billToEdit?.title ?: "") }
    var amountText by remember {
        mutableStateOf(billToEdit?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(categories.find { it.id == billToEdit?.categoryId } ?: categories.firstOrNull())
    }
    var selectedAccount by remember {
        mutableStateOf(accounts.find { it.id == billToEdit?.accountId } ?: accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull())
    }
    var frequency by remember { mutableStateOf(billToEdit?.frequency ?: BillFrequency.MONTHLY) }
    var dueDayText by remember { mutableStateOf(billToEdit?.dueDay?.toString() ?: "5") }
    var notes by remember { mutableStateOf(billToEdit?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCalculatorBar by remember { mutableStateOf(false) }

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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(ColorBorder)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (billToEdit != null) strings.editBill else strings.addBill,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = ColorTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Title Input
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    errorMessage = null
                },
                placeholder = { Text(strings.billTitle, color = ColorTextMuted, fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary,
                    focusedContainerColor = Color(0xFF0F131D),
                    unfocusedContainerColor = Color(0xFF0F131D)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Amount Input with In-place Calculator
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F131D),
                border = BorderStroke(1.dp, ColorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.expenseAmount,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (showCalculatorBar) ColorAccent.copy(alpha = 0.2f) else Color(0xFF0F131D),
                            border = BorderStroke(1.dp, if (showCalculatorBar) ColorAccent else ColorBorder),
                            modifier = Modifier.clickable {
                                haptics.lightTick()
                                showCalculatorBar = !showCalculatorBar
                            }
                        ) {
                            Text(
                                text = strings.calc,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (showCalculatorBar) ColorAccent else ColorTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currencySymbol,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorAccent
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { input ->
                                amountText = input.filter { it.isDigit() || it == '.' || it in "+-×÷/* " }
                                errorMessage = null
                            },
                            placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ColorTextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = ColorTextPrimary,
                                unfocusedTextColor = ColorTextPrimary
                            ),
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                        )
                    }

                    // Calculator Live Preview
                    CalculatorPreviewBadge(
                        expression = amountText,
                        currencySymbol = currencySymbol,
                        onApply = { evaluated ->
                            haptics.successFeedback()
                            amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Collapsible Keypad Bar
                    AnimatedVisibility(visible = showCalculatorBar) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            CalculatorKeypadBar(
                                currentExpression = amountText,
                                onExpressionChanged = { amountText = it },
                                onApplyResult = { evaluated ->
                                    haptics.successFeedback()
                                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Full-Width Segmented Frequency Selector
            Text(
                text = strings.frequency,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(SmallElementRadius))
                    .background(Color(0xFF0F131D))
                    .border(1.dp, ColorBorder, RoundedCornerShape(SmallElementRadius))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(BillFrequency.MONTHLY, BillFrequency.WEEKLY, BillFrequency.YEARLY).forEach { freq ->
                    val isSelected = frequency == freq
                    val label = if (strings.isBangla) freq.bnDisplayName else freq.displayName
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(SmallElementRadius - 2.dp))
                            .background(if (isSelected) ColorAccent else Color.Transparent)
                            .clickable {
                                haptics.lightTick()
                                frequency = freq
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected) Color.White else ColorTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Due Day of Month Input
            Text(
                text = "${strings.dueDay} (1 - 31)",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = dueDayText,
                onValueChange = {
                    if (it.length <= 2) {
                        val filtered = it.filter { c -> c.isDigit() }
                        dueDayText = filtered
                    }
                },
                placeholder = { Text(if (strings.isBangla) "৫" else "5", color = ColorTextMuted, fontSize = 13.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary,
                    focusedContainerColor = Color(0xFF0F131D),
                    unfocusedContainerColor = Color(0xFF0F131D)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Category Selection
            Text(
                text = strings.category,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory?.id == cat.id
                    val catColor = CategoryIconHelper.parseColor(cat.colorHex)

                    Surface(
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = if (isSelected) catColor.copy(alpha = 0.2f) else Color(0xFF0F131D),
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) catColor else ColorBorder),
                        modifier = Modifier.clickable {
                            haptics.lightTick()
                            selectedCategory = cat
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(cat.icon),
                                contentDescription = null,
                                tint = catColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = cat.getLocalizedName(strings.isBangla),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (isSelected) ColorTextPrimary else ColorTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Paying Account Selector
            if (accounts.isNotEmpty()) {
                Text(
                    text = if (strings.isBangla) "পরিশোধের অ্যাকাউন্ট" else "Paying Account",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ColorTextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

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
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
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
                                Text(
                                    text = acc.name,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = ColorTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 7. Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(strings.notesOptional, color = ColorTextMuted, fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary,
                    focusedContainerColor = Color(0xFF0F131D),
                    unfocusedContainerColor = Color(0xFF0F131D)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorDangerExpense.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = ColorDangerExpense,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 8. Save Button
            Button(
                onClick = {
                    val amount = amountText.toParsedAmountOrNull()
                    val day = dueDayText.toIntOrNull() ?: 1

                    if (title.isBlank()) {
                        errorMessage = if (strings.isBangla) "অনুগ্রহ করে বিলের নাম লিখুন" else "Please enter a bill title"
                        return@Button
                    }
                    if (amount == null || amount <= 0.0) {
                        errorMessage = if (strings.isBangla) "সঠিক পরিমাণ লিখুন" else "Please enter a valid amount"
                        return@Button
                    }
                    if (selectedCategory == null) {
                        errorMessage = if (strings.isBangla) "অনুগ্রহ করে ক্যাটাগরি নির্বাচন করুন" else "Please select a category"
                        return@Button
                    }

                    haptics.successFeedback()
                    val clampedDay = day.coerceIn(1, 31)
                    val nextDue = if (billToEdit != null) {
                        if (billToEdit.dueDay != clampedDay || billToEdit.frequency != frequency) {
                            calculateInitialDueDate(frequency, clampedDay)
                        } else {
                            billToEdit.nextDueDateMillis
                        }
                    } else {
                        calculateInitialDueDate(frequency, clampedDay)
                    }

                    val bill = RecurringBill(
                        id = billToEdit?.id ?: 0,
                        title = title.trim(),
                        amount = amount,
                        categoryId = selectedCategory!!.id,
                        categoryName = selectedCategory!!.name,
                        categoryIcon = selectedCategory!!.icon,
                        categoryColor = selectedCategory!!.colorHex,
                        accountId = selectedAccount?.id,
                        accountName = selectedAccount?.name,
                        frequency = frequency,
                        dueDay = clampedDay,
                        nextDueDateMillis = nextDue,
                        lastPaidDateMillis = billToEdit?.lastPaidDateMillis,
                        notes = notes.trim()
                    )
                    onSave(bill)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Text(text = strings.save, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 100% Responsive 1-Tap Pay Bill Confirmation Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PayBillSheet(
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

            // Bill Details Card
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

            // Pay From Account Selector
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

