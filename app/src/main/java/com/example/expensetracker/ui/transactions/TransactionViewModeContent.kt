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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import java.text.SimpleDateFormat
import java.util.Date

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
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surfaceVariant),
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconBadge(
                        iconName = selectedCategory?.icon ?: transaction.categoryIcon,
                        colorHex = selectedCategory?.colorHex ?: transaction.categoryColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = transaction.title.ifBlank { selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = ColorTextSecondary
                        )
                    }
                }

                HorizontalDivider(color = LocalAppColors.current.border.copy(alpha = 0.7f), thickness = 0.8.dp)

                val rawDate = dateOnlyFormat.format(Date(dateMillis))
                val dateDisp = if (strings.isBangla) convertToBengaliDigits(rawDate) else rawDate
                val dayDisp = dayOfWeekFormat.format(Date(dateMillis))
                val timeDisp = DateTimeHelper.formatTimeOnly(dateMillis, strings.isBangla, globalBengaliNumeralsEnabled || strings.isBangla)
                TransactionDetailInfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = strings.dateTimeLabel,
                    value = "$dateDisp ($dayDisp) • $timeDisp"
                )

                TransactionDetailInfoRow(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    label = strings.account,
                    value = getLocalizedAccountName(matchedAccount?.name ?: transaction.accountName ?: "Cash in Hand", strings.isBangla)
                )

                TransactionDetailInfoRow(
                    icon = Icons.Outlined.Payment,
                    label = strings.paymentMethod,
                    value = selectedPaymentMethod.getDisplayName(strings.isBangla)
                )
            }
        }

        // Notes Card (if present)
        if (notes.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surfaceVariant),
                border = BorderStroke(1.dp, LocalAppColors.current.border),
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
                            tint = ColorTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = strings.note,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp
                        ),
                        color = ColorTextPrimary
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
