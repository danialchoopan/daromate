package ir.nimaali.medimate.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import ir.nimaali.medimate.data.AppDatabase
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.ui.ReminderScheduler
import ir.nimaali.medimate.util.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // دریافت داده‌های ورودی
            val medicineName = inputData.getString("medicine_name") ?: ""
            val reminderId = inputData.getInt("reminder_id", 0)
            val reminderTime = inputData.getLong("reminder_time", 0L)

            // نمایش نوتیفیکیشن
            NotificationUtils.showNotification(
                context = applicationContext,
                medicineName = medicineName,
                reminderTime = reminderTime
            )

            // زمان‌بندی یادآوری بعدی
            scheduleNextReminder(reminderId)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun scheduleNextReminder(reminderId: Int) {
        withContext(Dispatchers.IO) {
            val database = AppDatabase.getInstance(applicationContext)
            val reminder = database.reminderDao().getReminderById(reminderId).last() ?: return@withContext
            val medicine = database.medicineDao().getMedicineById(reminder.medicineId).last() ?: return@withContext

            // محاسبه زمان یادآوری بعدی
            val nextReminderTime = calculateNextReminderTime(
                currentTime = reminder.nextReminderTime,
                intervalType = reminder.intervalType,
                intervalValue = reminder.intervalValue
            )

            // آپدیت در دیتابیس
            database.reminderDao().update(
                reminder.copy(nextReminderTime = nextReminderTime)
            )

            // زمان‌بندی یادآوری بعدی
            ReminderScheduler.scheduleReminder(
                context = applicationContext,
                reminder = reminder.copy(nextReminderTime = nextReminderTime),
                medicineName = medicine.name
            )
        }
    }

    private fun calculateNextReminderTime(
        currentTime: Long,
        intervalType: IntervalType,
        intervalValue: Int
    ): Long {
        return when (intervalType) {
            IntervalType.MINUTES -> currentTime + intervalValue * 60 * 1000L
            IntervalType.HOURS -> currentTime + intervalValue * 60 * 60 * 1000L
            IntervalType.DAYS -> currentTime + intervalValue * 24 * 60 * 60 * 1000L
            IntervalType.WEEKS -> currentTime + intervalValue * 7 * 24 * 60 * 60 * 1000L
        }
    }

    companion object {
        fun createWorkRequest(
            medicineName: String,
            reminderId: Int,
            reminderTime: Long,
            delay: Long
        ): OneTimeWorkRequest {
            val inputData = Data.Builder()
                .putString("medicine_name", medicineName)
                .putInt("reminder_id", reminderId)
                .putLong("reminder_time", reminderTime)
                .build()

            return OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("reminder_$reminderId")
                .build()
        }
    }
}