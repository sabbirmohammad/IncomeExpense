package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.CardPadding
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorBorderDashed
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.theme.HairlineDividerStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import java.text.NumberFormat
import java.util.Locale

@Volatile
var globalBengaliNumeralsEnabled: Boolean = false

private val currencyFormatterThreadLocal = object : ThreadLocal<NumberFormat>() {
    override fun initialValue(): NumberFormat {
        return NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
    }
}

fun convertToBengaliDigits(input: String): String {
    val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val sb = StringBuilder(input.length)
    for (ch in input) {
        if (ch in '0'..'9') {
            sb.append(bengaliDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

fun formatCurrency(
    amount: Double,
    symbol: String,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled
): String {
    val formatter = currencyFormatterThreadLocal.get()!!
    val formatted = formatter.format(amount)
    val displayAmount = if (useBengaliNumerals) convertToBengaliDigits(formatted) else formatted
    return "$symbol $displayAmount"
}

/**
 * Standard Surface Card per specification:
 * #141826 surface, 16px corner radius, 16px padding, #232838 border.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    cornerRadius: Dp = CardCornerRadius,
    padding: Dp = CardPadding,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val resolvedBg = if (backgroundColor != Color.Unspecified) backgroundColor else LocalAppColors.current.surface
    val resolvedBorder = if (borderColor != Color.Unspecified) borderColor else LocalAppColors.current.border

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = resolvedBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, resolvedBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

// Backwards compatibility alias
@Composable
fun OneUiCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        onClick = onClick,
        content = content
    )
}

/**
 * Category icon badge per spec:
 * 38x38px, 10px radius, colored background at ~15% tint of category color,
 * icon in full-strength category color.
 */
@Composable
fun CategoryIconBadge(
    iconName: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    val icon = CategoryIconHelper.getIcon(iconName)
    val color = CategoryIconHelper.parseColor(colorHex)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(SmallElementRadius))
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Compatibility alias for existing screens
@Composable
fun CategoryBadge(
    iconName: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Int = 38
) {
    CategoryIconBadge(
        iconName = iconName,
        colorHex = colorHex,
        modifier = modifier,
        size = size.dp
    )
}

/**
 * Metric chip per spec:
 * Tinted background (income = dark green tint, expense = dark red tint),
 * small icon + label on top row, value below. Used in pairs for income/expense.
 */
@Composable
fun MetricChip(
    type: TransactionType,
    amount: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled,
    labelOverride: String? = null,
    isPrivacyMode: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val haptics = rememberAppHaptics()
    val isIncome = type == TransactionType.INCOME
    val tintColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense
    val bgTint = tintColor.copy(alpha = 0.12f)
    val icon = if (isIncome) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward
    val strings = LocalAppStrings.current
    val label = labelOverride ?: if (isIncome) strings.income else strings.expense

    val animatedAmount by animateFloatAsState(
        targetValue = amount.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "metricAmountAnimation"
    )

    val displayAmountText = if (isPrivacyMode) {
        "$currencySymbol\u00A0••••"
    } else {
        formatCurrency(animatedAmount.toDouble(), currencySymbol, useBengaliNumerals)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        bgTint,
                        bgTint.copy(alpha = 0.05f)
                    )
                )
            )
            .border(1.dp, tintColor.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        haptics.lightTick()
                        onClick()
                    }
                } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(tintColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp
                        ),
                        color = tintColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (onClick != null) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(tintColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add $label",
                            tint = tintColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = displayAmountText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                ),
                color = LocalAppColors.current.textPrimary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Progress bar per spec:
 * 6-7px height, full track in border color #232838, fill in success green #3ddc97, rounded ends.
 */
@Composable
fun AppProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 7.dp,
    trackColor: Color = ColorBorder,
    fillColor: Color = ColorSuccessIncome
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(500),
        label = "progressBar"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(fillColor)
        )
    }
}

/**
 * Filter chip / Segmented control per spec:
 * Pill-shaped, active state = solid accent background #3b6ef5 + white text;
 * inactive = card-color background #141826 + secondary text #8b8f9c.
 */
@Composable
fun PillFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = if (selected) ColorAccent else ColorSurface,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (selected) Color.White else ColorTextSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

