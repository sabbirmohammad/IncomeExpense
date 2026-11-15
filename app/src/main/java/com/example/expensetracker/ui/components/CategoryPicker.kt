package com.example.expensetracker.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS

/**
 * V7 CategoryPicker:
 * - Inline category chips (2 rows scrollable horizontally when collapsed).
 * - "More" button expands the inline grid smoothly (no nested modal).
 * - "Less" button collapses it back.
 * - Minimum 48dp touch targets on every chip.
 * - Clear active state with category color and accentContainer tint.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryPicker(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    onAddNewCategory: (() -> Unit)? = null,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val appColors = LocalAppColors.current
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(240))
    ) {
        if (!isExpanded) {
            CategoryPickerCollapsedRows(
                categories = categories,
                selectedCategory = selectedCategory,
                isBangla = isBangla,
                onCategorySelected = onCategorySelected,
                onExpand = { isExpanded = true },
                onAddNewCategory = onAddNewCategory,
                haptics = haptics
            )
        } else {
            // Expanded: full inline FlowRow showing all categories
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    CategoryChipItem(
                        category = category,
                        isSelected = category.id == selectedCategory?.id,
                        isBangla = isBangla,
                        onClick = {
                            haptics.lightTick()
                            onCategorySelected(category)
                        }
                    )
                }

                // Inline "Less ▴" collapse chip
                Surface(
                    shape = RoundedCornerShape(RadiusS),
                    color = appColors.accentContainer,
                    border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                        .clickable {
                            haptics.lightTick()
                            isExpanded = false
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExpandLess,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isBangla) "সংক্ষিপ্ত" else "Less",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = appColors.accent
                        )
                    }
                }

                if (onAddNewCategory != null) {
                    Surface(
                        shape = RoundedCornerShape(RadiusS),
                        color = appColors.surfaceVariant,
                        border = BorderStroke(HairlineInkStroke, appColors.border),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 44.dp)
                            .minimumInteractiveComponentSize()
                            .clickable {
                                haptics.lightTick()
                                onAddNewCategory()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isBangla) "নতুন" else "New",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
