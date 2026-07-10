package ir.danialchoopan.medimate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.danialchoopan.medimate.data.local.dao.DrugInteractionDao
import ir.danialchoopan.medimate.data.local.dao.InventoryDao
import ir.danialchoopan.medimate.data.local.dao.MedicationLogDao
import ir.danialchoopan.medimate.data.local.dao.MedicineDao
import ir.danialchoopan.medimate.data.local.dao.ReminderDao
import ir.danialchoopan.medimate.data.local.entities.DrugInteractionEntity
import ir.danialchoopan.medimate.data.local.entities.InventoryEntity
import ir.danialchoopan.medimate.data.local.entities.MedicationLogEntity
import ir.danialchoopan.medimate.data.local.entities.MedicineEntity
import ir.danialchoopan.medimate.data.local.entities.ReminderEntity

@Database(
    entities = [
        MedicineEntity::class,
        ReminderEntity::class,
        MedicationLogEntity::class,
        InventoryEntity::class,
        DrugInteractionEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun reminderDao(): ReminderDao
    abstract fun medicationLogDao(): MedicationLogDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun drugInteractionDao(): DrugInteractionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medimate.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
