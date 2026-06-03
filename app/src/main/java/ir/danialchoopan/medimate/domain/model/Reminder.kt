package ir.danialchoopan.medimate.domain.model

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
