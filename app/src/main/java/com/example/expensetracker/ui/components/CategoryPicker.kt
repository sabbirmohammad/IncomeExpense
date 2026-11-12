package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS
import java.util.Calendar

/**
 * Smart recommendation engine for selecting default categories.
 * Enforces C4 rule:
 * 1. Last used category for this type + this time of day (within ±2 hours).
 * 2. Else most frequent category for this type.
 * 3. Else "Other" / "অন্যান্য".
 * 4. Never alphabetical first.
 */
object CategoryRecommendationEngine {

    fun findSmartDefaultCategory(
        categories: List<Category>,
        type: TransactionType,
        recentTransactions: List<Transaction> = emptyList(),
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): Category? {
        val matchingCategories = categories.filter { it.type == type }
        if (matchingCategories.isEmpty()) return null

        val categoryById = matchingCategories.associateBy { it.id }
        val typeTransactions = recentTransactions.filter { it.type == type }

        // 1. Last used category for this type + this time of day (±2 hours)
        if (typeTransactions.isNotEmpty()) {
            val timeOfDayTxs = typeTransactions.filter { tx ->
                val cal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
                val txHour = cal.get(Calendar.HOUR_OF_DAY)
                val diff = kotlin.math.abs(txHour - currentHour)
                diff <= 2 || diff >= 22 // Wrap around midnight
            }
            val matchFromTime = timeOfDayTxs.maxByOrNull { it.dateMillis }?.let { categoryById[it.categoryId] }
            if (matchFromTime != null) {
                return matchFromTime
            }

            // 2. Most frequent category for this type
            val categoryCounts = typeTransactions.groupingBy { it.categoryId }.eachCount()
            val mostFrequentCatId = categoryCounts.maxByOrNull { it.value }?.key
            val matchFromFreq = mostFrequentCatId?.let { categoryById[it] }
            if (matchFromFreq != null) {
                return matchFromFreq
            }
        }

        // 3. Fallback: "Other" or "অন্যান্য" or "General"
        val otherCategory = matchingCategories.find {
            it.name.equals("Other", ignoreCase = true) ||
            it.name.equals("Others", ignoreCase = true) ||
            it.name.contains("অন্যান্য") ||
            it.name.equals("General", ignoreCase = true)
        }
        if (otherCategory != null) {
            return otherCategory
        }

        // 4. Default: first available category in database order (not sorted alphabetically)
        return matchingCategories.firstOrNull()
    }
}

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
            // Collapsed: 2-row horizontal scrolling grid
            // Split categories into 2 balanced rows
            val halfSize = (categories.size + 1) / 2
            val row1 = categories.take(halfSize)
            val row2 = categories.drop(halfSize)

            val scrollState = rememberScrollState()

            Row(
                modifier = Modifier
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
                                        isExpanded = true
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

@Composable
private fun CategoryChipItem(
    category: Category,
    isSelected: Boolean,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val catColor = remember(category.colorHex) {
        CategoryIconHelper.parseColor(category.colorHex)
    }
    val icon = remember(category.icon) {
        CategoryIconHelper.getIcon(category.icon)
    }
    val label = remember(category.name, isBangla) {
        getLocalizedCategoryName(category.name, isBangla)
    }

    val backgroundColor = when {
        isSelected -> appColors.accentContainer
        else -> appColors.surfaceVariant
    }

    val borderStroke = when {
        isSelected -> BorderStroke(1.5.dp, catColor)
        appColors.isDark -> null
        else -> BorderStroke(HairlineInkStroke, appColors.border)
    }

    val contentColor = when {
        isSelected -> appColors.textPrimary
        else -> appColors.textSecondary
    }

    Surface(
        shape = RoundedCornerShape(RadiusS),
        color = backgroundColor,
        border = borderStroke,
        modifier = Modifier
            .defaultMinSize(minHeight = 44.dp)
            .minimumInteractiveComponentSize()
            .clickable { onClick() }
            .semantics {
                role = Role.RadioButton
                selected = isSelected
                contentDescription = "$label, ${if (isSelected) "selected" else "not selected"}"
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) catColor else appColors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.5.sp
                ),
                color = contentColor
            )
        }
    }
}

@Preview(name = "CategoryPicker - Light En", showBackground = true)
@Composable
private fun CategoryPickerLightPreview() {
    val sampleCategories = listOf(
        Category(1, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#10B981"),
        Category(2, "Dining", TransactionType.EXPENSE, "Restaurant", "#F97316"),
        Category(3, "Transport", TransactionType.EXPENSE, "DirectionsCar", "#0EA5E9"),
        Category(4, "Rent", TransactionType.EXPENSE, "Home", "#8B5CF6"),
        Category(5, "Bills", TransactionType.EXPENSE, "Receipt", "#F59E0B"),
        Category(6, "Health", TransactionType.EXPENSE, "LocalHospital", "#EC4899"),
        Category(7, "Shopping", TransactionType.EXPENSE, "ShoppingBag", "#14B8A6"),
        Category(8, "Other", TransactionType.EXPENSE, "MoreHoriz", "#64748B")
    )
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            CategoryPicker(
                categories = sampleCategories,
                selectedCategory = sampleCategories.first(),
                onCategorySelected = {},
                isBangla = false
            )
        }
    }
}
