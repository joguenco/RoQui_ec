package dev.joguenco.roqui.common.repository

import java.util.Date

interface CustomReportRepository<T> {

    fun findByDatesAndStatus(startDate: Date, endDate: Date, status: String): MutableList<T>
}
