package com.example.expensetracker.ui.share

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.ui.components.Strings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ShareImageSection(
    periodTitle: String,
    periodSubtitle: String,
    totalIncome: Double,
    totalExpense: Double,
    transactions: List<Transaction>,
    userProfile: UserProfile,
    strings: Strings,
    context: Context,
    graphicsLayer: GraphicsLayer,
    coroutineScope: CoroutineScope,
    isSharing: Boolean,
    isSharingPdf: Boolean,
    isLoading: Boolean,
    onSharingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            ) {
                ExpenseShareSlipCard(
                    periodTitle = periodTitle,
                    periodSubtitle = periodSubtitle,
                    userName = userProfile.name,
                    currencySymbol = userProfile.currencySymbol,
                    totalExpense = totalExpense,
                    totalIncome = totalIncome,
                    transactions = transactions
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (!isSharing && !isSharingPdf && !isLoading) {
                    coroutineScope.launch {
                        onSharingChange(true)
                        try {
                            val imageBitmap = graphicsLayer.toImageBitmap()
                            val bitmap = imageBitmap.asAndroidBitmap()
                            val uri = ShareExpenseHelper.saveBitmapToCache(context, bitmap)
                            if (uri != null) {
                                ShareExpenseHelper.shareImage(
                                    context = context,
                                    uri = uri,
                                    caption = "Expense Statement: $periodSubtitle"
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            onSharingChange(false)
                        }
                    }
                }
            },
            enabled = !isSharing && !isSharingPdf && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorAccent,
                contentColor = Color.White
            )
        ) {
            if (isSharing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${strings.shareImageSlip}...",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 14.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.shareImageSlip,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 14.sp
                )
            }
        }
    }
}
