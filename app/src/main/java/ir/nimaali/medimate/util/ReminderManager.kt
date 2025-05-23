package ir.nimaali.medimate.util

import android.content.Context
import ir.nimaali.medimate.app.MedicineApplication
import ir.nimaali.medimate.data.dao.ReminderDao
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.ui.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderManager(
    private val reminderDao: ReminderDao,
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun updateNextReminderTime(reminderId: Int) {
        scope.launch {
            val reminder = reminderDao.getReminderById(reminderId).first() ?: return@launch

            val nextTime = calculateNextReminderTime(
                startTime = reminder.nextReminderTime,
                intervalType = reminder.intervalType,
                intervalValue = reminder.intervalValue
            )

            reminderDao.update(reminder.copy(nextReminderTime = nextTime))

            // اگر نیاز به زمان‌بندی مجدد دارید
            if (reminder.isActive) {
                val medicine = getMedicineForReminder(reminder.medicineId)
                medicine?.let {
                    ReminderScheduler.scheduleReminder(
                        context = context,
                        reminder = reminder.copy(nextReminderTime = nextTime),
                        medicineName = it.name
                    )
                }
            }
        }
    }

    private suspend fun getMedicineForReminder(medicineId: Int): Medicine? {
        val app = context.applicationContext as MedicineApplication
        return app.database.medicineDao().getMedicineById(medicineId).first()
    }

    private fun calculateNextReminderTime(
        startTime: Long,
        intervalType: IntervalType,
        intervalValue: Int
    ): Long {
        val intervalMillis = when (intervalType) {
            IntervalType.MINUTES -> intervalValue * 60 * 1000L
            IntervalType.HOURS -> intervalValue * 60 * 60 * 1000L
            IntervalType.DAYS -> intervalValue * 24 * 60 * 60 * 1000L
            IntervalType.WEEKS -> intervalValue * 7 * 24 * 60 * 60 * 1000L
        }
        return startTime + intervalMillis
    }

    fun cancel() {
        scope.cancel()
    }
}