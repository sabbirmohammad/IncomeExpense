package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.LocalAppStrings

/**
 * V7 GetStartedChecklist for new users per UX_FLOWS section 2 & Phase 4 prompt:
 * - Shown when transactions are empty
 * - 3 clear actionable steps: Add account, Add first expense, Set monthly budget
 */
@Composable
fun DashboardGetStartedChecklist(
    hasAccount: Boolean,
    hasExpense: Boolean,
    hasBudget: Boolean,
    onAddAccount: (() -> Unit)?,
    onAddExpense: (() -> Unit)?,
    onSetBudget: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Surface(
        shape = RoundedCornerShape(RadiusM),
        color = appColors.surfaceRaised,
        border = if (appColors.isDark) null else BorderStroke(0.5.dp, appColors.border.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPaddingHorizontal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = strings.getStartedChecklist,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Step 1: Add account
            ChecklistItemRow(
                stepNumber = 1,
                title = strings.addAccountTask,
                isCompleted = hasAccount,
                onClick = onAddAccount
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Step 2: Add first expense
            ChecklistItemRow(
                stepNumber = 2,
                title = strings.addFirstExpenseTask,
                isCompleted = hasExpense,
                onClick = onAddExpense
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Step 3: Set monthly budget
            ChecklistItemRow(
                stepNumber = 3,
                title = strings.setBudgetTask,
                isCompleted = hasBudget,
                onClick = onSetBudget
            )
        }
    }
}

@Composable
private fun ChecklistItemRow(
    stepNumber: Int,
    title: String,
    isCompleted: Boolean,
    onClick: (() -> Unit)?
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) appColors.accent else appColors.border.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Done",
                        tint = appColors.onAccent,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = "$stepNumber",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.textSecondary
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium
                ),
                color = if (isCompleted) appColors.textMuted else appColors.textPrimary
            )
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = appColors.textMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
