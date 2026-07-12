package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * AddMedicineUseCase - Handles adding a new medicine with its reminders and inventory
 *
 * This use case performs three operations atomically:
 * 1. Inserts the medicine record
 * 2. Creates associated reminders with the medicine ID
 * 3. Optionally creates inventory tracking
 *
 * Returns the saved reminders with their database IDs for alarm scheduling
 */
class AddMedicineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        medicine: Medicine,
        reminders: List<Reminder>,
        inventory: Inventory?
    ): List<Reminder> {
        // Step 1: Insert medicine and get its auto-generated ID
        val medicineId = medicineRepository.insertMedicine(medicine).toInt()

        // Step 2: Insert reminders with the correct medicine ID
        val savedReminders = reminders.map { reminder ->
            val savedId = reminderRepository.insertReminder(reminder.copy(medicineId = medicineId)).toInt()
            reminder.copy(id = savedId, medicineId = medicineId)
        }

        // Step 3: Insert inventory if provided
        inventory?.let {
            medicineRepository.updateInventory(it.copy(medicineId = medicineId))
        }

        return savedReminders
    }
}
