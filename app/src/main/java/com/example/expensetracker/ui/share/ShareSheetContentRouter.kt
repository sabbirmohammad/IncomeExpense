package com.example.expensetracker.ui.share

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.Strings
import kotlinx.coroutines.CoroutineScope

@Composable
fun ShareSheetContentRouter(
    selectedFormat: ShareFormatType,
    periodTitle: String,
    periodSubtitle: String,
    totalIncome: Double,
    totalExpense: Double,
    transactions: List<Transaction>,
    userProfile: UserProfile,
    strings: Strings,
    context: Context,
    graphicsLayer: GraphicsLayer,
    coroutineScope: CoroutineScope,
    isSharing: Boolean,
    isSharingPdf: Boolean,
    isLoading: Boolean,
    onSharingChange: (Boolean) -> Unit,
    onSharingPdfChange: (Boolean) -> Unit
) {
    when (selectedFormat) {
        ShareFormatType.PDF -> SharePdfSection(
            periodTitle = periodTitle,
            periodSubtitle = periodSubtitle,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            transactions = transactions,
            userProfile = userProfile,
            strings = strings,
            context = context,
            coroutineScope = coroutineScope,
            isSharing = isSharing,
            isSharingPdf = isSharingPdf,
            isLoading = isLoading,
            onSharingPdfChange = onSharingPdfChange
        )
        ShareFormatType.IMAGE -> ShareImageSection(
            periodTitle = periodTitle,
            periodSubtitle = periodSubtitle,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            transactions = transactions,
            userProfile = userProfile,
            strings = strings,
            context = context,
            graphicsLayer = graphicsLayer,
            coroutineScope = coroutineScope,
            isSharing = isSharing,
            isSharingPdf = isSharingPdf,
            isLoading = isLoading,
            onSharingChange = onSharingChange
        )
        ShareFormatType.TEXT -> ShareTextSection(
            periodTitle = periodTitle,
            periodSubtitle = periodSubtitle,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            transactions = transactions,
            userProfile = userProfile,
            strings = strings,
            context = context
        )
    }
}
