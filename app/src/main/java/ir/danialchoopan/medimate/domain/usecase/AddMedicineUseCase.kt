package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository

class AddMedicineUseCase(
    private val medicineRepository: MedicineRepository,
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        medicine: Medicine,
        reminders: List<Reminder>,
        inventory: Inventory?
    ) {
        val medicineId = medicineRepository.insertMedicine(medicine).toInt()

        reminders.forEach { reminder ->
            reminderRepository.insertReminder(reminder.copy(medicineId = medicineId))
        }

        inventory?.let {
            medicineRepository.updateInventory(it.copy(medicineId = medicineId))
        }
    }
}
