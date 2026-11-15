package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS

@Composable
internal fun CategoryPickerCollapsedRows(
    categories: List<Category>,
    selectedCategory: Category?,
    isBangla: Boolean,
    onCategorySelected: (Category) -> Unit,
    onExpand: () -> Unit,
    onAddNewCategory: (() -> Unit)?,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val halfSize = (categories.size + 1) / 2
    val row1 = categories.take(halfSize)
    val row2 = categories.drop(halfSize)
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row1.forEach { category ->
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
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row2.forEach { category ->
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

                // Inline "More ▾" toggle chip
                if (categories.size > 6) {
                    Surface(
                        shape = RoundedCornerShape(RadiusS),
                        color = appColors.accentContainer,
                        border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 44.dp)
                            .minimumInteractiveComponentSize()
                            .clickable {
                                haptics.lightTick()
                                onExpand()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBangla) "আরও" else "More",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                ),
                                color = appColors.accent
                            )
                        }
                    }
                }

                // Optional Add New Category inline chip
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
