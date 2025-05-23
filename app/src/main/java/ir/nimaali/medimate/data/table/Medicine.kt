package ir.nimaali.medimate.data.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val startDate: Long, // timestamp
    val imageUri: String? = null,
    val color: Int = 0xFF4CAF50.toInt() // رنگ سبز پیش‌فرض
)