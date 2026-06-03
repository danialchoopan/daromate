package ir.danialchoopan.medimate.domain.repository

import ir.danialchoopan.medimate.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getRemindersForMedicine(medicineId: Int): Flow<List<Reminder>>
    fun getAllActiveReminders(): Flow<List<Reminder>>
    suspend fun getReminderById(id: Int): Reminder?
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
}
