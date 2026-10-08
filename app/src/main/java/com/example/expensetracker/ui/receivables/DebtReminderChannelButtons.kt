package com.example.expensetracker.ui.receivables

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.ui.components.LocalAppStrings
import java.net.URLEncoder

@Composable
fun DebtReminderChannelButtons(
    target: Receivable,
    generatedReminder: String?,
    context: Context
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // WhatsApp Button
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    val rawPhone = target.phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
                    val waPhone = when {
                        rawPhone.startsWith("01") -> "88$rawPhone"
                        rawPhone.startsWith("880") -> rawPhone
                        else -> rawPhone
                    }
                    val textEncoded = URLEncoder.encode(generatedReminder ?: "", "UTF-8")
                    val waUri = if (waPhone.isNotBlank()) {
                        Uri.parse("https://wa.me/$waPhone?text=$textEncoded")
                    } else {
                        Uri.parse("https://api.whatsapp.com/send?text=$textEncoded")
                    }
                    val waIntent = Intent(Intent.ACTION_VIEW, waUri)
                    try {
                        context.startActivity(waIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, if (strings.isBangla) "হোয়াটসঅ্যাপ ইনস্টল করা নেই" else "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }
                },
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF25D366).copy(alpha = 0.15f),
            border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = Color(0xFF25D366),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (strings.isBangla) "WhatsApp-এ পাঠান" else "WhatsApp",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF25D366)
                )
            }
        }

        // SMS Button
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    val phone = target.phoneNumber
                    val smsUri = if (phone.isNotBlank()) Uri.parse("smsto:$phone") else Uri.parse("smsto:")
                    val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                        putExtra("sms_body", generatedReminder)
                    }
                    try {
                        context.startActivity(smsIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, if (strings.isBangla) "মেসেজিং অ্যাপ পাওয়া যায়নি" else "No messaging app found", Toast.LENGTH_SHORT).show()
                    }
                },
            shape = RoundedCornerShape(8.dp),
            color = ColorAccent.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = ColorAccent,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (strings.isBangla) "SMS পাঠান" else "SMS",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorAccent
                )
            }
        }
    }
}
