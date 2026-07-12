package ir.danialchoopan.medimate.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.Reminder
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * ReminderScheduler - Manages alarm scheduling for medication reminders
 *
 * This utility handles:
 * - Scheduling exact alarms for medication times
 * - Canceling alarms
 * - Calculating next reminder time based on interval type
 *
 * Android version compatibility:
 * - Android 5-11: Uses setExactAndAllowWhileIdle()
 * - Android 12+: Requires canScheduleExactAlarms() check
 * - Android 13+: May need SCHEDULE_EXACT_ALARM permission
 *
 * All timestamps are stored in UTC for timezone independence
 */
object ReminderScheduler {

    /**
     * Schedule an alarm for a medication reminder
     *
     * @param context - Application context
     * @param reminder - Reminder object with timing details
     * @param medicineName - Name to display in notification
     *
     * Uses setExactAndAllowWhileIdle() to bypass Doze mode.
     * Falls back to setAndAllowWhileIdle() if exact alarm permission is denied.
     */
    fun scheduleReminder(context: Context, reminder: Reminder, medicineName: String) {
        // Don't schedule alarms in the past
        if (reminder.nextReminderTime <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminderId", reminder.id)
            putExtra("medicineName", medicineName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Check if we can schedule exact alarms (Android 12+)
        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        if (canScheduleExact) {
            // Use exact alarm (preferred for medication reminders)
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.nextReminderTime,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Fallback if exact alarm permission revoked
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.nextReminderTime,
                    pendingIntent
                )
            }
        } else {
            // Use inexact alarm as fallback
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.nextReminderTime,
                pendingIntent
            )
        }
    }

    /** Cancel a scheduled alarm by reminder ID */
    fun cancelReminder(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, reminderId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Schedule a daily recurring alarm at a specific time
     * Used for fixed daily reminders (e.g., every day at 8:00 AM)
     */
    fun scheduleDailyReminder(context: Context, reminderId: Int, hour: Int, minute: Int, medicineName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
            putExtra("medicineName", medicineName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, reminderId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set alarm for next occurrence of the specified time
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            // If time already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else true

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    /**
     * Calculate the next reminder time based on interval type
     *
     * @param reminder - Current reminder with interval configuration
     * @param lastTime - Timestamp of the last/triggered reminder
     * @return Timestamp (UTC) of the next reminder
     *
     * Supports:
     * - MINUTES, HOURS, DAYS, WEEKS: Simple time addition
     * - EVEN_DAYS: Next even-numbered day of month
     * - ODD_DAYS: Next odd-numbered day of month
     * - CYCLE: Custom on/off cycle (e.g., 5 days on, 2 days off)
     */
    fun calculateNextReminderTime(reminder: Reminder, lastTime: Long): Long {
        val utcZone = ZoneOffset.UTC
        val instant = Instant.ofEpochMilli(lastTime)
        val zonedDateTime = instant.atZone(utcZone)

        return when (reminder.intervalType) {
            IntervalType.MINUTES -> lastTime + reminder.intervalValue * 60 * 1000L
            IntervalType.HOURS -> lastTime + reminder.intervalValue * 60 * 60 * 1000L
            IntervalType.DAYS -> lastTime + reminder.intervalValue * 24 * 60 * 60 * 1000L
            IntervalType.WEEKS -> lastTime + reminder.intervalValue * 7 * 24 * 60 * 60 * 1000L
            IntervalType.EVEN_DAYS -> getNextEvenOrOddDay(zonedDateTime, true)
            IntervalType.ODD_DAYS -> getNextEvenOrOddDay(zonedDateTime, false)
            IntervalType.CYCLE -> calculateCycleNextTime(reminder, zonedDateTime)
        }
    }

    /** Find the next even or odd day of the month */
    private fun getNextEvenOrOddDay(start: ZonedDateTime, even: Boolean): Long {
        var current = start.plusDays(1)
        while (true) {
            val day = current.dayOfMonth
            val isEven = day % 2 == 0
            if (isEven == even) return current.toInstant().toEpochMilli()
            current = current.plusDays(1)
        }
    }

    /**
     * Calculate next time for cycle-based reminders
     * Example: 5 days on, 2 days off = 7 day cycle
     */
    private fun calculateCycleNextTime(reminder: Reminder, start: ZonedDateTime): Long {
        val cycleLength = reminder.cycleOnDays + reminder.cycleOffDays
        if (cycleLength <= 0) return start.plusDays(1).toInstant().toEpochMilli()

        val nowUtc = Instant.now().atZone(ZoneOffset.UTC)
        val daysSinceEpoch = nowUtc.toLocalDate().toEpochDay().toInt()
        val dayInCycle = daysSinceEpoch.mod(cycleLength)

        var next = start.plusDays(1)
        val nextDaysSinceEpoch = next.toLocalDate().toEpochDay().toInt()
        val nextDayInCycle = nextDaysSinceEpoch.mod(cycleLength)

        // If next day is in "off" period, skip to next "on" period
        if (nextDayInCycle >= reminder.cycleOnDays) {
            val daysToSkip = cycleLength - nextDayInCycle
            next = next.plusDays(daysToSkip.toLong())
        }

        return next.toInstant().toEpochMilli()
    }
}
