package com.example.expensetracker.ui.receivables

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarToday
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.PayablesSummary
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.formatCurrency
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    val statusFilter by viewModel.selectedStatusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current
    val haptics = rememberAppHaptics()

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

    val generatedReminder by viewModel.generatedReminder.collectAsState()
    val isGeneratingReminder by viewModel.isGeneratingReminder.collectAsState()

    val filteredList = receivables
    val filteredPayables = payables

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        // 1. Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
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
                    text = strings.debtsLedgerTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Privacy Toggle Button
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        viewModel.togglePrivacyMode()
                    }
                ) {
                    Icon(
                        imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Toggle Privacy",
                        tint = if (isPrivacyMode) ColorAccent else ColorTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Split Bill Button (only on Receivables tab)
                if (selectedTab == DebtLedgerTab.RECEIVABLES) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorAccent.copy(alpha = 0.15f),
                        modifier = Modifier.clickable {
                            haptics.mediumClick()
                            showSplitBillSheet = true
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Group,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.actionSplitBill,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorAccent,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Quick Add Button
                IconButton(
                    onClick = {
                        haptics.mediumClick()
                        if (selectedTab == DebtLedgerTab.RECEIVABLES) {
                            receivableToEdit = null
                            showAddEditSheet = true
                        } else {
                            payableToEdit = null
                            showAddEditPayableSheet = true
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = if (selectedTab == DebtLedgerTab.RECEIVABLES) strings.addReceivable else strings.addPayable,
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Segmented Control (Tabs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tab 1: Receivables
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = if (selectedTab == DebtLedgerTab.RECEIVABLES) ColorAccent else ColorSurface,
                border = if (selectedTab == DebtLedgerTab.RECEIVABLES) null else androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        haptics.lightTick()
                        viewModel.setSelectedTab(DebtLedgerTab.RECEIVABLES)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 9.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.tabReceivables,
                        fontSize = 12.5.sp,
                        fontWeight = if (selectedTab == DebtLedgerTab.RECEIVABLES) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White else ColorTextSecondary
                    )
                    if (summary.pendingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White.copy(alpha = 0.25f) else ColorAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = DateTimeHelper.formatCounter(summary.pendingCount, strings.isBangla),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White else ColorAccent
                            )
                        }
                    }
                }
            }

            // Tab 2: Payables
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = if (selectedTab == DebtLedgerTab.PAYABLES) ColorAccent else ColorSurface,
                border = if (selectedTab == DebtLedgerTab.PAYABLES) null else androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        haptics.lightTick()
                        viewModel.setSelectedTab(DebtLedgerTab.PAYABLES)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 9.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.tabPayables,
                        fontSize = 12.5.sp,
                        fontWeight = if (selectedTab == DebtLedgerTab.PAYABLES) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == DebtLedgerTab.PAYABLES) Color.White else ColorTextSecondary
                    )
                    if (payablesSummary.pendingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == DebtLedgerTab.PAYABLES) Color.White.copy(alpha = 0.25f) else ColorDangerExpense.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = DateTimeHelper.formatCounter(payablesSummary.pendingCount, strings.isBangla),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == DebtLedgerTab.PAYABLES) Color.White else ColorDangerExpense
                            )
                        }
                    }
                }
            }
        }

        // Net Position Banner
        Surface(
            shape = RoundedCornerShape(CardCornerRadius),
            color = ColorSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (debtBookSummary.netBalance > 0) ColorSuccessIncome
                                else if (debtBookSummary.netBalance < 0) ColorDangerExpense
                                else ColorTextSecondary
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.netPosition,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = ColorTextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {


                    val netText = if (isPrivacyMode) {
                        "••••••"
                    } else {
                        val prefix = if (debtBookSummary.netBalance > 0) "+" else ""
                        "$prefix ${formatCurrency(debtBookSummary.netBalance, userProfile.currencySymbol)}"
                    }
                    Text(
                        text = netText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (debtBookSummary.netBalance > 0) ColorSuccessIncome
                        else if (debtBookSummary.netBalance < 0) ColorDangerExpense
                        else ColorTextPrimary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ScreenPaddingHorizontal)
        ) {
            if (selectedTab == DebtLedgerTab.RECEIVABLES) {
                // 2. Summary Hero Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 18.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.remainingDue,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = ColorTextMuted
                                )
                                if (summary.pendingCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ColorDangerExpense.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${summary.pendingCount} ${strings.pendingBadge}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ColorDangerExpense
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isPrivacyMode) "••••••" else formatCurrency(summary.remainingDue, userProfile.currencySymbol),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ColorTextPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HairlineDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(strings.totalLent, fontSize = 11.sp, color = ColorTextMuted)
                                    Text(
                                        text = if (isPrivacyMode) "••••" else formatCurrency(summary.totalLent, userProfile.currencySymbol),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorTextPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(strings.totalCollected, fontSize = 11.sp, color = ColorTextMuted)
                                    Text(
                                        text = if (isPrivacyMode) "••••" else formatCurrency(summary.totalCollected, userProfile.currencySymbol),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorSuccessIncome
                                    )
                                }
                            }
                        }
                    }
                }

            // 3. Section Title & List
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${strings.pendingDebtors} (${filteredList.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextMuted,
                        letterSpacing = 0.8.sp
                    )

                    Text(
                        text = "+ ${strings.addReceivable}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorAccent,
                        modifier = Modifier.clickable {
                            receivableToEdit = null
                            showAddEditSheet = true
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (filteredList.isEmpty()) {
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
                                imageVector = Icons.Outlined.Handshake,
                                contentDescription = null,
                                tint = ColorTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = strings.noReceivablesFound,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                color = ColorTextPrimary
                            )
                            Text(
                                text = strings.noReceivablesHint,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { rec ->
                    ReceivableItemCard(
                        receivable = rec,
                        currencySymbol = userProfile.currencySymbol,
                        onRecordPayment = { receivableForPayment = rec },
                        onEdit = {
                            receivableToEdit = rec
                            showAddEditSheet = true
                        },
                        onDelete = { receivableToDelete = rec },
                        onCall = {
                            if (rec.phoneNumber.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${rec.phoneNumber}"))
                                context.startActivity(intent)
                            }
                        },
                        onGenerateReminder = {
                            selectedReceivableForReminder = rec
                            viewModel.generatePoliteReminder(rec)
                        }
                    )
                }
            }
        } else {
                // --- Payables Tab ---
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    PayablesSummaryCard(
                        summary = payablesSummary,
                        currencySymbol = userProfile.currencySymbol,
                        isPrivacyMode = isPrivacyMode
                    )
                }



                item {
                    Spacer(modifier = Modifier.height(CardGap))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${strings.payablesTitle.uppercase(Locale.getDefault())} (${filteredPayables.size})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorTextSecondary,
                            letterSpacing = 0.8.sp
                        )

                        Text(
                            text = "+ ${strings.addPayable}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorAccent,
                            modifier = Modifier.clickable {
                                payableToEdit = null
                                showAddEditPayableSheet = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (filteredPayables.isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(CardGap))
                        AppCard(modifier = Modifier.fillMaxWidth(), padding = 32.dp) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Handshake,
                                    contentDescription = null,
                                    tint = ColorTextSecondary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = strings.noPayablesFound,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                    color = ColorTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = strings.noPayablesHint,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = ColorTextSecondary
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(CardGap))
                    }
                    items(filteredPayables, key = { it.id }) { pay ->
                        PayableRowItem(
                            payable = pay,
                            currencySymbol = userProfile.currencySymbol,
                            isPrivacyMode = isPrivacyMode,
                            onRecordPayment = { payableForPayment = pay },
                            onEdit = {
                                payableToEdit = pay
                                showAddEditPayableSheet = true
                            },
                            onDelete = { payableToDelete = pay }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }



    // Modal: Add / Edit Receivable
    if (showAddEditSheet) {
        AddEditReceivableSheet(
            receivable = receivableToEdit,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showAddEditSheet = false },
            onSave = { name, phone, amount, dueMillis, note, deductAccId ->
                if (receivableToEdit != null) {
                    val updated = receivableToEdit!!.copy(
                        personName = name,
                        phoneNumber = phone,
                        amount = amount,
                        dueDateMillis = dueMillis,
                        notes = note
                    )
                    viewModel.updateReceivable(updated)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = FeedbackStrings.receivableAdded(strings.isBangla),
                            amount = amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = name
                        )
                    )
                } else {
                    viewModel.addReceivable(name, phone, amount, dueMillis, note, deductAccId)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = FeedbackStrings.receivableAdded(strings.isBangla),
                            amount = amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = name
                        )
                    )
                }
                showAddEditSheet = false
            }
        )
    }

    // Modal: Record Payment / Repayment
    if (receivableForPayment != null) {
        RecordPaymentSheet(
            receivable = receivableForPayment!!,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { receivableForPayment = null },
            onConfirm = { amount, accountId, note ->
                val target = receivableForPayment
                if (target != null) {
                    val willSettle = (target.remainingAmount - amount) <= 0.001
                    viewModel.recordPayment(target.id, amount, accountId, note)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.DEBT_PAYMENT,
                            title = FeedbackStrings.debtPayment(strings.isBangla),
                            amount = amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = "${target.personName} · ${if (willSettle) strings.settledBadge else strings.partialBadge}"
                        )
                    )
                }
                receivableForPayment = null
            }
        )
    }

    // Modal: Bill Splitter Sheet
    if (showSplitBillSheet) {
        BillSplitterSheet(
            accounts = accounts,
            categories = categories,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showSplitBillSheet = false },
            onConfirmSplit = { splitData ->
                viewModel.executeBillSplit(splitData)
                showSplitBillSheet = false
                val totalPeople = splitData.friends.size + 1
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.BILL_SPLIT,
                        title = FeedbackStrings.billSplit(strings.isBangla),
                        amount = splitData.totalAmount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = if (strings.isBangla) "$totalPeople জনের মধ্যে স্প্লিট" else "$totalPeople people split"
                    )
                )
            }
        )
    }

    // Delete Confirmation Dialog
    if (receivableToDelete != null) {
        AlertDialog(
            onDismissRequest = { receivableToDelete = null },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            title = {
                Text(
                    text = "${strings.deleteReceivableConfirm} (${receivableToDelete!!.personName})",
                    color = ColorTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = strings.deleteReceivableMessage,
                    color = ColorTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val deletedPerson = receivableToDelete!!.personName
                        viewModel.deleteReceivable(receivableToDelete!!.id)
                        receivableToDelete = null
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.ITEM_DELETED,
                                title = FeedbackStrings.itemDeleted(strings.isBangla),
                                subtitle = deletedPerson
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { receivableToDelete = null }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Modal: Add / Edit Payable
    if (showAddEditPayableSheet) {
        AddEditPayableSheet(
            payable = payableToEdit,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = {
                showAddEditPayableSheet = false
                payableToEdit = null
            },
            onSave = { name, phone, amount, dueMillis, note, depositAccId ->
                if (payableToEdit != null) {
                    val updated = payableToEdit!!.copy(
                        personName = name,
                        phoneNumber = phone,
                        amount = amount,
                        dueDateMillis = dueMillis,
                        notes = note
                    )
                    viewModel.updatePayable(updated)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = if (strings.isBangla) "দেনা হিসাব আপডেট করা হয়েছে" else "Payable updated",
                            amount = amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = name
                        )
                    )
                } else {
                    viewModel.addPayable(name, phone, amount, dueMillis, note, depositAccId)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = if (strings.isBangla) "দেনা হিসাব যুক্ত হয়েছে" else "Payable added",
                            amount = amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = name
                        )
                    )
                }
                showAddEditPayableSheet = false
                payableToEdit = null
            }
        )
    }

    // Modal: Record Payable Payment / Repayment
    if (payableForPayment != null) {
        RecordPayablePaymentSheet(
            payable = payableForPayment!!,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { payableForPayment = null },
            onConfirm = { amount, accountId, note ->
                val target = payableForPayment
                if (target != null) {
                    val willSettle = (target.remainingAmount - amount) <= 0.001
                    viewModel.recordPayablePayment(target.id, amount, accountId, note)
                    if (willSettle) {
                        showConfetti = true
                    }
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.DEBT_PAYMENT,
                            title = if (strings.isBangla) "দেনা পরিশোধ সফল হয়েছে" else "Debt payment recorded",
                            amount = amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = "${target.personName} · ${if (willSettle) strings.settledBadge else strings.partialBadge}"
                        )
                    )
                }
                payableForPayment = null
            }
        )
    }

    // Modal: Delete Payable Dialog
    if (payableToDelete != null) {
        AlertDialog(
            onDismissRequest = { payableToDelete = null },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            title = {
                Text(
                    text = "${if (strings.isBangla) "দেনা মুছুন" else "Delete Payable"} (${payableToDelete!!.personName})",
                    color = ColorTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla) "আপনি কি নিশ্চিত যে এই দেনার হিসাবটি মুছে ফেলতে চান?" else "Are you sure you want to delete this payable entry?",
                    color = ColorTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val deletedPerson = payableToDelete!!.personName
                        viewModel.deletePayable(payableToDelete!!.id)
                        payableToDelete = null
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.ITEM_DELETED,
                                title = FeedbackStrings.itemDeleted(strings.isBangla),
                                subtitle = deletedPerson
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { payableToDelete = null }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // AI Reminder Dialog
    if (selectedReceivableForReminder != null) {
        val target = selectedReceivableForReminder!!
        val clipboardManager = LocalClipboardManager.current

        AlertDialog(
            onDismissRequest = {
                selectedReceivableForReminder = null
                viewModel.clearGeneratedReminder()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.aiReminderTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = ColorTextPrimary
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${target.personName} • ${formatCurrency(target.remainingAmount, userProfile.currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ColorAccent
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (isGeneratingReminder) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = ColorAccent,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = strings.generatingReminder,
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorTextSecondary
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(SmallElementRadius),
                            color = ColorBackground,
                            border = BorderStroke(1.dp, ColorBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedReminder ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                                color = ColorTextPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Direct Intent Action Buttons (WhatsApp & SMS)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // WhatsApp Button
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val rawPhone = target.phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
                                        val waPhone = when {
                                            rawPhone.startsWith("01") -> "88$rawPhone"
                                            rawPhone.startsWith("880") -> rawPhone
                                            else -> rawPhone
                                        }
                                        val textEncoded = java.net.URLEncoder.encode(generatedReminder ?: "", "UTF-8")
                                        val waUri = if (waPhone.isNotBlank()) {
                                            Uri.parse("https://wa.me/$waPhone?text=$textEncoded")
                                        } else {
                                            Uri.parse("https://api.whatsapp.com/send?text=$textEncoded")
                                        }
                                        val waIntent = Intent(Intent.ACTION_VIEW, waUri)
                                        try {
                                            context.startActivity(waIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, if (strings.isBangla) "হোয়াটসঅ্যাপ ইনস্টল করা নেই" else "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF25D366).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (strings.isBangla) "WhatsApp-এ পাঠান" else "WhatsApp",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF25D366)
                                    )
                                }
                            }

                            // SMS Button
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val phone = target.phoneNumber
                                        val smsUri = if (phone.isNotBlank()) Uri.parse("smsto:$phone") else Uri.parse("smsto:")
                                        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                                            putExtra("sms_body", generatedReminder)
                                        }
                                        try {
                                            context.startActivity(smsIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, if (strings.isBangla) "মেসেজিং অ্যাপ পাওয়া যায়নি" else "No messaging app found", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = ColorAccent.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        tint = ColorAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (strings.isBangla) "SMS পাঠান" else "SMS",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorAccent
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isGeneratingReminder && !generatedReminder.isNullOrBlank()) {
                        TextButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, generatedReminder)
                                }
                                val shareIntent = Intent.createChooser(sendIntent, strings.aiReminderTitle)
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = ColorAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.shareReminder, color = ColorAccent)
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedReminder ?: ""))
                                Toast.makeText(context, strings.reminderCopied, Toast.LENGTH_SHORT).show()
                                selectedReceivableForReminder = null
                                viewModel.clearGeneratedReminder()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.copyReminder, color = Color.White)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedReceivableForReminder = null
                        viewModel.clearGeneratedReminder()
                    }
                ) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Confetti Overlay
    ConfettiCelebration(
        trigger = showConfetti,
        onAnimationEnd = { showConfetti = false }
    )
}

