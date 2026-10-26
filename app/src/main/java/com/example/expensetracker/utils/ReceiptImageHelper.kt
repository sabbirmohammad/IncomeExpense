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

    const val MAX_DIMENSION = 1024
    private const val COMPRESS_QUALITY = 85

    /**
     * Calculates the largest power-of-2 inSampleSize value to ensure the decoded
     * image does not exceed maxDimension on either axis, preventing OutOfMemoryError.
     */
    fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int = MAX_DIMENSION): Int {
        var inSampleSize = 1
        val largest = maxOf(width, height)
        while ((largest / inSampleSize) > maxDimension) {
            inSampleSize *= 2
        }
        return inSampleSize.coerceAtLeast(1)
    }

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

            // Pass 1: Decode image dimensions only without allocating bitmap pixels in heap
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(sourceUri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }
            if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) {
                return@withContext null
            }

            val sampleSize = calculateInSampleSize(boundsOptions.outWidth, boundsOptions.outHeight, MAX_DIMENSION)

            // Pass 2: Decode memory-efficient subsampled bitmap
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val sampledBitmap = context.contentResolver.openInputStream(sourceUri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return@withContext null

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

            // Downscale to exact MAX_DIMENSION (1024px) if post-subsample size still exceeds bound
            val width = sampledBitmap.width
            val height = sampledBitmap.height
            val largestDim = maxOf(width, height)
            val scale = if (largestDim > MAX_DIMENSION) {
                MAX_DIMENSION.toFloat() / largestDim
            } else {
                1f
            }

            val matrix = Matrix().apply {
                if (scale < 1f) postScale(scale, scale)
                if (rotation != 0f) postRotate(rotation)
            }

            val finalBitmap = if (scale < 1f || rotation != 0f) {
                Bitmap.createBitmap(sampledBitmap, 0, 0, width, height, matrix, true)
            } else {
                sampledBitmap
            }

            FileOutputStream(targetFile).use { fos ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fos)
                fos.flush()
            }

            if (finalBitmap != sampledBitmap) {
                finalBitmap.recycle()
            }
            sampledBitmap.recycle()

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
    suspend fun loadReceiptBitmap(context: Context, path: String?, maxDimension: Int = MAX_DIMENSION): Bitmap? = withContext(Dispatchers.IO) {
        val file = getReceiptFile(context, path) ?: return@withContext null
        try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null

            val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxDimension)
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
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

    /**
     * Creates a temporary file in cacheDir/shared_images and its corresponding FileProvider content Uri
     * for direct camera capture via ActivityResultContracts.TakePicture().
     */
    fun createCameraTempUri(context: Context): Uri? {
        return createCameraTempFileAndUri(context)?.second
    }

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
            val uri = androidx.core.content.FileProvider.getUriForFile(
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