/**
 * Dropdown filter chip with icon, text, and dropdown arrow indicator.
 * Active state shows subtle tinted background and colored border/text.
 */
@Composable
fun DropdownFilterChip(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    activeColor: Color = ColorAccent
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) activeColor.copy(alpha = 0.16f) else ColorSurface,
        border = BorderStroke(
            1.dp,
            if (isActive) activeColor.copy(alpha = 0.55f) else ColorBorder
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) activeColor else ColorTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isActive) (if (activeColor == ColorAccent) ColorAccent else activeColor) else ColorTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = if (isActive) activeColor else ColorTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * List row per spec:
 * Icon badge (left) + title/meta (middle, flexible) + amount (right, colored by type).
 * 10px vertical padding.
 */
@Composable
fun ListRow(
    iconName: String,
    categoryColorHex: String,
    title: String,
    meta: String,
    amountText: String,
    amountColor: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    paymentMethod: PaymentMethod? = null,
    paymentMethodName: String? = null,
    hasReceipt: Boolean = false
) {
    val isBangla = LocalAppStrings.current.isBangla
    val methodDisplayName = if (paymentMethodName != null) {
        getLocalizedAccountName(paymentMethodName, isBangla)
    } else {
        paymentMethod?.getDisplayName(isBangla)
    }
    val methodColor = paymentMethod?.let { getPaymentMethodColor(it) } ?: ColorTextSecondary
    val badgeBgColor = if (paymentMethod != null) methodColor.copy(alpha = 0.14f) else ColorBorder

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconBadge(
                iconName = iconName,
                colorHex = categoryColorHex
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = ColorTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ColorTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!methodDisplayName.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBgColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = methodDisplayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = methodColor,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    if (hasReceipt) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ColorAccent.copy(alpha = 0.14f))
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = "Receipt",
                                tint = ColorAccent,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = amountText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            color = amountColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * 0.5px hairline divider per spec.
 */
@Composable
fun HairlineDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val resolvedColor = if (color != Color.Unspecified) color else LocalAppColors.current.border
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = HairlineDividerStroke,
        color = resolvedColor
    )
}

/**
 * Empty state card per spec:
 * Dashed border #2a2f40, centered icon badge, bold one-line headline,
 * one-line explanation, solid accent CTA button below.
 */
@Composable
fun EmptyStateCard(
    icon: ImageVector = Icons.Outlined.ReceiptLong,
    headline: String,
    explanation: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                )
                drawRoundRect(
                    color = colors.border,
                    cornerRadius = CornerRadius(CardCornerRadius.toPx(), CardCornerRadius.toPx()),
                    style = stroke
                )
            }
            .clip(RoundedCornerShape(CardCornerRadius))
            .background(colors.surface)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(SmallElementRadius))
                    .background(ColorAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ColorAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Screen Header per spec:
 * Greeting ("Hello, [Name]") + date, small circular avatar button top-right (opens profile).
 */
fun getAvatarIcon(index: Int): ImageVector {
    return when (index) {
        1 -> Icons.Outlined.Person
        2 -> Icons.Outlined.SentimentSatisfied
        3 -> Icons.Outlined.Star
        4 -> Icons.Outlined.RocketLaunch
        5 -> Icons.Outlined.Work
        else -> Icons.Outlined.Person
    }
}

/**
 * Modern circular hamburger button to open side drawer
 */
@Composable
fun DrawerMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(LocalAppColors.current.surface)
            .border(1.dp, LocalAppColors.current.border, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Open side menu",
            tint = LocalAppColors.current.textPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Header per spec:
 * Menu icon (optional) + Greeting ("Hello, [Name]") + date, small circular avatar button top-right (opens profile).
 */
@Composable
fun OneUiHeader(
    userName: String,
    dateText: String,
    onAvatarClick: () -> Unit,
    avatarIndex: Int = 0,
    initials: String = "",
    onMenuClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onMenuClick != null) {
                DrawerMenuButton(onClick = onMenuClick)
            }
            val strings = LocalAppStrings.current
            Column {
                Text(
                    text = "${strings.greeting}, $userName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = ColorTextSecondary
                )
            }
        }

        ProfileAvatarButton(
            onClick = onAvatarClick,
            avatarIndex = avatarIndex,
            initials = initials
        )
    }
}

