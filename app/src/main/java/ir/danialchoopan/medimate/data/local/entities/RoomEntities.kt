package ir.danialchoopan.medimate.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

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

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey val medicineId: Int,
    val currentStock: Int,
    val lowStockThreshold: Int
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val intervalType: String, // String representation of IntervalType enum
    val intervalValue: Int,
    val nextReminderTime: Long,
    val isActive: Boolean,
    val cycleOnDays: Int,
    val cycleOffDays: Int
)

@Entity(tableName = "medication_logs")
data class MedicationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val reminderTime: Long,
    val takenTime: Long?,
    val status: String // TAKEN, MISSED, SNOOZED
)
