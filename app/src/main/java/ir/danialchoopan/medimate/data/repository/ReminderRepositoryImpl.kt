package ir.danialchoopan.medimate.data.repository

import ir.danialchoopan.medimate.data.local.dao.ReminderDao
import ir.danialchoopan.medimate.data.local.entities.ReminderEntity
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(private val reminderDao: ReminderDao) : ReminderRepository {
    override fun getRemindersForMedicine(medicineId: Int): Flow<List<Reminder>> =
        reminderDao.getRemindersForMedicine(medicineId).map { entities -> entities.map { it.toDomain() } }

    override fun getAllActiveReminders(): Flow<List<Reminder>> =
        reminderDao.getAllActiveReminders().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getReminderById(id: Int): Reminder? = reminderDao.getReminderById(id)?.toDomain()

    override suspend fun insertReminder(reminder: Reminder): Long = reminderDao.insertReminder(reminder.toEntity())

    override suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder.toEntity())

    override suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder.toEntity())

    private fun ReminderEntity.toDomain() = Reminder(
        id, medicineId, intervalType, intervalValue,
        nextReminderTime, isActive, cycleOnDays, cycleOffDays
    )

    private fun Reminder.toEntity() = ReminderEntity(
        id, medicineId, intervalType, intervalValue,
        nextReminderTime, isActive, cycleOnDays, cycleOffDays
    )
}
