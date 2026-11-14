package com.example.expensetracker.data.local

import com.example.expensetracker.data.model.BackupPayload

fun ExpenseDatabaseHelper.hasBudgetsForMonth(month: Int, year: Int): Boolean = budgetHelper.hasBudgetsForMonth(month, year)
fun ExpenseDatabaseHelper.copyBudgets(fromMonth: Int, fromYear: Int, toMonth: Int, toYear: Int): Boolean = budgetHelper.copyBudgets(fromMonth, fromYear, toMonth, toYear)

fun ExpenseDatabaseHelper.createBackupPayload(): BackupPayload = backupHelper.createBackupPayload()
fun ExpenseDatabaseHelper.restoreFromBackup(payload: BackupPayload): Boolean = backupHelper.restoreFromBackup(payload)
