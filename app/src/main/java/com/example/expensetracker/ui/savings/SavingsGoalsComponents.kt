package com.example.expensetracker.ui.savings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Maps string icon keys to Compose ImageVector
 */
fun getSavingsGoalIcon(key: String): ImageVector {
    return when (key.lowercase(Locale.ROOT)) {
        "star" -> Icons.Outlined.Star
        "car" -> Icons.Outlined.DirectionsCar
        "flight" -> Icons.Outlined.Flight
        "home" -> Icons.Outlined.Home
        "phone" -> Icons.Outlined.Smartphone
        "laptop" -> Icons.Outlined.Laptop
        "school" -> Icons.Outlined.School
        "heart" -> Icons.Outlined.Favorite
        "gift" -> Icons.Outlined.CardGiftcard
        else -> Icons.Outlined.Savings
    }
}

/**
 * Parses color hex safely, with fallback to default theme accent
 */
fun parseGoalColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        ColorAccent
    }
}

private val AVAILABLE_GOAL_ICONS = listOf(
    "savings" to Icons.Outlined.Savings,
    "star" to Icons.Outlined.Star,
    "car" to Icons.Outlined.DirectionsCar,
    "flight" to Icons.Outlined.Flight,
    "home" to Icons.Outlined.Home,
    "phone" to Icons.Outlined.Smartphone,
    "laptop" to Icons.Outlined.Laptop,
    "school" to Icons.Outlined.School,
    "heart" to Icons.Outlined.Favorite,
    "gift" to Icons.Outlined.CardGiftcard
)

private val AVAILABLE_GOAL_COLORS = listOf(
    "#3B82F6", // Blue
    "#10B981", // Emerald
    "#8B5CF6", // Purple
    "#F59E0B", // Amber
    "#EC4899", // Pink
    "#06B6D4", // Cyan
    "#EF4444"  // Coral Red
)

/**
 * Hero summary card displaying overall savings goal metrics
 */
