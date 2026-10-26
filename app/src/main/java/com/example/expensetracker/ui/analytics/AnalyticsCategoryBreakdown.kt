package com.example.expensetracker.ui.analytics

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.AppProgressBar
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.DonutChart
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun CategoryDonutChartCard(
    categorySpends: List<CategorySpend>,
    currencySymbol: String,
    totalAmount: Double,
    transactionType: TransactionType,
    strings: Strings,
    onCategoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    AppCard(modifier = modifier) {
        Column {
            Text(
                text = if (transactionType == TransactionType.EXPENSE) {
                    if (strings.isBangla) "ক্যাটাগরি ভিত্তিক খরচ" else "Expense by category"
                } else {
                    if (strings.isBangla) "ক্যাটাগরি ভিত্তিক আয়" else "Income by category"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            DonutChart(
                categorySpends = categorySpends,
                currencySymbol = currencySymbol,
                totalAmount = totalAmount,
                centerLabel = if (transactionType == TransactionType.EXPENSE) {
                    if (strings.isBangla) "মোট খরচ" else "Total spent"
                } else {
                    if (strings.isBangla) "মোট আয়" else "Total income"
                },
                onCategoryClick = onCategoryClick
            )
        }
    }
}

@Composable
fun CategoryRankingCard(
    categorySpends: List<CategorySpend>,
    userProfile: UserProfile,
    transactionType: TransactionType,
    strings: Strings,
    onCategoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    AppCard(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (strings.isBangla) "ক্যাটাগরি র‍্যাঙ্কিং ও অনুপাত" else "Category Ranking & Share",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.textPrimary
                )
                Text(
                    text = "${categorySpends.size} ${if (strings.isBangla) "টি ক্যাটাগরি" else "categories"}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = appColors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            categorySpends.forEachIndexed { index, spend ->
                val color = CategoryIconHelper.parseColor(spend.categoryColor)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(spend.categoryId) }
                        .padding(vertical = 8.dp)
                ) {
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
                                iconName = spend.categoryIcon,
                                colorHex = spend.categoryColor,
                                size = 32.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = getLocalizedCategoryName(spend.categoryName, strings.isBangla),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = appColors.textPrimary
                                    )
                                    if (index == 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(appColors.accent.copy(alpha = 0.15f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = if (transactionType == TransactionType.EXPENSE) {
                                                    if (strings.isBangla) "শীর্ষ খাত" else "Top"
                                                } else {
                                                    if (strings.isBangla) "শীর্ষ আয়" else "Top"
                                                },
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = appColors.accent
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${spend.transactionCount} ${if (strings.isBangla) "টি লেনদেন" else "transactions"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = appColors.textMuted
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatCurrency(spend.totalAmount, userProfile.currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = "${(spend.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = appColors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    AppProgressBar(
                        progress = spend.percentage,
                        fillColor = color,
                        height = 4.dp
                    )
                }

                if (index < categorySpends.lastIndex) {
                    HairlineDivider()
                }
            }
        }
    }
}
