package com.example.expensetracker.ui.transactions

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.expensetracker.ui.dashboard.MarketNativeContent
import java.io.File
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionSheet(
    sheetState: SheetState,
    existingTransaction: Transaction? = null,
    initialDateMillis: Long? = null,
    initialType: TransactionType = TransactionType.EXPENSE,
    initialOpenMarketTab: Boolean = false,
    categories: List<Category>,
    accounts: List<Account> = emptyList(),
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: ((Long) -> Unit)? = null,
    marketListWithItems: MarketListWithItems? = null,
    onCreateMarketListWithBulk: ((title: String, items: List<MarketItem>) -> Unit)? = null,
    onAddMarketItem: ((item: MarketItem) -> Unit)? = null,
    onUpdateMarketItem: ((item: MarketItem) -> Unit)? = null,
    onDeleteMarketItem: ((itemId: Long) -> Unit)? = null,
    onMarkMarketItemPurchased: ((item: MarketItem, amount: Double) -> Unit)? = null,
    onUnmarkMarketItemPurchased: ((item: MarketItem) -> Unit)? = null,
    onCompleteMarketList: ((listId: Long) -> Unit)? = null,
    onOpenMarketList: (() -> Unit)? = null,
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(),
    onCompleteMarketListWithRollOver: ((listId: Long, rollOverItems: List<MarketItem>) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state = remember(existingTransaction, initialType, initialDateMillis) {
        AddEditTransactionState(
            existingTransaction = existingTransaction,
            initialType = initialType,
            initialDateMillis = initialDateMillis,
            categories = categories,
            accounts = accounts,
            strings = strings,
            haptics = haptics,
            context = context,
            scope = scope,
            onDismiss = onDismiss,
            onSave = onSave
        )
    }

    LaunchedEffect(state.receiptImagePath) {
        state.receiptBitmap = state.receiptImagePath?.let { ReceiptImageHelper.loadReceiptBitmap(context, it) }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    var pendingCameraUri by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingCameraFilePath by rememberSaveable { mutableStateOf<String?>(null) }
    var showReceiptSourceChooser by rememberSaveable { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val previousUncommittedPath = state.receiptImagePath?.takeIf { it != existingTransaction?.receiptImagePath }
                val saved = ReceiptImageHelper.saveReceiptImage(context, uri)
                if (saved != null) {
                    ReceiptImageHelper.deleteReceiptImage(context, previousUncommittedPath)
                    state.receiptImagePath = saved
                    haptics.lightTick()
                } else {
                    state.errorMessage = if (strings.isBangla) "ছবি লোড করা সম্ভব হয়নি" else "Failed to load image"
                    haptics.warningFeedback()
                }
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uriStr = pendingCameraUri
        val filePath = pendingCameraFilePath
        val tempFile = filePath?.let { File(it) }

        if (success && uriStr != null) {
            val uri = Uri.parse(uriStr)
            scope.launch {
                val previousUncommittedPath = state.receiptImagePath?.takeIf { it != existingTransaction?.receiptImagePath }
                val savedPath = ReceiptImageHelper.saveReceiptImage(context, uri)
                if (savedPath != null) {
                    ReceiptImageHelper.deleteReceiptImage(context, previousUncommittedPath)
                    state.receiptImagePath = savedPath
                    haptics.successFeedback()
                } else {
                    state.errorMessage = if (strings.isBangla) "ছবি সংরক্ষণ করা সম্ভব হয়নি" else "Failed to save photo"
                    haptics.warningFeedback()
                }
                ReceiptImageHelper.deleteFileQuietly(tempFile)
                pendingCameraUri = null
                pendingCameraFilePath = null
            }
        } else {
            // Cancelled by user or failed
            ReceiptImageHelper.deleteFileQuietly(tempFile)
            pendingCameraUri = null
            pendingCameraFilePath = null
        }
    }

    val onLaunchCamera: () -> Unit = {
        val pair = ReceiptImageHelper.createCameraTempFileAndUri(context)
        if (pair != null) {
            pendingCameraFilePath = pair.first.absolutePath
            pendingCameraUri = pair.second.toString()
            try {
                takePictureLauncher.launch(pair.second)
            } catch (e: Exception) {
                e.printStackTrace()
                ReceiptImageHelper.deleteFileQuietly(pair.first)
                pendingCameraUri = null
                pendingCameraFilePath = null
                state.errorMessage = if (strings.isBangla) "ক্যামেরা ওপেন করা সম্ভব হয়নি" else "Could not open camera"
                haptics.warningFeedback()
            }
        } else {
            state.errorMessage = if (strings.isBangla) "ক্যামেরা প্রস্তুত করা সম্ভব হয়নি" else "Could not prepare camera"
            haptics.warningFeedback()
        }
    }

    val onLaunchGallery: () -> Unit = {
        try {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (e: Exception) {
            e.printStackTrace()
            state.errorMessage = if (strings.isBangla) "গ্যালারি ওপেন করা সম্ভব হয়নি" else "Could not open gallery"
            haptics.warningFeedback()
        }
    }

    LaunchedEffect(Unit) {
        if (existingTransaction == null) {
            delay(200)
            try { state.amountFocusRequester.requestFocus() } catch (_: Exception) {}
        }
    }

    var isMarketActive by rememberSaveable(existingTransaction, initialOpenMarketTab) {
        mutableStateOf(initialOpenMarketTab && existingTransaction == null)
    }

    LaunchedEffect(initialOpenMarketTab) {
        if (existingTransaction == null) {
            isMarketActive = initialOpenMarketTab
        }
    }

    var hasVisitedTransactionForm by rememberSaveable {
        mutableStateOf(!initialOpenMarketTab)
    }

    LaunchedEffect(isMarketActive) {
        if (!isMarketActive) {
            hasVisitedTransactionForm = true
        }
    }

    val marketTint = if (appColors.isDark) Color(0xFF00A8FF) else Color(0xFF0284C7)
    val typeAccentColor by animateColorAsState(
        targetValue = when {
            isMarketActive -> marketTint
            state.isExpense -> appColors.expense
            else -> appColors.income
        },
        animationSpec = tween(300),
        label = "typeAccentColor"
    )

    val dateOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }
    val dayOfWeekFormat = remember(strings.isBangla) { SimpleDateFormat("EEEE", DateTimeHelper.getLocale(strings.isBangla)) }
    val timeOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("hh:mm a", DateTimeHelper.getLocale(strings.isBangla)) }

    BackHandler(enabled = isMarketActive && hasVisitedTransactionForm) {
        haptics.lightTick()
        isMarketActive = false
    }

    ModalBottomSheet(
        onDismissRequest = { state.attemptDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = LocalAppColors.current.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionSheetHeader(
                existingTransaction = existingTransaction,
                isEditMode = state.isEditMode,
                isExpense = state.isExpense,
                typeAccentColor = typeAccentColor,
                strings = strings,
                onCloseClick = { state.attemptDismiss() },
                onDeleteClick = onDelete?.let { { state.showDeleteConfirmDialog = true } },
                onCancelEditClick = {
                    if (state.isDirty) {
                        state.pendingDismissAfterDiscard = false
                        state.showDiscardDialog = true
                    } else state.isEditMode = false
                },
                isMarketActive = isMarketActive
            )

            AnimatedContent(
                targetState = isMarketActive,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.99f, animationSpec = tween(220)))
                        .togetherWith(fadeOut(animationSpec = tween(160)))
                },
                label = "MarketTransactionTransition",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { marketActive ->
                if (marketActive) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        MarketNativeContent(
                            marketListWithItems = marketListWithItems,
                            currencySymbol = currencySymbol,
                            isBangla = strings.isBangla,
                            onCreateListWithBulk = { title, items ->
                                onCreateMarketListWithBulk?.invoke(title, items)
                            },
                            onAddItem = { item ->
                                onAddMarketItem?.invoke(item)
                            },
                            onUpdateItem = { item ->
                                onUpdateMarketItem?.invoke(item)
                            },
                            onDeleteItem = { itemId ->
                                onDeleteMarketItem?.invoke(itemId)
                            },
                            onMarkPurchased = { item, amount ->
                                onMarkMarketItemPurchased?.invoke(item, amount)
                            },
                            onUnmarkPurchased = { item ->
                                onUnmarkMarketItemPurchased?.invoke(item)
                            },
                            onCompleteList = { listId ->
                                onCompleteMarketList?.invoke(listId)
                            },
                            lastPurchasedRates = lastPurchasedRates,
                            onCompleteListWithRollOver = onCompleteMarketListWithRollOver,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        if (existingTransaction != null && !state.isEditMode) {
                            TransactionViewModeContent(
                                transaction = existingTransaction,
                                selectedCategory = state.selectedCategory,
                                matchedAccount = accounts.find { it.id == state.selectedAccountId },
                                selectedPaymentMethod = state.selectedPaymentMethod,
                                dateMillis = state.dateMillis,
                                notes = state.notes,
                                receiptImagePath = state.receiptImagePath,
                                receiptBitmap = state.receiptBitmap,
                                typeAccentColor = typeAccentColor,
                                currencySymbol = currencySymbol,
                                strings = strings,
                                dateOnlyFormat = dateOnlyFormat,
                                dayOfWeekFormat = dayOfWeekFormat,
                                timeOnlyFormat = timeOnlyFormat,
                                onViewReceipt = {
                                    if (state.receiptBitmap != null) {
                                        state.showFullReceiptPreview = true
                                    } else {
                                        state.errorMessage = if (strings.isBangla) "রশিদ লোড করা সম্ভব হয়নি" else "Could not load receipt"
                                        haptics.warningFeedback()
                                    }
                                }
                            )
                        } else {
                            TransactionEditFormBody(
                                state = state,
                                typeAccentColor = typeAccentColor,
                                currencySymbol = currencySymbol,
                                dateOnlyFormat = dateOnlyFormat,
                                dayOfWeekFormat = dayOfWeekFormat,
                                timeOnlyFormat = timeOnlyFormat,
                                onLaunchPhotoPicker = onLaunchGallery,
                                onLaunchCamera = onLaunchCamera,
                                onLaunchGallery = onLaunchGallery,
                                onChangeReceipt = { showReceiptSourceChooser = true },
                                onOpenMarketList = if (existingTransaction == null) { { isMarketActive = true } } else null
                            )
                        }
                    }
                }
            }

            if (!isMarketActive) {
                TransactionBottomActionBar(
                    isExistingTransaction = existingTransaction != null,
                    isEditMode = state.isEditMode,
                    isExpense = state.isExpense,
                    typeAccentColor = typeAccentColor,
                    amountText = state.amountText,
                    currencySymbol = currencySymbol,
                    strings = strings,
                    isSaving = state.isSaving,
                    onEditClick = { state.isEditMode = true },
                    onCancelEditClick = {
                        if (state.isDirty) {
                            state.pendingDismissAfterDiscard = false
                            state.showDiscardDialog = true
                        } else state.isEditMode = false
                    },
                    onSaveClick = { state.saveCurrentTransaction() }
                )
            }
        }
    }

    TransactionSheetAllDialogs(
        showDeleteConfirmDialog = state.showDeleteConfirmDialog && existingTransaction != null,
        existingTransactionTitle = existingTransaction?.title ?: "",
        onDeleteConfirm = {
            existingTransaction?.let { tx ->
                if (!tx.receiptImagePath.isNullOrBlank()) {
                    ReceiptImageHelper.deleteReceiptImage(context, tx.receiptImagePath)
                }
                if (state.receiptImagePath != null && state.receiptImagePath != tx.receiptImagePath) {
                    ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
                }
                onDelete?.invoke(tx.id)
            }
        },
        onDismissDeleteConfirm = { state.showDeleteConfirmDialog = false },
        showDatePickerDialog = state.showDatePickerDialog,
        dateMillis = state.dateMillis,
        onDateSelected = { state.dateMillis = it },
        onDismissDatePicker = { state.showDatePickerDialog = false },
        showTimePickerDialog = state.showTimePickerDialog,
        onTimeSelected = { state.dateMillis = it },
        onDismissTimePicker = { state.showTimePickerDialog = false },
        showFullReceiptPreview = state.showFullReceiptPreview,
        receiptBitmap = state.receiptBitmap,
        onRemoveReceipt = {
            if (state.receiptImagePath != null && state.receiptImagePath != existingTransaction?.receiptImagePath) {
                ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
            }
            state.receiptImagePath = null
            state.receiptBitmap = null
            if (existingTransaction != null) {
                state.isEditMode = true
            }
        },
        onDismissReceiptPreview = { state.showFullReceiptPreview = false },
        showDiscardDialog = state.showDiscardDialog,
        onConfirmDiscard = {
            if (existingTransaction != null) {
                if (state.receiptImagePath != null && state.receiptImagePath != existingTransaction.receiptImagePath) {
                    ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
                }
                state.resetToOriginal()
                if (state.pendingDismissAfterDiscard) onDismiss() else state.isEditMode = false
            } else {
                if (state.receiptImagePath != null) {
                    ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
                }
                onDismiss()
            }
        },
        onDismissDiscard = { state.showDiscardDialog = false },
        typeAccentColor = typeAccentColor,
        strings = strings,
        showReceiptSourceChooser = showReceiptSourceChooser,
        onSelectCamera = onLaunchCamera,
        onSelectGallery = onLaunchGallery,
        onDismissReceiptSourceChooser = { showReceiptSourceChooser = false }
    )
}
