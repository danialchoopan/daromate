package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.domain.model.MedicationLog
import ir.danialchoopan.medimate.domain.repository.LogRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import ir.danialchoopan.medimate.util.ReminderScheduler

class MarkAsTakenUseCase(
    private val medicineRepository: MedicineRepository,
    private val reminderRepository: ReminderRepository,
    private val logRepository: LogRepository
) {
    suspend operator fun invoke(reminderId: Int, takenTime: Long) {
        val reminder = reminderRepository.getReminderById(reminderId) ?: return

        // 1. Log the event
        logRepository.insertLog(
            MedicationLog(
                medicineId = reminder.medicineId,
                reminderTime = reminder.nextReminderTime,
                takenTime = takenTime,
                status = LogStatus.TAKEN
            )
        )

        // 2. Update Inventory
        val inventory = medicineRepository.getInventoryByMedicineId(reminder.medicineId)
        inventory?.let {
            if (it.currentStock > 0) {
                medicineRepository.updateInventory(it.copy(currentStock = it.currentStock - 1))
            }
        }

        // 3. Update next reminder time
        val nextTime = ReminderScheduler.calculateNextReminderTime(reminder, reminder.nextReminderTime)
        reminderRepository.updateReminder(reminder.copy(nextReminderTime = nextTime))

        // Note: The actual scheduling in AlarmManager should be done by the caller
        // who has access to Context, or by a specialized service.
    }
}
