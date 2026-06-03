package ir.danialchoopan.medimate.data.local.dao

import androidx.room.*
import ir.danialchoopan.medimate.data.local.entities.InventoryEntity
import ir.danialchoopan.medimate.data.local.entities.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Int): MedicineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    // Inventory
    @Query("SELECT * FROM inventory WHERE medicineId = :medicineId")
    suspend fun getInventoryByMedicineId(medicineId: Int): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInventory(inventory: InventoryEntity)
}
