package com.example.finalwork.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * 每日固定时间提醒的调度器（WorkManager 周期任务）。
 * 成员 C（李逸凡）维护。
 */
object ReminderScheduler {

    const val WORK_NAME = "focus_planet_daily_reminder"

    /**
     * 调度每日提醒：首次触发时间对齐到下一个 [hour]:[minute]，之后每 24 小时一次。
     */
    fun schedule(context: Context, hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        var next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val delayMinutes = Duration.between(now, next).toMinutes().coerceAtLeast(1L)

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    /** 取消每日提醒。 */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
