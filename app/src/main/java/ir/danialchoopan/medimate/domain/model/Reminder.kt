package ir.danialchoopan.medimate.domain.model

/**
 * Reminder - Domain model for medication scheduling
 *
 * Supports multiple interval types:
 * - MINUTES/HOURS/DAYS/WEEKS: Simple time-based intervals
 * - EVEN_DAYS/ODD_DAYS: Calendar day parity
 * - CYCLE: Custom on/off pattern (e.g., 5 days on, 2 days off)
 *
 * The nextReminderTime is stored in UTC milliseconds
 */
data class Reminder(
    val id: Int = 0,
    val medicineId: Int,
    val intervalType: IntervalType,
    val intervalValue: Int,
    val nextReminderTime: Long,
    val isActive: Boolean = true,
    val cycleOnDays: Int = 0,
    val cycleOffDays: Int = 0
)
