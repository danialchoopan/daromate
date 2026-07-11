package ir.danialchoopan.medimate.data.repository

import ir.danialchoopan.medimate.data.local.dao.InventoryDao
import ir.danialchoopan.medimate.data.local.dao.MedicineDao
import ir.danialchoopan.medimate.data.local.entities.InventoryEntity
import ir.danialchoopan.medimate.data.local.entities.MedicineEntity
import ir.danialchoopan.medimate.domain.model.InventoryItem
import ir.danialchoopan.medimate.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InventoryRepositoryImpl(
    private val inventoryDao: InventoryDao,
    private val medicineDao: MedicineDao
) : InventoryRepository {

    override fun getAllInventory(): Flow<List<InventoryItem>> {
        return medicineDao.getAllMedicines().map { medicines ->
            medicines.mapNotNull { medicine ->
                val inventory = inventoryDao.getByMedicineId(medicine.id)
                if (inventory != null) {
                    InventoryItem(
                        medicineId = medicine.id,
                        medicineName = medicine.name,
                        currentStock = inventory.currentStock,
                        lowStockThreshold = inventory.lowStockThreshold
                    )
                } else null
            }
        }
    }

    override suspend fun addInventory(medicineName: String, currentStock: Int, lowStockThreshold: Int) {
        val medicine = MedicineEntity(
            name = medicineName,
            description = "",
            dosage = "",
            form = "قرص",
            instruction = "",
            reason = "",
            color = 0xFF4CAF50.toInt()
        )
        val medicineId = medicineDao.insertMedicine(medicine).toInt()

        val inventory = InventoryEntity(
            medicineId = medicineId,
            currentStock = currentStock,
            lowStockThreshold = lowStockThreshold
        )
        inventoryDao.upsert(inventory)
    }

    override suspend fun increaseStock(medicineId: Int) {
        val inventory = inventoryDao.getByMedicineId(medicineId) ?: return
        inventoryDao.upsert(inventory.copy(currentStock = inventory.currentStock + 1))
    }

    override suspend fun decreaseStock(medicineId: Int) {
        val inventory = inventoryDao.getByMedicineId(medicineId) ?: return
        if (inventory.currentStock > 0) {
            inventoryDao.upsert(inventory.copy(currentStock = inventory.currentStock - 1))
        }
    }

    override suspend fun deleteInventory(medicineId: Int) {
        inventoryDao.deleteByMedicineId(medicineId)
    }
}
