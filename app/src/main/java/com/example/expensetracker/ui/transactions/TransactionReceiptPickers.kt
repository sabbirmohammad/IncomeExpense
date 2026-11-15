package com.example.expensetracker.ui.transactions

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

internal class TransactionReceiptPickers(
    val onLaunchCamera: () -> Unit,
    val onLaunchGallery: () -> Unit,
    val showSourceChooser: Boolean,
    val onOpenSourceChooser: () -> Unit,
    val onDismissSourceChooser: () -> Unit
)

@Composable
internal fun rememberTransactionReceiptPickers(
    context: Context,
    scope: CoroutineScope,
    state: AddEditTransactionState,
    existingTransaction: Transaction?,
    strings: Strings,
    haptics: AppHaptics
): TransactionReceiptPickers {
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

    return TransactionReceiptPickers(
        onLaunchCamera = onLaunchCamera,
        onLaunchGallery = onLaunchGallery,
        showSourceChooser = showReceiptSourceChooser,
        onOpenSourceChooser = { showReceiptSourceChooser = true },
        onDismissSourceChooser = { showReceiptSourceChooser = false }
    )
}
