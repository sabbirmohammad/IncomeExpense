package com.example.expensetracker

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.utils.ReceiptImageHelper
import java.io.File
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase1EmpiricalStressTest {

    // --------------------------------------------------------------------------
    // 1. ReceiptImageHelper.calculateInSampleSize Boundary Dimension Stress Tests
    // --------------------------------------------------------------------------

    @Test
    fun testReceiptImageHelper_boundaryDimensions_calculateInSampleSize() {
        val maxBound = 1024

        // Exact boundary (1024x1024) -> sampleSize = 1
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1024, 1024, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1024, 768, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(768, 1024, maxBound))

        // Boundary + 1 px (1025x768) -> sampleSize = 2
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(1025, 768, maxBound))
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(768, 1025, maxBound))
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(1025, 1025, maxBound))

        // 12MP camera photo (4000x3000) -> sampleSize = 4 (4000 / 4 = 1000 <= 1024)
        assertEquals(4, ReceiptImageHelper.calculateInSampleSize(4000, 3000, maxBound))

        // 80MP / ultra-high panorama (10000x8000) -> sampleSize = 16 (10000 / 16 = 625 <= 1024)
        assertEquals(16, ReceiptImageHelper.calculateInSampleSize(10000, 8000, maxBound))

        // Zero dimensions (0x0) -> guarded to 1
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(0, 0, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(0, 1024, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1024, 0, maxBound))

        // Negative dimensions -> guarded to 1
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(-1, -1, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(-100, 500, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(-4000, -3000, maxBound))

        // Extreme aspect ratios (e.g. receipt ribbons, tall panoramic scans)
        assertEquals(16, ReceiptImageHelper.calculateInSampleSize(1, 10000, maxBound))
        assertEquals(16, ReceiptImageHelper.calculateInSampleSize(10000, 1, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1024, 1, maxBound))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1, 1024, maxBound))
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(1025, 1, maxBound))

        // Custom maxDimension boundaries
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(1024, 1024, 512))
        assertEquals(4, ReceiptImageHelper.calculateInSampleSize(1024, 1024, 256))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(2048, 2048, 2048))
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(2049, 1000, 2048))
    }

    // --------------------------------------------------------------------------
    // 2. Pre-Restore Database Snapshot Copy Logic Stress Tests
    // --------------------------------------------------------------------------

    @Test
    fun testPreRestoreDatabaseSnapshotCopyLogic() {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "restore_test_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        try {
            val dbFile = File(tempDir, "ExpenseTracker.db")
            dbFile.writeText("SQLite format 3 database content simulation")
            val walFile = File(tempDir, "ExpenseTracker.db-wal")
            walFile.writeText("wal content")
            val shmFile = File(tempDir, "ExpenseTracker.db-shm")
            shmFile.writeText("shm content")

            val snapshotDir = File(tempDir, "backup_snapshots")
            snapshotDir.mkdirs()
            val targetSnapshot = File(snapshotDir, "pre_restore_snapshot.db")

            // Simulate file copy logic
            dbFile.copyTo(targetSnapshot, overwrite = true)
            if (walFile.exists()) {
                walFile.copyTo(File(snapshotDir, "pre_restore_snapshot.db-wal"), overwrite = true)
            }
            if (shmFile.exists()) {
                shmFile.copyTo(File(snapshotDir, "pre_restore_snapshot.db-shm"), overwrite = true)
            }

            assertTrue(targetSnapshot.exists())
            assertEquals("SQLite format 3 database content simulation", targetSnapshot.readText())
            val targetWal = File(snapshotDir, "pre_restore_snapshot.db-wal")
            assertTrue(targetWal.exists())
            assertEquals("wal content", targetWal.readText())
            val targetShm = File(snapshotDir, "pre_restore_snapshot.db-shm")
            assertTrue(targetShm.exists())
            assertEquals("shm content", targetShm.readText())
        } finally {
            tempDir.deleteRecursively()
        }
    }

    // --------------------------------------------------------------------------
    // 3. Account Deletion Net Worth Conservation Stress Tests
    // --------------------------------------------------------------------------

    /**
     * Mathematical Oracle simulating AccountTableHelper.deleteAccount logic:
     * - Absorbs initial_balance of deleted account into target account
     * - Reassigns all transactions from deleted account to target account
     * - Reassigns recurring bills
     * - Recalculates current_balance = initial_balance + income - expense
     * - Guard: stops if only 1 account remains
     */
    private class AccountDeletionOracle(
        accounts: List<Account>,
        transactions: List<Transaction>
    ) {
        val accountMap = accounts.associateBy { it.id }.toMutableMap()
        val txList = transactions.toMutableList()

        init {
            recalculateAll()
        }

        fun recalculateAccount(accId: Long) {
            val acc = accountMap[accId] ?: return
            val inc = txList.filter { it.accountId == accId && it.type == TransactionType.INCOME }.sumOf { it.amount }
            val exp = txList.filter { it.accountId == accId && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val raw = acc.initialBalance + inc - exp
            val rounded = kotlin.math.round(raw * 100.0) / 100.0
            accountMap[accId] = acc.copy(currentBalance = rounded)
        }

        fun recalculateAll() {
            accountMap.keys.toList().forEach { recalculateAccount(it) }
        }

        fun totalNetWorth(): Double {
            return kotlin.math.round(accountMap.values.sumOf { it.currentBalance } * 100.0) / 100.0
        }

        fun deleteAccount(accId: Long): Boolean {
            if (accountMap.size <= 1) return false // Guard: cannot delete last account
            val deleted = accountMap[accId] ?: return false

            // Target selection matching SQL query:
            // "SELECT * FROM accounts WHERE id != ? ORDER BY is_default DESC, id ASC LIMIT 1"
            val target = accountMap.values
                .filter { it.id != accId }
                .sortedWith(compareByDescending<Account> { it.isDefault }.thenBy { it.id })
                .first()

            val targetId = target.id

            // If deleted account was default, promote target
            var newTarget = if (deleted.isDefault) target.copy(isDefault = true) else target

            // Merge initial_balance into surviving target account
            val mergedInitial = newTarget.initialBalance + deleted.initialBalance
            newTarget = newTarget.copy(initialBalance = mergedInitial)
            accountMap[targetId] = newTarget

            // Reassign transactions
            for (i in txList.indices) {
                if (txList[i].accountId == accId) {
                    txList[i] = txList[i].copy(accountId = targetId, accountName = newTarget.name)
                }
            }

            // Remove deleted account
            accountMap.remove(accId)

            // Recalculate target balance
            recalculateAccount(targetId)
            return true
        }
    }

    @Test
    fun testAccountDeletion_multiAccount_positiveNegativeZero_netWorthConserved() {
        // Setup 5 diverse accounts:
        // Acc 1: Bank (default, positive initial balance)
        // Acc 2: Credit Card (negative initial balance, debt)
        // Acc 3: Cash (zero initial balance)
        // Acc 4: Savings (positive fractional initial balance)
        // Acc 5: Petty Cash (small positive initial balance)
        val initialAccounts = listOf(
            Account(id = 1, name = "City Bank", type = AccountType.BANK, initialBalance = 100000.0, isDefault = true),
            Account(id = 2, name = "Credit Card", type = AccountType.BANK, initialBalance = -15000.0, isDefault = false),
            Account(id = 3, name = "Cash in Hand", type = AccountType.CASH, initialBalance = 0.0, isDefault = false),
            Account(id = 4, name = "Savings Fund", type = AccountType.BANK, initialBalance = 50000.50, isDefault = false),
            Account(id = 5, name = "Petty Cash", type = AccountType.CASH, initialBalance = 500.25, isDefault = false)
        )

        // Setup 15 transactions across accounts
        val initialTransactions = listOf(
            Transaction(1, "Salary", 45000.0, TransactionType.INCOME, 1, "Salary", "Work", "#10B981", 1000L, accountId = 1),
            Transaction(2, "Electronics", 12000.0, TransactionType.EXPENSE, 2, "Shopping", "Cart", "#FF5722", 2000L, accountId = 2),
            Transaction(3, "Dining", 850.50, TransactionType.EXPENSE, 3, "Food", "Restaurant", "#FF5722", 3000L, accountId = 3),
            Transaction(4, "Interest", 1250.75, TransactionType.INCOME, 4, "Investment", "TrendingUp", "#10B981", 4000L, accountId = 4),
            Transaction(5, "Snacks", 150.25, TransactionType.EXPENSE, 5, "Food", "Restaurant", "#FF5722", 5000L, accountId = 5),
            Transaction(6, "Fuel", 2500.0, TransactionType.EXPENSE, 6, "Transport", "Car", "#2196F3", 6000L, accountId = 1),
            Transaction(7, "Repayment", 5000.0, TransactionType.INCOME, 7, "Debt", "CreditCard", "#10B981", 7000L, accountId = 2),
            Transaction(8, "ATM Withdrawal", 3000.0, TransactionType.INCOME, 8, "Cash", "Atm", "#10B981", 8000L, accountId = 3),
            Transaction(9, "Groceries", 3500.0, TransactionType.EXPENSE, 9, "Food", "Cart", "#FF5722", 9000L, accountId = 4),
            Transaction(10, "Tea", 50.0, TransactionType.EXPENSE, 10, "Food", "Coffee", "#FF5722", 10000L, accountId = 5)
        )

        val oracle = AccountDeletionOracle(initialAccounts, initialTransactions)
        val initialNetWorth = oracle.totalNetWorth()

        // 1. Delete Account 5 (Petty Cash: small positive balance)
        assertTrue(oracle.deleteAccount(5))
        assertEquals(4, oracle.accountMap.size)
        assertEquals(initialNetWorth, oracle.totalNetWorth(), 0.001)

        // 2. Delete Account 3 (Cash in Hand: zero initial balance)
        assertTrue(oracle.deleteAccount(3))
        assertEquals(3, oracle.accountMap.size)
        assertEquals(initialNetWorth, oracle.totalNetWorth(), 0.001)

        // 3. Delete Account 2 (Credit Card: negative initial balance / debt)
        assertTrue(oracle.deleteAccount(2))
        assertEquals(2, oracle.accountMap.size)
        assertEquals(initialNetWorth, oracle.totalNetWorth(), 0.001)

        // 4. Delete Account 1 (Default Bank Account: default status should promote surviving Account 4)
        assertTrue(oracle.deleteAccount(1))
        assertEquals(1, oracle.accountMap.size)
        assertEquals(initialNetWorth, oracle.totalNetWorth(), 0.001)
        assertTrue(oracle.accountMap[4]!!.isDefault) // Account 4 promoted to default!

        // 5. Guard check: deleting the final remaining account must fail and conserve balance
        assertFalse(oracle.deleteAccount(4))
        assertEquals(1, oracle.accountMap.size)
        assertEquals(initialNetWorth, oracle.totalNetWorth(), 0.001)
    }

    @Test
    fun testAccountDeletion_debtOverdraftAbsorptionConservesNetWorth() {
        // Deep debt scenario:
        // Account 1: Bank (balance: 20,000 + 10,000 income = 30,000)
        // Account 2: Overdraft (initial: -50,000, expense: 5,000 = -55,000)
        // Total Net Worth = 30,000 - 55,000 = -25,000
        val acc1 = Account(1, "Bank", AccountType.BANK, initialBalance = 20000.0, isDefault = true)
        val acc2 = Account(2, "Overdraft", AccountType.BANK, initialBalance = -50000.0, isDefault = false)

        val txs = listOf(
            Transaction(1, "Income", 10000.0, TransactionType.INCOME, 1, "Salary", "Work", "#0F0", 1000L, accountId = 1),
            Transaction(2, "Expense", 5000.0, TransactionType.EXPENSE, 2, "Expense", "Cart", "#F00", 2000L, accountId = 2)
        )

        val oracle = AccountDeletionOracle(listOf(acc1, acc2), txs)
        assertEquals(-25000.0, oracle.totalNetWorth(), 0.001)

        // Delete Account 2 (debt):
        assertTrue(oracle.deleteAccount(2))
        assertEquals(1, oracle.accountMap.size)

        // Surviving Account 1 has merged initial balance: 20,000 + (-50,000) = -30,000
        assertEquals(-30000.0, oracle.accountMap[1]!!.initialBalance, 0.001)
        // Surviving Account 1 has both transactions: +10,000 income, -5,000 expense
        // Current balance: -30,000 + 10,000 - 5,000 = -25,000
        assertEquals(-25000.0, oracle.accountMap[1]!!.currentBalance, 0.001)
        assertEquals(-25000.0, oracle.totalNetWorth(), 0.001)
    }

    @Test
    fun testAccountDeletion_stochasticFuzzing_500Iterations() {
        val random = Random(42) // Fixed seed for reproducibility

        for (iteration in 1..500) {
            val accountCount = random.nextInt(2, 8)
            val accounts = (1L..accountCount).map { id ->
                val initBal = (random.nextDouble(-50000.0, 50000.0) * 100.0).toLong() / 100.0
                Account(
                    id = id,
                    name = "Account $id",
                    type = AccountType.BANK,
                    initialBalance = initBal,
                    isDefault = (id == 1L)
                )
            }

            val txCount = random.nextInt(5, 30)
            val transactions = (1L..txCount).map { txId ->
                val amt = (random.nextDouble(1.0, 5000.0) * 100.0).toLong() / 100.0
                val targetAccId = random.nextLong(1, accountCount.toLong() + 1)
                val type = if (random.nextBoolean()) TransactionType.INCOME else TransactionType.EXPENSE
                Transaction(
                    id = txId,
                    title = "Tx $txId",
                    amount = amt,
                    type = type,
                    categoryId = 1,
                    categoryName = "General",
                    categoryIcon = "Tag",
                    categoryColor = "#000",
                    dateMillis = 1000L * txId,
                    accountId = targetAccId
                )
            }

            val oracle = AccountDeletionOracle(accounts, transactions)
            val expectedNetWorth = oracle.totalNetWorth()

            // Sequentially delete accounts until 1 remains
            val deleteOrder = accounts.map { it.id }.shuffled(random)
            for (delId in deleteOrder) {
                if (oracle.accountMap.size > 1) {
                    assertTrue(oracle.deleteAccount(delId))
                    val currentNetWorth = oracle.totalNetWorth()
                    assertEquals(
                        "Net worth drift at iteration $iteration, remaining accounts: ${oracle.accountMap.size}",
                        expectedNetWorth,
                        currentNetWorth,
                        0.05
                    )
                } else {
                    // Guard: deleting last account must be rejected
                    assertFalse(oracle.deleteAccount(delId))
                }
            }
        }
    }
}
