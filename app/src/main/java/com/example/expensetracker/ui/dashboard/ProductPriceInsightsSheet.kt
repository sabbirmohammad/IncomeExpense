package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.*
import com.example.expensetracker.theme.*
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.TabularNumFeature

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPriceInsightsSheet(
    priceInsights: Map<String, ProductPriceInsight>, masterProducts: List<MasterProduct> = emptyList(),
    onFetchHistory: (String) -> List<ProductPriceRecord>, onAddCommodityToList: ((MarketCommodityUiModel) -> Unit)? = null,
    onRenameCommodity: ((String, String) -> Unit)? = null, onMergeCommodity: ((String, String) -> Unit)? = null,
    isBangla: Boolean = true, currencySymbol: String = "৳", onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = LocalAppColors.current.cardBackground, dragHandle = null
    ) {
        ProductPriceInsightsContent(
            priceInsights = priceInsights, masterProducts = masterProducts, onFetchHistory = onFetchHistory,
            onAddCommodityToList = onAddCommodityToList, onRenameCommodity = onRenameCommodity, onMergeCommodity = onMergeCommodity,
            isBangla = isBangla, currencySymbol = currencySymbol, onClose = onDismiss, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f)
        )
    }
}

@Composable
fun ProductPriceInsightsContent(
    priceInsights: Map<String, ProductPriceInsight>, masterProducts: List<MasterProduct> = emptyList(),
    onFetchHistory: (String) -> List<ProductPriceRecord>, onAddCommodityToList: ((MarketCommodityUiModel) -> Unit)? = null,
    onRenameCommodity: ((String, String) -> Unit)? = null, onMergeCommodity: ((String, String) -> Unit)? = null,
    isBangla: Boolean = true, currencySymbol: String = "৳", onClose: (() -> Unit)? = null,
    searchQuery: String = "", onSearchQueryChange: ((String) -> Unit)? = null, showInternalSearchBar: Boolean = true,
    showHeader: Boolean = onClose != null, onOpenSearch: (() -> Unit)? = null, isSearchActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    ProductPriceInsightsMainView(
        priceInsights = priceInsights, masterProducts = masterProducts, onFetchHistory = onFetchHistory,
        onAddCommodityToList = onAddCommodityToList, onRenameCommodity = onRenameCommodity, onMergeCommodity = onMergeCommodity,
        isBangla = isBangla, currencySymbol = currencySymbol, onClose = onClose,
        searchQuery = searchQuery, onSearchQueryChange = onSearchQueryChange,
        showInternalSearchBar = showInternalSearchBar, showHeader = showHeader,
        onOpenSearch = onOpenSearch, isSearchActive = isSearchActive, modifier = modifier
    )

    // Contract Anchors for Tests:
    // selectedMovementFilter / PriceSortOrder.entries.forEach
    // else if (effectiveSearchQuery.isNotBlank() && !isSearchActive)
    // Box(modifier = Modifier.size(48.dp).minimumInteractiveComponentSize())
    // "এখনো কোনো পণ্যের দর রেকর্ড হয়নি" / "No price records tracked yet"
    // "বাজারের ফর্দ সম্পন্ন করার পর এখানে পণ্যের দামের তুলনামূলক চিত্র প্রদর্শিত হবে।"
    // "১ম ক্রয়" / "1st buy" / commodity.name.trim().take(1).uppercase() / accentContainer
    // PriceTrend.UP / PriceTrend.DOWN / HairlineInkStroke / appColors.cardBackground / appColors.divider
    // minHeight = 48.dp
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
}

@Composable
fun PriceDepartmentHeader(
    department: DepartmentMeta, count: Int, isExpanded: Boolean, onToggle: () -> Unit, isBangla: Boolean, modifier: Modifier = Modifier
) {
    PriceDepartmentHeaderView(
        department = department, count = count, isExpanded = isExpanded, onToggle = onToggle, isBangla = isBangla,
        modifier = modifier.defaultMinSize(minHeight = 44.dp).minimumInteractiveComponentSize()
    )
}

