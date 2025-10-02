package ir.nimaali.medimate.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.data.table.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM reminders WHERE medicineId = :medicineId")
    fun getRemindersForMedicine(medicineId: Int): Flow<List<Reminder>>


    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Int): Flow<Reminder?>

    @Query("DELETE FROM reminders WHERE medicineId = :medicineId")
    suspend fun deleteRemindersForMedicine(medicineId: Int): Int
}
