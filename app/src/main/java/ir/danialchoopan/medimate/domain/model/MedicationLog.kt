package ir.danialchoopan.medimate.domain.model

data class MedicationLog(
    val id: Int = 0,
    val medicineId: Int,
    val reminderTime: Long,
    val takenTime: Long?,
    val status: LogStatus
)

enum class LogStatus {
    TAKEN, MISSED, SNOOZED
}
