package com.example.expensetracker.ui.bills

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun AddEditBillCategorySelector(
    categories: List<Category>,
    selectedCategory: Category?,
    onSelectCategory: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.category,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = ColorTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory?.id == cat.id
                val catColor = CategoryIconHelper.parseColor(cat.colorHex)

                Surface(
                    shape = RoundedCornerShape(SmallElementRadius),
                    color = if (isSelected) catColor.copy(alpha = 0.2f) else Color(0xFF0F131D),
                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) catColor else ColorBorder),
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onSelectCategory(cat)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = CategoryIconHelper.getIcon(cat.icon),
                            contentDescription = null,
                            tint = catColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = cat.getLocalizedName(strings.isBangla),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected) ColorTextPrimary else ColorTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditBillAccountSelector(
    accounts: List<Account>,
    selectedAccount: Account?,
    onSelectAccount: (Account) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    if (accounts.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (strings.isBangla) "পরিশোধের অ্যাকাউন্ট" else "Paying Account",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = ColorTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accounts.forEach { acc ->
                val isSelected = selectedAccount?.id == acc.id
                val brandStyle = BrandIdentityHelper.getBrandStyle(acc)

                Surface(
                    shape = RoundedCornerShape(SmallElementRadius),
                    color = if (isSelected) brandStyle.primaryColor.copy(alpha = 0.2f) else Color(0xFF0F131D),
                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) brandStyle.primaryColor else ColorBorder),
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onSelectAccount(acc)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(brandStyle.gradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = brandStyle.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = acc.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = ColorTextPrimary
                        )
                    }
                }
            }
        }
    }
}
