package com.example.expensetracker.ui.market

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketShoppingHeaderHUD(
    totalCount: Int,
    purchasedCount: Int,
    totalSpent: Double,
    estimatedBudget: Double?,
    currencySymbol: String,
    isBangla: Boolean,
    isEditMode: Boolean,
    hasMultipleLists: Boolean,
    listTitle: String,
    onBackToAllLists: (() -> Unit)?,
    onEnterEditMode: () -> Unit,
    onExitEditMode: () -> Unit,
    onDeleteList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val progress = if (totalCount > 0) purchasedCount.toFloat() / totalCount.toFloat() else 0f
    val percent = (progress * 100).toInt()
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "hudProgress")

    Surface(
        shape = RoundedCornerShape(16.dp), color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border), modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                // LEFT: Progress, Status & Metrics
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f, fill = false)) {
                    if (hasMultipleLists && onBackToAllLists != null) {
                        Surface(onClick = { haptics.lightTick(); onBackToAllLists() }, shape = RoundedCornerShape(8.dp), color = appColors.surfaceVariant, modifier = Modifier.defaultMinSize(minHeight = 32.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = appColors.textSecondary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = if (isBangla) "সকল" else "All", style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = appColors.textSecondary)
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(if (progress >= 1f && totalCount > 0) appColors.income else appColors.accent))
                            val progressText = if (totalCount == 0) {
                                if (isBangla) "ফর্দে কোনো পণ্য নেই" else "No items in list"
                            } else if (isEditMode) {
                                if (isBangla) "ফর্দ সম্পাদনা (${convertToBengaliDigits(totalCount.toString())}টি পণ্য)" else "Edit List ($totalCount items)"
                            } else {
                                if (isBangla) "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি কেনা (${convertToBengaliDigits(percent.toString())}%)"
                                else "$purchasedCount/$totalCount bought ($percent%)"
                            }
                            Text(text = progressText, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"), color = appColors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        // Metrics (Spent & Estimated Budget)
                        if (totalCount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = if (isBangla) "খরচ: ${formatCurrency(totalSpent, currencySymbol)}" else "Spent: ${formatCurrency(totalSpent, currencySymbol)}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"), color = appColors.income, maxLines = 1)
                                if (estimatedBudget != null && estimatedBudget > 0) {
                                    Text(text = "•", fontSize = 12.sp, color = appColors.textSecondary.copy(alpha = 0.5f))
                                    Text(text = if (isBangla) "বাজেট: ${formatCurrency(estimatedBudget, currencySymbol)}" else "Est: ${formatCurrency(estimatedBudget, currencySymbol)}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum"), color = appColors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        } else {
                            Text(text = if (isBangla) "নিচে নতুন পণ্য যোগ করুন" else "Add new items below", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Normal), color = appColors.textSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // RIGHT: Actions (Edit button OR Delete List & Done buttons)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!isEditMode) {
                        Surface(
                            onClick = { haptics.lightTick(); onEnterEditMode() },
                            shape = RoundedCornerShape(9.dp), color = appColors.accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f)), modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Outlined.Edit, contentDescription = if (isBangla) "ফর্দ সম্পাদনা" else "Edit List", tint = appColors.accent, modifier = Modifier.size(13.dp))
                                Text(text = if (isBangla) "এডিট" else "Edit", style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = appColors.accent)
                            }
                        }
                    } else {
                        Surface(
                            onClick = { haptics.mediumClick(); onDeleteList() },
                            shape = RoundedCornerShape(9.dp), color = appColors.expense.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, appColors.expense.copy(alpha = 0.25f)), modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = if (isBangla) "মুছুন" else "Delete", tint = appColors.expense, modifier = Modifier.size(13.dp))
                                Text(text = if (isBangla) "মুছুন" else "Delete", style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = appColors.expense)
                            }
                        }

                        if (totalCount > 0) {
                            Surface(
                                onClick = { haptics.lightTick(); onExitEditMode() },
                                shape = RoundedCornerShape(9.dp), color = appColors.income, shadowElevation = 1.dp, modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Filled.Check, contentDescription = if (isBangla) "সম্পন্ন" else "Done", tint = Color.White, modifier = Modifier.size(13.dp))
                                    Text(text = if (isBangla) "সম্পন্ন" else "Done", style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Progress bar inside card
            if (totalCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().height(3.5.dp).clip(RoundedCornerShape(2.dp)),
                    color = appColors.income, trackColor = appColors.border.copy(alpha = 0.5f)
                )
            }
        }
    }
}
