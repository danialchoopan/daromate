package ir.danialchoopan.medimate.util

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import ir.danialchoopan.medimate.R
import ir.danialchoopan.medimate.presentation.MainActivity

object NotificationUtils {
    private const val CHANNEL_REMINDER = "medicine_reminder_channel"
    private const val CHANNEL_LOW_STOCK = "low_stock_channel"
    private const val GROUP_KEY = "medicine_group"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            // High priority - medication reminders
            val reminderCh = NotificationChannel(
                CHANNEL_REMINDER,
                "یادآوری دارو",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "اعلانات یادآوری مصرف دارو"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
                lightColor = android.graphics.Color.GREEN
                setSound(alarmSound, audioAttr)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setBypassDnd(true)
                }
            }

            // Default - low stock alerts
            val lowStockCh = NotificationChannel(
                CHANNEL_LOW_STOCK,
                "موجودی کم",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "اعلانات موجودی کم دارو"
                enableVibration(true)
            }

            nm.createNotificationChannel(reminderCh)
            nm.createNotificationChannel(lowStockCh)
        }
    }

    fun showMedicineReminder(
        context: Context,
        reminderId: Int,
        medicineName: String,
        dosage: String = "",
        instruction: String = ""
    ) {
        val contentText = buildString {
            append("وقت مصرف $medicineName رسیده")
            if (dosage.isNotBlank()) append("\nدوز: $dosage")
            if (instruction.isNotBlank()) append("\nدستورالعمل: $instruction")
        }

        // Full-screen intent for Android 10+
        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openTab", "dashboard")
            putExtra("reminderId", reminderId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, reminderId, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Content tap
        val contentPendingIntent = PendingIntent.getActivity(
            context, reminderId, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Taken
        val takenIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("reminderId", reminderId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context, reminderId + 1000, takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze
        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("reminderId", reminderId)
            putExtra("medicineName", medicineName)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, reminderId + 2000, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss
        val dismissIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_DISMISS"
            putExtra("reminderId", reminderId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, reminderId + 3000, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.baseline_medication_liquid_24_green)
            .setLargeIcon(android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.baseline_medication_liquid_24_green))
            .setContentTitle("یادآوری دارو")
            .setContentText("وقت مصرف $medicineName رسیده")
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY)
            .setContentIntent(contentPendingIntent)
            .addAction(R.drawable.baseline_medication_liquid_24_green, "مصرف شد", takenPendingIntent)
            .addAction(R.drawable.baseline_medication_liquid_24_green, "یادآوری مجدد", snoozePendingIntent)
            .setDeleteIntent(dismissPendingIntent)
            .setColor(0xFF4CAF50.toInt())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Sound + vibration
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        builder.setSound(alarmSound)
        builder.setVibrate(longArrayOf(0, 500, 200, 500))

        // Full-screen intent for Android 10+ (heads-up notification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        // Heads-up for Android 5-9
        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.LOLLIPOP until Build.VERSION_CODES.Q) {
            builder.setVibrate(longArrayOf(0, 500, 200, 500))
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(reminderId, builder.build())

        // Vibrate device directly (extra safety)
        vibrateDevice(context)
    }

    fun showLowStockNotification(
        context: Context,
        medicineId: Int,
        medicineName: String,
        currentStock: Int
    ) {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openTab", "inventory")
        }
        val pendingContentIntent = PendingIntent.getActivity(
            context, medicineId, contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_LOW_STOCK)
            .setSmallIcon(R.drawable.baseline_medication_liquid_24_green)
            .setContentTitle("موجودی کم: $medicineName")
            .setContentText("فقط $currentStock عدد باقی مانده!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("فقط $currentStock عدد از $medicineName باقی مانده. زمان خرید مجدد است."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingContentIntent)
            .setColor(0xFFF57C00.toInt())

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(medicineId + 3000, builder.build())
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notificationId)
    }

    fun cancelAllNotifications(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    fun vibrateDevice(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
        }
        vibrator.vibrate(android.os.VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
    }

    fun acquireWakeLock(context: Context, durationMs: Long = 30_000L): PowerManager.WakeLock? {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "medimate:med Reminder WakeLock"
        )
        wakeLock.acquire(durationMs)
        return wakeLock
    }
}
