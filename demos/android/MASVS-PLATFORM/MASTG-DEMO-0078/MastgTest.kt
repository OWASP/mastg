package org.owasp.mastestapp

//noinspection SuspiciousImport
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class MastgTest(private val context: Context) {

    val notificationManager =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(
                NotificationChannel(
                    "TEST_CHANNEL_ID",
                    "Test Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

    val sensitiveTitle = "Hi John Doe"
    val sensitiveText = "Hi John Doe <- This is a sensitive string containing PII"
    fun mastgTest(): String {

        notificationManager.notify(1, createNotification())
        notificationManager.notify(2, createNotificationOnChannel())
        notificationManager.notify(3, createNotificationCompat())
        notificationManager.notify(4, createNotificationCompatOnChannel())

        return sensitiveText
    }

    private fun createNotification() = Notification.Builder(context)
        .setContentTitle(sensitiveTitle)
        .setContentText(sensitiveText)
        .setSmallIcon(R.drawable.ic_menu_info_details)
        .build()

    private fun createNotificationOnChannel() = Notification.Builder(context, "TEST_CHANNEL_ID")
        .setContentTitle(sensitiveTitle)
        .setContentText(sensitiveText)
        .setSmallIcon(R.drawable.ic_menu_info_details)
        .build()

    private fun createNotificationCompat() = NotificationCompat.Builder(context)
        .setContentTitle(sensitiveTitle)
        .setContentText(sensitiveText)
        .setSmallIcon(R.drawable.ic_menu_info_details)
        .build()

    private fun createNotificationCompatOnChannel() =
        NotificationCompat.Builder(context, "TEST_CHANNEL_ID")
            .setContentTitle(sensitiveTitle)
            .setContentText(sensitiveText)
            .setSmallIcon(R.drawable.ic_menu_info_details)
            .build()

}
