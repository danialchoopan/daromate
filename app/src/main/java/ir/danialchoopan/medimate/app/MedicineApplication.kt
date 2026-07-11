package ir.danialchoopan.medimate.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import ir.danialchoopan.medimate.data.local.DrugInteractionSeeder
import ir.danialchoopan.medimate.data.local.dao.DrugInteractionDao
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

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var drugInteractionDao: DrugInteractionDao

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
        scheduleLowInventoryCheck()
        scheduleRescheduleAlarms()
        seedDrugInteractions()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun scheduleLowInventoryCheck() {
        val workRequest = PeriodicWorkRequestBuilder<LowInventoryWorker>(6, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "low_inventory_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
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
}
