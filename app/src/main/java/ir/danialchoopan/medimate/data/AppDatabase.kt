package ir.nimaali.medimate.data

// AppDatabase.kt
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ir.nimaali.medimate.data.dao.MedicineDao
import ir.nimaali.medimate.data.dao.ReminderDao
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.data.table.Reminder

@Database(
    entities = [Medicine::class, Reminder::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medicine_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}