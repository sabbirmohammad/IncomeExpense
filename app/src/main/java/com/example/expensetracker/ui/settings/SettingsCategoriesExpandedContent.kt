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

        CategoryTypeSegmentBar(
            totalCategoriesCount = categories.size,
            expenseCategoriesCount = expenseCategories.size,
            incomeCategoriesCount = incomeCategories.size,
            selectedTypeFilter = selectedTypeFilter,
            onSelectTypeFilter = onSelectTypeFilter,
            isBangla = isBangla,
            haptics = haptics
        )

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

        CategoryExpandedBottomActions(
            onAddCategoryClick = onAddCategoryClick,
            onCollapseClick = onCollapseClick,
            isBangla = isBangla,
            haptics = haptics
        )
    }
}
