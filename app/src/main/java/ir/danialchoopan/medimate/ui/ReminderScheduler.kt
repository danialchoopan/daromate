package ir.nimaali.medimate.ui


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Reminder
import ir.nimaali.medimate.data.workers.ReminderWorker
import ir.nimaali.medimate.util.ReminderReceiver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        reminder: Reminder,
        medicineName: String,
    ) {
        val currentTime = System.currentTimeMillis()
        val delay = reminder.nextReminderTime - currentTime

        if (delay > 0) {
            val data = workDataOf(
                "medicine_name" to medicineName,
                "reminder_id" to reminder.id,
                "reminder_time" to reminder.nextReminderTime
            )

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .build()

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .setConstraints(constraints)
                .addTag("reminder_${reminder.id}")
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }

    fun scheduleRepeatingReminder(
        context: Context,
        reminder: Reminder,
        medicineName: String
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminder_id", reminder.id)
            putExtra("medicine_name", medicineName)
            putExtra("interval_type", reminder.intervalType.name)
            putExtra("interval_value", reminder.intervalValue)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // استفاده از setExactAndAllowWhileIdle برای دقت بیشتر
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.nextReminderTime,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, reminderId: Int) {
        // لغو کار WorkManager
        WorkManager.getInstance(context).cancelAllWorkByTag("reminder_$reminderId")

        //  لغو آلارم
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

    }

}