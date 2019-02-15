package apps.jizzu.simpletodo.utils

import java.text.SimpleDateFormat

object DateAndTimeFormatter {
    private val mPreferenceHelper = PreferenceHelper.getInstance()

    fun getTime(time: Long): String {
        val timeFormatKey = mPreferenceHelper.getInt(PreferenceHelper.TIME_FORMAT_KEY)
        lateinit var timeFormat: SimpleDateFormat

        when(timeFormatKey) {
            0 -> timeFormat = SimpleDateFormat("HH:mm")
            1 -> timeFormat = SimpleDateFormat("hh:mm aa")
        }
        return timeFormat.format(time)
    }

    fun getDate(date: Long): String {
        val dateFormatKey = mPreferenceHelper.getInt(PreferenceHelper.DATE_FORMAT_KEY)
        lateinit var dateFormat: SimpleDateFormat

        when(dateFormatKey) {
            0 -> dateFormat = SimpleDateFormat("dd.MM.yy")
            1 -> dateFormat = SimpleDateFormat("yy.MM.dd")
            2 -> dateFormat = SimpleDateFormat("MM.dd.yy")
            3 -> dateFormat = SimpleDateFormat("yy.dd.MM")
        }
        return dateFormat.format(date)
    }

    fun getFullDate(date: Long): String {
        val fullDateFormatKey = mPreferenceHelper.getInt(PreferenceHelper.DATE_FORMAT_KEY)
        lateinit var fullDateFormat: SimpleDateFormat

        when(fullDateFormatKey) {
            0 -> fullDateFormat = SimpleDateFormat("dd.MM.yy")
            1 -> fullDateFormat = SimpleDateFormat("yy.MM.dd")
            2 -> fullDateFormat = SimpleDateFormat("MM.dd.yy")
            3 -> fullDateFormat = SimpleDateFormat("yy.dd.MM")
        }
        return fullDateFormat.format(date)
    }
}
