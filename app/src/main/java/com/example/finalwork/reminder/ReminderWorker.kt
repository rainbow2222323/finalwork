package com.example.finalwork.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * 每日提醒的后台任务。成员 C（李逸凡）维护。
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.showReminder(applicationContext)
        return Result.success()
    }
}
