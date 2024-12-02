package com.example.pomodojo.core.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.pomodojo.R
import com.example.pomodojo.functionality.pomodoro.ui.WorkTimeActivity

/**
 * Helper object for managing notifications.
 */
internal object NotificationsHelper {

    private const val NOTIFICATION_CHANNEL_ID = "general_notification_channel"

    /**
     * Creates a notification channel.
     *
     * @param context The context to use for creating the notification channel.
     */
    fun createNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "pomodojo_timer_channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Builds a notification.
     *
     * @param context The context to use for building the notification.
     * @param title The title of the notification.
     * @param text The text content of the notification.
     * @return The built notification.
     */
    fun buildNotification(context: Context, title: String, text: String): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(Intent(context, WorkTimeActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            })
            .setOngoing(true)
            .build()
    }
}