package ir.danialchoopan.medimate.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import ir.danialchoopan.medimate.domain.repository.LogRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    suspend fun exportMedicationHistory(
        context: Context,
        logRepository: LogRepository,
        medicineRepository: MedicineRepository
    ): Uri {
        val logs = logRepository.getAllLogs().first()
        val medicines = medicineRepository.getAllMedicines().first()
        val medicineMap = medicines.associateBy { it.id }

        val exportDir = File(context.filesDir, "exports").apply { mkdirs() }
        val fileName = "medimate_history_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.csv"
        val file = File(exportDir, fileName)

        file.bufferedWriter().use { writer ->
            writer.write("Medicine Name,Dosage,Form,Reminder Time,Taken Time,Status\n")
            logs.forEach { log ->
                val med = medicineMap[log.medicineId]
                writer.write(
                    "${med?.name?.escapeCsv() ?: "Unknown"}," +
                    "${med?.dosage?.escapeCsv() ?: ""}," +
                    "${med?.form?.escapeCsv() ?: ""}," +
                    "${log.reminderTime}," +
                    "${log.takenTime ?: ""}," +
                    "${log.status.name}\n"
                )
            }
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    private fun String.escapeCsv(): String =
        if (contains(",") || contains("\"") || contains("\n"))
            "\"${replace("\"", "\"\"")}\"" else this
}
