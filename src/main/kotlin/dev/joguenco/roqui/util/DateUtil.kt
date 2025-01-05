package dev.mestizos.roqui.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.XMLGregorianCalendar

class DateUtil {

    companion object {
        fun toDate(date: String): Date {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateInDateType: Date = simpleDateFormat.parse(date)

            return dateInDateType
        }

        fun accessKeyToDate(accessKey: String): Date {
            val dateAccessKey = accessKey.substring(0, 8)
            val formatter: DateFormat = SimpleDateFormat("ddMMyyyy")
            return formatter.parse(dateAccessKey)
        }

        fun extractDate(date: XMLGregorianCalendar): Date {
            val dateString = date.year.toString() + "-" +
                    date.month.toString() + "-" +
                    date.day.toString() + " " +
                    date.hour.toString() + ":" +
                    date.minute.toString()

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            return simpleDateFormat.parse(dateString)
        }
    }
}