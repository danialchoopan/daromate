package ir.danialchoopan.medimate.domain.repository

import ir.danialchoopan.medimate.domain.model.InventoryItem
import kotlinx.coroutines.flow.Flow

/**
 * InventoryRepository - Interface for inventory management operations
 *
 * Handles CRUD for medicine stock levels.
 * Keeps inventory in sync with the medicines table.
 */
interface InventoryRepository {
    /** Get all inventory items with their medicine names */
    fun getAllInventory(): Flow<List<InventoryItem>>

    /** Add inventory for a new medicine (creates MedicineEntity first) */
    suspend fun addInventory(medicineName: String, currentStock: Int, lowStockThreshold: Int)

    /** Increase stock by 1 */
    suspend fun increaseStock(medicineId: Int)

    /** Decrease stock by 1 (minimum 0) */
    suspend fun decreaseStock(medicineId: Int)

    /** Delete inventory record */
    suspend fun deleteInventory(medicineId: Int)
}
