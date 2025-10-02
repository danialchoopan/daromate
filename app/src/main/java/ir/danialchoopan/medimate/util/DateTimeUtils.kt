package ir.nimaali.medimate.util

import saman.zamani.persiandate.PersianDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date



object DateTimeUtils {
    fun timestampToPersianDate(timestamp: Long): String {
        val date = Date(timestamp)

        val persianDate = PersianDate(date)
        val dayOfWeek = persianDate.dayName()
        val day = persianDate.shDay
        val month = persianDate.monthName()
        val hour = persianDate.hour
        val minute = persianDate.minute

        return "$dayOfWeek $day $month | %02d:%02d".format(hour, minute)
    }

    fun timestampToPersianDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        val persianDate = PersianDate(date)
        val timeIn24Hour = getCurrentTime24HourFormat()

        return "${persianDate.toString()} - ${timeIn24Hour}"
    }

    fun getCurrentTime24HourFormat(): String {
        val currentDateTime = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val formattedTime = currentDateTime.format(formatter)

        return formattedTime
    }

}
