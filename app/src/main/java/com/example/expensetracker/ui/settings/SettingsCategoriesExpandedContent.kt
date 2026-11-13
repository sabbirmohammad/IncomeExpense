package com.example.expensetracker.ui.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.HairlineDivider

/**
 * Expanded body content of SettingsCategoriesCard including type segment filter, category rows, view all button, and actions.
 */
@Composable
fun SettingsCategoriesExpandedContent(
    categories: List<Category>,
    expenseCategories: List<Category>,
    incomeCategories: List<Category>,
    filteredCategories: List<Category>,
    selectedTypeFilter: TransactionType?,
    onSelectTypeFilter: (TransactionType?) -> Unit,
    onEditCategoryClick: (Category) -> Unit,
    onDeleteCategoryClick: (Category) -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    onCollapseClick: () -> Unit,
    isBangla: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier.padding(top = 14.dp)) {
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
                    onSelectTypeFilter(null)
                },
                modifier = Modifier.weight(1f)
            )

            CategoryFilterSegment(
                title = if (isBangla) "ব্যয় (${expenseCategories.size})" else "Expense (${expenseCategories.size})",
                isSelected = selectedTypeFilter == TransactionType.EXPENSE,
                activeColor = appColors.expense,
                onClick = {
                    haptics.lightTick()
                    onSelectTypeFilter(TransactionType.EXPENSE)
                },
                modifier = Modifier.weight(1f)
            )

            CategoryFilterSegment(
                title = if (isBangla) "আয় (${incomeCategories.size})" else "Income (${incomeCategories.size})",
                isSelected = selectedTypeFilter == TransactionType.INCOME,
                activeColor = appColors.income,
                onClick = {
                    haptics.lightTick()
                    onSelectTypeFilter(TransactionType.INCOME)
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
                    onCollapseClick()
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
