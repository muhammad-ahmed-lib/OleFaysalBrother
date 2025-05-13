package ae.oleapp.employee.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeHelper {

    const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"

    fun getCurrentDate(format: String): String {
        return try {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.format(Date())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun formatMillisecondsToDateString(milliseconds: Long, requiredFormat: String): String {
        val sdf = SimpleDateFormat(requiredFormat, Locale.ENGLISH)
        val date = Date(milliseconds)
        return sdf.format(date)
    }

}