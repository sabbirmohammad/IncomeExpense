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
            SettingsCategoriesCardHeader(
                isExpanded = isExpanded,
                onToggleExpand = { isExpanded = !isExpanded },
                chevronRotation = chevronRotation,
                categoriesCount = categories.size,
                isBangla = isBangla,
                useBengaliNumerals = useBengaliNumerals,
                onAddCategoryClick = onAddCategoryClick,
                haptics = haptics
            )

            // Compact Preview Ribbon when COLLAPSED
            if (!isExpanded && categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                SettingsCategoriesCollapsedRibbon(
                    categories = categories,
                    isBangla = isBangla,
                    onExpandClick = {
                        haptics.lightTick()
                        isExpanded = true
                    }
                )
            }

            // Expanded Accordion Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(280)) + fadeIn(animationSpec = tween(280)),
                exit = shrinkVertically(animationSpec = tween(240)) + fadeOut(animationSpec = tween(200))
            ) {
                SettingsCategoriesExpandedContent(
                    categories = categories,
                    expenseCategories = expenseCategories,
                    incomeCategories = incomeCategories,
                    filteredCategories = filteredCategories,
                    selectedTypeFilter = selectedTypeFilter,
                    onSelectTypeFilter = { selectedTypeFilter = it },
                    onEditCategoryClick = onEditCategoryClick,
                    onDeleteCategoryClick = onDeleteCategoryClick,
                    onViewAllCategoriesClick = onViewAllCategoriesClick,
                    onAddCategoryClick = onAddCategoryClick,
                    onCollapseClick = { isExpanded = false },
                    isBangla = isBangla,
                    haptics = haptics
                )
            }
        }
    }
}
