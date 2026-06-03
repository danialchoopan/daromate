package ir.danialchoopan.medimate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.danialchoopan.medimate.data.local.entities.MedicationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationLogDao {
    @Query("SELECT * FROM medication_logs ORDER BY reminderTime DESC")
    fun getAllLogs(): Flow<List<MedicationLogEntity>>

    @Query("SELECT * FROM medication_logs WHERE medicineId = :medicineId ORDER BY reminderTime DESC")
    fun getLogsForMedicine(medicineId: Int): Flow<List<MedicationLogEntity>>

    @Insert
    suspend fun insertLog(log: MedicationLogEntity)

    @Update
    suspend fun updateLog(log: MedicationLogEntity)
}
