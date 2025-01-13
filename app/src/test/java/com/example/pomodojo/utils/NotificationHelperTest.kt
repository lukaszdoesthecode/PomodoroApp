package com.example.pomodojo.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.R
import com.example.pomodojo.core.utils.NotificationsHelper
import com.example.pomodojo.functionality.pomodoro.ui.WorkTimeActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for the NotificationsHelper class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Specify the SDK version to use for the tests
class NotificationHelperTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Tests that the notification channel is created correctly.
     */
    @Test
    fun createNotificationChannel_CreatesChannel() {
        NotificationsHelper.createNotificationChannel(context)

        val channel = notificationManager.getNotificationChannel("general_notification_channel")
        assertNotNull(channel)
        assertEquals("general_notification_channel", channel.id)
        assertEquals("pomodojo_timer_channel", channel.name)
        assertEquals(NotificationManager.IMPORTANCE_LOW, channel.importance)
    }

    /**
     * Tests that the notification is built correctly.
     */
    @Test
    fun buildNotification_BuildsNotification() {
        val title = "Test Title"
        val text = "Test Text"

        val notification = NotificationsHelper.buildNotification(context, title, text)

        assertNotNull(notification)
        assertEquals(title, notification.extras.getString(Notification.EXTRA_TITLE))
        assertEquals(text, notification.extras.getString(Notification.EXTRA_TEXT))
        assertEquals(R.drawable.ic_launcher_foreground, notification.smallIcon.resId)

        val contentIntent = notification.contentIntent
        assertNotNull(contentIntent)
        val expectedIntent = PendingIntent.getActivity(
            context, 0, Intent(context, WorkTimeActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        assertEquals(expectedIntent, contentIntent)
    }
}