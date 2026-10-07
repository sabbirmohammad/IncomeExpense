package com.example.expensetracker.ui.budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.AppProgressBar
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.ConfettiCelebration
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.ProfileAvatarButton
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.savings.AddEditSavingsGoalSheet
import com.example.expensetracker.ui.savings.AddSavingsContributionSheet
import com.example.expensetracker.ui.savings.SavingsGoalCard
import com.example.expensetracker.ui.savings.SavingsSummaryCard
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val budgetStatuses by viewModel.budgetStatuses.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedBudgetToEdit by remember { mutableStateOf<Budget?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Budget?>(null) }

    val selectedTab by viewModel.selectedTab.collectAsState()
    val savingsGoals by viewModel.savingsGoals.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()
    val feedbackController = LocalFeedbackController.current
    val haptics = rememberAppHaptics()

    var showAddSavingsGoalSheet by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalForContribution by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalToDelete by remember { mutableStateOf<SavingsGoal?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
    val overallStatus = budgetStatuses.find { it.budget.categoryId == null }
    val categoryBudgetStatuses = budgetStatuses.filter { it.budget.categoryId != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
            .padding(horizontal = 16.dp)
    ) {
        // Pinned Header per spec: screen title (18px / 500) + Privacy Toggle + Quick Add + Profile Avatar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (onMenuClick != null) {
                    DrawerMenuButton(onClick = onMenuClick)
                } else if (onNavigateBack != null) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorTextPrimary
                        )
                    }
                }
                Text(
                    text = if (selectedTab == BudgetScreenTab.BUDGETS) strings.budgetTitle else strings.savingsGoalsTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Privacy Toggle Button
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        viewModel.togglePrivacyMode()
                    }
                ) {
                    Icon(
                        imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Toggle Privacy",
                        tint = if (isPrivacyMode) ColorAccent else ColorTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (onNavigateToSettings != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    ProfileAvatarButton(onClick = onNavigateToSettings)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Segmented Tabs: Budgets vs Savings Goals
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ColorSurface,
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Tab 1: Budgets
                val isBudgetsSelected = selectedTab == BudgetScreenTab.BUDGETS
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (isBudgetsSelected) ColorAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            viewModel.setSelectedTab(BudgetScreenTab.BUDGETS)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.budgetTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isBudgetsSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isBudgetsSelected) Color.White else ColorTextSecondary
                        )
                    }
                }

                // Tab 2: Savings Goals
                val isSavingsSelected = selectedTab == BudgetScreenTab.SAVINGS_GOALS
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (isSavingsSelected) ColorAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            viewModel.setSelectedTab(BudgetScreenTab.SAVINGS_GOALS)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.savingsGoalsTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isSavingsSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSavingsSelected) Color.White else ColorTextSecondary
                        )
                        if (savingsGoals.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSavingsSelected) Color.White.copy(alpha = 0.25f) else ColorAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (strings.isBangla) convertToBengaliDigits(savingsGoals.size.toString()) else savingsGoals.size.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSavingsSelected) Color.White else ColorAccent
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == BudgetScreenTab.BUDGETS) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // Overall Monthly Budget Status Card
            item {
                AppCard {
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
                                    .clip(RoundedCornerShape(SmallElementRadius))
                                    .background(ColorAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = strings.overallMonthlyBudget,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                selectedBudgetToEdit = overallStatus?.budget
                                showAddDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit overall budget",
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (overallStatus != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = strings.spent,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = ColorTextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatCurrency(overallStatus.spentAmount, userProfile.currencySymbol),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (overallStatus.isOverBudget) ColorDangerExpense else ColorSuccessIncome
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = strings.targetLimit,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = ColorTextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatCurrency(overallStatus.budget.amount, userProfile.currencySymbol),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val statusFillColor = when {
                            overallStatus.isOverBudget -> ColorDangerExpense
                            overallStatus.progressPercent > 0.8f -> ColorWarningBills
                            else -> ColorSuccessIncome
                        }

                        AppProgressBar(
                            progress = overallStatus.progressPercent,
                            height = 7.dp,
                            fillColor = statusFillColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (overallStatus.isOverBudget)
                                    "${strings.overBudgetBy} ${formatCurrency(overallStatus.spentAmount - overallStatus.budget.amount, userProfile.currencySymbol)}"
                                else
                                    if (strings.isBangla) "${(overallStatus.progressPercent * 100).toInt()}% ব্যবহৃত" else "${(overallStatus.progressPercent * 100).toInt()}% used",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (overallStatus.isOverBudget) ColorDangerExpense else ColorTextSecondary
                            )
                            Text(
                                text = if (strings.isBangla) "${formatCurrency(overallStatus.remainingAmount, userProfile.currencySymbol)} বাকি" else "${formatCurrency(overallStatus.remainingAmount, userProfile.currencySymbol)} left",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }

                        val calendar = remember { java.util.Calendar.getInstance() }
                        val maxDays = remember { calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) }
                        val curDay = remember { calendar.get(java.util.Calendar.DAY_OF_MONTH) }
                        val daysLeft = (maxDays - curDay + 1).coerceAtLeast(1)
                        val dailySafeSpend = (overallStatus.remainingAmount / daysLeft).coerceAtLeast(0.0)

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (overallStatus.isOverBudget) ColorDangerExpense.copy(alpha = 0.1f) else ColorAccent.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, if (overallStatus.isOverBudget) ColorDangerExpense.copy(alpha = 0.25f) else ColorAccent.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarToday,
                                        contentDescription = null,
                                        tint = if (overallStatus.isOverBudget) ColorDangerExpense else ColorAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (strings.isBangla) "দৈনিক নিরাপদ খরচ:" else "Daily Safe Spend:",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = ColorTextSecondary
                                    )
                                }
                                Text(
                                    text = if (overallStatus.isOverBudget)
                                        (if (strings.isBangla) "বাজেট শেষ (আর $daysLeft দিন)" else "Exceeded ($daysLeft days left)")
                                    else
                                        "${formatCurrency(dailySafeSpend, userProfile.currencySymbol)}/${if (strings.isBangla) "দিন (আর $daysLeft দিন)" else "day ($daysLeft days left)"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = if (overallStatus.isOverBudget) ColorDangerExpense else ColorSuccessIncome
                                )
                            }
                        }
                    } else {
                        Column {
                            Text(
                                text = strings.noOverallBudgetHint,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = ColorTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    selectedBudgetToEdit = null
                                    showAddDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (strings.isBangla) "বাজেট নির্ধারণ করুন" else "Set Monthly Budget",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }


        // Section header: "Category budgets" + "N active" badge + Add button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.categoryBudgets,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .background(ColorSurface)
                            .border(1.dp, ColorBorder, RoundedCornerShape(SmallElementRadius))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (strings.isBangla) "${categoryBudgetStatuses.size}টি সক্রিয়" else "${categoryBudgetStatuses.size} active",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        selectedBudgetToEdit = null
                        showAddDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add category budget",
                        tint = ColorAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Category budget cards or Empty State
        if (categoryBudgetStatuses.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    headline = strings.setFirstCategoryBudget,
                    explanation = strings.setFirstCategoryBudgetHint,
                    buttonText = strings.addCategoryBudget,
                    onButtonClick = {
                        selectedBudgetToEdit = null
                        showAddDialog = true
                    }
                )
            }
        } else {
            items(categoryBudgetStatuses, key = { it.budget.id }) { item ->
                val cat = categories.find { it.id == item.budget.categoryId }
                val catColor = cat?.colorHex ?: "#3b6ef5"
                val barFillColor = when {
                    item.isOverBudget -> ColorDangerExpense
                    item.progressPercent > 0.8f -> ColorWarningBills
                    else -> ColorSuccessIncome
                }

                AppCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                CategoryIconBadge(
                                    iconName = cat?.icon ?: "MoreHoriz",
                                    colorHex = catColor
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = getLocalizedCategoryName(item.budget.categoryName, strings.isBangla),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = ColorTextPrimary
                                    )
                                    Text(
                                        text = if (strings.isBangla) "${formatCurrency(item.spentAmount, userProfile.currencySymbol)} / ${formatCurrency(item.budget.amount, userProfile.currencySymbol)}" else "${formatCurrency(item.spentAmount, userProfile.currencySymbol)} of ${formatCurrency(item.budget.amount, userProfile.currencySymbol)}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                        color = ColorTextSecondary
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        selectedBudgetToEdit = item.budget
                                        showAddDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "Edit",
                                        tint = ColorTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        showDeleteConfirmDialog = item.budget
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete",
                                        tint = ColorDangerExpense,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AppProgressBar(
                            progress = item.progressPercent,
                            height = 6.dp,
                            fillColor = barFillColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (strings.isBangla) "${(item.progressPercent * 100).toInt()}% ব্যবহৃত" else "${(item.progressPercent * 100).toInt()}% used",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = barFillColor
                            )
                            Text(
                                text = if (item.isOverBudget) (if (strings.isBangla) "সীমা অতিক্রম করেছে!" else "Over limit!") else (if (strings.isBangla) "${formatCurrency(item.remainingAmount, userProfile.currencySymbol)} বাকি" else "${formatCurrency(item.remainingAmount, userProfile.currencySymbol)} remaining"),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (item.isOverBudget) ColorDangerExpense else ColorTextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
} else {
    // Savings Goals Tab
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SavingsSummaryCard(
                goals = savingsGoals,
                currencySymbol = userProfile.currencySymbol,
                isPrivacyMode = isPrivacyMode,
                onAddGoalClick = {
                    goalToEdit = null
                    showAddSavingsGoalSheet = true
                }
            )
        }

        if (savingsGoals.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(CardGap))
                EmptyStateCard(
                    icon = Icons.Outlined.Savings,
                    headline = strings.noSavingsGoalsFound,
                    explanation = strings.noSavingsGoalsHint,
                    buttonText = "+ ${strings.addSavingsGoal}",
                    onButtonClick = {
                        goalToEdit = null
                        showAddSavingsGoalSheet = true
                    }
                )
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${strings.savingsGoalsTitle.uppercase(java.util.Locale.getDefault())} (${if (strings.isBangla) convertToBengaliDigits(savingsGoals.size.toString()) else savingsGoals.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "+ ${strings.addSavingsGoal}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorAccent,
                        modifier = Modifier.clickable {
                            goalToEdit = null
                            showAddSavingsGoalSheet = true
                        }
                    )
                }
            }

            items(savingsGoals, key = { it.id }) { goal ->
                SavingsGoalCard(
                    goal = goal,
                    currencySymbol = userProfile.currencySymbol,
                    isPrivacyMode = isPrivacyMode,
                    geminiApiKey = userProfile.geminiApiKey,
                    onAddContribution = { goalForContribution = goal },
                    onEdit = {
                        goalToEdit = goal
                        showAddSavingsGoalSheet = true
                    },
                    onDelete = { goalToDelete = goal }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}

    // Add / Edit Budget Dialog
    if (showAddDialog) {
        var budgetAmountText by remember {
            mutableStateOf(selectedBudgetToEdit?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
        }
        var selectedCatId by remember {
            mutableStateOf(selectedBudgetToEdit?.categoryId)
        }
        var isDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = if (selectedBudgetToEdit != null) (if (strings.isBangla) "বাজেট সম্পাদনা" else "Edit budget") else (if (strings.isBangla) "বাজেট সীমা নির্ধারণ" else "Set budget limit"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = if (strings.isBangla) "বাজেটের লক্ষ্য" else "Budget target",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                    ) {
                        val selectedName = if (selectedCatId == null) {
                            strings.overallMonthlyBudget
                        } else {
                            categories.find { it.id == selectedCatId }?.getLocalizedName(strings.isBangla) ?: if (strings.isBangla) "ক্যাটাগরি নির্বাচন করুন" else "Select category"
                        }

                        OutlinedTextField(
                            value = selectedName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(SmallElementRadius),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ColorSurface,
                                unfocusedContainerColor = ColorSurface,
                                focusedBorderColor = ColorBorder,
                                unfocusedBorderColor = ColorBorder,
                                focusedTextColor = ColorTextPrimary,
                                unfocusedTextColor = ColorTextPrimary
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.background(ColorSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.overallMonthlyBudget, color = ColorTextPrimary) },
                                onClick = {
                                    selectedCatId = null
                                    isDropdownExpanded = false
                                }
                            )
                            expenseCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.getLocalizedName(strings.isBangla), color = ColorTextPrimary) },
                                    onClick = {
                                        selectedCatId = cat.id
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (strings.isBangla) "মাসিক বাজেট সীমা (${userProfile.currencySymbol})" else "Monthly limit (${userProfile.currencySymbol})",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = budgetAmountText,
                        onValueChange = { budgetAmountText = it.filter { c -> c.isDigit() || c == '.' } },
                        placeholder = { Text(if (strings.isBangla) "যেমন: ৫০০" else "e.g. 500", color = ColorTextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(SmallElementRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ColorSurface,
                            unfocusedContainerColor = ColorSurface,
                            focusedBorderColor = ColorBorder,
                            unfocusedBorderColor = ColorBorder,
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = budgetAmountText.toDoubleOrNull() ?: return@Button
                        val catName = if (selectedCatId == null) "Overall monthly" else categories.find { it.id == selectedCatId }?.name ?: "Category"
                        val cal = Calendar.getInstance()

                        val newBudget = Budget(
                            id = selectedBudgetToEdit?.id ?: 0L,
                            categoryId = selectedCatId,
                            categoryName = catName,
                            amount = amount,
                            month = cal.get(Calendar.MONTH),
                            year = cal.get(Calendar.YEAR)
                        )
                        viewModel.saveBudget(newBudget)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.save, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog != null) {
        val targetBudget = showDeleteConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = if (strings.isBangla) "বাজেট মুছে ফেলবেন?" else "Delete budget?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla) "আপনি কি নিশ্চিতভাবে ${getLocalizedCategoryName(targetBudget.categoryName, strings.isBangla)}-এর বাজেট সীমা মুছে ফেলতে চান?" else "Are you sure you want to remove the budget limit for ${targetBudget.categoryName}?",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBudget(targetBudget.id)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Modal: Add / Edit Savings Goal Sheet
    if (showAddSavingsGoalSheet) {
        AddEditSavingsGoalSheet(
            goal = goalToEdit,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = {
                showAddSavingsGoalSheet = false
                goalToEdit = null
            },
            onSave = { title, targetAmt, initAmt, targetDate, icon, colorHex, notes ->
                if (goalToEdit != null) {
                    val updated = goalToEdit!!.copy(
                        title = title,
                        targetAmount = targetAmt,
                        savedAmount = initAmt,
                        targetDateMillis = targetDate,
                        icon = icon,
                        colorHex = colorHex,
                        notes = notes
                    )
                    viewModel.updateSavingsGoal(updated)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = if (strings.isBangla) "লক্ষ্য আপডেট করা হয়েছে" else "Goal updated",
                            amount = targetAmt,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = title
                        )
                    )
                } else {
                    viewModel.addSavingsGoal(title, targetAmt, initAmt, targetDate, icon, colorHex, notes)
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = if (strings.isBangla) "নতুন সঞ্চয় লক্ষ্য যুক্ত হয়েছে" else "Savings goal added",
                            amount = targetAmt,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = title
                        )
                    )
                }
                showAddSavingsGoalSheet = false
                goalToEdit = null
            }
        )
    }

    // Modal: Add Savings Contribution Sheet
    if (goalForContribution != null) {
        AddSavingsContributionSheet(
            goal = goalForContribution!!,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { goalForContribution = null },
            onConfirm = { amount, fromAccountId ->
                val target = goalForContribution
                if (target != null) {
                    val willReach = (target.remainingAmount - amount) <= 0.001
                    viewModel.addSavingsContribution(target.id, amount, fromAccountId)
                    if (willReach) {
                        showConfetti = true
                    }
                    feedbackController.show(
                        TransactionFeedbackData(
                            type = FeedbackVisualType.GENERIC_SUCCESS,
                            title = if (willReach) strings.goalReachedCelebration else strings.addSavingsContribution,
                            amount = amount,
                            currencySymbol = userProfile.currencySymbol,
                            subtitle = target.title
                        )
                    )
                }
                goalForContribution = null
            }
        )
    }

    // Delete Goal Confirmation Dialog
    if (goalToDelete != null) {
        val targetGoal = goalToDelete!!
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = "${strings.deleteGoalConfirm} (${targetGoal.title})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Text(
                    text = strings.deleteGoalMessage,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSavingsGoal(targetGoal.id)
                        goalToDelete = null
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.ITEM_DELETED,
                                title = FeedbackStrings.itemDeleted(strings.isBangla),
                                subtitle = targetGoal.title
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Confetti Celebration
    ConfettiCelebration(
        trigger = showConfetti,
        onAnimationEnd = { showConfetti = false }
    )
}
}

