package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.ai.GeminiAiService
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionSheet(
    sheetState: SheetState,
    existingTransaction: Transaction? = null,
    initialDateMillis: Long? = null,
    initialType: TransactionType = TransactionType.EXPENSE,
    categories: List<Category>,
    accounts: List<Account> = emptyList(),
    currencySymbol: String,
    geminiApiKey: String? = null,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current

    var selectedType by remember(existingTransaction, initialType) {
        mutableStateOf(existingTransaction?.type ?: initialType)
    }
    var amountText by remember(existingTransaction) {
        mutableStateOf(
            existingTransaction?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: ""
        )
    }
    var title by remember(existingTransaction) {
        mutableStateOf(existingTransaction?.title ?: "")
    }
    var notes by remember(existingTransaction) {
        mutableStateOf(existingTransaction?.notes ?: "")
    }
    var showCalculatorBar by remember { mutableStateOf(false) }

    var receiptImagePath by remember(existingTransaction) {
        mutableStateOf(existingTransaction?.receiptImagePath)
    }
    var receiptBitmap by remember(receiptImagePath) {
        mutableStateOf<Bitmap?>(null)
    }
    var isScanningReceipt by remember { mutableStateOf(false) }
    var scanStatusMessage by remember { mutableStateOf<String?>(null) }
    var showFullReceiptPreview by remember { mutableStateOf(false) }

    // AI Magic Fill / SMS State
    var showAiMagicFill by remember { mutableStateOf(false) }
    var aiMagicInputText by remember { mutableStateOf("") }
    var isAiMagicParsing by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(receiptImagePath) {
        val currentPath = receiptImagePath
        if (currentPath != null) {
            receiptBitmap = ReceiptImageHelper.loadReceiptBitmap(context, currentPath)
        } else {
            receiptBitmap = null
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val saved = ReceiptImageHelper.saveReceiptImage(context, uri)
                if (saved != null) {
                    receiptImagePath = saved
                    haptics.lightTick()
                }
            }
        }
    }

    var selectedAccountId by remember(existingTransaction, accounts) {
        mutableStateOf(
            existingTransaction?.accountId
                ?: accounts.find { it.isDefault }?.id
                ?: accounts.firstOrNull()?.id
        )
    }

    val availableCategories = categories.filter { it.type == selectedType }
    var selectedCategory by remember(selectedType, existingTransaction, categories) {
        mutableStateOf(
            categories.find { it.id == existingTransaction?.categoryId && it.type == selectedType }
                ?: availableCategories.firstOrNull()
        )
    }

    var selectedPaymentMethod by remember(existingTransaction) {
        mutableStateOf(existingTransaction?.paymentMethod ?: PaymentMethod.CASH)
    }
    var dateMillis by remember(existingTransaction, initialDateMillis) {
        mutableStateOf(existingTransaction?.dateMillis ?: initialDateMillis ?: System.currentTimeMillis())
    }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val amountFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if (existingTransaction == null) {
            kotlinx.coroutines.delay(200)
            try {
                amountFocusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    val isExpense = selectedType == TransactionType.EXPENSE
    val typeAccentColor by animateColorAsState(
        targetValue = if (isExpense) ColorDangerExpense else ColorSuccessIncome,
        animationSpec = tween(300),
        label = "typeAccentColor"
    )

    val dateOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }
    val dayOfWeekFormat = remember(strings.isBangla) { SimpleDateFormat("EEEE", DateTimeHelper.getLocale(strings.isBangla)) }
    val timeOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("hh:mm a", DateTimeHelper.getLocale(strings.isBangla)) }

    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showAccountDropdown by remember { mutableStateOf(false) }

    var isEditMode by remember(existingTransaction) {
        mutableStateOf(existingTransaction == null)
    }
    var pendingDismissAfterDiscard by remember { mutableStateOf(false) }

    val resetToOriginal: () -> Unit = {
        if (existingTransaction != null) {
            selectedType = existingTransaction.type
            amountText = existingTransaction.amount.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
            title = existingTransaction.title
            notes = existingTransaction.notes
            receiptImagePath = existingTransaction.receiptImagePath
            selectedAccountId = existingTransaction.accountId
                ?: accounts.find { it.isDefault }?.id
                ?: accounts.firstOrNull()?.id
            selectedCategory = categories.find { it.id == existingTransaction.categoryId && it.type == existingTransaction.type }
                ?: categories.firstOrNull { it.type == existingTransaction.type }
            selectedPaymentMethod = existingTransaction.paymentMethod
            dateMillis = existingTransaction.dateMillis
            errorMessage = null
            showCalculatorBar = false
        }
    }

    val isDirty = remember(
        amountText, title, notes, selectedCategory, receiptImagePath,
        selectedType, selectedAccountId, selectedPaymentMethod, dateMillis,
        existingTransaction
    ) {
        if (existingTransaction != null) {
            val origAmount = existingTransaction.amount.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
            amountText.trim() != origAmount ||
                    title.trim() != existingTransaction.title.trim() ||
                    notes.trim() != existingTransaction.notes.trim() ||
                    selectedCategory?.id != existingTransaction.categoryId ||
                    receiptImagePath != existingTransaction.receiptImagePath ||
                    selectedType != existingTransaction.type ||
                    selectedAccountId != existingTransaction.accountId ||
                    selectedPaymentMethod != existingTransaction.paymentMethod ||
                    dateMillis != existingTransaction.dateMillis
        } else {
            amountText.isNotBlank() || title.isNotBlank() || notes.isNotBlank() || receiptImagePath != null
        }
    }
    var showDiscardDialog by remember { mutableStateOf(false) }
    val attemptDismiss: () -> Unit = {
        if (existingTransaction != null && isEditMode) {
            if (isDirty) {
                pendingDismissAfterDiscard = true
                showDiscardDialog = true
            } else {
                isEditMode = false
            }
        } else if (existingTransaction == null && isDirty) {
            pendingDismissAfterDiscard = true
            showDiscardDialog = true
        } else {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = attemptDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = LocalAppColors.current.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tactile Pill Drag Handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(ColorBorder.copy(alpha = 0.9f))
                )
            }

            // Top Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title with Context Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val headerTitle = when {
                        existingTransaction != null && !isEditMode -> {
                            if (isExpense) strings.expenseDetails else strings.incomeDetails
                        }
                        existingTransaction != null && isEditMode -> {
                            if (isExpense) strings.editExpenseTitle else strings.editIncomeTitle
                        }
                        else -> {
                            if (isExpense) strings.addExpenseTitle else strings.addIncomeTitle
                        }
                    }

                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorTextPrimary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = typeAccentColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, typeAccentColor.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = if (isExpense) strings.expenseBadge else strings.incomeBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = typeAccentColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (existingTransaction != null && !isEditMode) {
                        // View Mode: Delete + Close (Primary edit button is docked at bottom)
                        if (onDelete != null) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(ColorDangerExpense.copy(alpha = 0.12f))
                                    .clickable {
                                        haptics.warningFeedback()
                                        showDeleteConfirmDialog = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = ColorDangerExpense,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LocalAppColors.current.surfaceVariant)
                                .clickable {
                                    haptics.lightTick()
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Close",
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (existingTransaction != null && isEditMode) {
                        // Edit Mode for existing: Cancel Edit + Delete
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LocalAppColors.current.surfaceVariant,
                            border = BorderStroke(1.dp, LocalAppColors.current.border),
                            modifier = Modifier.clickable {
                                haptics.lightTick()
                                if (isDirty) {
                                    pendingDismissAfterDiscard = false
                                    showDiscardDialog = true
                                } else {
                                    isEditMode = false
                                }
                            }
                        ) {
                            Text(
                                text = strings.cancel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        if (onDelete != null) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(ColorDangerExpense.copy(alpha = 0.12f))
                                    .clickable {
                                        haptics.warningFeedback()
                                        showDeleteConfirmDialog = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = ColorDangerExpense,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else {
                        // Add Mode: Close button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LocalAppColors.current.surfaceVariant)
                                .clickable {
                                    haptics.lightTick()
                                    attemptDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Close",
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Edit Mode Active Banner (when editing an existing transaction)
            if (existingTransaction != null && isEditMode) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColorAccent.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = strings.editModeActiveBanner,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorAccent
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                if (existingTransaction != null && !isEditMode) {
                    TransactionViewModeContent(
                        transaction = existingTransaction,
                        selectedCategory = selectedCategory,
                        matchedAccount = accounts.find { it.id == selectedAccountId } ?: accounts.find { it.name.equals(existingTransaction.accountName, true) },
                        selectedPaymentMethod = selectedPaymentMethod,
                        dateMillis = dateMillis,
                        notes = notes,
                        receiptImagePath = receiptImagePath,
                        receiptBitmap = receiptBitmap,
                        typeAccentColor = typeAccentColor,
                        currencySymbol = currencySymbol,
                        strings = strings,
                        dateOnlyFormat = dateOnlyFormat,
                        dayOfWeekFormat = dayOfWeekFormat,
                        timeOnlyFormat = timeOnlyFormat,
                        onViewReceipt = { showFullReceiptPreview = true }
                    )
                } else {
                    // 0. Smart Assistant Row: AI/SMS Auto-Fill + Receipt Voucher (side-by-side)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // AI Magic Fill / SMS Trigger Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (showAiMagicFill) Color(0xFF8B5CF6).copy(alpha = 0.18f) else Color(0xFF8B5CF6).copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, if (showAiMagicFill) Color(0xFF8B5CF6) else Color(0xFF8B5CF6).copy(alpha = 0.35f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    haptics.lightTick()
                                    showAiMagicFill = !showAiMagicFill
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.aiMagicFillBtn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showAiMagicFill) "▲" else "▼",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF8B5CF6)
                                )
                            }
                        }

                        // Receipt / Voucher Trigger Button
                        val isReceiptAttached = receiptImagePath != null
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isReceiptAttached) ColorSuccessIncome.copy(alpha = 0.15f) else Color(0xFF0D9488).copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, if (isReceiptAttached) ColorSuccessIncome else Color(0xFF0D9488).copy(alpha = 0.35f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    haptics.lightTick()
                                    if (isReceiptAttached) {
                                        showFullReceiptPreview = true
                                    } else {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isReceiptAttached) Icons.Outlined.Check else Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = if (isReceiptAttached) ColorSuccessIncome else Color(0xFF0D9488),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isReceiptAttached) (if (strings.isBangla) "রসিদ যুক্ত আছে" else "Receipt Added")
                                           else (if (strings.isBangla) "রসিদ / ভাউচার" else "Attach Receipt"),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isReceiptAttached) ColorSuccessIncome else ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Expandable AI Magic Fill Content
                    AnimatedVisibility(visible = showAiMagicFill) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.06f),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                OutlinedTextField(
                                    value = aiMagicInputText,
                                    onValueChange = { aiMagicInputText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            text = strings.aiVoiceInputPlaceholder,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                            color = ColorTextSecondary.copy(alpha = 0.6f)
                                        )
                                    },
                                    minLines = 2,
                                    maxLines = 3,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF8B5CF6),
                                        unfocusedBorderColor = LocalAppColors.current.border,
                                        focusedContainerColor = LocalAppColors.current.surface,
                                        unfocusedContainerColor = LocalAppColors.current.surface
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (aiMagicInputText.isNotBlank()) {
                                            haptics.mediumClick()
                                            scope.launch {
                                                isAiMagicParsing = true
                                                val parsed = GeminiAiService.parseTransactionFromNaturalText(
                                                    input = aiMagicInputText,
                                                    categories = categories,
                                                    accounts = accounts,
                                                    isBangla = strings.isBangla,
                                                    apiKey = geminiApiKey
                                                )
                                                isAiMagicParsing = false
                                                if (parsed != null) {
                                                    haptics.successFeedback()
                                                    parsed.title?.takeIf { it.isNotBlank() }?.let { title = it }
                                                    parsed.amount?.let { amt ->
                                                        amountText = if (amt % 1.0 == 0.0) amt.toLong().toString() else amt.toString()
                                                    }
                                                    parsed.type.let { t -> selectedType = t }
                                                    parsed.categoryName?.let { catName ->
                                                        val matched = categories.find {
                                                            it.name.equals(catName, ignoreCase = true) ||
                                                                    it.getLocalizedName(true).contains(catName, ignoreCase = true) ||
                                                                    catName.contains(it.name, ignoreCase = true)
                                                        }
                                                        if (matched != null) selectedCategory = matched
                                                    }
                                                    parsed.accountName?.let { accName ->
                                                        val matchedAcc = accounts.find {
                                                            it.name.contains(accName, ignoreCase = true) ||
                                                                    accName.contains(it.name, ignoreCase = true)
                                                        }
                                                        if (matchedAcc != null) selectedAccountId = matchedAcc.id
                                                    }
                                                    parsed.notes?.let { n ->
                                                        notes = if (notes.isBlank()) n else "$notes • $n"
                                                    }
                                                    aiMagicInputText = ""
                                                    showAiMagicFill = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = aiMagicInputText.isNotBlank() && !isAiMagicParsing,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                                ) {
                                    if (isAiMagicParsing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = strings.aiAnalyzing,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.SmartToy,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = strings.aiMagicFillBtn,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Compact Receipt Preview Bar (if receipt attached)
                    AnimatedVisibility(visible = receiptImagePath != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LocalAppColors.current.surfaceVariant,
                            border = BorderStroke(1.dp, LocalAppColors.current.border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ColorBorder.copy(alpha = 0.4f))
                                            .clickable { showFullReceiptPreview = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val bmp = receiptBitmap
                                        if (bmp != null) {
                                            Image(
                                                bitmap = bmp.asImageBitmap(),
                                                contentDescription = "Receipt Thumbnail",
                                                modifier = Modifier.matchParentSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Outlined.ReceiptLong,
                                                contentDescription = null,
                                                tint = ColorTextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (strings.isBangla) "রসিদ সংযুক্ত আছে" else "Receipt Attached",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.5.sp
                                            ),
                                            color = ColorTextPrimary
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                text = strings.viewReceipt,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = typeAccentColor,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 11.sp
                                                ),
                                                modifier = Modifier.clickable { showFullReceiptPreview = true }
                                            )
                                            Text("•", color = ColorTextMuted, fontSize = 10.sp)
                                            Text(
                                                text = strings.removeReceipt,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = ColorDangerExpense,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 11.sp
                                                ),
                                                modifier = Modifier.clickable {
                                                    haptics.lightTick()
                                                    ReceiptImageHelper.deleteReceiptImage(context, receiptImagePath)
                                                    receiptImagePath = null
                                                    receiptBitmap = null
                                                    scanStatusMessage = null
                                                }
                                            )
                                        }
                                    }

                                    // AI Smart Scan Button
                                    Button(
                                        onClick = {
                                            val bmp = receiptBitmap ?: return@Button
                                            if (geminiApiKey.isNullOrBlank()) {
                                                scanStatusMessage = if (strings.isBangla) "সেটিংস থেকে আপনার Gemini API Key সেট করুন"
                                                else "Please set your Gemini API Key in Settings"
                                                haptics.warningFeedback()
                                                return@Button
                                            }

                                            isScanningReceipt = true
                                            scanStatusMessage = strings.scanningReceipt
                                            haptics.mediumClick()

                                            scope.launch {
                                                val result = GeminiAiService.scanReceipt(
                                                    apiKey = geminiApiKey,
                                                    bitmap = bmp,
                                                    categories = categories
                                                )
                                                isScanningReceipt = false
                                                if (result != null) {
                                                    haptics.successFeedback()
                                                    scanStatusMessage = strings.receiptScanSuccess
                                                    result.amount?.let { scannedAmt ->
                                                        amountText = if (scannedAmt % 1.0 == 0.0) scannedAmt.toLong().toString() else scannedAmt.toString()
                                                    }
                                                    result.title?.let { scannedTitle ->
                                                        title = scannedTitle
                                                    }
                                                    result.categoryName?.let { catName ->
                                                        val matchedCat = categories.find {
                                                            it.name.equals(catName, ignoreCase = true) ||
                                                                    it.getLocalizedName(true).contains(catName, ignoreCase = true) ||
                                                                    catName.contains(it.name, ignoreCase = true)
                                                        }
                                                        if (matchedCat != null) {
                                                            selectedCategory = matchedCat
                                                            selectedType = matchedCat.type
                                                        }
                                                    }
                                                    result.dateMillis?.let { dateMillis = it }
                                                    result.notes?.let { scannedNotes ->
                                                        notes = if (notes.isBlank()) scannedNotes else "$notes • $scannedNotes"
                                                    }
                                                    result.paymentMethod?.let { selectedPaymentMethod = it }
                                                } else {
                                                    haptics.warningFeedback()
                                                    scanStatusMessage = strings.receiptScanFailed
                                                }
                                            }
                                        },
                                        enabled = !isScanningReceipt && receiptBitmap != null,
                                        modifier = Modifier.height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ColorAccent.copy(alpha = 0.15f),
                                            contentColor = ColorAccent
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                        elevation = null
                                    ) {
                                        if (isScanningReceipt) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = ColorAccent,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.SmartToy,
                                                    contentDescription = null,
                                                    tint = ColorAccent,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = strings.scanReceiptWithAi,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                                    color = ColorAccent
                                                )
                                            }
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = scanStatusMessage != null) {
                                    scanStatusMessage?.let { msg ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = if (msg == strings.receiptScanSuccess) ColorSuccessIncome else ColorTextSecondary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Fluid Animated Income/Expense Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LocalAppColors.current.surfaceVariant)
                        .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Expense Tab
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = if (isExpense) ColorDangerExpense else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (selectedType != TransactionType.EXPENSE) {
                                    haptics.mediumClick()
                                    selectedType = TransactionType.EXPENSE
                                    selectedCategory = categories.firstOrNull { it.type == TransactionType.EXPENSE }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowUpward,
                                contentDescription = null,
                                tint = if (isExpense) Color.White else ColorTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.expense,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isExpense) Color.White else ColorTextSecondary
                            )
                        }
                    }

                    // Income Tab
                    val isIncome = selectedType == TransactionType.INCOME
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = if (isIncome) ColorSuccessIncome else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (selectedType != TransactionType.INCOME) {
                                    haptics.mediumClick()
                                    selectedType = TransactionType.INCOME
                                    selectedCategory = categories.firstOrNull { it.type == TransactionType.INCOME }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowDownward,
                                contentDescription = null,
                                tint = if (isIncome) Color.White else ColorTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.income,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isIncome) Color.White else ColorTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Modern Luxury Hero Amount Card with Quick Increments
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(typeAccentColor.copy(alpha = 0.45f), LocalAppColors.current.border)
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(LocalAppColors.current.surface, LocalAppColors.current.surfaceVariant)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top row inside card: Type indicator + Calculator toggle
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
                                            .background(typeAccentColor)
                                    )
                                    Text(
                                        text = if (isExpense) strings.expenseAmount else strings.incomeAmount,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 0.2.sp
                                        ),
                                        color = ColorTextSecondary
                                    )
                                }

                                // Calculator keypad toggle button
                                Surface(
                                    shape = CircleShape,
                                    color = if (showCalculatorBar) typeAccentColor.copy(alpha = 0.2f) else LocalAppColors.current.surfaceVariant,
                                    border = BorderStroke(1.dp, if (showCalculatorBar) typeAccentColor else LocalAppColors.current.border),
                                    modifier = Modifier.clickable {
                                        haptics.lightTick()
                                        showCalculatorBar = !showCalculatorBar
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Calculate,
                                            contentDescription = "Calculator",
                                            tint = if (showCalculatorBar) typeAccentColor else ColorTextSecondary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = strings.calc,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = if (showCalculatorBar) typeAccentColor else ColorTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Center row: Currency Symbol + Amount Input + Clear Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = typeAccentColor.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, typeAccentColor.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = currencySymbol,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black
                                        ),
                                        color = typeAccentColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = amountText,
                                    onValueChange = { input ->
                                        val sanitized = input.filter { it.isDigit() || it == '.' || it in "+-×÷/* " }
                                        amountText = sanitized
                                        errorMessage = null
                                    },
                                    placeholder = {
                                        Text(
                                            text = "0.00",
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Black,
                                            color = ColorTextMuted.copy(alpha = 0.4f)
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ColorTextPrimary,
                                        textAlign = TextAlign.Start
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        cursorColor = typeAccentColor
                                    ),
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .focusRequester(amountFocusRequester),
                                    singleLine = true
                                )

                                if (amountText.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            haptics.lightTick()
                                            amountText = ""
                                            errorMessage = null
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Clear,
                                            contentDescription = "Clear",
                                            tint = ColorTextMuted,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                }
                            }

                            // Calculator live preview badge
                            CalculatorPreviewBadge(
                                expression = amountText,
                                currencySymbol = currencySymbol,
                                onApply = { evaluated ->
                                    haptics.successFeedback()
                                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            // Collapsible Calculator Keypad Bar
                            AnimatedVisibility(
                                visible = showCalculatorBar,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
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
                }

                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorDangerExpense.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = ColorDangerExpense,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Title / Description Field (Spacious & Modern)
                Text(
                    text = strings.titleOrDescription,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            text = strings.titlePlaceholder,
                            color = ColorTextMuted.copy(alpha = 0.7f),
                            fontSize = 13.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(typeAccentColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Title,
                                contentDescription = null,
                                tint = typeAccentColor,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        if (title.isNotEmpty()) {
                            IconButton(onClick = { title = "" }, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Clear",
                                    tint = ColorTextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LocalAppColors.current.surfaceVariant,
                        unfocusedContainerColor = LocalAppColors.current.surfaceVariant,
                        focusedBorderColor = typeAccentColor.copy(alpha = 0.8f),
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary,
                        cursorColor = typeAccentColor
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes Field (Grouped directly under Title)
                Text(
                    text = strings.notesOptional,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = {
                        Text(
                            text = strings.notesPlaceholder,
                            color = ColorTextMuted.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorBorder.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Notes,
                                contentDescription = null,
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        if (notes.isNotEmpty()) {
                            IconButton(onClick = { notes = "" }, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Clear",
                                    tint = ColorTextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LocalAppColors.current.surfaceVariant,
                        unfocusedContainerColor = LocalAppColors.current.surfaceVariant,
                        focusedBorderColor = typeAccentColor.copy(alpha = 0.8f),
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary,
                        cursorColor = typeAccentColor
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 1,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Category & Account / Payment Method (Single Row - 50%/50% Dropdowns)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left: Category Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        val catColor = selectedCategory?.let { CategoryIconHelper.parseColor(it.colorHex) } ?: typeAccentColor
                        Column {
                            Text(
                                text = strings.category,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = LocalAppColors.current.surfaceVariant,
                                border = BorderStroke(1.dp, if (showCategoryDropdown) typeAccentColor else LocalAppColors.current.border),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clickable {
                                        haptics.lightTick()
                                        showCategoryDropdown = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f, fill = false),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(catColor.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = CategoryIconHelper.getIcon(selectedCategory?.icon ?: "MoreHoriz"),
                                                contentDescription = null,
                                                tint = catColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Text(
                                            text = selectedCategory?.getLocalizedName(strings.isBangla)
                                                ?: strings.selectCategoryPrompt,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = ColorTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select Category",
                                        tint = ColorTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false },
                            modifier = Modifier
                                .widthIn(min = 220.dp, max = 280.dp)
                                .background(LocalAppColors.current.surface)
                        ) {
                            availableCategories.forEach { cat ->
                                val isSelected = selectedCategory?.id == cat.id
                                val itemColor = CategoryIconHelper.parseColor(cat.colorHex)
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(itemColor.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = CategoryIconHelper.getIcon(cat.icon),
                                                contentDescription = null,
                                                tint = itemColor,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = cat.getLocalizedName(strings.isBangla),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = ColorTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    trailingIcon = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = typeAccentColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        haptics.lightTick()
                                        selectedCategory = cat
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Right: Account / Payment Method Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        val currentAccount = accounts.find { it.id == selectedAccountId }
                        val brandStyle = currentAccount?.let { BrandIdentityHelper.getBrandStyle(it) }

                        Column {
                            Text(
                                text = if (accounts.isNotEmpty()) strings.account else strings.paymentMethod,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = LocalAppColors.current.surfaceVariant,
                                border = BorderStroke(1.dp, if (showAccountDropdown) typeAccentColor else LocalAppColors.current.border),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clickable {
                                        haptics.lightTick()
                                        showAccountDropdown = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f, fill = false),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (currentAccount != null && brandStyle != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(brandStyle.gradient),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = brandStyle.icon,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                                Text(
                                                    text = currentAccount.name,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = ColorTextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = formatCurrency(currentAccount.currentBalance, currencySymbol, strings.isBangla),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = brandStyle.primaryColor,
                                                    maxLines = 1
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(typeAccentColor.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Payment,
                                                    contentDescription = null,
                                                    tint = typeAccentColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Text(
                                                text = selectedPaymentMethod.getDisplayName(strings.isBangla),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = ColorTextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select Account",
                                        tint = ColorTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showAccountDropdown,
                            onDismissRequest = { showAccountDropdown = false },
                            modifier = Modifier
                                .widthIn(min = 230.dp, max = 290.dp)
                                .background(LocalAppColors.current.surface)
                        ) {
                            if (accounts.isNotEmpty()) {
                                accounts.forEach { acc ->
                                    val isSelected = selectedAccountId == acc.id
                                    val accBrand = BrandIdentityHelper.getBrandStyle(acc)
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(accBrand.gradient),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = accBrand.icon,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        },
                                        text = {
                                            Column {
                                                Text(
                                                    text = acc.name,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    ),
                                                    color = ColorTextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = formatCurrency(acc.currentBalance, currencySymbol, strings.isBangla),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = if (isSelected) accBrand.primaryColor else ColorTextMuted
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = accBrand.primaryColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            haptics.lightTick()
                                            selectedAccountId = acc.id
                                            selectedPaymentMethod = when (acc.type) {
                                                AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                                                AccountType.MFS -> {
                                                    val lower = (acc.name + " " + acc.institutionName).lowercase()
                                                    if (lower.contains("nagad") || lower.contains("নগদ")) PaymentMethod.NAGAD
                                                    else if (lower.contains("rocket") || lower.contains("রকেট")) PaymentMethod.OTHER
                                                    else PaymentMethod.BKASH
                                                }
                                                AccountType.CASH -> PaymentMethod.CASH
                                            }
                                            showAccountDropdown = false
                                        }
                                    )
                                }
                            } else {
                                PaymentMethod.entries.forEach { method ->
                                    val isSelected = selectedPaymentMethod == method
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = method.getDisplayName(strings.isBangla),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = ColorTextPrimary
                                            )
                                        },
                                        trailingIcon = {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = typeAccentColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            haptics.lightTick()
                                            selectedPaymentMethod = method
                                            showAccountDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Account Insufficient Balance Warning (Inline)
                val parsedCurrentAmount = amountText.toParsedAmountOrNull() ?: 0.0
                val currentSelectedAcc = accounts.find { it.id == selectedAccountId }
                if (isExpense && currentSelectedAcc != null && parsedCurrentAmount > currentSelectedAcc.currentBalance && currentSelectedAcc.currentBalance > 0.0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorWarningBills.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, ColorWarningBills.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = ColorWarningBills,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (strings.isBangla)
                                    "${currentSelectedAcc.getLocalizedName(true)}-এ ব্যালেন্স ${formatCurrency(currentSelectedAcc.currentBalance, currencySymbol, true)}। খরচ করার পর ব্যালেন্স ঋণাত্মক হবে।"
                                else
                                    "Balance in ${currentSelectedAcc.getLocalizedName(false)} is ${formatCurrency(currentSelectedAcc.currentBalance, currencySymbol, false)}. Balance will become negative after this expense.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorWarningBills
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7. Date & Time Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.dateTimeLabel,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )
                    Text(
                        text = strings.tapToChange,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ColorTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Interactive Date & Time Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(SmallElementRadius))
                        .background(LocalAppColors.current.surfaceVariant)
                        .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(SmallElementRadius))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date picker trigger
                    Row(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                haptics.lightTick()
                                showDatePickerDialog = true
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Pick date",
                            tint = typeAccentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = dateOnlyFormat.format(Date(dateMillis)),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextPrimary
                            )
                            Text(
                                text = dayOfWeekFormat.format(Date(dateMillis)),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .height(26.dp)
                            .width(1.dp)
                            .background(ColorBorder)
                    )

                    // Time picker trigger
                    Row(
                        modifier = Modifier
                            .weight(0.9f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                haptics.lightTick()
                                showTimePickerDialog = true
                            }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Pick time",
                            tint = ColorTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timeOnlyFormat.format(Date(dateMillis)),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = ColorTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date Quick Shortcuts: Today | Yesterday | Calendar | Now
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isToday = isSameDay(dateMillis, System.currentTimeMillis())
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isToday) typeAccentColor.copy(alpha = 0.15f) else ColorSurface,
                        border = BorderStroke(1.dp, if (isToday) typeAccentColor else ColorBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptics.lightTick()
                                val nowCal = Calendar.getInstance()
                                val cur = Calendar.getInstance().apply { timeInMillis = dateMillis }
                                cur.set(Calendar.YEAR, nowCal.get(Calendar.YEAR))
                                cur.set(Calendar.DAY_OF_YEAR, nowCal.get(Calendar.DAY_OF_YEAR))
                                dateMillis = cur.timeInMillis
                            }
                    ) {
                        Text(
                            text = strings.today,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isToday) typeAccentColor else ColorTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    val yesterdayMillis = remember {
                        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
                    }
                    val isYesterday = isSameDay(dateMillis, yesterdayMillis)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isYesterday) typeAccentColor.copy(alpha = 0.15f) else ColorSurface,
                        border = BorderStroke(1.dp, if (isYesterday) typeAccentColor else ColorBorder),
                        modifier = Modifier
                            .weight(1.1f)
                            .clickable {
                                haptics.lightTick()
                                val yestCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                                val cur = Calendar.getInstance().apply { timeInMillis = dateMillis }
                                cur.set(Calendar.YEAR, yestCal.get(Calendar.YEAR))
                                cur.set(Calendar.DAY_OF_YEAR, yestCal.get(Calendar.DAY_OF_YEAR))
                                dateMillis = cur.timeInMillis
                            }
                    ) {
                        Text(
                            text = strings.yesterday,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isYesterday) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isYesterday) typeAccentColor else ColorTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ColorSurface,
                        border = BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptics.lightTick()
                                showDatePickerDialog = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EditCalendar,
                                contentDescription = null,
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = strings.calendar,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ColorSurface,
                        border = BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier
                            .weight(0.9f)
                            .clickable {
                                haptics.lightTick()
                                dateMillis = System.currentTimeMillis()
                            }
                    ) {
                        Text(
                            text = strings.now,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ColorTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

            // 9. Docked / Sticky Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ColorSurface,
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    if (existingTransaction != null && !isEditMode) {
                        // View Mode Action Bar: Primary "Edit Transaction" button
                        Button(
                            onClick = {
                                haptics.mediumClick()
                                isEditMode = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAccent,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isExpense) "${strings.editTransaction} (${strings.expense})" else "${strings.editTransaction} (${strings.income})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    } else if (existingTransaction != null && isEditMode) {
                        // Edit Mode Action Bar: Cancel + Update buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    haptics.lightTick()
                                    if (isDirty) {
                                        pendingDismissAfterDiscard = false
                                        showDiscardDialog = true
                                    } else {
                                        isEditMode = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(0.35f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, LocalAppColors.current.border),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ColorTextSecondary
                                )
                            ) {
                                Text(
                                    text = strings.cancel,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            val isAmountValid = (amountText.toParsedAmountOrNull() ?: 0.0) > 0.0
                            Button(
                                onClick = {
                                    val evaluatedAmount = amountText.toParsedAmountOrNull()
                                    if (evaluatedAmount == null || evaluatedAmount <= 0.0) {
                                        errorMessage = strings.amountGreaterThanZero
                                        haptics.warningFeedback()
                                        return@Button
                                    }
                                    val amount = evaluatedAmount
                                    val category = selectedCategory ?: run {
                                        errorMessage = strings.selectCategoryPrompt
                                        haptics.warningFeedback()
                                        return@Button
                                    }

                                    val finalTitle = title.ifBlank { category.getLocalizedName(strings.isBangla) }
                                    val effectiveAccountId = selectedAccountId
                                        ?: accounts.find { it.isDefault }?.id
                                        ?: accounts.firstOrNull()?.id
                                    val matchedAccount = accounts.find { it.id == effectiveAccountId }

                                    val tx = Transaction(
                                        id = existingTransaction.id,
                                        title = finalTitle,
                                        amount = amount,
                                        type = selectedType,
                                        categoryId = category.id,
                                        categoryName = category.name,
                                        categoryIcon = category.icon,
                                        categoryColor = category.colorHex,
                                        dateMillis = dateMillis,
                                        paymentMethod = selectedPaymentMethod,
                                        accountId = effectiveAccountId,
                                        accountName = matchedAccount?.name ?: existingTransaction.accountName ?: "Cash in Hand",
                                        notes = notes,
                                        receiptImagePath = receiptImagePath
                                    )
                                    haptics.successFeedback()
                                    onSave(tx)
                                },
                                modifier = Modifier
                                    .weight(0.65f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAmountValid) typeAccentColor else typeAccentColor.copy(alpha = 0.4f),
                                    contentColor = if (isAmountValid) Color.White else Color.White.copy(alpha = 0.6f)
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isAmountValid) 2.dp else 0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))

                                val currentAmountVal = amountText.toParsedAmountOrNull()
                                val amountLabel = if (currentAmountVal != null && currentAmountVal > 0.0) {
                                    " • " + formatCurrency(currentAmountVal, currencySymbol)
                                } else ""

                                val buttonText = if (isExpense) "${strings.updateExpense}$amountLabel" else "${strings.updateIncome}$amountLabel"

                                Text(
                                    text = buttonText,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        // New transaction (existingTransaction == null): Single Save button
                        val isAmountValid = (amountText.toParsedAmountOrNull() ?: 0.0) > 0.0
                        Button(
                            onClick = {
                                val evaluatedAmount = amountText.toParsedAmountOrNull()
                                if (evaluatedAmount == null || evaluatedAmount <= 0.0) {
                                    errorMessage = strings.amountGreaterThanZero
                                    haptics.warningFeedback()
                                    return@Button
                                }
                                val amount = evaluatedAmount
                                val category = selectedCategory ?: run {
                                    errorMessage = strings.selectCategoryPrompt
                                    haptics.warningFeedback()
                                    return@Button
                                }

                                val finalTitle = title.ifBlank { category.getLocalizedName(strings.isBangla) }
                                val effectiveAccountId = selectedAccountId
                                    ?: accounts.find { it.isDefault }?.id
                                    ?: accounts.firstOrNull()?.id
                                val matchedAccount = accounts.find { it.id == effectiveAccountId }

                                val tx = Transaction(
                                    id = 0L,
                                    title = finalTitle,
                                    amount = amount,
                                    type = selectedType,
                                    categoryId = category.id,
                                    categoryName = category.name,
                                    categoryIcon = category.icon,
                                    categoryColor = category.colorHex,
                                    dateMillis = dateMillis,
                                    paymentMethod = selectedPaymentMethod,
                                    accountId = effectiveAccountId,
                                    accountName = matchedAccount?.name ?: "Cash in Hand",
                                    notes = notes,
                                    receiptImagePath = receiptImagePath
                                )
                                haptics.successFeedback()
                                onSave(tx)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAmountValid) typeAccentColor else typeAccentColor.copy(alpha = 0.4f),
                                contentColor = if (isAmountValid) Color.White else Color.White.copy(alpha = 0.6f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isAmountValid) 2.dp else 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            val currentAmountVal = amountText.toParsedAmountOrNull()
                            val amountLabel = if (currentAmountVal != null && currentAmountVal > 0.0) {
                                " • " + formatCurrency(currentAmountVal, currencySymbol)
                            } else ""

                            val buttonText = if (isExpense) "${strings.saveExpense}$amountLabel" else "${strings.saveIncome}$amountLabel"

                            Text(
                                text = buttonText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog && existingTransaction != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = strings.deleteTransactionTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla) "\"${existingTransaction.title}\" লেনদেনটি স্থায়ীভাবে মুছে ফেলতে চান? এটি আর ফিরিয়ে আনা যাবে না।"
                    else "Are you sure you want to permanently delete \"${existingTransaction.title}\"? This action cannot be undone.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete?.invoke(existingTransaction.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Material 3 Date Picker Dialog
    if (showDatePickerDialog) {
        val localCal = remember(dateMillis) {
            Calendar.getInstance().apply { timeInMillis = dateMillis }
        }
        val initialUtcMillis = remember(dateMillis) {
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
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedUtcMillis ->
                            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = selectedUtcMillis
                            }
                            val selYear = utcCal.get(Calendar.YEAR)
                            val selMonth = utcCal.get(Calendar.MONTH)
                            val selDay = utcCal.get(Calendar.DAY_OF_MONTH)

                            val updatedLocal = Calendar.getInstance().apply {
                                timeInMillis = dateMillis
                                set(Calendar.YEAR, selYear)
                                set(Calendar.MONTH, selMonth)
                                set(Calendar.DAY_OF_MONTH, selDay)
                            }
                            dateMillis = updatedLocal.timeInMillis
                        }
                        showDatePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = typeAccentColor),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.selectDatePrompt, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            },
            shape = RoundedCornerShape(CardCornerRadius),
            colors = DatePickerDefaults.colors(
                containerColor = ColorSurface,
                titleContentColor = ColorTextPrimary,
                headlineContentColor = ColorTextPrimary,
                weekdayContentColor = ColorTextSecondary,
                subheadContentColor = ColorTextSecondary,
                yearContentColor = ColorTextPrimary,
                currentYearContentColor = typeAccentColor,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = typeAccentColor,
                dayContentColor = ColorTextPrimary,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = typeAccentColor,
                todayContentColor = typeAccentColor,
                todayDateBorderColor = typeAccentColor
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = ColorSurface,
                    titleContentColor = ColorTextPrimary,
                    headlineContentColor = ColorTextPrimary,
                    weekdayContentColor = ColorTextSecondary,
                    subheadContentColor = ColorTextSecondary,
                    yearContentColor = ColorTextPrimary,
                    currentYearContentColor = typeAccentColor,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = typeAccentColor,
                    dayContentColor = ColorTextPrimary,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = typeAccentColor,
                    todayContentColor = typeAccentColor,
                    todayDateBorderColor = typeAccentColor
                )
            )
        }
    }

    // Material 3 Time Picker Dialog
    if (showTimePickerDialog) {
        val timePickerCal = remember(dateMillis) {
            Calendar.getInstance().apply { timeInMillis = dateMillis }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = timePickerCal.get(Calendar.HOUR_OF_DAY),
            initialMinute = timePickerCal.get(Calendar.MINUTE),
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            title = {
                Text(
                    text = strings.selectTime,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = ColorBorder,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = ColorTextPrimary,
                            selectorColor = typeAccentColor,
                            containerColor = ColorSurface,
                            periodSelectorBorderColor = ColorBorder,
                            periodSelectorSelectedContainerColor = typeAccentColor,
                            periodSelectorUnselectedContainerColor = ColorSurface,
                            periodSelectorSelectedContentColor = Color.White,
                            periodSelectorUnselectedContentColor = ColorTextSecondary,
                            timeSelectorSelectedContainerColor = typeAccentColor,
                            timeSelectorUnselectedContainerColor = ColorBorder,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = ColorTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = Calendar.getInstance().apply {
                            timeInMillis = dateMillis
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        dateMillis = updated.timeInMillis
                        showTimePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = typeAccentColor),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.setTime, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Full-screen / Zoomed Receipt Preview Dialog
    if (showFullReceiptPreview && receiptBitmap != null) {
        AlertDialog(
            onDismissRequest = { showFullReceiptPreview = false },
            confirmButton = {
                Button(
                    onClick = { showFullReceiptPreview = false },
                    colors = ButtonDefaults.buttonColors(containerColor = typeAccentColor),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.cancel, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        haptics.lightTick()
                        ReceiptImageHelper.deleteReceiptImage(context, receiptImagePath)
                        receiptImagePath = null
                        receiptBitmap = null
                        showFullReceiptPreview = false
                    }
                ) {
                    Text(strings.removeReceipt, color = ColorDangerExpense)
                }
            },
            title = {
                Text(
                    text = strings.viewReceipt,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ColorTextPrimary
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    val bmp = receiptBitmap
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Full Receipt",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = {
                Text(
                    text = strings.discardDialogTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LocalAppColors.current.textPrimary
                )
            },
            text = {
                Text(
                    text = strings.discardDialogMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalAppColors.current.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardDialog = false
                        if (existingTransaction != null) {
                            resetToOriginal()
                            if (pendingDismissAfterDiscard) {
                                onDismiss()
                            } else {
                                isEditMode = false
                            }
                        } else {
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.discard, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(strings.keepEditing, color = LocalAppColors.current.textPrimary)
                }
            },
            containerColor = LocalAppColors.current.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun TransactionViewModeContent(
    transaction: Transaction,
    selectedCategory: Category?,
    matchedAccount: Account?,
    selectedPaymentMethod: PaymentMethod,
    dateMillis: Long,
    notes: String,
    receiptImagePath: String?,
    receiptBitmap: Bitmap?,
    typeAccentColor: Color,
    currencySymbol: String,
    strings: Strings,
    dateOnlyFormat: SimpleDateFormat,
    dayOfWeekFormat: SimpleDateFormat,
    timeOnlyFormat: SimpleDateFormat,
    onViewReceipt: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Amount Card (View Mode - Read Only)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(
                1.dp,
                Brush.verticalGradient(
                    listOf(typeAccentColor.copy(alpha = 0.45f), LocalAppColors.current.border)
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                typeAccentColor.copy(alpha = 0.08f),
                                LocalAppColors.current.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Type badge with dot
                    Surface(
                        shape = CircleShape,
                        color = typeAccentColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, typeAccentColor.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(typeAccentColor)
                            )
                            Text(
                                text = if (transaction.type == TransactionType.EXPENSE) strings.expense else strings.income,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = typeAccentColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Big Bold Amount
                    val amountFormatted = formatCurrency(transaction.amount, currencySymbol, globalBengaliNumeralsEnabled || strings.isBangla)
                    val prefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
                    Text(
                        text = "$prefix $amountFormatted",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = typeAccentColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Context subtitle (Category & Account)
                    val catName = selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName
                    val accName = getLocalizedAccountName(matchedAccount?.name ?: transaction.accountName ?: "", strings.isBangla)
                    val subText = if (accName.isNotBlank()) "$catName • $accName" else catName
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextSecondary
                    )
                }
            }
        }

        // Transaction Details Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surfaceVariant),
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title & Category Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconBadge(
                        iconName = selectedCategory?.icon ?: transaction.categoryIcon,
                        colorHex = selectedCategory?.colorHex ?: transaction.categoryColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = transaction.title.ifBlank { selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = ColorTextSecondary
                        )
                    }
                }

                HorizontalDivider(color = LocalAppColors.current.border.copy(alpha = 0.7f), thickness = 0.8.dp)

                // Date & Time Row
                val rawDate = dateOnlyFormat.format(Date(dateMillis))
                val dateDisp = if (strings.isBangla) convertToBengaliDigits(rawDate) else rawDate
                val dayDisp = dayOfWeekFormat.format(Date(dateMillis))
                val timeDisp = DateTimeHelper.formatTimeOnly(dateMillis, strings.isBangla, globalBengaliNumeralsEnabled || strings.isBangla)
                TransactionDetailInfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = strings.dateTimeLabel,
                    value = "$dateDisp ($dayDisp) • $timeDisp"
                )

                // Account Row
                TransactionDetailInfoRow(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    label = strings.account,
                    value = getLocalizedAccountName(matchedAccount?.name ?: transaction.accountName ?: "Cash in Hand", strings.isBangla)
                )

                // Payment Method Row
                TransactionDetailInfoRow(
                    icon = Icons.Outlined.Payment,
                    label = strings.paymentMethod,
                    value = selectedPaymentMethod.getDisplayName(strings.isBangla)
                )
            }
        }

        // Notes Card (if present)
        if (notes.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surfaceVariant),
                border = BorderStroke(1.dp, LocalAppColors.current.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = ColorTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = strings.note,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp
                        ),
                        color = ColorTextPrimary
                    )
                }
            }
        }

        // Receipt Card (if attached)
        if (receiptImagePath != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surfaceVariant),
                border = BorderStroke(1.dp, LocalAppColors.current.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewReceipt() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (receiptBitmap != null) {
                        Image(
                            bitmap = receiptBitmap.asImageBitmap(),
                            contentDescription = strings.viewReceipt,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ColorAccent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.viewReceipt,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = strings.tapToViewReceipt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp
                            ),
                            color = ColorTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = strings.viewReceipt,
                        tint = ColorAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ColorTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.5.sp
                ),
                color = ColorTextSecondary
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = ColorTextPrimary
        )
    }
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
