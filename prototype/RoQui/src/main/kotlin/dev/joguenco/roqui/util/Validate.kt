package dev.joguenco.roqui.util

object Validate {
    fun rangeOfDates(startDate: String, endDate: String): Pair<Boolean, String> {
        try {
            DateUtil.toDate(startDate)
            DateUtil.toDate(endDate)
            return Pair(true, "")
        } catch (e: java.text.ParseException) {
            return Pair(false, e.message.toString().replace("\"", "'"))
        }
    }
}
