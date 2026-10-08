package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import com.example.expensetracker.data.ai.GeminiAiService
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.TransactionType

data class ParsedMagicResult(
    val title: String?,
    val amount: Double?,
    val type: TransactionType,
    val category: Category?,
    val accountId: Long?,
    val notes: String?
)

data class ParsedReceiptScanResult(
    val amount: Double?,
    val title: String?,
    val category: Category?,
    val dateMillis: Long?,
    val notes: String?,
    val paymentMethod: PaymentMethod?
)

object TransactionAiScanHelper {
    suspend fun parseMagicFill(
        inputText: String,
        categories: List<Category>,
        accounts: List<Account>,
        isBangla: Boolean,
        apiKey: String?
    ): ParsedMagicResult? {
        val parsed = GeminiAiService.parseTransactionFromNaturalText(
            input = inputText,
            categories = categories,
            accounts = accounts,
            isBangla = isBangla,
            apiKey = apiKey
        )

        val matchedCat = parsed.categoryName?.let { catName ->
            categories.find {
                it.name.equals(catName, ignoreCase = true) ||
                        it.getLocalizedName(true).contains(catName, ignoreCase = true) ||
                        catName.contains(it.name, ignoreCase = true)
            }
        }
        val matchedAcc = parsed.accountName?.let { accName ->
            accounts.find {
                it.name.contains(accName, ignoreCase = true) || accName.contains(it.name, ignoreCase = true)
            }
        }
        return ParsedMagicResult(
            title = parsed.title?.takeIf { it.isNotBlank() },
            amount = parsed.amount,
            type = parsed.type,
            category = matchedCat,
            accountId = matchedAcc?.id,
            notes = parsed.notes
        )
    }

    suspend fun parseReceiptScan(
        apiKey: String?,
        bitmap: Bitmap,
        categories: List<Category>
    ): ParsedReceiptScanResult? {
        if (apiKey.isNullOrBlank()) return null
        val result = GeminiAiService.scanReceipt(
            apiKey = apiKey,
            bitmap = bitmap,
            categories = categories
        ) ?: return null

        val matchedCat = result.categoryName?.let { catName ->
            categories.find {
                it.name.equals(catName, ignoreCase = true) ||
                        it.getLocalizedName(true).contains(catName, ignoreCase = true) ||
                        catName.contains(it.name, ignoreCase = true)
            }
        }
        return ParsedReceiptScanResult(
            amount = result.amount,
            title = result.title,
            category = matchedCat,
            dateMillis = result.dateMillis,
            notes = result.notes,
            paymentMethod = result.paymentMethod
        )
    }
}
