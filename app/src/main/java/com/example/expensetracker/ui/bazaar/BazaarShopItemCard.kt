package com.example.expensetracker.ui.bazaar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.PulseTabularText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarShopItemCard(
    item: MarketItem,
    lastRate: Pair<Double, String>?,
    isHighlighted: Boolean,
    onCardClick: () -> Unit,
    onCheckCircleClick: () -> Unit,
    onSwipeRightDone: () -> Unit,
    onSwipeLeftSkip: () -> Unit,
    onEditPrice: () -> Unit,
    onMarkNotAvailable: () -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false,
    isPrivacyMode: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    // Pulse highlight animation for auto-advance visual feedback
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    var showMenu by remember { mutableStateOf(false) }

    // SwipeToDismissBox with reset so card never gets stuck offscreen (Amendment 2)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onSwipeRightDone()
                    false // Return false to snap back immediately
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onSwipeLeftSkip()
                    false // Return false to snap back immediately
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    val doneActionLabel = if (isBangla) "কেনা সম্পন্ন" else "Mark Done"
    val skipActionLabel = if (isBangla) "স্কিপ করুন" else "Skip Item"

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val isRight = direction == SwipeToDismissBoxValue.StartToEnd
            val bgColor = if (isRight) appColors.accent else appColors.warning
            val icon = if (isRight) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward
            val label = if (isRight) doneActionLabel else skipActionLabel

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(bgColor)
                    .padding(horizontal = 24.dp),
                contentAlignment = if (isRight) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label, style = typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(doneActionLabel) { onSwipeRightDone(); true },
                    CustomAccessibilityAction(skipActionLabel) { onSwipeLeftSkip(); true }
                )
            }
    ) {
        val cardBorderColor by animateColorAsState(
            targetValue = when {
                isHighlighted -> appColors.accent.copy(alpha = pulseAlpha)
                item.isPurchased -> appColors.accent.copy(alpha = 0.35f)
                else -> appColors.border
            },
            label = "cardBorder"
        )

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (item.isPurchased) appColors.surfaceVariant.copy(alpha = 0.5f) else appColors.surface,
            border = androidx.compose.foundation.BorderStroke(if (isHighlighted) 2.dp else 1.dp, cardBorderColor),
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = if (isBangla) "${item.name} এর দাম লিখুন" else "Enter price for ${item.name}",
                    onClick = onCardClick
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                // 1. Big Check Circle Button (56dp touch target per Amendment 1)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(
                            role = Role.Checkbox,
                            onClickLabel = doneActionLabel,
                            onClick = onCheckCircleClick
                        )
                        .semantics {
                            role = Role.Checkbox
                            contentDescription = "${item.name}, ${if (item.isPurchased) doneActionLabel else "Not bought"}"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (item.isPurchased) appColors.accent else Color.Transparent)
                            .border(
                                width = 2.dp,
                                color = if (item.isPurchased) appColors.accent else appColors.border,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.isPurchased) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = appColors.onAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 2. Center: Name, Qty/Unit, and Last Rate Badge
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = if (item.isPurchased) appColors.textSecondary else appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val qtyNum = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()}" else "${item.quantity}"
                        val qtyDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(qtyNum) else qtyNum

                        PulseTabularText(
                            text = "$qtyDisplay ${item.unit}",
                            style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = appColors.textSecondary
                        )

                        if (!item.isPurchased && lastRate != null && lastRate.first > 0.0) {
                            Text(text = " · ", style = typography.bodySmall, color = appColors.textMuted)
                            val rateNum = if (lastRate.first % 1.0 == 0.0) "${lastRate.first.toInt()}" else "%.1f".format(lastRate.first)
                            val rateDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(rateNum) else rateNum
                            Text(
                                text = if (isBangla) "গতবার ৳$rateDisplay/${lastRate.second}" else "Last ৳$rateDisplay/${lastRate.second}",
                                style = typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = appColors.textMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 3. Right: Amount Badge & 48dp Options Menu
                Column(horizontalAlignment = Alignment.End) {
                    if (item.isPurchased && item.amount != null && item.amount > 0.0) {
                        val amtNum = if (item.amount % 1.0 == 0.0) "${item.amount.toInt()}" else "%.1f".format(item.amount)
                        val amtDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(amtNum) else amtNum
                        PulseTabularText(
                            text = if (isPrivacyMode) "••••" else "৳$amtDisplay",
                            style = typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = appColors.accent
                        )
                    } else if (item.isPurchased) {
                        Text(
                            text = if (isBangla) "মূল্য ছাড়া" else "No price",
                            style = typography.bodySmall,
                            color = appColors.textMuted
                        )
                    } else {
                        PulseTabularText(
                            text = "৳—",
                            style = typography.bodyMedium,
                            color = appColors.textMuted
                        )
                    }
                }

                // 4. Overflow Menu (48dp touch target)
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = if (isBangla) "বিকল্পসমূহ" else "Options",
                            tint = appColors.textMuted
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isBangla) "দাম পরিবর্তন" else "Edit price") },
                            onClick = {
                                showMenu = false
                                onEditPrice()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(skipActionLabel) },
                            onClick = {
                                showMenu = false
                                onSwipeLeftSkip()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isBangla) "পাওয়া যায়নি" else "Not available") },
                            onClick = {
                                showMenu = false
                                onMarkNotAvailable()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isBangla) "মুছুন" else "Remove", color = appColors.expense) },
                            onClick = {
                                showMenu = false
                                onRemoveItem()
                            }
                        )
                    }
                }
            }
        }
    }
}
