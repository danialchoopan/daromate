package ir.danialchoopan.medimate.domain.repository

import ir.danialchoopan.medimate.domain.model.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getAllInventory(): Flow<List<InventoryItem>>
    suspend fun addInventory(medicineName: String, currentStock: Int, lowStockThreshold: Int)
    suspend fun increaseStock(medicineId: Int)
    suspend fun decreaseStock(medicineId: Int)
    suspend fun deleteInventory(medicineId: Int)
}
