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

object ReminderScheduler {

    fun scheduleReminder(context: Context, reminder: Reminder, medicineName: String) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.nextReminderTime,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.nextReminderTime,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun calculateNextReminderTime(reminder: Reminder, lastTime: Long): Long {
        val utcZone = ZoneOffset.UTC
        val instant = Instant.ofEpochMilli(lastTime)
        val zonedDateTime = instant.atZone(utcZone)

        return when (reminder.intervalType) {
            IntervalType.MINUTES -> lastTime + reminder.intervalValue * 60 * 1000L
            IntervalType.HOURS -> lastTime + reminder.intervalValue * 60 * 60 * 1000L
            IntervalType.DAYS -> lastTime + reminder.intervalValue * 24 * 60 * 60 * 1000L
            IntervalType.WEEKS -> lastTime + reminder.intervalValue * 7 * 24 * 60 * 60 * 1000L
            IntervalType.EVEN_DAYS -> getNextEvenOrOddDay(zonedDateTime, utcZone, true)
            IntervalType.ODD_DAYS -> getNextEvenOrOddDay(zonedDateTime, utcZone, false)
            IntervalType.CYCLE -> calculateCycleNextTime(reminder, zonedDateTime, utcZone)
        }
    }

    private fun getNextEvenOrOddDay(start: ZonedDateTime, zone: ZoneOffset, even: Boolean): Long {
        var current = start.plusDays(1)
        while (true) {
            val day = current.dayOfMonth
            val isEven = day % 2 == 0
            if (isEven == even) return current.toInstant().toEpochMilli()
            current = current.plusDays(1)
        }
    }

    private fun calculateCycleNextTime(reminder: Reminder, start: ZonedDateTime, zone: ZoneOffset): Long {
        val cycleLength = reminder.cycleOnDays + reminder.cycleOffDays
        if (cycleLength <= 0) {
            return start.plusDays(1).toInstant().toEpochMilli()
        }

        val nowUtc = Instant.now().atZone(zone)
        val daysSinceEpoch = nowUtc.toLocalDate().toEpochDay().toInt()
        val dayInCycle = daysSinceEpoch.mod(cycleLength)

        var next = start.plusDays(1)
        val nextDaysSinceEpoch = next.toLocalDate().toEpochDay().toInt()
        val nextDayInCycle = nextDaysSinceEpoch.mod(cycleLength)

        if (nextDayInCycle >= reminder.cycleOnDays) {
            val daysToSkip = cycleLength - nextDayInCycle
            next = next.plusDays(daysToSkip.toLong())
        }

        return next.toInstant().toEpochMilli()
    }
}
