package ir.nimaali.medimate.util

import saman.zamani.persiandate.PersianDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date



object DateTimeUtils {
    fun timestampToPersianDate(timestamp: Long): String {
        val date = Date(timestamp)
        val persianDate = PersianDate(date)
        return persianDate.toString()
    }

    fun timestampToPersianDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        val persianDate = PersianDate(date)
        val timeIn24Hour = getCurrentTime24HourFormat()

        return "${persianDate.toString()} - ${timeIn24Hour}"
    }

    fun getCurrentTime24HourFormat(): String {
        // Get the current date and time
        val currentDateTime = LocalDateTime.now()

        // Define the desired format pattern (HH for 24-hour format hour)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss") // Example: 13:45:00

        // Format the current date and time using the pattern
        val formattedTime = currentDateTime.format(formatter)

        return formattedTime
    }
}
