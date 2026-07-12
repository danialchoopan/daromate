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

/**
 * NotificationUtils - Handles all notification-related operations
 *
 * This utility manages:
 * - Notification channel creation (required for Android 8+)
 * - Medicine reminder notifications with action buttons
 * - Low stock alert notifications
 * - Device vibration
 * - Wake lock management for critical alerts
 *
 * Version compatibility:
 * - Android 5-7: Basic notifications with vibration
 * - Android 8+: Requires notification channels
 * - Android 10+: Supports full-screen intent for lock screen
 * - Android 12+: Requires POST_NOTIFICATIONS permission
 * - Android 13+: Requires runtime permission request
 */
object NotificationUtils {
    // Channel IDs for notification categories
    private const val CHANNEL_REMINDER = "medicine_reminder_channel"
    private const val CHANNEL_LOW_STOCK = "low_stock_channel"
    private const val GROUP_KEY = "medicine_group"

    /**
     * Creates notification channels for Android 8+ (API 26+)
     * Must be called on app startup before any notifications are shown.
     * Channels cannot be deleted once created - only updated.
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Alarm sound configuration
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            // High priority channel - medication reminders
            // Shows on lock screen, plays alarm sound, vibrates
            val reminderCh = NotificationChannel(
                CHANNEL_REMINDER,
                "یادآوری دارو",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "اعلانات یادآوری مصرف دارو"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500) // Vibrate pattern: wait, vibrate, wait, vibrate
                enableLights(true)
                lightColor = android.graphics.Color.GREEN
                setSound(alarmSound, audioAttr)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // Default priority - low stock alerts
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

    /**
     * Shows a medicine reminder notification with action buttons
     *
     * @param context - Application context
     * @param reminderId - Unique ID for the reminder (used for notification ID and pending intents)
     * @param medicineName - Name of the medicine to display
     * @param dosage - Dosage information (optional)
     * @param instruction - Usage instructions (optional)
     *
     * Features:
     * - BigTextStyle for expanded view showing full details
     * - "مصرف شد" (Taken) action button
     * - "یادآوری مجدد" (Snooze) action button
     * - Full-screen intent for Android 10+ (wakes device)
     * - Alarm sound + vibration pattern
     */
    fun showMedicineReminder(
        context: Context,
        reminderId: Int,
        medicineName: String,
        dosage: String = "",
        instruction: String = ""
    ) {
        // Build the expanded notification text
        val contentText = buildString {
            append("وقت مصرف $medicineName رسیده")
            if (dosage.isNotBlank()) append("\nدوز: $dosage")
            if (instruction.isNotBlank()) append("\nدستورالعمل: $instruction")
        }

        // Full-screen intent - shows on lock screen for Android 10+
        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openTab", "dashboard")
            putExtra("reminderId", reminderId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, reminderId, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Content tap - opens app
        val contentPendingIntent = PendingIntent.getActivity(
            context, reminderId, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Taken" action - marks medicine as taken
        val takenIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("reminderId", reminderId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context, reminderId + 1000, takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Snooze" action - reminds again in 10 minutes
        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("reminderId", reminderId)
            putExtra("medicineName", medicineName)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, reminderId + 2000, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Dismiss" action - cancels notification
        val dismissIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_DISMISS"
            putExtra("reminderId", reminderId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, reminderId + 3000, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification with all features
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
            .setColor(0xFF4CAF50.toInt()) // Green accent color

        // Sound configuration
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        builder.setSound(alarmSound)
        builder.setVibrate(longArrayOf(0, 500, 200, 500))

        // Full-screen intent for Android 10+ (shows on lock screen)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        // Show the notification
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(reminderId, builder.build())

        // Extra vibration for reliability
        vibrateDevice(context)
    }

    /**
     * Shows a low stock alert notification
     * Triggered by LowInventoryWorker when stock falls below threshold
     */
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
            .setColor(0xFFF57C00.toInt()) // Orange accent color

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(medicineId + 3000, builder.build())
    }

    /** Cancel a specific notification by ID */
    fun cancelNotification(context: Context, notificationId: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notificationId)
    }

    /** Cancel all notifications */
    fun cancelAllNotifications(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    /**
     * Vibrate the device with a custom pattern
     * Works on all Android versions with proper API handling
     */
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

    /**
     * Acquire a wake lock to ensure screen turns on for critical notifications
     * Must be released after use to avoid battery drain
     */
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
