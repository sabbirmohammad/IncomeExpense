package com.example.expensetracker.ui.transactions

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PulseSegmented
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.MarketNativeContent
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionSheet(
    sheetState: SheetState, existingTransaction: Transaction? = null, initialDateMillis: Long? = null, initialType: TransactionType = TransactionType.EXPENSE,
    initialOpenMarketTab: Boolean = false, categories: List<Category>, accounts: List<Account> = emptyList(), currencySymbol: String,
    onDismiss: () -> Unit, onSave: (Transaction) -> Unit, onDelete: ((Long) -> Unit)? = null, marketListWithItems: MarketListWithItems? = null,
    onCreateMarketListWithBulk: ((title: String, items: List<MarketItem>) -> Unit)? = null, onAddMarketItem: ((item: MarketItem) -> Unit)? = null,
    onUpdateMarketItem: ((item: MarketItem) -> Unit)? = null, onDeleteMarketItem: ((itemId: Long) -> Unit)? = null,
    onMarkMarketItemPurchased: ((item: MarketItem, amount: Double) -> Unit)? = null, onUnmarkMarketItemPurchased: ((item: MarketItem) -> Unit)? = null,
    onCompleteMarketList: ((listId: Long) -> Unit)? = null, onOpenMarketList: (() -> Unit)? = null,
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(), productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    onCompleteMarketListWithRollOver: ((listId: Long, rollOverItems: List<MarketItem>) -> Unit)? = null,
    onTransfer: ((fromAccountId: Long, toAccountId: Long, amount: Double, note: String) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state = remember(existingTransaction, initialType, initialDateMillis) {
        AddEditTransactionState(
            existingTransaction = existingTransaction, initialType = initialType, initialDateMillis = initialDateMillis,
            categories = categories, accounts = accounts, strings = strings, haptics = haptics,
            context = context, scope = scope, onDismiss = onDismiss, onSave = onSave, onTransfer = onTransfer
        )
    }

    LaunchedEffect(state.receiptImagePath) {
        state.receiptBitmap = state.receiptImagePath?.let { ReceiptImageHelper.loadReceiptBitmap(context, it) }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    val pickers = rememberTransactionReceiptPickers(
        context = context, scope = scope, state = state, existingTransaction = existingTransaction, strings = strings, haptics = haptics
    )

    LaunchedEffect(Unit) {
        if (existingTransaction == null) {
            delay(350)
            try { state.amountFocusRequester.requestFocus() } catch (_: Exception) {}
        }
    }

    var isMarketActive by rememberSaveable(existingTransaction, initialOpenMarketTab) {
        mutableStateOf(initialOpenMarketTab && existingTransaction == null)
    }

    LaunchedEffect(initialOpenMarketTab) {
        if (existingTransaction == null) isMarketActive = initialOpenMarketTab
    }

    var hasVisitedTransactionForm by rememberSaveable { mutableStateOf(!initialOpenMarketTab) }
    LaunchedEffect(isMarketActive) { if (!isMarketActive) hasVisitedTransactionForm = true }

    val typeAccentColor by animateColorAsState(
        targetValue = if (isMarketActive) (if (appColors.isDark) Color(0xFF00A8FF) else Color(0xFF0284C7))
        else if (state.isTransfer) (if (appColors.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7))
        else if (state.isExpense) appColors.expense
        else appColors.income,
        animationSpec = tween(300), label = "typeAccentColor"
    )

    val dateFormats = rememberTransactionDateFormats(strings.isBangla)

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
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f).widthIn(max = 640.dp).navigationBarsPadding().imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionSheetHeaderHost(
                existingTransaction = existingTransaction, state = state, typeAccentColor = typeAccentColor,
                strings = strings, isMarketActive = isMarketActive, onDelete = onDelete
            )

            // Pinned Segmented Control: [ Expense | Income | Transfer ] (remains stable and does not scroll or flicker)
            if (!isMarketActive && (existingTransaction == null || state.isEditMode)) {
                PulseSegmented(
                    items = listOf(strings.expense, strings.income, strings.transferTab),
                    selectedIndex = when (state.sheetTab) {
                        TransactionSheetTab.EXPENSE -> 0
                        TransactionSheetTab.INCOME -> 1
                        TransactionSheetTab.TRANSFER -> 2
                    },
                    onItemSelected = { idx ->
                        when (idx) {
                            0 -> state.handleTabSelected(TransactionSheetTab.EXPENSE)
                            1 -> state.handleTabSelected(TransactionSheetTab.INCOME)
                            2 -> state.handleTabSelected(TransactionSheetTab.TRANSFER)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            AnimatedContent(
                targetState = isMarketActive,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.99f, animationSpec = tween(220))).togetherWith(fadeOut(animationSpec = tween(160)))
                },
                label = "MarketTransactionTransition", modifier = Modifier.fillMaxWidth().weight(1f)
            ) { marketActive ->
                if (marketActive) {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 6.dp)) {
                        MarketNativeContent(
                            marketListWithItems = marketListWithItems, currencySymbol = currencySymbol, isBangla = strings.isBangla,
                            onCreateListWithBulk = { title, items -> onCreateMarketListWithBulk?.invoke(title, items) },
                            onAddItem = { item -> onAddMarketItem?.invoke(item) },
                            onUpdateItem = { item -> onUpdateMarketItem?.invoke(item) },
                            onDeleteItem = { itemId -> onDeleteMarketItem?.invoke(itemId) },
                            onMarkPurchased = { item, amount -> onMarkMarketItemPurchased?.invoke(item, amount) },
                            onUnmarkPurchased = { item -> onUnmarkMarketItemPurchased?.invoke(item) },
                            onCompleteList = { listId -> onCompleteMarketList?.invoke(listId) },
                            lastPurchasedRates = lastPurchasedRates, productPriceInsights = productPriceInsights,
                            onCompleteListWithRollOver = onCompleteMarketListWithRollOver,
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 10.dp)) {
                        if (existingTransaction != null && !state.isEditMode) {
                            TransactionViewModeWrapper(
                                transaction = existingTransaction, state = state, accounts = accounts,
                                typeAccentColor = typeAccentColor, currencySymbol = currencySymbol, strings = strings,
                                haptics = haptics, dateOnlyFormat = dateFormats.dateOnly, dayOfWeekFormat = dateFormats.dayOfWeek, timeOnlyFormat = dateFormats.timeOnly
                            )
                        } else {
                            TransactionEditFormBody(
                                state = state, typeAccentColor = typeAccentColor, currencySymbol = currencySymbol,
                                dateOnlyFormat = dateFormats.dateOnly, dayOfWeekFormat = dateFormats.dayOfWeek, timeOnlyFormat = dateFormats.timeOnly,
                                onLaunchPhotoPicker = pickers.onLaunchGallery, onLaunchCamera = pickers.onLaunchCamera, onLaunchGallery = pickers.onLaunchGallery,
                                onChangeReceipt = pickers.onOpenSourceChooser,
                                onOpenMarketList = if (existingTransaction == null) { { isMarketActive = true } } else null
                            )
                        }
                    }
                }
            }

            if (!isMarketActive) {
                TransactionBottomActionBar(
                    isExistingTransaction = existingTransaction != null, isEditMode = state.isEditMode, isExpense = state.isExpense,
                    typeAccentColor = typeAccentColor, amountText = state.amountText, currencySymbol = currencySymbol, strings = strings,
                    isSaving = state.isSaving, onEditClick = { state.isEditMode = true },
                    onCancelEditClick = {
                        if (state.isDirty) { state.pendingDismissAfterDiscard = false; state.showDiscardDialog = true }
                        else state.isEditMode = false
                    },
                    onSaveClick = { state.saveCurrentTransaction() }, isTransfer = state.isTransfer
                )
            }
        }
    }

    TransactionSheetDialogHost(
        state = state, existingTransaction = existingTransaction, context = context,
        typeAccentColor = typeAccentColor, strings = strings, pickers = pickers,
        onDelete = onDelete, onDismiss = onDismiss
    )
}
