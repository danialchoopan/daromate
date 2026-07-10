package ir.danialchoopan.medimate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.danialchoopan.medimate.data.local.entities.DrugInteractionEntity

@Dao
interface DrugInteractionDao {
    @Query("""
        SELECT * FROM drug_interactions
        WHERE (LOWER(drugA) = LOWER(:drugName) OR LOWER(drugB) = LOWER(:drugName))
    """)
    suspend fun getInteractionsForDrug(drugName: String): List<DrugInteractionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(interactions: List<DrugInteractionEntity>)

    @Query("SELECT COUNT(*) FROM drug_interactions")
    suspend fun count(): Int
}
