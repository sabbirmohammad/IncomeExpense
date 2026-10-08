package com.example.expensetracker.ui.transactions

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddEditTransactionState(
    val existingTransaction: Transaction?,
    val initialType: TransactionType,
    val initialDateMillis: Long?,
    val categories: List<Category>,
    val accounts: List<Account>,
    val geminiApiKey: String?,
    val strings: Strings,
    val haptics: AppHaptics,
    val context: Context,
    val scope: CoroutineScope,
    val onDismiss: () -> Unit,
    val onSave: (Transaction) -> Unit
) {
    var selectedType by mutableStateOf(existingTransaction?.type ?: initialType)
    var amountText by mutableStateOf(existingTransaction?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    var title by mutableStateOf(existingTransaction?.title ?: "")
    var notes by mutableStateOf(existingTransaction?.notes ?: "")
    var showCalculatorBar by mutableStateOf(false)

    var receiptImagePath by mutableStateOf(existingTransaction?.receiptImagePath)
    var receiptBitmap by mutableStateOf<Bitmap?>(null)
    var isScanningReceipt by mutableStateOf(false)
    var scanStatusMessage by mutableStateOf<String?>(null)
    var showFullReceiptPreview by mutableStateOf(false)

    var showAiMagicFill by mutableStateOf(false)
    var aiMagicInputText by mutableStateOf("")
    var isAiMagicParsing by mutableStateOf(false)

    var selectedAccountId by mutableStateOf(existingTransaction?.accountId ?: accounts.find { it.isDefault }?.id ?: accounts.firstOrNull()?.id)
    val availableCategories: List<Category> get() = categories.filter { it.type == selectedType }
    var selectedCategory by mutableStateOf(
        categories.find { it.id == existingTransaction?.categoryId && it.type == selectedType } ?: categories.firstOrNull { it.type == selectedType }
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

    val isExpense: Boolean get() = selectedType == TransactionType.EXPENSE

    val isDirty: Boolean get() {
        return if (existingTransaction != null) {
            val orig = existingTransaction.amount.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
            amountText.trim() != orig || title.trim() != existingTransaction.title.trim() ||
                    notes.trim() != existingTransaction.notes.trim() || selectedCategory?.id != existingTransaction.categoryId ||
                    receiptImagePath != existingTransaction.receiptImagePath || selectedType != existingTransaction.type ||
                    selectedAccountId != existingTransaction.accountId || selectedPaymentMethod != existingTransaction.paymentMethod ||
                    dateMillis != existingTransaction.dateMillis
        } else {
            amountText.isNotBlank() || title.isNotBlank() || notes.isNotBlank() || receiptImagePath != null
        }
    }

    fun resetToOriginal() {
        if (existingTransaction != null) {
            selectedType = existingTransaction.type
            amountText = existingTransaction.amount.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
            title = existingTransaction.title
            notes = existingTransaction.notes
            receiptImagePath = existingTransaction.receiptImagePath
            selectedAccountId = existingTransaction.accountId ?: accounts.find { it.isDefault }?.id ?: accounts.firstOrNull()?.id
            selectedCategory = categories.find { it.id == existingTransaction.categoryId && it.type == existingTransaction.type }
                ?: categories.firstOrNull { it.type == existingTransaction.type }
            selectedPaymentMethod = existingTransaction.paymentMethod
            dateMillis = existingTransaction.dateMillis
            errorMessage = null
            showCalculatorBar = false
        }
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
        val evaluated = amountText.toParsedAmountOrNull()
        if (evaluated == null || evaluated <= 0.0) {
            errorMessage = strings.amountGreaterThanZero
            haptics.warningFeedback()
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
        haptics.successFeedback()
        onSave(tx)
    }

    fun parseAiMagic() {
        if (aiMagicInputText.isBlank()) return
        haptics.mediumClick()
        scope.launch {
            isAiMagicParsing = true
            val res = TransactionAiScanHelper.parseMagicFill(aiMagicInputText, categories, accounts, strings.isBangla, geminiApiKey)
            isAiMagicParsing = false
            if (res != null) {
                haptics.successFeedback()
                res.title?.let { title = it }
                res.amount?.let { amt -> amountText = if (amt % 1.0 == 0.0) amt.toLong().toString() else amt.toString() }
                selectedType = res.type
                res.category?.let { selectedCategory = it }
                res.accountId?.let { selectedAccountId = it }
                res.notes?.let { n -> notes = if (notes.isBlank()) n else "$notes • $n" }
                aiMagicInputText = ""
                showAiMagicFill = false
            }
        }
    }

    fun scanReceipt() {
        val bmp = receiptBitmap ?: return
        if (geminiApiKey.isNullOrBlank()) {
            scanStatusMessage = if (strings.isBangla) "সেটিংস থেকে Gemini API Key দিন" else "Please set Gemini API Key in Settings"
            haptics.warningFeedback()
            return
        }
        isScanningReceipt = true
        scanStatusMessage = strings.scanningReceipt
        haptics.mediumClick()
        scope.launch {
            val scanRes = TransactionAiScanHelper.parseReceiptScan(geminiApiKey, bmp, categories)
            isScanningReceipt = false
            if (scanRes != null) {
                haptics.successFeedback()
                scanStatusMessage = strings.receiptScanSuccess
                scanRes.amount?.let { amt -> amountText = if (amt % 1.0 == 0.0) amt.toLong().toString() else amt.toString() }
                scanRes.title?.let { title = it }
                scanRes.category?.let { selectedCategory = it; selectedType = it.type }
                scanRes.dateMillis?.let { dateMillis = it }
                scanRes.notes?.let { n -> notes = if (notes.isBlank()) n else "$notes • $n" }
                scanRes.paymentMethod?.let { selectedPaymentMethod = it }
            } else {
                haptics.warningFeedback()
                scanStatusMessage = strings.receiptScanFailed
            }
        }
    }
}
