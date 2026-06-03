package ir.danialchoopan.medimate.domain.model

data class Inventory(
    val medicineId: Int,
    val currentStock: Int,
    val lowStockThreshold: Int
)
