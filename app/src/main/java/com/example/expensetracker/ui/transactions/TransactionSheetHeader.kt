package com.example.expensetracker.ui.transactions

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionSheetHeader(
    existingTransaction: Transaction?,
    isEditMode: Boolean,
    isExpense: Boolean,
    typeAccentColor: Color,
    strings: Strings,
    onCloseClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onCancelEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    isMarketActive: Boolean = false
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(appColors.border.copy(alpha = 0.9f))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val headerTitle = when {
                    isMarketActive ->
                        if (strings.isBangla) "বাজারের ফর্দ ও হিসাব" else "Market Checklist"
                    existingTransaction != null && !isEditMode ->
                        if (isExpense) strings.expenseDetails else strings.incomeDetails
                    existingTransaction != null && isEditMode ->
                        if (isExpense) strings.editExpenseTitle else strings.editIncomeTitle
                    else ->
                        if (isExpense) strings.addExpenseTitle else strings.addIncomeTitle
                }

                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = typeAccentColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, typeAccentColor.copy(alpha = 0.45f))
                ) {
                    val badgeText = when {
                        isMarketActive -> if (strings.isBangla) "বাজার" else "Market"
                        isExpense -> strings.expenseBadge
                        else -> strings.incomeBadge
                    }
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = typeAccentColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (existingTransaction != null && isEditMode) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LocalAppColors.current.surfaceVariant,
                        border = BorderStroke(1.dp, LocalAppColors.current.border),
                        modifier = Modifier.clickable {
                            haptics.lightTick()
                            onCancelEditClick()
                        }
                    ) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                            color = appColors.textSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                if (existingTransaction != null && onDeleteClick != null) {
                    HeaderCircleButton(
                        icon = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = appColors.expense,
                        background = appColors.expense.copy(alpha = 0.12f),
                        onClick = {
                            haptics.warningFeedback()
                            onDeleteClick()
                        }
                    )
                }

                if (existingTransaction == null || !isEditMode) {
                    HeaderCircleButton(
                        icon = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary,
                        background = LocalAppColors.current.surfaceVariant,
                        onClick = {
                            haptics.lightTick()
                            onCloseClick()
                        }
                    )
                }
            }
        }

    }
}

@Composable
private fun HeaderCircleButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}
