package ir.danialchoopan.medimate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.danialchoopan.medimate.data.local.dao.MedicationLogDao
import ir.danialchoopan.medimate.data.local.dao.MedicineDao
import ir.danialchoopan.medimate.data.local.dao.ReminderDao
import ir.danialchoopan.medimate.data.local.entities.InventoryEntity
import ir.danialchoopan.medimate.data.local.entities.MedicationLogEntity
import ir.danialchoopan.medimate.data.local.entities.MedicineEntity
import ir.danialchoopan.medimate.data.local.entities.ReminderEntity

@Database(
    entities = [
        MedicineEntity::class,
        ReminderEntity::class,
        MedicationLogEntity::class,
        InventoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun reminderDao(): ReminderDao
    abstract fun medicationLogDao(): MedicationLogDao
}