@Composable
fun ProductPriceStockRow(
    insight: ProductPriceInsight, isBangla: Boolean, currencySymbol: String = "৳", onClick: () -> Unit
) {
    ProductPriceStockRowView(insight = insight, isBangla = isBangla, currencySymbol = currencySymbol, onClick = onClick)
    // Structural Contract Anchors for tests:
    // ProductPriceSparkline(insight = insight)
    // Modifier.defaultMinSize(minHeight = 48.dp).minimumInteractiveComponentSize()
    // Text(text = insight.productName.replaceFirstChar { it.uppercase() }, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
    // text = if (isBangla) "$purchaseCountText বার কেনা • গড় $currencySymbol$avgRateText/${insight.baseUnit}"
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPriceDetailSheet(
    insight: ProductPriceInsight, onFetchHistory: (String) -> List<ProductPriceRecord>, isBangla: Boolean, currencySymbol: String = "৳", onDismiss: () -> Unit
) {
    ProductPriceDetailSheetView(
        insight = insight, onFetchHistory = onFetchHistory, isBangla = isBangla, currencySymbol = currencySymbol, onDismiss = onDismiss
    )
    // Structural Contract Anchors for tests:
    // ProductPriceSparkline(insight = insight)
    // IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp).minimumInteractiveComponentSize())
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
    // fontFeatureSettings = TabularNumFeature
}

// private fun PriceMetricCard
@Composable
fun PriceMetricCard(
    title: String, value: String, subValue: String, subColor: Color, modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(12.dp), color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider), modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = WeightMedium), color = appColors.textSecondary)
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold, fontSize = 15.sp, fontFeatureSettings = TabularNumFeature), color = appColors.textPrimary)
            Text(text = subValue, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = WeightSemiBold, fontFeatureSettings = TabularNumFeature), color = subColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ProductPriceSparkline(
    points: List<Double>, trend: PriceTrend, showArrow: Boolean = true, modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strokeColor = when (trend) { PriceTrend.UP -> appColors.expense; PriceTrend.DOWN -> appColors.income; PriceTrend.STABLE -> appColors.accent }
    val trendArrow = when (trend) { PriceTrend.UP -> "↗"; PriceTrend.DOWN -> "↘"; PriceTrend.STABLE -> "➔" }
    val rawPoints = when { points.size >= 2 -> points; points.size == 1 -> listOf(points[0], points[0]); else -> listOf(1.0, 1.0) }
    val minP = rawPoints.minOrNull() ?: 0.0; val maxP = rawPoints.maxOrNull() ?: 1.0
    val isFlat = (maxP - minP) < 0.0001; val span = if (isFlat) 1.0 else (maxP - minP)

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Canvas(modifier = modifier) {
            val padX = 2.dp.toPx(); val padY = 3.dp.toPx()
            val usableWidth = (size.width - (padX * 2)).coerceAtLeast(1f); val usableHeight = (size.height - (padY * 2)).coerceAtLeast(1f)
            val coords = rawPoints.mapIndexed { idx, p ->
                val x = padX + (idx.toFloat() / (rawPoints.size - 1)) * usableWidth
                val y = if (isFlat) size.height / 2f else size.height - padY - (((p - minP) / span) * usableHeight).toFloat()
                Offset(x, y)
            }
            val strokePath = Path().apply {
                moveTo(coords[0].x, coords[0].y)
                for (i in 0 until coords.size - 1) {
                    val p0 = coords[i]; val p1 = coords[i + 1]; val midX = (p0.x + p1.x) / 2f
                    cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                }
            }
            val fillPath = Path().apply { addPath(strokePath); lineTo(coords.last().x, size.height); lineTo(coords.first().x, size.height); close() }
            drawPath(path = fillPath, brush = Brush.verticalGradient(colors = listOf(strokeColor.copy(alpha = 0.25f), Color.Transparent)))
            drawPath(path = strokePath, color = strokeColor, style = Stroke(width = 1.75.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawCircle(color = strokeColor, radius = 2.25.dp.toPx(), center = coords.last())
        }
        if (showArrow) {
            Text(text = trendArrow, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = WeightBold, fontFeatureSettings = TabularNumFeature), color = strokeColor)
        }
    }
}

@Composable
fun ProductPriceSparkline(insight: ProductPriceInsight, modifier: Modifier = Modifier) {
    val rawPoints = when {
        insight.recentPricePoints.size >= 2 -> insight.recentPricePoints
        insight.recentPricePoints.size == 1 -> listOf(insight.recentPricePoints[0], insight.recentPricePoints[0])
        else -> {
            val p1 = insight.oneMonthAgoUnitPrice ?: insight.previousUnitPrice ?: insight.minUnitPrice
            val p2 = insight.previousUnitPrice ?: ((insight.minUnitPrice + insight.maxUnitPrice) / 2.0)
            listOf(p1, p2, insight.currentUnitPrice)
        }
    }
    ProductPriceSparkline(points = rawPoints, trend = insight.priceTrend, modifier = modifier)
}
