package com.example.expensetracker.ui.transactions

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.CategoryRecommendationEngine
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class TransactionSheetTab {
    EXPENSE,
    INCOME,
    TRANSFER
}

class AddEditTransactionState(
    val existingTransaction: Transaction?,
    val initialType: TransactionType,
    val initialDateMillis: Long?,
    val categories: List<Category>,
    val accounts: List<Account>,
    val strings: Strings,
    val haptics: AppHaptics,
    val context: Context,
    val scope: CoroutineScope,
    val onDismiss: () -> Unit,
    val onSave: (Transaction) -> Unit,
    val onTransfer: ((fromAccountId: Long, toAccountId: Long, amount: Double, note: String) -> Unit)? = null
) {
    var sheetTab by mutableStateOf(
        when (existingTransaction?.type ?: initialType) {
            TransactionType.INCOME -> TransactionSheetTab.INCOME
            TransactionType.EXPENSE -> TransactionSheetTab.EXPENSE
        }
    )

    var selectedType by mutableStateOf(existingTransaction?.type ?: initialType)
    var amountText by mutableStateOf(existingTransaction?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    var title by mutableStateOf(existingTransaction?.title ?: "")
    var notes by mutableStateOf(existingTransaction?.notes ?: "")

    var receiptImagePath by mutableStateOf(existingTransaction?.receiptImagePath)
    var receiptBitmap by mutableStateOf<Bitmap?>(null)
    var showFullReceiptPreview by mutableStateOf(false)

    var selectedAccountId by mutableStateOf(existingTransaction?.accountId ?: accounts.find { it.isDefault }?.id ?: accounts.firstOrNull()?.id)
    var toAccountId by mutableStateOf(accounts.getOrNull(1)?.id ?: accounts.firstOrNull()?.id)

    val availableCategories: List<Category> get() = categories.filter { it.type == selectedType }

    var selectedCategory by mutableStateOf(
        if (existingTransaction != null) {
            categories.find { it.id == existingTransaction.categoryId && it.type == selectedType }
                ?: categories.firstOrNull { it.type == selectedType }
        } else {
            CategoryRecommendationEngine.findSmartDefaultCategory(categories, selectedType)
        }
    )

    var selectedPaymentMethod by mutableStateOf(existingTransaction?.paymentMethod ?: PaymentMethod.CASH)
    var dateMillis by mutableStateOf(existingTransaction?.dateMillis ?: initialDateMillis ?: System.currentTimeMillis())

    var showDeleteConfirmDialog by mutableStateOf(false)
    var showDatePickerDialog by mutableStateOf(false)
    var showTimePickerDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    val amountFocusRequester = FocusRequester()

    var showCategoryDropdown by mutableStateOf(false)
    var showAccountDropdown by mutableStateOf(false)
    var isEditMode by mutableStateOf(existingTransaction == null)
    var showDiscardDialog by mutableStateOf(false)
    var pendingDismissAfterDiscard by mutableStateOf(false)

    var isSaving by mutableStateOf(false)

    val isExpense: Boolean get() = selectedType == TransactionType.EXPENSE && sheetTab != TransactionSheetTab.TRANSFER
    val isTransfer: Boolean get() = sheetTab == TransactionSheetTab.TRANSFER

    val isFormValid: Boolean get() = (amountText.toParsedAmountOrNull() ?: 0.0) > 0.0 &&
            (isTransfer || selectedCategory != null)

    val isDirty: Boolean
        get() {
            if (existingTransaction == null) {
                return amountText.isNotBlank() || title.isNotBlank() || notes.isNotBlank() || receiptImagePath != null
            }
            val initialAmt = existingTransaction.amount.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
            val initialCategory = categories.find { it.id == existingTransaction.categoryId && it.type == existingTransaction.type }
                ?: categories.firstOrNull { it.type == existingTransaction.type }
            val initialAccountId = existingTransaction.accountId ?: accounts.find { it.isDefault }?.id ?: accounts.firstOrNull()?.id
            val initialNotes = existingTransaction.notes
            return amountText != initialAmt ||
                    title != existingTransaction.title ||
                    notes != initialNotes ||
                    selectedType != existingTransaction.type ||
                    selectedCategory?.id != initialCategory?.id ||
                    selectedAccountId != initialAccountId ||
                    selectedPaymentMethod != existingTransaction.paymentMethod ||
                    dateMillis != existingTransaction.dateMillis ||
                    receiptImagePath != existingTransaction.receiptImagePath
        }

    fun resetToOriginal() {
        if (existingTransaction != null) {
            val amt = existingTransaction.amount
            amountText = if (amt % 1.0 == 0.0) amt.toLong().toString() else amt.toString()
            title = existingTransaction.title
            notes = existingTransaction.notes
            selectedType = existingTransaction.type
            sheetTab = if (existingTransaction.type == TransactionType.INCOME) TransactionSheetTab.INCOME else TransactionSheetTab.EXPENSE
            selectedCategory = categories.find { it.id == existingTransaction.categoryId }
                ?: categories.firstOrNull { it.type == existingTransaction.type }
            selectedAccountId = existingTransaction.accountId ?: accounts.firstOrNull()?.id
            selectedPaymentMethod = existingTransaction.paymentMethod
            dateMillis = existingTransaction.dateMillis
            receiptImagePath = existingTransaction.receiptImagePath
            errorMessage = null
        }
    }

    fun handleTabSelected(tab: TransactionSheetTab) {
        if (sheetTab != tab) {
            haptics.lightTick()
            sheetTab = tab
            when (tab) {
                TransactionSheetTab.EXPENSE -> handleTypeSelected(TransactionType.EXPENSE)
                TransactionSheetTab.INCOME -> handleTypeSelected(TransactionType.INCOME)
                TransactionSheetTab.TRANSFER -> {
                    if (selectedAccountId == toAccountId) {
                        val other = accounts.firstOrNull { it.id != selectedAccountId }?.id
                        if (other != null) toAccountId = other
                    }
                }
            }
        }
    }

    fun handleTypeSelected(type: TransactionType) {
        if (selectedType != type) {
            haptics.lightTick()
            selectedType = type
            sheetTab = if (type == TransactionType.INCOME) TransactionSheetTab.INCOME else TransactionSheetTab.EXPENSE
            selectedCategory = CategoryRecommendationEngine.findSmartDefaultCategory(categories, type)
                ?: categories.firstOrNull { it.type == type }
        }
    }

    fun handleAmountChange(newVal: String) {
        val filtered = newVal.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/*−— " }
        amountText = filtered
        if (errorMessage != null) errorMessage = null
    }

    fun attemptDismiss() {
        if ((existingTransaction != null && isEditMode && isDirty) || (existingTransaction == null && isDirty)) {
            pendingDismissAfterDiscard = true
            showDiscardDialog = true
        } else if (existingTransaction != null && isEditMode) {
            isEditMode = false
        } else {
            onDismiss()
        }
    }

    fun saveCurrentTransaction() {
        if (isSaving) return
        val evaluated = amountText.toParsedAmountOrNull()
        if (evaluated == null || evaluated <= 0.0) {
            errorMessage = strings.amountGreaterThanZero
            haptics.warningFeedback()
            return
        }

        if (sheetTab == TransactionSheetTab.TRANSFER) {
            val fromId = selectedAccountId ?: accounts.firstOrNull()?.id
            val toId = toAccountId ?: accounts.getOrNull(1)?.id
            if (fromId == null || toId == null || fromId == toId) {
                errorMessage = if (strings.isBangla) "উৎস ও গন্তব্য হিসেবে ভিন্ন অ্যাকাউন্ট নির্বাচন করুন" else "Select different source and destination accounts"
                haptics.warningFeedback()
                return
            }
            isSaving = true
            haptics.successFeedback()
            onTransfer?.invoke(fromId, toId, evaluated, notes.ifBlank { title })
            onDismiss()
            return
        }

        val cat = selectedCategory
        if (cat == null) {
            errorMessage = strings.selectCategoryPrompt
            haptics.warningFeedback()
            return
        }
        val effAccId = selectedAccountId ?: accounts.find { it.isDefault }?.id ?: accounts.firstOrNull()?.id
        val matchedAcc = accounts.find { it.id == effAccId }

        // Clean up replaced old receipt file from disk if existing transaction had one that changed
        if (existingTransaction != null &&
            !existingTransaction.receiptImagePath.isNullOrBlank() &&
            existingTransaction.receiptImagePath != receiptImagePath
        ) {
            ReceiptImageHelper.deleteReceiptImage(context, existingTransaction.receiptImagePath)
        }

        val tx = Transaction(
            id = existingTransaction?.id ?: 0L,
            title = title.ifBlank { cat.getLocalizedName(strings.isBangla) },
            amount = evaluated,
            type = selectedType,
            categoryId = cat.id,
            categoryName = cat.name,
            categoryIcon = cat.icon,
            categoryColor = cat.colorHex,
            dateMillis = dateMillis,
            paymentMethod = selectedPaymentMethod,
            accountId = effAccId,
            accountName = matchedAcc?.name ?: existingTransaction?.accountName ?: "Cash in Hand",
            notes = notes,
            receiptImagePath = receiptImagePath
        )
        isSaving = true
        haptics.successFeedback()
        onSave(tx)
    }
}