/**
 * Small circular avatar button top-right (opens profile & settings)
 */
@Composable
fun ProfileAvatarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarIndex: Int = 0,
    initials: String = ""
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(ColorAccent.copy(alpha = 0.15f))
            .border(1.dp, ColorAccent.copy(alpha = 0.5f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (avatarIndex == 0 && initials.isNotBlank()) {
            Text(
                text = initials.take(2),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorAccent
            )
        } else {
            Icon(
                imageVector = getAvatarIcon(avatarIndex),
                contentDescription = "Profile and Settings",
                tint = ColorAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Responsive Hero Balance:
 * Adapts typography dynamically to available card width and number length.
 * Enforces single-line presentation (maxLines = 1, softWrap = false) with non-breaking space
 * between currency symbol and number so line wrapping can NEVER occur.
 * Currency symbol matches the amount's font size and weight.
 * Turns red (ColorDangerExpense) when amount is negative.
 */
@Composable
fun ResponsiveHeroBalance(
    amount: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled,
    maxFontSize: TextUnit = 50.sp,
    minFontSize: TextUnit = 22.sp,
    isPrivacyMode: Boolean = false
) {
    val animatedAmount by animateFloatAsState(
        targetValue = amount.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "heroAmountAnimation"
    )

    val formattedNumber = remember(animatedAmount, amount, useBengaliNumerals, isPrivacyMode) {
        if (isPrivacyMode) {
            "• • • • • •"
        } else {
            val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).apply {
                minimumFractionDigits = 0
                maximumFractionDigits = 2
            }
            val formatted = formatter.format(animatedAmount.toDouble())
            if (useBengaliNumerals) convertToBengaliDigits(formatted) else formatted
        }
    }

    val isNegative = amount < 0 && !isPrivacyMode
    val balanceColor = if (isNegative) ColorDangerExpense else ColorTextPrimary

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val availableWidthDp = maxWidth.value
        val totalChars = formattedNumber.length + 1.8f

        // Initial responsive calculation: ensure (totalChars * charWidth) <= availableWidthDp
        val calculatedSp = (availableWidthDp / (totalChars * 0.58f))
        val initialSize = calculatedSp.coerceIn(minFontSize.value, maxFontSize.value).sp

        var currentFontSize by remember(amount, availableWidthDp) {
            mutableStateOf(initialSize)
        }
        var readyToDraw by remember(amount, availableWidthDp) {
            mutableStateOf(true)
        }

        val annotatedBalance = remember(formattedNumber, amount, currencySymbol, currentFontSize, balanceColor) {
            buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontSize = currentFontSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.0).sp,
                        color = balanceColor
                    )
                ) {
                    append(currencySymbol)
                }
                append("\u00A0") // Non-breaking space
                withStyle(
                    SpanStyle(
                        fontSize = currentFontSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.2).sp,
                        color = balanceColor
                    )
                ) {
                    append(formattedNumber)
                }
            }
        }

        Text(
            text = annotatedBalance,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    if (readyToDraw) drawContent()
                },
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth && currentFontSize.value > minFontSize.value) {
                    readyToDraw = false
                    currentFontSize = (currentFontSize.value - 2f).coerceAtLeast(minFontSize.value).sp
                } else {
                    readyToDraw = true
                }
            }
        )
    }
}

/**
 * Balance Card per Home Screen specification:
 * - Label "Total net balance" (12px secondary)
 * - Hero value (32-34px / 500, white) - single largest element on screen
 * - Two metric chips side by side: Income (green tint) and Expense (red tint)
 * - Budget progress: label row ("Budget · X% used" / "spent / limit") + progress bar
 */
