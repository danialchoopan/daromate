package ir.nimaali.medimate.data.table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [ForeignKey(
        entity = Medicine::class,
        parentColumns = ["id"],
        childColumns = ["medicineId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val intervalType: IntervalType,
    val intervalValue: Int, // تعداد ساعت/روز/هفته
    val nextReminderTime: Long, // timestamp
    val isActive: Boolean = true
)

enum class IntervalType {
    HOURS, DAYS, WEEKS, MINUTES
}