package com.example.expensetracker.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun SettingsCategoriesCard(
    categories: List<Category>,
    useBengaliNumerals: Boolean,
    onAddCategoryClick: () -> Unit,
    onEditCategoryClick: (Category) -> Unit,
    onDeleteCategoryClick: (Category) -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val isBangla = strings.isBangla

    var isExpanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    var selectedTypeFilter by remember { mutableStateOf<TransactionType?>(null) } // null = all

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "chevron_rotate"
    )

    val expenseCategories = remember(categories) { categories.filter { it.type == TransactionType.EXPENSE } }
    val incomeCategories = remember(categories) { categories.filter { it.type == TransactionType.INCOME } }

    val filteredCategories = remember(categories, selectedTypeFilter) {
        when (selectedTypeFilter) {
            TransactionType.EXPENSE -> expenseCategories
            TransactionType.INCOME -> incomeCategories
            null -> categories
        }
    }

    AppCard(
        modifier = modifier,
        borderColor = if (isExpanded) appColors.accent.copy(alpha = 0.35f) else appColors.border
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Interactive Header (Tapping toggles expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        haptics.lightTick()
                        isExpanded = !isExpanded
                    }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Category Section Icon Badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.accent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Category,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBangla) "ক্যাটাগরি সমূহ" else "Categories",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textPrimary
                        )

                        // Counter badge capsule
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = appColors.accent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${DateTimeHelper.formatCounter(categories.size, isBangla, useBengaliNumerals || isBangla)} ${if (isBangla) "টি" else "active"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = appColors.accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Action Buttons on Right: Quick "+ New" and Chevron Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Add Button
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            onAddCategoryClick()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = appColors.accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBangla) "নতুন" else "New",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                ),
                                color = appColors.accent
                            )
                        }
                    }

                    // Rotating Chevron Toggle Button
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            isExpanded = !isExpanded
                        },
                        shape = CircleShape,
                        color = appColors.surface.copy(alpha = 0.7f),
                        border = BorderStroke(0.5.dp, appColors.border)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = if (isExpanded) appColors.accent else appColors.textSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(chevronRotation)
                            )
                        }
                    }
                }
            }

            // Compact Preview Ribbon when COLLAPSED
            if (!isExpanded && categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(appColors.background.copy(alpha = 0.45f))
                        .clickable {
                            haptics.lightTick()
                            isExpanded = true
                        }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        categories.take(6).forEach { cat ->
                            CategoryIconBadge(
                                iconName = cat.icon,
                                colorHex = cat.colorHex,
                                size = 24.dp
                            )
                        }
                        if (categories.size > 6) {
                            Surface(
                                shape = CircleShape,
                                color = appColors.surface,
                                border = BorderStroke(0.5.dp, appColors.border)
                            ) {
                                Text(
                                    text = "+${categories.size - 6}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = appColors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = if (isBangla) "খুলুন →" else "Expand →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.accent
                    )
                }
            }

            // Expanded Accordion Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(280)) + fadeIn(animationSpec = tween(280)),
                exit = shrinkVertically(animationSpec = tween(240)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HairlineDivider(color = appColors.border.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Type Filter Segment Bar (All / Expense / Income)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.surface)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CategoryFilterSegment(
                            title = if (isBangla) "সব (${categories.size})" else "All (${categories.size})",
                            isSelected = selectedTypeFilter == null,
                            activeColor = appColors.accent,
                            onClick = {
                                haptics.lightTick()
                                selectedTypeFilter = null
                            },
                            modifier = Modifier.weight(1f)
                        )

                        CategoryFilterSegment(
                            title = if (isBangla) "ব্যয় (${expenseCategories.size})" else "Expense (${expenseCategories.size})",
                            isSelected = selectedTypeFilter == TransactionType.EXPENSE,
                            activeColor = appColors.expense,
                            onClick = {
                                haptics.lightTick()
                                selectedTypeFilter = TransactionType.EXPENSE
                            },
                            modifier = Modifier.weight(1f)
                        )

                        CategoryFilterSegment(
                            title = if (isBangla) "আয় (${incomeCategories.size})" else "Income (${incomeCategories.size})",
                            isSelected = selectedTypeFilter == TransactionType.INCOME,
                            activeColor = appColors.income,
                            onClick = {
                                haptics.lightTick()
                                selectedTypeFilter = TransactionType.INCOME
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // List of categories (showing up to 5 items of the filtered view)
                    val displayList = filteredCategories.take(5)
                    if (displayList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) "কোনো ক্যাটাগরি পাওয়া যায়নি" else "No categories found",
                                style = MaterialTheme.typography.bodySmall,
                                color = appColors.textMuted
                            )
                        }
                    } else {
                        displayList.forEach { cat ->
                            SettingsCategoryRowItem(
                                category = cat,
                                onEditClick = { onEditCategoryClick(cat) },
                                onDeleteClick = { onDeleteCategoryClick(cat) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // "View All" Button (if more than 5 items or user wants full modal)
                    if (categories.size > 5) {
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                onViewAllCategoriesClick()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = appColors.background.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, appColors.border.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBangla) "সব ক্যাটাগরি ব্রাউজ করুন (${categories.size})" else "Browse all categories (${categories.size})",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = appColors.accent
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = appColors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Bottom Row: Add New + Collapse Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                haptics.lightTick()
                                onAddCategoryClick()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "নতুন ক্যাটাগরি যোগ করুন" else "Add New Category",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.accent
                            )
                        }

                        TextButton(
                            onClick = {
                                haptics.lightTick()
                                isExpanded = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ExpandLess,
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBangla) "সংকুচিত করুন" else "Collapse",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterSegment(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.15f) else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, activeColor.copy(alpha = 0.4f)) else null
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) activeColor else appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
