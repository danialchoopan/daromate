package ir.danialchoopan.medimate.data.repository

import ir.danialchoopan.medimate.data.local.dao.MedicationLogDao
import ir.danialchoopan.medimate.data.local.entities.MedicationLogEntity
import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.domain.model.MedicationLog
import ir.danialchoopan.medimate.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LogRepositoryImpl(private val logDao: MedicationLogDao) : LogRepository {
    override fun getAllLogs(): Flow<List<MedicationLog>> = logDao.getAllLogs().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun getLogsForMedicine(medicineId: Int): Flow<List<MedicationLog>> =
        logDao.getLogsForMedicine(medicineId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertLog(log: MedicationLog) = logDao.insertLog(log.toEntity())

    override suspend fun updateLog(log: MedicationLog) = logDao.updateLog(log.toEntity())

    private fun MedicationLogEntity.toDomain() = MedicationLog(
        id, medicineId, reminderTime, takenTime, LogStatus.valueOf(status)
    )

    private fun MedicationLog.toEntity() = MedicationLogEntity(
        id, medicineId, reminderTime, takenTime, status.name
    )
}
