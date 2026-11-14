package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.BillSplitFriend
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun BillSplitterFriendsSection(
    friends: SnapshotStateList<BillSplitFriend>,
    isBangla: Boolean,
    isSplitEqually: Boolean,
    equalShare: Double,
    currencySymbol: String,
    addFriendLabel: String,
    haptics: AppHaptics
) {
    BillSplitterFriendsSectionHeader(
        isBangla = isBangla,
        addFriendLabel = addFriendLabel,
        onAddFriend = {
            friends.add(
                BillSplitFriend(
                    name = if (isBangla) "বন্ধু ${convertToBengaliDigits((friends.size + 1).toString())}" else "Friend ${friends.size + 1}",
                    amount = 0.0
                )
            )
        },
        haptics = haptics
    )

    Spacer(modifier = Modifier.height(10.dp))

    friends.forEachIndexed { index, friend ->
        BillSplitterFriendRow(
            friend = friend,
            isBangla = isBangla,
            isSplitEqually = isSplitEqually,
            equalShare = equalShare,
            currencySymbol = currencySymbol,
            canRemove = friends.size > 1,
            onNameChange = { friends[index] = friend.copy(name = it) },
            onPhoneChange = { friends[index] = friend.copy(phoneNumber = it) },
            onAmountChange = { friends[index] = friend.copy(amount = it) },
            onRemove = { friends.removeAt(index) },
            haptics = haptics
        )
    }
}
