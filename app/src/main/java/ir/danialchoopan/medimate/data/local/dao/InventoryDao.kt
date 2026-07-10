package ir.danialchoopan.medimate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.danialchoopan.medimate.data.local.entities.InventoryEntity

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory WHERE medicineId = :medicineId")
    suspend fun getByMedicineId(medicineId: Int): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(inventory: InventoryEntity)

    @Query("DELETE FROM inventory WHERE medicineId = :medicineId")
    suspend fun deleteByMedicineId(medicineId: Int)
}
