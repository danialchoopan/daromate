package ir.danialchoopan.medimate.domain.usecase

import android.content.Context
import android.net.Uri
import ir.danialchoopan.medimate.domain.repository.LogRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.util.CsvExporter
import javax.inject.Inject

class ExportHistoryUseCase @Inject constructor(
    private val logRepository: LogRepository,
    private val medicineRepository: MedicineRepository
) {
    suspend operator fun invoke(context: Context): Uri =
        CsvExporter.exportMedicationHistory(context, logRepository, medicineRepository)
}
