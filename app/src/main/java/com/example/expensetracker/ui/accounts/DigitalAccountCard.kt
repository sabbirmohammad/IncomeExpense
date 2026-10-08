package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun DigitalAccountCard(
    account: Account,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val brandStyle = BrandIdentityHelper.getBrandStyle(account)
    val isLightContent = brandStyle.isDarkCard

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brandStyle.gradient)
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset(x = 180.dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.07f))
            )
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 220.dp, y = 50.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = brandStyle.icon,
                                contentDescription = null,
                                tint = if (isLightContent) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (account.institutionName.isNotBlank()) account.institutionName else brandStyle.brandName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = if (isLightContent) Color.White else Color.Black
                            )
                            Text(
                                text = when (account.type) {
                                    AccountType.BANK -> "BANKING"
                                    AccountType.MFS -> "MOBILE WALLET"
                                    AccountType.CASH -> "PHYSICAL CASH"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.2.sp
                                ),
                                color = if (isLightContent) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DigitalCardEmvChip()

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLightContent) Color.White.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = when (account.type) {
                                    AccountType.BANK -> "DEBIT"
                                    AccountType.MFS -> "WALLET"
                                    AccountType.CASH -> "CASH"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = if (isLightContent) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = if (strings.isBangla) "বর্তমান ব্যালেন্স" else "CURRENT BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.2.sp
                        ),
                        color = if (isLightContent) Color.White.copy(alpha = 0.75f) else Color.Black.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(account.currentBalance, currencySymbol),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = if (isLightContent) Color.White else Color.Black
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = if (strings.isBangla) "অ্যাকাউন্টের নাম" else "ACCOUNT NAME",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, letterSpacing = 1.sp),
                            color = if (isLightContent) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.55f)
                        )
                        Text(
                            text = account.name.uppercase(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = if (isLightContent) Color.White else Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (strings.isBangla) "অ্যাকাউন্ট নম্বর" else "ACCOUNT NO",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, letterSpacing = 1.sp),
                            color = if (isLightContent) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.55f)
                        )
                        val formattedNum = when {
                            account.accountNumber.length >= 8 -> "•••• •••• ${account.accountNumber.takeLast(4)}"
                            account.accountNumber.isNotBlank() -> "•••• ${account.accountNumber}"
                            account.type == AccountType.CASH -> "CASH WALLET"
                            else -> "•••• ${account.id.toString().padStart(4, '0')}"
                        }
                        Text(
                            text = formattedNum,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.2.sp
                            ),
                            color = if (isLightContent) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}
