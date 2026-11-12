package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.ui.components.AppButton
import com.example.expensetracker.ui.components.AppButtonVariant
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

/**
 * V7 TransactionDetailSheet per UX_FLOWS section 4:
 * - Clean modal sheet opened on tapping any transaction row
 * - Displays category icon, title, colored signed amount with tnum
 * - Metadata rows: date/time, category, account, payment method, notes
 * - Action buttons: Delete (with undo) & Edit (tap alternatives to swipe)
 */
@Composable
fun TransactionDetailSheet(
    transaction: Transaction,
    currencySymbol: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onViewReceipt: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val isIncome = transaction.type == TransactionType.INCOME
    val amountFormatted = "${if (isIncome) "+" else "-"} ${formatCurrency(transaction.amount, currencySymbol, useBengaliNumerals || isBangla)}"
    val amountColor = if (isIncome) appColors.income else appColors.expense

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Top Header: Icon + Title + Type Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CategoryIconHelper.parseColor(transaction.categoryColor).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = CategoryIconHelper.getIcon(transaction.categoryIcon),
                    contentDescription = null,
                    tint = CategoryIconHelper.parseColor(transaction.categoryColor),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )
                Text(
                    text = if (isIncome) strings.income else strings.expense,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = amountColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Amount
        Text(
            text = amountFormatted,
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFeatureSettings = "tnum"
            ),
            color = amountColor
        )

        Spacer(modifier = Modifier.height(18.dp))
        HairlineDivider()
        Spacer(modifier = Modifier.height(12.dp))

        // Details list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Date & Time
            TransactionDetailInfoRow(
                icon = Icons.Outlined.CalendarToday,
                label = if (isBangla) "তারিখ ও সময়" else "Date & Time",
                value = DateTimeHelper.formatItemDateTime(transaction.dateMillis, isBangla, useBengaliNumerals || isBangla)
            )

            // Category
            TransactionDetailInfoRow(
                icon = Icons.Outlined.Category,
                label = if (isBangla) "ক্যাটাগরি" else "Category",
                value = getLocalizedCategoryName(transaction.categoryName, isBangla)
            )

            // Account
            if (!transaction.accountName.isNullOrBlank()) {
                TransactionDetailInfoRow(
                    icon = Icons.Outlined.AccountBalance,
                    label = if (isBangla) "অ্যাকাউন্ট" else "Account",
                    value = transaction.accountName ?: ""
                )
            }

            // Payment method
            TransactionDetailInfoRow(
                icon = Icons.Outlined.Payment,
                label = strings.paymentMethod,
                value = transaction.paymentMethod.name
            )

            // Notes
            if (transaction.notes.isNotBlank()) {
                TransactionDetailInfoRow(
                    icon = Icons.Outlined.Notes,
                    label = if (isBangla) "নোট" else "Notes",
                    value = transaction.notes
                )
            }

            // Receipt preview chip if receipt attached
            if (transaction.receiptImagePath != null && onViewReceipt != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusM))
                        .clickable { onViewReceipt() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "📎 সংযুক্ত রশিদ" else "📎 Attached receipt",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                        color = appColors.accent
                    )
                    Text(
                        text = if (isBangla) "দেখুন ➔" else "View ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = appColors.accent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: Delete & Edit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppButton(
                text = strings.delete,
                onClick = {
                    onDismiss()
                    onDelete(transaction)
                },
                variant = AppButtonVariant.Destructive,
                icon = Icons.Outlined.Delete,
                modifier = Modifier.weight(1f)
            )

            AppButton(
                text = strings.editTransaction,
                onClick = {
                    onDismiss()
                    onEdit(transaction)
                },
                variant = AppButtonVariant.Primary,
                icon = Icons.Outlined.Edit,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
