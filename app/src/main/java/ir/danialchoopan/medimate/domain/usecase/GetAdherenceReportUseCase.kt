package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class AdherenceReport(
    val totalExpected: Int,
    val totalTaken: Int,
    val adherenceRate: Float
)

class GetAdherenceReportUseCase @Inject constructor(
    private val logRepository: LogRepository
) {
    operator fun invoke(): Flow<AdherenceReport> {
        return logRepository.getAllLogs().map { logs ->
            val total = logs.size
            val taken = logs.count { it.status == LogStatus.TAKEN }
            val rate = if (total > 0) (taken.toFloat() / total) * 100 else 100f
            AdherenceReport(total, taken, rate)
        }
    }
}
