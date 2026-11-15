package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun MarketListsStarterTemplates(
    isBangla: Boolean,
    onCreateNewClick: () -> Unit,
    onCreateStarterList: ((String, List<Pair<String, String>>) -> Unit)?,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(appColors.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isBangla) "বর্তমানে কোনো সক্রিয় বাজার ফর্দ নেই" else "No active market checklist",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                color = appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isBangla)
                    "সকালের কাঁচাবাজার বা সাপ্তাহিক মুদিবাজারের ফর্দ তৈরি করুন অথবা রেডিমেড টেমপ্লেট দিয়ে ১-ট্যাপে শুরু করুন।"
                else
                    "Create shopping lists or choose a quick starter template to get started instantly.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    haptics.mediumClick()
                    onCreateNewClick()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isBangla) "নতুন বাজার ফর্দ তৈরি করুন" else "Create Shopping List", fontWeight = FontWeight.Bold)
            }

            if (onCreateStarterList != null) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isBangla) "বা রেডিমেড টেমপ্লেট দিয়ে শুরু করুন:" else "Or start with a ready template:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val templates = listOf(
                    Triple(
                        "সকালের কাঁচাবাজার", "🥬 কাঁচাবাজার",
                        listOf("আলু" to "১ কেজি", "দেশি পেঁয়াজ" to "১ কেজি", "কাঁচামরিচ" to "২৫০ গ্রাম", "ফার্মের ডিম" to "১ ডজন")
                    ),
                    Triple(
                        "সাপ্তাহিক মুদি বাজার", "🛒 মুদি সদাই",
                        listOf("মিনিকেট চাল" to "৫ কেজি", "সয়াবিন তেল" to "২ লিটার", "মসুর ডাল" to "১ কেজি", "লবণ" to "১ কেজি")
                    ),
                    Triple(
                        "মাছ ও মাংসের বাজার", "🐟 মাছ-মাংস",
                        listOf("ব্রয়লার মুরগি" to "১ কেজি", "গরুর মাংস" to "১ কেজি", "রুই মাছ" to "১ কেজি", "আদা" to "২৫০ গ্রাম")
                    )
                )

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    templates.forEach { (title, subtitle, items) ->
                        Surface(
                            onClick = {
                                haptics.mediumClick()
                                onCreateStarterList(title, items)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = appColors.cardBackground,
                            border = BorderStroke(HairlineInkStroke, appColors.divider),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp), color = appColors.textPrimary)
                                    Text(text = "${items.size}টি প্রয়োজনীয় পণ্য • $subtitle", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = appColors.textSecondary)
                                }

                                Surface(shape = RoundedCornerShape(8.dp), color = appColors.accent.copy(alpha = 0.12f)) {
                                    Text(
                                        text = if (isBangla) "শুরু করুন" else "Start",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        color = appColors.accent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
