package apps.jizzu.simpletodo.utils

import java.text.SimpleDateFormat

/**
 * Class to represent date in string format.
 */
object Utils {
    fun getDate(date: Long): String {
        val dateFormat = SimpleDateFormat("dd.MM.yy")
        return dateFormat.format(date)
    }

    fun getTime(time: Long): String {
        val timeFormat = SimpleDateFormat("HH:mm")
        return timeFormat.format(time)
    }

    fun getFullDate(date: Long): String {
        val fullDateFormat = SimpleDateFormat("dd.MM.yy  HH:mm")
        return fullDateFormat.format(date)
    }
}
