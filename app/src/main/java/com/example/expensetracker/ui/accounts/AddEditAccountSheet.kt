package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountSheet(
    account: Account?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Account) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    var type by remember { mutableStateOf(account?.type ?: AccountType.BANK) }
    var name by remember { mutableStateOf(account?.name ?: "") }
    var institution by remember { mutableStateOf(account?.institutionName ?: "") }
    var accountNumber by remember { mutableStateOf(account?.accountNumber ?: "") }
    var initialBalanceText by remember {
        mutableStateOf(account?.initialBalance?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "0")
    }
    var selectedColor by remember { mutableStateOf(account?.colorHex ?: "#3B8FF0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val presetColors = listOf("#3B8FF0", "#2EC4B6", "#E056FD", "#F0A63D", "#3DDC97", "#FF758F", "#9D4EDD", "#F0716B")
    val bankSuggestions = listOf("City Bank", "Brac Bank", "Dutch-Bangla Bank", "Eastern Bank", "Islami Bank", "Standard Chartered", "Sonali Bank", "UCB")
    val mfsSuggestions = listOf("bKash", "Nagad", "Rocket", "Upay", "Cellfin", "SureCash")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (account != null) strings.editAccount else strings.addAccount,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = appColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(strings.accountTypeLabel, fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            AccountTypeSelector(
                selectedType = type,
                onTypeSelected = { t ->
                    type = t
                    if (name.isBlank() || name == "City Bank" || name == "bKash" || name == "Cash in Hand") {
                        name = when (t) {
                            AccountType.BANK -> "City Bank"
                            AccountType.MFS -> "bKash"
                            AccountType.CASH -> "Cash in Hand"
                        }
                        institution = when (t) {
                            AccountType.BANK -> "City Bank"
                            AccountType.MFS -> "bKash"
                            AccountType.CASH -> "Cash"
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (type != AccountType.CASH) {
                val suggestions = if (type == AccountType.BANK) bankSuggestions else mfsSuggestions
                Text(strings.quickSelectProvider, fontSize = 11.sp, color = appColors.textMuted)
                Spacer(modifier = Modifier.height(6.dp))
                AccountProviderSuggestionChips(
                    selectedInstitution = institution,
                    suggestions = suggestions,
                    onSelect = { s ->
                        institution = s
                        if (name.isBlank() || bankSuggestions.contains(name) || mfsSuggestions.contains(name)) {
                            name = s
                        }
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            AddEditAccountFormFields(
                name = name,
                onNameChange = { name = it },
                institution = institution,
                onInstitutionChange = { institution = it },
                accountNumber = accountNumber,
                onAccountNumberChange = { accountNumber = it },
                initialBalanceText = initialBalanceText,
                onInitialBalanceChange = { initialBalanceText = it },
                type = type,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(strings.accountColorTheme, fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            AccountColorThemePicker(
                selectedColor = selectedColor,
                presetColors = presetColors,
                onColorSelected = { selectedColor = it }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = appColors.dangerExpense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = if (strings.isBangla) "অনুগ্রহ করে অ্যাকাউন্টের নাম লিখুন" else "Please enter an account name"
                        return@Button
                    }
                    val initialBal = initialBalanceText.toParsedAmountOrNull() ?: 0.0
                    val updated = (account ?: Account(name = name, type = type)).copy(
                        name = name.trim(),
                        type = type,
                        institutionName = institution.trim(),
                        accountNumber = accountNumber.trim(),
                        initialBalance = initialBal,
                        colorHex = selectedColor
                    )
                    onSave(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
            ) {
                Text(
                    text = if (account != null) strings.updateAccount else strings.saveAccount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
