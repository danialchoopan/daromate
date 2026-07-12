package ir.danialchoopan.medimate.domain.model

/**
 * IntervalType - Defines how often a medication should be taken
 *
 * Usage examples:
 * - HOURS with intervalValue=4: Every 4 hours
 * - DAYS with intervalValue=1: Once daily
 * - WEEKS with intervalValue=1: Once weekly
 * - EVEN_DAYS: Every even-numbered day (2nd, 4th, 6th...)
 * - ODD_DAYS: Every odd-numbered day (1st, 3rd, 5th...)
 * - CYCLE: Custom pattern using cycleOnDays/cycleOffDays
 */
enum class IntervalType {
    MINUTES,
    HOURS,
    DAYS,
    WEEKS,
    EVEN_DAYS,
    ODD_DAYS,
    CYCLE // e.g., 2 weeks on, 1 week off
}
