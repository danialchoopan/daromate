package ir.danialchoopan.medimate.util

import saman.zamani.persiandate.PersianDate
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
        val hour = persianDate.hour
        val minute = persianDate.minute
        return "${persianDate.shYear}/${persianDate.shMonth}/${persianDate.shDay} - %02d:%02d".format(hour, minute)
    }

    fun timestampToPersianDay(timestamp: Long): Int {
        return PersianDate(Date(timestamp)).shDay
    }

    fun timestampToPersianMonth(timestamp: Long): Int {
        return PersianDate(Date(timestamp)).shMonth
    }

    fun timestampToPersianYear(timestamp: Long): Int {
        return PersianDate(Date(timestamp)).shYear
    }

    fun getPersianMonthName(month: Int): String {
        return when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> ""
        }
    }

    fun getPersianDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "شنبه"
            2 -> "یکشنبه"
            3 -> "دوشنبه"
            4 -> "سه‌شنبه"
            5 -> "چهارشنبه"
            6 -> "پنجشنبه"
            7 -> "جمعه"
            else -> ""
        }
    }

    fun getDaysInPersianMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 2, 3, 4, 5, 6 -> 31
            7, 8, 9, 10, 11 -> 30
            12 -> if (isPersianLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    fun isPersianLeapYear(year: Int): Boolean {
        val remainder = year % 2820
        val cycle = remainder % 128
        val positions = intArrayOf(1, 5, 9, 13, 17, 22, 26, 30, 34, 38, 43, 47, 51, 55, 59, 63, 67, 72, 76, 80, 84, 88, 92, 96, 101, 105, 109, 113, 117, 121, 125)
        return cycle in positions
    }

    fun getCurrentPersianDate(): Triple<Int, Int, Int> {
        val now = PersianDate()
        return Triple(now.shYear, now.shMonth, now.shDay)
    }
}
