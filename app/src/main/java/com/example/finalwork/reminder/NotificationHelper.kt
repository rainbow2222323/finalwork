package com.example.finalwork.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.finalwork.R

/**
 * 通知渠道与通知发送。成员 C（李逸凡）维护。
 */
object NotificationHelper {

    const val CHANNEL_ID = "focus_planet_reminder"
    const val NOTIFICATION_ID = 1001

    /** 创建通知渠道（API 26+ 需要）。 */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "每日专注提醒",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "提醒你开始今日专注"
        }
        manager.createNotificationChannel(channel)
    }

    /** 是否已授予通知权限（API 33+ 需要运行时申请）。 */
    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** 发送一条提醒通知。 */
    fun showReminder(context: Context) {
        ensureChannel(context)
        if (!hasNotificationPermission(context)) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("专注星球")
            .setContentText("今天还没有专注记录，开始一段专注吧！")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
