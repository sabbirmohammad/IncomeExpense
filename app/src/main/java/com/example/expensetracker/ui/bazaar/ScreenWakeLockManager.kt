package com.example.expensetracker.ui.bazaar

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager

interface ScreenWakeLockManager {
    fun acquire(activity: Activity)
    fun release(activity: Activity)
}

object AndroidScreenWakeLockManager : ScreenWakeLockManager {
    override fun acquire(activity: Activity) {
        runCatching {
            activity.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun release(activity: Activity) {
        runCatching {
            activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    fun findActivity(context: Context): Activity? {
        var current: Context? = context
        while (current is ContextWrapper) {
            if (current is Activity) return current
            current = current.baseContext
        }
        return null
    }
}
