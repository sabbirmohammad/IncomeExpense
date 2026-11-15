package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import java.text.SimpleDateFormat

@Composable
fun TransactionViewModeContent(
    transaction: Transaction,
    selectedCategory: Category?,
    matchedAccount: Account?,
    selectedPaymentMethod: PaymentMethod,
    dateMillis: Long,
    notes: String,
    receiptImagePath: String?,
    receiptBitmap: Bitmap?,
    typeAccentColor: Color,
    currencySymbol: String,
    strings: Strings,
    dateOnlyFormat: SimpleDateFormat,
    dayOfWeekFormat: SimpleDateFormat,
    timeOnlyFormat: SimpleDateFormat,
    onViewReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Amount Card
        TransactionHeroAmountCard(
            transaction = transaction,
            selectedCategory = selectedCategory,
            matchedAccount = matchedAccount,
            typeAccentColor = typeAccentColor,
            currencySymbol = currencySymbol,
            strings = strings
        )

        // Transaction Details Card
        TransactionDetailsCard(
            transaction = transaction,
            selectedCategory = selectedCategory,
            matchedAccount = matchedAccount,
            selectedPaymentMethod = selectedPaymentMethod,
            dateMillis = dateMillis,
            strings = strings,
            dateOnlyFormat = dateOnlyFormat,
            dayOfWeekFormat = dayOfWeekFormat
        )

        // Notes Card (if present)
        if (notes.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = appColors.surfaceVariant),
                border = BorderStroke(1.dp, appColors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = strings.note,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp
                        ),
                        color = appColors.textPrimary
                    )
                }
            }
        }

        // Receipt Card (if attached)
        if (receiptImagePath != null) {
            TransactionReceiptCard(
                receiptBitmap = receiptBitmap,
                strings = strings,
                onViewReceipt = onViewReceipt
            )
        }
    }
}
