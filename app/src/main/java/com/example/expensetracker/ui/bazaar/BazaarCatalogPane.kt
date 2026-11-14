package com.example.expensetracker.ui.bazaar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PulseTabularText
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.DepartmentMeta

@Composable
fun BazaarCatalogPane(
    masterProducts: List<MasterProduct>,
    currentItems: List<MarketItem>,
    onAddOrIncrementProduct: (MasterProduct) -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    var searchQuery by remember { mutableStateOf("") }

    // Map of normalized name to count on current list
    val itemCountsOnList = remember(currentItems) {
        val map = mutableMapOf<String, Double>()
        for (item in currentItems) {
            val key = UnitConverterEngine.normalizeProductName(item.name)
            map[key] = (map[key] ?: 0.0) + item.quantity
        }
        map
    }

    val filteredProducts = remember(masterProducts, searchQuery) {
        if (searchQuery.isBlank()) {
            masterProducts
        } else {
            val queryClean = UnitConverterEngine.normalizeProductName(searchQuery)
            masterProducts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.normalizedName.contains(queryClean)
            }
        }
    }

    val groupedByDepartment = remember(filteredProducts) {
        filteredProducts.groupBy { it.category }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = if (isBangla) "পণ্য খুঁজুন (আলু, ডিম, তেল…)" else "Search items (potato, eggs…)",
                    style = typography.bodyMedium,
                    color = appColors.textMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = if (isBangla) "খুঁজুন" else "Search",
                    tint = appColors.textSecondary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = appColors.surfaceVariant,
                unfocusedContainerColor = appColors.surfaceVariant,
                focusedBorderColor = appColors.accent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            for (dept in ALL_DEPARTMENTS) {
                val deptProducts = groupedByDepartment[dept.key] ?: emptyList()
                if (deptProducts.isEmpty()) continue

                // Department Header
                item(span = { GridItemSpan(maxLineSpan) }, key = "dept_header_${dept.key}") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = "${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}",
                            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(deptProducts.size.toString()) else deptProducts.size.toString()})",
                            style = typography.bodySmall,
                            color = appColors.textMuted
                        )
                    }
                }

                items(deptProducts, key = { "mp_${it.id}_${it.normalizedName}" }) { product ->
                    val cleanKey = product.normalizedName
                    val countOnList = itemCountsOnList[cleanKey] ?: 0.0
                    val isOnList = countOnList > 0.0

                    CatalogCommodityTile(
                        product = product,
                        countOnList = countOnList,
                        isOnList = isOnList,
                        onClick = {
                            haptics.lightTick()
                            onAddOrIncrementProduct(product)
                        },
                        isBangla = isBangla,
                        useBengaliNumerals = useBengaliNumerals
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogCommodityTile(
    product: MasterProduct,
    countOnList: Double,
    isOnList: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    val tileBgColor by animateColorAsState(
        targetValue = if (isOnList) appColors.accent.copy(alpha = 0.08f) else appColors.surface,
        label = "tileBgColor"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tileBgColor,
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClickLabel = if (isBangla) "${product.name} যোগ করুন" else "Add ${product.name}",
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = "${product.name}, ${if (isOnList) "তালিকার মধ্যে $countOnList" else "যোগ করুন"}"
            }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Name
                Text(
                    text = product.name,
                    style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = appColors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Plus or Count Badge
                if (isOnList) {
                    val countStr = if (countOnList % 1.0 == 0.0) "${countOnList.toInt()}" else "$countOnList"
                    val formattedCount = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(countStr) else countStr
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(appColors.accent)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formattedCount,
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = appColors.onAccent
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(appColors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subline: Default Unit and Latest Price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = product.defaultUnit,
                    style = typography.bodySmall,
                    color = appColors.textMuted
                )

                if (product.latestUnitPrice > 0.0) {
                    val priceNum = if (product.latestUnitPrice % 1.0 == 0.0) {
                        "${product.latestUnitPrice.toInt()}"
                    } else {
                        "%.1f".format(product.latestUnitPrice)
                    }
                    val formattedPrice = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(priceNum) else priceNum
                    Text(
                        text = "৳$formattedPrice",
                        style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = appColors.textSecondary
                    )
                }
            }
        }
    }
}
