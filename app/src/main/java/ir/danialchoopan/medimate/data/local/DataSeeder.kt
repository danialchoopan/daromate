package ir.danialchoopan.medimate.data.local

import android.content.Context
import ir.danialchoopan.medimate.data.local.dao.InventoryDao
import ir.danialchoopan.medimate.data.local.dao.MedicationLogDao
import ir.danialchoopan.medimate.data.local.dao.MedicineDao
import ir.danialchoopan.medimate.data.local.dao.ReminderDao
import ir.danialchoopan.medimate.data.local.entities.InventoryEntity
import ir.danialchoopan.medimate.data.local.entities.MedicationLogEntity
import ir.danialchoopan.medimate.data.local.entities.MedicineEntity
import ir.danialchoopan.medimate.data.local.entities.ReminderEntity
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.util.ReminderScheduler
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * DataSeeder - Populates the database with sample data on first launch
 *
 * Creates:
 * - 5 sample medicines with Persian names
 * - Associated reminders for each
 * - Inventory records
 * - Some historical medication logs
 *
 * Only runs if the medicines table is empty
 */
object DataSeeder {

    suspend fun seedIfNeeded(
        context: Context,
        medicineDao: MedicineDao,
        reminderDao: ReminderDao,
        inventoryDao: InventoryDao,
        logDao: MedicationLogDao
    ) {
        // Only seed if database is empty
        if (medicineDao.getAllMedicines().first().isNotEmpty()) return

        // Sample medicines with Farsi names
        val medicines = listOf(
            MedicineEntity(
                name = "استامینوفن",
                description = "مسکن و ضد درد",
                dosage = "500mg",
                form = "قرص",
                instruction = "بعد از غذا",
                reason = "سردرد و درد بدن",
                color = 0xFF4CAF50.toInt()
            ),
            MedicineEntity(
                name = "اموکسی‌سیلین",
                description = "آنتی‌بیوتیک",
                dosage = "500mg",
                form = "کپسول",
                instruction = "هر 8 ساعت",
                reason = "عفونت باکتریایی",
                color = 0xFF2196F3.toInt()
            ),
            MedicineEntity(
                name = "متفورمین",
                description = "کنترل قند خون",
                dosage = "850mg",
                form = "قرص",
                instruction = "قبل از غذا",
                reason = "دیابت نوع 2",
                color = 0xFFFF9800.toInt()
            ),
            MedicineEntity(
                name = "لوزارتان",
                description = "کاهش فشار خون",
                dosage = "50mg",
                form = "قرص",
                instruction = "هر روز صبح",
                reason = "فشار خون بالا",
                color = 0xFF9C27B0.toInt()
            ),
            MedicineEntity(
                name = "ویتامین D",
                description = "مکمل غذایی",
                dosage = "1000 IU",
                form = "کپسول",
                instruction = "با صبحانه",
                reason = "کمبود ویتامین D",
                color = 0xFFFFC107.toInt()
            )
        )

        // Insert medicines and get their IDs
        val medicineIds = medicines.map { medicineDao.insertMedicine(it).toInt() }

        // Create reminders for each medicine
        val now = System.currentTimeMillis()
        val reminders = listOf(
            // استامینوفن - every 6 hours
            ReminderEntity(
                medicineId = medicineIds[0],
                intervalType = IntervalType.HOURS,
                intervalValue = 6,
                nextReminderTime = now + TimeUnit.HOURS.toMillis(2),
                isActive = true,
                cycleOnDays = 0,
                cycleOffDays = 0
            ),
            // اموکسی‌سیلین - every 8 hours
            ReminderEntity(
                medicineId = medicineIds[1],
                intervalType = IntervalType.HOURS,
                intervalValue = 8,
                nextReminderTime = now + TimeUnit.HOURS.toMillis(4),
                isActive = true,
                cycleOnDays = 0,
                cycleOffDays = 0
            ),
            // متفورمین - twice daily
            ReminderEntity(
                medicineId = medicineIds[2],
                intervalType = IntervalType.DAYS,
                intervalValue = 1,
                nextReminderTime = now + TimeUnit.HOURS.toMillis(1),
                isActive = true,
                cycleOnDays = 0,
                cycleOffDays = 0
            ),
            // لوزارتان - once daily
            ReminderEntity(
                medicineId = medicineIds[3],
                intervalType = IntervalType.DAYS,
                intervalValue = 1,
                nextReminderTime = now + TimeUnit.HOURS.toMillis(8),
                isActive = true,
                cycleOnDays = 0,
                cycleOffDays = 0
            ),
            // ویتامین D - every other day
            ReminderEntity(
                medicineId = medicineIds[4],
                intervalType = IntervalType.EVEN_DAYS,
                intervalValue = 1,
                nextReminderTime = now + TimeUnit.DAYS.toMillis(1),
                isActive = true,
                cycleOnDays = 0,
                cycleOffDays = 0
            )
        )

        reminders.forEach { reminderDao.insertReminder(it) }

        // Create inventory records
        val inventoryItems = listOf(
            InventoryEntity(medicineId = medicineIds[0], currentStock = 20, lowStockThreshold = 5),
            InventoryEntity(medicineId = medicineIds[1], currentStock = 8, lowStockThreshold = 5),
            InventoryEntity(medicineId = medicineIds[2], currentStock = 3, lowStockThreshold = 5), // Low stock!
            InventoryEntity(medicineId = medicineIds[3], currentStock = 15, lowStockThreshold = 5),
            InventoryEntity(medicineId = medicineIds[4], currentStock = 25, lowStockThreshold = 10)
        )
        inventoryItems.forEach { inventoryDao.upsert(it) }

        // Create some historical logs (past week)
        val logs = mutableListOf<MedicationLogEntity>()
        for (daysAgo in 7 downTo 1) {
            val dayStart = now - TimeUnit.DAYS.toMillis(daysAgo.toLong())

            // استامینوفن - taken most days
            logs.add(MedicationLogEntity(
                medicineId = medicineIds[0],
                reminderTime = dayStart + TimeUnit.HOURS.toMillis(8),
                takenTime = dayStart + TimeUnit.HOURS.toMillis(8) + TimeUnit.MINUTES.toMillis(15),
                status = if (daysAgo != 3) LogStatus.TAKEN else LogStatus.MISSED
            ))

            // اموکسی‌سیلین - taken regularly
            logs.add(MedicationLogEntity(
                medicineId = medicineIds[1],
                reminderTime = dayStart + TimeUnit.HOURS.toMillis(10),
                takenTime = dayStart + TimeUnit.HOURS.toMillis(10) + TimeUnit.MINUTES.toMillis(5),
                status = LogStatus.TAKEN
            ))

            // متفورمین - mixed
            logs.add(MedicationLogEntity(
                medicineId = medicineIds[2],
                reminderTime = dayStart + TimeUnit.HOURS.toMillis(7),
                takenTime = if (daysAgo % 2 == 0) dayStart + TimeUnit.HOURS.toMillis(7) + TimeUnit.MINUTES.toMillis(30) else null,
                status = if (daysAgo % 2 == 0) LogStatus.TAKEN else LogStatus.MISSED
            ))

            // لوزارتان - mostly taken
            logs.add(MedicationLogEntity(
                medicineId = medicineIds[3],
                reminderTime = dayStart + TimeUnit.HOURS.toMillis(9),
                takenTime = if (daysAgo != 5) dayStart + TimeUnit.HOURS.toMillis(9) + TimeUnit.MINUTES.toMillis(10) else null,
                status = if (daysAgo != 5) LogStatus.TAKEN else LogStatus.SNOOZED
            ))
        }
        logs.forEach { logDao.insertLog(it) }
    }
}