/**
 * Individual Debtor Item Card
 */
@Composable
private fun ReceivableItemCard(
    receivable: Receivable,
    currencySymbol: String,
    onRecordPayment: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onGenerateReminder: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    var showMenu by remember { mutableStateOf(false) }
    val dateFormat = remember(strings.isBangla) { SimpleDateFormat("d MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    val progress = if (receivable.amount > 0) {
        (receivable.amountReceived / receivable.amount).toFloat().coerceIn(0f, 1f)
    } else 0f

    val isOverdue = receivable.dueDateMillis != null &&
        System.currentTimeMillis() > receivable.dueDateMillis &&
        !receivable.isSettled

    val statusText = when (receivable.status) {
        ReceivableStatus.SETTLED -> strings.settledBadge
        ReceivableStatus.PARTIAL -> strings.partialBadge
        ReceivableStatus.PENDING -> strings.pendingBadge
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 16.dp
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
                    // Initials Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f))
                            .border(1.dp, ColorAccent.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = receivable.personName.take(2).uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = receivable.personName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (receivable.phoneNumber.isNotBlank()) {
                            Text(
                                text = receivable.phoneNumber,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }
                }

                // Status Badge & Menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (receivable.status) {
                                    ReceivableStatus.SETTLED -> ColorSuccessIncome.copy(alpha = 0.15f)
                                    ReceivableStatus.PARTIAL -> Color(0xFFF0A63D).copy(alpha = 0.15f)
                                    ReceivableStatus.PENDING -> ColorDangerExpense.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (receivable.status) {
                                ReceivableStatus.SETTLED -> ColorSuccessIncome
                                ReceivableStatus.PARTIAL -> Color(0xFFF0A63D)
                                ReceivableStatus.PENDING -> ColorDangerExpense
                            }
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, null, tint = ColorTextSecondary, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(ColorSurface)
                        ) {
                            if (!receivable.isSettled) {
                                DropdownMenuItem(
                                    text = { Text(strings.generatePoliteReminder, color = ColorTextPrimary) },
                                    leadingIcon = { Icon(Icons.Outlined.NotificationsActive, null, tint = ColorAccent) },
                                    onClick = {
                                        showMenu = false
                                        onGenerateReminder()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(strings.recordPayment, color = ColorTextPrimary) },
                                leadingIcon = { Icon(Icons.Outlined.Paid, null, tint = ColorSuccessIncome) },
                                onClick = {
                                    showMenu = false
                                    onRecordPayment()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (strings.isBangla) "তথ্য এডিট করুন" else "Edit Details", color = ColorTextPrimary) },
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

            Spacer(modifier = Modifier.height(12.dp))

            // Amount details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.remainingDue, fontSize = 11.sp, color = ColorTextMuted)
                    Text(
                        text = formatCurrency(receivable.remainingAmount, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (receivable.isSettled) ColorSuccessIncome else ColorDangerExpense
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("${strings.totalLent}: ${formatCurrency(receivable.amount, currencySymbol)}", fontSize = 11.sp, color = ColorTextSecondary)
                    Text("${strings.totalCollected}: ${formatCurrency(receivable.amountReceived, currencySymbol)}", fontSize = 11.sp, color = ColorSuccessIncome)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorSuccessIncome,
                trackColor = ColorBorder
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Due Date & Note / Quick Call Action (Responsive Layout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f, fill = false)) {
                    if (receivable.dueDateMillis != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = if (isOverdue) ColorDangerExpense else ColorTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val formattedDue = dateFormat.format(Date(receivable.dueDateMillis))
                            val displayDue = if (strings.isBangla) convertToBengaliDigits(formattedDue) else formattedDue
                            Text(
                                text = if (isOverdue) "${strings.overdueBadge} ($displayDue)"
                                else "${strings.dueDate}: $displayDue",
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (isOverdue) ColorDangerExpense else ColorTextSecondary
                            )
                        }
                    } else {
                        Text(
                            text = receivable.notes.ifBlank { "" },
                            fontSize = 11.sp,
                            color = ColorTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (receivable.phoneNumber.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ColorSurface,
                            border = BorderStroke(1.dp, ColorBorder),
                            modifier = Modifier.clickable { onCall() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Call, null, tint = ColorAccent, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.callPerson, fontSize = 11.sp, color = ColorAccent, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    if (!receivable.isSettled) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ColorSuccessIncome.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, ColorSuccessIncome.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { onRecordPayment() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Paid, null, tint = ColorSuccessIncome, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.recordPayment, fontSize = 11.sp, color = ColorSuccessIncome, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Add / Edit Receivable Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditReceivableSheet(
    receivable: Receivable?,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Long?, String, Long?) -> Unit
) {
    var personName by remember { mutableStateOf(receivable?.personName ?: "") }
    var phoneNumber by remember { mutableStateOf(receivable?.phoneNumber ?: "") }
    var amountText by remember {
        mutableStateOf(receivable?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var dueDateMillis by remember { mutableStateOf(receivable?.dueDateMillis) }
    var notes by remember { mutableStateOf(receivable?.notes ?: "") }
    var deductFromAccount by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val strings = LocalAppStrings.current
    val dateFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

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
                    text = if (receivable != null) (if (strings.isBangla) "পাওনা হিসাব পরিবর্তন" else "Edit Receivable") else strings.addReceivable,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = ColorTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Person Name Field
            Text(strings.personName, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                placeholder = { Text(if (strings.isBangla) "যেমন: করিম আহমেদ, বন্ধু তানভীর" else "e.g. Karim Ahmed, Friend Tanvir", color = ColorTextMuted) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Person, null, tint = ColorTextSecondary, modifier = Modifier.size(18.dp)) },
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

            // Phone Number Field
            Text("${strings.phoneNumber} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("017XXXXXXXX", color = ColorTextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = ColorTextSecondary, modifier = Modifier.size(18.dp)) },
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

            // Amount Field
            Text("${strings.amount} ($currencySymbol)", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " } },
                placeholder = { Text("0.00", color = ColorTextMuted) },
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
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = amountText,
                onExpressionChanged = { amountText = it },
                onApplyResult = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Due Date Selector
            Text("${strings.dueDate} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = ColorBackground,
                border = BorderStroke(1.dp, ColorBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (dueDateMillis != null) {
                                val d = dateFormat.format(Date(dueDateMillis!!))
                                if (strings.isBangla) convertToBengaliDigits(d) else d
                            } else (if (strings.isBangla) "তারিখ নির্ধারণ করুন" else "Set a due date"),
                            fontSize = 13.sp,
                            color = if (dueDateMillis != null) ColorTextPrimary else ColorTextMuted
                        )
                    }
                    if (dueDateMillis != null) {
                        Text(
                            text = strings.clear,
                            fontSize = 11.sp,
                            color = ColorDangerExpense,
                            modifier = Modifier.clickable { dueDateMillis = null }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes Field
            Text("${strings.note} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(if (strings.isBangla) "যেমন: প্রোজেক্টের অগ্রিম, জরুরি ধার" else "e.g. Project advance, Emergency loan", color = ColorTextMuted) },
                maxLines = 2,
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

            // Deduct from account option (only for new receivables)
            if (receivable == null && accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(SmallElementRadius),
                    color = ColorBackground,
                    border = BorderStroke(1.dp, ColorBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(strings.deductFromAccount, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorTextPrimary)
                                Text(if (strings.isBangla) "ওয়ালেট থেকে ব্যয়ের হিসাব হিসেবে যুক্ত করুন" else "Log as an expense from one of your accounts", fontSize = 11.sp, color = ColorTextSecondary)
                            }
                            Switch(
                                checked = deductFromAccount,
                                onCheckedChange = { deductFromAccount = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ColorAccent)
                            )
                        }

                        if (deductFromAccount) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("${strings.fromAccount}:", fontSize = 11.sp, color = ColorTextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                accounts.forEach { acc ->
                                    val isSelected = selectedAccountId == acc.id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) ColorAccent else ColorSurface,
                                        border = BorderStroke(1.dp, if (isSelected) ColorAccent else ColorBorder),
                                        modifier = Modifier.clickable { selectedAccountId = acc.id }
                                    ) {
                                        Text(
                                            text = "${acc.name} (${formatCurrency(acc.currentBalance, currencySymbol)})",
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color.White else ColorTextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
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
                    if (personName.isBlank()) {
                        errorMessage = if (strings.isBangla) "অনুগ্রহ করে ব্যক্তির নাম লিখুন" else "Please enter the person's name"
                        return@Button
                    }
                    val amt = amountText.toParsedAmountOrNull()
                    if (amt == null || amt <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    val deductId = if (deductFromAccount) selectedAccountId else null
                    onSave(personName.trim(), phoneNumber.trim(), amt, dueDateMillis, notes.trim(), deductId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Text(
                    text = strings.save,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        val initialUtcMillis = remember(dueDateMillis) {
            val localCal = Calendar.getInstance().apply {
                timeInMillis = dueDateMillis ?: System.currentTimeMillis()
            }
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                clear()
                set(
                    localCal.get(Calendar.YEAR),
                    localCal.get(Calendar.MONTH),
                    localCal.get(Calendar.DAY_OF_MONTH)
                )
            }.timeInMillis
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialUtcMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedUtcMillis ->
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = selectedUtcMillis
                        }
                        val selYear = utcCal.get(Calendar.YEAR)
                        val selMonth = utcCal.get(Calendar.MONTH)
                        val selDay = utcCal.get(Calendar.DAY_OF_MONTH)

                        val updatedLocal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, selYear)
                            set(Calendar.MONTH, selMonth)
                            set(Calendar.DAY_OF_MONTH, selDay)
                            set(Calendar.HOUR_OF_DAY, 12)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        dueDateMillis = updatedLocal.timeInMillis
                    }
                    showDatePicker = false
                }) {
                    Text(strings.confirm, color = ColorAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Record Payment / Repayment Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordPaymentSheet(
    receivable: Receivable,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, Long?, String) -> Unit
) {
    val strings = LocalAppStrings.current
    var amountText by remember { mutableStateOf(receivable.remainingAmount.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    text = strings.recordPayment,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = ColorTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${receivable.personName} • ${strings.remainingDue}: ${formatCurrency(receivable.remainingAmount, currencySymbol)}",
                fontSize = 12.sp,
                color = ColorTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Received Field
            Text(strings.amount, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }
                    if (errorMessage != null) errorMessage = null
                },
                placeholder = { Text("0.00", color = ColorTextMuted) },
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
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = amountText,
                onExpressionChanged = {
                    amountText = it
                    if (errorMessage != null) errorMessage = null
                },
                onApplyResult = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Amount Suggestion Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Full Remaining
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ColorAccent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ColorAccent),
                    modifier = Modifier.clickable {
                        amountText = if (receivable.remainingAmount % 1.0 == 0.0) receivable.remainingAmount.toLong().toString() else receivable.remainingAmount.toString()
                        if (errorMessage != null) errorMessage = null
                    }
                ) {
                    Text(
                        text = "100% (${formatCurrency(receivable.remainingAmount, currencySymbol)})",
                        fontSize = 11.sp,
                        color = ColorAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Half Remaining
                val half = receivable.remainingAmount / 2.0
                if (half > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ColorBackground,
                        border = BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier.clickable {
                            amountText = if (half % 1.0 == 0.0) half.toLong().toString() else half.toString()
                            if (errorMessage != null) errorMessage = null
                        }
                    ) {
                        Text(
                            text = "50% (${formatCurrency(half, currencySymbol)})",
                            fontSize = 11.sp,
                            color = ColorTextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Deposit to Account selector
            Text(strings.depositToAccount, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { acc ->
                    val isSelected = selectedAccountId == acc.id
                    Surface(
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = if (isSelected) ColorSuccessIncome else ColorBackground,
                        border = if (isSelected) null else BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier.clickable { selectedAccountId = acc.id }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = acc.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else ColorTextPrimary
                            )
                            Text(
                                text = formatCurrency(acc.currentBalance, currencySymbol),
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else ColorTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Note Field
            Text(strings.note, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text(strings.optional, color = ColorTextMuted) },
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

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = ColorDangerExpense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toParsedAmountOrNull()?.takeIf { it > 0 }
                    if (amt == null || amt <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    if (amt > receivable.remainingAmount + 0.01) {
                        errorMessage = "${strings.repaymentExceedError} (${formatCurrency(receivable.remainingAmount, currencySymbol)})"
                        return@Button
                    }
                    onConfirm(amt, selectedAccountId, note)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccessIncome)
            ) {
                Text(
                    text = strings.confirmRepayment,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

