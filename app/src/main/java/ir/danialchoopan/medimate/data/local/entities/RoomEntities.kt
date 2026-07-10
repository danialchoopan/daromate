package ir.danialchoopan.medimate.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.LogStatus

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val dosage: String,
    val form: String,
    val instruction: String,
    val reason: String,
    val imageUri: String? = null,
    val color: Int
)

@Entity(
    tableName = "inventory",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InventoryEntity(
    @PrimaryKey val medicineId: Int,
    val currentStock: Int,
    val lowStockThreshold: Int
)

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val intervalType: IntervalType,
    val intervalValue: Int,
    val nextReminderTime: Long,
    val isActive: Boolean,
    val cycleOnDays: Int,
    val cycleOffDays: Int
)

@Entity(
    tableName = "medication_logs",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MedicationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val reminderTime: Long,
    val takenTime: Long?,
    val status: LogStatus
)