@Composable
fun SavingsSummaryCard(
    goals: List<SavingsGoal>,
    currencySymbol: String,
    isPrivacyMode: Boolean = false,
    onAddGoalClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val totalTarget = remember(goals) { goals.sumOf { it.targetAmount } }
    val totalSaved = remember(goals) { goals.sumOf { it.savedAmount } }
    val overallProgress = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toFloat().coerceIn(0f, 100f) else 0f
    val completedCount = remember(goals) { goals.count { it.isCompleted } }

    val animatedProgress by animateFloatAsState(
        targetValue = overallProgress / 100f,
        label = "overallSavingsProgress"
    )

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 18.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Savings,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.savingsGoalsTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = if (strings.isBangla) {
                                "${convertToBengaliDigits(completedCount.toString())}টি লক্ষ্য পূরণ / ${convertToBengaliDigits(goals.size.toString())}টি মোট"
                            } else {
                                "$completedCount completed / ${goals.size} total"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorAccent.copy(alpha = 0.12f),
                    modifier = Modifier.clickable { onAddGoalClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.Add, null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                        Text(
                            text = if (strings.isBangla) "নতুন লক্ষ্য" else "New Goal",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total figures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = strings.savedAmount,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isPrivacyMode) "••••••" else formatCurrency(totalSaved, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorSuccessIncome
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${strings.targetAmount}: ${if (isPrivacyMode) "••••••" else formatCurrency(totalTarget, currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                    Text(
                        text = if (strings.isBangla) {
                            "${convertToBengaliDigits(String.format(Locale.US, "%.1f", overallProgress))}% সঞ্চিত"
                        } else {
                            "${String.format(Locale.US, "%.1f", overallProgress)}% Saved"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ColorAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorAccent,
                trackColor = ColorBorder
            )
        }
    }
}

/**
 * Individual Savings Goal Item Card
 */
/**
 * Individual Savings Goal Item Card
 */
@Composable
fun SavingsGoalCard(
    goal: SavingsGoal,
    currencySymbol: String,
    isPrivacyMode: Boolean = false,
    geminiApiKey: String? = null,
    monthlyIncome: Double = 0.0,
    monthlyExpense: Double = 0.0,
    onAddContribution: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    var showMenu by remember { mutableStateOf(false) }
    val goalColor = remember(goal.colorHex) { parseGoalColor(goal.colorHex) }
    val icon = remember(goal.icon) { getSavingsGoalIcon(goal.icon) }
    val dateFormat = remember(strings.isBangla) { SimpleDateFormat("d MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    val animatedProgress by animateFloatAsState(
        targetValue = (goal.progressPercentage / 100f).coerceIn(0f, 1f),
        label = "goalProgress_${goal.id}"
    )

    val daysRemaining = remember(goal.targetDateMillis) {
        goal.targetDateMillis?.let { targetMillis ->
            val diff = targetMillis - System.currentTimeMillis()
            val days = (diff / (1000 * 60 * 60 * 24)).toInt()
            days
        }
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 16.dp
    ) {
        Column {
            // Header Row: Icon + Title + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(goalColor.copy(alpha = 0.15f))
                            .border(1.dp, goalColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = goalColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (goal.notes.isNotBlank()) {
                            Text(
                                text = goal.notes,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge: Completed vs Remaining Days
                    if (goal.isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorSuccessIncome.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (strings.isBangla) "অর্জিত 🎉" else "Reached 🎉",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorSuccessIncome
                            )
                        }
                    } else if (daysRemaining != null) {
                        val isOverdue = daysRemaining < 0
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isOverdue) ColorDangerExpense.copy(alpha = 0.15f)
                                    else ColorAccent.copy(alpha = 0.12f)
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isOverdue) {
                                    if (strings.isBangla) "মেয়াদোত্তীর্ণ" else "Overdue"
                                } else {
                                    if (strings.isBangla) "${convertToBengaliDigits(daysRemaining.toString())} দিন বাকি"
                                    else "$daysRemaining days left"
                                },
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isOverdue) ColorDangerExpense else ColorAccent
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, null, tint = ColorTextSecondary, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(ColorSurface)
                        ) {

                            DropdownMenuItem(
                                text = { Text(strings.addSavingsContribution, color = ColorTextPrimary) },
                                leadingIcon = { Icon(Icons.Outlined.Savings, null, tint = goalColor) },
                                onClick = {
                                    showMenu = false
                                    onAddContribution()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (strings.isBangla) "লক্ষ্য এডিট করুন" else "Edit Goal", color = ColorTextPrimary) },
                                leadingIcon = { Icon(Icons.Outlined.Edit, null, tint = ColorTextSecondary) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.delete, color = ColorDangerExpense) },
                                leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = ColorDangerExpense) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Progress Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(strings.savedAmount, fontSize = 11.sp, color = ColorTextMuted)
                    Text(
                        text = if (isPrivacyMode) "••••••" else formatCurrency(goal.savedAmount, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (goal.isCompleted) ColorSuccessIncome else ColorTextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${strings.targetAmount}: ${if (isPrivacyMode) "••••••" else formatCurrency(goal.targetAmount, currencySymbol)}",
                        fontSize = 11.sp,
                        color = ColorTextSecondary
                    )
                    Text(
                        text = if (strings.isBangla) {
                            "${convertToBengaliDigits(String.format(Locale.US, "%.0f", goal.progressPercentage))}%"
                        } else {
                            "${String.format(Locale.US, "%.0f", goal.progressPercentage)}%"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = goalColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = goalColor,
                trackColor = ColorBorder
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (goal.targetDateMillis != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = ColorTextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val formattedDate = dateFormat.format(Date(goal.targetDateMillis))
                        Text(
                            text = if (strings.isBangla) convertToBengaliDigits(formattedDate) else formattedDate,
                            fontSize = 11.sp,
                            color = ColorTextSecondary
                        )
                    }
                } else {
                    Text(
                        text = if (goal.remainingAmount > 0) {
                            "${if (strings.isBangla) "বাকি" else "Left"}: ${if (isPrivacyMode) "••••" else formatCurrency(goal.remainingAmount, currencySymbol, globalBengaliNumeralsEnabled || strings.isBangla)}"
                        } else {
                            strings.goalReachedCelebration
                        },
                        fontSize = 11.sp,
                        color = if (goal.remainingAmount <= 0) ColorSuccessIncome else ColorTextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // AI Strategy Button


                    // Quick Contribution Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = goalColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, goalColor.copy(alpha = 0.35f)),
                        modifier = Modifier.clickable { onAddContribution() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Outlined.Add, null, tint = goalColor, modifier = Modifier.size(13.dp))
                            Text(
                                text = if (strings.isBangla) "জমানো" else "Contribute",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = goalColor
                            )
                        }
                    }
                }
            }
        }
    }


}




/**
 * Add or Edit Savings Goal Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSavingsGoalSheet(
    goal: SavingsGoal?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Long?, String, String, String) -> Unit
) {
    val strings = LocalAppStrings.current
    var title by remember { mutableStateOf(goal?.title ?: "") }
    var targetAmountText by remember {
        mutableStateOf(goal?.targetAmount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var initialSavedText by remember {
        mutableStateOf(goal?.savedAmount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var targetDateMillis by remember { mutableStateOf(goal?.targetDateMillis) }
    var selectedIcon by remember { mutableStateOf(goal?.icon ?: "savings") }
    var selectedColorHex by remember { mutableStateOf(goal?.colorHex ?: "#3B82F6") }
    var notes by remember { mutableStateOf(goal?.notes ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val dateFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (goal != null) (if (strings.isBangla) "সঞ্চয় লক্ষ্য সম্পাদনা" else "Edit Savings Goal") else strings.addSavingsGoal,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = ColorTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Goal Title
            Text(strings.goalTitle, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(if (strings.isBangla) "যেমন: নতুন বাইক, জরুরি তহবিল, হজ্ব" else "e.g. New Bike, Emergency Fund, Laptop", color = ColorTextMuted) },
                singleLine = true,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Target Amount
            Text("${strings.targetAmount} ($currencySymbol)", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = targetAmountText,
                onValueChange = { targetAmountText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " } },
                placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = ColorTextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            CalculatorPreviewBadge(
                expression = targetAmountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    targetAmountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = targetAmountText,
                onExpressionChanged = { targetAmountText = it },
                onApplyResult = { evaluated ->
                    targetAmountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Initial Saved Amount (Optional)
            if (goal == null) {
                Text("${strings.savedAmount} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = initialSavedText,
                    onValueChange = { initialSavedText = it.filter { c -> c.isDigit() || c == '.' } },
                    placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = ColorTextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(SmallElementRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ColorBackground,
                        unfocusedContainerColor = ColorBackground,
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = ColorBorder,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Target Date Selector
            Text("${strings.targetDate} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = ColorBackground,
                border = BorderStroke(1.dp, ColorBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (targetDateMillis != null) {
                                val dateStr = dateFormat.format(Date(targetDateMillis!!))
                                if (strings.isBangla) convertToBengaliDigits(dateStr) else dateStr
                            } else (if (strings.isBangla) "টার্গেট তারিখ বেছে নিন" else "Pick a target date"),
                            fontSize = 13.sp,
                            color = if (targetDateMillis != null) ColorTextPrimary else ColorTextMuted
                        )
                    }
                    if (targetDateMillis != null) {
                        Text(
                            text = strings.clear,
                            fontSize = 11.sp,
                            color = ColorDangerExpense,
                            modifier = Modifier.clickable { targetDateMillis = null }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Icon Picker
            Text(if (strings.isBangla) "আইকন নির্বাচন করুন" else "Select Icon", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AVAILABLE_GOAL_ICONS.forEach { (key, iconVec) ->
                    val isSelected = selectedIcon == key
                    val currentColor = parseGoalColor(selectedColorHex)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) currentColor.copy(alpha = 0.25f) else ColorBackground)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) currentColor else ColorBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedIcon = key },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVec,
                            contentDescription = null,
                            tint = if (isSelected) currentColor else ColorTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Color Picker
            Text(if (strings.isBangla) "রং নির্বাচন করুন" else "Select Color", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AVAILABLE_GOAL_COLORS.forEach { hex ->
                    val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                    val color = parseGoalColor(hex)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColorHex = hex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notes
            Text("${strings.note} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(if (strings.isBangla) "লক্ষ্য সম্পর্কিত বিবরণ..." else "Notes or motivation...", color = ColorTextMuted) },
                maxLines = 2,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = ColorDangerExpense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = if (strings.isBangla) "লক্ষ্যের একটি নাম লিখুন" else "Please enter a goal title"
                        return@Button
                    }
                    val targetAmt = targetAmountText.toParsedAmountOrNull()
                    if (targetAmt == null || targetAmt <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    val initAmt = initialSavedText.toParsedAmountOrNull() ?: 0.0
                    onSave(title.trim(), targetAmt, initAmt, targetDateMillis, selectedIcon, selectedColorHex, notes.trim())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = parseGoalColor(selectedColorHex))
            ) {
                Text(
                    text = strings.save,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        val initialUtcMillis = remember(targetDateMillis) {
            val localCal = Calendar.getInstance().apply {
                timeInMillis = targetDateMillis ?: (System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
            }
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                clear()
                set(
                    localCal.get(Calendar.YEAR),
                    localCal.get(Calendar.MONTH),
                    localCal.get(Calendar.DAY_OF_MONTH)
                )
            }.timeInMillis
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialUtcMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedUtcMillis ->
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = selectedUtcMillis
                        }
                        val updatedLocal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                            set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }
                        targetDateMillis = updatedLocal.timeInMillis
                    }
                    showDatePicker = false
                }) {
                    Text(strings.confirm, color = ColorAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Add Contribution to Savings Goal Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsContributionSheet(
    goal: SavingsGoal,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, Long?) -> Unit
) {
    val strings = LocalAppStrings.current
    var amountText by remember { mutableStateOf("") }
    var deductFromAccount by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val goalColor = remember(goal.colorHex) { parseGoalColor(goal.colorHex) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.addSavingsContribution,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = ColorTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle with goal target
            Text(
                text = "${goal.title} • ${if (strings.isBangla) "বাকি" else "Remaining"}: ${formatCurrency(goal.remainingAmount, currencySymbol)}",
                fontSize = 12.sp,
                color = ColorTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Field
            Text("${strings.amount} ($currencySymbol)", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }
                    if (errorMessage != null) errorMessage = null
                },
                placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = ColorTextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = goalColor,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            CalculatorPreviewBadge(
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = amountText,
                onExpressionChanged = {
                    amountText = it
                    if (errorMessage != null) errorMessage = null
                },
                onApplyResult = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Shortcut Chips (+500, +1000, +2000, +5000, Full Remaining)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(500, 1000, 2000, 5000).forEach { shortcut ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorBackground,
                        border = BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier.clickable {
                            val curr = amountText.toDoubleOrNull() ?: 0.0
                            val next = curr + shortcut
                            amountText = next.toLong().toString()
                        }
                    ) {
                        Text(
                            text = "+$shortcut",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                if (goal.remainingAmount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = goalColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, goalColor.copy(alpha = 0.35f)),
                        modifier = Modifier.clickable {
                            amountText = if (goal.remainingAmount % 1.0 == 0.0) goal.remainingAmount.toLong().toString()
                            else String.format(Locale.US, "%.2f", goal.remainingAmount)
                        }
                    ) {
                        Text(
                            text = if (strings.isBangla) "বাকি পুরোটা" else "Full Remaining",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = goalColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Deduct from wallet / account option
            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(SmallElementRadius),
                    color = ColorBackground,
                    border = BorderStroke(1.dp, ColorBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (strings.isBangla) "ওয়ালেট থেকে কেটে নিন" else "Deduct from Account",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = if (strings.isBangla) "টাকাটি নির্বাচিত অ্যাকাউন্ট থেকে বিয়োগ হবে" else "Balance will be deducted from chosen account",
                                    fontSize = 11.sp,
                                    color = ColorTextSecondary
                                )
                            }
                            Switch(
                                checked = deductFromAccount,
                                onCheckedChange = { deductFromAccount = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = goalColor)
                            )
                        }

                        if (deductFromAccount) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("${strings.fromAccount}:", fontSize = 11.sp, color = ColorTextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                accounts.forEach { acc ->
                                    val isSelected = selectedAccountId == acc.id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) goalColor else ColorSurface,
                                        border = BorderStroke(1.dp, if (isSelected) goalColor else ColorBorder),
                                        modifier = Modifier.clickable { selectedAccountId = acc.id }
                                    ) {
                                        Text(
                                            text = "${acc.getLocalizedName(strings.isBangla)} (${formatCurrency(acc.currentBalance, currencySymbol)})",
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color.White else ColorTextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = ColorDangerExpense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toParsedAmountOrNull()
                    if (amt == null || amt <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    val fromAccId = if (deductFromAccount) selectedAccountId else null
                    onConfirm(amt, fromAccId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = goalColor)
            ) {
                Text(
                    text = strings.confirm,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
