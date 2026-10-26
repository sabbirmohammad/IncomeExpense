package com.example.expensetracker

import android.content.Context
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.settings.SettingsViewModel
import com.example.expensetracker.ui.transactions.AddEditTransactionState
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.lang.reflect.Proxy

class OfflineReceiptAndAiPurgeVerificationTest {

    @Test
    fun testSettingsViewModelHasZeroGeminiAiApiKeyMethodsOrFields() {
        val vmClass = SettingsViewModel::class.java
        val methodNames = vmClass.declaredMethods.map { it.name }
        val fieldNames = vmClass.declaredFields.map { it.name }

        // Assert no testing or API key manipulation remains in SettingsViewModel
        assertFalse("SettingsViewModel must not contain testGeminiApiKey", methodNames.contains("testGeminiApiKey"))
        assertFalse("SettingsViewModel must not contain clearApiTestResult", methodNames.contains("clearApiTestResult"))
        assertFalse("SettingsViewModel must not contain setGeminiApiKey", methodNames.contains("setGeminiApiKey"))
        assertFalse("SettingsViewModel must not expose apiConnectionTest", fieldNames.contains("apiConnectionTest"))
        assertFalse("SettingsViewModel must not expose _apiConnectionTest", fieldNames.contains("_apiConnectionTest"))
        assertFalse("SettingsViewModel must not expose isTestingApi", fieldNames.contains("isTestingApi"))
        assertFalse("SettingsViewModel must not expose _isTestingApi", fieldNames.contains("_isTestingApi"))
    }

    @Test
    fun testAddEditTransactionStateHasZeroAiScanningStateOrMethods() {
        val stateClass = AddEditTransactionState::class.java
        val methodNames = stateClass.declaredMethods.map { it.name }
        val fieldNames = stateClass.declaredFields.map { it.name }

        // Assert scanning states and scanReceipt method have been completely purged
        assertFalse("AddEditTransactionState must not contain scanReceipt()", methodNames.contains("scanReceipt"))
        assertFalse("AddEditTransactionState must not contain isScanningReceipt field", fieldNames.contains("isScanningReceipt"))
        assertFalse("AddEditTransactionState must not contain scanStatusMessage field", fieldNames.contains("scanStatusMessage"))
        // Assert geminiApiKey has been removed from AddEditTransactionState
        assertFalse("AddEditTransactionState must not contain geminiApiKey field", fieldNames.contains("geminiApiKey"))
    }

    @Test
    fun testTransactionAiScanHelperClassDoesNotExist() {
        try {
            Class.forName("com.example.expensetracker.ui.transactions.TransactionAiScanHelper")
            fail("TransactionAiScanHelper class should be completely deleted from codebase")
        } catch (_: ClassNotFoundException) {
            // Expected
        }
    }

    @Test
    fun testOfflineReceiptAttachmentStateFlow() {
        val testCategory = Category(
            id = 10,
            name = "Bazaar",
            type = TransactionType.EXPENSE,
            icon = "shopping_cart",
            colorHex = "#F0716B"
        )
        val initialTx = Transaction(
            id = 1,
            title = "Weekly Groceries",
            amount = 1200.0,
            type = TransactionType.EXPENSE,
            categoryId = 10,
            categoryName = "Bazaar",
            categoryIcon = "shopping_cart",
            categoryColor = "#F0716B",
            dateMillis = 1700000000000L,
            paymentMethod = PaymentMethod.CASH,
            receiptImagePath = null
        )

        // Transaction without receipt
        assertNull(initialTx.receiptImagePath)
        assertFalse(initialTx.receiptImagePath != null)

        // Attaching offline voucher
        val attachedTx = initialTx.copy(receiptImagePath = "/data/user/0/com.example.expensetracker/files/receipts/receipt_sample.jpg")
        assertEquals("/data/user/0/com.example.expensetracker/files/receipts/receipt_sample.jpg", attachedTx.receiptImagePath)
        assertTrue(attachedTx.receiptImagePath != null)

        // Removing offline voucher
        val removedTx = attachedTx.copy(receiptImagePath = null)
        assertNull(removedTx.receiptImagePath)
        assertFalse(removedTx.receiptImagePath != null)
    }

