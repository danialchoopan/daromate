package ir.danialchoopan.medimate.data.local

import androidx.room.TypeConverter
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.LogStatus

class Converters {
    @TypeConverter
    fun fromIntervalType(value: IntervalType): String = value.name

    @TypeConverter
    fun toIntervalType(value: String): IntervalType = IntervalType.valueOf(value)

    @TypeConverter
    fun fromLogStatus(value: LogStatus): String = value.name

    @TypeConverter
    fun toLogStatus(value: String): LogStatus = LogStatus.valueOf(value)
}
