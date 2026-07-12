package ir.danialchoopan.medimate.app

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import ir.danialchoopan.medimate.data.local.DataSeeder
import ir.danialchoopan.medimate.data.local.DrugInteractionSeeder
import ir.danialchoopan.medimate.data.local.dao.DrugInteractionDao
import ir.danialchoopan.medimate.data.local.dao.InventoryDao
import ir.danialchoopan.medimate.data.local.dao.MedicationLogDao
import ir.danialchoopan.medimate.data.local.dao.MedicineDao
import ir.danialchoopan.medimate.data.local.dao.ReminderDao
import ir.danialchoopan.medimate.data.workers.LowInventoryWorker
import ir.danialchoopan.medimate.data.workers.RescheduleAlarmsWorker
import ir.danialchoopan.medimate.util.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MedicineApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var drugInteractionDao: DrugInteractionDao
    @Inject lateinit var medicineDao: MedicineDao
    @Inject lateinit var reminderDao: ReminderDao
    @Inject lateinit var inventoryDao: InventoryDao
    @Inject lateinit var medicationLogDao: MedicationLogDao

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        scheduleLowInventoryCheck()
        scheduleRescheduleAlarms()
        seedDrugInteractions()
        seedSampleData()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setWorkerFactory(workerFactory).build()

    private fun createNotificationChannels() {
        NotificationUtils.createNotificationChannels(this)
    }

    private fun scheduleLowInventoryCheck() {
        val workRequest = PeriodicWorkRequestBuilder<LowInventoryWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "low_inventory_check", ExistingPeriodicWorkPolicy.KEEP, workRequest
        )
    }

    private fun scheduleRescheduleAlarms() {
        val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>().build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun seedDrugInteractions() {
        CoroutineScope(Dispatchers.IO).launch {
            DrugInteractionSeeder.seedIfNeeded(this@MedicineApplication, drugInteractionDao)
        }
    }

    private fun seedSampleData() {
        // Seeding disabled - uncomment to re-enable
        // CoroutineScope(Dispatchers.IO).launch {
        //     DataSeeder.seedIfNeeded(
        //         context = this@MedicineApplication,
        //         medicineDao = medicineDao,
        //         reminderDao = reminderDao,
        //         inventoryDao = inventoryDao,
        //         logDao = medicationLogDao
        //     )
        // }
    }
}