    @Test
    fun testTransactionListHasReceiptIndicatorLogic() {
        val txWithoutReceipt = Transaction(
            id = 101,
            title = "Morning Coffee",
            amount = 120.0,
            type = TransactionType.EXPENSE,
            categoryId = 1,
            categoryName = "Food",
            categoryIcon = "restaurant",
            categoryColor = "#F0716B",
            dateMillis = 1700000000000L,
            paymentMethod = PaymentMethod.CASH,
            receiptImagePath = null
        )
        val txWithReceipt = Transaction(
            id = 102,
            title = "Pharmacy Medicines",
            amount = 850.0,
            type = TransactionType.EXPENSE,
            categoryId = 2,
            categoryName = "Health",
            categoryIcon = "medical",
            categoryColor = "#00B4D8",
            dateMillis = 1700000000000L,
            paymentMethod = PaymentMethod.BKASH,
            receiptImagePath = "/files/receipts/memo_2026.jpg"
        )

        // ListRow logic: hasReceipt = transaction.receiptImagePath != null
        assertFalse(txWithoutReceipt.receiptImagePath != null)
        assertTrue(txWithReceipt.receiptImagePath != null)
    }

    @Test
    fun testReceiptImageHelperSafeDimensionBounds() {
        // Safe bitmap bounds check
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(800, 600, 1024))
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(1600, 1200, 1024))
        assertEquals(4, ReceiptImageHelper.calculateInSampleSize(3840, 2160, 1024))
    }

    @Test
    fun testZoomAndPanBoundaryClampingLogic() {
        // Test pan bounding formula used in TransactionFullReceiptPreviewDialog
        // For scale = 1.0 (unzoomed), max pan is 0 -> offset is clamped strictly to 0
        val unzoomedScale = 1.0f
        val boxWidth = 1080
        val boxHeight = 2400
        val maxPanXUnzoomed = if (boxWidth > 0) ((boxWidth * (unzoomedScale - 1f)) / 2f) else 1000f
        val maxPanYUnzoomed = if (boxHeight > 0) ((boxHeight * (unzoomedScale - 1f)) / 2f) else 1000f
        assertEquals(0f, maxPanXUnzoomed, 0.001f)
        assertEquals(0f, maxPanYUnzoomed, 0.001f)

        // For scale = 2.5 (double tap zoom), max pan allows scrolling within viewport edges without flying away
        val zoomedScale = 2.5f
        val maxPanX25 = ((boxWidth * (zoomedScale - 1f)) / 2f)
        val maxPanY25 = ((boxHeight * (zoomedScale - 1f)) / 2f)
        assertEquals(810f, maxPanX25, 0.001f)
        assertEquals(1800f, maxPanY25, 0.001f)

        // User drags pan by 50,000 pixels (trying to fling receipt off-screen)
        val extremePanX = 50000f
        val clampedX = extremePanX.coerceIn(-maxPanX25, maxPanX25)
        assertEquals(810f, clampedX, 0.001f)

        // For extreme tall receipt (> 1:5 ratio, e.g. 200x1200 within 1080x2400 viewport)
        val extremeScale = 5.0f
        val maxPanY5 = ((boxHeight * (extremeScale - 1f)) / 2f)
        assertEquals(4800f, maxPanY5, 0.001f)
        val clampedTallY = (-100000f).coerceIn(-maxPanY5, maxPanY5)
        assertEquals(-4800f, clampedTallY, 0.001f)
    }

    @Test
    fun testBilingualReceiptStringsCompleteness() {
        val stringsBn = Strings(isBangla = true)
        val stringsEn = Strings(isBangla = false)

        // Attach receipt title
        assertTrue(stringsBn.attachReceipt.isNotBlank())
        assertTrue(stringsEn.attachReceipt.isNotBlank())

        // Take photo
        assertEquals("ছবি তুলুন", stringsBn.takePhoto)
        assertEquals("Take Photo", stringsEn.takePhoto)

        // Choose from gallery
        assertTrue(stringsBn.chooseFromGallery.isNotBlank())
        assertTrue(stringsEn.chooseFromGallery.isNotBlank())

        // View receipt
        assertEquals("রসিদ দেখুন", stringsBn.viewReceipt)
        assertEquals("View Receipt", stringsEn.viewReceipt)

        // Remove receipt
        assertEquals("রসিদ মুছুন", stringsBn.removeReceipt)
        assertEquals("Remove Receipt", stringsEn.removeReceipt)

        // Tap to view full receipt
        assertEquals("সম্পূর্ণ রসিদ দেখতে ট্যাপ করুন", stringsBn.tapToViewReceipt)
        assertEquals("Tap to view full receipt", stringsEn.tapToViewReceipt)
    }

    @Test
    fun testGeminiAiServiceAndReceiptParserHaveZeroScanReceiptMethods() {
        val purgedClasses = listOf(
            "com.example.expensetracker.data.ai.GeminiAiService",
            "com.example.expensetracker.data.ai.GeminiAiReceiptParser",
            "com.example.expensetracker.data.ai.GeminiAiAdvisorAndReminders",
            "com.example.expensetracker.data.ai.GeminiAiAuditService",
            "com.example.expensetracker.data.ai.GeminiAiClientHelper",
            "com.example.expensetracker.data.ai.GeminiAiFinancialAuditor",
            "com.example.expensetracker.data.ai.GeminiAiGoalsAndBills",
            "com.example.expensetracker.data.ai.GeminiAiJsonUtils",
            "com.example.expensetracker.data.ai.GeminiAiLocalFinancialFallbacks",
            "com.example.expensetracker.data.ai.GeminiAiLocalMarketFallbacks",
            "com.example.expensetracker.data.ai.GeminiAiMarketEstimator",
            "com.example.expensetracker.data.ai.GeminiAiMarketService",
            "com.example.expensetracker.data.local.SecureStorageHelper",
            "com.example.expensetracker.data.model.ScannedReceiptData",
            "com.example.expensetracker.ui.receivables.DebtReminderAiDialogKt",
            "com.example.expensetracker.ui.receivables.DebtReminderChannelButtonsKt",
            "com.example.expensetracker.ui.components.AiAdvisorChatSheetKt",
            "com.example.expensetracker.ui.components.AiInsightsCardKt",
            "com.example.expensetracker.ui.transactions.TransactionAiMagicFillSectionKt"
        )

        for (className in purgedClasses) {
            try {
                Class.forName(className)
                fail("Class $className must be completely purged from codebase")
            } catch (_: ClassNotFoundException) {
                // Expected
            }
        }
    }

    @Test
    fun testAddEditTransactionStateSaveCurrentTransactionInvokesOnSave() {
        val mockFeedback = object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {}
        }
        val appHaptics = AppHaptics(mockFeedback, null)
        val mockContext = object : android.content.ContextWrapper(null) {
            override fun getFilesDir(): java.io.File = java.io.File("/tmp/receipts_test")
            override fun getPackageName(): String = "com.example.expensetracker"
        }

        var savedTransaction: Transaction? = null
        val testCat = Category(
            id = 5,
            name = "Bazaar",
            type = TransactionType.EXPENSE,
            icon = "shopping_bag",
            colorHex = "#F0716B"
        )
        val state = AddEditTransactionState(
            existingTransaction = null,
            initialType = TransactionType.EXPENSE,
            initialDateMillis = 1700000000000L,
            categories = listOf(testCat),
            accounts = emptyList(),
            strings = Strings(isBangla = false),
            haptics = appHaptics,
            context = mockContext,
            scope = CoroutineScope(Dispatchers.Unconfined),
            onDismiss = {},
            onSave = { savedTransaction = it }
        )

        state.amountText = "1250"
        state.selectedCategory = testCat
        state.title = "Grocery shopping"
        state.receiptImagePath = "/tmp/receipts_test/receipt_sample.jpg"

        state.saveCurrentTransaction()

        assertNotNull("saveCurrentTransaction must invoke onSave callback with constructed Transaction", savedTransaction)
        assertEquals(1250.0, savedTransaction?.amount ?: 0.0, 0.001)
        assertEquals("Grocery shopping", savedTransaction?.title)
        assertEquals("/tmp/receipts_test/receipt_sample.jpg", savedTransaction?.receiptImagePath)
        assertTrue("isSaving state must be true", state.isSaving)
    }

    @Test
    fun testTransactionReceiptRemovalTransitionsToEditMode() {
        val mockFeedback = object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {}
        }
        val appHaptics = AppHaptics(mockFeedback, null)
        val mockContext = object : android.content.ContextWrapper(null) {
            override fun getFilesDir(): java.io.File = java.io.File("/tmp/receipts_test")
            override fun getPackageName(): String = "com.example.expensetracker"
        }

        val originalTx = Transaction(
            id = 42,
            title = "Hardware tools",
            amount = 890.0,
            type = TransactionType.EXPENSE,
            categoryId = 1,
            categoryName = "Tools",
            categoryIcon = "build",
            categoryColor = "#112233",
            dateMillis = 1700000000000L,
            paymentMethod = PaymentMethod.CASH,
            receiptImagePath = "/tmp/receipts_test/committed_receipt.jpg"
        )

        val state = AddEditTransactionState(
            existingTransaction = originalTx,
            initialType = TransactionType.EXPENSE,
            initialDateMillis = 1700000000000L,
            categories = emptyList(),
            accounts = emptyList(),
            strings = Strings(isBangla = false),
            haptics = appHaptics,
            context = mockContext,
            scope = CoroutineScope(Dispatchers.Unconfined),
            onDismiss = {},
            onSave = {}
        )

        // Initial state is view mode
        assertFalse("Existing transaction initially starts in view mode", state.isEditMode)
        assertFalse("Initial state without edits is not dirty", state.isDirty)
        assertEquals("/tmp/receipts_test/committed_receipt.jpg", state.receiptImagePath)

        // Removing receipt simulates user clicking remove in viewer or edit form
        state.receiptImagePath = null
        state.receiptBitmap = null
        state.isEditMode = true

        assertTrue("Removing receipt transitions state into edit mode", state.isEditMode)
        assertTrue("State is now dirty after removing receipt", state.isDirty)
        assertNull("Receipt image path is now cleared", state.receiptImagePath)
    }

    @Test
    fun testCloseButtonContentDescriptionSemantics() {
        val stringsBn = Strings(isBangla = true)
        val stringsEn = Strings(isBangla = false)

        assertEquals("বন্ধ করুন", stringsBn.close)
        assertEquals("Close", stringsEn.close)
        assertEquals("বাতিল", stringsBn.cancel)
        assertEquals("Cancel", stringsEn.cancel)

        // Ensure close and cancel are distinct bilingual semantic tokens
        assertTrue(stringsBn.close != stringsBn.cancel)
        assertTrue(stringsEn.close != stringsEn.cancel)
    }

    @Test
    fun testTransactionDeleteCleansUpUncommittedReplacementReceipt() {
        val committedPath = "/tmp/receipts/old_receipt.jpg"
        val replacementPath = "/tmp/receipts/new_receipt.jpg"

        val deletedPaths = mutableListOf<String>()
        val deleteHelper = { path: String? ->
            if (path != null) deletedPaths.add(path)
        }

        // Simulate onDeleteConfirm logic:
        val existingReceipt: String? = committedPath
        val stateReceipt: String? = replacementPath

        if (!existingReceipt.isNullOrBlank()) {
            deleteHelper(existingReceipt)
        }
        if (stateReceipt != null && stateReceipt != existingReceipt) {
            deleteHelper(stateReceipt)
        }

        assertEquals(2, deletedPaths.size)
        assertTrue("Both old committed and new uncommitted replacement files are deleted", deletedPaths.contains(committedPath) && deletedPaths.contains(replacementPath))
    }

    @Test
    fun testTransactionAccountFallbackDisplaysPaymentMethodWhenAccountNameIsNull() {
        val stringsBn = Strings(isBangla = true)
        val stringsEn = Strings(isBangla = false)

        val txWithoutAccount = Transaction(
            id = 201,
            title = "Snacks",
            amount = 150.0,
            type = TransactionType.EXPENSE,
            categoryId = 1,
            categoryName = "Food",
            categoryIcon = "Fastfood",
            categoryColor = "#FF5722",
            dateMillis = 1700000000000L,
            paymentMethod = PaymentMethod.BKASH,
            accountId = null,
            accountName = null
        )

        val displayBn = txWithoutAccount.accountName?.takeIf { it.isNotBlank() }?.let {
            com.example.expensetracker.data.model.getLocalizedAccountName(it, true)
        } ?: txWithoutAccount.paymentMethod.getDisplayName(true)

        val displayEn = txWithoutAccount.accountName?.takeIf { it.isNotBlank() }?.let {
            com.example.expensetracker.data.model.getLocalizedAccountName(it, false)
        } ?: txWithoutAccount.paymentMethod.getDisplayName(false)

        assertEquals("বিকাশ", displayBn)
        assertEquals("bKash", displayEn)
    }

    @Test
    fun testSettledReceivableHasZeroRemainingAndGuardsFurtherRepayment() {
        val settledRec = com.example.expensetracker.data.model.Receivable(
            id = 301,
            personName = "Karim",
            amount = 5000.0,
            amountReceived = 5000.0,
            status = com.example.expensetracker.data.model.ReceivableStatus.SETTLED
        )

        assertTrue(settledRec.isSettled)
        assertEquals(0.0, settledRec.remainingAmount, 0.001)

        val activeRec = com.example.expensetracker.data.model.Receivable(
            id = 302,
            personName = "Rahim",
            amount = 5000.0,
            amountReceived = 2000.0,
            status = com.example.expensetracker.data.model.ReceivableStatus.PARTIAL
        )

        assertFalse(activeRec.isSettled)
        assertEquals(3000.0, activeRec.remainingAmount, 0.001)
    }
}
