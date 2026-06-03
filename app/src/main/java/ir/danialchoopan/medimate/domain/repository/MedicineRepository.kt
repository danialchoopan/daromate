package ir.danialchoopan.medimate.domain.repository

import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.Medicine
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {
    fun getAllMedicines(): Flow<List<Medicine>>
    suspend fun getMedicineById(id: Int): Medicine?
    suspend fun insertMedicine(medicine: Medicine): Long
    suspend fun updateMedicine(medicine: Medicine)
    suspend fun deleteMedicine(medicine: Medicine)

    // Inventory related
    suspend fun getInventoryByMedicineId(medicineId: Int): Inventory?
    suspend fun updateInventory(inventory: Inventory)
}
