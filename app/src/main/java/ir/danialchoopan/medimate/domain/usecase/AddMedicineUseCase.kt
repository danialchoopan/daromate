package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import javax.inject.Inject

class AddMedicineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        medicine: Medicine,
        reminders: List<Reminder>,
        inventory: Inventory?
    ): List<Reminder> {
        val medicineId = medicineRepository.insertMedicine(medicine).toInt()

        val savedReminders = reminders.map { reminder ->
            val savedId = reminderRepository.insertReminder(reminder.copy(medicineId = medicineId)).toInt()
            reminder.copy(id = savedId, medicineId = medicineId)
        }

        inventory?.let {
            medicineRepository.updateInventory(it.copy(medicineId = medicineId))
        }

        return savedReminders
    }
}
