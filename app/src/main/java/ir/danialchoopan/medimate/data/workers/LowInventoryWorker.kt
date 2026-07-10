package ir.danialchoopan.medimate.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.util.NotificationUtils
import kotlinx.coroutines.flow.first

@HiltWorker
class LowInventoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val medicineRepository: MedicineRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val medicines = medicineRepository.getAllMedicines().first()
        for (medicine in medicines) {
            val inventory = medicineRepository.getInventoryByMedicineId(medicine.id)
            if (inventory != null && inventory.currentStock <= inventory.lowStockThreshold) {
                NotificationUtils.showLowStockNotification(
                    applicationContext,
                    medicine.id,
                    medicine.name,
                    inventory.currentStock
                )
            }
        }
        return Result.success()
    }
}
