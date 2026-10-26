package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Calendar

data class ChooserContextualState(
    val isMorningBazaarHours: Boolean,
    val hasActiveTrip: Boolean,
    val isMarketContextual: Boolean,
    val isExpenseSuggested: Boolean,
    val timeGreeting: String,
    val marketSubtitle: String,
    val marketBadge: String?
)

fun calculateChooserContextualState(
    currentHour: Int,
    activeMarketList: MarketListWithItems?,
    isBangla: Boolean
): ChooserContextualState {
    val isMorningBazaarHours = currentHour in 6..11
    val totalItems = activeMarketList?.totalItems ?: 0
    val remainingItems = activeMarketList?.remainingItems ?: 0
    val isCompleted = activeMarketList?.list?.isCompleted ?: false
    val hasActiveTrip = totalItems > 0 && !isCompleted
    val isMarketContextual = hasActiveTrip || isMorningBazaarHours
    val isExpenseSuggested = !isMarketContextual

    val timeGreeting = when (currentHour) {
        in 5..11 -> if (isBangla) "শুভ সকাল · দ্রুত হিসাব যোগ করুন" else "Good Morning · Quick Add"
        in 12..16 -> if (isBangla) "শুভ দুপুর · দৈনন্দিন হিসাব" else "Good Afternoon · Daily Log"
        in 17..21 -> if (isBangla) "শুভ সন্ধ্যা · সারাদিনের হিসাব" else "Good Evening · Day's Summary"
        else -> if (isBangla) "শুভ রাত্রি · নতুন এন্ট্রি" else "Good Night · Quick Entry"
    }

    val marketSubtitle = when {
        hasActiveTrip && remainingItems > 0 -> {
            val remStr = if (isBangla) convertToBengaliDigits(remainingItems.toString()) else remainingItems.toString()
            val totStr = if (isBangla) convertToBengaliDigits(totalItems.toString()) else totalItems.toString()
            if (isBangla) "${remStr}টি কেনা বাকি (${totStr}টির মধ্যে)" else "$remStr items remaining (of $totStr)"
        }
        hasActiveTrip && remainingItems == 0 -> {
            val totStr = if (isBangla) convertToBengaliDigits(totalItems.toString()) else totalItems.toString()
            if (isBangla) "সবগুলো পণ্য কেনা সম্পন্ন (${totStr}টি) · হিসাব চূড়ান্ত করুন" else "All $totStr items purchased · Finalize trip"
        }
        isMorningBazaarHours -> {
            if (isBangla) "সকালের বাজার ফর্দ তৈরি বা কেনাকাটা" else "Create morning bazaar list & shopping"
        }
        else -> {
            if (isBangla) "বাজারের ফর্দ তৈরি করুন বা কেনাকাটার হিসাব" else "Create market checklist or track shopping"
        }
    }

    val marketBadge = when {
        hasActiveTrip && remainingItems == 0 -> if (isBangla) "হিসাব চূড়ান্ত করুন" else "Finalize Trip"
        hasActiveTrip -> if (isBangla) "চলমান ফর্দ" else "Active Trip"
        isMorningBazaarHours -> if (isBangla) "প্রস্তাবিত" else "Suggested"
        else -> null
    }

    return ChooserContextualState(
        isMorningBazaarHours = isMorningBazaarHours,
        hasActiveTrip = hasActiveTrip,
        isMarketContextual = isMarketContextual,
        isExpenseSuggested = isExpenseSuggested,
        timeGreeting = timeGreeting,
        marketSubtitle = marketSubtitle,
        marketBadge = marketBadge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalActionChooserSheet(
    isBangla: Boolean,
    activeMarketList: MarketListWithItems?,
    onDismiss: () -> Unit,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onOpenMarket: () -> Unit,
    modifier: Modifier = Modifier,
    onTransfer: (() -> Unit)? = null,
    currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    sheetState: SheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val marketTint = Color(0xFF00A8FF)

    val state = remember(currentHour, activeMarketList, isBangla) {
        calculateChooserContextualState(currentHour, activeMarketList, isBangla)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = LocalAppColors.current.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LocalAppColors.current.border)
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header Row with Time-Aware Greeting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBangla) "নতুন কী যুক্ত করতে চান?" else "What would you like to add?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = appColors.textPrimary
                    )
                    Text(
                        text = state.timeGreeting,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = appColors.textSecondary
                    )
                }

                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = if (isBangla) "বন্ধ করুন" else "Close",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Add Expense Card
            ActionTileCard(
                icon = Icons.Outlined.ArrowDownward,
                iconTint = appColors.expense,
                title = if (isBangla) "ব্যয় যোগ করুন" else "Add Expense",
                subtitle = if (isBangla) "দৈনন্দিন কেনাকাটা ও খরচের হিসাব" else "Log daily purchases & expenses",
                badgeText = if (state.isExpenseSuggested) (if (isBangla) "প্রস্তাবিত" else "Suggested") else null,
                badgeColor = appColors.expense,
                isHighlighted = state.isExpenseSuggested,
                onClick = {
                    haptics.mediumClick()
                    onAddExpense()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Add Income Card
            ActionTileCard(
                icon = Icons.Outlined.ArrowUpward,
                iconTint = appColors.income,
                title = if (isBangla) "আয় যোগ করুন" else "Add Income",
                subtitle = if (isBangla) "বেতন, লাভ বা অর্জিত টাকা" else "Record salary, profits or earnings",
                onClick = {
                    haptics.mediumClick()
                    onAddIncome()
                }
            )

            if (onTransfer != null) {
                Spacer(modifier = Modifier.height(10.dp))
                ActionTileCard(
                    icon = Icons.Outlined.SwapHoriz,
                    iconTint = Color(0xFF6366F1),
                    title = if (isBangla) "ফান্ড স্থানান্তর" else "Transfer Funds",
                    subtitle = if (isBangla) "এক অ্যাকাউন্ট থেকে অন্যটিতে স্থানান্তর" else "Move money between your accounts",
                    onClick = {
                        haptics.mediumClick()
                        onTransfer()
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Market Checklist & Trip Card
            ActionTileCard(
                icon = Icons.Outlined.ShoppingCart,
                iconTint = marketTint,
                title = if (isBangla) "বাজারের ফর্দ ও হিসাব" else "Market Checklist & Trip",
                subtitle = state.marketSubtitle,
                badgeText = state.marketBadge,
                badgeColor = marketTint,
                isHighlighted = state.isMarketContextual,
                onClick = {
                    haptics.mediumClick()
                    onOpenMarket()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActionTileCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color = iconTint,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isHighlighted) iconTint.copy(alpha = 0.08f) else LocalAppColors.current.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            width = if (isHighlighted) 1.5.dp else 1.dp,
            color = if (isHighlighted) badgeColor.copy(alpha = 0.45f) else LocalAppColors.current.border
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Title & Subtitle
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.textPrimary
                    )

                    if (badgeText != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeColor.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary,
                        maxLines = 1
                    )
                }
            }

            // Arrow Right icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = appColors.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
