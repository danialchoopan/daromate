package ir.danialchoopan.medimate.domain.repository

import ir.danialchoopan.medimate.domain.model.MedicationLog
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    fun getAllLogs(): Flow<List<MedicationLog>>
    fun getLogsForMedicine(medicineId: Int): Flow<List<MedicationLog>>
    suspend fun insertLog(log: MedicationLog)
    suspend fun updateLog(log: MedicationLog)
}
