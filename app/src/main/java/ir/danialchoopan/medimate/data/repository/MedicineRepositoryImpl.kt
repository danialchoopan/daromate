package ir.danialchoopan.medimate.data.repository

import ir.danialchoopan.medimate.data.local.dao.MedicineDao
import ir.danialchoopan.medimate.data.local.entities.InventoryEntity
import ir.danialchoopan.medimate.data.local.entities.MedicineEntity
import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicineRepositoryImpl(private val medicineDao: MedicineDao) : MedicineRepository {
    override fun getAllMedicines(): Flow<List<Medicine>> = medicineDao.getAllMedicines().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun getMedicineById(id: Int): Medicine? = medicineDao.getMedicineById(id)?.toDomain()

    override suspend fun insertMedicine(medicine: Medicine): Long = medicineDao.insertMedicine(medicine.toEntity())

    override suspend fun updateMedicine(medicine: Medicine) = medicineDao.updateMedicine(medicine.toEntity())

    override suspend fun deleteMedicine(medicine: Medicine) = medicineDao.deleteMedicine(medicine.toEntity())

    override suspend fun getInventoryByMedicineId(medicineId: Int): Inventory? =
        medicineDao.getInventoryByMedicineId(medicineId)?.toDomain()

    override suspend fun updateInventory(inventory: Inventory) =
        medicineDao.updateInventory(inventory.toEntity())

    private fun MedicineEntity.toDomain() = Medicine(id, name, description, dosage, form, instruction, reason, imageUri, color)
    private fun Medicine.toEntity() = MedicineEntity(id, name, description, dosage, form, instruction, reason, imageUri, color)
    private fun InventoryEntity.toDomain() = Inventory(medicineId, currentStock, lowStockThreshold)
    private fun Inventory.toEntity() = InventoryEntity(medicineId, currentStock, lowStockThreshold)
}
