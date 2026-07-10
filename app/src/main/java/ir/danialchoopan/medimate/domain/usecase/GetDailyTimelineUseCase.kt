package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class TimelineItem(
    val medicine: Medicine,
    val reminder: Reminder
)

class GetDailyTimelineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(): Flow<List<TimelineItem>> {
        return combine(
            medicineRepository.getAllMedicines(),
            reminderRepository.getAllActiveReminders()
        ) { medicines, reminders ->
            val medicineMap = medicines.associateBy { it.id }
            reminders.mapNotNull { reminder ->
                medicineMap[reminder.medicineId]?.let { medicine ->
                    TimelineItem(medicine, reminder)
                }
            }.sortedBy { it.reminder.nextReminderTime }
        }
    }
}