@Composable
fun BalanceCard(
    netBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    budgetAmount: Double,
    budgetSpent: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || LocalAppStrings.current.isBangla,
    savingsRate: Int? = null,
    totalWalletBalance: Double? = null,
    periodLabel: String? = null,
    selectedTimeFilter: TimeFilter? = null,
    onTimeFilterSelected: ((TimeFilter) -> Unit)? = null,
    isPrivacyMode: Boolean = false,
    onTogglePrivacyMode: (() -> Unit)? = null,
    onIncomeClick: (() -> Unit)? = null,
    onExpenseClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    var showWalletAsHero by remember { mutableStateOf(totalWalletBalance != null) }

    val displayedHero = if (showWalletAsHero && totalWalletBalance != null) totalWalletBalance else netBalance
    val currentHeaderLabel = if (showWalletAsHero && totalWalletBalance != null) {
        strings.totalWalletBalance
    } else {
        strings.totalBalance
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(LocalAppColors.current.border, LocalAppColors.current.border.copy(alpha = 0.5f))
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            LocalAppColors.current.surface,
                            LocalAppColors.current.surfaceVariant
                        )
                    )
                )
                .padding(top = 34.dp, bottom = 32.dp, start = 22.dp, end = 22.dp)
        ) {
            Column {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header row: Interactive title pill + Modern Privacy Toggle (Eye button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LocalAppColors.current.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = 0.8f)),
                            modifier = Modifier
                                .then(
                                    if (totalWalletBalance != null) {
                                        Modifier.clickable {
                                             haptics.lightTick()
                                             showWalletAsHero = !showWalletAsHero
                                        }
                                    } else Modifier
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (displayedHero < 0 && !isPrivacyMode) ColorDangerExpense else ColorAccent)
                                )
                                Text(
                                    text = currentHeaderLabel,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.2.sp
                                    ),
                                    color = ColorTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                if (totalWalletBalance != null) {
                                    Icon(
                                        imageVector = Icons.Outlined.SwapHoriz,
                                        contentDescription = "Toggle view",
                                        tint = ColorTextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        if (onTogglePrivacyMode != null) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isPrivacyMode) ColorAccent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        1.dp,
                                        if (isPrivacyMode) ColorAccent.copy(alpha = 0.45f) else LocalAppColors.current.border,
                                        CircleShape
                                    )
                                    .clickable {
                                        haptics.lightTick()
                                        onTogglePrivacyMode()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (isPrivacyMode) strings.showBalance else strings.hideBalance,
                                    tint = if (isPrivacyMode) ColorAccent else LocalAppColors.current.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedContent(
                        targetState = displayedHero,
                        label = "heroBalance",
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) { balance ->
                        ResponsiveHeroBalance(
                            amount = balance,
                            currencySymbol = currencySymbol,
                            useBengaliNumerals = useBengaliNumerals,
                            isPrivacyMode = isPrivacyMode,
                            maxFontSize = 52.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Two metric chips side by side with interactive click
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricChip(
                        type = TransactionType.INCOME,
                        amount = totalIncome,
                        currencySymbol = currencySymbol,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode,
                        modifier = Modifier.weight(1f),
                        onClick = onIncomeClick
                    )
                    MetricChip(
                        type = TransactionType.EXPENSE,
                        amount = totalExpense,
                        currencySymbol = currencySymbol,
                        useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = isPrivacyMode,
                        modifier = Modifier.weight(1f),
                        onClick = onExpenseClick
                    )
                }



                // Budget progress row + progress bar
                if (budgetAmount > 0) {
                    Spacer(modifier = Modifier.height(20.dp))
                    val usedRatio = (budgetSpent / budgetAmount).toFloat()
                    val usedPct = (usedRatio * 100).toInt()
                    val formattedPct = if (useBengaliNumerals) convertToBengaliDigits(usedPct.toString()) else usedPct.toString()
                    val progressFillColor = when {
                        usedRatio > 0.95f -> ColorDangerExpense
                        usedRatio > 0.8f -> ColorWarningBills
                        else -> ColorSuccessIncome
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${strings.navBudgets} · $formattedPct% ${strings.budgetUsedPercent}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextSecondary
                        )
                        Text(
                            text = "${formatCurrency(budgetSpent, currencySymbol, useBengaliNumerals)} / ${formatCurrency(budgetAmount, currencySymbol, useBengaliNumerals)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AppProgressBar(
                        progress = usedRatio,
                        height = 8.dp,
                        fillColor = progressFillColor
                    )
                }
            }
        }
    }
}

