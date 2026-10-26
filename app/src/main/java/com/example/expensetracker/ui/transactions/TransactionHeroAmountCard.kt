package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled

@Composable
fun TransactionHeroAmountCard(
    transaction: Transaction,
    selectedCategory: Category?,
    matchedAccount: Account?,
    typeAccentColor: Color,
    currencySymbol: String,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(typeAccentColor.copy(alpha = 0.45f), LocalAppColors.current.border)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            typeAccentColor.copy(alpha = 0.08f),
                            LocalAppColors.current.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = typeAccentColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, typeAccentColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(typeAccentColor)
                        )
                        Text(
                            text = if (transaction.type == TransactionType.EXPENSE) strings.expense else strings.income,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = typeAccentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val amountFormatted = formatCurrency(transaction.amount, currencySymbol, globalBengaliNumeralsEnabled || strings.isBangla)
                val prefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
                Text(
                    text = "$prefix $amountFormatted",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = typeAccentColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                val catName = selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName
                val accountOrMethod = (matchedAccount?.name ?: transaction.accountName)?.takeIf { it.isNotBlank() }?.let { getLocalizedAccountName(it, strings.isBangla) }
                    ?: transaction.paymentMethod.getDisplayName(strings.isBangla)
                val subText = if (accountOrMethod.isNotBlank()) "$catName • $accountOrMethod" else catName
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.textSecondary
                )
            }
        }
    }
}
