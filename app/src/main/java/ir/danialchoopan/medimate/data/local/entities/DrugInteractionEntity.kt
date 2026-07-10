package ir.danialchoopan.medimate.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drug_interactions")
data class DrugInteractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val drugA: String,
    val drugB: String,
    val severity: String,
    val description: String
)
