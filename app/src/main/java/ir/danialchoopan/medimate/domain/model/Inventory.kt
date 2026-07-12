package ir.danialchoopan.medimate.domain.model

/**
 * Inventory - Tracks medicine stock levels
 *
 * When stock falls below lowStockThreshold:
 * - LowInventoryWorker triggers a notification
 * - Dashboard shows low stock warning
 * - Inventory screen highlights in red
 */
data class Inventory(
    val medicineId: Int,
    val currentStock: Int,
    val lowStockThreshold: Int
)
