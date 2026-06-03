package ir.danialchoopan.medimate.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ir.danialchoopan.medimate.R

object NotificationUtils {
    private const val CHANNEL_ID = "medicine_reminder_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medicine Reminders"
            val descriptionText = "Notifications for medication reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showActionableNotification(context: Context, reminderId: Int, medicineName: String) {
        val takenIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("reminderId", reminderId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(context, reminderId + 1000, takenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("reminderId", reminderId)
            putExtra("medicineName", medicineName)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(context, reminderId + 2000, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Use standard icon for now
            .setContentTitle("Medicine Reminder")
            .setContentText("It's time to take your $medicineName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(0, "Taken", takenPendingIntent)
            .addAction(0, "Snooze (10m)", snoozePendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(reminderId, builder.build())
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
