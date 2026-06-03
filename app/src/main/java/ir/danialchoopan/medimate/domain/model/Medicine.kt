package ir.danialchoopan.medimate.domain.model

data class Medicine(
    val id: Int = 0,
    val name: String,
    val description: String,
    val dosage: String, // e.g., "50mg"
    val form: String, // e.g., "Tablet", "Syrup"
    val instruction: String, // e.g., "After meal"
    val reason: String,
    val imageUri: String? = null,
    val color: Int = 0xFF4CAF50.toInt()
)
