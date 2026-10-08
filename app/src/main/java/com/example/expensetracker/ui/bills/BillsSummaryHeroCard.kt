package com.example.expensetracker.ui.bills

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Warning
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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun BillsSummaryHeroCard(
    totalMonthlyCommitments: Double,
    currencySymbol: String,
    activeBillsCount: Int,
    overdueCount: Int,
    dueSoonCount: Int,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF141829),
        borderColor = Color(0xFF282F46),
        padding = 16.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.totalMonthlyCommitment,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatCurrency(totalMonthlyCommitments, currencySymbol),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HairlineDivider(color = Color(0xFF232838))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ColorAccent)
                    )
                    Text(
                        text = "$activeBillsCount ${strings.activeSubscriptions}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = ColorTextSecondary
                    )
                }

                if (overdueCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = ColorDangerExpense.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = ColorDangerExpense,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "$overdueCount ${if (strings.isBangla) "বকেয়া" else "overdue"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ColorDangerExpense
                            )
                        }
                    }
                } else if (dueSoonCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF7941D).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFF7941D).copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFFF7941D),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "$dueSoonCount ${if (strings.isBangla) "তাগিদ" else "due soon"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFF7941D)
                            )
                        }
                    }
                }
            }
        }
    }
}
