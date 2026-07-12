package ir.danialchoopan.medimate.domain.repository

import ir.danialchoopan.medimate.domain.model.Medicine
import kotlinx.coroutines.flow.Flow

/**
 * MedicineRepository - Interface for medicine data operations
 *
 * Provides CRUD operations for medicines and inventory.
 * Implementation: MedicineRepositoryImpl (data layer)
 */
interface MedicineRepository {
    /** Get all medicines as a reactive Flow */
    fun getAllMedicines(): Flow<List<Medicine>>

    /** Get a single medicine by ID */
    suspend fun getMedicineById(id: Int): Medicine?

    /** Insert a new medicine, returns the auto-generated ID */
    suspend fun insertMedicine(medicine: Medicine): Long

    /** Update an existing medicine */
    suspend fun updateMedicine(medicine: Medicine)

    /** Delete a medicine (cascades to reminders, logs, inventory via foreign keys) */
    suspend fun deleteMedicine(medicine: Medicine)

    // Inventory operations (delegated to InventoryDao internally)
    suspend fun getInventoryByMedicineId(medicineId: Int): ir.danialchoopan.medimate.domain.model.Inventory?
    suspend fun updateInventory(inventory: ir.danialchoopan.medimate.domain.model.Inventory)
}
