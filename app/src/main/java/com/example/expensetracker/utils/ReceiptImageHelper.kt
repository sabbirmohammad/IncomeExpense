package com.example.expensetracker.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ReceiptImageHelper {

    private const val MAX_DIMENSION = 1600
    private const val COMPRESS_QUALITY = 85

    /**
     * Copies and optimizes a picked or captured image to private internal storage.
     * Returns the absolute path of the saved receipt file, or null on failure.
     */
    suspend fun saveReceiptImage(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val receiptsDir = File(context.filesDir, "receipts").apply {
                if (!exists()) mkdirs()
            }

            val fileName = "receipt_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val targetFile = File(receiptsDir, fileName)

            var inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return@withContext null

            // Read Exif orientation if possible
            val rotation = try {
                val exifStream = context.contentResolver.openInputStream(sourceUri)
                if (exifStream != null) {
                    val exif = ExifInterface(exifStream)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    exifStream.close()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }
                } else 0f
            } catch (_: Exception) {
                0f
            }

            // Downscale if larger than MAX_DIMENSION to preserve RAM and storage
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
                MAX_DIMENSION.toFloat() / maxOf(width, height)
            } else {
                1f
            }

            val matrix = Matrix().apply {
                if (scale < 1f) postScale(scale, scale)
                if (rotation != 0f) postRotate(rotation)
            }

            val finalBitmap = if (scale < 1f || rotation != 0f) {
                Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true)
            } else {
                originalBitmap
            }

            FileOutputStream(targetFile).use { fos ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fos)
                fos.flush()
            }

            if (finalBitmap != originalBitmap) {
                finalBitmap.recycle()
            }
            originalBitmap.recycle()

            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Resolves a File from a saved receipt path string.
     */
    fun getReceiptFile(context: Context, path: String?): File? {
        if (path.isNullOrBlank()) return null
        val file = File(path)
        if (file.exists()) return file
        val relativeFile = File(context.filesDir, path)
        if (relativeFile.exists()) return relativeFile
        return null
    }

    /**
     * Loads a Bitmap from a saved receipt path with memory safety.
     */
    suspend fun loadReceiptBitmap(context: Context, path: String?): Bitmap? = withContext(Dispatchers.IO) {
        val file = getReceiptFile(context, path) ?: return@withContext null
        try {
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes receipt image from internal storage.
     */
    fun deleteReceiptImage(context: Context, path: String?): Boolean {
        val file = getReceiptFile(context, path) ?: return false
        return try {
            file.delete()
        } catch (_: Exception) {
            false
        }
    }
}
