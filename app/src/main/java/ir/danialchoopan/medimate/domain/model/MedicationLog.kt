package ir.danialchoopan.medimate.domain.model

/**
 * MedicationLog - Records each medication event
 *
 * Used for:
 * - Tracking adherence (taken vs missed)
 * - Building the Jalali calendar view
 * - Generating adherence reports
 * - CSV export
 */
data class MedicationLog(
    val id: Int = 0,
    val medicineId: Int,
    val reminderTime: Long,    // When the reminder was scheduled
    val takenTime: Long?,      // When it was actually taken (null if missed)
    val status: LogStatus      // TAKEN, MISSED, or SNOOZED
)

enum class LogStatus {
    TAKEN,    // Medicine was taken
    MISSED,   // Medicine was not taken
    SNOOZED   // Medicine reminder was snoozed
}
