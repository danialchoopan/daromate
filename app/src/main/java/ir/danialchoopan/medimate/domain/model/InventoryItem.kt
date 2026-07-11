package ir.danialchoopan.medimate.domain.model

data class InventoryItem(
    val medicineId: Int,
    val medicineName: String,
    val currentStock: Int,
    val lowStockThreshold: Int
)
