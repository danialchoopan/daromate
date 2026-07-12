package ir.danialchoopan.medimate.domain.model

/**
 * Medicine - Domain model representing a medication
 *
 * This is the core entity that links to:
 * - ReminderEntity: When to take the medicine
 * - InventoryEntity: How much stock is available
 * - MedicationLogEntity: History of taken/missed doses
 * - DrugInteractionEntity: Interactions with other medicines
 *
 * Fields:
 * - name: Display name (e.g., "Ibuprofen")
 * - dosage: Amount per dose (e.g., "200mg")
 * - form: Physical form (Tablet, Capsule, Syrup, Injection)
 * - instruction: When to take (e.g., "After meal")
 * - reason: Why taking this medicine
 * - color: UI accent color for the medicine card
 */
data class Medicine(
    val id: Int = 0,
    val name: String,
    val description: String,
    val dosage: String,
    val form: String,
    val instruction: String,
    val reason: String,
    val imageUri: String? = null,
    val color: Int = 0xFF4CAF50.toInt()
)
