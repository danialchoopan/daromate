package ir.danialchoopan.medimate.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import ir.danialchoopan.medimate.util.ReminderScheduler
import kotlinx.coroutines.flow.first

@HiltWorker
class RescheduleAlarmsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderRepository: ReminderRepository,
    private val medicineRepository: MedicineRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val activeReminders = reminderRepository.getAllActiveReminders().first()
        activeReminders.forEach { reminder ->
            val medicine = medicineRepository.getMedicineById(reminder.medicineId) ?: return@forEach
            if (reminder.nextReminderTime > System.currentTimeMillis()) {
                ReminderScheduler.scheduleReminder(applicationContext, reminder, medicine.name)
            }
        }
        return Result.success()
    }
}
