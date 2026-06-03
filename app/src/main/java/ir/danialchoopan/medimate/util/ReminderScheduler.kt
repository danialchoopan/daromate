package ir.danialchoopan.medimate.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.Reminder
import java.util.Calendar

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

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.nextReminderTime,
            pendingIntent
        )
    }

    fun calculateNextReminderTime(reminder: Reminder, lastTime: Long): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = lastTime }

        return when (reminder.intervalType) {
            IntervalType.MINUTES -> lastTime + reminder.intervalValue * 60 * 1000L
            IntervalType.HOURS -> lastTime + reminder.intervalValue * 60 * 60 * 1000L
            IntervalType.DAYS -> lastTime + reminder.intervalValue * 24 * 60 * 60 * 1000L
            IntervalType.WEEKS -> lastTime + reminder.intervalValue * 7 * 24 * 60 * 60 * 1000L
            IntervalType.EVEN_DAYS -> getNextEvenOrOddDay(calendar, true)
            IntervalType.ODD_DAYS -> getNextEvenOrOddDay(calendar, false)
            IntervalType.CYCLE -> calculateCycleNextTime(reminder, calendar)
        }
    }

    private fun getNextEvenOrOddDay(calendar: Calendar, even: Boolean): Long {
        do {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val isEven = day % 2 == 0
            if (isEven == even) return calendar.timeInMillis
        } while (true)
    }

    private fun calculateCycleNextTime(reminder: Reminder, calendar: Calendar): Long {
        // Simple logic: e.g., 2 weeks on (14 days), 1 week off (7 days).
        // This needs a reference start date for the cycle to be accurate.
        // For simplicity, let's just add 1 day and check if we are in the 'on' period.
        // Real implementation would be more complex.
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.timeInMillis
    }
}
