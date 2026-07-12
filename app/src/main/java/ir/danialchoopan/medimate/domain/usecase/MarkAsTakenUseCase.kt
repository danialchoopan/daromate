package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.domain.model.MedicationLog
import ir.danialchoopan.medimate.domain.repository.LogRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import ir.danialchoopan.medimate.util.ReminderScheduler
import javax.inject.Inject

/**
 * MarkAsTakenUseCase - Handles marking a medication as taken
 *
 * This use case performs three operations:
 * 1. Logs the medication event as TAKEN
 * 2. Decrements inventory stock
 * 3. Calculates and schedules the next reminder
 *
 * Called when user taps "مصرف شد" (Taken) on a notification
 */
class MarkAsTakenUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val reminderRepository: ReminderRepository,
    private val logRepository: LogRepository
) {
    suspend operator fun invoke(reminderId: Int, takenTime: Long) {
        // Get the reminder details
        val reminder = reminderRepository.getReminderById(reminderId) ?: return

        // Step 1: Log the taken event
        logRepository.insertLog(
            MedicationLog(
                medicineId = reminder.medicineId,
                reminderTime = reminder.nextReminderTime,
                takenTime = takenTime,
                status = LogStatus.TAKEN
            )
        )

        // Step 2: Decrement inventory
        val inventory = medicineRepository.getInventoryByMedicineId(reminder.medicineId)
        inventory?.let {
            if (it.currentStock > 0) {
                medicineRepository.updateInventory(it.copy(currentStock = it.currentStock - 1))
            }
        }

        // Step 3: Calculate and update next reminder time
        val nextTime = ReminderScheduler.calculateNextReminderTime(reminder, reminder.nextReminderTime)
        reminderRepository.updateReminder(reminder.copy(nextReminderTime = nextTime))

        // Note: Actual alarm scheduling is done by ReminderReceiver after this use case
    }
}
