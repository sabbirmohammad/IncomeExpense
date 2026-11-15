package com.example.expensetracker.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ReceiptCameraTempHelper {

    /**
     * Creates a temporary file in cacheDir/shared_images and its corresponding FileProvider content Uri,
     * returning both the File and Uri for easy cleanup.
     */
    fun createCameraTempFileAndUri(context: Context): Pair<File, Uri>? {
        return try {
            val cacheImagesDir = File(context.cacheDir, "shared_images").apply {
                if (!exists()) mkdirs()
            }
            val tempFile = File(cacheImagesDir, "camera_receipt_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            Pair(tempFile, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Safely deletes a file if it exists, ignoring errors.
     */
    fun deleteFileQuietly(file: File?) {
        try {
            file?.takeIf { it.exists() }?.delete()
        } catch (_: Exception) {}
    }
}
