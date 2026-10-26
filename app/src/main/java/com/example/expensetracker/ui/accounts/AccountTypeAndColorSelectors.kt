package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountTypeSelector(
    selectedType: AccountType,
    onTypeSelected: (AccountType) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(AccountType.BANK, AccountType.MFS, AccountType.CASH).forEach { t ->
            val isSelected = selectedType == t
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = if (isSelected) appColors.accent else appColors.surfaceVariant,
                border = if (isSelected) null else BorderStroke(1.dp, appColors.border),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTypeSelected(t) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (t) {
                            AccountType.BANK -> Icons.Outlined.AccountBalance
                            AccountType.MFS -> Icons.Outlined.PhoneAndroid
                            AccountType.CASH -> Icons.Outlined.Payments
                        },
                        contentDescription = null,
                        tint = if (isSelected) Color.White else appColors.textSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (t) {
                            AccountType.BANK -> strings.banksTab
                            AccountType.MFS -> strings.mfsTab
                            AccountType.CASH -> strings.cashTab
                        },
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.White else appColors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AccountProviderSuggestionChips(
    selectedInstitution: String,
    suggestions: List<String>,
    onSelect: (String) -> Unit
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        suggestions.forEach { s ->
            val isSelected = selectedInstitution == s
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) appColors.accent.copy(alpha = 0.2f) else appColors.surfaceVariant,
                border = BorderStroke(1.dp, if (isSelected) appColors.accent else appColors.border),
                modifier = Modifier.clickable { onSelect(s) }
            ) {
                Text(
                    text = s,
                    fontSize = 11.sp,
                    color = if (isSelected) appColors.accent else appColors.textSecondary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun AccountColorThemePicker(
    selectedColor: String,
    presetColors: List<String>,
    onColorSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        presetColors.forEach { cHex ->
            val isPicked = selectedColor.equals(cHex, ignoreCase = true)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CategoryIconHelper.parseColor(cHex))
                    .clickable { onColorSelected(cHex) }
                    .then(
                        if (isPicked) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isPicked) {
                    Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
