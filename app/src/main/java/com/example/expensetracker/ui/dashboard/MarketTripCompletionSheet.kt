package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Modern Digital Receipt Bottom Sheet for Market Trip Completion.
 * Presented when completing a shopping trip ("বাজার সম্পন্ন করুন").
 * - Digital receipt style card layout.
 * - Total cost highlight and item count.
 * - Savings summary comparing actual costs vs prior purchase insights.
 * - Payment account selector (Cash/Bkash/Bank).
 * - Celebration feedback on confirmation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketTripCompletionSheet(
    trip: MarketListWithItems,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    onDismiss: () -> Unit,
    onConfirmComplete: (selectedAccountId: Long?) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Default to the default account or the first available account
    val defaultAccount = remember(accounts) {
        accounts.find { it.isDefault } ?: accounts.firstOrNull()
    }
    var selectedAccountId by remember(defaultAccount?.id) { mutableStateOf<Long?>(defaultAccount?.id) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Summary calculations
    val allItems = trip.items
    val totalSpent = remember(allItems) {
        allItems.sumOf { it.amount ?: 0.0 }
    }

    // Savings calculation based on historical unit price vs actual spent for PRICED items only
    val calculatedSavings = remember(allItems, productPriceInsights) {
        val pricedItems = allItems.filter { (it.amount ?: 0.0) > 0.0 }
        val estForPriced = pricedItems.sumOf { item ->
            val actual = item.amount ?: 0.0
            val matched = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
            if (matched != null && matched.currentUnitPrice > 0.0 && item.quantity > 0.0) {
                UnitConverterEngine.estimateCost(matched.currentUnitPrice, item.quantity, item.unit)
            } else {
                actual
            }
        }
        val spentForPriced = pricedItems.sumOf { it.amount ?: 0.0 }
        if (estForPriced > spentForPriced && spentForPriced > 0.0) estForPriced - spentForPriced else 0.0
    }

    var isCelebrated by remember { mutableStateOf(false) }
    val celebrationScale by animateFloatAsState(
        targetValue = if (isCelebrated) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "celebrationScale"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = appColors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(appColors.border.copy(alpha = 0.8f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Celebration & Header Banner
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(appColors.income.copy(alpha = 0.14f))
                    .graphicsLayer {
                        scaleX = celebrationScale
                        scaleY = celebrationScale
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Celebration,
                    contentDescription = null,
                    tint = appColors.income,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isBangla) "বাজার সম্পন্ন ও ডিজিটাল রসিদ" else "Market Trip Digital Receipt",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = appColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = trip.list.title.ifBlank {
                    if (isBangla) "আজকের বাজার" else "Today's Market Trip"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.5.sp
                ),
                color = appColors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Digital Receipt Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = appColors.background,
                border = BorderStroke(1.dp, appColors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Total Cost Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "সর্বমোট খরচ" else "Total Cost",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = appColors.textSecondary
                            )
                            Text(
                                text = formatCurrency(totalSpent, currencySymbol),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = appColors.accent
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = appColors.accent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (isBangla)
                                    "${convertToBengaliDigits(allItems.size.toString())}টি পণ্যের ফর্দ"
                                else
                                    "${allItems.size} items",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = appColors.accent,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Savings Summary Card
                    Spacer(modifier = Modifier.height(10.dp))
                    if (calculatedSavings > 0.0) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = appColors.income.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, appColors.income.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = appColors.income,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isBangla) "🎉 চমৎকার! আনুমানিক সাশ্রয়:" else "🎉 Estimated Savings:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = appColors.income
                                    )
                                    Text(
                                        text = formatCurrency(calculatedSavings, currencySymbol),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        color = appColors.income
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = appColors.surface,
                            border = BorderStroke(1.dp, appColors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "💡 সুপরিকল্পিত ও গোছানো বাজার সম্পন্ন হচ্ছে" else "💡 Well-planned shopping trip completed",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = appColors.textSecondary
                                )
                            }
                        }
                    }

                    // Itemized Receipt Lines (Scrollable preview)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isBangla) "ক্রয়কৃত পণ্যের বিবরণ:" else "Purchased Items Breakdown:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = appColors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(modifier = Modifier.heightIn(max = 140.dp)) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(allItems) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "• ${item.name} (${item.formatQuantity()})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = appColors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    val itemAmt = item.amount ?: 0.0
                                    Text(
                                        text = if (itemAmt > 0) formatCurrency(itemAmt, currencySymbol)
                                               else (if (isBangla) "মূল্য ছাড়া" else "No price"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        ),
                                        color = if (itemAmt > 0) appColors.textPrimary else appColors.textSecondary.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Account Selector
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isBangla) "যে অ্যাকাউন্ট থেকে খরচ পরিশোধ হবে:" else "Select Payment Account:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp
                    ),
                    color = appColors.textSecondary
                )

                if (accounts.isEmpty()) {
                    // Fallback pill when no accounts registered
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = appColors.accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, appColors.accent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Payments,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isBangla) "নগদ (Cash)" else "Cash",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = appColors.accent
                            )
                        }
                    }
                } else if (accounts.size <= 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        accounts.forEach { acc ->
                            val isSelected = selectedAccountId == acc.id
                            val accountIcon: ImageVector = when (acc.type) {
                                AccountType.BANK -> Icons.Filled.AccountBalance
                                AccountType.MFS -> Icons.Filled.CreditCard
                                else -> Icons.Filled.Payments
                            }

                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    selectedAccountId = acc.id
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) appColors.accent.copy(alpha = 0.14f) else appColors.background,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) appColors.accent else appColors.border
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = accountIcon,
                                        contentDescription = null,
                                        tint = if (isSelected) appColors.accent else appColors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = acc.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) appColors.accent else appColors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(accounts, key = { it.id }) { acc ->
                            val isSelected = selectedAccountId == acc.id
                            val accountIcon: ImageVector = when (acc.type) {
                                AccountType.BANK -> Icons.Filled.AccountBalance
                                AccountType.MFS -> Icons.Filled.CreditCard
                                else -> Icons.Filled.Payments
                            }

                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    selectedAccountId = acc.id
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) appColors.accent.copy(alpha = 0.14f) else appColors.background,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) appColors.accent else appColors.border
                                ),
                                modifier = Modifier.width(105.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = accountIcon,
                                        contentDescription = null,
                                        tint = if (isSelected) appColors.accent else appColors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = acc.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) appColors.accent else appColors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, appColors.border)
                ) {
                    Text(
                        text = if (isBangla) "ফিরে যান" else "Back",
                        color = appColors.textSecondary,
                        fontSize = 13.5.sp
                    )
                }

                Button(
                    onClick = {
                        if (!isSubmitting) {
                            isSubmitting = true
                            haptics.successFeedback()
                            isCelebrated = true
                            onConfirmComplete(selectedAccountId)
                        }
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1.4f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.income)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isBangla) "লেনদেন সম্পন্ন করুন" else "Confirm & Save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
